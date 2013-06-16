package org.renjin.cran;

public class ProcessMonitor extends Thread {

  private Process process;
  private boolean finished;
  private int exitCode;
  
  public ProcessMonitor(Process process) {
    super();
    this.process = process;
  }

  @Override
  public void run() {
    finished = false;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      System.out.println("ProcessMonitor received interrupt");
    }
    finished = true;
  }

  public boolean isFinished() {
    return finished;
  }

  public int getExitCode() {
    return exitCode;
  }
 
}
