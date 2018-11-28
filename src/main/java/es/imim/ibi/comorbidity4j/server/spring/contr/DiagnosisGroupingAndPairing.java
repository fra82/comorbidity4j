package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharSource;

import es.imim.ibi.comorbidity4j.loader.DataLoadContainer;
import es.imim.ibi.comorbidity4j.loader.DescriptionDataLoader;
import es.imim.ibi.comorbidity4j.loader.DiagnosisDataLoader;
import es.imim.ibi.comorbidity4j.loader.PatientDataLoader;
import es.imim.ibi.comorbidity4j.loader.VisitDataLoader;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.comorbidity4j.util.stat.AdjMethodENUM;

@Controller
public class DiagnosisGroupingAndPairing extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DiagnosisGroupingAndPairing.class);
	
	private static DecimalFormat fileSizeFormatter = new DecimalFormat("##0.00000");
	
	@GetMapping(value = "/diagnosisGrouping")
	public String diseaseGrouping(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		// TO REMOVE!!!!!!!
		// so = getArtificialUserInputContainer();
		
		request.getSession().setAttribute("so", so);
		
		so.resetDiagnosisGroupingData();
		
		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before specifying Diagnosis Grouping! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}

		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before specifying Diagnosis Grouping! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}

		if(so.getDiagnosisData_LOADED() == null || so.getDiagnosisData_LOADED().data == null || so.getDiagnosisData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Diagnosis Data file before specifying Diagnosis Grouping! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 3, "STEP A: upload file", so);
			return "diagnosisData_1_specifyCSV";
		}



		// Generate diagnosis list object (for JSON)
		List<Map<String, String>> diagnosisList = new ArrayList<Map<String, String>>();
		Map<String, String> diagnosisListIndex = new HashMap<String, String>();
		Map<Integer, String> diagnosisCodeIdStringMapInversed = new HashMap<Integer, String>();
		if(so.getDiagnosisCodeIdStringMap() != null) {
			diagnosisCodeIdStringMapInversed = so.getDiagnosisCodeIdStringMap().entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		}

		if(so.getDiagnosisData_LOADED() != null && so.getDiagnosisData_LOADED().data != null) {
			for(Visit vis : so.getDiagnosisData_LOADED().data) {
				if(vis != null && vis.getDiagnosisCodeSet() != null && vis.getDiagnosisCodeSet().size() > 0) {
					for(Integer diagIntegerCode : vis.getDiagnosisCodeSet()) {
						if(diagIntegerCode != null && diagnosisCodeIdStringMapInversed.containsKey(diagIntegerCode) && !Strings.isEmpty(diagnosisCodeIdStringMapInversed.get(diagIntegerCode))) {
							String diagStringCode = diagnosisCodeIdStringMapInversed.get(diagIntegerCode);
							if(diagnosisListIndex.containsKey(diagStringCode)) {
								continue;
							}
							diagnosisListIndex.put(diagStringCode, 
									(so.getDescrDiagnosisData_LOADED().data != null && so.getDescrDiagnosisData_LOADED().data.containsKey(diagStringCode) && !Strings.isEmpty(so.getDescrDiagnosisData_LOADED().data.get(diagStringCode))) 
									? so.getDescrDiagnosisData_LOADED().data.get(diagStringCode) : "");

							Map<String, String> diagnosisListEntry = new HashMap<String, String>();
							diagnosisListEntry.put("code", diagStringCode);
							diagnosisListEntry.put("description", (so.getDescrDiagnosisData_LOADED().data != null && so.getDescrDiagnosisData_LOADED().data.containsKey(diagStringCode) && !Strings.isEmpty(so.getDescrDiagnosisData_LOADED().data.get(diagStringCode))) 
									? so.getDescrDiagnosisData_LOADED().data.get(diagStringCode) : "");
							diagnosisList.add(diagnosisListEntry);
						} 
					}
				}

			}
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			model.put("diagnosisList", mapper.writeValueAsString(diagnosisList));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error(" > EXCEPTION while creating diagnosisList: " + e.getMessage());
		}
		try {
			model.put("diagnosisListIndex", mapper.writeValueAsString(diagnosisListIndex));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error(" > EXCEPTION while creating diagnosisListIndex: " + e.getMessage());
		}

		System.gc();		
		
		setMenu(model, 5, "Select groups", so);
		return "diagnosisGrouping";
	}


	@GetMapping(value = "/diagnosisPairing")
	public String diagnosisPairingg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before specifying Diagnosis Pairing Patterns! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}

		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before specifying Diagnosis Pairing Patterns! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}

		if(so.getDiagnosisData_LOADED() == null || so.getDiagnosisData_LOADED().data == null || so.getDiagnosisData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Diagnosis Data file before specifying Diagnosis Pairing Patterns! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 3, "STEP A: upload file", so);
			return "diagnosisData_1_specifyCSV";
		}

		// Generate diagnosis list object (for JSON)
		// Get all diagnoses included in a group
		Map<String, String> diagnosisStringCodeGroupNameMap = new HashMap<String, String>();
		for(Entry<String, Set<String>> groupListDiagCodesEntry : so.getGroupNameListCodesMap().entrySet()) {
			if(groupListDiagCodesEntry != null && !Strings.isEmpty(groupListDiagCodesEntry.getKey()) &&
					groupListDiagCodesEntry.getValue() != null && groupListDiagCodesEntry.getValue().size() > 0) {
				for(String diagCodeString : groupListDiagCodesEntry.getValue()) {
					if(!Strings.isEmpty(diagCodeString)) {
						diagnosisStringCodeGroupNameMap.put(diagCodeString, groupListDiagCodesEntry.getKey());
					}
				}
			}
		}

		// Define diagnoses list by considering groups of diagnoses
		List<Map<String, String>> diagnosisList = new ArrayList<Map<String, String>>();
		Map<String, String> diagnosisListIndex = new HashMap<String, String>();
		Map<Integer, String> diagnosisCodeIdStringMapInversed = new HashMap<Integer, String>();
		if(so.getDiagnosisCodeIdStringMap() != null) {
			diagnosisCodeIdStringMapInversed = so.getDiagnosisCodeIdStringMap().entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		}

		if(so.getDiagnosisData_LOADED() != null && so.getDiagnosisData_LOADED().data != null) {
			for(Visit vis : so.getDiagnosisData_LOADED().data) {
				if(vis != null && vis.getDiagnosisCodeSet() != null && vis.getDiagnosisCodeSet().size() > 0) {
					for(Integer diagIntegerCode : vis.getDiagnosisCodeSet()) {
						if(diagIntegerCode != null && diagnosisCodeIdStringMapInversed.containsKey(diagIntegerCode) && !Strings.isEmpty(diagnosisCodeIdStringMapInversed.get(diagIntegerCode))) {
							String diagStringCode = diagnosisCodeIdStringMapInversed.get(diagIntegerCode);

							if(diagnosisStringCodeGroupNameMap.containsKey(diagStringCode) && !Strings.isEmpty(diagnosisStringCodeGroupNameMap.get(diagStringCode))) {
								// Diagnosis in group
								String groupName = diagnosisStringCodeGroupNameMap.get(diagStringCode);
								if(diagnosisListIndex.containsKey(groupName)) {
									continue;
								}
								
								String groupDescription = "GROUP OF " + (
										(so.getGroupNameListCodesMap().containsKey(groupName) && so.getGroupNameListCodesMap().get(groupName) != null) ? " " + so.getGroupNameListCodesMap().get(groupName).size() : " ") + " DIAGNOSES";
								
								diagnosisListIndex.put(groupName, groupDescription);

								Map<String, String> diagnosisListEntry = new HashMap<String, String>();
								diagnosisListEntry.put("code", groupName);
								diagnosisListEntry.put("description", groupDescription);
								diagnosisList.add(diagnosisListEntry);

							}
							else {
								// Diagnosis not in group
								if(diagnosisListIndex.containsKey(diagStringCode)) {
									continue;
								}

								diagnosisListIndex.put(diagStringCode, 
										(so.getDescrDiagnosisData_LOADED().data != null && so.getDescrDiagnosisData_LOADED().data.containsKey(diagStringCode) && !Strings.isEmpty(so.getDescrDiagnosisData_LOADED().data.get(diagStringCode))) 
										? so.getDescrDiagnosisData_LOADED().data.get(diagStringCode) : "");

								Map<String, String> diagnosisListEntry = new HashMap<String, String>();
								diagnosisListEntry.put("code", diagStringCode);
								diagnosisListEntry.put("description", (so.getDescrDiagnosisData_LOADED().data != null && so.getDescrDiagnosisData_LOADED().data.containsKey(diagStringCode) && !Strings.isEmpty(so.getDescrDiagnosisData_LOADED().data.get(diagStringCode))) 
										? so.getDescrDiagnosisData_LOADED().data.get(diagStringCode) : "");
								diagnosisList.add(diagnosisListEntry);
							}
						} 
					}
				}
			}
		}


		ObjectMapper mapper = new ObjectMapper();
		try {
			model.put("diagnosisList", mapper.writeValueAsString(diagnosisList));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error(" > EXCEPTION while creating diagnosisList: " + e.getMessage());
		}
		try {
			model.put("diagnosisListIndex", mapper.writeValueAsString(diagnosisListIndex));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error(" > EXCEPTION while creating diagnosisListIndex: " + e.getMessage());
		}
		
		System.gc();

		setMenu(model, 6, "Select pairs", so);
		return "diagnosisPairing";
	}


	@PostMapping(value = "/diagnosisPairing")
	public String diagnosisPairingp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		so.resetDiagnosisGroupingData();

		// Get diagnosis grouping data and save in session object
		String jsonGroupStr = request.getParameter("jsonGroup");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Set<String>> groupNameListCodesMap = new HashMap<String, Set<String>>();

		try {
			Map<String, Object> groupMap = mapper.readValue(jsonGroupStr, new TypeReference<Map<String, Object>>() {});

			for(Entry<String, Object> groupMapElem : groupMap.entrySet()) {
				try {
					String groupStndName = groupMapElem.getKey();
					Set<String> codeSet = new HashSet<String>();
					String groupUserName = "";
					for(Entry<String, Object> groupMapElemInt : ((Map<String, Object>) groupMapElem.getValue()).entrySet()) {
						if(groupMapElemInt.getKey().equals("codes")) {
							for(String elem : ((List<String>) groupMapElemInt.getValue())) {
								codeSet.add(elem);
							}
						}

						if(groupMapElemInt.getKey().equals("name")) {
							groupUserName = (String) groupMapElemInt.getValue();
						}
					}

					if(codeSet != null && codeSet.size() > 0 && !Strings.isEmpty(groupUserName)) {
						groupNameListCodesMap.put(groupUserName, codeSet);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		so.setGroupNameListCodesMap(groupNameListCodesMap);

		setMenu(model, 6, "", so);
		return diagnosisPairingg(model, request);
	}



	@GetMapping(value = "/comorbidityParameters")
	public String comorbidityParametersg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}

		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}

		if(so.getDiagnosisData_LOADED() == null || so.getDiagnosisData_LOADED().data == null || so.getDiagnosisData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Diagnosis Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 3, "STEP A: upload file", so);
			return "diagnosisData_1_specifyCSV";
		}

		String errorMessage = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";
		
		if(so.isGenderEnabled()) {
			if(so.getPatientData_LOADED() != null && so.getPatientData_LOADED().data != null && so.getPatientData_LOADED().data.size() > 0) {
				Set<String> genederIdentifiers = new HashSet<String>();

				for(Patient pt : so.getPatientData_LOADED().data) {
					if(pt != null && !Strings.isEmpty(pt.getSex())) {
						genederIdentifiers.add(pt.getSex());
					}
				}

				List<String> femaleIdentifiers = new ArrayList<String>(genederIdentifiers);
				List<String> maleIdentifiers = new ArrayList<String>(genederIdentifiers);

				if(genederIdentifiers.size() > 1) {
					model.put("femaleIdentifierList", femaleIdentifiers);
					model.put("maleIdentifierList", maleIdentifiers);
				}
			}
			else {
				errorMessage += "Impossible to determine the set of gener identifiers used.<br/>";
			}
		}
		

		if(so.getPatientData_LOADED() != null && so.getPatientData_LOADED().data != null && so.getPatientData_LOADED().data.size() > 0) {
			Set<String> facetIdentifiers = new HashSet<String>();

			for(Patient pt : so.getPatientData_LOADED().data) {
				if(pt != null && !Strings.isEmpty(pt.getClassification1())) {
					facetIdentifiers.add(pt.getClassification1());
				}
			}

			List<String> facetIdentifiersList = new ArrayList<String>(facetIdentifiers);
			model.put("facetIdentifierList", facetIdentifiersList);
		}

		if(!Strings.isEmpty(errorMessage)) {
			model.put("errorMessage", errorMessage);
		}
		
		// TO DELETE
		/*
		List<String> femaleIdentifiers = new ArrayList<String>();
		List<String> maleIdentifiers = new ArrayList<String>();
		femaleIdentifiers.add("MALE");
		femaleIdentifiers.add("FEMALE");
		maleIdentifiers.add("MALE");
		maleIdentifiers.add("FEMALE");
		model.put("femaleIdentifierList", femaleIdentifiers);
		model.put("maleIdentifierList", maleIdentifiers);

		List<String> facetIdentifiersList = new ArrayList<String>();
		facetIdentifiersList.add("Facet1");
		facetIdentifiersList.add("Facet2");
		facetIdentifiersList.add("Facet3");
		model.put("facetIdentifierList", facetIdentifiersList);
		 */
		
		System.gc();

		setMenu(model, 7, "", so);
		return "comorbidityParameters";
	}


	@PostMapping(value = "/comorbidityParameters")
	public String comorbidityParametersp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		so.resetDiagnosisPairingData();


		// Get diagnosis pairing data and store in session object
		String jsonPairStr = request.getParameter("jsonPair");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Set<String>> groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
		Map<String, String> groupPairignMap = new HashMap<String, String>();

		try {
			Map<String, Object> groupMap = mapper.readValue(jsonPairStr, new TypeReference<Map<String, Object>>() {});

			if(groupMap.containsKey("diagnosisGroups")) {
				groupNameListCodesPairingMap = (Map<String, Set<String>>) groupMap.get("diagnosisGroups");
			}

			if(groupMap.containsKey("groupPairs")) {
				groupPairignMap = (Map<String, String>) groupMap.get("groupPairs");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		int numberOfDiagnosisPairsToAnalyze = 0;
		Map<String, Set<String>> diagPairingGlobalMap = new HashMap<String, Set<String>>();
		if(groupPairignMap != null && groupNameListCodesPairingMap != null && groupNameListCodesPairingMap.size() > 0) {
			
			for(Entry<String, String> groupNamePair : groupPairignMap.entrySet()) {
				if(groupNamePair != null && !Strings.isEmpty(groupNamePair.getKey()) && !Strings.isEmpty(groupNamePair.getValue()) &&
						groupNameListCodesPairingMap.containsKey(groupNamePair.getKey()) && groupNameListCodesPairingMap.get(groupNamePair.getKey()) != null &&
						groupNameListCodesPairingMap.containsKey(groupNamePair.getValue()) && groupNameListCodesPairingMap.get(groupNamePair.getValue()) != null
						) {
					Set<String> sourceGroupSet = new HashSet();
					sourceGroupSet.addAll(groupNameListCodesPairingMap.get(groupNamePair.getKey()));
					Set<String> destinationGroupSet = new HashSet();
					destinationGroupSet.addAll(groupNameListCodesPairingMap.get(groupNamePair.getValue()));
					if(destinationGroupSet.contains("__ALL_DIAGNOSES_SELECTED__")) {
						destinationGroupSet = new HashSet();
						for(Entry<String, Integer> diagnosisCodeIdStringMapEntry : so.getDiagnosisCodeIdStringMap().entrySet()) {
							if(diagnosisCodeIdStringMapEntry != null && !Strings.isEmpty(diagnosisCodeIdStringMapEntry.getKey())) {
								destinationGroupSet.add(diagnosisCodeIdStringMapEntry.getKey());
							}
						}
					}
					for(String diagnosisCodeS : sourceGroupSet) {
						if(!Strings.isEmpty(diagnosisCodeS)) {
							for(String diagnosisCodeD : destinationGroupSet) {
								if(!Strings.isEmpty(diagnosisCodeD)) {


									if(!diagnosisCodeS.equals(diagnosisCodeD)) {

										if( (diagPairingGlobalMap.get(diagnosisCodeS) != null && diagPairingGlobalMap.get(diagnosisCodeS).contains(diagnosisCodeD)) ||
												(diagPairingGlobalMap.get(diagnosisCodeD) != null && diagPairingGlobalMap.get(diagnosisCodeD).contains(diagnosisCodeS))) {
											// Do nothing
										}
										else {
											if(!diagPairingGlobalMap.containsKey(diagnosisCodeS)) {
												diagPairingGlobalMap.put(diagnosisCodeS, new HashSet<String>());
											}
											diagPairingGlobalMap.get(diagnosisCodeS).add(diagnosisCodeD);
										}
									}
								}
							}
						}
					}
				}
			}
			
			for(Entry<String, Set<String>> disPair : diagPairingGlobalMap.entrySet()) {
				if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
					numberOfDiagnosisPairsToAnalyze += disPair.getValue().size();
				}
			}
		}
		else {
			for(int i = 0; i < so.getDiagnosisCodeIdStringMap().size(); i++) {
				for(int j = i + 1; j < so.getDiagnosisCodeIdStringMap().size(); j++) {
					numberOfDiagnosisPairsToAnalyze++;
				}
			}
		}
		
		if(ServerExecConfig.isOnline && ServerExecConfig.maxNumberComorbidityPairsToAnalyze > 0l && ((long) numberOfDiagnosisPairsToAnalyze) > ServerExecConfig.maxNumberComorbidityPairsToAnalyze) {
			String checkErrors = "ATTENTION: the number of diagnosis paris to analyze is " + (numberOfDiagnosisPairsToAnalyze) + ",  greater than " + ServerExecConfig.maxNumberComorbidityPairsToAnalyze + ", that is the maximum number of diagnosis pairs allowed to process in this server.<br/>" +
					"Please, define diagnosis pairing patterns so as to reduce this number of diagnosis pairs to a value equal or lower than " + ServerExecConfig.maxNumberComorbidityPairsToAnalyze + ".<br/>"
					+ "The running instance of Comorbidity4web supports analysis of a number of diagnosis paris equal or smaller than " + ServerExecConfig.maxNumberComorbidityPairsToAnalyze + ".<br/><br/>"
					+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
					+ "to performe analysis of comorbidities over larger sets of data on your PC (wihhout this limitation)." + "<br/>";
			model.put("errorMessage", checkErrors);
			return diagnosisPairingg(model, request);
		}

		so.setGroupNameListCodesPairingMap(groupNameListCodesPairingMap);
		so.setGroupPairignMap(groupPairignMap);

		setMenu(model, 7, "", so);
		return comorbidityParametersg(model, request);
	}

	@GetMapping(value = "/startAnalysis")
	public String startAnalysisg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}

		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}

		if(so.getDiagnosisData_LOADED() == null || so.getDiagnosisData_LOADED().data == null || so.getDiagnosisData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Diagnosis Data file before specifying Comorbidity Analysis Parameters! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 3, "STEP A: upload file", so);
			return "diagnosisData_1_specifyCSV";
		}

		// Retrieve backtracked errors
		checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		if(!Strings.isEmpty(checkErrors)) {
			return comorbidityParametersg(model, request);
		}

		// Summary patient data
		model.put("fileName_PD", StringUtils.defaultIfEmpty(so.getPatientDataFileName_PD(), "-"));
		model.put("fileSize_PD", fileSizeFormatter.format(so.getPatientDataFileSize_PD()) + "");
		if(so.getPatientData_LOADED() != null) {
			model.put("skippedLine_PD", so.getPatientData_LOADED().skippedLine_PD);
			model.put("unparsableDate_PD", so.getPatientData_LOADED().unparsableDate_PD);
			model.put("duplicatedPatientID_PD", so.getPatientData_LOADED().duplicatedPatientID_PD);
			model.put("numberPatientsLoaded", (so.getPatientData_LOADED().data != null) ? so.getPatientData_LOADED().data.size() : 0);
		}


		// Summary visit data
		model.put("fileName_VD", StringUtils.defaultIfEmpty(so.getVisitDataFileName_VD(), "-"));
		model.put("fileSize_VD", fileSizeFormatter.format(so.getVisitDataFileSize_VD()) + "");
		if(so.getVisitData_LOADED() != null) {
			model.put("skippedLine_VD", so.getVisitData_LOADED().skippedLine_VD);
			model.put("unparsableVisitDate_VD", so.getVisitData_LOADED().unparsableVisitDate_VD);
			model.put("duplicatedVisitID_VD", so.getVisitData_LOADED().duplicatedVisitID_VD);
			model.put("numberVisitsLoaded", (so.getVisitData_LOADED().data != null) ? so.getVisitData_LOADED().data.size() : 0);
		}


		// Summary diagnosis data
		model.put("fileName_DD", StringUtils.defaultIfEmpty(so.getDiagnosisDataFileName_DD(), "-"));
		model.put("fileSize_DD", fileSizeFormatter.format(so.getDiagnosisDataFileSize_DD()) + "");
		if(so.getDiagnosisData_LOADED() != null) {
			model.put("skippedLine_DD", so.getDiagnosisData_LOADED().skippedLine_DD);
			model.put("duplicatedPatVisitDiagnosis_DD", so.getDiagnosisData_LOADED().duplicatedPatVisitDiagnosis_DD);
			model.put("unexistingPatientOrVisitID_DD", so.getDiagnosisData_LOADED().unexistingPatientOrVisitID_DD);
			int numberOfDiagnosesLoaded = ControllerUtil.countPatientDiagnoses(so);
			model.put("numberDiagnosisLoaded", numberOfDiagnosesLoaded + "");
		}


		// Summary about diagnosis description data
		model.put("fileName_DDE", StringUtils.defaultIfEmpty(so.getDescrDiagnosisDataFileName_DDE(), "-"));
		model.put("fileSize_DDE", fileSizeFormatter.format(so.getDescrDiagnosisDataFileSize_DDE()) + "");
		if(so.getDescrDiagnosisData_LOADED() != null) {
			model.put("skippedLine_DDE", so.getDescrDiagnosisData_LOADED().skippedLine_DDE);
			model.put("nullOrEmptyDiagnosisDescription_DDE", so.getDescrDiagnosisData_LOADED().nullOrEmptyDiagnosisDescription_DDE);
			model.put("numberDiagnosisDescrLoaded", (so.getDescrDiagnosisData_LOADED().data != null) ? so.getDescrDiagnosisData_LOADED().data.size() : 0);
		}

		// Summary of grouping and pairing
		model.put("patientAgeComputation", so.getPatientAgeComputation_p() != null ? so.getPatientAgeComputation_p() : "-");
		model.put("pvalAdjApproach", so.getPvalAdjApproach_p() != null ? so.getPvalAdjApproach_p() : "-");
		model.put("ORconfidenceInterval", so.getOddsRatioConfindeceInterval_p() != null ? so.getOddsRatioConfindeceInterval_p() : "-");
		model.put("isGenderEnabled", so.isGenderEnabled() + "");
		if(so.isGenderEnabled()) {
			model.put("sexRatioFemaleIdentifier", so.getFemaleIdentifier_p() != null ? so.getFemaleIdentifier_p() : "-");
			model.put("sexRatioMaleIdentifier", so.getMaleIdentifier_p() != null ? so.getMaleIdentifier_p() : "-");
		}
		model.put("patient_filter", so.getPatientFilter() != null ? so.getPatientFilter().toString(false, true) : "-");
		model.put("directionality_filter", so.getComorbidityDirectionalityFilter() != null ? so.getComorbidityDirectionalityFilter().toString(true) : "-");
		model.put("score_filter", so.getComorbidityScoreFilter() != null ? so.getComorbidityScoreFilter().toString(true) : "-");
		
		System.gc();
		
		setMenu(model, 8, "", so);
		return "startAnalysis";
	}

	@PostMapping(value = "/startAnalysis")
	public String startAnalysisp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		so.resetAnalysisParameters();

		String errorMessage = "";

		// Get diagnosis pairing data and store in session object
		String femaleIdentifier = request.getParameter("femaleIdentifier");
		if(!Strings.isEmpty(femaleIdentifier)) {
			so.setFemaleIdentifier_p(femaleIdentifier);
		}

		String maleIdentifier = request.getParameter("maleIdentifier");
		if(!Strings.isEmpty(maleIdentifier)) {
			so.setMaleIdentifier_p(maleIdentifier);
		}

		String isDirectional = request.getParameter("isDirectional");
		String directMinDays = request.getParameter("directMinDays");
		if(!Strings.isEmpty(isDirectional) && isDirectional.toLowerCase().trim().contains("enab")) {
			so.setIsDirectional_p(isDirectional);
			Integer directMinDaysInt = null;
			try {
				directMinDaysInt = Integer.valueOf(directMinDays);
			}
			catch(Exception e) {

			}
			if(directMinDaysInt != null && directMinDaysInt > 0) {
				so.setDirectMinDays_p(directMinDaysInt);
			}
			else {
				errorMessage += "Incorrect value for the minimum number of days between two diagnoses in directionality analysis - please provide a positive integer.<br/>";
			}
		}


		String patientAgeComputation = request.getParameter("patientAgeComputation");
		if(patientAgeComputation != null && patientAgeComputation.trim().length() > 0) {
			if(patientAgeComputation.trim().toLowerCase().equals(PatientAgeENUM.FIRST_ADMISSION.toString().toLowerCase())) {
				so.setPatientAgeComputation_p(PatientAgeENUM.FIRST_ADMISSION);
			}
			else if(patientAgeComputation.trim().toLowerCase().equals(PatientAgeENUM.FIRST_DIAGNOSTIC.toString().toLowerCase())) {
				so.setPatientAgeComputation_p(PatientAgeENUM.FIRST_DIAGNOSTIC);
			}
			else if(patientAgeComputation.trim().toLowerCase().equals(PatientAgeENUM.LAST_ADMISSION.toString().toLowerCase())) {
				so.setPatientAgeComputation_p(PatientAgeENUM.LAST_ADMISSION);
			}
			else if(patientAgeComputation.trim().toLowerCase().equals(PatientAgeENUM.LAST_DIAGNOSTIC.toString().toLowerCase())) {
				so.setPatientAgeComputation_p(PatientAgeENUM.LAST_DIAGNOSTIC);
			}
			else if(patientAgeComputation.trim().toLowerCase().equals(PatientAgeENUM.EXECUTION_TIME.toString().toLowerCase())) {
				so.setPatientAgeComputation_p(PatientAgeENUM.EXECUTION_TIME);
			}
		}


		String pvalAdjApproach = request.getParameter("pvalAdjApproach");
		if(pvalAdjApproach != null && pvalAdjApproach.trim().length() > 0) {
			if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.BENJAMINI_HOCHBERG.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.BENJAMINI_HOCHBERG);
			}
			else if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.BENJAMINI_YEKUTIELI.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.BENJAMINI_YEKUTIELI);
			}
			else if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.BONFERRONI.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.BONFERRONI);
			}
			else if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.HOCHBERG.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.HOCHBERG);
			}
			else if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.HOLM.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.HOLM);
			}
			else if(pvalAdjApproach.trim().toLowerCase().equals(AdjMethodENUM.HOMMEL.toString().toLowerCase())) {
				so.setPvalAdjApproach_p(AdjMethodENUM.HOMMEL);
			}
		}


		String minAgeCb = request.getParameter("minAgeCb_name");
		String FPATminAge = request.getParameter("FPATminAge");
		if(!Strings.isEmpty(minAgeCb)) {
			so.setMinAgeEnabled_p(true);
			Integer FPATminAgeInt = null;
			try {
				FPATminAgeInt = Integer.valueOf(FPATminAge);
			}
			catch(Exception e) {
				so.setMinAgeEnabled_p(false);
			}
			if(FPATminAgeInt != null && FPATminAgeInt > 0) {
				so.setFPATminAge_p(FPATminAgeInt);
			}
			else {
				errorMessage += "Incorrect value for the minimum age of patient filters - please provide a positive integer.<br/>";
			}
		}

		String maxAgeCb = request.getParameter("maxAgeCb_name");
		String FPATmaxAge = request.getParameter("FPATmaxAge");
		if(!Strings.isEmpty(maxAgeCb)) {
			so.setMaxAgeEnabled_p(true);
			Integer FPATmaxAgeInt = null;
			try {
				FPATmaxAgeInt = Integer.valueOf(FPATmaxAge);
			}
			catch(Exception e) {
				so.setMaxAgeEnabled_p(false);
			}
			if(FPATmaxAgeInt != null && FPATmaxAgeInt > 0) {
				so.setFPATmaxAge_p(FPATmaxAgeInt);
			}
			else {
				errorMessage += "Incorrect value for the maximum age of patient filters - please provide a positive integer.<br/>";
			}
		}

		if(so.isMinAgeEnabled_p() && so.isMaxAgeEnabled_p() && (so.getFPATminAge_p() >= so.getFPATmaxAge_p())) {
			errorMessage += "Incorrect value for the minimum / maximum age of patient filters - the min age value is greater or equal than the max age value.<br/>";
		}


		String FPATCb = request.getParameter("FPATCb_name");
		String[] checkedIds = request.getParameterValues("FPATclassification1");
		if(!Strings.isEmpty(FPATCb)) {
			so.setPatientFacetFilteringEnabled_p(true);

			if(checkedIds != null && checkedIds.length > 0) {
				so.setPatientFacetsInFilter_p(checkedIds);
			}
			else {
				errorMessage += "Please, select at least one facet among the patient filters or disable the patient facet filter.<br/>";
			}
		}


		String FCOMscoreCb = request.getParameter("FCOMscoreCb_name");
		String FCOMscoreEGID = request.getParameter("FCOMscoreEG");
		String FCOMscore = request.getParameter("FCOMscore");
		if(!Strings.isEmpty(FCOMscoreCb)) {
			so.setFCOMscoreEnabled_p(true);

			Double FCOMscoreDouble = null;
			try {
				FCOMscoreDouble = Double.valueOf(FCOMscore);
			}
			catch(Exception e) {
				so.setFCOMscoreEnabled_p(false);
			}

			if(FCOMscoreDouble != null && !Strings.isEmpty(FCOMscoreEGID)) {
				so.setFCOMscore_p(FCOMscoreDouble);
				so.setFCOMscoreGreaterLower_p(FCOMscoreEGID);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity score filter.<br/>";
			}
		}

		String FCOMrriskCb = request.getParameter("FCOMrriskCb_name");
		String FCOMrriskEG = request.getParameter("FCOMrriskEG");
		String FCOMrrisk = request.getParameter("FCOMrrisk");
		if(!Strings.isEmpty(FCOMrriskCb)) {
			so.setFCOMrriskEnabled_p(true);

			Double FCOMrriskDouble = null;
			try {
				FCOMrriskDouble = Double.valueOf(FCOMrrisk);
			}
			catch(Exception e) {
				so.setFCOMrriskEnabled_p(false);
			}

			if(FCOMrriskDouble != null && !Strings.isEmpty(FCOMrriskEG)) {
				so.setFCOMrrisk_p(FCOMrriskDouble);
				so.setFCOMrriskGreaterLower_p(FCOMrriskEG);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity relative risk filter.<br/>";
			}
		}

		String FCOModdsRatioCb = request.getParameter("FCOModdsRatioCb_name");
		String FCOModdsRatioEG = request.getParameter("FCOModdsRatioEG");
		String FCOModdsRatio = request.getParameter("FCOModdsRatio");
		if(!Strings.isEmpty(FCOModdsRatioCb)) {
			so.setFCOModdsRatioEnabled_p(true);

			Double FCOModdsRatioDouble = null;
			try {
				FCOModdsRatioDouble = Double.valueOf(FCOModdsRatio);
			}
			catch(Exception e) {
				so.setFCOModdsRatioEnabled_p(false);
			}

			if(FCOModdsRatioDouble != null && !Strings.isEmpty(FCOModdsRatioEG)) {
				so.setFCOModdsRatio_p(FCOModdsRatioDouble);
				so.setFCOModdsRatioGreaterLower_p(FCOModdsRatioEG);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity odds ratio filter.<br/>";
			}
		}


		String FCOMphiCb = request.getParameter("FCOMphiCb_name");
		String FCOMphiEG = request.getParameter("FCOMphiEG");
		String FCOMphi = request.getParameter("FCOMphi");
		if(!Strings.isEmpty(FCOMphiCb)) {
			so.setFCOMphiEnabled_p(true);

			Double FCOMphiDouble = null;
			try {
				FCOMphiDouble = Double.valueOf(FCOMphi);
			}
			catch(Exception e) {
				so.setFCOMphiEnabled_p(false);
			}

			if(FCOMphiDouble != null && !Strings.isEmpty(FCOMphiEG)) {
				so.setFCOMphi_p(FCOMphiDouble);
				so.setFCOMphiGreaterLower_p(FCOMphiEG);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity phi filter.<br/>";
			}
		}

		String FCOMfisherAdjCb = request.getParameter("FCOMfisherAdjCb_name");
		String FCOMfisherAdjEG = request.getParameter("FCOMfisherAdjEG");
		String FCOMfisherAdj = request.getParameter("FCOMfisherAdj");
		if(!Strings.isEmpty(FCOMfisherAdjCb)) {
			so.setFCOMfisherAdjEnabled_p(true);

			Double FCOMfisherAdjDouble = null;
			try {
				FCOMfisherAdjDouble = Double.valueOf(FCOMfisherAdj);
			}
			catch(Exception e) {
				so.setFCOMfisherAdjEnabled_p(false);
			}

			if(FCOMfisherAdjDouble != null && !Strings.isEmpty(FCOMfisherAdjEG)) {
				so.setFCOMfisherAdj_p(FCOMfisherAdjDouble);
				so.setFCOMfisherAdjGreaterLower_p(FCOMfisherAdjEG);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity adjusted Fisher filter.<br/>";
			}
		}

		String FCOMminPatCb = request.getParameter("FCOMminPatCb_name");
		String FCOMminPatEG = request.getParameter("FCOMminPatEG");
		String FCOMminPat = request.getParameter("FCOMminPat");
		if(!Strings.isEmpty(FCOMminPatCb)) {
			so.setFCOMminPatEnabled_p(true);

			Integer FCOMminPatInteger = null;
			try {
				FCOMminPatInteger = Integer.valueOf(FCOMminPat);
			}
			catch(Exception e) {
				so.setFCOMminPatEnabled_p(false);
			}

			if(FCOMminPatInteger != null && !Strings.isEmpty(FCOMminPatEG)) {
				so.setFCOMminPat_p(FCOMminPatInteger);
				so.setFCOMminPatGreaterLower_p(FCOMminPatEG);
			}
			else {
				errorMessage += "Incorrect value for the comorbidity minimum number of patients filter.<br/>";
			}
		}

		String oddsRatioConfidenceInterval = request.getParameter("oddsRatioConfidenceInterval");
		if(oddsRatioConfidenceInterval == null) {
			errorMessage += "Please, specify a correct confidence interval to compute the odds ratio in ]0;1[.<br/>";
		}
		else {
			Double oddsRatioConfidenceIntervalDouble = null;
			try {
				oddsRatioConfidenceIntervalDouble = Double.valueOf(oddsRatioConfidenceInterval);
			}
			catch(Exception e) {
				errorMessage += "Please, specify a correct confidence interval to compute the odds ratio in ]0;1[.<br/>";
			}

			if(oddsRatioConfidenceIntervalDouble <= 0d || oddsRatioConfidenceIntervalDouble >= 1d) {
				errorMessage += "Please, specify a correct confidence interval to compute the odds ratio in ]0;1[.<br/>";
			}
			else {
				so.setOddsRatioConfindeceInterval_p(oddsRatioConfidenceIntervalDouble);
			}
		}


		if(!Strings.isEmpty(errorMessage)) {
			model.put("errorMessage", errorMessage);
		}

		setMenu(model, 8, "", so);
		return startAnalysisg(model, request);
	}


	public static UserInputContainer getSynthea1kArtificialUserInputContainer(boolean isGenderEnabled) {
		UserInputContainer retUserInputContainer = new UserInputContainer();
		
		retUserInputContainer.setGenderEnabled(isGenderEnabled);
		
		// Patient data
		Class<? extends DiagnosisGroupingAndPairing> classRef = new DiagnosisGroupingAndPairing().getClass();
		try {
			InputStream is = classRef.getResourceAsStream("/Synthea1k/patients.txt");
			retUserInputContainer.setPatientData_PD(IOUtils.toString(is, "UTF-8")); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		retUserInputContainer.setPatientDataFileSize_PD(-1l);
		retUserInputContainer.setPatientDataFileName_PD("patientData.csv");
		retUserInputContainer.setPatientIDcolumn_PD("patient_id");
		retUserInputContainer.setPatientBirthDateColumn_PD("patient_dateBirth");
		retUserInputContainer.setPatientGenderColumn_PD("patient_gender");
		retUserInputContainer.setPatientFacet1column_PD("race");
		retUserInputContainer.setColumnSeparatorChar_PD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_PD('N');
		retUserInputContainer.setHasFirstRowHeader_PD(true);
		retUserInputContainer.setDateFormat_PD("yyyy/MM/dd");
		retUserInputContainer.setOMOP_PD(false);

		PatientDataLoader pd_loader = new PatientDataLoader();
		pd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			DataLoadContainer<List<Patient>> loadedData = pd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getPatientData_PD()).openStream(), retUserInputContainer.isGenderEnabled());
			retUserInputContainer.setPatientData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Visit data
		try {
			InputStream is = classRef.getResourceAsStream("/Synthea1k/visits.txt");
			retUserInputContainer.setVisitData_VD(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setVisitDataFileSize_VD(-1l);
		retUserInputContainer.setVisitDataFileName_VD("admissionData.csv");
		retUserInputContainer.setPatientIDcolumn_VD("patient_id");
		retUserInputContainer.setVisitIDcolumn_VD("visit_id");
		retUserInputContainer.setVisitStartDateColumn_VD("visit_date");
		retUserInputContainer.setColumnSeparatorChar_VD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_VD('N');
		retUserInputContainer.setHasFirstRowHeader_VD(true);
		retUserInputContainer.setDateFormat_VD("yyyy/MM/dd");
		retUserInputContainer.setOMOP_VD(false);

		VisitDataLoader vd_loader = new VisitDataLoader();
		vd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			DataLoadContainer<List<Visit>> loadedData = vd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getVisitData_VD()).openStream());
			retUserInputContainer.setVisitData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Diagnosis data
		try {
			InputStream is = classRef.getResourceAsStream("/Synthea1k/diagnoses.txt");
			retUserInputContainer.setDiagnosisData_DD(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setDiagnosisDataFileSize_DD(-1l);
		retUserInputContainer.setDiagnosisDataFileName_DD("diagnosisData.csv");
		retUserInputContainer.setPatientIDcolumn_DD("patient_id");
		retUserInputContainer.setVisitIDcolumn_DD("visit_id");
		retUserInputContainer.setDiagnosisCodeColumn_DD("diagnosis_code");
		retUserInputContainer.setColumnSeparatorChar_DD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_DD('N');
		retUserInputContainer.setHasFirstRowHeader_DD(true);
		retUserInputContainer.setOMOP_DD(false);

		DiagnosisDataLoader dd_loader = new DiagnosisDataLoader();
		dd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			Map<String, Integer> diagnosisCodeIdStringMap = new HashMap<String, Integer>();
			DataLoadContainer<List<Visit>> loadedData = dd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getDiagnosisData_DD()).openStream(),
					retUserInputContainer.getVisitData_LOADED().data, diagnosisCodeIdStringMap);

			retUserInputContainer.setDiagnosisData_LOADED(loadedData);
			retUserInputContainer.setDiagnosisCodeIdStringMap(diagnosisCodeIdStringMap);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Diagnosis description data
		try {
			InputStream is = classRef.getResourceAsStream("/Synthea1k/diagnosisDescriptions.txt");
			retUserInputContainer.setDescrDiagnosisData_DDE(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setDescrDiagnosisDataFileSize_DDE(-1l);
		retUserInputContainer.setDescrDiagnosisDataFileName_DDE("diagnosisDescription.csv");
		retUserInputContainer.setDiagnosisCodeColumn_DDE("diagnosis_code");
		retUserInputContainer.setDiagnosisDescriptionColumn_DDE("diagnosis_description");
		retUserInputContainer.setColumnSeparatorChar_DDE('\t');
		retUserInputContainer.setColumnTextDelimiterChar_DDE('N');
		retUserInputContainer.setHasFirstRowHeader_DDE(true);

		DescriptionDataLoader dde_loader = new DescriptionDataLoader();
		dde_loader.initializeParamsFromSessionObj(retUserInputContainer);

		try {
			DataLoadContainer<Map<String, String>> loadedData = dde_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getDescrDiagnosisData_DDE()).openStream());
			retUserInputContainer.setDescrDiagnosisData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Disase grouping default

		// Disease pairing default

		return retUserInputContainer;
	}
	
	
	public static void main(String[] args) {
		UserInputContainer ui = getSynthea1kArtificialUserInputContainer(true);
		
	}


}