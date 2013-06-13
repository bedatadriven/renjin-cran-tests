<#include "base.ftl">
<@scaffolding>

  <div class="row">
  <div class="span12"> 
  <h1>Packages</h1>

  <table class="table table-condensed">
  	<thead>
  		<th>Package</th>
  		<th>Languages</th>
  		<th>Problems</th>
  		<th>Description</th>
  	</thead>
  	
  	<#list packages?sort_by("name") as package>
  		<tr class="${package.displayClass}">
  			<td><img src="/img/${package.outcome}16.png" width="16" height="16"></td>
  			<td><a href="packages/${package.name}.html">${package.name}</a></td>
  			<td><#list package.nativeLanguages as lang>${lang} </#list></td>
  			<td><#if package.testsFailed>TF</#if>
  			    <#if package.legacyCompilationFailed>LCF</#if>
  			</td>
  			<td>${package.description.title}</td>
  		</tr>
  	</#list>
  </table>
  </div>
  </div>

</@scaffolding>