<#include "base.ftl">
<@scaffolding>
 
  <div class="row">
  <div class="span12">
 	
  <h1>Build Statistics</h1>
 
  <h2>Packages</h2>
  <table class="table">
  	<thead>
  		<th>&nbsp;</th>
  		<th>Count</th>
  	</thead>
  	<tr>
  		<td>Packages</td>
  		<td>${stats.totalPackages}</td>
  	</tr>
  	<tr>
  		<td>Packages built</td>
  		<td>${stats.totalPackagesBuilt}</td>
  	</tr>
  </table>

  <h2>Tests</h2>
  
  <table class="table">
  	<thead>
  		<th>&nbsp;</th>
  		<th>Count</th>
  	</thead>
  	<tr>
  		<td>Total Tests</td>
  		<td>${stats.totalTests}</td>
  	</tr>
  	<tr>
  		<td>Tests Passing</td>
  		<td>${stats.totalTestsPassed}</td>
  	</tr>
  </table>  
  </div>
  </div>

</@scaffolding>