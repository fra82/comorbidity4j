<#-- https://codepen.io/wideckop/pen/ZOgOPq -->

<#-- Page header -->
<#macro pageHeader>
<!DOCTYPE html>
<html>
<#setting number_format="computer">
	<head>
	
	  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	  
	  <title>Comorbidity4web</title>
	  <!-- Jquery and JqueryUI -->
	  <script src="${md.baseCSS_JSpath!''}js/jquery-3.2.1.min.js?v=${md.CSSJScount!'---'}"></script>
	  <script src="${md.baseCSS_JSpath!''}js/jquery-ui.min.js?v=${md.CSSJScount!'---'}"></script>
	  <link rel="stylesheet" href="${md.baseCSS_JSpath!''}css/jquery-ui.min.css?v=${md.CSSJScount!'---'}" type="text/css" />
	  <script src="${md.baseCSS_JSpath!''}js/jquery.form-validator.min.js?v=${md.CSSJScount!'---'}"></script> <!-- http://www.formvalidator.net/ -->
  
	  <!-- http://www.formvalidator.net/ -->
  	  <script src="${md.baseCSS_JSpath!''}js/jquery.form-validator.min.js?v=${md.CSSJScount!'---'}"></script>
	  
      <!-- Tabulator.js -->
	  <link href="${md.baseCSS_JSpath!''}css/tabulator.min.css?v=${md.CSSJScount!'---'}" rel="stylesheet">
	  <script type="text/javascript" src="${md.baseCSS_JSpath!''}js/tabulator.min.js?v=${md.CSSJScount!'---'}"></script>
	  
	  <!-- Custom CSS -->
	  <link rel="stylesheet" href="${md.baseCSS_JSpath!''}css/dataInput.css?v=${md.CSSJScount!'---'}" type="text/css" />
	  
	  <!-- Custom JS -->
	  <script type="text/javascript" src="${md.baseCSS_JSpath!''}js/dataInput.js?v=${md.CSSJScount!'---'}"></script>
	  
	  <!-- FAVICO -->
	  <link rel="icon" href="${md.baseCSS_JSpath!''}img/favicon.ico">
	  
	</head>
	<body>
		<div class="globalContainer-c4j">
</#macro>

<#macro patientDataDescription>
	<div class="explainInput">
	  
	</div>
</#macro>


<#-- Side menu -->
<#macro printMenu>
	<!-- Sidebar -->
	<div class="sidebar-c4j">
	  
	
	  <nav class="sidebarNav-c4j">
	     <ul>
	     	<li><div style="text-align: center; vertical-align: middle; line-height: 15px; border: none; font-family: Gill Sans, Verdana; font-size: 14px; letter-spacing: 2px; font-weight: bold;">
	     		<div class="sidebarNavLink-c4j" style="background:#F3FAB6; text-align:center; border: none; margin-top: 15px;">
	     			<img src="img/comorbidity4j-logo.png" width="220px" title="Comorbidity4j" alt="Flower">
	  			</div>
	  			
	  			<a class="sidebarNavLink-c4j" style="background:#F3FAB6; text-align:center; border: none; height: 15px; margin-bottom: 5px;" onMouseOver="this.style.color='green'" onMouseOut="this.style.color='black'" href="home">
	     			HOME
	  			</a>
	  			<a class="sidebarNavLink-c4j" style="background:#F3FAB6; text-align:center; border: none; height: 15px; margin-bottom: 20px;" onMouseOver="this.style.color='green'" onMouseOut="this.style.color='black'" href="https://comorbidity4j.readthedocs.io/" target="_blank">
	     			DOCUMENTATION
	  			</a>
	  			<div>
	  		</li>
	     	<#list md.menuItemsList as key, value>
			  	<li>
		           <!-- <a class="sidebarNavLink-c4j" href="${key}"> -->
		           <span class="sidebarNavLink-c4j <#if value??>sidebarNavLink-highlight-c4j</#if>"><em>${key}</em></span>
		           <!-- </a> -->
	        	</li>
			</#list>
			<li>
			<div style="text-align: center; vertical-align: middle; line-height: 15px; border: none; font-family: Gill Sans, Verdana; font-size: 14px; letter-spacing: 2px; font-weight: bold; color:black;">
			&#9400; 2018, <a class="sidebarNavLink-c4j" style="background:#F3FAB6; text-align:center; border: none; height: 15px; margin-bottom: 5px;" onMouseOver="this.style.color='green'" onMouseOut="this.style.color='black'" href="https://github.com/fra82/comorbidity4j/blob/master/LICENSE" target="_blank">GNU AFFERO v3</a><br/>
			<a class="sidebarNavLink-c4j" style="background:#F3FAB6; text-align:center; border: none; height: 15px; margin-bottom: 5px;" onMouseOver="this.style.color='green'" onMouseOut="this.style.color='black'" href="http://grib.imim.es/research/integrative-biomedical-informatics/index/" target="_blank">
	     			Integrative Biomedical Informatics Group, GRIB
	  			</a><br>
	  			
	  		<div>
	  		</li>
	     </ul>
	  </nav>
	</div>
</#macro>

<#-- Page footer -->
<#macro pageContent>
	<!-- Content -->
	<main class="mainContent-c4j">
	  <#nested>
	</main>
</#macro>

<#-- Error div -->
<#macro errorDiv>
	<!-- Error Div -->
	<div class="error-c4j">
	  <span style="font-weight: bold; font-size: 120%;">Error message:</span>
	  <#nested>
	</div>
</#macro>

<#-- Alert div -->
<#macro alertDiv>
<!-- Expandable alert div -->
<div class="explainInput" style="background-color: #FEE389; font-size: 80%;">
    <div class="headerExpander">
    	<span>Click for more info</span>
    </div>
    <div class="contentExpander" style="max-height: 400px; overflow: auto;">
		<#nested>
    </div>
</div>
</#macro>


<#-- Page footer -->
<#macro pageFooter>
		</div>
	</body>
</html>
</#macro>