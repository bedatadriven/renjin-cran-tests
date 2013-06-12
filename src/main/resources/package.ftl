<#include "base.ftl">
<@scaffolding>

  <h1>${name} ${description.version}</h1>

  <p class="lead">${description.description}</p>
  
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
  
  <#if wasBuilt>
  <h2>Build Output</h2>
  
  <iframe src="build.log.txt" width="100%" height="250px" style="border: 1px solid black">
  </iframe>
  <p class="pull-right small"><a href="build.log.txt">View fullscreen</a></p>
  </#if>
</@scaffolding>