package org.renjin.cran;

public class BuildResult {

  private final PackageNode pkg;
  private long buildDuration;
  private boolean timedOut;
  private boolean succeeded;
  
  public BuildResult(PackageNode pkg) {
    this.pkg = pkg;
  }


  public long getBuildDuration() {
    return buildDuration;
  }

  public void setBuildDuration(long buildDuration) {
    this.buildDuration = buildDuration;
  }

  public boolean isTimedOut() {
    return timedOut;
  }

  public void setTimedOut(boolean timedOut) {
    this.timedOut = timedOut;
  }

  public boolean isSucceeded() {
    return succeeded;
  }

  public void setSucceeded(boolean succeeded) {
    this.succeeded = succeeded;
  }
  
  public PackageNode getPackage() {
    return pkg;
  }
  
}
