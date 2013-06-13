<#include "base.ftl">
<@scaffolding>

  <h1>${name} ${description.version}</h1>

  <p class="lead">${description.title}</p>


  <#if dependencies?has_content>
  <p>Dependencies:
  <#list dependencies as dependency>
  <a href="${dependency.name}.html" class="label label-${dependency.className}">${dependency.name}</a>
  </#list>
  </p>
  </#if>


  <#if outcome == "not_built">
  <div class="alert alert-error">This package was not built due to an unresolved dependency</div>
  </#if>

  <#if testsFailed>
  <div class="alert alert-warning">There were test failures when building this package</div>
  </#if>

  <#if legacyCompilationFailed>
  <div class="alert alert-warning">Compilation of C/Fortran sources failed, full functionality may not be available</div>
  </#if>

  <#if outcome == "error" || outcome == "timeout">
  <div class="alert alert-error">There was an error building this package</div>
  </#if>

  <p>${description.description}</p>

  <table>
  	<tr>
  		<td>Author</td>
  		<td>${description.author!"No Author"}
 	</tr> 	
  	<tr>
  		<td>Maintainer</td>
  		<td>${description.maintainer!"No Maintainer"}</td>
  	</tr>
  	<tr>
  		<td>License</td>
  		<td>${description.license!"Not specified"}</td>
  	</tr>
  </table>
  
  <h2>Languages</h2>
  <table class="table" style="width: auto">
  	<thead>
  		<th>Language</th>
  		<th>Lines of Code</th>
  	</thead>
  	<#list linesOfCode?keys as lang>
  	<tr>
  		<td>${lang}</td>
  		<td align="right">${linesOfCode[lang]}</td>
  	</tr> 
	</#list>
  </table>


  <#if outcome == "success">


  <h2>Dependency Information</h2>

  <h3>Apache Maven</h3>
  <code>
    <div>&lt;dependency&gt;</div>
    <div>&lt;groupId&gt;org.renjin.cran&lt;/groupId&gt;</div>
    <div>&lt;artifactId&gt;${name}&lt;/artifactId&gt;</div>
    <div>&lt;version&gt;${description.version}&lt;/version&gt;</div>
    <div>&lt;/dependency&gt;</div>
  </code>

  </#if>


  
  <#if wasBuilt>

  
  <h2>Build Output</h2>
  
  <iframe src="${name}.log.txt" width="100%" height="250px" style="border: 1px solid black">
  </iframe>
  <p class="pull-right small"><a href="${name}.log.txt">View fullscreen</a></p>
  </#if>
</@scaffolding>