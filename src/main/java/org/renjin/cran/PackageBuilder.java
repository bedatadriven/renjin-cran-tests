package org.renjin.cran;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class PackageBuilder implements Callable<BuildResult> {
  
  private PackageNode pkg;
  private File logFile;


  /**
   * When this is true, we force maven to check for snapshot
   * updates. This is important during the first few builds to make
   * sure that we have the latest version of renjin.
   *
   * <p>After the first few builds succeed, {@link Builder} sets this
   * flag to false</p>
   */
  public static boolean updateSnapshots = true;

  public static final long TIMEOUT_SECONDS = 20 * 60;

  public PackageBuilder(PackageNode pkg) {
    this.pkg = pkg;
    this.logFile = new File(pkg.getBaseDir(), "build.log");
  }

  @Override
  public BuildResult call() throws Exception {
   
    // set the name of this thread to the package
    // name for debugging
    Thread.currentThread().setName(pkg.getName());
    
    // write the POM to the base dir
    pkg.writePom();
    
    BuildResult result = new BuildResult();
    result.setPackageName(pkg.getName());

    List<String> command = Lists.newArrayList();
    command.add(getMavenPath());
//    if(updateSnapshots) {
//      command.add("-U");
//    }
    command.add("-o");
    // hot fix for tests that exceed memory
    if(pkg.getName().equals("MASS")) {
      command.add("-DskipTests");
    } else {
      command.add("-Dmaven.test.failure.ignore=true");
    }
    command.add("-DenvClassifier=linux-x86_64");
    command.add("-Dignore.gnur.compilation.failure=true");
    command.add("clean");
    command.add("install");
//    command.add("deploy");

    ProcessBuilder builder = new ProcessBuilder(command);
    
    builder.directory(pkg.getBaseDir());
    builder.redirectErrorStream(true);
    try {
      long startTime = System.currentTimeMillis();
      Process process = builder.start();

      InputStream processOutput = process.getInputStream();
      OutputCollector collector = new OutputCollector(processOutput, logFile);
      collector.setName(pkg + " - output collector");
      collector.start();

      try {
        ProcessMonitor monitor = new ProcessMonitor(process);
        monitor.setName(pkg + " - monitor");
        monitor.start();

        while(!monitor.isFinished()) {

          if(System.currentTimeMillis() > (startTime + TIMEOUT_SECONDS * 1000)) {
            System.out.println(pkg + " build timed out after " + TIMEOUT_SECONDS + " seconds.");
            process.destroy();
            result.setOutcome(BuildOutcome.TIMEOUT);
            break;
          }
          Thread.sleep(1000);
        }

        collector.join();
        if(result.getOutcome() != BuildOutcome.TIMEOUT) {
          if(monitor.getExitCode() == 0) {
            result.setOutcome(BuildOutcome.SUCCESS);
          } else if(monitor.getExitCode() == 1) {
            result.setOutcome(BuildOutcome.FAILED);
          } else {
            System.out.println(pkg.getName() + " exited with code " + monitor.getExitCode());
            result.setOutcome(BuildOutcome.ERROR);
          }
        }
      } finally {
        Closeables.closeQuietly(processOutput);
      }
      
    } catch (Exception e) {
      result.setOutcome(BuildOutcome.ERROR);
      e.printStackTrace();
    }
    return result; 
  }
  
  private String getMavenPath() {
    if(System.getProperty("os.name").toLowerCase().contains("windows")) {
      return "mvn.bat";
    } else {
      return "mvn";
    }
  }
}
