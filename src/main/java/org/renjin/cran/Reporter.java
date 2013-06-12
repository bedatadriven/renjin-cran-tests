package org.renjin.cran;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Reporter {

  private File reportDir;
  private List<PackageReporter> packages = Lists.newArrayList();

  public Reporter(File reportDir) {
    this.reportDir = reportDir;
  }
  
  public PackageReporter getPackageReporter(PackageNode node) {
    PackageReporter packageReporter = new PackageReporter(node);
    packages.add(packageReporter);
    return packageReporter;
  }
  
  public List<PackageReporter> getPackages() {
    return packages;
  }
  
  public void writeIndex() throws IOException, TemplateException {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(getClass(), "/");
    cfg.setObjectWrapper(new DefaultObjectWrapper());

    Template template = cfg.getTemplate("index.ftl");  
    
    FileWriter writer = new FileWriter(new File(reportDir, "index.html"));
    template.process(this, writer);
    writer.close();
  
    for(PackageReporter pkg : packages) {
      pkg.writeHtml(cfg);
    }
  }
  
  public class PackageReporter {

    private PackageNode node;
    private File pkgDir;
    private int exitCode;
    
    public PackageReporter(PackageNode node) {
      this.node = node;
      this.pkgDir = new File(reportDir, node.getName());
      this.pkgDir.mkdirs();
    }
    
    public void writeHtml(Configuration cfg) throws IOException, TemplateException {
      
      FileWriter index = new FileWriter(new File(pkgDir, "index.html"));
      
      Template template = cfg.getTemplate("package.ftl");
      template.process(this, index);
      index.close();
     
    }

    public File getBuildOutputFile() {
      return new File(pkgDir, "build.log");
    }

    public void reportOutcome(int exitCode) {
      this.exitCode = exitCode;
    }
    
    public boolean getSucceeded() {
      return exitCode == 0;
    }
    
    public String getName() {
      return node.getName();
    }
    
    public String getDescription() {
      return node.getDescription().getDescription();
    }
    
    public String getShortDescription() {
      String desc = getDescription();

      for(int i=150;i<desc.length();++i) {
        if(desc.charAt(i) == ' ') {
          return desc.substring(0, i);
        }
      }
      return desc;
    }
  }
  
}
