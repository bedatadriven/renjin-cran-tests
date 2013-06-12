package org.renjin.cran;

import java.io.File;
import java.util.concurrent.Callable;

public class PackageBuilder implements Callable<BuildResult> {
  
  private PackageNode pkg;
  private File logFile;

  public static final long TIMEOUT_SECONDS = 20;
  
  public PackageBuilder(PackageNode pkg, File logFile) {
    this.pkg = pkg;
    this.logFile = logFile;
  }
  

  @Override
  public BuildResult call() throws Exception {
   
    Thread.currentThread().setName(pkg.getName() + " [main]");
    
    BuildResult result = new BuildResult(pkg);
    
    pkg.writePom();
    
    ProcessBuilder builder = new ProcessBuilder(getMavenPath(), 
        "-Dmaven.ignore.test.failures=true",
        "-DenvClassifier=linux-x86_64",
        "clean", "install");
    
    builder.directory(pkg.getBaseDir());
    builder.redirectErrorStream(true);
    try {
      long startTime = System.currentTimeMillis();
      Process process = builder.start();
      
      OutputCollector collector = new OutputCollector(process.getInputStream(), logFile);
      collector.setName(pkg + " [output collector]");
      collector.start();
      
      ProcessMonitor monitor = new ProcessMonitor(process);
      monitor.setName(pkg + " [monitor]");
      monitor.start();
      
      while(!monitor.isFinished()) {
       
        if(System.currentTimeMillis() > (startTime + TIMEOUT_SECONDS * 1000)) {
          System.out.println(pkg + " build timed out after " + TIMEOUT_SECONDS + " seconds.");
          process.destroy();
          result.setTimedOut(true);
          break;
        }
        Thread.sleep(1000);
      }
           
      collector.join();
      if(!result.isTimedOut()) {
        result.setSucceeded(monitor.getExitCode() == 0);
      }
    } catch (Exception e) {
      result.setSucceeded(false);
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
