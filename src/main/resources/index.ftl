<#include "base.ftl">
<@scaffolding>

  <h1>Packages</h1>

  <table>
  	<tr>
  		<td>&nbsp;</td>
  		<td>Package name</td>
  		<td>Notes</td>
  	</tr>
  	
  	<#list packages?sort_by("name") as package>
  		<tr>
  			<td>
  			<#if package.wasBuilt>
	  			<#if package.buildResult.succeeded>
	  			<i class="icon-ok" title="Build succeeded"></i>
	  			<#else>
	  			<i class="icon-remove" title="Build failed"></i>
	  			</#if>
	  		<#else>
	  			<i class="icon-warning" title="Not built because of incomplete dependencies">
	  		</#if>
  			</td>
  			<td>
  			<a href="${package.name}/index.html" title="${package.description.description}">${package.name}</a></td>
  			<td>
  			<#if !package.wasBuilt>
  			Unmet dependencies
  			</td>
  			</#if>
  		</tr>
  	</#list>
  </table>

</@scaffolding>