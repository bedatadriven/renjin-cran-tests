package org.renjin.cran;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.renjin.cran.PackageDescription.PackageDependency;
import org.renjin.cran.Reporter.PackageReporter;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class PackageNode {

  private File baseDir;
  private PackageDescription description;
  	
	public PackageNode(File packageDir) throws IOException {
	  this.baseDir = packageDir;
		this.description = PackageDescription.fromString(Files.toString(
		    new File(baseDir, "DESCRIPTION"), Charsets.UTF_8));
		
	}
	
	public String getName() {
	  return description.getPackage();
	}

  public PackageDescription getDescription() {
    return description;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  public void writePom() throws IOException {
    PomBuilder pom = new PomBuilder(baseDir);
    pom.writePom();
  }

  public boolean build(PackageReporter reporter)  {
    PackageBuilder builder = new PackageBuilder(baseDir, reporter);
    Thread buildThread = new Thread(builder);
    buildThread.start();
    try {
      buildThread.join(5 * 60 * 1000);
      return builder.succeeded();
    } catch (InterruptedException e) {
      return false;
    }
  }
}
