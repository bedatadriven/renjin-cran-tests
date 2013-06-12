<#include "base.ftl">
<@scaffolding>

  <h1>${name}</h1>

  <p class="lead">${description}</p>
  
  <h2>Build Output</h2>
  
  <iframe src="build.log" width="100%" height="250px" style="border: 1px solid black">
  </iframe>
  <p class="pull-right small"><a href="build.log">View fullscreen</a></p>

</@scaffolding>