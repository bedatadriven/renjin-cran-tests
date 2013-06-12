package org.renjin.cran;

public class BuildResult {

  private String packageName;
  private BuildOutcome outcome;
 
  
  public BuildResult() {
    
  }
  
  public BuildResult(String name, BuildOutcome outcome) {
    this.packageName = name;
    this.outcome = outcome;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public BuildOutcome getOutcome() {
    return outcome;
  }

  public void setOutcome(BuildOutcome outcome) {
    this.outcome = outcome;
  }  
}
