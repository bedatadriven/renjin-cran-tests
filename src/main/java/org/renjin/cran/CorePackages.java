package org.renjin.cran;

import java.util.Set;

import com.google.common.collect.Sets;

public class CorePackages {
  private static Set<String> corePackages = Sets.newHashSet("stats", "stats4", "graphics", "grDevices", "utils", "methods", "datasets", "splines");

  public static boolean isCorePackage(String name) {
    return corePackages.contains(name);
  }
  
}
