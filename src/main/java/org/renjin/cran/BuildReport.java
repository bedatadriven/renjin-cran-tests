package org.renjin.cran;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BuildReport {

  private File reportDir;
  private Map<String, PackageReport> packages = Maps.newHashMap();

  public BuildReport(File outputDir, File reportDir) throws Exception {
    
    this.reportDir = reportDir;
    this.reportDir.mkdirs();
    
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
  
  public class PackageReport {

    private PackageNode pkg;
    private File pkgDir;
    private BuildOutcome outcome;
    
    public PackageReport(PackageNode pkg, BuildOutcome outcome) {
      this.pkg = pkg;
      this.pkgDir = pkg.getBaseDir();
      this.outcome = outcome;
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
    
    public BuildOutcome getOutcome() {
      return outcome;
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
  
  public static void main(String[] args) throws Exception {
    BuildReport report = new BuildReport(new File("F:\\cran"), new File("cran-reports"));
    report.writeReports();
  }
}
