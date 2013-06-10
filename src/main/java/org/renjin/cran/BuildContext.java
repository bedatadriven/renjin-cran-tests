package org.renjin.cran;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class BuildContext {
  private List<CranPackage> packages;
  private Map<String, CranPackage> index = Maps.newHashMap();

  public BuildContext(List<CranPackage> packages) {
    this.packages = packages;
    for(CranPackage pkg : packages) {
      index.put(pkg.getName(), pkg);
    }
  }

  public String getLatestVersion(String pkgName) {
    CranPackage pkg = index.get(pkgName);
    if(pkg == null) {
      throw new RuntimeException("Can't find package " + pkgName);
    }
    return pkg.getVersion();
  }

}
