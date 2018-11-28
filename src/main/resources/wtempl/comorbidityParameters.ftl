<#import "utilMacro.ftl" as um>
<#import "descriptionMacro.ftl" as dm>

<@um.pageHeader/>

<@um.printMenu/>
	

<@um.pageContent>

	<!--[if lt IE 9]><script src="http://cdnjs.cloudflare.com/ajax/libs/es5-shim/2.0.8/es5-shim.min.js"></script><![endif]-->
	
	<div class="main">
	  <div class="navigation-c4j">
	  	<div class="linkButtonBack-c4j"><a href="patientData_1_specifyCSV">Upload new Patient Data file</a></div>&nbsp;
	  	<div class="linkButtonBack-c4j"><a href="visitData_1_specifyCSV">Upload new Visit Data file</a></div>&nbsp;
	  	<div class="linkButtonBack-c4j"><a href="diagnosisData_1_specifyCSV">Upload new Diagnosis Data file</a></div>&nbsp;
	  	<div class="linkButtonBack-c4j"><a href="descrDiagnosisData_1_specifyCSV">Upload new Diagnosis Description Data file</a></div>&nbsp;
	  	<div class="linkButtonBack-c4j"><a href="diagnosisGrouping">Define new diagnosis groups</a></div>&nbsp;
	  	<div class="linkButtonBack-c4j"><a href="diagnosisPairing">Define new diagnosis pairing patterns</a></div>&nbsp;
	  </div>
	  
	  <div class="navigation-c4j">
	  	<h2>7) Define comorbidity analysis parameters</h2>
	  </div>
	  
	  <@dm.comorbidityAnalysisParametersDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  </@um.errorDiv>
	  </#if>
	  
	  <div id="dialogAlert"> <div id="dialogAlertInt"></div></div>
	  
	  <div id="infoDialog" title="">
	  </div>
	  
	  <script type="text/javascript">
	   
	   var infoDialogRef = null;
	   
	   $(document).ready(function() {
	   		// Directionality controls
			$("#isDirectionalID").change(function() {
				var currentValue = $("#isDirectionalID").val(); // enabDirect, disabDirec
				
				if(currentValue == "enabDirect") {
					$("#directMinDaysDiv").show();
					$("#directMinDaysID").focus();
				}
				else {
					$("#directMinDaysID").blur();
					$("#directMinDaysDiv").hide();
				}
			});
			$("#directMinDaysID").val("");
			$("#directMinDaysDiv").hide();
			
			// Enable filters
			$("#minAgeCb").change(function() {
				if($('#minAgeCb').is(':checked')) {
					$("#FPATminAgeDiv").show();
					$("#FPATminAgeID").focus();
				}
				else {
					$("#FPATminAgeID").blur();
					$("#FPATminAgeDiv").hide();
				}
			});
			$("#FPATminAgeDiv").hide();
			
			$("#maxAgeCb").change(function() {
				if($('#maxAgeCb').is(':checked')) {
					$("#FPATmaxAgeDiv").show();
					$("#FPATmaxAgeID").focus();
				}
				else {
					$("#FPATmaxAgeID").blur();
					$("#FPATmaxAgeDiv").hide();
				}
			});
			$("#FPATmaxAgeID").blur();
			$("#FPATmaxAgeDiv").hide();
			
			$("#FPATCb").change(function() {
				if($('#FPATCb').is(':checked')) {
					$("#FPATCbCont").show();
				}
				else {
					$("#FPATCbCont").hide();
				}
			});
			$("#FPATCbCont").hide();
			
			
			$("#FCOMscoreCb").change(function() {
				if($('#FCOMscoreCb').is(':checked')) {
					$("#FCOMscoreDiv").show();
					$("#FCOMscoreID").focus();
				}
				else {
					$("#FCOMscoreID").blur();
					$("#FCOMscoreDiv").hide();
				}
			});
			$("#FCOMscoreID").blur();
			$("#FCOMscoreDiv").hide();
			
			
			$("#FCOMrriskCb").change(function() {
				if($('#FCOMrriskCb').is(':checked')) {
					$("#FCOMrriskDiv").show();
					$("#FCOMrriskID").focus();
				}
				else {
					$("#FCOMrriskID").blur();
					$("#FCOMrriskDiv").hide();
				}
			});
			$("#FCOMrriskID").blur();
			$("#FCOMrriskDiv").hide();
			
			
			$("#FCOModdsRatioCb").change(function() {
				if($('#FCOModdsRatioCb').is(':checked')) {
					$("#FCOModdsRatioDiv").show();
					$("#FCOModdsRatioID").focus();
				}
				else {
					$("#FCOModdsRatioID").blur();
					$("#FCOModdsRatioDiv").hide();
				}
			});
			$("#FCOModdsRatioID").blur();
			$("#FCOModdsRatioDiv").hide();
			
			
			$("#FCOMphiCb").change(function() {
				if($('#FCOMphiCb').is(':checked')) {
					$("#FCOMphiDiv").show();
					$("#FCOMphiDiv").focus();
				}
				else {
					$("#FCOMphiID").blur();
					$("#FCOMphiDiv").hide();
				}
			});
			$("#FCOMphiID").blur();
			$("#FCOMphiDiv").hide();
			
			
			$("#FCOMfisherAdjCb").change(function() {
				if($('#FCOMfisherAdjCb').is(':checked')) {
					$("#FCOMfisherAdjDiv").show();
					$("#FCOMfisherAdjID").focus();
				}
				else {
					$("#FCOMfisherAdjID").blur();
					$("#FCOMfisherAdjDiv").hide();
				}
			});
			$("#FCOMfisherAdjID").blur();
			$("#FCOMfisherAdjDiv").hide();
			
			$("#FCOMminPatCb").change(function() {
				if($('#FCOMminPatCb').is(':checked')) {
					$("#FCOMminPatDiv").show();
					$("#FCOMminPatID").focus();
				}
				else {
					$("#FCOMminPatID").blur();
					$("#FCOMminPatDiv").hide();
				}
			});
			$("#FCOMminPatID").blur();
			$("#FCOMminPatDiv").hide();
			
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
			 
			 
			
			// Form submit check
			$.validate({
			   onError : function($form) {
                    alert('Invalid '+$form.attr('id'));
                },
                onSuccess : function($form) {
                	
                	var alertMsg = "";
                	
                	if($('#femaleIdentifierID').length) {
                		var femaleIdentifier = $( "#femaleIdentifierID" ).val();
					    var maleIdentifier = $( "#maleIdentifierID" ).val(); 
					    
					    if(femaleIdentifier == "__SELECT_MANDATORY__" || maleIdentifier == "__SELECT_MANDATORY__") {
					    	alertMsg += "- select both a male and female identifiers.<br/>";
					    }
					    else if (femaleIdentifier == maleIdentifier) {
					    	alertMsg += "- select two different identifiers as female and male identifiers.<br/>";
					    }
                	}
                
                    if($('#FPATCb').length) {
                		var enabledPatFilter = $('#FPATCb').is(":checked");
                		
                		var FPATclassification1array = [];
						$('[name="FPATclassification1"]:checked').each(function(i,e) {
						    FPATclassification1array.push(e.value);
						});
					    
					    if(enabledPatFilter && FPATclassification1array.length == 0) {
					    	alertMsg += "- select at least one patient facet or disable patient facet filter.<br/>";
					    }
                	}
                	
                	var duirectEnabledVal = $("#isDirectionalID").val(); // enabDirect, disabDirec
					if(duirectEnabledVal == "enabDirect") {
						var minDaysVal = $("#directMinDaysID").val();
						if(!minDaysVal || 0 === minDaysVal.length) {
							alertMsg += "- select a valid minimum number of days for the directionality analysis.<br/>";
						}
					}
                	
                	
				    
				    if(alertMsg.length > 0) {
				      alertMsg = "Please, correct the following issues to proceed:<br/>" + alertMsg;
				      $("#dialogAlertInt").html(alertMsg);
				      $("#dialogAlert").dialog("open");
				      return false;
				    }
				    
                    return true;
               }
			});
			
			
			$("#directionalityInfoD").click(function() {
				openInfoDialog("INFO: Directionality analysis", "directionalityInfoD_cont");
			});
			
			$("#patientAgeComputationInfoD").click(function() {
				openInfoDialog("INFO: Patient age computation approach", "patientAgeComputationInfoD_cont");
			});
			
			$("#pvalAdjApproachInfoD").click(function() {
				openInfoDialog("INFO: P-value adjustment approach (for multiple testing)", "pvalAdjApproachInfoD_cont");
			});
			
			$("#genderIdentifierInfoD").click(function() {
				openInfoDialog("INFO: Gender identifier", "genderIdentifierInfoD_cont");
			});
			
			$("#patientFilterInfoD").click(function() {
				openInfoDialog("INFO: Patient filters", "patientFilterInfoD_cont");
			});
			
			$("#comorbidityFilterInfoD").click(function() {
				openInfoDialog("INFO: Comorbidity scores filters", "comorbidityFilterInfoD_cont");
			});
			
			
			
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
			
		});
		
		function openInfoDialog(title, HTMLcontentID) {
			$("#infoDialog").dialog('option', 'title', title);
			var clone = $("#" + HTMLcontentID).clone(true);
			infoDialogRef.html("");
			infoDialogRef.html(clone.html());
			$("#infoDialog").dialog("open");
		} 
		
		
		
	  </script>
	  
	  <form class="sendForm" action="startAnalysis" method="post" enctype="multipart/form-data" name="comorbidityParametersForm" id="comorbidityParametersFormID">
	  	
	  	<#if md.femaleIdentifierList?? && md.femaleIdentifierList?has_content>
	  	<div class="sendFormElemGroup">
			<div id="genderIdentifierInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>Gender identifiers in patient dataset</b>&nbsp;<br/>
			Identifier for FEMALE:&nbsp;
			<select name="femaleIdentifier" id="femaleIdentifierID">
		 	  	<option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
				<#list 0..md.femaleIdentifierList?size-1 as i>
					<option value="${md.femaleIdentifierList[i]!'-'}">${md.femaleIdentifierList[i]!'-'}</option>
				</#list>
			</select>
			<br/>
			Identifier for MALE:&nbsp;
			<select name="maleIdentifier" id="maleIdentifierID">
		 	  <option value="__SELECT_MANDATORY__" selected="">__SELECT_MANDATORY__</option>
			  <#list 0..md.maleIdentifierList?size-1 as i>
					<option value="${md.maleIdentifierList[i]!'-'}">${md.maleIdentifierList[i]!'-'}</option>
			  </#list>
			</select>
			<div id="genderIdentifierInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				Identifiers of male and female genders to compute the sex ratio parameter of each comorbidity pair.
			    The following two values are the ones used in the gender column of the Patient data file in order to specify the gender of each patient.
			</div>
		</div>
		</#if>
	  	
	  	<div class="sendFormElemGroup">
			<div id="directionalityInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>Directionality analysis</b>:&nbsp;
			<select name="isDirectional" id="isDirectionalID">
				  <option value="enabDirect">enabled</option>
				  <option value="disabDirec" selected>disabled</option>
			</select>
			<div id="directMinDaysDiv">
				Minimum number of days between two diagnoses of the same patient, occurring in different visits, 
				to consider such pair of diagnoses valid for the analysis:&nbsp;<br/>
				<input name="directMinDays" type="text" id="directMinDaysID" value="" 
				data-validation-help="Number: range[1;100000]" data-validation="number" data-validation-allowing="range[1;100000]" data-validation-optional="true"/>
			</div>
			<div id="directionalityInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				When diagnosis time directionality is not enabled, a patient is considered to suffer a pair of diagnoses if she/he experimented 
				both of them during her/his clinical history, independently from their temporal order.
				When diagnosis time directionality is enabled, a patient is considered to suffer an ordered pair of diagnoses if the first 
				diagnosis precedes the second diagnosis by a number of days equal or greater than the previous user defined value.
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div id="patientAgeComputationInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>Patient age computation approach</b>:&nbsp;
			<select name="patientAgeComputation" id="patientAgeComputationID">
		 	  <option value="FIRST_ADMISSION">FIRST_ADMISSION</option>
			  <option value="FIRST_DIAGNOSTIC">FIRST_DIAGNOSTIC</option>
			  <option value="LAST_ADMISSION">LAST_ADMISSION</option>
			  <option value="LAST_DIAGNOSTIC" selected>LAST_DIAGNOSTIC</option>
			  <option value="EXECUTION_TIME">EXECUTION_TIME</option>
			</select>
			<div id="patientAgeComputationInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				By means of the 'Patient filters' (see below), it is possible to filter patients that should be analyzed for comorbidities by age. 
				The age of a patient can be determined by one of the following approaches:			
				<ul>
				  <li><b>FIRST_ADMISSION</b>: the age of the patient at the moment (date) of the first visit (with or without diagnosis associated)
				  <li><b>FIRST_DIAGNOSTIC</b>: the age of the patient at the moment (date) of the first visit with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)
				  <li><b>LAST_ADMISSION</b>: the age of the patient at the moment (date) of the last visit (with or without diagnosis associated)
				  <li><b>LAST_DIAGNOSTIC</b>: the age of the patient at the moment (date) of the last visit with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)
				  <li><b>EXECUTION_TIME</b>: the age of the patient at execution time
				</ul>
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<div id="pvalAdjApproachInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>P-value adjustment approach (for multiple testing)</b>:&nbsp;
			<select name="pvalAdjApproach" id="pvalAdjApproachID">
		 	  <option value="BONFERRONI">BONFERRONI</option>
			  <option value="BENJAMINI_HOCHBERG" selected>BENJAMINI_HOCHBERG</option>
			  <option value="HOLM">HOLM</option>
			  <option value="HOCHBERG">HOCHBERG</option>
			  <option value="BENJAMINI_YEKUTIELI">BENJAMINI_YEKUTIELI</option>
			  <option value="HOMMEL">HOMMEL</option>
			</select>
			<div id="pvalAdjApproachInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				One of the following approaches can be exploited to adjust p-value in case of multiple testing 
			    (for more information refer to the R method <a href="https://stat.ethz.ch/R-manual/R-devel/library/stats/html/p.adjust.html" target="_blank">p.adjust</a>).
			</div>
		</div>
		
		<div class="sendFormElemGroup">
			<b>Odds ratio confidence interval</b>:&nbsp;
			<input name="oddsRatioConfidenceInterval" type="text" id="oddsRatioConfidenceIntervalID" value="0.95" 
			data-validation-help="[floar: > 0 and < 1]" data-validation="number" data-validation-allowing="float,range[0;1]" data-validation-optional="false"/>
		</div>
		
		
		<div class="sendFormElemGroup">
			<div id="patientFilterInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>Patient filters</b>:&nbsp;<br/>
			<input type="checkbox" id="minAgeCb" name="minAgeCb_name"/>&nbsp;Minimum age&nbsp;
			<div id="FPATminAgeDiv" style="display: inline;">
			<input name="FPATminAge" type="text" id="FPATminAgeID" value="" 
			data-validation-help="[integer number > 0]" data-validation="number" data-validation-allowing="positive,range[1;10000000]" data-validation-optional="false"/>
			</div><br/>
			<input type="checkbox" id="maxAgeCb" name="maxAgeCb_name"/>&nbsp;Maximum age&nbsp;
			<div id="FPATmaxAgeDiv" style="display: inline;">
			<input name="FPATmaxAge" type="text" id="FPATmaxAgeID" value="" 
			data-validation-help="[integer number > 0]" data-validation="number" data-validation-allowing="positive,range[1;10000000]" data-validation-optional="false"/>
			</div><br/>
			<#if md.facetIdentifierList?? && md.facetIdentifierList?has_content>
				<input type="checkbox" id="FPATCb" name="FPATCb_name"/>&nbsp;Only patients with one or more of the following facets&nbsp;<br/>
				<div id="FPATCbCont" style="border: 1px black solid; margin-left: 25px;">
				<#list 0..md.facetIdentifierList?size-1 as i>
					<input type="checkbox" name="FPATclassification1" value="${md.facetIdentifierList[i]!'-'}">Label:&nbsp;<i>${md.facetIdentifierList[i]!'-'}</i><br/>
				</#list>
				</div>
			</#if>
			
			<div id="patientFilterInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				All enabled patient fitlers are applied in AND - only the patients that pass all the filters are considered to carry out the comorbidity analysis. 
				The approach to determine the age of a patient can be specified by the value of the 'Patient age computation approach' property.
			</div>
		</div>
		
		
		<div class="sendFormElemGroup">
			<div id="comorbidityFilterInfoD" style="display: inline; cursor: pointer;">
				<img src="img/info.png" width="16" height="16" />
			</div>
			<b>Comorbidity scores filters</b>:&nbsp;<br/>
			<input type="checkbox" id="FCOMscoreCb" name="FCOMscoreCb_name"/>&nbsp;Comorbidity score index threshold&nbsp;
			<div id="FCOMscoreDiv" style="display: inline;">
				<select name="FCOMscoreEG" type="text" id="FCOMscoreEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOMscore" type="text" id="FCOMscoreID" 
					data-validation-help="[float number]" value="" data-validation="number" data-validation-allowing="float" data-validation-optional="false"/>
			</div><br/>
			
			<input type="checkbox" id="FCOMrriskCb" name="FCOMrriskCb_name"/>&nbsp;Relative risk index threshold&nbsp;
			<div id="FCOMrriskDiv" style="display: inline;">
				<select name="FCOMrriskEG" type="text" id="FCOMrriskEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOMrrisk" type="text" id="FCOMrriskID" 
				data-validation-help="[float number]" value="" data-validation="number" data-validation-allowing="float" data-validation-optional="false"/>
			</div><br/>
			
			<input type="checkbox" id="FCOModdsRatioCb" name="FCOModdsRatioCb_name"/>&nbsp;Odds ratio threshold&nbsp;
			<div id="FCOModdsRatioDiv" style="display: inline;">
				<select name="FCOModdsRatioEG" type="text" id="FCOModdsRatioEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOModdsRatio" type="text" id="FCOModdsRatioID" value="" 
				data-validation-help="[float number]" value="" data-validation="number" data-validation-allowing="float" data-validation-optional="false"/>
			</div><br/>
				
			<input type="checkbox" id="FCOMphiCb" name="FCOMphiCb_name"/>&nbsp;Phi index threshold&nbsp;
			<div id="FCOMphiDiv" style="display: inline;">
				<select name="FCOMphiEG" type="text" id="FCOMphiEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOMphi" type="text" id="FCOMphiID" 
				data-validation-help="[float number]" value="" data-validation="number" data-validation-allowing="float" data-validation-optional="false"/>
			</div><br/>
			
			<input type="checkbox" id="FCOMfisherAdjCb" name="FCOMfisherAdjCb_name"/>&nbsp;Fisher test adjusted thrashold&nbsp;
			<div id="FCOMfisherAdjDiv" style="display: inline;">
				<select name="FCOMfisherAdjEG" type="text" id="FCOMfisherAdjEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOMfisherAdj" type="text" id="FCOMfisherAdjID" 
				data-validation-help="[float number]" value="" data-validation="number" data-validation-allowing="float" data-validation-optional="false"/>
			</div><br/>
			
			<input type="checkbox" id="FCOMminPatCb" name="FCOMminPatCb_name"/>&nbsp;Minimum number of patients threshold&nbsp;
			<div id="FCOMminPatDiv" style="display: inline;">
				<select name="FCOMminPatEG" type="text" id="FCOMminPatEGEGID" >
			 	  <option value="EQUAL_OR_GREATER_THAN">EQUAL_OR_GREATER_THAN</option>
				  <option value="EQUAL_OR_LOWER_THAN">EQUAL_OR_LOWER_THAN</option>
				</select>
				<input name="FCOMminPat" type="text" id="FCOMminPatID" 
				data-validation-help="[integer number > 0]" value="" data-validation="number" data-validation-allowing="positive,range[1;10000000]" data-validation-optional="false"/>
			</div><br/>
			
			
			<div id="comorbidityFilterInfoD_cont" class="contentExpanderForm" style="display: none;"> 
				Select the pairs of comorbidities to be considered in the output of the analysis by means of a set of filters. 
			    Enabled comorbidity filters are applied in AND, thus to be selected in the output a diagnosis pair should pass all the filters.
			</div>
		</div>
		
		<div class="buttonSubmit">
			<button id="submitButton" type="submit" class="buttonStyle">Start comorbidity analysis!</button>
		</div>
		</form>
	  
	  
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
