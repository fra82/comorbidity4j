
<script>

/* HEATMAP DATA */
var heatmapLayout = {
  title: 'Annotated Heatmap',
  annotations: [],
  xaxis: {
    ticks: '',
    side: 'bottom'
  },
  yaxis: {
    ticks: '',
    ticksuffix: ' ',
    width: 700,
    height: 700,
    autosize: true
  }
};

<#if isGenderEnabled == "true">
var heatmapLayoutSR = {
  title: 'Sex Ratio Heatmap',
  annotations: [],
  xaxis: {
    ticks: '',
    side: 'bottom'
  },
  yaxis: {
    ticks: '',
    ticksuffix: ' ',
    width: 700,
    height: 700,
    autosize: true
  }
};
</#if>

var RRconfidenceInterval = "${RRconfidenceInterval!'-'}";
var ORconfidenceInterval = "${ORconfidenceInterval!'-'}";

/* TABLE DATA */
var columnList = [ //Define Table Columns
    {title:"DisAcode", field:"disAcode", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Identifier of Disease A"},
	{title:"DisAname", field:"disAname", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Extended name of Disease A"},
	{title:"DisBcode", field:"disBcode", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Identifier of Disease A"},
	{title:"DisBname", field:"disBname", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Extended name of Disease B"},
	{title:"Tot. Pts", field:"patTotal", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients"},
	{title:"DisA Pts", field:"patWdisA", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease A"},
	{title:"DisB Pts", field:"patWdisB", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease B"},
	{title:"DisAB Pts", field:"patWdisAB", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with both Disease A and Disease B"},
	{title:"AnotB Pts", field:"patWdisAnotB", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease A and NOT Disease B"},
	{title:"BnotA Pts", field:"patWdisBnotA", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease B and NOT Disease A"},
	{title:"notAB Pts", field:"patWOdisAB", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with NOT Disease A and NOT Disease B"},
	{title:"Rel Risk", field:"relativeRiskIndex", width:100, align:"left", sorter:"number", headerTooltip:"Relative risk"},
	{title:"RR " + RRconfidenceInterval + " CI lower", field:"relativeRiskCI_lower", width:100, align:"left", sorter:"number", headerTooltip: RRconfidenceInterval + " Confidence Interval of Relative Risk - Lower bound"},
	{title:"RR " + RRconfidenceInterval + " CI upper", field:"relativeRiskCI_upper", width:100, align:"left", sorter:"number", headerTooltip: RRconfidenceInterval + " Confidence Interval of Relative Risk - Upper bound"},
	{title:"Phi", field:"phiIndex", width:100, align:"left", sorter:"number", headerTooltip:"Phi"},
	{title:"Odds Ratio", field:"oddsRatioIndex", width:100, align:"left", sorter:"number", headerTooltip:"Odds ration (OR)"},
	{title:"OR " + ORconfidenceInterval + " CI lower", field:"oddsRatioCI_lower", width:100, align:"left", sorter:"number", headerTooltip: ORconfidenceInterval + " Confidence Interval of Odds Ratio - Lower bound"},
	{title:"OR " + ORconfidenceInterval + " CI upper", field:"oddsRatioCI_upper", width:100, align:"left", sorter:"number", headerTooltip: ORconfidenceInterval + " Confidence Interval of Odds Ratio - Upper bound"},
	{title:"P-value", field:"fisherTest", width:100, align:"left", sorter:"number", headerTooltip:"Fisher test p-value"},
	{title:"${executorObj.pvalAdjApproach!'---'} adj. p-value", field:"fisherTestAdjusted", width:100, align:"left", sorter:"number", headerTooltip:"Adjusted p-value with approach: ${executorObj.pvalAdjApproach!'---'}"},
	{title:"Expect", field:"expect", width:100, align:"left", sorter:"number", headerTooltip:"Expectation"},
	{title:"Comor. Score", field:"score", width:100, align:"left", sorter:"number", headerTooltip:"Comorbidity score"}
	// {title:"femWdisA", field:"femaleWithDisA", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease A - sex ratio"},
	// {title:"femWdisB", field:"femaleWithDisB", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease B - sex ratio"},
	// {title:"femWdisAandB", field:"femaleWithDisAandB", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease A and B - sex ratio"},
	// {title:"mWdisA", field:"maleWithDisA", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease A - sex ratio"},
	// {title:"mWdisB", field:"maleWithDisB", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease B - sex ratio"},
	// {title:"mWdisAandB", field:"maleWithDisAandB", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease A and B - sex ratio"},
	<#if isGenderEnabled == "true">,{title:"BA Sex Ratio", field:"sexRatioBA", width:100, align:"left", sorter:"number", headerTooltip:"Sex ratio BA of comorbidity pair: given all individuals with disease A: (i) > 0 means prevalence of disease B in females; (ii) < 0 means prevalence of disease B in males; (iii) close to 0 means disease B is equally likely for females and males."},
	{title:"AB Sex Ratio", field:"sexRatioAB", width:100, align:"left", sorter:"number", headerTooltip:"Sex ratio AB of comorbidity pair: given all individuals with disease B: (i) > 0 means prevalence of disease A in females; (ii) < 0 means prevalence of disease A in males; (iii) close to 0 means disease A is equally likely for females and males."}</#if>
];


<#if isGenderEnabled == "true">
var columnListSR = [ //Define Table Columns
    {title:"DisAcode", field:"disAcode", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Identifier of Disease A"},
	{title:"DisAname", field:"disAname", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Extended name of Disease A"},
	{title:"DisBcode", field:"disBcode", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Identifier of Disease A"},
	{title:"DisBname", field:"disBname", width:100, align:"left", sorter:"string", headerFilter:"input", headerTooltip:"Extended name of Disease B"},
	{title:"Tot. Pts", field:"patTotal", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients"},
	{title:"DisA Pts", field:"patWdisA", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease A"},
	{title:"DisB Pts", field:"patWdisB", width:100, align:"left", sorter:"number", headerTooltip:"Total number of patients with Disease B"},
	{title:"femWdisA", field:"femaleWithDisA", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease A - sex ratio"},
	{title:"femWdisB", field:"femaleWithDisB", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease B - sex ratio"},
	{title:"femWdisAandB", field:"femaleWithDisAandB", width:100, align:"left", sorter:"number", headerTooltip:"Females (${executorObj.sexRatioFemaleIdentifier!'-'}) with disease A and B - sex ratio"},
	{title:"mWdisA", field:"maleWithDisA", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease A - sex ratio"},
	{title:"mWdisB", field:"maleWithDisB", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease B - sex ratio"},
	{title:"mWdisAandB", field:"maleWithDisAandB", width:100, align:"left", sorter:"number", headerTooltip:"Males (${executorObj.sexRatioMaleIdentifier!'-'}) with disease A and B - sex ratio"}
	<#if isGenderEnabled == "true">,{title:"BA Sex Ratio", field:"sexRatioBA", width:100, align:"left", sorter:"number", headerTooltip:"Sex ratio of comorbidity pair (> 0 prevalence in females of disease B, given all individuals with disease A - ${executorObj.sexRatioFemaleIdentifier!'-'})"},
	{title:"AB Sex Ratio", field:"sexRatioAB", width:100, align:"left", sorter:"number", headerTooltip:"Sex ratio of comorbidity pair (> 0 prevalence in females of disease A, given all individuals with disease B - ${executorObj.sexRatioFemaleIdentifier!'-'})"}</#if>
];
</#if>


var tableData_ALL = ${JSONobj_ALL}

var tableData_MALE = ${JSONobj_MALE}

var tableData_FEMALE = ${JSONobj_FEMALE}

var tableData = [];


var diseaseData = ${JSONobj_DiseaseNameAutocomplete}


var malePatNumber = ${malePatNumber};
var femalePatNumber = ${femalePatNumber};


/* FILTER DATA */
var FDRCutoffInit_ALL = ${initFDR_ALL};
var FDRadjCutoffInit_ALL = ${initFDRadj_ALL};
var RRCutoffInit_ALL = ${initRrisk_ALL};
var PhiCutoffInit_ALL = ${initPhi_ALL};
var CSCutoffInit_ALL = ${initCscore_ALL};
var OddsRatioCutoffInit_ALL = ${initOddsRatio_ALL};
var NpatCutoffInit_ALL = ${initNumPatients_ALL};
var minFDRSelect_ALL = ${minFDRSelect_ALL};
var maxFDRSelect_ALL = ${maxFDRSelect_ALL};
var stepFDR_ALL = ${stepFDR_ALL};
var minFDRadjSelect_ALL = ${minFDRadjSelect_ALL};
var maxFDRadjSelect_ALL = ${maxFDRadjSelect_ALL};
var stepFDRadj_ALL = ${stepFDRadj_ALL};
var minRriskSelect_ALL = ${minRriskSelect_ALL};
var maxRriskSelect_ALL = ${maxRriskSelect_ALL};
var stepRrisk_ALL = ${stepRrisk_ALL};
var minPhiSelect_ALL = ${minPhiSelect_ALL};
var maxPhiSelect_ALL = ${maxPhiSelect_ALL};
var stepPhi_ALL = ${stepPhi_ALL};
var minCscoreSelect_ALL = ${minCscoreSelect_ALL};
var maxCscoreSelect_ALL = ${maxCscoreSelect_ALL};
var stepCscore_ALL = ${stepCscore_ALL};
var minOddsRatioSelect_ALL = ${minOddsRatioSelect_ALL};
var maxOddsRatioSelect_ALL = ${maxOddsRatioSelect_ALL};
var stepOddsRatio_ALL = ${stepOddsRatio_ALL};
var minNumPatientsSelect_ALL = ${minNumPatientsSelect_ALL};
var maxNumPatientsSelect_ALL = ${maxNumPatientsSelect_ALL};
var stepNumPatients_ALL = ${stepNumPatients_ALL};


var FDRCutoffInit_FEMALE = ${initFDR_FEMALE};
var FDRadjCutoffInit_FEMALE = ${initFDRadj_FEMALE};
var RRCutoffInit_FEMALE = ${initRrisk_FEMALE};
var PhiCutoffInit_FEMALE = ${initPhi_FEMALE};
var CSCutoffInit_FEMALE = ${initCscore_FEMALE};
var OddsRatioCutoffInit_FEMALE = ${initOddsRatio_FEMALE};
var NpatCutoffInit_FEMALE = ${initNumPatients_FEMALE};
var minFDRSelect_FEMALE = ${minFDRSelect_FEMALE};
var maxFDRSelect_FEMALE = ${maxFDRSelect_FEMALE};
var stepFDR_FEMALE = ${stepFDR_FEMALE};
var minFDRadjSelect_FEMALE = ${minFDRadjSelect_FEMALE};
var maxFDRadjSelect_FEMALE = ${maxFDRadjSelect_FEMALE};
var stepFDRadj_FEMALE = ${stepFDRadj_FEMALE};
var minRriskSelect_FEMALE = ${minRriskSelect_FEMALE};
var maxRriskSelect_FEMALE = ${maxRriskSelect_FEMALE};
var stepRrisk_FEMALE = ${stepRrisk_FEMALE};
var minPhiSelect_FEMALE = ${minPhiSelect_FEMALE};
var maxPhiSelect_FEMALE = ${maxPhiSelect_FEMALE};
var stepPhi_FEMALE = ${stepPhi_FEMALE};
var minCscoreSelect_FEMALE = ${minCscoreSelect_FEMALE};
var maxCscoreSelect_FEMALE = ${maxCscoreSelect_FEMALE};
var stepCscore_FEMALE = ${stepCscore_FEMALE};
var minOddsRatioSelect_FEMALE = ${minOddsRatioSelect_FEMALE};
var maxOddsRatioSelect_FEMALE = ${maxOddsRatioSelect_FEMALE};
var stepOddsRatio_FEMALE = ${stepOddsRatio_FEMALE};
var minNumPatientsSelect_FEMALE = ${minNumPatientsSelect_FEMALE};
var maxNumPatientsSelect_FEMALE = ${maxNumPatientsSelect_FEMALE};
var stepNumPatients_FEMALE = ${stepNumPatients_FEMALE};


var FDRCutoffInit_MALE = ${initFDR_MALE};
var FDRadjCutoffInit_MALE = ${initFDRadj_MALE};
var RRCutoffInit_MALE = ${initRrisk_MALE};
var PhiCutoffInit_MALE = ${initPhi_MALE};
var CSCutoffInit_MALE = ${initCscore_MALE};
var OddsRatioCutoffInit_MALE = ${initOddsRatio_MALE};
var NpatCutoffInit_MALE = ${initNumPatients_MALE};
var minFDRSelect_MALE = ${minFDRSelect_MALE};
var maxFDRSelect_MALE = ${maxFDRSelect_MALE};
var stepFDR_MALE = ${stepFDR_MALE};
var minFDRadjSelect_MALE = ${minFDRadjSelect_MALE};
var maxFDRadjSelect_MALE = ${maxFDRadjSelect_MALE};
var stepFDRadj_MALE = ${stepFDRadj_MALE};
var minRriskSelect_MALE = ${minRriskSelect_MALE};
var maxRriskSelect_MALE = ${maxRriskSelect_MALE};
var stepRrisk_MALE = ${stepRrisk_MALE};
var minPhiSelect_MALE = ${minPhiSelect_MALE};
var maxPhiSelect_MALE = ${maxPhiSelect_MALE};
var stepPhi_MALE = ${stepPhi_MALE};
var minCscoreSelect_MALE = ${minCscoreSelect_MALE};
var maxCscoreSelect_MALE = ${maxCscoreSelect_MALE};
var stepCscore_MALE = ${stepCscore_MALE};
var minOddsRatioSelect_MALE = ${minOddsRatioSelect_MALE};
var maxOddsRatioSelect_MALE = ${maxOddsRatioSelect_MALE};
var stepOddsRatio_MALE = ${stepOddsRatio_MALE};
var minNumPatientsSelect_MALE = ${minNumPatientsSelect_MALE};
var maxNumPatientsSelect_MALE = ${maxNumPatientsSelect_MALE};
var stepNumPatients_MALE = ${stepNumPatients_MALE};


var pvalAdjApproach = "${executorObj.pvalAdjApproach!'---'}";


var heatmapDataFiltered = [];
var tableDataFiltered = [];


function scrollToId(targetElemId) {

	if(parseInt($(window).width()) < 769) {
  		document.getElementById(targetElemId).scrollIntoView();
  		$("#navMenu").removeData("executing");
  	}
  	else {
	  	// Commented FF incompatib: event.preventDefault();
		$('html, body').stop().animate({
	       	scrollTop: (parseInt($("#" + targetElemId).offset().top) - 80)
	    }, 600, 'swing', function() {
	    	$("#navMenu").removeData("executing");
	  	});
  	}
  	
}

function updateTableHeatMapNetwork() {
	
	// Scroll to results
  	scrollToId("analysisResultDivExt");
	
	// Update table
	$("#tableDivFiltered").tabulator("setData", tableDataFiltered);
	$('#recordSelectedREs').html("Numbers of comorbidity pairs selected after filtering: " + tableDataFiltered.length + " (over " + tableData.length + " pairs)");
	var selectName = $('#parHeatmapSel').find(":selected").text();
	var selectValue = $('#parHeatmapSel').find(":selected").val();
	$('#paramHeatmapRes').html(" - parameter visualized in the heatmap: '" + selectName + "' - ");
	$('#paramNetworkRes').html(" - parameter visualized in the network: '" + selectName + "' - ");

	// Update heatmap
	var columnValues = [];
	var columnValuesDescr = [];
	for (i = 0; i < tableDataFiltered.length; i++) {
		if (tableDataFiltered[i].disAcode !== undefined && tableDataFiltered[i].disAcode && columnValues.indexOf(tableDataFiltered[i].disAcode) < 0) {
			columnValues.push(tableDataFiltered[i].disAcode);
			if(tableDataFiltered[i].disAname !== undefined && tableDataFiltered[i].disAname) {
				columnValuesDescr.push(tableDataFiltered[i].disAname);
			}
			else {
				columnValuesDescr.push("");
			}
		}
		if (tableDataFiltered[i].disBcode !== undefined && tableDataFiltered[i].disBcode && columnValues.indexOf(tableDataFiltered[i].disBcode) < 0) {
			columnValues.push(tableDataFiltered[i].disBcode);
			if(tableDataFiltered[i].disBname !== undefined && tableDataFiltered[i].disBname) {
				columnValuesDescr.push(tableDataFiltered[i].disBname);
			}
			else {
				columnValuesDescr.push("");
			}
		}
	}
	
	var matrixValue = [];
	var matrixValueSR = [];
	for(var i = 0; i < columnValues.length; i++) {
    	matrixValue[i] = new Array(columnValues.length);
    	matrixValueSR[i] = new Array(columnValues.length);
		for(var j = 0; j < columnValues.length; j++) {
			matrixValue[i][j] = null;
			matrixValueSR[i][j] = null;
		}
	}
	
	for (i = 0; i < tableDataFiltered.length; i++) {
		if(tableDataFiltered[i].disAcode !== undefined && tableDataFiltered[i].disAcode && tableDataFiltered[i].disBcode !== undefined && tableDataFiltered[i].disBcode) {
			var indexDiseaseA = columnValues.indexOf(tableDataFiltered[i].disAcode);
			var indexDiseaseB = columnValues.indexOf(tableDataFiltered[i].disBcode);
			if(indexDiseaseA >= 0 && indexDiseaseB >= 0) {
				
				if(selectValue !== 'undefined' && selectValue === "rr") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].relativeRiskIndex;
				}
				else if (selectValue !== 'undefined' && selectValue === "phi") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].phiIndex;
				}
				else if (selectValue !== 'undefined' && selectValue === "sc") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].score;
				}
				else if (selectValue !== 'undefined' && selectValue === "fdr") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].fisherTest;
				}
				else if (selectValue !== 'undefined' && selectValue === "fdrAdj") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].fisherTestAdjusted;
				}
				else if (selectValue !== 'undefined' && selectValue === "or") {
					matrixValue[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].oddsRatioIndex;
				}
				else {
					matrixValue[indexDiseaseA][indexDiseaseB] = 0.0;
				}
				
				matrixValueSR[indexDiseaseA][indexDiseaseB] = tableDataFiltered[i].sexRatioBA;
				matrixValueSR[indexDiseaseB][indexDiseaseA] = tableDataFiltered[i].sexRatioAB;
				
				<#if (directionality_filter_numDays <= 0) >
				/* Since time directionality filter is disabled... */
				matrixValue[indexDiseaseB][indexDiseaseA] = matrixValue[indexDiseaseA][indexDiseaseB];
				</#if>
				
			}	
		}
	}
	
	// Refine column names with description if present
	var columnValuesX = [];
	var columnValuesY = [];
	for(var i = 0; i < columnValues.length; i++) {
		var descriptionX = "";
		var descriptionY = "";
		if(columnValuesDescr[i] !== undefined && columnValuesDescr[i] && (typeof columnValuesDescr[i] === 'string' || columnValuesDescr[i] instanceof String) && 
			columnValuesDescr[i] !== columnValues[i]) {
			if(columnValuesDescr[i].length > 20) {
				descriptionX = ", " + columnValuesDescr[i].substring(0, 19) + "... ";
			}
			else {
				descriptionX = ", " + columnValuesDescr[i] + " ";
			}
			
			if(columnValuesDescr[i].length > 15) {
				descriptionY = ", " + columnValuesDescr[i].substring(0, 14) + "... ";
			}
			else {
				descriptionY = ", " + columnValuesDescr[i] + " ";
			}
		}
    	columnValuesX.push(columnValues[i] + descriptionX);
    	columnValuesY.push(descriptionY + columnValues[i]);
	}
	
	var heatmapDataFiltered = [
	  {
	    z: matrixValue, // [[1, 20, 30], [20, 1, 60], [30, 60, 1]],
	    x: columnValuesX,
	    y: columnValuesY,
	    type: 'heatmap',
	    colorscale:'Viridis'
	  }
	];

    /*
    colorscale: [
		    ['-4.5', 'rgb(165,0,38)'],
		    ['-3.5', 'rgb(215,48,39)'],
		    ['-2.5', 'rgb(244,109,67)'],
		    ['-1.5', 'rgb(253,174,97)'],
		    ['-0.5', 'rgb(254,224,144)'],
		    ['0.5', 'rgb(224,243,248)'],
		    ['1.5', 'rgb(171,217,233)'],
		    ['2.5', 'rgb(116,173,209)'],
		    ['3.5', 'rgb(69,117,180)'],
		    ['4.5', 'rgb(49,54,149)']
 	    ]
 	    */

	heatmapLayout.title = selectName + " heatmap";
	
	Plotly.newPlot('heatMapDiv', heatmapDataFiltered, heatmapLayout);
	
	
	
	<#if isGenderEnabled == "true">
	// Update heatmap sex ratio
	var heatmapDataFilteredSR = [
	  {
	    z: matrixValueSR, // [[1, 20, 30], [20, 1, 60], [30, 60, 1]],
	    x: columnValuesX,
	    y: columnValuesY,
	    type: 'heatmap',
	    colorscale: 'Jet'
	  }
	];
	
	Plotly.newPlot('heatMapSRDiv', heatmapDataFilteredSR, heatmapLayoutSR);
	</#if>
	
	// Populate network representation of disease comorbidities
	var nodesData = [];
	var nodesDataIDs = [];
	var edgesData = [];
	var edgesDataIDs = [];
	
	for (i = 0; i < tableDataFiltered.length; i++) {
		if(tableDataFiltered[i].disAcode !== undefined && tableDataFiltered[i].disAcode && tableDataFiltered[i].disBcode !== undefined && tableDataFiltered[i].disBcode) {
			// Add disease A nodes
			var indexDiseaseA = nodesDataIDs.indexOf(tableDataFiltered[i].disAcodeNum);
			if(indexDiseaseA !== 'undefined' && indexDiseaseA < 0) {
				var adjName = ((tableDataFiltered[i].disAname.length > 20) ? tableDataFiltered[i].disAname.substring(0, 19) + "..." : tableDataFiltered[i].disAname);
				nodesData.push({
					id: tableDataFiltered[i].disAcodeNum, 
					value: (tableDataFiltered[i].patWdisA + 1),
					mass: (tableDataFiltered[i].patWdisA + 1),
					label: adjName + " ("+ tableDataFiltered[i].patWdisA + " pts.)",
					title: tableDataFiltered[i].disAname + "," + tableDataFiltered[i].disAcode + " ("+ tableDataFiltered[i].patWdisA + " pts.)"
				});
				nodesDataIDs.push(tableDataFiltered[i].disAcodeNum);
			}
			
			// Add disease B nodes
			var indexDiseaseB = nodesDataIDs.indexOf(tableDataFiltered[i].disBcodeNum);
			if(indexDiseaseB !== 'undefined' && indexDiseaseB < 0) {
				var adjName = ((tableDataFiltered[i].disBname.length > 20) ? tableDataFiltered[i].disBname.substring(0, 19) + "..." : tableDataFiltered[i].disBname);
				nodesData.push({
					id: tableDataFiltered[i].disBcodeNum, 
					value: (tableDataFiltered[i].patWdisB + 1),
					mass: (tableDataFiltered[i].patWdisB + 1),
					label: adjName + " ("+ tableDataFiltered[i].patWdisB + " pts.)",
					title: tableDataFiltered[i].disBname + "," + tableDataFiltered[i].disBcode + ", ("+ tableDataFiltered[i].patWdisB + " pts.)"
				});
				nodesDataIDs.push(tableDataFiltered[i].disBcodeNum);
				
			}
			
			var arkValue = 0.0;
			if(selectValue !== 'undefined' && selectValue === "rr") {
				arkValue = tableDataFiltered[i].relativeRiskIndex;
			}
			else if (selectValue !== 'undefined' && selectValue === "phi") {
				arkValue = tableDataFiltered[i].phiIndex;
			}
			else if (selectValue !== 'undefined' && selectValue === "sc") {
				arkValue = tableDataFiltered[i].score;
			}
			else if (selectValue !== 'undefined' && selectValue === "fdr") {
				arkValue = tableDataFiltered[i].fisherTest;
			}
			else if (selectValue !== 'undefined' && selectValue === "fdrAdj") {
				arkValue = tableDataFiltered[i].fisherTestAdjusted;
			}
			else if (selectValue !== 'undefined' && selectValue === "or") {
				arkValue = tableDataFiltered[i].oddsRatioIndex;
			}
			else {
				arkValue = 0.0;
			}
			
			<#if (directionality_filter_numDays > 0) >
			var edgeStr1 = nodesDataIDs.indexOf(tableDataFiltered[i].disAcodeNum) + "__*__" + nodesDataIDs.indexOf(tableDataFiltered[i].disBcodeNum) + "__*__" + arkValue;
			
			edgesDataIDs.push(edgeStr1);
			<#else>
			var edgeStr1 = nodesDataIDs.indexOf(tableDataFiltered[i].disAcodeNum) + "__*__" + nodesDataIDs.indexOf(tableDataFiltered[i].disBcodeNum) + "__*__" + arkValue;
			var edgeStr2 = nodesDataIDs.indexOf(tableDataFiltered[i].disBcodeNum) + "__*__" + nodesDataIDs.indexOf(tableDataFiltered[i].disAcodeNum) + "__*__" + arkValue;
			
			edgesDataIDs.push(edgeStr1);
			edgesDataIDs.push(edgeStr2);
			</#if>
		}
	}
	
	
	// Normalize node mass
	var maxNodeMass = null;
	var minNodeMass = null;
	
	for(nodeIndex = 0; nodeIndex < nodesData.length; nodeIndex ++) {
	    nodeVar = nodesData[nodeIndex];
	    if(nodeVar != null && typeof nodeVar.mass != 'undefined') {
	        nodeMass = nodeVar.mass;
	        if(maxNodeMass == null || nodeMass > maxNodeMass) maxNodeMass = nodeMass;
	        if(minNodeMass == null || nodeMass < minNodeMass) minNodeMass = nodeMass;
	    }
	}
	
	for(nodeIndex = 0; nodeIndex < nodesData.length; nodeIndex ++) {
	    nodeVar = nodesData[nodeIndex];
	    if(nodeVar != null && typeof nodeVar.mass != 'undefined' && minNodeMass != null && maxNodeMass != null) {
	        nodeMass = (10 * ((nodeVar.mass + 1 - minNodeMass) / (maxNodeMass + 1 - minNodeMass))) + 1;
	        // alert("OLD MASS: " + nodeVar.mass + " > " + nodeMass);
	        nodeVar.mass = nodeMass;
	    }
	}
	
	
	var addedEdges = [];
	for(var edgeIndex = 0; edgeIndex < edgesDataIDs.length; edgeIndex++) {
		var edgeIdValue = edgesDataIDs[edgeIndex];
		var edgeIdValueSplit = edgeIdValue.split("__*__");
		
		if(typeof edgeIdValueSplit != 'undefined' && edgeIdValueSplit.length == 3) {
			var indexDiseaseA = edgeIdValueSplit[0];
			var indexDiseaseB = edgeIdValueSplit[1];
			var arkValue = parseFloat(edgeIdValueSplit[2]);
			
			var returnEdgeIdValue = indexDiseaseB + "__*__" + indexDiseaseA + "__*__" + arkValue;
			
			<#if (directionality_filter_numDays > 0) >
			var directionalityOfEdge = "to";
			<#else>
			var directionalityOfEdge = "";
			</#if>
			
			if(directionalityOfEdge == "" && (addedEdges.indexOf(edgeIdValue) >= 0 || addedEdges.indexOf(returnEdgeIdValue) >= 0)) {
				continue;
			}
			
			edgesData.push({
		    	id: nodesDataIDs[indexDiseaseA] + "_" + nodesDataIDs[indexDiseaseB] + "_" + arkValue,
				from: nodesDataIDs[indexDiseaseA], // tableDataFiltered[i].disAcodeNum, 
				to: nodesDataIDs[indexDiseaseB], // tableDataFiltered[i].disBcodeNum,
				value: arkValue,
				label: arkValue + "",
				font: {align: 'top'},
				arrows: directionalityOfEdge
			});
			
			addedEdges.push(edgeIdValue);
			addedEdges.push(returnEdgeIdValue);
		}
	}
	
	// Create a network
    var container = document.getElementById('networkDiv');

    // Provide the data in the vis format
    nodesData = new vis.DataSet(nodesData);
    edgesData = new vis.DataSet(edgesData);
    
    var data = {
        nodes: nodesData,
        edges: edgesData
    };
    var options = {
    	autoResize: true,
  		height: '700px',
  		width: '100%',
  		locale: 'en',
  		physics: {
           	enabled: true
        },
  		nodes: {
  		   	color: {
      			highlight: '#E7F6A4'
      		},
      		shape: 'dot',
      		scaling: {
	            customScalingFunction: function (min, max, total, value) {
	              	var valueRet = (value + 1 - min) / (max + 2 - min);
	              	// alert("max " + max + " min " + min + " ---> value: " + valueRet);
	              	return valueRet;
	            },
	            min:1,
	            max:20
	        }
  		},
  		interaction: {
  			tooltipDelay: 300
  		},
  		edges: {
      		color: {
      			highlight: '#1A4A00'
      		},
      		scaling: {
        		min: 0.3,
        		max: 5
      		}
  		}
    };

    // Initialize your network
    var network = new vis.Network(container, data, options);
	network.stabilize();
	
	network.on("dragEnd", function (params) {
		// console.log('dragEnd Event:', params);
		$("#networkTable").height("150px");
		updateNetworkTable(params, nodesData, edgesData);
	});
	
	network.on("selectNode", function (params) {
        // console.log('selectNode Event:', params);
        $("#networkTable").height("150px");
        updateNetworkTable(params, nodesData, edgesData);
    });
    
    network.on("deselectNode", function (params) {
        // console.log('deselectNode Event:', params);
        // $("#networkTable").html("");
    });
  	
  	// Disable physics when stabilization is done
  	network.on("stabilizationIterationsDone", function () {
	    network.setOptions( { physics: false } );
	    network.fit();
	});
  	
}

function updateNetworkTable(params, nodesData, edgesData) {
		$("#networkTable").html("");
        
        var nodeSelectedId = "";
        if(typeof params !== 'undefined' && typeof params.nodes !== 'undefined' && params.nodes.length > 0) {
        	var nodeSelected = nodesData.get(params.nodes[0]);
			
			if(typeof nodeSelected !== 'undefined' && typeof nodeSelected.title !== 'undefined') {
				nodeSelectedId = nodeSelected.id;
				var $newSpan = $("<span>Node selected: <span style='color: green;'>" + nodeSelected.title.replace("\"", "'") + "</span></span><br/>");
				$("#networkTable").append($newSpan);
			}
        }
        
        
        if(typeof params !== 'undefined' && typeof params.edges !== 'undefined' && params.edges.length > 0) {
        	$.each(params.edges, function(index, value) {
			  	var connectedEdge = edgesData.get(params.edges[index]);
			  	if(typeof connectedEdge !== 'undefined' && typeof connectedEdge.label !== 'undefined' && typeof connectedEdge.to !== 'undefined') {
			  		
			  		var nodeTo = nodesData.get(connectedEdge.to);
			  		var nodeFrom = nodesData.get(connectedEdge.from);
			  		var directionality = <#if (directionality_filter_numDays > 0) >true<#else>false</#if>;
			  		var title = (directionality) ? ((nodeTo.id == nodeSelectedId) ? "preceeded by disease" : "followed by disease") : "connected to disease";
			  		
			  		if(typeof nodeTo !== 'undefined' && typeof nodeTo.id !== 'undefined' && 
			  				nodeTo.id !== nodeSelectedId && typeof nodeTo.title !== 'undefined') {
			  			var $newSpan = $("<span>&gt; " + title + " &gt; <span style='color: green;'>" + nodeTo.title.replace("\"", "'") + "</span> with score: " + connectedEdge.label + "</span><br/>");
						$("#networkTable").append($newSpan);
			  		}
			  		
			  		if(typeof nodeFrom !== 'undefined' && typeof nodeFrom.id !== 'undefined' && 
			  				nodeFrom.id !== nodeSelectedId && typeof nodeFrom.title !== 'undefined') {
				  		var $newSpan = $("<span>&gt; " + title + " &gt; <span style='color: green;'>" + nodeFrom.title.replace("\"", "'") + "</span> with score: " + connectedEdge.label + "<span><br/>");
						$("#networkTable").append($newSpan);
			  		}
				}
			});
        }
}

// Input data plots
window.chartColors = {
	blue : "rgb(54, 162, 235)",
	green : "rgb(75, 192, 192)",
	grey : "rgb(201, 203, 207)",
	orange : "rgb(255, 159, 64)",
	purple : "rgb(153, 102, 255)",
	red : "rgb(255, 99, 132)",
	yellow : "rgb(255, 205, 86)"
}

var patientByAgeSexDataset_FIRST_ADMISSION = ${patientByAgeAndSexDataset_FIRST_ADMISSION!'[]'};
var patientByAgeClassificationDataset_FIRST_ADMISSION = ${patientByAgeAndClassificationDataset_FIRST_ADMISSION!'[]'};
var patientByAgeSexDataset_FIRST_DIAGNOSTIC = ${patientByAgeAndSexDataset_FIRST_DIAGNOSTIC!'[]'};
var patientByAgeClassificationDataset_FIRST_DIAGNOSTIC = ${patientByAgeAndClassificationDataset_FIRST_DIAGNOSTIC!'[]'};
var patientByAgeSexDataset_LAST_ADMISSION = ${patientByAgeAndSexDataset_LAST_ADMISSION!'[]'};
var patientByAgeClassificationDataset_LAST_ADMISSION = ${patientByAgeAndClassificationDataset_LAST_ADMISSION!'[]'};
var patientByAgeSexDataset_LAST_DIAGNOSTIC = ${patientByAgeAndSexDataset_LAST_DIAGNOSTIC!'[]'};
var patientByAgeClassificationDataset_LAST_DIAGNOSTIC = ${patientByAgeAndClassificationDataset_LAST_DIAGNOSTIC!'[]'};
var patientByAgeSexDataset_EXECUTION_TIME = ${patientByAgeAndSexDataset_EXECUTION_TIME!'[]'};
var patientByAgeClassificationDataset_EXECUTION_TIME = ${patientByAgeAndClassificationDataset_EXECUTION_TIME!'[]'};

var patientByBirthDateAndSexDataset = ${patientByBirthDateAndSexDataset!'[]'};
var patientByBirthDateAndClassificationDataset = ${patientByBirthDateAndClassificationDataset!'[]'};

var patientCountBySexDataset = ${patientCountBySexDataset!'[]'};
var patientCountByClassificationDataset = ${patientCountByClassificationDataset!'[]'};

var patientCountByDiseaseAndSexDataset = ${patientCountByDiseaseAndSexDataset!'[]'};
var patientCountByDiseaseAndClassificationDataset = ${patientCountByDiseaseAndClassificationDataset!'[]'};
var patientCountByDiseaseAndSexOnlyIndexDataset = ${patientCountByDiseaseAndSexOnlyIndexDataset!'[]'};
var patientCountByDiseaseAndClassificationOnlyIndexDataset = ${patientCountByDiseaseAndClassificationOnlyIndexDataset!'[]'};

var visitCountByDiseaseAndSexDataset = ${visitCountByDiseaseAndSexDataset!'[]'};
var visitCountByDiseaseAndClassificationDataset = ${visitCountByDiseaseAndClassificationDataset!'[]'};
var visitCountByDiseaseAndSexOnlyIndexDataset = ${visitCountByDiseaseAndSexOnlyIndexDataset!'[]'};
var visitCountByDiseaseAndClassificationOnlyIndexDataset = ${visitCountByDiseaseAndClassificationOnlyIndexDataset!'[]'};


var minFDR = 0;
var maxFDR = 0;
var minFDRadj = 0;
var maxFDRadj = 0;
var minRrisk = 0;
var maxRrisk = 0;
var minPhi = 0;
var maxPhi = 0;
var minCscore = 0;
var maxCscore = 0;
var minOddsRatio = 0;
var maxOddsRatio = 0;
var minNumPatients = 0;
var maxNumPatients = 0;    

function patientDataOverviewChartGeneration() {
	
	// Patient count by sex
	var layout_PatCountBySex = {
		title: 'Number of patients<#if isGenderEnabled == "true"> by sex</#if>',
		xaxis: {
		  title: '<#if isGenderEnabled == "true">Sex<#else>Number</#if>'
		},
		yaxis: {
		  title: 'Number of patients'
		}
	};
	Plotly.newPlot('patCountBySexChart', patientCountBySexDataset, layout_PatCountBySex);
	
	// Patient count by classification
	if(typeof patientCountByClassificationDataset !== 'undefined' && patientCountByClassificationDataset.length > 0) {
		var layout_PatCountByClassification = {
		  title: 'Number of patients by ${patientFacet1_name!'facet_1'}',
		  xaxis: {
		    title: '${patientFacet1_name!'facet_1'}'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patCountByClassificationChart', patientCountByClassificationDataset, layout_PatCountByClassification);
	}
	else {
		$("#patCountByClassificationChart").remove();
	}
	
	
	// Patient age by sex
	var layout_AgeSex_FIRST_ADMISSION = {
	  title: 'Number of patients by age (years at FIRST_ADMISSION)<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Age at FIRST_ADMISSION'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patByAgeSexChart_FIRST_ADMISSION', patientByAgeSexDataset_FIRST_ADMISSION, layout_AgeSex_FIRST_ADMISSION);
	
	var layout_AgeSex_FIRST_DIAGNOSTIC = {
	  title: 'Number of patients by age (years at FIRST_DIAGNOSTIC)<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Age at FIRST_DIAGNOSTIC'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patByAgeSexChart_FIRST_DIAGNOSTIC', patientByAgeSexDataset_FIRST_DIAGNOSTIC, layout_AgeSex_FIRST_DIAGNOSTIC);
	
	var layout_AgeSex_LAST_ADMISSION = {
	  title: 'Number of patients by age (years at LAST_ADMISSION)<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Age at LAST_ADMISSION'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patByAgeSexChart_LAST_ADMISSION', patientByAgeSexDataset_LAST_ADMISSION, layout_AgeSex_LAST_ADMISSION);
	
	var layout_AgeSex_LAST_DIAGNOSTIC = {
	  title: 'Number of patients by age (years at LAST_DIAGNOSTIC)<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Age at LAST_DIAGNOSTIC'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patByAgeSexChart_LAST_DIAGNOSTIC', patientByAgeSexDataset_LAST_DIAGNOSTIC, layout_AgeSex_LAST_DIAGNOSTIC);
	
	var layout_AgeSex_EXECUTION_TIME = {
	  title: 'Number of patients by age (years at EXECUTION_TIME)<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Age at EXECUTION_TIME'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patByAgeSexChart_EXECUTION_TIME', patientByAgeSexDataset_EXECUTION_TIME, layout_AgeSex_EXECUTION_TIME);
	
	
	
	// Patient age by classification
	if(typeof patientByAgeClassificationDataset_FIRST_ADMISSION !== 'undefined' && patientByAgeClassificationDataset_FIRST_ADMISSION.length > 0) {
		var layout_AgeClassification_FIRST_ADMISSION = {
		  title: 'Number of patients by age (years at FIRST_ADMISSION) and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Age at FIRST_ADMISSION'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patByAgeClassificationChart_FIRST_ADMISSION', patientByAgeClassificationDataset_FIRST_ADMISSION, layout_AgeClassification_FIRST_ADMISSION);
	}
	else {
		$("#patByAgeClassificationChart_FIRST_ADMISSION").remove();
	}
	
	if(typeof patientByAgeClassificationDataset_FIRST_DIAGNOSTIC !== 'undefined' && patientByAgeClassificationDataset_FIRST_DIAGNOSTIC.length > 0) {
		var layout_AgeClassification_FIRST_DIAGNOSTIC = {
		  title: 'Number of patients by age (years at FIRST_DIAGNOSTIC) and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Age at FIRST_DIAGNOSTIC'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patByAgeClassificationChart_FIRST_DIAGNOSTIC', patientByAgeClassificationDataset_FIRST_DIAGNOSTIC, layout_AgeClassification_FIRST_DIAGNOSTIC);
	}
	else {
		$("#patByAgeClassificationChart_FIRST_DIAGNOSTIC").remove();
	}
	
	if(typeof patientByAgeClassificationDataset_LAST_ADMISSION !== 'undefined' && patientByAgeClassificationDataset_LAST_ADMISSION.length > 0) {
		var layout_AgeClassification_LAST_ADMISSION = {
		  title: 'Number of patients by age (years at LAST_ADMISSION) and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Age at LAST_ADMISSION'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patByAgeClassificationChart_LAST_ADMISSION', patientByAgeClassificationDataset_LAST_ADMISSION, layout_AgeClassification_LAST_ADMISSION);
	}
	else {
		$("#patByAgeClassificationChart_LAST_ADMISSION").remove();
	}
	
	if(typeof patientByAgeClassificationDataset_LAST_DIAGNOSTIC !== 'undefined' && patientByAgeClassificationDataset_LAST_DIAGNOSTIC.length > 0) {
		var layout_AgeClassification_LAST_DIAGNOSTIC = {
		  title: 'Number of patients by age (years at LAST_DIAGNOSTIC) and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Age at LAST_DIAGNOSTIC'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patByAgeClassificationChart_LAST_DIAGNOSTIC', patientByAgeClassificationDataset_LAST_DIAGNOSTIC, layout_AgeClassification_LAST_DIAGNOSTIC);
	}
	else {
		$("#patByAgeClassificationChart_LAST_DIAGNOSTIC").remove();
	}
	
	if(typeof patientByAgeClassificationDataset_EXECUTION_TIME !== 'undefined' && patientByAgeClassificationDataset_EXECUTION_TIME.length > 0) {
		var layout_AgeClassification_EXECUTION_TIME = {
		  title: 'Number of patients by age (years at EXECUTION_TIME) and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Age at EXECUTION_TIME'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patByAgeClassificationChart_EXECUTION_TIME', patientByAgeClassificationDataset_EXECUTION_TIME, layout_AgeClassification_EXECUTION_TIME);
	}
	else {
		$("#patByAgeClassificationChart_EXECUTION_TIME").remove();
	}
	
	// Patient by birth year by sex
	var layout_PatientByBirthDateAndSexDataset = {
	  title: 'Number of patients by birth year<#if isGenderEnabled == "true"> and sex</#if> <br> (select area to zoom)',
	  xaxis: {
	    title: 'Birth year'
	  },
	  yaxis: {
	    title: 'Number of patients'
	  }
	};
	Plotly.newPlot('patientByBirthDateAndSexChart', patientByBirthDateAndSexDataset, layout_PatientByBirthDateAndSexDataset);
	
	// Patient by birth year by classification
	if(typeof patientByBirthDateAndClassificationDataset !== 'undefined' && patientByBirthDateAndClassificationDataset.length > 0) {
		var layout_patientByBirthDateAndClassificationDataset = {
		  title: 'Number of patients by birth year and ${patientFacet1_name!'facet_1'} <br> (select area to zoom)',
		  xaxis: {
		    title: 'Birth year'
		  },
		  yaxis: {
		    title: 'Number of patients'
		  }
		};
		Plotly.newPlot('patientByBirthDateAndClassificationChart', patientByBirthDateAndClassificationDataset, layout_patientByBirthDateAndClassificationDataset);
	}
	else {
		$("#patientByBirthDateAndClassificationChart").remove();
	}
	
	// Patient count by disease and sex - ALL
	var layout_patientCountByDiseaseAndSex = {
		title: 'Number of patients by disease<#if isGenderEnabled == "true">, split by sex</#if> <br> All diseases considered (select area to zoom)',
		barmode: 'stack'
	};
	Plotly.newPlot('patientCountByDiseaseAndSexChart', patientCountByDiseaseAndSexDataset, layout_patientCountByDiseaseAndSex);
	
	
	// Patient count by disease and classification - ALL
	if(typeof patientCountByDiseaseAndClassificationDataset !== 'undefined' && patientCountByDiseaseAndClassificationDataset.length > 0) {
		var layout_patientCountByDiseaseAndClassification = {
			title: 'Number of patients by disease, split by ${patientFacet1_name!'facet_1'} <br> All diseases considered (select area to zoom)',
			barmode: 'stack'
		};
		Plotly.newPlot('patientCountByDiseaseAndClassificationChart', patientCountByDiseaseAndClassificationDataset, layout_patientCountByDiseaseAndClassification);
	}
	else {
		$("#patCountByClassificationChart").remove();
	}
	
	// Patient count by disease and sex - ONLY INDEX
	var layout_patientCountByDiseaseAndSexOnlyIndex = {
		title: 'Number of patients by disease<#if isGenderEnabled == "true">, split by sex</#if> <br> only studied diseases (select area to zoom)',
		barmode: 'stack'
	};
	// DISABLED: Plotly.newPlot('patientCountByDiseaseAndSexOnlyIndexChart', patientCountByDiseaseAndSexOnlyIndexDataset, layout_patientCountByDiseaseAndSexOnlyIndex);
	
	
	// Patient count by disease and classification - ONLY INDEX
	if(typeof patientCountByDiseaseAndClassificationOnlyIndexDataset !== 'undefined' && patientCountByDiseaseAndClassificationOnlyIndexDataset.length > 0) {
		var layout_patientCountByDiseaseAndClassificationOnlyIndex = {
			title: 'Number of patients by disease, split by ${patientFacet1_name!'facet_1'} <br> only studied diseases (select area to zoom)',
			barmode: 'stack'
		};
		Plotly.newPlot('patientCountByDiseaseAndClassificationOnlyIndexChart', patientCountByDiseaseAndClassificationOnlyIndexDataset, layout_patientCountByDiseaseAndClassificationOnlyIndex);
	}
	else {
		$("#patCountByClassificationOnlyIndexChart").remove();
	}
	
	// Visit count by disease and sex - ALL
	var layout_visitCountByDiseaseAndSex = {
		title: 'Number of visit by disease<#if isGenderEnabled == "true">, split by sex</#if> <br> All diseases considered (select area to zoom)',
		barmode: 'stack'
	};
	Plotly.newPlot('visitCountByDiseaseAndSexChart', visitCountByDiseaseAndSexDataset, layout_visitCountByDiseaseAndSex);
	
	
	// Visit count by disease and classification - ALL
	if(typeof visitCountByDiseaseAndClassificationDataset !== 'undefined' && visitCountByDiseaseAndClassificationDataset.length > 0) {
		var layout_visitCountByDiseaseAndClassification = {
			title: 'Number of visits by disease, split by ${patientFacet1_name!'facet_1'} <br> all diseases (select area to zoom)',
			barmode: 'stack'
		};
		Plotly.newPlot('visitCountByDiseaseAndClassificationChart', visitCountByDiseaseAndClassificationDataset, layout_visitCountByDiseaseAndClassification);
	}
	else {
		$("#patCountByClassificationChart").remove();
	}
	
	// Visit count by disease and sex - ONLY INDEX
	var layout_visitCountByDiseaseAndSexOnlyIndex = {
		title: 'Number of visits by disease<#if isGenderEnabled == "true">, split by sex</#if> <br/> only studied diseases (select area to zoom)',
		barmode: 'stack'
	};
	// DISABLED: Plotly.newPlot('visitCountByDiseaseAndSexOnlyIndexChart', visitCountByDiseaseAndSexOnlyIndexDataset, layout_visitCountByDiseaseAndSexOnlyIndex);
	
	
	// Visit count by disease and classification - ONLY INDEX
	if(typeof visitCountByDiseaseAndClassificationOnlyIndexDataset !== 'undefined' && visitCountByDiseaseAndClassificationOnlyIndexDataset.length > 0) {
		var layout_visitCountByDiseaseAndClassificationOnlyIndex = {
			title: 'Number of visits by disease, split by ${patientFacet1_name!'facet_1'} <br> only studied diseases (select area to zoom)',
			barmode: 'stack'
		};
		Plotly.newPlot('visitCountByDiseaseAndClassificationOnlyIndexChart', visitCountByDiseaseAndClassificationOnlyIndexDataset, layout_visitCountByDiseaseAndClassificationOnlyIndex);
	}
	else {
		$("#patCountByClassificationOnlyIndexChart").remove();
	}
	
}


function setFEMALEData(enableForce) {
    if(tableData !== tableData_FEMALE || enableForce) {
       	$("#tableDiv").tabulator("clearData");
       	tableData = tableData_FEMALE;
       	$('#totComorPairs').html(tableData.length);
       	$("#tableDiv").tabulator("setData", tableData);
       	<#if isGenderEnabled == "true">
       	$(".sexInfo").each(function() {
    		$(this).html("Showing comorbidity analysis results of ${executorObj.sexRatioFemaleIdentifier!'---'} patients (" + femalePatNumber + " over " + (femalePatNumber + malePatNumber) + "). <br/> Click on this icon <img title='Settings' onclick=\"$('#changeSexSelection').dialog('open');\" border='0' alt='Settings' src='${baseCSS_JSpath!''}img/settings.ico' width='16' height='16' style='cursor:pointer;'> to modify this setting.");
		});
		$("#sexInfoPopUp").each(function() {
    		$(this).html("Showing comorbidity analysis results of ${executorObj.sexRatioFemaleIdentifier!'---'} patients (" + femalePatNumber + " over " + (femalePatNumber + malePatNumber) + ").");
		});
		</#if>
			
		minFDR = ${minFDR_FEMALE};
		maxFDR = ${maxFDR_FEMALE};
		minFDRadj = ${minFDRadj_FEMALE};
		maxFDRadj = ${maxFDRadj_FEMALE};
		minRrisk = ${minRrisk_FEMALE};
		maxRrisk = ${maxRrisk_FEMALE};
		minPhi = ${minPhi_FEMALE};
		maxPhi = ${maxPhi_FEMALE};
		minCscore = ${minCscore_FEMALE};
		maxCscore = ${maxCscore_FEMALE};
		minOddsRatio = ${minOddsRatio_FEMALE};
		maxOddsRatio = ${maxOddsRatio_FEMALE};
		minNumPatients = ${minNumPatients_FEMALE};
		maxNumPatients = ${maxNumPatients_FEMALE};
		
		alertNumPatients = "${alertNumPatients_FEMALE}"
		if( ((typeof alertNumPatients != "undefined") && (typeof alertNumPatients.valueOf() == "string")) && (alertNumPatients.length > 0) ) {
			$('#alertNumPatients').html(alertNumPatients);
		}
		
		$('#FDRCutoffSel').html(FDRCutoffInit_FEMALE);
  	    $('#FDRCutoffSelExt').html(FDRCutoffInit_FEMALE);
	    $('#FDRCutoff').slider({
	      min: minFDRSelect_FEMALE, max: maxFDRSelect_FEMALE, step: stepFDR_FEMALE, value: FDRCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#FDRCutoff').on('slidechange', function( event, ui ) { $('#FDRCutoffSel').html(ui.value); $('#FDRCutoffSelExt').html(ui.value); } );
	  
	    $('#FDRadjCutoffSel').html(FDRadjCutoffInit_FEMALE);
	    $('#FDRadjCutoffSelExt').html(FDRadjCutoffInit_FEMALE);
	    $('#FDRadjCutoff').slider({
	      min: minFDRadjSelect_FEMALE, max: maxFDRadjSelect_FEMALE, step: stepFDRadj_FEMALE, value: FDRadjCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#FDRadjCutoff').on('slidechange', function( event, ui ) { $('#FDRadjCutoffSel').html(ui.value); $('#FDRadjCutoffSelExt').html(ui.value); } );
	  
	    $('#RRCutoffSel').html(RRCutoffInit_FEMALE);
	    $('#RRCutoffSelExt').html(RRCutoffInit_FEMALE);
	    $('#RRCutoff').slider({
	       min: minRriskSelect_FEMALE, max: maxRriskSelect_FEMALE, step: stepRrisk_FEMALE, value: RRCutoffInit_FEMALE,
	    }).slider("float").slider("pips");
	    $('#RRCutoff').on('slidechange', function( event, ui ) { $('#RRCutoffSel').html(ui.value); $('#RRCutoffSelExt').html(ui.value); } );
	  
	    $('#PhiCutoffSel').html(PhiCutoffInit_FEMALE);
	    $('#PhiCutoffSelExt').html(PhiCutoffInit_FEMALE);
	    $('#PhiCutoff').slider({
	      min: minPhiSelect_FEMALE, max: maxPhiSelect_FEMALE, step: stepPhi_FEMALE, value: PhiCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#PhiCutoff').on('slidechange', function( event, ui ) { $('#PhiCutoffSel').html(ui.value); $('#PhiCutoffSelExt').html(ui.value); } );
	  
	    $('#CSCutoffSel').html(CSCutoffInit_FEMALE);
	    $('#CSCutoffSelExt').html(CSCutoffInit_FEMALE);
	    $('#CSCutoff').slider({
	      min: minCscoreSelect_FEMALE, max: maxCscoreSelect_FEMALE, step: stepCscore_FEMALE, value: CSCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#CSCutoff').on('slidechange', function( event, ui ) { $('#CSCutoffSel').html(ui.value); $('#CSCutoffSelExt').html(ui.value); } );
	  
	    $('#OddsRatioCutoffSel').html(OddsRatioCutoffInit_FEMALE);
	    $('#OddsRatioCutoffSelExt').html(OddsRatioCutoffInit_FEMALE);
	    $('#OddsRatioCutoff').slider({
	      min: minOddsRatioSelect_FEMALE, max: maxOddsRatioSelect_FEMALE, step: stepOddsRatio_FEMALE, value: OddsRatioCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#OddsRatioCutoff').on('slidechange', function( event, ui ) { $('#OddsRatioCutoffSel').html(ui.value); $('#OddsRatioCutoffSelExt').html(ui.value); } );
	  
	    $('#NpatCutoffSel').html(NpatCutoffInit_FEMALE);
	    $('#NpatCutoffSelExt').html(NpatCutoffInit_FEMALE); 
	    $('#NpatCutoff').slider({
	      min: minNumPatientsSelect_FEMALE, max: maxNumPatientsSelect_FEMALE, step: stepNumPatients_FEMALE, value: NpatCutoffInit_FEMALE
	    }).slider("float").slider("pips");
	    $('#NpatCutoff').on('slidechange', function( event, ui ) { $('#NpatCutoffSel').html(ui.value); $('#NpatCutoffSelExt').html(ui.value); } );
		
		$("#FDRValRangeSelExt").html("[" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelExt").html("[" + minFDRadj + ", " + maxFDRadj + "]");
		$("#RRValRangeSelExt").html("[" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelExt").html("[" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelExt").html("[" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioValRangeSelExt").html("[" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NumPatientsValRangeSelExt").html("[" + minNumPatients + ", " + maxNumPatients + "]");
		
		$("#FDRValRangeSelInt").attr('title', "P-value, value range: [" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelInt").attr('title', "Adjusted P-value, value range: [" + minFDRadj + ", " + maxFDRadj + "] / adjustment approach: " + pvalAdjApproach);
		$("#RRValRangeSelInt").attr('title', "Relative risk, value range: [" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelInt").attr('title', "Phi, value range: [" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelInt").attr('title', "Comorbidity score, value range: [" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioCutoffSelInt").attr('title', "Odds ratio, value range: [" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NpatCutoffSelInt").attr('title', "Number of patients, value range: [" + minNumPatients + ", " + maxNumPatients + "]");
		
        $('#resetButton').trigger('click');
   }
}

function setMALEData(enableForce) {
   if(tableData !== tableData_MALE || enableForce) {
       	$("#tableDiv").tabulator("clearData");
        tableData = tableData_MALE;
        $('#totComorPairs').html(tableData.length);
        $("#tableDiv").tabulator("setData", tableData);
        <#if isGenderEnabled == "true">
        $(".sexInfo").each(function() {
    		$(this).html("Showing comorbidity analysis results of ${executorObj.sexRatioMaleIdentifier!'---'} patients (" + malePatNumber + " over " + (femalePatNumber + malePatNumber) + "). <br/> Click on this icon <img title='Settings' onclick=\"$('#changeSexSelection').dialog('open');\" border='0' alt='Settings' src='${baseCSS_JSpath!''}img/settings.ico' width='16' height='16' style='cursor:pointer;'> to modify this setting.");
		});
		$("#sexInfoPopUp").each(function() {
    		$(this).html("Showing comorbidity analysis results of ${executorObj.sexRatioMaleIdentifier!'---'} patients (" + malePatNumber + " over " + (femalePatNumber + malePatNumber) + ").");
		});
		</#if>
		
		minFDR = ${minFDR_MALE};
		maxFDR = ${maxFDR_MALE};
		minFDRadj = ${minFDRadj_MALE};
		maxFDRadj = ${maxFDRadj_MALE};
		minRrisk = ${minRrisk_MALE};
		maxRrisk = ${maxRrisk_MALE};
		minPhi = ${minPhi_MALE};
		maxPhi = ${maxPhi_MALE};
		minCscore = ${minCscore_MALE};
		maxCscore = ${maxCscore_MALE};
		minOddsRatio = ${minOddsRatio_MALE};
		maxOddsRatio = ${maxOddsRatio_MALE};
		minNumPatients = ${minNumPatients_MALE};
		maxNumPatients = ${maxNumPatients_MALE};
		
		alertNumPatients = "${alertNumPatients_MALE}"
		if( ((typeof alertNumPatients != "undefined") && (typeof alertNumPatients.valueOf() == "string")) && (alertNumPatients.length > 0) ) {
			$('#alertNumPatients').html(alertNumPatients);
		}
		
		$('#FDRCutoffSel').html(FDRCutoffInit_MALE);
  	    $('#FDRCutoffSelExt').html(FDRCutoffInit_MALE);
	    $('#FDRCutoff').slider({
	      min: minFDRSelect_MALE, max: maxFDRSelect_MALE, step: stepFDR_MALE, value: FDRCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#FDRCutoff').on('slidechange', function( event, ui ) { $('#FDRCutoffSel').html(ui.value); $('#FDRCutoffSelExt').html(ui.value); } );
	  
	    $('#FDRadjCutoffSel').html(FDRadjCutoffInit_MALE);
	    $('#FDRadjCutoffSelExt').html(FDRadjCutoffInit_MALE);
	    $('#FDRadjCutoff').slider({
	      min: minFDRadjSelect_MALE, max: maxFDRadjSelect_MALE, step: stepFDRadj_MALE, value: FDRadjCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#FDRadjCutoff').on('slidechange', function( event, ui ) { $('#FDRadjCutoffSel').html(ui.value); $('#FDRadjCutoffSelExt').html(ui.value); } );
	  
	    $('#RRCutoffSel').html(RRCutoffInit_MALE);
	    $('#RRCutoffSelExt').html(RRCutoffInit_MALE);
	    $('#RRCutoff').slider({
	       min: minRriskSelect_MALE, max: maxRriskSelect_MALE, step: stepRrisk_MALE, value: RRCutoffInit_MALE,
	    }).slider("float").slider("pips");
	    $('#RRCutoff').on('slidechange', function( event, ui ) { $('#RRCutoffSel').html(ui.value); $('#RRCutoffSelExt').html(ui.value); } );
	  
	    $('#PhiCutoffSel').html(PhiCutoffInit_MALE);
	    $('#PhiCutoffSelExt').html(PhiCutoffInit_MALE);
	    $('#PhiCutoff').slider({
	      min: minPhiSelect_MALE, max: maxPhiSelect_MALE, step: stepPhi_MALE, value: PhiCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#PhiCutoff').on('slidechange', function( event, ui ) { $('#PhiCutoffSel').html(ui.value); $('#PhiCutoffSelExt').html(ui.value); } );
	  
	    $('#CSCutoffSel').html(CSCutoffInit_MALE);
	    $('#CSCutoffSelExt').html(CSCutoffInit_MALE);
	    $('#CSCutoff').slider({
	      min: minCscoreSelect_MALE, max: maxCscoreSelect_MALE, step: stepCscore_MALE, value: CSCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#CSCutoff').on('slidechange', function( event, ui ) { $('#CSCutoffSel').html(ui.value); $('#CSCutoffSelExt').html(ui.value); } );
	  
	    $('#OddsRatioCutoffSel').html(OddsRatioCutoffInit_MALE);
	    $('#OddsRatioCutoffSelExt').html(OddsRatioCutoffInit_MALE);
	    $('#OddsRatioCutoff').slider({
	      min: minOddsRatioSelect_MALE, max: maxOddsRatioSelect_MALE, step: stepOddsRatio_MALE, value: OddsRatioCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#OddsRatioCutoff').on('slidechange', function( event, ui ) { $('#OddsRatioCutoffSel').html(ui.value); $('#OddsRatioCutoffSelExt').html(ui.value); } );
	  
	    $('#NpatCutoffSel').html(NpatCutoffInit_MALE);
	    $('#NpatCutoffSelExt').html(NpatCutoffInit_MALE); 
	    $('#NpatCutoff').slider({
	      min: minNumPatientsSelect_MALE, max: maxNumPatientsSelect_MALE, step: stepNumPatients_MALE, value: NpatCutoffInit_MALE
	    }).slider("float").slider("pips");
	    $('#NpatCutoff').on('slidechange', function( event, ui ) { $('#NpatCutoffSel').html(ui.value); $('#NpatCutoffSelExt').html(ui.value); } );
		
		$("#FDRValRangeSelExt").html("[" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelExt").html("[" + minFDRadj + ", " + maxFDRadj + "]");
		$("#RRValRangeSelExt").html("[" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelExt").html("[" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelExt").html("[" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioValRangeSelExt").html("[" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NumPatientsValRangeSelExt").html("[" + minNumPatients + ", " + maxNumPatients + "]");
		
		$("#FDRValRangeSelInt").attr('title', "P-value, value range: [" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelInt").attr('title', "Adjusted P-value, value range: [" + minFDRadj + ", " + maxFDRadj + "] / adjustment approach: " + pvalAdjApproach);
		$("#RRValRangeSelInt").attr('title', "Relative risk, value range: [" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelInt").attr('title', "Phi, value range: [" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelInt").attr('title', "Comorbidity score, value range: [" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioCutoffSelInt").attr('title', "Odds ratio, value range: [" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NpatCutoffSelInt").attr('title', "Number of patients, value range: [" + minNumPatients + ", " + maxNumPatients + "]");
		
        $('#resetButton').trigger('click');
   }
}

function setAllSexData(enableForce) {
	if(tableData !== tableData_ALL || enableForce) {
       $("#tableDiv").tabulator("clearData");
     	tableData = tableData_ALL;
       	$('#totComorPairs').html(tableData.length);
       	$("#tableDiv").tabulator("setData", tableData);
       	<#if isGenderEnabled == "true">
       	$(".sexInfo").each(function() {
  			$(this).html("Showing comorbidity analysis results of all patients (" + (femalePatNumber + malePatNumber) + " patients - " + femalePatNumber + " females and " + malePatNumber + " males), independently from sex. <br/> Click on this icon <img title='Settings' onclick=\"$('#changeSexSelection').dialog('open');\"  border='0' alt='Settings' src='${baseCSS_JSpath!''}img/settings.ico' width='16' height='16' style='cursor:pointer;'> to modify this setting.");
		});
		$("#sexInfoPopUp").each(function() {
    		$(this).html("Showing comorbidity analysis results of all patients (" + (femalePatNumber + malePatNumber) + " patients - " + femalePatNumber + " females and " + malePatNumber + " males), independently from sex.");
		});
		</#if>
			
		minFDR = ${minFDR_ALL};
		maxFDR = ${maxFDR_ALL};
		minFDRadj = ${minFDRadj_ALL};
		maxFDRadj = ${maxFDRadj_ALL};
		minRrisk = ${minRrisk_ALL};
		maxRrisk = ${maxRrisk_ALL};
		minPhi = ${minPhi_ALL};
		maxPhi = ${maxPhi_ALL};
		minCscore = ${minCscore_ALL};
		maxCscore = ${maxCscore_ALL};
		minOddsRatio = ${minOddsRatio_ALL};
		maxOddsRatio = ${maxOddsRatio_ALL};
		minNumPatients = ${minNumPatients_ALL};
		maxNumPatients = ${maxNumPatients_ALL};
		
		alertNumPatients = "${alertNumPatients_ALL}"
		if( ((typeof alertNumPatients != "undefined") && (typeof alertNumPatients.valueOf() == "string")) && (alertNumPatients.length > 0) ) {
			$('#alertNumPatients').html(alertNumPatients);
		}
		
		$('#FDRCutoffSel').html(FDRCutoffInit_ALL);
  	    $('#FDRCutoffSelExt').html(FDRCutoffInit_ALL);
	    $('#FDRCutoff').slider({
	      min: minFDRSelect_ALL, max: maxFDRSelect_ALL, step: stepFDR_ALL, value: FDRCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#FDRCutoff').on('slidechange', function( event, ui ) { $('#FDRCutoffSel').html(ui.value); $('#FDRCutoffSelExt').html(ui.value); } );
	  
	    $('#FDRadjCutoffSel').html(FDRadjCutoffInit_ALL);
	    $('#FDRadjCutoffSelExt').html(FDRadjCutoffInit_ALL);
	    $('#FDRadjCutoff').slider({
	      min: minFDRadjSelect_ALL, max: maxFDRadjSelect_ALL, step: stepFDRadj_ALL, value: FDRadjCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#FDRadjCutoff').on('slidechange', function( event, ui ) { $('#FDRadjCutoffSel').html(ui.value); $('#FDRadjCutoffSelExt').html(ui.value); } );
	  
	    $('#RRCutoffSel').html(RRCutoffInit_ALL);
	    $('#RRCutoffSelExt').html(RRCutoffInit_ALL);
	    $('#RRCutoff').slider({
	       min: minRriskSelect_ALL, max: maxRriskSelect_ALL, step: stepRrisk_ALL, value: RRCutoffInit_ALL,
	    }).slider("float").slider("pips");
	    $('#RRCutoff').on('slidechange', function( event, ui ) { $('#RRCutoffSel').html(ui.value); $('#RRCutoffSelExt').html(ui.value); } );
	  
	    $('#PhiCutoffSel').html(PhiCutoffInit_ALL);
	    $('#PhiCutoffSelExt').html(PhiCutoffInit_ALL);
	    $('#PhiCutoff').slider({
	      min: minPhiSelect_ALL, max: maxPhiSelect_ALL, step: stepPhi_ALL, value: PhiCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#PhiCutoff').on('slidechange', function( event, ui ) { $('#PhiCutoffSel').html(ui.value); $('#PhiCutoffSelExt').html(ui.value); } );
	  
	    $('#CSCutoffSel').html(CSCutoffInit_ALL);
	    $('#CSCutoffSelExt').html(CSCutoffInit_ALL);
	    $('#CSCutoff').slider({
	      min: minCscoreSelect_ALL, max: maxCscoreSelect_ALL, step: stepCscore_ALL, value: CSCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#CSCutoff').on('slidechange', function( event, ui ) { $('#CSCutoffSel').html(ui.value); $('#CSCutoffSelExt').html(ui.value); } );
	  
	    $('#OddsRatioCutoffSel').html(OddsRatioCutoffInit_ALL);
	    $('#OddsRatioCutoffSelExt').html(OddsRatioCutoffInit_ALL);
	    $('#OddsRatioCutoff').slider({
	      min: minOddsRatioSelect_ALL, max: maxOddsRatioSelect_ALL, step: stepOddsRatio_ALL, value: OddsRatioCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#OddsRatioCutoff').on('slidechange', function( event, ui ) { $('#OddsRatioCutoffSel').html(ui.value); $('#OddsRatioCutoffSelExt').html(ui.value); } );
	  
	    $('#NpatCutoffSel').html(NpatCutoffInit_ALL);
	    $('#NpatCutoffSelExt').html(NpatCutoffInit_ALL); 
	    $('#NpatCutoff').slider({
	      min: minNumPatientsSelect_ALL, max: maxNumPatientsSelect_ALL, step: stepNumPatients_ALL, value: NpatCutoffInit_ALL
	    }).slider("float").slider("pips");
	    $('#NpatCutoff').on('slidechange', function( event, ui ) { $('#NpatCutoffSel').html(ui.value); $('#NpatCutoffSelExt').html(ui.value); } );
		
		$("#FDRValRangeSelExt").html("[" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelExt").html("[" + minFDRadj + ", " + maxFDRadj + "]");
		$("#RRValRangeSelExt").html("[" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelExt").html("[" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelExt").html("[" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioValRangeSelExt").html("[" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NumPatientsValRangeSelExt").html("[" + minNumPatients + ", " + maxNumPatients + "]");
		
		$("#FDRValRangeSelInt").attr('title', "P-value, value range: [" + minFDR + ", " + maxFDR + "]");
		$("#FDRadjValRangeSelInt").attr('title', "Adjusted P-value, value range: [" + minFDRadj + ", " + maxFDRadj + "] / adjustment approach: " + pvalAdjApproach);
		$("#RRValRangeSelInt").attr('title', "Relative risk, value range: [" + minRrisk + ", " + maxRrisk + "]");
		$("#PhiValRangeSelInt").attr('title', "Phi, value range: [" + minPhi + ", " + maxPhi + "]");
		$("#CSValRangeSelInt").attr('title', "Comorbidity score, value range: [" + minCscore + ", " + maxCscore + "]");
		$("#OddsRatioCutoffSelInt").attr('title', "Odds ratio, value range: [" + minOddsRatio + ", " + maxOddsRatio + "]");
		$("#NpatCutoffSelInt").attr('title', "Number of patients, value range: [" + minNumPatients + ", " + maxNumPatients + "]");
		
        $('#resetButton').trigger('click');
    }
}


var selectedDiseaseFilter = null;
var selectedDiseaseFilterElement = null;

/* INIT METHOD */
$(document).ready(function() {
  
  // Init table data
  tableData = tableData_ALL;
  
  // Init accordeon and header Div
  $('#accordionInitParams').accordion();
  $('#accordionLogParams').accordion();
  
  // Init top menu
  $(".topMenuItem").click(function () {
  	// Close menu if in mobile view
  	if(parseInt($(window).width()) < 769) {
  		$("#header__icon").click();
  	}
  	
  	if($("#navMenu").data("executing")) return;
  	$("#navMenu").data("executing", true);
  	
  	var nameScroll = $(this).attr("scrollId");
  	$(".topMenuItem").css("color", "#005A31");
	$(this).css("color", "#ff4411");
	
	scrollToId(nameScroll);
  });
  
  $(window).scroll(function() {
  	var errorScrollOffset = -1;
  	if($('#' + 'errorScroll').length) {
  	   errorScrollOffset = parseInt($('#' + 'errorScroll').offset().top) - 180;
  	}
  	
  	var infoMessageScrollOffset = parseInt($('#' + 'infoMessageScroll').offset().top) - 180;
  	var inputScrollOffset = parseInt($('#' + 'inputScroll').offset().top) - 180;
 	var logScrollOffset = parseInt($('#' + 'logScroll').offset().top) - 180;
 	var datasetScrollOffset = parseInt($('#' + 'datasetScroll').offset().top) - 180;
 	<#if isGenderEnabled == "true">var sexRatioScrollOffset = parseInt($('#' + 'sexRatioScroll').offset().top) - 180;</#if>
  	var fullResultScrollOffset = parseInt($('#' + 'fullResultScroll').offset().top) - 180;
  	var visualizationScrollOffset = parseInt($('#' + 'visualizationScroll').offset().top) - 180;
    var contactScrollOffset = parseInt($('#' + 'contactScroll').offset().top) - 180;
  
    if(errorScrollOffset > 0 && $(window).scrollTop() < errorScrollOffset && $(window).scrollTop() < inputScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=infoMessageScroll]").css("color", "#ff4411");
    }
    else if(errorScrollOffset > 0 && $(window).scrollTop() < errorScrollOffset && $(window).scrollTop() > inputScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=inputScroll]").css("color", "#ff4411");
    }
    else if(errorScrollOffset > 0 && $(window).scrollTop() >= errorScrollOffset && $(window).scrollTop() < logScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=errorScroll]").css("color", "#ff4411");
    }
    else if(errorScrollOffset <= 0 && $(window).scrollTop() < inputScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=infoMessageScroll]").css("color", "#ff4411");
    }
    else if(errorScrollOffset <= 0 && $(window).scrollTop() < logScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=inputScroll]").css("color", "#ff4411");
    }
    else if($(window).scrollTop() >= logScrollOffset && $(window).scrollTop() < datasetScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=logScroll]").css("color", "#ff4411");
    }<#if isGenderEnabled == "true">else if($(window).scrollTop() >= datasetScrollOffset && $(window).scrollTop() < sexRatioScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=datasetScroll]").css("color", "#ff4411");
    }
    else if($(window).scrollTop() >= sexRatioScrollOffset && $(window).scrollTop() < fullResultScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=sexRatioScroll]").css("color", "#ff4411");
    }</#if>
    else if($(window).scrollTop() >= fullResultScrollOffset && $(window).scrollTop() < visualizationScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=fullResultScroll]").css("color", "#ff4411");
    }
    else if($(window).scrollTop() >= visualizationScrollOffset && $(window).scrollTop() < contactScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=visualizationScroll]").css("color", "#ff4411");
    }
    else if($(window).scrollTop() >= contactScrollOffset) {
    	$(".topMenuItem").css("color", "#005A31");
		$("[scrollId=visualizationScroll]").css("color", "#ff4411");
    }
    
  });
    
  // Init too many pairs dialogue
  $("#tooManyPairsAlert").dialog({
      modal: true,
      autoOpen: false,
      height: 310,
      width: 455,
      buttons: {
        "Set more restrictive filters": function() {
          $('#resetButton').trigger('click');
          $(this).dialog("close");
        },
        "Proceed anyway": function() {
          updateTableHeatMapNetwork();
          $(this).dialog("close");
        }
      }
    });
    
  // Init change sex selection dialogue
  <#if isGenderEnabled == "true">
  $("#changeSexSelection").dialog({
      modal: true,
      autoOpen: false,
      height: 310,
      width: 455,
      buttons: {
        "Load comorbidity data independently from sex": function() {
          setAllSexData(false);
          $(this).dialog("close");
        },
        "Load comorbidity data of females - ${executorObj.sexRatioFemaleIdentifier!'---'}": function() {
          setFEMALEData(false);
          $(this).dialog("close");
        },
        "Load comorbidity data of males - ${executorObj.sexRatioMaleIdentifier!'---'}": function() {
          setMALEData(false);
          $(this).dialog("close");
        }
      }
    });
  </#if>     
    
  // Load patient dataset overview charts
  patientDataOverviewChartGeneration();
  
  // Init tot comorbidity pairs
  $('#totComorPairs').html(tableData.length);
  
  // Init edit filter dialogue
  $(".sliderTitleExt").dialog({
      modal: true,
      autoOpen: false,
      height: 330,
      width: 550,
      buttons: {
        Ok: function() {
          $(this).dialog("close");
        }
      }
    });
  
  $(".editValue").click(function () {
    // Check if enabled
    var checkboxName = $(this).parent().attr('id').replace("Title", "Enabled");
    
    if(!$('#' + checkboxName).is(":checked")) {
		$('#' + checkboxName).prop('checked', true);
	}
	
	var dialogueName = $(this).parent().attr('id') + "Ext";
    $("#" + dialogueName).dialog("open");
  });
  
  // Init heatmap
  // Plotly.newPlot('heatMapDiv', heatmapData, heatmapLayout);
  
  // Init table all
  $("#tableDiv").tabulator({
    layout: "fitColumns",
    columns: columnList,
    // rowClick: function(e, row){ //trigger an alert message when the row is clicked
    //     alert("Row " + row.getData().id + " Clicked!!!!");
    // },
    movableColumns: true,
    pagination: "local", //enable local pagination.
    paginationSize: 20 // this option can take any positive integer value (default = 10)
  });
  $("#tableDiv").tabulator("setData", tableData);
  
  <#if isGenderEnabled == "true">
  // Init table SR
  $("#tableDivSR").tabulator({
    layout: "fitColumns",
    columns: columnListSR,
    // rowClick: function(e, row){ //trigger an alert message when the row is clicked
    //     alert("Row " + row.getData().id + " Clicked!!!!");
    // },
    movableColumns: true,
    pagination: "local", //enable local pagination.
    paginationSize: 10 // this option can take any positive integer value (default = 10)
  });
  $("#tableDivSR").tabulator("setData", tableData);
  </#if>
  
  // Init table filtered
  $("#tableDivFiltered").tabulator({
    layout: "fitColumns",
    columns: columnList,
    // rowClick: function(e, row){ //trigger an alert message when the row is clicked
    //     alert("Row " + row.getData().id + " Clicked!!!!");
    // },
    movableColumns: true,
    pagination: "local", //enable local pagination.
    paginationSize: 10 // this option can take any positive integer value (default = 10)
  });
  // $("#tableDivFiltered").tabulator("setData", tableData);
  
  // Init download CSV buttons
  $("#downloadTableDiv").click(function () {
    $("#tableDiv").tabulator("download", "csv", "comorbidity4j_results_${execID!'---'}_${execDateCSVfileName!'N_A'}.csv");
  });
  
  <#if isGenderEnabled == "true">
  $("#downloadTableDivSR").click(function () {
    $("#tableDivSR").tabulator("download", "csv", "comorbidity4j_sexRatio_results_${execID!'---'}_${execDateCSVfileName!'N_A'}.csv");
  });
  </#if>
  
  $("#downloadTableDivFiltered").click(function () {
    $("#tableDivFiltered").tabulator("download", "csv", "comorbidity4j_results_filtered_${execID!'---'}_${execDateCSVfileName!'N_A'}.csv");
  });
  
  // Init disease selector with autocomplete
  /*
  $("#diseaseSelector").autocomplete({
      minLength: 0,
      source: diseaseData,
      focus: function( event, ui ) {
        $("#diseaseSelector").val(ui.item.label);
        return false;
      },
      select: function(event, ui) {
        $("#diseaseSelector").val(ui.item.label);
        $("#diseaseSelectorHiddenID").val(ui.item.codeInt);
        $("#diseaseSelectorDescription").html(ui.item.codeStr);
 
        return false;
      }
   })
   .autocomplete("instance")._renderItem = function(ul, item) {
      return $("<li>")
        .append("<div>" + item.label) //  + "<br/>" + item.codeInt + " / " + item.codeStr + "</div>")
        .appendTo(ul);
   };
  */
  
  $("#diseaseSelectionTextTrigger").click(function() {
	  $("#dialog").dialog("open");
  });
  
  $("#dialog").dialog({
	      resizable: true,
      	  autoOpen: false,
      	  minHeight: 400,
	      height: "auto",
	      width: "auto",
	      modal: true,
	      title: "Selection of diagnosis",
	      buttons: {
	        "Select": function() {
	        	var selectedDiseaseFilterValue = selectedDiseaseFilter[0].selectize.getValue();
	        	
	        	selectedDiseaseFilterElement = null;
	        	for(var k = 0; k < diseaseData.length; k++) {
					if(typeof diseaseData[k] !== 'undefined' && typeof diseaseData[k].codeInt !== 'undefined' && diseaseData[k].codeInt == selectedDiseaseFilterValue) {
						selectedDiseaseFilterElement = diseaseData[k];
						break;
					}
				}
	        	
	        	if(typeof selectedDiseaseFilterElement !== 'undefined' && typeof selectedDiseaseFilterElement.label !== 'undefined') {
	        		$("#diseaseSelectorText").html(selectedDiseaseFilterElement.label);
	        	}
	        	else {
	        		$("#diseaseSelectorText").html("No disease selected");
	        	}
	        	
	          	$(this).dialog("close");
	        },
	        Cancel: function() {
	        	
	          	$(this).dialog("close");
	        }
	      }
	    });
  
  
  
  selectedDiseaseFilter = $('#selected-to').selectize({
				persist: false,
				openOnFocus: false,
				maxItems: 1,
				maxOptions: 10,
				valueField: 'codeInt',
				labelField: 'label',
				searchField: ['codeStr', 'label'],
				sortField: [
					{field: 'label', direction: 'asc'},
					{field: 'codeStr', direction: 'asc'}
				],
				options: diseaseData,
				render: {
					item: function(item, escape) {
						return '<div>' +
							(item.label ? '<span class="description" style="font-weight: bold;">' + escape((item.label.length > 50) ? item.label.substring(1, 49) + "..." : item.label) + '</span>' : '') + '&nbsp;' +
							// (item.codeStr ? '<span class="code">(\'' + escape(item.codeStr) + '\')</span>' : '') +
						'</div>';
					},
					option: function(item, escape) {
						return '<div>' +
							(item.label ? '<span class="description" style="font-weight: bold;">' + escape((item.label.length > 50) ? item.label.substring(1, 49) + "..." : item.label) + '</span>' : '') + '&nbsp;' +
							// (item.codeStr ? '<span class="code">(\'' + escape(item.codeStr) + '\')</span>' : '') +
						'</div>';
					}
				}
			});
  
  function isInt(n){
    return (typeof n !== 'undefined') && !isNaN(n) && Number(n) === n && n % 1 === 0;
  }

  function toFloat(n){
    
    if(typeof n !== 'undefined') {
    	if (typeof n === 'string' || n instanceof String) {
    		return parseFloat(n);
    	}
    	else if(isNaN(n)) {
    		return n;
    	}
    	else {
    		return n;
    	}
    }
    
    return n;
  }
  
  
  $('#resetButton').click(function() {
    var selectValueFDRName = $('#FDRCutoffLessThen').find(":selected").text();
  	var selectValueFDRadjName = $('#FDRadjCutoffLessThen').find(":selected").text();
  	var selectValueRriskName = $('#RRcutoffLessThen').find(":selected").text();
  	var selectValueCscoreName = $('#CScutoffLessThen').find(":selected").text();
  	var selectValueOddsRatioName = $('#OddsRatioCutoffLessThen').find(":selected").text();
  	var selectValuePhiName = $('#PhiCutoffLessThen').find(":selected").text();
  	var selectValueNumPatientsName = $('#NpatCutoffLessThen').find(":selected").text();
  
    // Clear data
    $('#FDRCutoffRes').html("&gt; <span style='color:#005A31;'>P-value cut-off (value range: [" + minFDR + ", " + maxFDR + "])</span>:&nbsp;" + selectValueFDRName + "&nbsp;No value selected");
	$('#FDRadjCutoffRes').html("&gt; <span style='color:#005A31;'>Adjusted p-value cut-off (value range: [" + minFDRadj + ", " + maxFDRadj + "])</span>:&nbsp;" + selectValueFDRadjName + "&nbsp;No value selected");
	$('#RRCutoffRes').html("&gt; <span style='color:#005A31;'>Relative risk cut-off (value range: [" + minRrisk + ", " + maxRrisk + "])</span>:&nbsp;" + selectValueRriskName + "&nbsp;No value selected");
	$('#PhiCutoffRes').html("&gt; <span style='color:#005A31;'>Phi cut-off (value range: [" + minPhi + ", " + maxPhi + "])</span>:&nbsp;" + selectValuePhiName + "&nbsp;No value selected");
	$('#CSCutoffRes').html("&gt; <span style='color:#005A31;'>Comorbidity score cut-off (value range: [" + minCscore + ", " + maxCscore + "])</span>:&nbsp;" + selectValueCscoreName + "&nbsp;No value selected");
	$('#OddsRatioCutoffRes').html("&gt; <span style='color:#005A31;'>Odds ratio cut-off (value range: [" + minOddsRatio + ", " + maxOddsRatio + "])</span>:&nbsp;" + selectValueOddsRatioName + "&nbsp;No value selected");
	$('#NpatCutoffRes').html("&gt; <span style='color:#005A31;'>Number of patients suffering the comorbidity (value range: [" + minNumPatients + ", " + maxNumPatients + "])</span>:&nbsp;" + selectValueNumPatientsName + "&nbsp;No value selected");
	$('#diseaseSelectorRes').html("&gt; <span style='color:#005A31;'>Only comorbidity pairs with diagnosis</span>:&nbsp;No value selected");
	
  	$("#tableDivFiltered").tabulator("clearData");
  	
  	$("#heatMapDiv").html("");
  	$('#paramHeatmapRes').html("");
  	<#if isGenderEnabled == "true">$("#heatMapSRDiv").html("");</#if>
  	$("#networkDiv").html("");
  	$("#networkTable").height("0px");
  	$("#networkTable").html("");
  	$('#paramNetworkRes').html("");
  	
  	$("#recordSelectedREs").html("Please, properly set filters and other visualization parameters in the 'Interactive filter setting' box, then click 'Analyze!'");	
  	
  });
  
  $('#analyzeButton').click(function() {
  	// Clear data table filtered
  	$("#tableDivFiltered").tabulator("clearData");
  	$("#heatMapDiv").html("");
  	<#if isGenderEnabled == "true">$("#heatMapSRDiv").html("");</#if>
  	$("#networkDiv").html("");
  	$("#networkTable").html("");
  	
  	// Update data
  	var FDRCutoff = $('#FDRCutoff').slider("option", "value");
  	var FDRadjCutoff = $('#FDRadjCutoff').slider("option", "value");
	var RRCutoff = $('#RRCutoff').slider("option", "value");
	var PhiCutoff = $('#PhiCutoff').slider("option", "value");
	var CSCutoff = $('#CSCutoff').slider("option", "value");
	var oddsRatioIndexCutoff = $('#OddsRatioCutoff').slider("option", "value");
	var NpatCutoff = $('#NpatCutoff').slider("option", "value");
	
	FDRCutoff = toFloat(FDRCutoff);
	FDRadjCutoff = toFloat(FDRadjCutoff);
	RRCutoff = toFloat(RRCutoff);
	PhiCutoff = toFloat(PhiCutoff);
	CSCutoff = toFloat(CSCutoff);
	NpatCutoff = toFloat(NpatCutoff);
	oddsRatioIndexCutoff = toFloat(oddsRatioIndexCutoff);
	
	// alert("FDRadjCutoff " + FDRadjCutoff + ", RRCutoff " + RRCutoff + ", PhiCutoff " + PhiCutoff + ", CSCutoff " + CSCutoff + ", NpatCutoff " + NpatCutoff + ", oddsRatioIndexCutoff " + oddsRatioIndexCutoff);
	
	var selectValueFDR = $('#FDRCutoffLessThen').find(":selected").val();
	var selectValueFDRadj = $('#FDRadjCutoffLessThen').find(":selected").val();
  	var selectValueRrisk = $('#RRcutoffLessThen').find(":selected").val();
  	var selectValueCscore = $('#CScutoffLessThen').find(":selected").val();
  	var selectValueOddsRatio = $('#OddsRatioCutoffLessThen').find(":selected").val();
  	var selectValuePhi = $('#PhiCutoffLessThen').find(":selected").val();
  	var selectValueNumPatients = $('#NpatCutoffLessThen').find(":selected").val();
  	
  	var selectValueFDRName = $('#FDRCutoffLessThen').find(":selected").text();
  	var selectValueFDRadjName = $('#FDRadjCutoffLessThen').find(":selected").text();
  	var selectValueRriskName = $('#RRcutoffLessThen').find(":selected").text();
  	var selectValueCscoreName = $('#CScutoffLessThen').find(":selected").text();
  	var selectValueOddsRatioName = $('#OddsRatioCutoffLessThen').find(":selected").text();
  	var selectValuePhiName = $('#PhiCutoffLessThen').find(":selected").text();
  	var selectValueNumPatientsName = $('#NpatCutoffLessThen').find(":selected").text();
	
	if($('#FDRCutoffEnabled').is(":checked")) {
		$('#FDRCutoffRes').html("&gt; <span style='color:#005A31;'>P-value cut-off (value range: [" + minFDR + ", " + maxFDR + "])</span>:&nbsp;" + selectValueFDRName + "&nbsp;" + FDRCutoff);
	}
	else {
		$('#FDRCutoffRes').html("");
	}
	
	if($('#FDRadjCutoffEnabled').is(":checked")) {
		$('#FDRadjCutoffRes').html("&gt; <span style='color:#005A31;'>Adjusted p-value cut-off (value range: [" + minFDRadj + ", " + maxFDRadj + "])</span>:&nbsp;" + selectValueFDRadjName + "&nbsp;" + FDRadjCutoff);
	}
	else {
		$('#FDRadjCutoffRes').html("");
	}
	
	if($('#RRcutoffEnabled').is(":checked")) {
		$('#RRCutoffRes').html("&gt; <span style='color:#005A31;'>Relative risk cut-off (value range: [" + minRrisk + ", " + maxRrisk + "])</span>:&nbsp;" + selectValueRriskName + "&nbsp;" + RRCutoff);
	}
	else {
		$('#RRCutoffRes').html("");
	}
	
	if($('#PhiCutoffEnabled').is(":checked")) {
		$('#PhiCutoffRes').html("&gt; <span style='color:#005A31;'>Phi cut-off (value range: [" + minPhi + ", " + maxPhi + "])</span>:&nbsp;" + selectValuePhiName + "&nbsp;" + PhiCutoff);
	}
	else {
		$('#PhiCutoffRes').html("");
	}
	
	if($('#CSCutoffEnabled').is(":checked")) {
		$('#CSCutoffRes').html("&gt; <span style='color:#005A31;'>Comorbidity score cut-off (value range: [" + minCscore + ", " + maxCscore + "])</span>:&nbsp;" + selectValueCscoreName + "&nbsp;" + CSCutoff);
	}
	else {
		$('#CSCutoffRes').html("");
	}
	
	if($('#OddsRatioCutoffEnabled').is(":checked")) {
		$('#OddsRatioCutoffRes').html("&gt; <span style='color:#005A31;'>Odds ratio cut-off (value range: [" + minOddsRatio + ", " + maxOddsRatio + "])</span>:&nbsp;" + selectValueOddsRatioName + "&nbsp;" + oddsRatioIndexCutoff);
	}
	else {
		$('#OddsRatioCutoffRes').html("");
	}
	
	if($('#NpatCutoffEnabled').is(":checked")) {
		$('#NpatCutoffRes').html("&gt; <span style='color:#005A31;'>Number of patients suffering the comorbidity (value range: [" + minNumPatients + ", " + maxNumPatients + "])</span>:&nbsp;" + selectValueNumPatientsName + "&nbsp;" + NpatCutoff);
    }
	else {
		$('#NpatCutoffRes').html("");
	}
	
	var selectedDiseaseFilterElementInt = null;
	if($('#diseaseSelectorEnabled').is(":checked")) {
    	selectedDiseaseFilterElementInt = selectedDiseaseFilterElement;
    	
        if(selectedDiseaseFilterElementInt && selectedDiseaseFilterElementInt.label) {
             $('#diseaseSelectorRes').html("&gt; <span style='color:#005A31;'>Only comorbidity pairs with diagnosis</span>:&nbsp;" + selectedDiseaseFilterElementInt.label);
        }
	}
	else {
		$('#diseaseSelectorRes').html("");
	}
    
    // Min max values
    
  	tableDataFiltered = [];
  	
	for (i = 0; i < tableData.length; i++) {
		var selectedRecord = true;
		
		tableData[i].fisherTest = toFloat(tableData[i].fisherTest);
		if( $('#FDRCutoffEnabled').is(":checked") && (typeof tableData[i].fisherTest !== 'undefined') && (typeof selectValueFDR !== 'undefined') && 
			((selectValueFDR === "moreThan" && tableData[i].fisherTest < FDRCutoff) || (selectValueFDR === "lessThan" && tableData[i].fisherTest > FDRCutoff)) ) {
			selectedRecord = false;	
		}
		
		tableData[i].fisherTestAdjusted = toFloat(tableData[i].fisherTestAdjusted);
		if( $('#FDRadjCutoffEnabled').is(":checked") && (typeof tableData[i].fisherTestAdjusted !== 'undefined') && (typeof selectValueFDRadj !== 'undefined') && 
			((selectValueFDRadj === "moreThan" && tableData[i].fisherTestAdjusted < FDRadjCutoff) || (selectValueFDRadj === "lessThan" && tableData[i].fisherTestAdjusted > FDRadjCutoff)) ) {
			selectedRecord = false;	
		}
		
		tableData[i].relativeRiskIndex = toFloat(tableData[i].relativeRiskIndex);
		if( $('#RRcutoffEnabled').is(":checked") && (typeof tableData[i].relativeRiskIndex !== 'undefined') && (typeof selectValueRrisk !== 'undefined') && 
			((selectValueRrisk === "moreThan" && tableData[i].relativeRiskIndex < RRCutoff) || (selectValueRrisk === "lessThan" && tableData[i].relativeRiskIndex > RRCutoff)) ) {
			selectedRecord = false;	
		}
		
		tableData[i].phiIndex = toFloat(tableData[i].phiIndex);
		if( $('#PhiCutoffEnabled').is(":checked") && (typeof tableData[i].phiIndex !== 'undefined') && (typeof selectValuePhi !== 'undefined') && 
			((selectValuePhi === "moreThan" && tableData[i].phiIndex < PhiCutoff) || (selectValuePhi === "lessThan" && tableData[i].phiIndex > PhiCutoff)) ) {
			selectedRecord = false;	
		}

		tableData[i].score = toFloat(tableData[i].score);
		if( $('#CSCutoffEnabled').is(":checked") && (typeof tableData[i].score  !== 'undefined') && (typeof selectValueCscore !== 'undefined') && 
			((selectValueCscore === "moreThan" && tableData[i].score < CSCutoff) || (selectValueCscore === "lessThan" && tableData[i].score > CSCutoff)) ) {
			selectedRecord = false;	
		}
		
		tableData[i].oddsRatioIndex = toFloat(tableData[i].oddsRatioIndex);
		if( $('#OddsRatioCutoffEnabled').is(":checked") && (typeof tableData[i].oddsRatioIndex  !== 'undefined') && (typeof selectValueOddsRatio !== 'undefined') && 
			((selectValueOddsRatio === "moreThan" && tableData[i].oddsRatioIndex < oddsRatioIndexCutoff) || (selectValueOddsRatio === "lessThan" && tableData[i].oddsRatioIndex > oddsRatioIndexCutoff)) ) {
			selectedRecord = false;	
		}

		tableData[i].patWdisAB = toFloat(tableData[i].patWdisAB);
		if( $('#NpatCutoffEnabled').is(":checked") && (typeof tableData[i].patWdisAB !== 'undefined') && (typeof selectValueNumPatients !== 'undefined') && 
			((selectValueNumPatients === "moreThan" && tableData[i].patWdisAB < NpatCutoff) || (selectValueNumPatients === "lessThan" && tableData[i].patWdisAB > NpatCutoff)) ) {
			selectedRecord = false;	
		}
		
		if($('#diseaseSelectorEnabled').is(":checked") && selectedDiseaseFilterElementInt && selectedDiseaseFilterElementInt.codeInt) {
			if( (typeof tableData[i].disAcodeNum === 'undefined' || tableData[i].disAcodeNum !== selectedDiseaseFilterElementInt.codeInt) &&
				(typeof tableData[i].disBcodeNum === 'undefined' || tableData[i].disBcodeNum !== selectedDiseaseFilterElementInt.codeInt) ) {
				selectedRecord = false;	
			}
		}
		
		if(selectedRecord) {
			tableDataFiltered.push(tableData[i]);
		}
	}
	
	// Check if to continue
	if(typeof tableDataFiltered !== 'undefined' && tableDataFiltered.length > 250) {
		$("#tooManyPairsAlertNumSel").html(tableDataFiltered.length + "");
		$("#tooManyPairsAlertNumTot").html(tableData.length + "");
		$("#tooManyPairsAlert").dialog("open");
	}
	else {
		updateTableHeatMapNetwork();
	}
	
  });
  
  // Reset interactive viz data
  setAllSexData(true);

});

</script>

<!-- //// NAV CODE - Start //// -->
<div class="site-container">
  <div class="site-pusher">
    
    <header class="header">
      
     <a href="http://comorbidity4j.readthedocs.io/en/latest/" class="header__icon" id="header__icon" target="_blank"></a>
     <a href="http://comorbidity4j.readthedocs.io/en/latest/" class="header__logo" target="_blank">C4J</a> <!-- <span>Analysis ID: ${executorObj.execID!'---'} (${execDate!'N/A'})</span> -->
     <#if isGenderEnabled == "true"><img title="Settings" onclick="$('#changeSexSelection').dialog('open');" border="0" alt="Settings" src="${baseCSS_JSpath!''}img/settings.ico" width="16" height="16" style="margin-left:10px;cursor:pointer;"></#if>
     
     <nav id="navMenu" class="menu">
     	<a class="topMenuItem" scrollId="infoMessageScroll" >General info</a>
        <a class="topMenuItem" scrollId="inputScroll" >Input params</a>
        <#if (errorMsg)??><a class="topMenuItem" scrollId="errorScroll" >Error message</a></#if>
        <a class="topMenuItem" scrollId="logScroll" >Processing log</a>
        <a class="topMenuItem" scrollId="datasetScroll" >Patient data overview</a>
        <#if isGenderEnabled == "true"><a class="topMenuItem" scrollId="sexRatioScroll" >Sex ratio analysis</a></#if>
        <a class="topMenuItem" scrollId="fullResultScroll" >Comorbidity table</a>
        <a class="topMenuItem" scrollId="visualizationScroll" >Comorbidity interactive viz</a>
        <a class="topMenuItem" scrollId="contactScroll" >Info and contact</a>
      </nav>
      
    </header>

    <div class="site-content">
      <div class="top-nav">
<!-- //// NAV CODE - End ////-->


<!-- SECTION: General information -->
<div id="infoMessageScroll" class="scrollSection">
  <span class="mainTitle">Comorbidity analysis results: general info</span><br/>
  <div style="text-align:left;">
	  
	  <div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
	  Comorbidity analysis with ID: <b>${execID!'---'}</b>, executed in date: <b>${execDate!'N/A'}</b>
	  </div>
	  
	  <#if filterResultMessage??>
	  <div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
	  ${filterResultMessage!'---'}
	  </div>
	  </#if>
	  
	  <#if notFileStorage??>
	  
	  <#if isOnline??>
		  <div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
		  From the following link you can download:
		  <ul>
		  <li>a self-contained ZIP file including this stand-alone web visualization and the results of the comorbidity analysis in CSV format: <a href="results?eid=${execID!'---'}&type=zip" target="_blank">here</a></li>
		  <li>a CSV file including all the results of the comorbidity analysis: <a href="results?eid=${execID!'---'}&type=csvALL" target="_blank">here</a></li>
		  <#if isGenderEnabled == "true">
	  	   <li>a CSV file including all the results of the comorbidity analysis for female patients: <a href="results?eid=${execID!'---'}&type=csvFEMALE" target="_blank">here</a></li>
		   <li>a CSV file including all the results of the comorbidity analysis for male patients: <a href="results?eid=${execID!'---'}&type=csvMALE" target="_blank">here</a></li>
	  	  </#if>
		  </ul>
		  </div>
		  <div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
		  <span style="color:red;">To preserve privacy, all the results of this comorbidity analysis will be deleted from the server after 24 hours from the execution of the analysis.</span><br/>
		  Deletion is scheduled on: ${generationDate!'---'}<br/>
		  You can trigger the immediate deletion of all the results of this comorbidity analysis by clicking <a href="results?eid=${execID!'---'}&action=delete" target="_blank">here</a>.
		  </div>
	  <#else>
	  	<#if storageFullName??>
	  	<div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
		  From the following link you can download:
		  <ul>
		  <li>a self-contained ZIP file including this stand-alone web visualization and the results of the comorbidity analysis in CSV format: <a href="results?eid=${execID!'---'}&type=zip" target="_blank">here</a></li>
		  <li>a CSV file including all the results of the comorbidity analysis: <a href="results?eid=${execID!'---'}&type=csvALL" target="_blank">here</a></li>
		  <#if isGenderEnabled == "true">
	  	   <li>a CSV file including all the results of the comorbidity analysis for female patients: <a href="results?eid=${execID!'---'}&type=csvFEMALE" target="_blank">here</a></li>
		   <li>a CSV file including all the results of the comorbidity analysis for male patients: <a href="results?eid=${execID!'---'}&type=csvMALE" target="_blank">here</a></li>
	  	  </#if>
		  </ul>
		</div>
		<div style="border: 1px black solid; background-color: white; padding: 5px; margin-bottom: 5px;" class="fileStoreInfoTD">
		  The results you're visualizing in this page are temporarly stored in the local files: ${storageFullName!'---'}.<br/>
		  You can trigger the immediate deletion of all the results of this comorbidity analysis by clicking <a href="results?eid=${execID!'---'}&action=delete" target="_blank">here</a>.
		</div>
	    </#if>
	  </#if>
	  
	  
	  </#if>
  </div>
</div>

<!-- SECTION: Input data -->
<div id="inputScroll" class="scrollSection">
  <span class="mainTitle">Input parameters</span><br/>
  
  <div class="container">
	  The following set of tabs collects the parameters used to perform the comorbidity analysis:
	  <div id="accordionInitParams">
	  
	  <h3>Patient Data input settings</h3>
	  <div style="height: 400px; overflow:scroll;">
	    <ul>
	       <li><b>Patient Data file name</b>:&nbsp; ${executorObj.userInputCont.patientDataFileName_PD!'---'}</li>
	       <li><b>Patient Data file size</b>:&nbsp; ${executorObj.userInputCont.patientDataFileSize_PD?c!'---'}&nbsp;Mb approx.</li>
	       <li><b>Column Name of patientData file, column patient_id</b>:&nbsp; ${executorObj.userInputCont.patientIDcolumn_PD!'---'}</li>
	       <li><b>Column Name of patientData file, column patient_dateBirth</b>:&nbsp; ${executorObj.userInputCont.patientBirthDateColumn_PD!'---'}</li>
	       <#if isGenderEnabled == "true">
	  	   <li><b>Column Name of patientData file, column patient_gender</b>:&nbsp; ${executorObj.userInputCont.patientGenderColumn_PD!'---'}</li>
	  	   <#else>
	  	   <li><b>No patient gender is considered in comorbidity analysis.</b></li>
	  	   </#if>
	  	   <#if executorObj.userInputCont.patientFacet1column_PD != "__SELECT_OPTIONAL__">
	  		 <#if executorObj.userInputCont.patientFacet1column_PD?length &gt; 0><li><b>Column Name of patientData file, column patient_facet_1</b>:&nbsp; ${executorObj.userInputCont.patientFacet1column_PD!'---'}</li></#if>
  		   </#if>
	       <li><b>Patient Data date format</b>:&nbsp; ${executorObj.userInputCont.dateFormat_PD!'---'}</li>
	       <li><b>Patient Data column separator</b>:&nbsp; '${executorObj.userInputCont.columnSeparatorChar_PD!'---'}'</li>
	       <li><b>Patient Data text delimiter</b>:&nbsp; ${executorObj.userInputCont.columnTextDelimiterChar_PD!'---'}</li>
	       <li><b>Patient Data has first row header</b>:&nbsp; ${executorObj.userInputCont.hasFirstRowHeader_PD?then('Yes', 'No')!'---'}</li>
	       <li><b>Patient Data is OMOP Common Data Model compliant</b>:&nbsp; ${executorObj.userInputCont.isOMOP_PD()?then('Yes', 'No')!'---'}</li>
	    </ul>
	  </div>
	  
	  <h3>Visit Data input settings</h3>
	  <div>
	    <ul>
	       <li><b>Visit Data file name</b>:&nbsp; ${executorObj.userInputCont.visitDataFileName_VD!'---'}</li>
	       <li><b>Visit Data file size</b>:&nbsp; ${executorObj.userInputCont.visitDataFileSize_VD?c!'---'}&nbsp;Mb approx.</li>
	       <li><b>Column Name of Visit Data file, column patient_id</b>:&nbsp; ${executorObj.userInputCont.patientIDcolumn_VD!'---'}</li>
	       <li><b>Column Name of Visit Data file, column visit_id</b>:&nbsp; ${executorObj.userInputCont.visitIDcolumn_VD!'---'}</li>
	       <li><b>Column Name of Visit Data file, column visitStartDate</b>:&nbsp; ${executorObj.userInputCont.visitStartDateColumn_VD!'---'}</li>
	       <li><b>Visit Data date format</b>:&nbsp; ${executorObj.userInputCont.dateFormat_VD!'---'}</li>
	       <li><b>Visit Data column separator</b>:&nbsp; '${executorObj.userInputCont.columnSeparatorChar_VD!'---'}'</li>
	       <li><b>Visit Data text delimiter</b>:&nbsp; ${executorObj.userInputCont.columnTextDelimiterChar_VD!'---'}</li>
	       <li><b>Visit Data has first row header</b>:&nbsp; ${executorObj.userInputCont.hasFirstRowHeader_VD?then('Yes', 'No')!'---'}</li>
	       <li><b>Visit Data is OMOP Common Data Model compliant</b>:&nbsp; ${executorObj.userInputCont.isOMOP_VD()?then('Yes', 'No')!'---'}</li>
	    </ul>
	  </div>
	  
	  <h3>Diagnosis Data input settings</h3>
	  <div>
	    <ul>
	       <li><b>Diagnosis Data file name</b>:&nbsp; ${executorObj.userInputCont.data_fileName_diagnosisData!'---'}</li>
	       <li><b>Diagnosis Data file size</b>:&nbsp; ${executorObj.userInputCont.diagnosisDataFileSize_DD?c!'---'}&nbsp;Mb approx.</li>
	       <li><b>Column Name of Diagnosis Data file, column patient_id</b>:&nbsp; ${executorObj.userInputCont.patientIDcolumn_DD!'---'}</li>
	       <li><b>Column Name of Diagnosis Data file, column visit_id</b>:&nbsp; ${executorObj.userInputCont.visitIDcolumn_DD!'---'}</li>
	       <li><b>Column Name of Diagnosis Data file, column diagnosis_code</b>:&nbsp; ${executorObj.userInputCont.diagnosisCodeColumn_DD!'---'}</li>
	       <li><b>Diagnosis Data column separator</b>:&nbsp; '${executorObj.userInputCont.columnSeparatorChar_DD!'---'}'</li>
	       <li><b>Diagnosis Data text delimiter</b>:&nbsp; ${executorObj.userInputCont.columnTextDelimiterChar_DD!'---'}</li>
	       <li><b>Diagnosis Data has first row header</b>:&nbsp; ${executorObj.userInputCont.hasFirstRowHeader_DD?then('Yes', 'No')!'---'}</li>
	       <li><b>Diagnosis Data is OMOP Common Data Model compliant</b>:&nbsp; ${executorObj.userInputCont.isOMOP_DD()?then('Yes', 'No')!'---'}</li>
	    </ul>
	  </div>
	  
	  <h3>Diagnosis Description input settings</h3>
	  <div>
	    <ul>
	       <li><b>Diagnosis Description Data file name</b>:&nbsp; ${executorObj.userInputCont.descrDiagnosisDataFileName_DDE!'---'}</li>
	       <li><b>Diagnosis Description Data file size</b>:&nbsp; ${executorObj.userInputCont.descrDiagnosisDataFileSize_DDE?c!'---'}&nbsp;Mb approx.</li>
	       <li><b>Column Name of Diagnosis Description file, column diagnosis_code</b>:&nbsp; ${executorObj.userInputCont.diagnosisDescriptionColumn_DDE!'---'}</li>
	       <li><b>Column Name of Diagnosis Description file, column diagnosis_description</b>:&nbsp; ${executorObj.userInputCont.diagnosisCodeColumn_DDE!'---'}</li>
	       <li><b>Diagnosis Description Data column separator</b>:&nbsp; '${executorObj.userInputCont.columnSeparatorChar_DDE!'---'}'</li>
	       <li><b>Diagnosis Description Data text delimiter</b>:&nbsp; ${executorObj.userInputCont.columnTextDelimiterChar_DDE!'---'}</li>
	       <li><b>Diagnosis Description Data has first row header</b>:&nbsp; ${executorObj.userInputCont.hasFirstRowHeader_DDE?then('Yes', 'No')!'---'}</li>
	    </ul>
	  </div>
	  
	  <h3>Disease Grouping settings</h3>
	  <div>
	    <ul>
	       <li><b>Number of groups of diseases defined</b>:&nbsp; ${disGroup_numGroups!'---'}</li>
	       <#if (disGroup_groupNames)??>
	       <li><b>List of names of disease groups defined</b>:&nbsp; ${disGroup_groupNames!'---'}</li>
	       </#if>
	       
	    </ul>
	  </div>
	  
	  <h3>Disease Pairing settings</h3>
	  <div>
	    <ul>
	       <li><b>Number of disease pairing patterns defined</b>:&nbsp; ${disPair_numPairingPatterns!'---'}</li>
	       <li><b>Number of disease pairs to study</b>:&nbsp; ${disPair_numPairsToStudy!'---'}</li>
	       <li><b>Comments</b>:&nbsp; ${disPair_comments!'---'}</li>
	    </ul>
	  </div>
	  
	  <h3>Other parameters</h3>
	  <div>
	    <ul>
	       <li><b>Enable multithreading</b>:&nbsp; ${executorObj.getNumThreads()?c!'---'}</li>
	       <li><b>Patient age computation approach (used if the patient age filter is enabled)</b>:&nbsp; ${executorObj.patientAgeComputation!'---'}</li>
	       <li><b>P-value adjust approach</b>:&nbsp; ${executorObj.pvalAdjApproach!'---'}</li>
	       <li><b>Relative risk confidence interval</b>:&nbsp; ${RRconfidenceInterval!'---'}</li>
	       <li><b>Odds ratio confidence interval</b>:&nbsp; ${ORconfidenceInterval!'---'}</li>
	       <#if isGenderEnabled == "true">
	  		 <li><b>Female identifier to compute sex ratio value</b>:&nbsp; ${executorObj.sexRatioFemaleIdentifier!'---'}</li>
	         <li><b>Male identifier to compute sex ratio value</b>:&nbsp; ${executorObj.sexRatioMaleIdentifier!'---'}</li>
	  	   <#else>
	  		 <li><b>No patient gender is considered in comorbidity analysis.</b></li>
	  	   </#if>
	       
	    </ul>
	  </div>
	  
	  <h3>Filters used to generate the analysis</h3>
	  <div>
	    <ul>
	       <li><b>Patient filter</b>:&nbsp; ${patient_filter!'---'}</li>
	       <li><b>Time directionality filter</b>:&nbsp; ${directionality_filter!'---'}</li>
	       <li><b>Scores filter</b>:&nbsp; ${score_filter!'---'}</li>
	    </ul>
	  </div>
	  
	  </div>
  </div>
  
</div>


<!-- SECTION: Error message -->
<#if (errorMsg)??>
<div id="errorScroll" class="scrollSection">
  <span class="mainTitle">Error message</span><br/>
  The following error message was generated while performing comorbidigy analysis:<br/>
  <div style="text-align:left;">
	${errorMsg!'No errors.'}
  </div>
</div>
</#if>

<!-- SECTION: Processing log -->
<div id="logScroll" class="scrollSection">
  <span class="mainTitle">Processing log</span><br/>
  Below you can find the log of the comorbidity data loading and analysis:
  
  <div id="accordionLogParams">
	  	  
	  <#if (dataLoadProcLogMsg)??>
	  <h3>Global log messages</h3>
	  <div style="height: 400px; overflow: auto;">
	      
		  <div class="content" style="height: 400px; overflow: auto;">
			<code><pre>${dataLoadProcLogMsg!'No errors.'}</pre></code>
		  </div>
		  
	  </div>
	  </#if>
	  
	  <#if (patientDataLog)??>
	  <h3>Patient Data loading log</h3>
	  <div style="height: 400px; overflow: auto;">
	      
		  <div class="content" style="height: 400px; overflow: auto;">
			<code><pre>${patientDataLog!'No errors.'}</pre></code>
		  </div>
		  
	  </div>
	  </#if>
	  
	  <#if (visitDataLog)??>
	  <h3>Visit Data loading log</h3>
	  <div style="height: 400px; overflow: auto;">
	      
		  <div class="content" style="height: 400px; overflow: auto;">
			<code><pre>${visitDataLog!'No errors.'}</pre></code>
		  </div>
		  
	  </div>
	  </#if>
	  
	  <#if (diagnosisDataLog)??>
	  <h3>Diagnosis Data loading log</h3>
	  <div style="height: 400px; overflow: auto;">
	      
		  <div class="content" style="height: 400px; overflow: auto;">
			<code><pre>${diagnosisDataLog!'No errors.'}</pre></code>
		  </div>
		  
	  </div>
	  </#if>
	  
	  <#if (descrDiagnosisDataLog)??>
	  <h3>Diagnosis Description Data loading log</h3>
	  <div style="height: 400px; overflow: auto;">
	      
		  <div class="content" style="height: 400px; overflow: auto;">
			<code><pre>${descrDiagnosisDataLog!'No errors.'}</pre></code>
		  </div>
		  
	  </div>
	  </#if>
	  
  </div>
  
  
</div>

<!-- SECTION: Patient data overview -->
<div id="datasetScroll" class="scrollSection" style="overflow:auto;">
  <span class="mainTitle">Patient data overview</span><br/>
  Below you can find a collection of charts useful to explore the features of the group of patient data you provided as input for comorbidity analysis. To explore the results of the comorbidity analysis 
  over this set of patients, refer to the <a onClick="scrollToId('fullResultScroll');">complete list of comorbidity pairs</a>.<br/>
  <#if (patient_filter)??>
  Important: you defined the following set of criteria to select / filter patients:&nbsp; ${patient_filter!'---'} for comorbidity analysis.<br/>
  <b>The charts shown below provide an overview of all the patient data of your input dataset / files WITHOUT applying any filter!</b><br/>
  </#if>
  
  
  <div class="graphGroupDiv">
  	  <!-- Patient count by sex chart -->
	  <div id="patCountBySexChart" class="graphDiv"  style="width:320px;">
	  </div>
	  
	  <!-- Patient count by classification chart -->
	  <div id="patCountByClassificationChart" class="graphDiv" style="width:320px;">
	  </div>
  </div>
  
  
  
  <div class="graphTitleDiv">Patients by age charts</div>
 
  <div class="graphGroupDiv">
	  <!-- Patient by age / sex chart - FIRST_ADMISSION -->
	  <div id="patByAgeSexChart_FIRST_ADMISSION" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / sex chart FIRST_DIAGNOSTIC -->
	  <div id="patByAgeSexChart_FIRST_DIAGNOSTIC" class="graphDiv">
	  </div>
	  
  	  <!-- Patient by age / sex chart LAST_ADMISSION -->
	  <div id="patByAgeSexChart_LAST_ADMISSION" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / sex chart LAST_DIAGNOSTIC -->
	  <div id="patByAgeSexChart_LAST_DIAGNOSTIC" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / sex chart EXECUTION_TIME -->
	  <div id="patByAgeSexChart_EXECUTION_TIME" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / classification chart - FIRST_ADMISSION -->
	  <div id="patByAgeClassificationChart_FIRST_ADMISSION" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / classification chart - FIRST_DIAGNOSTIC -->
	  <div id="patByAgeClassificationChart_FIRST_DIAGNOSTIC" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / classification chart - LAST_ADMISSION -->
	  <div id="patByAgeClassificationChart_LAST_ADMISSION" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / classification chart - LAST_DIAGNOSTIC -->
	  <div id="patByAgeClassificationChart_LAST_DIAGNOSTIC" class="graphDiv">
	  </div>
	  
	  <!-- Patient by age / classification chart - EXECUTION_TIME -->
	  <div id="patByAgeClassificationChart_EXECUTION_TIME" class="graphDiv">
	  </div>
  </div>
  
  
  
  <div class="graphTitleDiv">Patients by birth-date charts</div>
  
  <div class="graphGroupDiv">
	  <!-- Patient by birth date and gender -->
	  <div id="patientByBirthDateAndSexChart" class="graphDiv">
	  </div>
	  
	  <!-- Patient by birth date and classification -->
	  <div id="patientByBirthDateAndClassificationChart" class="graphDiv">
	  </div>
  </div>

  
  
  <div class="graphTitleDiv">Patients by disease charts</div>
  
  <div class="graphGroupDiv">
	  <!-- Patient count by disease and sex chart - only studied diseases 
	  <div id="patientCountByDiseaseAndSexOnlyIndexChart" class="graphDiv">
	  </div>
	  -->
	  
	  <!-- Patient count by disease and classification chart - only studied diseases -->
	  <div id="patientCountByDiseaseAndClassificationOnlyIndexChart" class="graphDiv">
	  </div>
	  
	  <!-- Patient count by disease and sex chart -->
	  <div id="patientCountByDiseaseAndSexChart" class="graphDiv">
	  </div>
	  
	  <!-- Patient count by disease and classification chart -->
	  <div id="patientCountByDiseaseAndClassificationChart" class="graphDiv">
	  </div>
  </div>

  
  
  <div class="graphTitleDiv">Visits by disease charts</div>
  
  <div class="graphGroupDiv"> 
	  <!-- Visit count by disease and sex chart - only studied diseases 
	  <div id="visitCountByDiseaseAndSexOnlyIndexChart" class="graphDiv">
	  </div>
	  -->
	  
	  <!-- Visit count by disease and classification chart - only studied diseases -->
	  <div id="visitCountByDiseaseAndClassificationOnlyIndexChart" class="graphDiv">
	  </div>
	  
	  <!-- Visit count by disease and sex chart -->
	  <div id="visitCountByDiseaseAndSexChart" class="graphDiv">
	  </div>
	  
	  <!-- Visit count by disease and classification chart -->
	  <div id="visitCountByDiseaseAndClassificationChart" class="graphDiv">
	  </div>
  </div>
  
</div>

<#if isGenderEnabled == "true">
<!-- SECTION: Sex ratio table -->
<div id="sexRatioScroll" class="scrollSection">
    <span class="mainTitle">Sex ratio analysis</span><br/>
    The following table shows the sex ratio of all pairs of diseases analyzed.
    <div class="directionalityCell">
	    <b>BA Sex Ratio column</b>: given all individuals with disease A: (i) > 0 means prevalence of disease B in females; 
	    (ii) < 0 means prevalence of disease B in males; (iii) close to 0 means disease B is equally likely for females and males.
		<br/> <b>AB Sex Ratio column</b>: given all individuals with disease B: (i) > 0 means prevalence of disease A in females; 
	    (ii) < 0 means prevalence of disease A in males; (iii) close to 0 means disease A is equally likely for females and males.
    </div>
    <!-- Table: complete results -->
	<div class="resultDivSR" style="overflow: auto;">
	<div id="tableDivSR"></div>
	<button id="downloadTableDivSR" type="button">Download as CSV</button>
	</div>
	
</div>
</#if>

<!-- SECTION: Full result table -->
<div id="fullResultScroll" class="scrollSection">
  <span class="mainTitle">Comorbidity list</span><br/>
  The following table shows the results of the comorbidity analysis: each row describe a pair of diseases.<br/>
  <div class="directionalityCell">
  The input dataset has been filtered by means of the following criteria (if any):
  <ul>
    <li><b>Patient filter</b>:&nbsp; ${patient_filter!'---'}</li>
	<li><b>Time directionality filter</b>:&nbsp; ${directionality_filter!'---'}</li>
	<li><b>Scores filter</b>:&nbsp; ${score_filter!'---'}</li>
  </ul>
  resulting in <b><span id="totComorPairs">-</span></b> disease pairs shown in the following table and evaluated with respect to the relevance of their comorbidity
  (<a onClick="scrollToId('inputScroll');">see Input parameter section to review the input data used</a>).<br/>
  </div>
  You can click on the header of each column to order disease pairs with respect to a specific comorbidity score. 
  It is possible to resize columns as well as to change the order of each column by clicking on its header and draggin it. 
  
  <#if isGenderEnabled == "true">
  <div id="sexSelection_1" class="sexInfo">You're considering both FEMALE and MALE</div>
  </#if>
  
  <!-- Table: complete results -->
  <div class="resultDiv" style="overflow: auto;">
  <div id="tableDiv"></div>
  <button id="downloadTableDiv" type="button">Download as CSV</button>
  </div>
</div>


<!-- SECTION: Filtered results - interactive comorbidity visualizations -->
<div id="visualizationScroll" class="scrollSection">
  <span class="mainTitle">Interactive visualizations</span><br/>
  From this tab you can interactively define criteria to further filter the <a onClick="scrollToId('fullResultScroll');">complete list of 
  comorbidity pairs</a>.<br/>
  The filtered set of comorbidity pairs will be visualized by means of a table and a heatmap to visually suport data exploration and analysis.
  
  <#if isGenderEnabled == "true">
  <div id="sexSelection_2" class="sexInfo">You're considering both FEMALE and MALE</div>
  </#if>
  
  <div id="tooManyPairsAlert" title="Too many diagnosis pairs to visualize!" style="z-index:50000;">
    Due to the high number of pairs that match filters (<b><span id="tooManyPairsAlertNumSel">X</span></b> pairs selected by the filter over <span id="tooManyPairsAlertNumTot">X</span>), 
    your interactive visualizations could be difficult to explore and slow to generate.<br/><br/>
    As a general rule, we suggest to keep the number of disease pairs to interactively explore under 250 otherwise browser may slow down considerably.<br/><br/>
    As a consequence, we suggest to click on the 'Set more restrictive filters' button, change you filter to make them more restrictive, then trgger data visualization again.</p>
  </div>
  
  <#if isGenderEnabled == "true">
  <div id="changeSexSelection" title="Modify patient sex selection" style="z-index:50000;">
    <span id="sexInfoPopUp">-</span>
  </div>
  </#if>
  
  <div class="controlDivExt">
	  <h3>Interactive filter setting</h3>
	  Click on the checkbox to enable / disable each filter, then modify the filter parameters by clicking on the pencil icon.<br/>
	  <span id="alertNumPatients"></span>
	  <div id="controls" class="controlDiv">
	    <div id="FDRCutoffCont" class="controlElem">
		<div id="FDRCutoffTitle" class="sliderTitle">
		<input id="FDRCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="FDRValRangeSelInt">P-value cut-off:</span>
		<select id="FDRCutoffLessThen">
	      <option value="lessThan" selected>Equal or LOWER than</option>
	      <option value="moreThan">Equal or GREATER than</option>
	   	</select>
		<span id="FDRCutoffSel"></span>
		</div>
		<div id="FDRCutoffTitleExt" class="sliderTitleExt" title="P-value cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="FDRCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="FDRCutoffSelExt"></span> <br/> Value range: <span id="FDRValRangeSelExt"></span></div>
	      <div id="FDRCutoff" class="slider"></div>
	    </div>
	    </div>
	    	    
	    <div id="FDRadjCutoffCont" class="controlElem">
		<div id="FDRadjCutoffTitle" class="sliderTitle">
		<input id="FDRadjCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="FDRadjValRangeSelInt" >Adj. p-value cut-off:</span>
		<select id="FDRadjCutoffLessThen">
	      <option value="lessThan" selected>Equal or LOWER than</option>
	      <option value="moreThan">Equal or GREATER than</option>
	   	</select>
		<span id="FDRadjCutoffSel"></span>
		</div>
		<div id="FDRadjCutoffTitleExt" class="sliderTitleExt" title="Adjusted p-value cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="FDRadjCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="FDRadjCutoffSelExt"></span> <br/> Value range: <span id="FDRadjValRangeSelExt"></span></div>
	      <div id="FDRadjCutoff" class="slider"></div>
	    </div>
	    </div>
	
	    <div id="RRCutoffCont" class="controlElem">
		<div id="RRCutoffTitle" class="sliderTitle">
	    <input id="RRcutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="RRValRangeSelInt" >Rel. risk cut-off:</span>
		<select id="RRcutoffLessThen">
	        <option value="lessThan">Equal or LOWER than</option>
	        <option value="moreThan" selected>Equal or GREATER than</option>
	    </select>
	    <span id="RRCutoffSel"></span>
		</div>
		<div id="RRCutoffTitleExt" class="sliderTitleExt" title="Relative risk cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="RRCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="RRCutoffSelExt"></span> <br/> Value range: <span id="RRValRangeSelExt"></span></div>
		  <div id="RRCutoff" class="slider"></div>
		</div>
	    </div>
	
	    <div id="PhiCutoffCont" class="controlElem">
		<div id="PhiCutoffTitle" class="sliderTitle">
		<input id="PhiCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="PhiValRangeSelInt" >Phi cut-off:</span>
		<select id="PhiCutoffLessThen">
	        <option value="lessThan">Equal or LOWER than</option>
	        <option value="moreThan" selected>Equal or GREATER than</option>
	    </select>
	    <span id="PhiCutoffSel"></span>
		</div>
		<div id="PhiCutoffTitleExt" class="sliderTitleExt" title="Phi cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="PhiCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="PhiCutoffSelExt"></span> <br/> Value range: <span id="PhiValRangeSelExt"></span></div>
		  <div id="PhiCutoff" class="slider"></div>
		</div>
	    </div>
	
	    <div id="CSCutoffCont" class="controlElem">
		<div id="CSCutoffTitle" class="sliderTitle">
		<input id="CSCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="CSValRangeSelInt" >Comor. score cut-off:</span>
		<select id="CScutoffLessThen">
	        <option value="lessThan">Equal or LOWER than</option>
	        <option value="moreThan" selected>Equal or GREATER than</option>
	    </select>
	    <span id="CSCutoffSel"></span>
		</div>
		<div id="CSCutoffTitleExt" class="sliderTitleExt" title="Comorbidity score cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="CSCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="CSCutoffSelExt"></span> <br/> Value range: <span id="CSValRangeSelExt"></span></div>
		  <div id="CSCutoff" class="slider"></div>
		</div>
	    </div>
	    
	    <div id="OddsRatioCutoffCont" class="controlElem">
		<div id="OddsRatioCutoffTitle" class="sliderTitle">
		<input id="OddsRatioCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="OddsRatioCutoffSelInt" >Odds ratio cut-off:</span>
		<select id="OddsRatioCutoffLessThen">
	        <option value="lessThan">Equal or LOWER than</option>
	        <option value="moreThan" selected>Equal or GREATER than</option>
	    </select>
	    <span id="OddsRatioCutoffSel"></span>
		</div>
		<div id="OddsRatioCutoffTitleExt" class="sliderTitleExt" title="Odds ratio cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="OddsRatioCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="OddsRatioCutoffSelExt"></span> <br/> Value range: <span id="OddsRatioValRangeSelExt"></span></div>
		  <div id="OddsRatioCutoff" class="slider"></div>
		</div>
	    </div>
	
	    <div id="NpatCutoffCont" class="controlElem">
		<div id="NpatCutoffTitle" class="sliderTitle">
		<input id="NpatCutoffEnabled" type="checkbox" />
		<a class="editValue">
			<img border="0" alt="Change filter" src="${baseCSS_JSpath!''}img/edit.ico" width="16" height="16" style="cursor:pointer;">
		</a>
		<span id="NpatCutoffSelInt" >Num. of patients cut-off:</span>
		<select id="NpatCutoffLessThen">
	        <option value="lessThan">Equal or LOWER than</option>
	        <option value="moreThan" selected>Equal or GREATER than</option>
	    </select>
	    <span id="NpatCutoffSel"></span>
		</div>
		<div id="NpatCutoffTitleExt" class="sliderTitleExt" title="Num. of patients cut-off">
		  <div class="sliderTitle" style="height:30px;">(use mouse or left / right arrows to change slider value)</div>
		  <div id="NpatCutoffVariability" class="sliderTitle" style="margin-bottom: 35px;">Current selection: <span id="NpatCutoffSelExt"></span> <br/> Value range: <span id="NumPatientsValRangeSelExt"></span></div>
		  <div id="NpatCutoff" class="slider"></div>
		</div>
	    </div>
	    
	    <!--
	    <div class="controlElem" sytle="height:auto;">
	      <input id="diseaseSelectorEnabled" type="checkbox" />
	      Pairs with diagnosis:&nbsp
		  <input id="diseaseSelector" type="text"/>
		  <input id="diseaseSelectorHiddenID" type="hidden" />
		  <p id="diseaseSelectorDescription"></p>
	    </div> 
	    -->
	    
	    <div class="controlElem" sytle="height:auto;">
	      <input id="diseaseSelectorEnabled" type="checkbox" />
	      Pairs with diagnosis:&nbsp
	      <span id="diseaseSelectionTextTrigger" class="buttonStyleAnalyze" style="margin: 0px; padding: 0px; background-color: transparent;">SELECT</span>
	      <span id="diseaseSelectorText" style="font-style: italic;">No disease selected</span>
	    </div> 
	    
	    
	    
	    <div id="controls" class="controlElem excludedResizeElem" style="height:15px;">
	      <div id="parHeatmapTitle" class="sliderTitle">Parameter to visualize in the heatmap: 
		      <select id="parHeatmapSel">
		        <option value="rr">Relative risk</option>
		        <option value="phi" selected>Phi</option>
		        <option value="sc">Score</option>
		        <option value="fdr">P-value</option>
				<option value="fdrAdj">P-value adjusted</option>
				<option value="or">Odds ratio</option>
		      </select>
	      </div>
	    </div>
	    
	  </div>
	
	  <div class="buttonDiv">
	    <button id="analyzeButton" type="button" class="buttonStyleAnalyze">Analyze!</button>
	    <button id="resetButton" type="button" class="buttonStyleReset">Reset</button>
	  </div>
	</div>
	
	<!-- Results -->
	<div id="analysisResultDivExt" class="resultDivExt" style="background-color: #CDECDE;">
	<h3>Results:</h3>
	
	<div id="recordSelected" class="resCellContainer">
	  <span id="recordSelectedREs" class="resCell" style="color:red;">Please, properly set filters and other visualization parameters in the 'Interactive filter setting' box, then click 'Analyze!'</span>
	  <br/>
	  
	  <div style="left; margin-top:10px; margin-bottom: 10px; margin-left:15px; float:none; display: inline-block;">
		  <span id="filterTitle" class="resCell">List of applied filters:</span>
		  <span id="FDRCutoffRes" class="resCell">No filter applied</span>
		  <span id="FDRadjCutoffRes" class="resCell">No filter applied</span>
		  <span id="RRCutoffRes" class="resCell">No filter applied</span>
		  <span id="PhiCutoffRes" class="resCell">No filter applied</span>
		  <span id="CSCutoffRes" class="resCell">No filter applied</span>
		  <span id="OddsRatioCutoffRes" class="resCell">No filter applied</span>
		  <span id="NpatCutoffRes" class="resCell">No filter applied</span>
		  <span id="diseaseSelectorRes" class="resCell">No filter applied</span>
		  <#if isGenderEnabled == "true">
		  <div id="sexSelection_4" class="sexInfo">You're considering both FEMALE and MALE</div>
		  </#if>
	  </div>
	  <br/>
	  <div style="left; margin-top:5px; margin-bottom: 10px; float:none; display: inline-block;">
		  <span id="filterTableTitle" class="resCell" style="float:none;">Table of filtered comorbidities:
		  <#if (directionality_filter_numDays > 0) >
		  <div class="directionalityCell">
		  <b>Time directionality is considered in the analysis of comorbidities.</b>
		  <br/>For each comorbidity pair shown in the table, the first temporal occurring disease is the disease A, followed in time by the disease B.
		  </div>
		  </#if>
		  </span><br/>
		  <div class="resultDiv" style="overflow: auto;">
			<div id="tableDivFiltered"></div>
			<button id="downloadTableDivFiltered" type="button">Download as CSV</button>
		  </div>
	  </div>
	</div>
	
	<!-- Filter status 
	<div id="cutoffCont" class="resCellContainer">
	</div>
	-->
	
	<!-- Table: filtered results 
	<div id="filteredTableCont" class="resCellContainer">
	</div>
	-->
	
	<!-- Heatmap - ALL -->
	<div id="heatmapCont" class="resCellContainer">
	  <span id="filterHeatmapTitle" class="resCell" style="float:none;">Heatmap visualization of filtered comorbidities:
	  <#if (directionality_filter_numDays > 0) >
	  <div class="directionalityCell">
	  <b>Time directionality is considered in the analysis of comorbidities</b>
	  <br/>For each comorbidity pair shown in the heatmap, consider as first temporal occurring disease the one on the y-axis (disease A), 
	  followed in time by the corresponding disease on the x-axis (disease B).
	  </div>
	  </#if>
	  </span><br/>
	  <span id="paramHeatmapRes" class="resCell" style="float:none;"></span><br/>
	  <div id="heatMapDiv" class="resultDiv">
      </div>
    </div>
    
    <!-- Network -->
	<div id="heatmapCont" class="resCellContainer">
	  <span id="filterNetworkTitle" class="resCell" style="float:none;">Network visualization of filtered comorbidities <br/> (zoom with mouse scroll wheel and drag & drop nodes to explore the network):
	  <#if (directionality_filter_numDays > 0) >
	  <div class="directionalityCell">
	  <b>Time directionality is considered in the analysis of comorbidities</b>
	  <br/>The arrow of the ark that connects a pair of diseases represents their time directionality (the arrow points to the second disease occurring in time).
	  </div>
	  </#if>
	  </span><br/>
	  <span id="paramNetworkRes" class="resCell" style="float:none;"></span><br/>
	  <div id="networkTable" class="resultDiv resNetwork" style="height: 0px; overflow: scroll;">
      </div>
      <div id="networkDiv" class="resultDiv resNetwork">
      </div>
    </div>
    
    <!-- Heatmap - SEX RATIO -->
    <#if isGenderEnabled == "true">
	<div id="heatmapSRCont" class="resCellContainer">
	  <span id="filterHeatmapSRTitle" class="resCell" style="float:none;">Heatmap visualization of sex ratio of the filtered disease pairs:
	  <div class="directionalityCell">
	   For each comorbidity pair shown in the heatmap, a sex-ratio value close to zero indicates that the co-occurrence of the disease on the x-axis,
	   given a patient suffering the corresponding disease on the y-axis, is equally likely for males and females. A positive (negative) value of sex ratio indicate that 
	   the co-occurrence of the disease on the x-axis, given a patient suffering the corresponding disease on the y-axis, is more likely for females (males). 
	   Thus if the sex ratio has positive values the diagnosis of the x-axis disease in patients that have been diagnosed with the y-axis disease is more likely in females than males. 
	  </div>
	  </span><br/>
	  <div id="heatMapSRDiv" class="resultDiv">
      </div>
    </div>
    </#if>
    
  </div>
  

<!-- SECTION: Info and contact -->
<div id="contactScroll" class="scrollSection">
  <span class="mainTitle">Info and contact</span><br/>
  This analysis and visualizations have been performed by relying on <b><a href="http://comorbidity4j.readthedocs.io/en/latest/" target="_blank">Comorbidity4j</a></b>,
  a java library useful to carry out comorbidity analyses. Detailed documentation and practical examples are available online at:<br/>
  <a href="http://comorbidity4j.readthedocs.io/" target="_blank">http://comorbidity4j.readthedocs.io/</a>
  <br/><br/><br/>
  
  Comorbidity4j is developed and maintained by the:<br/>
  <b><a href="http://grib.imim.es/research/integrative-biomedical-informatics/index.html" target="_blank">Integrative Biomedical Informatics Group</a></b><br/>
  part of the Research Programme on Biomedical Informatics (GRIB), a joint research programme of the 
  <a href="http://www.imim.es/" target="_blank">Hospital del Mar Medical Research Institute (IMIM)</a> and the Department of Experimental and Health Sciences of 
  the <a href="http://www.upf.edu/" target="_blank">Universitat Pompeu Fabra</a> in Barcelona.  <br/><br/>
  
  If you need any support in using the tool or if you want to provide us with feedback and suggestions, please send an email to <a href="mailto:francesco.ronzano@upf.edu?Subject=Comorbidity4j issue" target="_top">francesco&lt;DOT&gt;ronzano&lt;AT&gt;upf&lt;DOT&gt;edu</a>.
  
</div>
  
</div>

	<!-- Diagnosis selection dialogue -->
 	<div id="dialog" title="Diagnosis selection">
		Please, select a diagnosis:<br/>
		
		<div id="selectedSel" class="diagSelectorGroup-c4j" style="background-color:white;margin-top: 10px;">
			<div id="selectedSelDiv" style="margin-top: 5px;">
			<select id="selected-to" placeholder="No diagnosis selected."></select>
			</div>
		</div>
	</div>



<!-- //// NAV CODE - Start //// -->
		</div> <!-- END container -->
      </div> <!-- END site-content -->
    <div class="site-cache" id="site-cache"></div>
  </div> <!-- END site-pusher -->
</div> <!-- END site-container -->
<!-- //// NAV CODE - End //// -->

</body>