<#include "base.ftl">
<@scaffolding>

  <h1>${name} ${description.version}</h1>

  <p class="lead">${description.description}</p>
  
  <#if dependencies?has_content>
  <p>Dependencies: 
  <#list dependencies as dependency>
  <a href="${dependency.name}.html" class="label label-${dependency.className}">${dependency.name}</a>
  </#list>
  </p>
  </#if>
  
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
  <table class="table span4">
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
  
  <#if wasBuilt>
  <h2>Build Output</h2>
  
  <iframe src="build.log.txt" width="100%" height="250px" style="border: 1px solid black">
  </iframe>
  <p class="pull-right small"><a href="build.log.txt">View fullscreen</a></p>
  </#if>
</@scaffolding>