<#macro scaffolding>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Renjin CRAN Builds</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Builds of CRAN packages for use with Renjin">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="/css/bootstrap.css" rel="stylesheet">
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-20543588-4', 'renjin.org');
      ga('send', 'pageview');

    </script>

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="/js/html5shiv.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <div class="nav-collapse collapse">
               <ul class="nav">
                  <li><a href="http://www.renjin.org/">Home</a></li>
                  <li><a href="http://www.renjin.org/#about">About</a></li>
                  <li><a href="http://www.renjin.org/#downloads">Downloads</a></li>
                  <li><a href="http://www.renjin.org/blog/">Blog</a></li>
                  <li><a href="http://packages.renjin.org/index.html">Packages</a></li>
                  <li><a href="http://www.renjin.org/support.html">Support</a></li>
                  <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-target="#" data-toggle="dropdown">Documentation <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                      <li><a href="http://www.renjin.org/documentation/">Overview</a></li>
                      <li class="divider"></li>
                      <li class="nav-header">Guides for...</li>
                      <li><a href="http://www.renjin.org/documentation/analyst-guide.html">Analysts</a></li>
                      <li><a href="http://www.renjin.org/documentation/developer-guide.html">Developers</a></li>
                      <li><a href="http://www.renjin.org/documentation/contributor-guide.html">Contributors</a></li>
                    </ul>
                  </li>
                </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container">

		<#nested>

    </div> <!-- /container -->

  </body>
</html>
</#macro>

