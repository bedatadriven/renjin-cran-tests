<#include "base.ftl">
<#include "index-row.ftl">

<@scaffolding>

  <div class="row">
  <div class="span12">
  <h1>Blocker Packages</h1>

  <p>These failed/unstable builds are blocking downstream builds</p>

  <table class="table table-condensed">
  	<thead>
  		<th>Package</th>
  		<th>Downstream</th>
  		<th>Languages</th>
  		<th>Problems</th>
  		<th>Description</th>
  	</thead>

  	<#list packages?sort_by("downstreamCount")?reverse as package>
  	    <#if package.downstreamCount != 0>
            <@indexRow package=package/>
        </#if>
  	</#list>
  </table>
  </div>
  </div>

</@scaffolding>