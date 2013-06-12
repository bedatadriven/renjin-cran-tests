<#include "base.ftl">
<@scaffolding>

  <h1>Packages</h1>

  <table>
  	<tr>
  		<td>&nbsp;</td>
  		<td>Package name</td>
  		<td>Description</td>
  		<td>Tests</td>
  	</tr>
  	
  	<#list packages as package>
  		<tr>
  			<td>
  			<#if package.succeeded>
  			<i class="icon-ok"></i>
  			<#else>
  			<i class="icon-remove"></i>
  			</#if>
  			</td>
  			<td>
  			<a href="${package.name}/index.html">${package.name}</a></td>
  			<td class="pkg-description">${package.shortDescription}</td>
  			<td>21/20</td>
  		</tr>
  	</#list>
  </table>

</@scaffolding>