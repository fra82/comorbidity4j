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
	  
	  <h2>4) Diagnosis description data - step A: upload file</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="descrDiagnosisData_1_specifyCSV" class="linkButton-c4j linkButtonNotActive-c4j">4.A) Upload file</a>&nbsp;
	  	<a href="descrDiagnosisData_2_validateCSV" class="linkButton-c4j">4.B) Select table columns</a>&nbsp;
	  	<a href="descrDiagnosisData_3_confirmDataImport" class="linkButton-c4j">4.C) Review uploaded data</a>
	  </div>
	  
	  <div class="navigation-c4j">
	  	<a href="diagnosisGrouping" class="linkButtonBack-c4j" style="color:red; background-color:white;">
	  	OPTIONAL STEP: click here to skip this step and proceed with data loading.</a>&nbsp;
	  </div>
	  
	  <@dm.descrDiagnosisDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  </@um.errorDiv>
	  </#if>
	  
	  <#if md.warningMessage??>
	  <div class="explainInput">
	  	  <b>Warnings generated while loading data:</b>
		  <@um.alertDiv>
		  ${md.warningMessage}
		  </@um.alertDiv>
	  </div>
	  </#if>
	  
	  <form class="sendForm" action="descrDiagnosisData_2_validateCSV" method="post" enctype="multipart/form-data" name="descrDiagnosisData_2_validateCSV_form" id="descrDiagnosisData_2_validateCSV_formID">
		
		<div class="formExplanation"></div>
		
		<div class="sendFormElemGroup">
			<div class="sendFormElem">Select Diagnosis Description Data file:&nbsp;<input name="descrDiagnosisDataFile" type="file" id="descrDiagnosisDataFileID"></div>
		</div>
		
		<div class="sendFormElemGroup">
			<div class="explanationText"></div>
			<div class="sendFormElem">Column separator:&nbsp;
				<select name="columnSeparator" id="columnSeparatorID">
				  <option value="tabSep" selected="">tab (\t)</option>
				  <option value="commaSep">comma (,)</option>
				  <option value="verticalBarSep">vertical bar (|)</option>
				</select>
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div class="explanationText"></div>
			<div class="sendFormElem">Column text delimiter:&nbsp;
				<select name="columnTextDelimiter" id="columnTextDelimiterID">
				  <option value="none" selected="">none</option>
				  <option value="doubleQuotes">double quotes (")</option>
				  <option value="singleQuote">single quote (')</option>
				</select>
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div class="explanationText"></div>
			<div class="sendFormElem">Is the first row an header row specifying column names?&nbsp;<input name="isFirstRowHeader" type="checkbox" id="isFirstRowHeaderID" checked/></div>
		</div>
		
		<div class="sendFormSubmitGroup">
			<button id="submitButton" type="submit" class="buttonStyle">Upload Diagnosis Description Data file!</button>
		</div>
	  </form>
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
