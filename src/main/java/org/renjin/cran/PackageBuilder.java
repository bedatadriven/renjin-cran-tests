package org.renjin.cran;

import java.io.File;
import java.util.concurrent.Callable;

public class PackageBuilder implements Callable<BuildResult> {
  
  private PackageNode pkg;
  private File logFile;

  public static final long TIMEOUT_SECONDS = 5 * 60;
  
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
    
    ProcessBuilder builder = new ProcessBuilder(getMavenPath(), 
        "-Dmaven.test.failure.ignore=true",
        "-DenvClassifier=linux-x86_64",
        "clean", "install");
    
    builder.directory(pkg.getBaseDir());
    builder.redirectErrorStream(true);
    try {
      long startTime = System.currentTimeMillis();
      Process process = builder.start();
      
      OutputCollector collector = new OutputCollector(process.getInputStream(), logFile);
      collector.setName(pkg + " - output collector");
      collector.start();
      
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
        } else { 
          result.setOutcome(BuildOutcome.ERROR);
        }
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
