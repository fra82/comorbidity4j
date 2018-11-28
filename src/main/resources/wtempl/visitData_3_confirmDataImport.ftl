<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Patient Data file</a>&nbsp;
	  </div>
	  
	  <h2>2) Visit data - Step C: review uploaded data</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="visitData_1_specifyCSV" class="linkButton-c4j">2.A) Upload file</a>&nbsp;
	  	<a href="visitData_2_validateCSV" class="linkButton-c4j">2.B) Select table columns</a>&nbsp;
	  	<a href="visitData_3_confirmDataImport" class="linkButton-c4j linkButtonNotActive-c4j">2.C) Review uploaded data</a>
	  </div>
	  
	  
	  <@dm.visitDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  <br/>
		  <ul>
		  	<li><a href="visitData_2_validateCSV">Click here to correct by reviewing column parsing setting of your Visit Data file.</a></li>
		  	<li><a href="visitData_1_specifyCSV">Click here to correct by selecting a new / sanitized Visit Data file.</a></li>
		  </ul>
		  <br/>
	  	  ...or proceed to the next step: <a href="diagnosisData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Diagnosis data</a><br/>
		  </@um.errorDiv>
	  <#else>
	      <div class="info-c4j">
  	      <b>Visit Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)<br/>
  	      <b>The Visit Data file has been correctly loaded. Below you can find more information on its content</b>.<br/>
  	      </div>
  	  	  <br/>
	  	  Proceed to the next step: <a href="diagnosisData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Diagnosis data</a><br/>
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
	  	<h3>Information about the Visit Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)</li>
	  		<li>
	  			<b>Column names</b>:&nbsp;
	  			<ul>
	  				<li><b>Patient ID column name / order</b>:&nbsp;${md.patientIDcolumn_VD!'-'}</li>
			  		<li><b>Visit ID column name / order</b>:&nbsp;${md.visitIDcolumn_VD!'-'}</li>
			  		<li><b>Visit Date column name / order</b>:&nbsp;${md.visitStartDateColumn_VD!'-'}</li>
			  	</ul>
	  		</li>
	  		<li><b>Visit Date format</b>:&nbsp;${md.dateFormat_VD!'-'}</li>
	  		
	  		<#if md.skippedLine_VD &gt; 0>
	  		<li><b>-----</b></li>
	  		<li>
	  			<b>Number of lines skipped because of data inconsisency issues</b>:&nbsp;${md.skippedLine_VD!'-'}
	  			<ul>
			  		<li><b>Lines skipped because the date format can't be parsed</b>:&nbsp;${md.unparsableVisitDate_VD!'-'}</li>
			  		<li><b>Lines skipped because the patient ID is a equal to the patient ID of a previous line</b>:&nbsp;${md.duplicatedVisitID_VD!'-'}</li>
			  	</ul>
	  		</li>
	  		</#if>
	  		<li><b>-----</b></li>
	  		<li><b>Number of visits correctly loaded</b>:&nbsp;${md.numberVisitsLoaded!'-'}</li>
	  	</ul>
	  </div>
	  
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
