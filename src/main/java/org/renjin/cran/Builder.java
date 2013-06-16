package org.renjin.cran;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.codehaus.jackson.map.ObjectMapper;
import org.renjin.cran.PackageDescription.PackageDependency;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

/**
 * Program that will retrieve package sources from CRAN,
 * build, and report results.
 */
public class Builder {

  private File outputDir;
  private Map<String, PackageNode> nodes = Maps.newHashMap();

  /**
   * List of projects that still need to be built
   */
  private List<PackageNode> toBuild;
 
  /**
   * Set of packages scheduled to build
   */
  private Set<PackageNode> scheduled = Sets.newHashSet();

  /**
   * Set of all projects that have been successfully built.
   */
  private Set<PackageNode> built = Sets.newHashSet();

  private int maxNumberToBuild = Integer.MAX_VALUE;

  private Map<String, Integer> retryCount = Maps.newHashMap();
  private ExecutorCompletionService<BuildResult> service;

  public static void main(String[] args) throws Exception {

    Builder builder = new Builder();
    builder.outputDir = new File(System.getProperty("cran.dir"));
    builder.outputDir.mkdirs();

    if(args.length > 1 && args[1].equals("unpack")) {
      builder.unpack();
    }
    builder.scanForProjects();
    builder.buildPackages();

  }
  
  public Builder() {
    String maxBuilds = System.getProperty("package.limit");
    if(Strings.isNullOrEmpty(maxBuilds)) {
      maxNumberToBuild = Integer.MAX_VALUE; 
    } else {
      maxNumberToBuild = Integer.parseInt(maxBuilds);
      System.out.println(" ");
    }
    
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

  /**
   * Build the list of package nodes from the package
   * directories present.
   */
  private void scanForProjects() throws IOException {

    System.out.println("Scanning for packages...");

    for(File dir : outputDir.listFiles()) {
      if(dir.isDirectory() && !dir.getName().equals("00buildlogs")) {
        try {
          PackageNode node = new PackageNode(dir);
          nodes.put(node.getName(), node);
        } catch(Exception e) {
          System.err.println("Error building POM for " + dir.getName());
          e.printStackTrace(System.err);
        }
      }
    }
  }

  private void buildPackages() throws Exception {

    if(System.getProperty("max.builds")!=null) {
      maxNumberToBuild = 5;
    }
    
    System.out.println("Starting build...");

    List<BuildResult> results = Lists.newArrayList();
    
    toBuild = Lists.newArrayList(nodes.values());
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(getThreadPoolSize());
    service = new ExecutorCompletionService<BuildResult>(executor);

    System.out.println("Thread pool created with " + getThreadPoolSize() + " threads");

    int scheduledCount = 0;
    
    while(true) {

      // schedule any packages whose dependencies have been met
      ListIterator<PackageNode> it = toBuild.listIterator();
      while(it.hasNext()) {
        PackageNode pkg = it.next();
        if(dependenciesAreResolved(pkg) && scheduledCount < maxNumberToBuild) {
          scheduleForBuild(pkg);

          scheduledCount ++;
          it.remove();
        }
      }
      
      // Is our queue empty? In that case any remaining items
      // to build have unresolvable dependencies
      if(scheduled.isEmpty()) {
        break;
      }
      
      // wait for the next package to complete
      BuildResult result = service.take().get();
      PackageNode completed = nodes.get(result.getPackageName());
      scheduled.remove(completed);

      results.add(result);
      
      System.out.println(result.getPackageName() + ": " + result.getOutcome());
      
      // if it's succeeded, add to list of packages that
      // are now available as dependencies
      if(result.getOutcome() == BuildOutcome.SUCCESS) {
        built.add(completed);

      } else if(result.getOutcome() == BuildOutcome.ERROR ||
                result.getOutcome() == BuildOutcome.TIMEOUT) {
        // otherwise reschedule a few times
        // it's possible to encounter OutOfMemory Errors
        Integer retries = retryCount.get(result.getPackageName());
        if(retries == null) {
          retries = 0;
        }
        if(retries < 3) {
          // reschedule
          scheduleForBuild(nodes.get(result.getPackageName()));
          retryCount.put(result.getPackageName(), retries+1);
        }
      }
      
      // report status periodically
      if(results.size() % 50 == 0) {
        System.out.println(results.size() + "/" + nodes.size() + " builds completed; " + built.size() + " successful.");
      }
      
    }
    
    // close down the thread pool so that the process can exit
    executor.shutdown();
    
    System.out.println("Build complete; " + toBuild.size() + " package(s) with unmet dependencies");
    
    for(PackageNode node : toBuild) {
      results.add(new BuildResult(node.getName(), BuildOutcome.NOT_BUILT));
    }
    
    writeResults(new BuildResults(results));    
  }

  private void scheduleForBuild(PackageNode pkg) {
    System.out.println("Scheduling " + pkg + "...");

    // for the first few packages, force checking for snapshots so we get
    // the latest versions, after that we should have the latest copies
    // in the local repository
    boolean updateSnapshots = (built.size() < 10);

    this.service.submit(new PackageBuilder(pkg, updateSnapshots));
    scheduled.add(pkg);
  }

  /**
   * Write the results of the build to a JSON file so we can subsequently
   * generate build reports
   * @param results
   * @throws Exception
   */
  private void writeResults(BuildResults results) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(new File(outputDir, "build.json"), results);
  }

  private int getThreadPoolSize() {
    String property = System.getProperty("cran.thread.pool.size");
    if(!Strings.isNullOrEmpty(property)) {
      return Integer.parseInt(property);
    } else {
     return Runtime.getRuntime().availableProcessors();
    }
  }

  private boolean dependenciesAreResolved(PackageNode pkg) {
    for(PackageDependency node : pkg.getDescription().getDepends()) {
      if(!node.getName().equals("R") && !CorePackages.isCorePackage(node.getName())) {
        PackageNode depNode = nodes.get(node.getName());
        if(depNode == null) {
          return false;
        }
        if(!built.contains(depNode)) {
          return false;
        }
      }
    }
    return true;
  }
}
