package org.renjin.cran;

import java.util.List;

import com.google.common.collect.Lists;

public class BuildResults {
  private List<BuildResult> results;
  
  public BuildResults() {
    results = Lists.newArrayList();
  }
  
  public BuildResults(List<BuildResult> results) {
    this.results = results;
  }
  
  public List<BuildResult> getResults() {
    return results;
  }
}
