<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	
	  </div>
	  
	  <h2>1) Patient data - Step C: review uploaded data</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButton-c4j">1.A) Upload file</a>&nbsp;
	  	<a href="patientData_2_validateCSV" class="linkButton-c4j">1.B) Select table columns</a>&nbsp;
	  	<a href="patientData_3_confirmDataImport" class="linkButton-c4j linkButtonNotActive-c4j">1.C) Review uploaded data</a>
	  </div>
	  
	  <div class="navigation-c4j">
	  	
	  </div>
	  
	  <@dm.patientDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  <ul>
		  	<li><a href="patientData_2_validateCSV">Click here to correct by reviewing table columns selection of your Patient Data file</a></li>
		  	<li><a href="patientData_1_specifyCSV">Click here to correct by selecting a new / sanitized Patient Data file</a></li>
		  </ul>
		  <br/>
	  	  ...or proceed to the next step: <a href="visitData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Visit data</a><br/>
		  </@um.errorDiv>
	  <#else>
	      <div class="info-c4j">
  	      <b>Patient Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)<br/>
  	      <b>The Patient Data file has been correctly loaded. Below you can find more information on its content</b>.<br/>
  	  	  </div>
  	  	  <br/>
	  	  Proceed to the next step: <a href="visitData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Visit data</a><br/>
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
	  	<h3>Information about the Patient Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)</li>
	  		<li>
	  			<b>Column names</b>:&nbsp;
	  			<ul>
	  				<li><b>Patient ID column name / order</b>:&nbsp;${md.patientIDcolumn_PD!'-'}</li>
			  		<li><b>Patient Birth Date column name / order</b>:&nbsp;${md.patientBirthDateColumn_PD!'-'}</li>
			  		<#if md.isGenderEnabled == "true">
			  		<li><b>Patient Gender column name / order</b>:&nbsp;${md.patientGenderColumn_PD!'-'}</li>
			  		<#else>
			  		<li><b>No patient gender is considered in comorbidity analysis.</li>
			  		</#if>
			  		
			  		<#if md.patientFacet1column_PD != "__SELECT_OPTIONAL__">
				  		<#if md.patientFacet1column_PD?length &gt; 0><li><b>Patient ID stratification facet column name / order</b>:&nbsp;${md.patientFacet1column_PD!'-'}</li></#if>
			  		</#if>
	  			</ul>
	  		</li>
	  		<li><b>Patient Birth Date format</b>:&nbsp;${md.dateFormat_PD!'-'}</li>
	  		<#if md.skippedLine_PD &gt; 0>
	  		<li><b>-----</b></li>
	  		<li>
	  			<b>Number of lines skipped because of data inconsisency issues</b>:&nbsp;${md.skippedLine_PD!'-'}
	  			<ul>
			  		<li><b>Lines skipped because the date format can't be parsed</b>:&nbsp;${md.unparsableDate_PD!'-'}</li>
			  		<li><b>Lines skipped because the patient ID is a equal to the patient ID of a previous line</b>:&nbsp;${md.duplicatedPatientID_PD!'-'}</li>
			  	</ul>
	  		</li>
	  		</#if>
	  		<li><b>-----</b></li>
	  		<li><b>Number of patients correctly loaded</b>:&nbsp;${md.numberPatientsLoaded!'-'}</li>
	  	</ul>
	  </div>
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
