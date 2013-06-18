package org.renjin.cran;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * List of errors encountered during tests
 */
public class ErrorList implements Iterable<ErrorList.TestError> {

  public class TestError {
    private BuildReport.PackageReport pkg;
    private TestResult test;
    private String line;

    public TestError(BuildReport.PackageReport pkg, TestResult test, String errorLine) {
      this.pkg = pkg;
      this.test = test;
      this.line = errorLine;
    }

    public BuildReport.PackageReport getPkg() {
      return pkg;
    }

    public TestResult getTest() {
      return test;
    }

    public String getLine() {
      return line;
    }
  }

  private List<TestError> list = Lists.newArrayList();
  
  public ErrorList(Collection<BuildReport.PackageReport> reports) throws IOException {
    for(BuildReport.PackageReport report : reports) {
      for(TestResult test : report.getTestResults()) {
        if(!test.isPassed()) {
          String errorLine = test.parseError();
          if(errorLine != null) {
            TestError error = new TestError(report, test, errorLine);
          }
        }
      }
    }
  }

  @Override
  public Iterator<TestError> iterator() {
    return list.iterator();
  }

}
