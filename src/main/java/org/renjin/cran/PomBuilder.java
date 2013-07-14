package org.renjin.cran;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.renjin.cran.PackageDescription.PackageDependency;
import org.renjin.cran.PackageDescription.Person;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Constructs a Maven Projct Object Model (POM) from a GNU-R style
 * package folder and DESCRIPTION file.
 *
 */
public class PomBuilder {
  private static final String RENJIN_VERSION = "0.7.0-RC5";

  private File baseDir;

  private boolean successful = true;
  private final PackageDescription description;

  public PomBuilder(File baseDir) throws IOException {
    this.baseDir = baseDir;
    description = readDescription();
  }

  private Model buildPom() throws IOException {
    Model model = new Model();
    model.setModelVersion("4.0.0");
    model.setArtifactId(description.getPackage());
    model.setGroupId("org.renjin.cran");
    model.setVersion(description.getVersion() + "-SNAPSHOT");
    model.setDescription(description.getDescription());
    model.setUrl(description.getUrl());
    
//    Parent parent = new Parent();
//    parent.setGroupId("org.renjin.cran");
//    parent.setArtifactId("cran-parent");
//    parent.setVersion("0.7.0-SNAPSHOT");
//    model.setParent(parent);
    
    if(!Strings.isNullOrEmpty(description.getLicense())) {
      License license = new License();
      license.setName(description.getLicense());
      model.addLicense(license);
    }
    
    for(Person author : description.getAuthors()) {
      Developer developer = new Developer();
      developer.setName(author.getName());
      developer.setEmail(author.getEmail());
      model.addDeveloper(developer);
    }
    
    addCoreModule(model, "graphics");
    addCoreModule(model, "methods");
    
    for(PackageDependency packageDep : description.getDepends()) {
      if(!packageDep.getName().equals("R")) {
        model.addDependency(toMavenDependency(packageDep.getName()));
      }
    }

    Plugin renjinPlugin = new Plugin();
    renjinPlugin.setGroupId("org.renjin");
    renjinPlugin.setArtifactId("renjin-maven-plugin");
    renjinPlugin.setVersion(RENJIN_VERSION);

    PluginExecution compileExecution = compileExecution();
    renjinPlugin.addExecution(compileExecution);
    renjinPlugin.addExecution(legacyCompileExecution());
    renjinPlugin.addExecution(testExecution());
    
    Build build = new Build();
    build.addPlugin(renjinPlugin);

    DeploymentRepository snapshotDeploymentRepository = new DeploymentRepository();
    snapshotDeploymentRepository.setId("renjin-cran-repo");
    snapshotDeploymentRepository.setUrl("http://nexus.bedatadriven.com/content/repositories/renjin-cran-0.7.0/");
    snapshotDeploymentRepository.setName("Renjin CRAN Builds");

    DistributionManagement distributionManagement = new DistributionManagement();
    distributionManagement.setSnapshotRepository(snapshotDeploymentRepository);

    
    Repository repository = new Repository();
    repository.setId("bedatadriven-public");
    repository.setUrl("http://nexus.bedatadriven.com/content/groups/public/");

    model.setDistributionManagement(distributionManagement);
    model.setBuild(build);
    model.setRepositories(Lists.newArrayList(repository));
    model.setPluginRepositories(Lists.newArrayList(repository));
    




    return model;
  }

  private PluginExecution compileExecution() {
    PluginExecution compileExecution = new PluginExecution();
    compileExecution.setId("renjin-compile");
    compileExecution.addGoal("namespace-compile");

    Xpp3Dom sourceDirectory = new Xpp3Dom("sourceDirectory");
    sourceDirectory.setValue("${basedir}/R");

    Xpp3Dom dataDirectory = new Xpp3Dom("dataDirectory");
    dataDirectory.setValue("${basedir}/data");

    Xpp3Dom configuration = new Xpp3Dom("configuration");
    configuration.addChild(sourceDirectory);
    configuration.addChild(dataDirectory);
    compileExecution.setConfiguration(configuration);

    return compileExecution;
  }

  private PluginExecution legacyCompileExecution() {
    PluginExecution compileExecution = new PluginExecution();
    compileExecution.setId("gnur-compile");
    compileExecution.addGoal("gnur-sources-compile");

    Xpp3Dom sourceDirectory = new Xpp3Dom("sourceDirectory");
    sourceDirectory.setValue("${basedir}/src");

    Xpp3Dom sourceDirectories = new Xpp3Dom("sourceDirectories");
    sourceDirectories.addChild(sourceDirectory);

    Xpp3Dom configuration = new Xpp3Dom("configuration");
    configuration.addChild(sourceDirectories);

    compileExecution.setConfiguration(configuration);

    return compileExecution;
  }

  private PluginExecution testExecution() {
    PluginExecution testExecution = new PluginExecution();
    testExecution.setId("renjin-test");
    testExecution.addGoal("test");

    Xpp3Dom testSourceDirectory = new Xpp3Dom("testSourceDirectory");
    testSourceDirectory.setValue("${basedir}/tests");

    Xpp3Dom defaultPackages = new Xpp3Dom("defaultPackages");
    for(String defaultPackage : new String[] {
        "methods" , "stats", "utils", "grDevices", "graphics", "datasets" }) {
      Xpp3Dom pkg = new Xpp3Dom("package");
      pkg.setValue(defaultPackage);
      defaultPackages.addChild(pkg);
    }

    Xpp3Dom configuration = new Xpp3Dom("configuration");
    configuration.addChild(testSourceDirectory);
    configuration.addChild(defaultPackages);


    testExecution.setConfiguration(configuration);

    return testExecution;
  }

  private Dependency toMavenDependency(String pkgName)
      throws IOException {
    Dependency mavenDep = new Dependency();
    mavenDep.setArtifactId(pkgName);
    if(CorePackages.isCorePackage(pkgName)) {
      mavenDep.setGroupId("org.renjin");
      mavenDep.setVersion(RENJIN_VERSION);
    } else {
      mavenDep.setGroupId("org.renjin.cran");
      mavenDep.setVersion("[0,)");
    }
    return mavenDep;
  }

  private void addCoreModule(Model model, String name) {
    Dependency mavenDep = new Dependency();
    mavenDep.setGroupId("org.renjin");
    mavenDep.setArtifactId(name);
    mavenDep.setVersion(RENJIN_VERSION);
    model.addDependency(mavenDep);
  }

  private PackageDescription readDescription() throws IOException {
    File descFile = new File(baseDir, "DESCRIPTION");
    FileReader reader = new FileReader(descFile);
    PackageDescription desc = PackageDescription.fromReader(reader);
    reader.close();
    
    return desc;
  }

  public void writePom() throws IOException {
    Model pom = buildPom();
    File pomFile = new File(baseDir, "pom.xml");
    FileWriter fileWriter = new FileWriter(pomFile);
    MavenXpp3Writer writer = new MavenXpp3Writer();
    writer.write(fileWriter, pom);
    fileWriter.close();
  }

  public boolean isSuccessful() {
    return successful;
  }
}
