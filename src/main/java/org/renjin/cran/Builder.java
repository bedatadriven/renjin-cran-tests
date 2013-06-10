package org.renjin.cran;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class Builder {

  private File outputDir;
  private File packagesFile;
  private List<String> modules = Lists.newArrayList();
  
  public static void main(String[] args) throws IOException {
    Builder builder = new Builder();
    builder.outputDir = new File(args[0]);
    builder.outputDir.mkdirs();

    if(args.length > 1 && args[1].equals("unpack")) {
      builder.unpack();
    }
    builder.updatePoms();
  }
  
  private void unpack() throws IOException {

    // download package index
    File packageIndex = new File(outputDir, "index.html");
    if(!packageIndex.exists()) {
      CRAN.fetchPackageIndex(packageIndex);
    }
    List<CranPackage> cranPackages = CRAN.parsePackageList(
        Files.newInputStreamSupplier(packageIndex));

    for(CranPackage pkg : cranPackages) {
      System.out.println(pkg.getName());
      String pkgName = pkg.getName().trim();
      if(!Strings.isNullOrEmpty(pkgName)) {
        File pkgRoot = new File(outputDir, pkgName);
        CRAN.unpackSources(pkg, pkgRoot);
      }
    }
  }

  private void updatePoms() throws IOException {

    for(File dir : outputDir.listFiles()) {
      if(dir.isDirectory()) {
        try {
          ProjectBuilder builder = new ProjectBuilder(dir);
          builder.writePom();
          modules.add(dir.getName());
        } catch(Exception e) {
          System.err.println("Error building POM for " + dir.getName());
          e.printStackTrace(System.err);
        }
      }
    }

    writeReactorPom();
  }

  private void writeReactorPom() throws IOException {

    Model model = new Model();
    model.setModelVersion("4.0.0");
    model.setArtifactId("cran-parent");
    model.setGroupId("org.renjin.cran");
    model.setVersion("0.7.0-SNAPSHOT");
    model.setPackaging("pom");
    
    for(String module : modules) {
      model.addModule(module);
    }
   
    DeploymentRepository repo = new DeploymentRepository();
    repo.setId("bedatadriven-oss");
    repo.setName("Bedatadriven Open-Source releases");
    repo.setUrl("http://nexus.bedatadriven.com/content/repositories/oss-releases");
    
    DeploymentRepository snapshotRepo = new DeploymentRepository();
    snapshotRepo.setId("bedatadriven-oss");
    snapshotRepo.setName("Bedatadriven Open-Source snapshots");
    snapshotRepo.setUrl("http://nexus.bedatadriven.com/content/repositories/renjin-cran-0.7.0");

    DistributionManagement dist = new DistributionManagement();
    dist.setRepository(repo);
    dist.setSnapshotRepository(snapshotRepo);
    model.setDistributionManagement(dist);


    Repository bddRepo = new Repository();
    bddRepo.setId("bedatadriven-public");
    bddRepo.setUrl("http://nexus.bedatadriven.com/content/groups/public");
    bddRepo.setName("bedatadriven Public Repo");
    model.addRepository(bddRepo);
    
    model.addPluginRepository(bddRepo);
    
    File pomFile = new File(outputDir, "pom.xml");
    FileWriter fileWriter = new FileWriter(pomFile);
    MavenXpp3Writer writer = new MavenXpp3Writer();
    writer.write(fileWriter, model);
    fileWriter.close();
  }


  private Set<String> readPkgList() throws IOException {
    return Sets.newHashSet(Files.readLines(packagesFile, Charsets.UTF_8));
  } 
}
