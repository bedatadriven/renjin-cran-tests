package org.renjin.cran;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
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
  
  public File getLogFile() {
    return new File(baseDir, "build.log");
  }
  
  @Override
  public String toString() {
    return getName();
  }

  public void writePom() throws IOException {
    PomBuilder pom = new PomBuilder(baseDir);
    pom.writePom();
  }

  public Map<String, Integer> countLoc() throws IOException {
    System.out.println("Counting lines of code in " + this);
    Map<String, Integer> map = Maps.newHashMap();
    countLoc(map, "src");
    countLoc(map, "R");
    return map;
  }

  private void countLoc(Map<String, Integer> map, String subDir) throws IOException {
    File srcDir = new File(baseDir, subDir);
    if(srcDir.exists() && srcDir.listFiles() != null) {
      for(File srcFile : srcDir.listFiles()) {
        String lang = languageFromFile(srcFile);
        if(lang != null) {
          int loc = countLoc(srcFile);
          if(map.containsKey(lang)) {
            map.put(lang, map.get(lang) + loc);
          } else {
            map.put(lang, loc);
          }
        }
      }
    }

  }

  private int countLoc(File srcFile) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(srcFile));
    int lines = 0;
    while (reader.readLine() != null) lines++;
    reader.close();
    return lines;
  }

  private String languageFromFile(File srcFile) {
    String name = srcFile.getName().toLowerCase();
    if(name.endsWith(".cpp")) {
      return "C++";
    } else if(name.endsWith(".r") || name.endsWith(".s")) {
      return "R";
    } else if(name.endsWith(".c")) {
      return "C";
    } else if(name.endsWith(".f") || name.endsWith(".f77")) {
      return "Fortran";
    } else {
      return null;
    }
  }

}
