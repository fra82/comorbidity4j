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
	  	<div class="linkButtonBack-c4j"><a href="comorbidityParameters">Define new comorbidity parameters</a></div>&nbsp;
	  </div>
	  
	  <div class="navigation-c4j">
	  	<h2>8) Start comorbidity analysis</h2>
	  </div>
	  
	   <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  </@um.errorDiv>
	  </#if>
	  
	  <div class="navigation-c4j" style="font-size: 140%; margin-bottom: 10px; line-height: 2.0;">
	  Below you can find an overview of the data loaded for this comorbidity analysis.<br/>
	  After revising them, to start the comorbidity analysis, click on the following button:<br/>
	  </div>
	  
	  <div style="text-align:center; margin: auto; width: 400px; padding: 10px;">
      	<button id="startAnalysis" type="submit" class="buttonStyle" style="font-size: 130%;">Start comorbidity analysis!</button>
      </div>
	  
	  
	  <script type="text/javascript">
	   
	   $(document).ready(function() {
		   	$("#startAnalysis").click(function() {
				var win = window.open(window.location.href.replace("startAnalysis", "compute"), '_blank');
  				win.focus();
			});
		   	
	   });
	   
	  </script>
	  
	  <div class="summary-c4j">
	  	<h3>Global parameters</h3>
	  	<ul>
	       <li><b>Patient age computation approach (used if the patient age filter is enabled)</b>:&nbsp; ${md.patientAgeComputation!'---'}</li>
	       <li><b>P-value adjust approach</b>:&nbsp; ${md.pvalAdjApproach!'---'}</li>
	       <li><b>Relative risko confidence interval</b>:&nbsp; ${md.RRconfidenceInterval!'---'}</li>
	       <li><b>Odds ratio confidence interval</b>:&nbsp; ${md.ORconfidenceInterval!'---'}</li>
	       <#if md.isGenderEnabled == "true">
	  		 <li><b>Female identifier to compute sex ratio value</b>:&nbsp; ${md.sexRatioFemaleIdentifier!'---'}</li>
	         <li><b>Male identifier to compute sex ratio value</b>:&nbsp; ${md.sexRatioMaleIdentifier!'---'}</li>
	  	   <#else>
	  		 <li><b>No patient gender is considered in comorbidity analysis.</li>
	  	   </#if>
	       <li><b>Patient filter</b>:&nbsp; ${md.patient_filter!'---'}</li>
	       <li><b>Time directionality filter</b>:&nbsp; ${md.directionality_filter!'---'}</li>
	       <li><b>Scores filter</b>:&nbsp; ${md.score_filter!'---'}</li>
	    </ul>
	  </div>
	  
	  <div class="summary-c4j">
	  	<h3>Patient Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName_PD!'-'}&nbsp;(size:&nbsp;${md.fileSize_PD!'-'}&nbsp;Mb approx.)</li>
	  		
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
	  
	  <div class="summary-c4j">
	  	<h3>Visit Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName_VD!'-'}&nbsp;(size:&nbsp;${md.fileSize_VD!'-'}&nbsp;Mb approx.)</li>
	  		
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
	  
	  <div class="summary-c4j">
	  	<h3>Diagnosis Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName_DD!'-'}&nbsp;(size:&nbsp;${md.fileSize_DD!'-'}&nbsp;Mb approx.)</li>
	  		
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
	 
	  <div class="summary-c4j">
	  	<h3>Diagnosis Description Data file</h3>
	  	<ul>
	  		<li><b>File name</b>:&nbsp;${md.fileName_DDE!'-'}&nbsp;(size:&nbsp;${md.fileSize_DDE!'-'}&nbsp;Mb approx.)</li>
	  		
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
