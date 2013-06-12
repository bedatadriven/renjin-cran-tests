package org.renjin.cran;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.renjin.cran.PackageDescription.PackageDependency;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class PackageGraph {
  
  private Map<String, PackageNode> nodes = Maps.newHashMap();
  
  public PackageGraph(Map<String, PackageNode> nodes) {
    this.nodes.putAll(nodes); 
  }

  private Multimap<PackageNode, PackageNode> buildEdges() {
    Multimap<PackageNode, PackageNode> dependencies = HashMultimap.create();
    for(PackageNode node : this.nodes.values()) {
      for(PackageDependency dependency : node.getDescription().getDepends()) {
        PackageNode dependencyNode = nodes.get(dependency.getName());
        if(dependencyNode != null) {
          dependencies.put(node, dependencyNode);
        }
      }
    }
    return dependencies;
  }
  
  public List<PackageNode> sortTopologically() {
    //  L ← Empty list where we put the sorted elements
    //  Q ← Set of all nodes with no incoming edges
    //  while Q is non-empty do
    //      remove a node n from Q
    //      insert n into L
    //      for each node m with an edge e from n to m do
    //          remove edge e from the graph
    //          if m has no other incoming edges then
    //              insert m into Q
    //  if graph has edges then
    //      output error message (graph has a cycle)
    //  else 
    //      output message (proposed topologically sorted order: L)
    
    // outgoing = dependends on
    // incoming = is a dependency of
    
    Multimap<PackageNode, PackageNode> dependsOn = buildEdges();
    Multimap<PackageNode, PackageNode> dependencyOf = HashMultimap.create();
    Multimaps.invertFrom(dependsOn, dependencyOf);
    
    List<PackageNode> buildOrder = Lists.newArrayList();
    
    Set<PackageNode> Q = Sets.newHashSet();
    for(PackageNode node : nodes.values()) {
      if(dependsOn.get(node).isEmpty()) {
        Q.add(node);
      }
    }
    
    while(!Q.isEmpty()) {
      
      // take the next package which has no remaining
      // dependencies, and build it.
      PackageNode n = Q.iterator().next();
      Q.remove(n);
      buildOrder.add(n);
      
      System.out.println(n);
      
      // for each of this package's dependencies, remove the
      // dependency, and it to Q if it has no remaining dependencies.
      List<PackageNode> adjacent = Lists.newArrayList(dependencyOf.get(n));
      for(PackageNode m : adjacent) {
        dependsOn.remove(m, n);
        dependencyOf.remove(n, m);
        if(dependsOn.get(m).isEmpty()) {
          Q.add(m);
        }
      }
    }  
    
    if(!dependsOn.isEmpty() || buildOrder.size() != nodes.size()) {
      System.err.println("Warning: cyclic dependencies detected.");
    }
    
    return buildOrder;
  }
}
