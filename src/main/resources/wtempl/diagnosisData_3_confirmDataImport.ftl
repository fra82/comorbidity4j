<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Patient Data file</a>&nbsp;
	  	<a href="visitData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Visit Data file</a>&nbsp;
	  </div>
	  
	  <h2>2) Diagnosis data - Step C: review uploaded data</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="diagnosisData_1_specifyCSV" class="linkButton-c4j">3.A) Upload file</a>&nbsp;
	  	<a href="diagnosisData_2_validateCSV" class="linkButton-c4j">3.B) Select table columns</a>&nbsp;
	  	<a href="diagnosisData_3_confirmDataImport" class="linkButton-c4j linkButtonNotActive-c4j">3.C) Review uploaded data</a>
	  </div>
	  
	  
	  <@dm.diagnosisDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  <br/>
		  <ul>
		  	<li><a href="diagnosisData_2_validateCSV">Click here to correct by reviewing column parsing setting of your Diagnosis Data file.</a></li>
		  	<li><a href="diagnosisData_1_specifyCSV">Click here to correct by selecting a new / sanitized Diagnosis Data file.</a></li>
		  </ul>
		  <br/>
	  	  ...or proceed to the next step: <a href="descrDiagnosisData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Diagnosis Descriptions if any</a><br/>
		  </@um.errorDiv>
	  <#else>
	      <div class="info-c4j">
  	      <b>Diagnosis Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)<br/>
  	      <b>The Diagnosis Data file has been correctly loaded. Below you can find more information on its content</b>.<br/>
  	      </div>
  	  	  <br/>
	  	  Proceed to the next step: <a href="descrDiagnosisData_1_specifyCSV" class="linkButton-c4j">Click here to proceed with data loading by uploading the Diagnosis Descriptions if any</a><br/>
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
	  	<h3>Information about the Diagnosis Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.)</li>
	  		<li>
	  			<b>Column names</b>:&nbsp;
	  			<ul>
	  				<li><b>Patient ID column name / order</b>:&nbsp;${md.patientIDcolumn_DD!'-'}</li>
			  		<li><b>Visit ID column name / order</b>:&nbsp;${md.visitIDcolumn_DD!'-'}</li>
			  		<li><b>Diagnosis Code column name / order</b>:&nbsp;${md.diagnosisCodeColumn_DD!'-'}</li>
			  	</ul>
	  		</li>
	  		
	  		<#if md.skippedLine_DD &gt; 0>
	  		<li><b>-----</b></li>
	  		<li>
	  			<b>Number of lines skipped because of data inconsisency issues</b>:&nbsp;${md.skippedLine_DD!'-'}
	  			<ul>
			  		<li><b>Lines skipped because there is already the same diagnosis associated to a specific patient in the context of a specific visit</b>:&nbsp;${md.duplicatedPatVisitDiagnosis_DD!'-'}</li>
			  		<li><b>Lines skipped because the patient ID or the visit ID have not been specified in the Patient Data or Visit Data file respectively </b>:&nbsp;${md.unexistingPatientOrVisitID_DD!'-'}</li>
			  	</ul>
	  		</li>
	  		</#if>
	  		<li><b>-----</b></li>
	  		<li><b>Number of (patient_id, visit_id, diagnosis_code) associations correctly loaded</b>:&nbsp;${md.numberDiagnosisLoaded!'-'}</li>
	  	</ul>
	  </div>
	  
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
