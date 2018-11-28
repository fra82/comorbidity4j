<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Patient Data file</a>&nbsp;
	  	<a href="visitData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Visit Data file</a>&nbsp;
	  	<a href="diagnosisData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Diagnosis Data file</a>&nbsp;
	  </div>
	  
	  <h2>4) Diagnosis description data - Step C: review uploaded data</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="descrDiagnosisData_1_specifyCSV" class="linkButton-c4j">3.A) Upload file</a>&nbsp;
	  	<a href="descrDiagnosisData_2_validateCSV" class="linkButton-c4j">3.B) Select table columns</a>&nbsp;
	  	<a href="descrDiagnosisData_3_confirmDataImport" class="linkButton-c4j linkButtonNotActive-c4j">3.C) Review uploaded data</a>
	  </div>
	  
	  <@dm.descrDiagnosisDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  <br/>
		  <ul>
		  	<li><a href="descrDiagnosisData_2_validateCSV">Click here to correct by reviewing column parsing setting of your Diagnosis Description Data file.</a></li>
		  	<li><a href="descrDiagnosisData_1_specifyCSV">Click here to correct by selecting a new / sanitized Diagnosis Descriptio Data file.</a></li>
		  </ul>
		  <br/>
	  	  ...or proceed to the next step: <a href="diagnosisGrouping" class="linkButton-c4j">Click here to proceed by defining diagnosis grouping rules</a><br/>
		  </@um.errorDiv>
	  <#else>
	      <div class="info-c4j">
  	      <b>Diagnosis Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)<br/>
  	      <b>The Diagnosis Data file has been correctly loaded. Below you can find more information on its content</b>.<br/>
  	      </div>
  	  	  <br/>
	  	  Proceed to the next step: <a href="diagnosisGrouping" class="linkButton-c4j">Click here to proceed by defining diagnosis grouping rules</a><br/>
	  </#if>
	  
	  <#if md.warningMessage??>
	  <div class="explainInput">
	  	  <b>Warnings generated while loading data:</b>
	  	  <@um.alertDiv>
		  ${md.warningMessage}
		  </@um.alertDiv>
	  </div>
	  </#if>
	  
	  <div class="summary-c4j">
	  	<h3>Information about the Diagnosis Description Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)</li>
	  		<li>
	  			<b>Column names</b>:&nbsp;
	  			<ul>
	  				<li><b>Diagnosis Code column name / order</b>:&nbsp;${md.diagnosisCodeColumn_DDE!'-'}</li>
			  		<li><b>Diagnosis Description column name / order</b>:&nbsp;${md.diagnosisDescriptionColumn_DDE!'-'}</li>
			  	</ul>
	  		</li>
	  		
	  		<#if md.skippedLine_DDE &gt; 0>
	  		<li><b>-----</b></li>
	  		<li>
	  			<b>Number of lines skipped because of data inconsisency issues</b>:&nbsp;${md.skippedLine_DDE!'-'}
	  			<ul>
			  		<li><b>Null or empty diagnosis description string</b>:&nbsp;${md.nullOrEmptyDiagnosisDescription_DDE!'-'}</li>
			  	</ul>
	  		</li>
	  		</#if>
	  		<li><b>-----</b></li>
	  		<li><b>Number of diagnosis textual description correctly loaded</b>:&nbsp;${md.numberDiagnosisDescrLoaded!'-'}</li>
	  	</ul>
	  </div>
	  
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
