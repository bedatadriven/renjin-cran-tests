
<#macro indexRow package>

<tr class="${package.displayClass}">
  <td><img src="/img/${package.outcome}16.png" width="16" height="16"></td>
  <td class="align-center">
  <#if package.downstreamCount != 0 >
      <span class="badge">${package.downstreamCount}</span>
  </#if></td>
  <td><a href="packages/${package.name}.html">${package.name}</a></td>
  <td><#list package.nativeLanguages as lang>${lang} </#list></td>
  <td><#if package.testsFailed>TF</#if>
      <#if package.legacyCompilationFailed>LCF</#if>
  </td>
  <td>${package.description.title}</td>
</tr>

</#macro>