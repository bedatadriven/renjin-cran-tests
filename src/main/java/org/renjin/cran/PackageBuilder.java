package org.renjin.cran;

import java.io.File;

import org.renjin.cran.Reporter.PackageReporter;

public class PackageBuilder implements Runnable {
  
  private File baseDir;
  private boolean succeeded = false;
  private PackageReporter reporter;

  public PackageBuilder(File baseDir, PackageReporter reporter) {
    this.baseDir = baseDir;
    this.reporter = reporter;
  }

  @Override
  public void run() {
    
    ProcessBuilder builder = new ProcessBuilder(getMavenPath(), "-X", 
        "-Dmaven.ignore.test.failures=true",
        "-DenvClassifier=linux-x86_64",
        "clean", "install");
    
    builder.directory(baseDir);
    builder.redirectErrorStream(true);
    try {
      Process process = builder.start();
      
      OutputCollector collector = new OutputCollector(process.getInputStream(),
          reporter.getBuildOutputFile());
      collector.start();
      
      int exitCode = process.waitFor();
      collector.join();
      reporter.reportOutcome(exitCode);
      succeeded = (exitCode == 0);
    } catch (Exception e) {
      succeeded = false;
      e.printStackTrace();
    } 
  }

  private String getMavenPath() {
    return "mvn.bat";
    
  }

  public boolean succeeded() {
    return succeeded;
  }

}
