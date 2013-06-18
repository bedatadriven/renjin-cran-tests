
<#include "base.ftl">
<#include "index-row.ftl">
<@scaffolding>

  <div class="row">
  <div class="span12">
  <h1>Packages</h1>

  <table class="table table-condensed">
    <thead>
        <th>Package</th>
        <th>Test</th>
        <th>Error Message</th>
    </thead>

    <#list failedTests as test>
        <tr>
            <td>test.package.name</td>
            <td>test.name</td>
            <td>test.errorMessage</td>
        </tr>
    </#list>
  </table>
  </div>
  </div>

</@scaffolding>