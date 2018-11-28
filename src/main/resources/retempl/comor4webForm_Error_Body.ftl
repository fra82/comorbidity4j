

<style>
#page-wrap {
     width: 95%;
     margin: 0 auto;
     font-family: 'Inder', sans-serif; 
     line-height: 28px;
     margin-bottom: 15px; 
     color: black; 
}

h1 { color: #4B490B; font-size: 48px; font-family: 'Signika', sans-serif; padding-bottom: 10px; }

h2 { color: #58351A; font-size: 28px; font-family: 'Signika', sans-serif; padding-bottom: 5px; }

p { font-family: 'Inder', sans-serif; line-height: 28px; margin-bottom: 15px; color: black; }

a { color: #036798; transition: .5s; -moz-transition: .5s; -webkit-transition: .5s; -o-transition: .5s; text-decoration: none; }

a:hover { color: #a03c21 }

.errorDiv {
	margin: 10px;
	padding: 20px;
	border: 2px solid #CBE32D;
	overflow: hidden;
}

.sendForm {
	margin: 10px;
	padding: 20px;
	border: 2px solid #CBE32D;
	background-color: #F3FAB6;
	overflow: hidden;
}

.sendFormElemGroup {
	margin: 5px;
	padding: 3px;
	width: 98%;
	border: 2px solid #CBE32D;
	background-color: #F3FAB6;
	float: left;
}

.explanationText {
	color: #005A31; 
}

.sendFormElem {
	padding: 3px;
	width: 98%;
	float: left;
}

.buttonSubmit {
    	text-align: center;
	margin-top: 30px;
	margin-left: auto;
	margin-right: auto;
}

.buttonStyle {
    	background-color: #CBE32D;
    	border: none;
   	color: #005A31;
    	padding: 15px 32px;
	margin-left:10px;
    	text-align: center;
    	text-decoration: none;
    	display: inline-block;
    	font-size: 16px;
	cursor: pointer;
}

.container {
    width:100%;
    border:1px solid #d3d3d3;
    margin-top: 3px;
    margin-bottom: 10px;
}
.container div {
    width:100%;
}
.container .header {
    background-color:#FFFCFC;
    padding: 2px;
    cursor: pointer;
    font-weight: bold;
}
.container .content {
    display: none;
    padding : 5px;
}

</style>

  <div id="page-wrap">
	<div style="text-align:center;margin-top:30px;margin-bottom:15px;">
		<h1>Comorbidity4web</h1>
		<b>online generation of interactive analysis of clinical comorbidities on the web</b>, based on <a href="http://comorbidity4j.readthedocs.io/en/latest/" target="_blank">Comorbidity4j</a><br/>
		developed and maintained by the
  <b><a href="http://grib.imim.es/research/integrative-biomedical-informatics/index.html" target="_blank">Integrative Biomedical Informatics Group</a></b><br/>
  <div style="font-size:80%">
  part of the Research Programme on Biomedical Informatics (GRIB), a joint research programme of the 
  <a href="http://www.imim.es/" target="_blank">Hospital del Mar Medical Research Institute (IMIM)</a> and the Department of Experimental and Health Sciences of 
  the <a href="http://www.upf.edu/" target="_blank">Universitat Pompeu Fabra</a> in Barcelona</div>
	</div>
	
	<#if (errorMsg)??>
	
	<div style="text-align:left;" class="errorDiv">
	<h2><span style="color: red;">The following errors occurred while processing your patient dataset</span></h2>
	Please, look at the error log below, <b>CORRECT THE ERROR IN THE WEB FORM OPENED IN ONE OF THE OTHER TABS OF YOUR BROWSER</b> and click again the 'Start comorbidity analysis!' button to trigger the analysis of your data.<br/><br/>
	
	<div style="text-align:left; border: 1px black solid; background-color: #FFD9DA; padding: 10px;">
	${errorMsg!'No errors.'}
	</div>
		
	</div>
	
	</#if>
	
	<br/><br/><br/><br/>

</body></html>
