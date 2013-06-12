package org.renjin.cran;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Reporter {

  private File reportDir;
  private Map<String, PackageReport> packages = Maps.newHashMap();

  public Reporter(File reportDir) {
    this.reportDir = reportDir;
  }
  
  public File getLogDestination(PackageNode node) {
    File pkgDir = getPackageReportDir(node);
    File logFile = new File(pkgDir, "build.log.txt");
    return logFile;
  }

  private File getPackageReportDir(PackageNode node) {
    File pkgDir = new File(reportDir, node.getName());
    return pkgDir;
  }
  
  public Collection<PackageReport> getPackages() {
    return packages.values();
  }
  
  public void recordBuildResult(BuildResult completed) {
    packages.put(completed.getPackage().getName(), new PackageReport(completed));
  }
  

  public void recordUnbuilt(PackageNode node) {
    packages.put(node.getName(), new PackageReport(node));
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
  
  public class PackageReport {

    private BuildResult result;
    private PackageNode pkg;
    private File pkgDir;
    
    public PackageReport(BuildResult result) {
      this.result = result;
      this.pkg = result.getPackage();
      this.pkgDir = getPackageReportDir(pkg);
    }
    
    public PackageReport(PackageNode node) {
      this.result = null;
      this.pkg = node;
      this.pkgDir = getPackageReportDir(pkg);
    }

    public void writeHtml(Configuration cfg) throws IOException, TemplateException {
      pkgDir.mkdirs(); 
      FileWriter index = new FileWriter(new File(pkgDir, "index.html"));
      
      Template template = cfg.getTemplate("package.ftl");
      template.process(this, index);
      index.close();
    }

    public File getBuildOutputFile() {
      return new File(pkgDir, "build.log.txt");
    }
    
    public String getName() {
      return pkg.getName();
    }
    
    public boolean getWasBuilt() {
      return result != null;
    }
    
    public BuildResult getBuildResult() {
      return result;
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
  }
  
}
