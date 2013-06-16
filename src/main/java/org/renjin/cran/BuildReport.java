package org.renjin.cran;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import org.codehaus.jackson.map.ObjectMapper;
import org.renjin.cran.PackageDescription.PackageDependency;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BuildReport {

  private File reportDir;
  private File packageReportsDir;
  private Map<String, PackageReport> packages = Maps.newHashMap();

  public BuildReport(File outputDir, File reportDir) throws Exception {
    
    this.reportDir = reportDir;
    this.packageReportsDir = new File(reportDir, "packages");
    this.packageReportsDir.mkdirs();
    
    ObjectMapper mapper = new ObjectMapper();
    BuildResults results = mapper.readValue(new File(outputDir, "build.json"), BuildResults.class);
    for(BuildResult result : results.getResults()) {
      PackageNode node = new PackageNode(new File(outputDir, result.getPackageName()));
      packages.put(node.getName(), new PackageReport(node, result.getOutcome()));
    }
  }
  
  public Collection<PackageReport> getPackages() {
    return packages.values();
  }
  

  
  public void writeReports() throws IOException, TemplateException {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(getClass(), "/");
    cfg.setObjectWrapper(new DefaultObjectWrapper());

    Template template = cfg.getTemplate("index.ftl");  
    
    FileWriter writer = new FileWriter(new File(reportDir, "index.html"));
    template.process(this, writer);
    writer.close();
  
    for(PackageReport pkg : packages.values()) {
      pkg.writeHtml(cfg);
    }
  }
  
  public class PackageDep {
    private String name;
    private PackageReport report;
    
    public PackageDep(String name, PackageReport report) {
      this.name = name;
      this.report = report;
    }
    
    public String getName() {
      return name;
    }
    
    public String getClassName() {
      if(CorePackages.isCorePackage(name)) {
        return "info";
      } else if(report == null) {
        return "inverse";
      } else {
        switch(report.outcome) {
        case ERROR:
        case TIMEOUT:
          return "important";
        case SUCCESS:
          return "success";
        default:
          return "";
        }
      }
    }
  }
  
  public class PackageReport {

    private PackageNode pkg;
    private BuildOutcome outcome;
    
    private Map<String, Integer> loc;
    
    private boolean legacyCompilationFailed = false;
    private boolean testsFailed = false;
    
    public PackageReport(PackageNode pkg, BuildOutcome outcome) throws IOException {
      this.pkg = pkg;
      this.outcome = outcome;

      parseBuildLog();
    }
    
    private void parseBuildLog() throws IOException {
      File logFile = new File(pkg.getBaseDir(), "build.log");
      if(logFile.exists()) {
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String line;
        while((line=reader.readLine())!=null) {
          if(line.contains("Compilation of legacy sources failed")) {
            legacyCompilationFailed = true;
          } else if(line.contains("There were R test failures")) {
            testsFailed = true;
          }
        }
      }
    }
    
    public List<PackageDep> getDependencies() {
      List<PackageDep> reports = Lists.newArrayList();
      for(PackageDependency dep : pkg.getDescription().getDepends()) {
        if(!dep.getName().equals("R")) {
          PackageReport report = packages.get(dep.getName());
          reports.add(new PackageDep(dep.getName(), report));
        }
      }
      return reports;
    }
    
    public void writeHtml(Configuration cfg) throws IOException, TemplateException {
      System.out.println("Writing report for " + pkg);

      FileWriter index = new FileWriter(new File(packageReportsDir, pkg.getName() + ".html"));
      
      Template template = cfg.getTemplate("package.ftl");
      template.process(this, index);
      index.close();
    }

    public boolean isLegacyCompilationFailed() {
      return legacyCompilationFailed;
    }

    public boolean isTestsFailed() {
      return testsFailed;
    }

    public String getDisplayClass() {
      switch (outcome) {
        case TIMEOUT:
        case ERROR:
          return "error";
        case SUCCESS:
          if(legacyCompilationFailed || testsFailed) {
            return "warning";
          } else {
            return "success";
          }
        case NOT_BUILT:
        default:
          return "";
      }
    }

    public List<TestResult> getTestResults() throws IOException {
      List<TestResult> results = Lists.newArrayList();
      if(getWasBuilt()) {
        File targetDir = new File(pkg.getBaseDir(), "target");
        File testReportDir = new File(targetDir, "renjin-test-reports");
        if(testReportDir.exists() && testReportDir.listFiles() != null) {
          for(File file : testReportDir.listFiles()) {
            if(file.getName().endsWith(".xml")) {
              results.add(new TestResult(file));
            }
          }
        }
      }
      return results;
    }

    public String getBuildOutput() throws IOException {
      if(getWasBuilt() && pkg.getLogFile().exists()) {
        return Files.toString(pkg.getLogFile(), Charsets.UTF_8);
      } else {
        return "\n";
      }
    }
    
    private String getLogFileName() {
      return pkg.getName() + ".log.txt";
    }
    
    public String getName() {
      return pkg.getName();
    }
    
    public String getOutcome() {
      return outcome.name().toLowerCase();
    }
    
    public boolean getWasBuilt() {
      return outcome != BuildOutcome.NOT_BUILT;
    }
    
    public PackageDescription getDescription() {
      return pkg.getDescription();
    }
    
    public String getShortDescription() {
      String desc = getDescription().getDescription();

      for(int i=150;i<desc.length();++i) {
        if(desc.charAt(i) == ' ') {
          return desc.substring(0, i);
        }
      }
      return desc;
    }
    
    public Map<String, Integer> getLinesOfCode() throws IOException {
      if(loc == null) {
        loc = pkg.countLoc();
      }
      return loc;
    }
    
    public Set<String> getNativeLanguages() throws IOException {
      Set<String> langs = Sets.newHashSet(getLinesOfCode().keySet());
      langs.remove("R");
      return langs;
    }
  }
  
  
  public static void main(String[] args) throws Exception {
    BuildReport report = new BuildReport(
        new File(System.getProperty("cran.dir")), 
        new File(System.getProperty("reports.dir")));
    report.writeReports();
  }
}
