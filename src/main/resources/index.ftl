<#include "base.ftl">
<@scaffolding>

  <div class="row">
  <div class="span12"> 
  <h1>Packages</h1>

  <table class="table table-condensed">
  	<tr>
  		<td>&nbsp;</td>
  		<td>Package</td>
  		<td>Languages</td>
  		<td>Description</td>
  	</tr>
  	
  	<#list packages?sort_by("name") as package>
  		<tr>
  			<td><img src="/img/${package.outcome}16.png" width="16" height="16"></td>
  			<td><a href="packages/${package.name}.html">${package.name}</a></td>
  			<td><#list package.nativeLanguages as lang>${lang} </#list></td>
  			<td>${package.shortDescription}</td>
  		</tr>
  	</#list>
  </table>
  </div>
  </div>

</@scaffolding>