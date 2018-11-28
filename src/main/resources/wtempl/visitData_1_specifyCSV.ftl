<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButtonBack-c4j">Upload new Patient Data file</a>&nbsp;
	  </div>
	  
	  <h2>2) Visit data - step A: upload file</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="visitData_1_specifyCSV" class="linkButton-c4j linkButtonNotActive-c4j">2.A) Upload file</a>&nbsp;
	  	<a href="visitData_2_validateCSV" class="linkButton-c4j">2.B) Select table columns</a>&nbsp;
	  	<a href="visitData_3_confirmDataImport" class="linkButton-c4j">2.C) Review uploaded data</a>
	  </div>
	  
	  
	  
	  <@dm.visitDataDescription/>
	  
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
	  
	  <div id="infoDialog" title="">
	  </div>
	  
	  <script type="text/javascript">
	  	var infoDialogRef = null;
	   
	   	$(document).ready(function() {
	   		
			infoDialogRef = $("#infoDialog").dialog({
			      resizable: true,
		      	  autoOpen: false,
			      height: "auto",
			      width: "auto",
			      modal: true,
			      closeOnEscape: true,
			      buttons: {
			        "Close": function() {
			          $(this).html("");
			          $(this).dialog("close");
			        }
			      }
			 });
			 
			 
			$("#OMOPexplanationD").click(function() {
				openInfoDialog("INFO: Visit Data in OMOP format", "OMOPexplanationD_cont");
			});
	   	
	   	});
	   	
	   	function openInfoDialog(title, HTMLcontentID) {
			$("#infoDialog").dialog('option', 'title', title);
			var clone = $("#" + HTMLcontentID).clone(true);
			infoDialogRef.html("");
			infoDialogRef.html(clone.html());
			$("#infoDialog").dialog("open");
		}
	   	
	  </script>
	  
	  <form class="sendForm" action="visitData_2_validateCSV" method="post" enctype="multipart/form-data" name="visitData_2_validateCSV_form" id="visitData_2_validateCSV_formID">
		
		<div class="formExplanation"></div>
		
		<div class="sendFormElemGroup">
			<div class="sendFormElem">Select Visit Data file:&nbsp;<input name="visitDataFile" type="file" id="visitDataFileID"></div>
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
		
		<div class="sendFormElemGroup">
			<input name="isOMOP" id="isOMOPID" type="checkbox" value="isOMOPactive">&nbsp;
			Check if you are providing Visit Data by means of a a tabular UTF-8 text file (i.e. CSV, TSV) in the format defined by the 
			<a href="https://github.com/OHDSI/CommonDataModel/wiki/VISIT_OCCURRENCE" target="_blank">VISIT_OCCURRENCE table</a> 
			of the  
			<a href="https://github.com/OHDSI/CommonDataModel/wiki" target="_blank">OHDSI Common Data Model</a>.&nbsp;
			<div id="OMOPexplanationD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>&nbsp;
			
			<div id="OMOPexplanationD_cont" class="contentExpanderForm" style="display: none;"> 
				In a Visit Data table compliant with the <a href="https://github.com/OHDSI/CommonDataModel/wiki" target="_blank">OHDSI Common Data Model</a> 
				we expect an header row and the following column names (as for OMOP specifications):
				<ul>
					<li><b>person_id</b>:&nbsp;the unambiguous and unique identifier of a patient</li>
					<li><b>visit_occurrence_id</b>:&nbsp;the unambiguous and unique identifier of a visit</li>
					<li><b>visit_start_date</b>:&nbsp;the date when the visit occurred (the format of the date is automatically guessed)</li>
				</ul>
			</div>
			
		</div>
		
		<div class="sendFormSubmitGroup">
			<button id="submitButton" type="submit" class="buttonStyle">Upload Visit Data file!</button>
		</div>
	  </form>
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
