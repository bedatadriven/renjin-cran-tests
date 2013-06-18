<#include "base.ftl">

<#macro deplabel d>
<a href="${d.name}.html" class="label label-${d.className}">${d.name}</a>
</#macro>

<@scaffolding>

  <h1>${name} ${description.version}</h1>

  <p class="lead">${description.title}</p>

  <#if dependencies?has_content>
  <p>Dependencies:
  <#list dependencies as package>
    <@deplabel d=package/>
  </#list>
  </p>
  </#if>

  <#if downstream?has_content>
  <p>Reverse depends:
  <#list downstream as package>
    <@deplabel d=package/>
  </#list>
  </p>
  </#if>

  <#if outcome == "not_built">
  <div class="alert alert-error">This package was not built due to an unresolved dependency</div>
  </#if>

  <#if testsFailed>
  <div class="alert alert-warning">There were test failures when building this package</div>
  </#if>

  <#if legacyCompilationFailed>
  <div class="alert alert-warning">Compilation of C/Fortran sources failed, full functionality may not be available</div>
  </#if>

  <#if outcome == "error" || outcome == "timeout">
  <div class="alert alert-error">There was an error building this package</div>
  </#if>

  <p>${description.description}</p>

  <h2>Dependency Information</h2>
  
  <h2>Languages</h2>
  <table class="table" style="width: auto">
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


  <#if outcome == "success">


  <h2>Dependency Information</h2>

  <h3>Apache Maven</h3>
  <code>
    <div>&lt;dependency&gt;</div>
    <div>&lt;groupId&gt;org.renjin.cran&lt;/groupId&gt;</div>
    <div>&lt;artifactId&gt;${name}&lt;/artifactId&gt;</div>
    <div>&lt;version&gt;${description.version}&lt;/version&gt;</div>
    <div>&lt;/dependency&gt;</div>
  </code>

  </#if>

  
  <#if wasBuilt>

  <h2>Test Results</h2>

  <h3>Summary</h3>

  <table class="table" style="width: auto">
  <#list testResults as test>
  <tr class="<#if test.passed>success<#else>error</#if>">
    <td><a href="#test-${test.name}">${test.name}</a></td>
    <td><#if test.passed>OK<#else>ERROR</#if></td>
  </tr>
  </#list>
  </table>

  <#list testResults as test>
  <a name="test-${test.name}"></a>
  <h3>${test.name} [<#if test.passed>OK<#else>ERROR</#if>] </h3>
  <pre>
${test.output?html}
  </pre>
  </#list>


  <h2>Build Output</h2>

  <pre>
 ${buildOutput?html}
  </pre>

  </#if>

</@scaffolding>