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
	  
	  <h2>2) Diagnosis data - Step B: select table columns</h2>
	  
	  <div class="navigation-c4j">
	  	<a href="diagnosisData_1_specifyCSV" class="linkButton-c4j">3.A) Upload file</a>&nbsp;
	  	<a href="diagnosisData_2_validateCSV" class="linkButton-c4j linkButtonNotActive-c4j">3.B) Select table columns</a>&nbsp;
	  	<a href="diagnosisData_3_confirmDataImport" class="linkButton-c4j">3.C) Review uploaded data</a>
	  </div>
	 
	  <@dm.diagnosisDataDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  </@um.errorDiv>
	  </#if>
	  
	  <#if md.warningMessage??>
		  <@um.alertDiv>
		  ${md.warningMessage}
		  </@um.alertDiv>
	  </#if>
	  
	  
      <div class="info-c4j">
  	  <b>Diagnosis Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.&nbsp;${md.numberOfCSVlinesRead!'-'}&nbsp;valid lines)<br/>
  	  </div>
  	  The tables below shows the first rows of the Diagnosis Data file loaded: 
  	  <div id="firstTableRows"></div>
  	
  	  <script type="text/javascript">
	   $(document).ready(function() {
			$("#firstTableRows").tabulator({
				layout:"fitColumns",
				movableColumns:false,
				resizableRows:false,
				columns:${md.sampleCSVcolumns!'[]'},
				data: ${md.sampleCSVrows!'[]'}
			});
		});
	  </script>
	  
	  <br/>
	  Please specify the following parameters concerning the Diagnosis Data file:
	  <form class="sendForm" action="diagnosisData_3_confirmDataImport" method="post" name="diagnosisData_3_confirmDataImport" id="diagnosisData_3_confirmDataImportID">
		
		<div class="formExplanation"></div>
		
		<div class="sendFormElemGroup">
			<div class="sendFormElem">
				<b>Which is the Patient ID column?</b>&nbsp;
				<select name="patientIDcolumn" id="patientIDcolumnID">
				  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
				  <#list 0..md.columnNameList?size-1 as i>
					<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
				  </#list>
				</select>
			</div>
			<div class="explanationText">This column specifies the ID that unambiguously identifies the patient (any non-empty String can be used).&nbsp;(mandatory)</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div class="sendFormElem">
				<b>Which is the Visit ID column?</b>&nbsp;
				<select name="visitIDcolumn" id="visitIDcolumnID">
				  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
				  <#list 0..md.columnNameList?size-1 as i>
					<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
				  </#list>
				</select>
			</div>
			<div class="explanationText">This column specifies the ID that unambiguously identifies the visit of the patient. Each patient should have a different visit ID for each one of her / his visits (any non-empty String can be used).&nbsp;(mandatory)</div>
		</div>
		
		<div class="sendFormElemGroup">
			
			<div class="sendFormElem">
				<b>Which is the Diagnosis Code column?</b>&nbsp;
				<select name="diagnosisCodeColumn" id="diagnosisCodeColumnID">
				  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
				  <#list 0..md.columnNameList?size-1 as i>
					<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
				  </#list>
				</select>
			</div>
			<div class="explanationText">This column specifies the ID that unambiguously identifies the disease / diagnosis related to the visit (any non-empty String can be used - i.e. ICD9, SNOMED-CT, CUI or any other identifier).&nbsp;(mandatory)</div>
		</div>
		
		<div class="sendFormSubmitGroup">
			<button id="submitButton" type="submit" class="buttonStyle">Confirm column selection</button>
		</div>
	  </form>
	  
	  <div id="dialogAlert"> <div id="dialogAlertInt"></div></div>
	  
	  <script type="text/javascript">
	  	   function unique(list) {
			    var result = [];
			    $.each(list, function(i, e) {
			        if ($.inArray(e, result) == -1) result.push(e);
			    });
			    return result;
			}
	  
		   $(document).ready(function() {
		   		
		   		$('#dialogAlert').dialog({ 
		   			  resizable: true,
			      	  autoOpen: false,
				      height: "auto",
				      width: "auto",
				      modal: true,
				      closeOnEscape: true,
				      title: "Attention",
				      buttons: {
				        Ok: function() {
				          $(this).dialog( "close" );
				        }
				      }
				});
		   	
		   	
				$(document)
				.on('click', 'form button[type=submit]', function(e) {
				    var patientIDcolumn = $( "#patientIDcolumnID" ).val(); 
				    var visitIDcolumn = $( "#visitIDcolumnID" ).val(); 
				    var diagnosisCodeColumn = $( "#diagnosisCodeColumnID" ).val();
				    
				    // Check values
				    var alertMsg = "";
				    if(patientIDcolumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the patient ID column.<br/>"
				    }
				    if(visitIDcolumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the visit ID column.<br/>"
				    }
				    if(diagnosisCodeColumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the Diagnosis Code column.<br/>"
				    }
				    
			    	var arrayVals = [patientIDcolumn, visitIDcolumn, diagnosisCodeColumn]
				    var arrayValsUnique = unique(arrayVals)
				    
				    if(arrayValsUnique.length != arrayVals.length) {
				    	alertMsg = alertMsg + "- select different values as patient ID, visit ID and Diagnosis Code columns.<br/>"
				    }
				    
				    
				    if(alertMsg.length > 0) {
				      alertMsg = "Please, correct the following issues to proceed:<br/>" + alertMsg;
				      $("#dialogAlertInt").html(alertMsg);
				      e.preventDefault();
				      $("#dialogAlert").dialog("open");
				    }
				});
			});
	  </script>
	  
	  
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
