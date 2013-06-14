package org.renjin.cran;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;


public class TestResult {
  
  private boolean passed;
  private String output;
  private String name;

  /**
   * 
   * @param xmlFile the junit-style XML file describing the test outcome.
   *               
   */
  public TestResult(File xmlFile) throws IOException {
    if(!xmlFile.getName().startsWith("TEST-") || !xmlFile.getName().endsWith(".xml")) {
      throw new IllegalArgumentException("Expected XML file named TEST-testname.xml");
    }
    
    this.name = xmlFile.getName().substring("TEST-".length(), xmlFile.getName().length()-".xml".length());

    // rough check to see if there was an error.
    // technically a testsuite can have many tests, but all the CRAN
    // tests will have only one test- basically just evaluating the given R file or examples in .Rd file
    
    this.passed = !Files.toString(xmlFile, Charsets.UTF_8).contains("<error");


    // read the the output from the txt file
    File outputFile = new File(xmlFile.getParentFile(), name + "-output.txt");
    this.output = Files.toString(outputFile, Charsets.UTF_8);
  }

  public boolean isPassed() {
    return passed;
  }

  public String getOutput() {
    return output;
  }

  public String getName() {
    return name;
  }
}
