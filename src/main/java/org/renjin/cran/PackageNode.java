package org.renjin.cran;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
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
  
  public File getBaseDir() {
    return baseDir;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  public void writePom() throws IOException {
    PomBuilder pom = new PomBuilder(baseDir);
    pom.writePom();
  }

}
