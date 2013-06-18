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
  		<tr>Packages</tr>
  		<tr>${totalPackages}</tr>
  	</tr>
  	<tr>
  		<tr>Packages built</tr>
  		<tr>${totalPackagesBuilt}</tr>
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
  		<td>${totalTests}</td>
  	</tr>
  	<tr>
  		<td>Tests Passing</td>
  		<td>${totalTestsPassed}</td>
  	</tr>
  </table>  
  </div>
  </div>

</@scaffolding>