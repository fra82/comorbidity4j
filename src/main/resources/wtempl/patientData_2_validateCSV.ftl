<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>
	<div class="main">
	  <div class="navigation-c4j">
	  	
	  </div>
	  
	  <h2>1) Patient data - step B: select table columns</h2>
	 
	  <div class="navigation-c4j">
	  	<a href="patientData_1_specifyCSV" class="linkButton-c4j">1.A) Upload file</a>&nbsp;
	  	<a href="patientData_2_validateCSV" class="linkButton-c4j linkButtonNotActive-c4j">1.B) Select table columns</a>&nbsp;
	  	<a href="patientData_3_confirmDataImport" class="linkButton-c4j">1.C) Review uploaded data</a>
	  </div>
	  
	  <div class="navigation-c4j">
	  	
	  </div>
	  
	  <@dm.patientDataDescription/>
	  
	  
	  
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
  	  <b>Patient Data file name</b>:&nbsp;${md.fileName!'-'}&nbsp;(size:&nbsp;${md.fileSize!'-'}&nbsp;Mb approx.&nbsp;${md.numberOfCSVlinesRead!'-'}&nbsp;valid lines)<br/>
  	  </div>
  	  The tables below shows the first rows of the Patient Data file loaded: 
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
	  Please specify the following parameters concerning the Patient Data file:
	  <form class="sendForm" action="patientData_3_confirmDataImport" method="post" name="patientData_patientData_3_confirmDataImport" id="patientData_patientData_3_confirmDataImportID">
		
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
				<b>Which is the Patient Birth Date column?</b>&nbsp;
				<select name="patientBirthDateColumn" id="patientBirthDateColumnID">
				  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
				  <#list 0..md.columnNameList?size-1 as i>
					<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
				  </#list>
				</select>
			</div>
			<div class="explanationText">This column specifies the birth date of the patient.&nbsp;(mandatory)</div>
		</div>
		
		<div class="sendFormElemGroup">
			<input type="checkbox" id="patGenderAnalysisEnabled" name="patGenderAnalysisEnabled_name"/>&nbsp;Enable patient gender analysis (to enable the execution of gender specific comorbidity analysis and explore sex ratio)<br/>
			<div id="patGenderFormElement">
				<div class="sendFormElem">
					<b>Which is the Patient Gender column?</b>&nbsp;
					<select name="patientGenderColumn" id="patientGenderColumnID">
					  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
					  <#list 0..md.columnNameList?size-1 as i>
						<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
					  </#list>
					</select>
				</div>
				<div class="explanationText">This column specifies the gender of the patient (any set non-empty String can be used).&nbsp;(optional)</div>
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div class="sendFormElem">
				<b>Which is the Patient Stratification Facet column?</b>&nbsp;
				<select name="patientFacet1column" id="patientFacet1columnID">
				  <option value="__SELECT_OPTIONAL__" selected="">__SELECT_OPTIONAL__</option>
				  <#list 0..md.columnNameList?size-1 as i>
					<option value="${md.columnNameList[i]!'-'}">Column: ${md.columnNameList[i]!'-'}</option>
				  </#list>
				</select>
			</div>
			<div class="explanationText">This column specifies any arbitrary feature useful to characterize the patient 
			(by means of a set of strings / nominal values - any set of non-empty String can be used - e.g.: each patient can be characterized as: 
			DIABETIC, NOT_DIABETIC, UNKNOWN). If present, these column values will be exploited to stratify patient population in order to execute scoped comobidity analyses. &nbsp;(optional, '__SELECT_OPTIONAL__' to leave unspecified)</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div id="patientDateInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			
			<div id="patientDateInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				Examples of date formatting patterns:
				<ul>
					<li><b>dd-MM-yy</b>&nbsp;to parse a date like&nbsp;31-01-12</li>
					<li><b>dd-MM-yyyy</b>&nbsp;to parse a date like&nbsp;31-01-2012</li>
					<li><b>MM-dd-yyyy</b>&nbsp;to parse a date like&nbsp;01-31-2012</li>
					<li><b>yyyy-MM-dd</b>&nbsp;to parse a date like&nbsp;2012-01-31</li>
					<li><b>yyyy-MM-dd HH:mm:ss</b>&nbsp;to parse a date like&nbsp;2012-01-31 23:59:59</li>
					<li><b>yyyy-MM-dd HH:mm:ss.SSS</b>&nbsp;to parse a date like&nbsp;2012-01-31 23:59:59.999</li>
					<li><b>yyyy-MM-dd HH:mm:ss.SSSZ</b>&nbsp;to parse a date like&nbsp;2012-01-31 23:59:59.999+0100</li>
					<li><b>EEEEE MMMMM yyyy HH:mm:ss.SSSZ</b>&nbsp;to parse a date like&nbsp;Saturday November 2012 10:45:42.720+0100</li>
				</ul>
				
				For more information, please visit: <a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html" target="_blank">this documentation Web page</a>.
			</div>
			
			<b>Date format used in Patient Birth Date column</b>
			<div class="explanationText">
			Define the format used to parse the 'birth date' column</div>
			<div class="sendFormElem">Date format (yyyy = year, MM = month, dd = day, hh=hours, mm=minutes, ss=seconds, SSS=milliseconds):&nbsp;
			<input name="dateFormat" type="text" id="dateFormatID" value="${md.guessedDateFormat!'AAAdd-MM-yyyy'}" data-validation="length" 
				data-validation-length="min1"/> <#if md.guessedDateFormat??>(date format guessed by parsing table entries)</#if></div>
		</div>
		
		<div class="sendFormSubmitGroup">
			<button id="submitButton" type="submit" class="buttonStyle">Confirm column selection</button>
		</div>
	  </form>
	  
	  <div id="dialogAlert"> <div id="dialogAlertInt"></div></div>
	  
	  <div id="infoDialog" title="">
	  </div>
	  
	  <script type="text/javascript">
	  	   function unique(list) {
			    var result = [];
			    $.each(list, function(i, e) {
			        if ($.inArray(e, result) == -1) result.push(e);
			    });
			    return result;
		   }
	  	   
	  	   var infoDialogRef = null;
	  	   
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
				    var patientBirthDateColumn = $( "#patientBirthDateColumnID" ).val(); 
				    var patientGenderColumn = $( "#patientGenderColumnID" ).val(); 
				    var patientFacet1column = $( "#patientFacet1columnID" ).val(); 
				    
				    // Check values
				    var alertMsg = "";
				    if(patientIDcolumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the patient ID column.<br/>"
				    }
				    if(patientBirthDateColumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the patient Birth Date column.<br/>"
				    }
				    if($('#patGenderAnalysisEnabled').is(':checked') && patientGenderColumn == "__SELECT_MANDATORY__") {
				    	alertMsg = alertMsg + "- select the patient Gender column or disable the patient geneder analysis.<br/>"
				    }
				    
				    if(patientFacet1column != "__SELECT_OPTIONAL__") {
				    	var arrayValsInt = (($('#patGenderAnalysisEnabled').is(':checked')) ? [patientIDcolumn, patientBirthDateColumn, patientGenderColumn, patientFacet1column] : [patientIDcolumn, patientBirthDateColumn, patientFacet1column]);
				    	var arrayValsIntUnique = unique(arrayValsInt)
				    	
				    	if(arrayValsIntUnique.length != arrayValsInt.length) {
					    	alertMsg = alertMsg + (($('#patGenderAnalysisEnabled').is(':checked')) ? "- select different values as ID, Birth Date, Gender and Patient Stratification Facet columns.<br/>" : "- select different values as ID, Birth Date and Patient Stratification Facet columns.<br/>");
					    }
				    }
				    else {
				    	var arrayVals = (($('#patGenderAnalysisEnabled').is(':checked')) ? [patientIDcolumn, patientBirthDateColumn, patientGenderColumn] : [patientIDcolumn, patientBirthDateColumn]); 
				    	var arrayValsUnique = unique(arrayVals)
					    
					    if(arrayValsUnique.length != arrayVals.length) {
					    	alertMsg = alertMsg + (($('#patGenderAnalysisEnabled').is(':checked')) ? "- select different values as ID, Birth Date and Gender columns.<br/>" : "- select different values as ID and Birth Date columns.<br/>");
					    }
				    }
				    
				    if(alertMsg.length > 0) {
				      alertMsg = "Please, correct the following issues to proceed:<br/>" + alertMsg;
				      $("#dialogAlertInt").html(alertMsg);
				      e.preventDefault();
				      $("#dialogAlert").dialog("open");
				    }
				});
				
				$("#patGenderAnalysisEnabled").change(function() {
					if($('#patGenderAnalysisEnabled').is(':checked')) {
						$("#patGenderFormElement").show();
					}
					else {
						$("#patGenderFormElement").hide();
					}
				});
				
				$('#patGenderAnalysisEnabled').prop('checked', false);
				$("#patGenderFormElement").hide();
				
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
				
				$("#patientDateInfoD").click(function() {
					openInfoDialog("INFO: Date format info", "patientDateInfoD_cont");
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
	  
	  
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
