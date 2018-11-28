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
	  </div>
	  
	  <div class="navigation-c4j">
	  	<h2>5) Diagnosis grouping</h2>
	  </div>
	  
	  <div class="navigation-c4j">
	  	<span id="diagnosisGroupingSkipButton" class="linkButtonBack-c4j" style="color:red; background-color:white;">
	  	OPTIONAL STEP: click here to skip this step and proceed with data loading.</span>&nbsp;
	  </div>
	  
	  <@dm.diagnosisGroupingDescription/>
	  
	  <#if md.errorMessage??>
		  <@um.errorDiv>
		  ${md.errorMessage}
		  </@um.errorDiv>
	  </#if>
	  
	  
	  <!-- DIAGNOSIS GROUP SELECTION - START -->
	  <!-- Selectize.js - https://github.com/selectize/selectize.js -->
	  <link href="${md.baseCSS_JSpath!''}css/selectize.css?v=${md.CSSJScount!'---'}" rel="stylesheet">
	  <script type="text/javascript" src="${md.baseCSS_JSpath!''}js/selectize.min.js?v=${md.CSSJScount!'---'}"></script>
	  
	  <div id="dialog" title="Basic dialog">
	  			Please, specify the group name and the diagnoses that belong to the group:<br/>
	  			
	  			<div id="selectedSel" class="diagSelectorGroup-c4j" style="background-color:white;margin-top: 10px;">
					<b>Diagnosis group name</b>: <input id="groupName" type="input" value=""/>&nbsp;
				</div>
				
				<div id="selectedSel" class="diagSelectorGroup-c4j" style="background-color:white;margin-top: 10px;">
					<b>Your group currently contains <span id="diagnCount">0</span> diagnosis over <span id="diagnTotal">0</span> diagnses available</b>:&nbsp;<input id="iteractiveClearAllSelected" type="button" value="Remove all diagnoses"/>&nbsp;
					<div id="selectedSelDiv" style="margin-top: 5px;">
					<select id="selected-to" placeholder="No diagnosis selected."></select>
					</div>
				</div>
				
				
				<div id="regExpInfoExampleD_cont" class="contentExpanderForm" style="display: none;"> 
					Examples of regular expressions to search for all diagnoses matching them in code or description:
					<ul>
						<li><b>Match string begin</b>:&nbsp; <i>$</i></li>
						<li><b>Match string end</b>:&nbsp; <i>^</i></li>
						<li><b>Case insensitive</b>:&nbsp;start the regular expression with <i>i/</i></li>
					</ul>
					For instance:
					<ul>
						<li><b>i/$Disease of</b>&nbsp; is a case insensitive match of all the strings that starts with 'Disease of'</li>
						<li><b>.*disease.*</b>&nbsp; is a case sensitive match of all the strings that include the word 'diseases'</li>
					</ul>
				</div>
				
	  			<div id="selectorDG" style="background-color:#DEEAEE; padding:5px; border:1px black solid;">
		  			Add one or more diagnoses to your group by relying on the following approaches:<br/>
		  			<div id="codeRegexpSel" class="diagSelectorGroup-c4j">
		  				<div id="regExpInfoExampleD" style="display: inline; cursor: pointer;">
							<img src="img/info.png" width="16" height="16" />
						</div>
		  				<b>Add to the group all the diagnosis with code matching the following regexp</b>&nbsp; <input id="codeRegexp" type="text"/>&nbsp;
		  				<input id="codeRegexpCb" type="button" value="Add!"/>&nbsp;
		  			</div>
		  			<div id="descrRegexpSel" class="diagSelectorGroup-c4j">
		  				<div id="regExpInfoExampleD1" style="display: inline; cursor: pointer;">
							<img src="img/info.png" width="16" height="16" />
						</div>
		  				<b>Add to the group all the diagnosis with description matching the following regexp</b>&nbsp; <input id="descrRegexp" type="text"/>&nbsp;
		  				<input id="descrRegexpCb" type="button" value="Add!"/>&nbsp;
		  			</div>
		  			<!--
			  		<div id="interactiveSel" class="diagSelectorGroup-c4j">
						<b>Add to the group all the diagnosis belongining to the following search and suggest list</b>&nbsp;
						<div id="interactiveSelDiv">
						<select id="select-to" placeholder="Pick some diagnosis..."></select>
						</div>
						<input id="iteractiveClearSelection" type="button" value="Clear selection"/>&nbsp;
						<input id="interactiveCb" type="button" value="Add!"/>&nbsp;
					</div>
					-->
				</div>
				
				
	  </div>
	  
	  <div id="infoDialog" title="">
	  </div>
	  
	  <script type="text/javascript">
	   var diagnosisGroups = {}
	   var diagnosisInGroups = []
	  
	   var diagnosisList = ${md.diagnosisList!'[]'};
				
	   var diagnosisListIndex = ${md.diagnosisListIndex!'[]'};
	  
	   var selectRef = null;
	   var selectedRef = null;
	   
	   var diagnosisGroupCounter = 0;
	   
	   var infoDialogRef = null;
	   
	   $(document).ready(function() {
	   		
	   		$("#diagnTotal").html(diagnosisList.length);
	   		
	   		$("#addGroup").click(function() {
			  	$("#dialog").dialog("open");
			  	var w = window.innerWidth;
				var h = window.innerHeight;
			  	$("#dialog").dialog("option", "width", ((w > 100) ? w - 50 : w - 1));
			  	$("#dialog").dialog("option", "height", ((h > 100) ? h - 50 : h - 1));
			});
	   		
	   		$("#codeRegexpCb").click(function() {
			    var inputVal = $("#codeRegexp").val();
			    if(inputVal != null && inputVal.length > 0) {
			    	var diagnosisCodesToAdd = [];
			    	
					try {			    
				    	var re = null; 
						if(inputVal.startsWith("i/")) {
							re = new RegExp(inputVal.substring(2), "i");
						}
						else {
							re = new RegExp(inputVal);
						}
						
				    	for (var i = 0; i < diagnosisList.length; i++){
				    		diagnosisElem = diagnosisList[i];
				    		var res = diagnosisElem.code.match(re);
				    		if(res != null && res.length > 0) {
				    			diagnosisCodesToAdd.push(diagnosisElem.code);
				    		}
				    	}
			    	} catch(err) {
			    		
			    	}
			    	
			    	if(diagnosisCodesToAdd.length > 250) {
			    		$("#alertDialog").html("ATTENTION: your regular expression matches " + diagnosisCodesToAdd.length + " diagnosis codes. " + 
			    		"Adding diagnoses by regular expressions is allowed up to a maximum of 250 matches per regular expression. Please, try to reformulate your regular expression to meet this constraint.");
			    		$("#alertDialog").dialog("open");
			    	}
			    	else if(diagnosisCodesToAdd.length == 0) {
			    		$("#alertDialog").html("No matches for the regular expression: " + inputVal);
			    	    $("#alertDialog").dialog("open");
			    	}
			    	else {
			    		for(var k = 0; k < diagnosisCodesToAdd.length; k++) {
			    			selectedRef[0].selectize.addItem(diagnosisCodesToAdd[k], false);
			    		}
			    	}
			    }
			    else {
			    	$("#alertDialog").html("Empty string - specify a valid regular expression.");
			    	$("#alertDialog").dialog("open");
			    }
			    
			    $("#diagnCount").html(selectedRef[0].selectize.getValue().length);
			});
			
	   		$("#descrRegexpCb").click(function() {
			    var inputVal = $("#descrRegexp").val();
			    
			    if(inputVal != null && inputVal.length > 0) {
			    	var diagnosisCodesToAdd = [];
					try {
						var re = null; 
						if(inputVal.startsWith("i/")) {
							re = new RegExp(inputVal.substring(2), "i");
						}
						else {
							re = new RegExp(inputVal);
						}
						
				    	for (var i = 0; i < diagnosisList.length; i++){
				    		diagnosisElem = diagnosisList[i];
				    		var res = diagnosisElem.description.match(re);
				    		if(res != null && res.length > 0) {
				    			diagnosisCodesToAdd.push(diagnosisElem.code);
				    		}
				    	}
			    	} catch(err) {
			    		
			    	}
			    	
			    	if(diagnosisCodesToAdd.length > 250) {
			    		$("#alertDialog").html("ATTENTION: your regular expression matches " + diagnosisCodesToAdd.length + " diagnosis codes. " + 
			    		"Adding diagnoses by regular expressions is allowed up to a maximum of 250 matches per regular expression. Please, try to reformulate your regular expression to meet this constraint.");
			    		$("#alertDialog").dialog("open");
			    	}
			    	else if(diagnosisCodesToAdd.length == 0) {
			    		$("#alertDialog").html("No matches for the regular expression: " + inputVal);
			    	    $("#alertDialog").dialog("open");
			    	}
			    	else {
			    		for(var k = 0; k < diagnosisCodesToAdd.length; k++) {
			    			selectedRef[0].selectize.addItem(diagnosisCodesToAdd[k], false);
			    		}
			    	}
			    	
			    }
			    else {
			    	$("#alertDialog").html("Empty string - specify a valid regular expression.");
			    	$("#alertDialog").dialog("open");
			    }
			    
			    $("#diagnCount").html(selectedRef[0].selectize.getValue().length);
			});
			
			$("#interactiveCb").click(function() {
			    var itemsToAdd = selectRef[0].selectize.getValue();
			    var itemsAdded = selectedRef[0].selectize.getValue();
			    
			    if(itemsToAdd && itemsToAdd.length > 0) {
					for (var i = 0; i < itemsToAdd.length; i++){
			    		item = itemsToAdd[i];
			    		if(!itemsAdded.includes(item)) {
			    			selectedRef[0].selectize.addItem(item, false);
			    		}
			    	}
			    	selectedRef[0].selectize.refreshItems();
			    }
			    
			    $("#diagnCount").html(selectedRef[0].selectize.getValue().length);
			});
			
			$("#iteractiveClearSelection").click(function() {
			    selectRef[0].selectize.clear(true);
			});
	   		
	   		$("#iteractiveClearAllSelected").click(function() {
			    selectedRef[0].selectize.clear(true);
			    $("#diagnCount").html(selectedRef[0].selectize.getValue().length);
			});
	   		
	   		/*
	   		selectRef = $('#select-to').selectize({
				persist: false,
				openOnFocus: false,
				maxItems: null,
				maxOptions: 10,
				valueField: 'code',
				labelField: 'description',
				searchField: ['code', 'description'],
				sortField: [
					{field: 'description', direction: 'asc'},
					{field: 'code', direction: 'asc'}
				],
				options: diagnosisList,
				render: {
					item: function(item, escape) {
						return '<div>' +
							(item.description ? '<span class="description" style="font-weight: bold;">' + escape((item.description.length > 20) ? item.description.substring(0, 19) + "..." : item.description) + '</span>' : '') + '&nbsp;' +
							(item.code ? '<span class="code">(\'' + escape(item.code) + '\')</span>' : '') +
						'</div>';
					},
					option: function(item, escape) {
						return '<div>' +
							(item.description ? '<span class="description" style="font-weight: bold;">' + escape((item.description.length > 20) ? item.description.substring(0, 19) + "..." : item.description) + '</span>' : '') + '&nbsp;' +
							(item.code ? '<span class="code">(\'' + escape(item.code) + '\')</span>' : '') +
						'</div>';
					}
				}
			});
			*/
			
			selectedRef = $('#selected-to').selectize({
				persist: false,
				openOnFocus: false,
				maxItems: null,
				maxOptions: 10,
				valueField: 'code',
				labelField: 'description',
				searchField: ['code', 'description'],
				sortField: [
					{field: 'description', direction: 'asc'},
					{field: 'code', direction: 'asc'}
				],
				options: diagnosisList,
				onItemAdd: function(name) { 
					$("#diagnCount").html(selectedRef[0].selectize.getValue().length);
				},
				onItemRemove: function(name) { 
					$("#diagnCount").html(selectedRef[0].selectize.getValue().length);
				},
				render: {
					item: function(item, escape) {
						return '<div>' +
							(item.description ? '<span class="description" style="font-weight: bold;">' + escape((item.description.length > 20) ? item.description.substring(0, 19) + "..." : item.description) + '</span>' : '') + '&nbsp;' +
							(item.code ? '<span class="code">(\'' + escape(item.code) + '\')</span>' : '') +
						'</div>';
					},
					option: function(item, escape) {
						return '<div>' +
							(item.description ? '<span class="description" style="font-weight: bold;">' + escape((item.description.length > 20) ? item.description.substring(0, 19) + "..." : item.description) + '</span>' : '') + '&nbsp;' +
							(item.code ? '<span class="code">(\'' + escape(item.code) + '\')</span>' : '') +
						'</div>';
					}
				}
			});
			
			$("#dialog").dialog({
		      resizable: true,
	      	  autoOpen: false,
		      height: "auto",
		      width: "auto",
		      modal: true,
		      title: "Selection of a group of diagnoses",
		      buttons: {
		        "Create group with selected diagnoses": function() {
		        	
		        	// Check group name
		        	var groupNameVal = $("#groupName").val();
		        	
		        	if(groupNameVal == null || groupNameVal.trim() == "") {
			        	$("#alertDialog").html("Specify a name for the group, please.");
				    	$("#alertDialog").dialog("open");
		        		return;
		        	}
		        	
		        	var groupNamePresent = false;
		        	for(var key in diagnosisGroups) {
					    if(diagnosisGroups.hasOwnProperty(key)) {
					        // console.log(key + " -> " + groupNameVal[key]);
					        if(key.trim() == groupNameVal) {
					        	groupNamePresent = true;
					        }
					    }
					}
		        	
		        	if(groupNamePresent) {
			        	$("#alertDialog").html("A group with this name has been already defined. Please specify another group name.");
				    	$("#alertDialog").dialog("open");
		        		return;
		        	}
		        	
		        	// Populate new group
		        	
		        	var newDiagnosisGroup = [];
		        	
		        	var itemsAdded = selectedRef[0].selectize.getValue();
		        	for (var i = 0; i < itemsAdded.length; i++) {
		        		var itemA = itemsAdded[i];
		        		if(!diagnosisInGroups.includes(itemA)) {
		        			newDiagnosisGroup.push(itemA);
		        			diagnosisInGroups.push(itemA);
		        		}
		        	}
		        	
		        	if(newDiagnosisGroup == null || newDiagnosisGroup.length == 0) {
			        	$("#alertDialog").html("Please, select at least one diagnosis to add.");
				    	$("#alertDialog").dialog("open");
		        		return;
		        	}
		        	
		        	if(newDiagnosisGroup != null && newDiagnosisGroup.length > 0) {
		        		diagnosisGroupCounter = diagnosisGroupCounter + 1
		        		diagnosisGroups["group_" + diagnosisGroupCounter] = {};
		        		diagnosisGroups["group_" + diagnosisGroupCounter].name = groupNameVal.trim();
		        		diagnosisGroups["group_" + diagnosisGroupCounter].codes = newDiagnosisGroup;
		        		
		        		// Remove from selectable
		        		var diagElemToRemoveFromSelectable = []
		        		for (var i = 0; i < diagnosisInGroups.length; i++) {
		        			diagnosisInGr = diagnosisInGroups[i];
		        			for (var k = 0; k < diagnosisList.length; k++) {
		        				if(diagnosisList[k].code.trim() == diagnosisInGr.trim()) {
							        diagElemToRemoveFromSelectable.push(k);
							        // selectRef[0].selectize.removeOption(diagnosisList[k].code, true);
							        selectedRef[0].selectize.removeOption(diagnosisList[k].code, true);
							    }
							}
		        		}
		        		
		        		// selectRef[0].selectize.updateOption("options", diagnosisList);
		        		// selectRef[0].selectize.clear(true);
		        		// selectRef[0].selectize.refreshItems();
		        		
		        		selectedRef[0].selectize.updateOption("options", diagnosisList);
		        		selectedRef[0].selectize.clear(true);
		        		selectedRef[0].selectize.refreshItems();
		        		
		        		$("#groupName").val("");
		        		$("#diagnCount").html(0);
		        		$("#diagnTotal").html(diagnosisList.length - diagnosisInGroups.length);
		        		
		        		$("#diagGroupDefinedCount").html(Object.keys(diagnosisGroups).length + "");
		        		
		        		// Update list of groups
		        		$("#groupList").html("");
		        		var groupCount = 0;
		        		for(var groupName in diagnosisGroups) {
						    if(diagnosisGroups.hasOwnProperty(groupName)) {
						        // console.log(groupName + " -> " + diagnosisGroups[groupName]);
						        
						        var stringDiagList = "";
						        for(var i = 0; i < diagnosisGroups[groupName].codes.length; i++) {
						        	stringDiagList = stringDiagList + ((stringDiagList.length > 0) ? ", " : "") + diagnosisGroups[groupName].codes[i];
						        	if(stringDiagList.length > 100) {
						        		stringDiagList = stringDiagList + "...";
						        		break;
						        	}
						        }
						        
						        groupCount = groupCount + 1;
						        var groupListHTML = "<div id='DIV_" + groupName + "' style='border: 1px black solid; padding:3px; margin: 3px; background-color: #FFF7AA; width: 100%;'>" + 
						        groupCount + ")&nbsp;<b>Group name</b>&nbsp;<i>" + diagnosisGroups[groupName].name + "&nbsp;(" + groupName + ")</i>&nbsp;<b> containing " + diagnosisGroups[groupName].codes.length + " diagnoses</b>:&nbsp;" + 
						        stringDiagList + "</div>";
						        var groupListHTMLel = $(groupListHTML);
						        
						        var showDiagnosesHTML = "<div class='buttonDiv-c4j' data-gname='" + groupName + "' >Show list of diagnoses</div>";;
						        var showDiagnosesHTMLel = $(showDiagnosesHTML);
						        
						        var deleteHTML = "<div class='buttonDiv-c4j' data-gname='" + groupName + "' >Delete group</div>";
						        var deleteHTMLel = $(deleteHTML);
						        
						        var buttonGroupHTML = "<div style='padding: 3px;' data-gname='" + groupName + "' ></div>";
						        var buttonGroupHTMLel = $(buttonGroupHTML);
						        buttonGroupHTMLel.append(showDiagnosesHTMLel);
								buttonGroupHTMLel.append(deleteHTMLel);
								
								
						        groupListHTMLel.append(buttonGroupHTMLel);
								$("#groupList").append(groupListHTMLel);
								
								deleteHTMLel.click(function(e){
									var gName = $(e.currentTarget).data('gname');
								
									delete diagnosisGroups[gName];
	   								
	   								diagnosisInGroups = [];
	   								for(var gn in diagnosisGroups) {
									    if(diagnosisGroups.hasOwnProperty(gn)) {
									        // console.log(groupName + " -> " + diagnosisGroups[gn]);
									        for(var i = 0; i < diagnosisGroups[gn].codes.length; i++) {
									        	diagnosisInGroups.push(diagnosisGroups[gn].codes[i]);
									        }
									    }
						        	}
						        	
						        	for(var i = 0; i < diagnosisList.length; i++) {
						        		if(!diagnosisInGroups.includes(diagnosisList[i].code)) {
						        			// selectRef[0].selectize.addOption(diagnosisList[i], true);
							        		selectedRef[0].selectize.addOption(diagnosisList[i], true);
						        		}
						        	}
						        	// selectRef[0].selectize.refreshItems();
						        	selectedRef[0].selectize.refreshItems();
						        	
								 	$("#diagGroupDefinedCount").html(Object.keys(diagnosisGroups).length + "");
								 	$("#DIV_" + gName).remove();
								 	
								 	updateUploadGroups();
								});
								
								showDiagnosesHTMLel.click(function(e){
									$("#alertDialog").html("");
									
									var gName = $(e.currentTarget).data('gname');
	   								
	   								var fullListHTML = "<div>List of diagnoses of group with name <i>" + diagnosisGroups[gName].name + " (" + gName + ")</i>: <ul>";
	   								for(var i = 0; i < diagnosisGroups[gName].codes.length; i++) {
	   									var descr = "<b>" + diagnosisGroups[gName].codes[i] + "</b>";
	   									if(diagnosisListIndex.hasOwnProperty(diagnosisGroups[gName].codes[i])) {
	   										descr = descr + ":&nbsp;" + diagnosisListIndex[diagnosisGroups[gName].codes[i]];
	   									}
	   									
	   									fullListHTML = fullListHTML + "<li>" + descr + "</li>";
	   								}
	   								fullListHTML = fullListHTML + "</ul></div>";
	   								
						        	var fullListHTMLel = $(fullListHTML);
						        	
	   								$("#alertDialog").html(fullListHTMLel);
				    				$("#alertDialog").dialog("open");
								});		       						        
						    }
						}
						
		        	}
		        	else {
		        		$("#alertDialog").html("No diagnosis added to the group.");
				    	$("#alertDialog").dialog("open");
		        	}
		        	
		        	updateUploadGroups();
		          	$(this).dialog("close");
		        },
		        Cancel: function() {
		          updateUploadGroups();
		          $(this).dialog("close");
		        }
		      }
		    });
		    
		    $("#alertDialog").dialog({
			      resizable: true,
		      	  autoOpen: false,
			      height: "auto",
			      width: "auto",
			      modal: true,
			      closeOnEscape: true,
			      buttons: {
			        "Ok": function() {
			          $(this).html("");
			          $(this).dialog("close");
			        }
			      }
			 });
			 
			 $("#confirmDialog").dialog({
			      resizable: true,
		      	  autoOpen: false,
			      height: "auto",
			      width: "auto",
			      modal: true,
			      closeOnEscape: true,
			      title: 'No diagnosis groups have been specified',
			      buttons: {
			        "Proceed": function() {
			          $(this).html("");
			          window.location = "diagnosisPairing";
			          $(this).dialog("close");
			        },
			        "Cancel": function() {
			          $(this).html("");
			          $(this).dialog("close");
			        }
			      }
			 });
			 
			 $("#uploadGroups").click(function() {		
			    
			    if(diagnosisGroups == null || Object.keys(diagnosisGroups).length == 0) {
			    	$("#confirmDialog").html("Click 'Proceed' to go on anyway, otherwise click 'Cancel' to define one or more diangosis groups.");
				    $("#confirmDialog").dialog("open");
				    return;
			    }
			 	 		 	
			 	var form = $('<form method="post"><input type="submit" /><input type="hidden" name="jsonGroup" /></form>').attr('action', 'diagnosisPairing');
			    $('input[name="jsonGroup"]', form[0]).val(JSON.stringify(diagnosisGroups));
			    document.body.appendChild(form[0]);
			    $('input[type="submit"]', form).click();
			 });
			 
			 $("#resetGroups").click(function() {			 	
			 	window.location = "diagnosisGrouping";
			 });
			 
			 $("#diagnosisGroupingSkipButton").click(function() {
			 	$("#uploadGroups").click();
		 	 });
		 	 
		 	 $("#uploadGroups").hide();
			 
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
			 
			 $("#regExpInfoExampleD").click(function() {
				openInfoDialog("INFO: Regular expressions examples", "regExpInfoExampleD_cont");
			 });
			 
			 $("#regExpInfoExampleD1").click(function() {
				openInfoDialog("INFO: Regular expressions examples", "regExpInfoExampleD_cont");
			 });
			
		});
		
		function openInfoDialog(title, HTMLcontentID) {
			$("#infoDialog").dialog('option', 'title', title);
			var clone = $("#" + HTMLcontentID).clone(true);
			infoDialogRef.html("");
			infoDialogRef.html(clone.html());
			$("#infoDialog").dialog("open");
		} 
		
		
		function updateUploadGroups() {
		  $("#uploadGroups").hide();
		  
		  if(diagnosisGroups != null && Object.keys(diagnosisGroups).length > 0) {
		  		$("#uploadGroups").show();
		  }
		}
		
		function escapeHtml(text) {
		  var map = {
		    '&': '&amp;',
		    '<': '&lt;',
		    '>': '&gt;',
		    '"': '&quot;',
		    "'": '&#039;'
		  };
		
		  return text.replace(/[&<>"']/g, function(m) { return map[m]; });
		}
	  </script>
	  <!-- DIAGNOSIS GROUP SELECTION - END -->
	  
	  <div id="alertDialog" title="">
	  </div>
	  
	  <div id="confirmDialog" title="No diagnosis groups have been specified">
	  </div>
	  
	  <h3>Diagnosis groups defined: <span id="diagGroupDefinedCount">0</span></h3>
	  <div id="groupListTitleLower">
	  <button id="addGroup" class="buttonStyle" style="background-color: #FFC97F;">Define a new diagnosis group</button>&nbsp;
	  <button id="resetGroups" class="buttonStyle" style="background-color: #FFC97F;">Empty diagnosis group list</button>&nbsp;
	  <button id="uploadGroups" class="buttonStyle">Upload diagnosis group(s) from the list below</button>&nbsp;
	  </div>
	  <div id="groupList" style="background-color: #D3D7CF; padding: 5px; margin: 5px;">
	  	
	  </div>
		
	</div>
</@um.pageContent>
	    

<@um.pageFooter/>
