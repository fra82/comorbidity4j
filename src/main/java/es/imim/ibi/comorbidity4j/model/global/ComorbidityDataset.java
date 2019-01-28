package es.imim.ibi.comorbidity4j.model.global;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.math.Stats;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.util.GenericUtils;


/**
 * Comorbidity dataset holder
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityDataset {

	private static final Logger logger = LoggerFactory.getLogger(ComorbidityDataset.class);

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	// Data loading vars
	private List<Patient> patientList = null;
	private List<Visit> visitList = null;
	private Map<String, Integer> diagnosisCodeStringIdMap = new HashMap<String, Integer>();
	private Map<String, String> diagnosisCodeStringDescriptionMap = new HashMap<String, String>();
	private Map<String, Set<String>> groupNameListCodesGroupMap = new HashMap<String, Set<String>>();
	private Map<String, String> diagnosisCodeStringGroupMap = new HashMap<String, String>();
	private Map<String, Set<String>> groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
	private Map<String, String> groupPairignMap = new HashMap<String, String>();

	private Map<Integer, String> diseaseByIDmap_index = new HashMap<Integer, String>();

	// Data index vars
	private Map<Integer, Visit> visitByIDmap_index = new HashMap<Integer, Visit>();
	private Map<Integer, Patient> patientByIDmap_index = new HashMap<Integer, Patient>();
	private Map<Integer, Set<Integer>> patientIDdiseaseIDSetMap_index = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Set<Integer>> diseaseIDpatientIDSetMap_index = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Set<Patient>> diseaseIDsetPatientMap_index = new HashMap<Integer, Set<Patient>>();
	private Map<Integer, Set<Visit>> diseaseIDsetVisitMap_index = new HashMap<Integer, Set<Visit>>();
	private Map<Integer, Set<Integer>> diagnosisCodePairingMap_index = new HashMap<Integer, Set<Integer>>();

	// Invalid data vars
	private Map<Patient, List<Visit>> error_visitBeforeBirthDate = new HashMap<Patient, List<Visit>>();

	// Father ref
	private ComorbidityMiner comMiner = null;

	// Constructor
	public ComorbidityDataset(List<Patient> patientList_param, List<Visit> visitList_param,
			Map<String, Integer> diagnosisCodeStringIdMap_param, Map<String, String> diagnosisCodeStringDescriptionMap_param, 
			Map<String, Set<String>> groupNameListCodesGroupMap_param,
			Map<String, String> diagnosisCodeStringGroupMap_param,
			Map<String, Set<String>> groupNameListCodesPairingMap_param,
			Map<String, String> groupPairignMap_param,
			ComorbidityMiner comMiner_param) {
		super();

		logger.debug("");
		logger.debug("++++++++++++++++++++++++++++++++++++");
		logger.debug("+++ LOADING COMOPRBIDITY DATASET +++");

		this.patientList = patientList_param;
		this.visitList = visitList_param;
		this.diagnosisCodeStringIdMap = diagnosisCodeStringIdMap_param;
		this.diagnosisCodeStringDescriptionMap = diagnosisCodeStringDescriptionMap_param; // Includes also description for group codes
		this.groupNameListCodesGroupMap = groupNameListCodesGroupMap_param;
		this.diagnosisCodeStringGroupMap = diagnosisCodeStringGroupMap_param;
		this.groupNameListCodesPairingMap = groupNameListCodesPairingMap_param;
		this.groupPairignMap = groupPairignMap_param;

		this.comMiner = comMiner_param;

		Map<String, Patient> patElemStrIdPatientMap = new HashMap<String, Patient>();

		for(Patient patientElem : patientList) {
			patElemStrIdPatientMap.put(patientElem.getStrId(), patientElem);
		}

		// Link patients and visits
		for(Visit visitElem : visitList) {
			if(visitElem != null) {
				String patientIdStr = visitElem.getPatientStringId();
				if(patientIdStr != null && patElemStrIdPatientMap.containsKey(patientIdStr) && patElemStrIdPatientMap.get(patientIdStr) != null) {

					// Check if visit after birth date
					if(DateUtils.isSameDay(patElemStrIdPatientMap.get(patientIdStr).getBirthDate(), visitElem.getVisitDate()) || patElemStrIdPatientMap.get(patientIdStr).getBirthDate().before(visitElem.getVisitDate())) {
						visitElem.setPatientIntId(patElemStrIdPatientMap.get(patientIdStr).getIntId());
						patElemStrIdPatientMap.get(patientIdStr).getVisitSet().add(visitElem);
					}
					else {
						if(!this.error_visitBeforeBirthDate.containsKey(patElemStrIdPatientMap.get(patientIdStr))) this.error_visitBeforeBirthDate.put(patElemStrIdPatientMap.get(patientIdStr), new ArrayList<Visit>());
						this.error_visitBeforeBirthDate.get(patElemStrIdPatientMap.get(patientIdStr)).add(visitElem);
					}

				}
			}
		}

		System.out.println("********************************************************+");
		System.out.println("STARTING DATA SANITIZING...");
		
		// Remove patients with not visits
		List<Patient> patientToRemove = new ArrayList<Patient>();
		for(Patient pat : patientList) {
			if(pat != null && (pat.getVisitSet() == null || pat.getVisitSet().size() == 0)) {
				patientToRemove.add(pat);
			}
		}
		int patientRemovedCount = 0;
		for(Patient patToRemove : patientToRemove) {
			patientList.remove(patToRemove);
			patientRemovedCount++;
		}
		if(patientRemovedCount > 0) {
			System.out.println(" INPUT DATA SANITIZING: " + patientRemovedCount + " patient removed because of no visits present.");
		}
		

		// Remove patients with not visits
		Set<Integer> patientIntIDs = patientList.stream().map(pat -> pat.getIntId()).collect(Collectors.toSet());
		List<Visit> visitToRemove = new ArrayList<Visit>();
		for(Visit vis : visitList) {
			if(vis != null && (vis.getPatientIntId() == null || !patientIntIDs.contains(vis.getPatientIntId()) )) {
				visitToRemove.add(vis);
			}
		}
		int visitRemovedCount = 0;
		for(Visit visToRemove : visitToRemove) {
			visitList.remove(visToRemove);
			visitRemovedCount++;
		}
		if(visitRemovedCount > 0) {
			System.out.println(" INPUT DATA SANITIZING: " + visitRemovedCount + " visits removed because of no patients associated.");
		}

		// Remove visits before the birth date of patient
		visitToRemove = new ArrayList<Visit>();
		Map<Integer, Patient> patByID = new HashMap<Integer, Patient>();
		for(Patient pat : patientList) {
			patByID.put(pat.getIntId(), pat);
		}
		for(Visit vis : visitList) {
			if(vis != null && (vis.getPatientIntId() != null || patientIntIDs.contains(vis.getPatientIntId()) )) {
				Patient relatedPat = patByID.get(vis.getPatientIntId());
				
				if(relatedPat != null && relatedPat.getBirthDate() != null && (!DateUtils.isSameDay(relatedPat.getBirthDate(), vis.getVisitDate()) && relatedPat.getBirthDate().after(vis.getVisitDate())) ) {
					visitToRemove.add(vis);
				}
			}
		}
		visitRemovedCount = 0;
		for(Visit visToRemove : visitToRemove) {
			visitList.remove(visToRemove);
			visitRemovedCount++;
		}
		if(visitRemovedCount > 0) {
			System.out.println(" INPUT DATA SANITIZING: " + visitRemovedCount + " visits removed because of patient birth date is posterior to visit date.");
		}
		
		// Remove visits without diagnosis
		visitToRemove = new ArrayList<Visit>();
		for(Visit vis : visitList) {
			if(vis != null && vis.getDiagnosisCodeSet() != null && vis.getDiagnosisCodeSet().size() == 0) {
				visitToRemove.add(vis);
			}
		}
		visitRemovedCount = 0;
		for(Visit visToRemove : visitToRemove) {
			visitList.remove(visToRemove);
			visitRemovedCount++;
		}
		if(visitRemovedCount > 0) {
			System.out.println(" INPUT DATA SANITIZING: " + visitRemovedCount + " visits removed because of no diagnses associated.");
		}
		
		System.out.println("ENDED DATA SANITIZING.");
		System.out.println("********************************************************+");
		
		


		/*
		// DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
		// Link patients and visits
		Map<Integer, String> diagnosisIdToCodeMap = new HashMap<Integer, String>();
		for(Entry<String, Integer> diagnosisCodeToIdEntry : diagnosisCodeStringIdMap.entrySet()) {
			diagnosisIdToCodeMap.put(diagnosisCodeToIdEntry.getValue(), diagnosisCodeToIdEntry.getKey());
		}

		Random rnd = new Random();

		for(Patient patientElem : patientList) {
			if(rnd.nextInt(1000) == 999 && patientElem != null && patientElem.getVisitSet() != null && patientElem.getVisitSet().size() > 0) {
				System.out.print("\n");
				System.out.print("CONSIDERING PATIENT: " + patientElem.getStrId() + " CHARACTERIZED BY " + patientElem.getVisitSet().size() + " ADMISSIONS AND ");
				System.out.print(" WITH AGE: '" + patientElem.getPatientAge() + "' > \n");

				System.out.print("PATIENT_ID\tPATIENT_BIRTH_DATE\tADMISSION_ID\tADMISSION_DATE\tDIAGNOSIS_NUM\tDIAGNOSIS_LIST\tPATIENT_AGE_AT_ADMISSION\t\n");
				for(Visit vis : patientElem.getVisitSet()) {
					LocalDate birthday = patientElem.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				    LocalDate currentVisitDate = vis.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				    long yearsAtVisit = ChronoUnit.YEARS.between(birthday, currentVisitDate);

				    String diagList = vis.getDiagnosisCodeSet().stream().map(diag -> ((diagnosisIdToCodeMap.containsKey(diag)) ? diagnosisIdToCodeMap.get(diag) : "-")).collect(Collectors.joining("_*_"));
				    System.out.print(vis.getPatientStringId() + "\t" + patientElem.getBirthDate() + "\t" + vis.getStrId() + "\t" + vis.getVisitDate() +
							"\t" + vis.getDiagnosisCodeSet().size() + 
							"\t" + ((!Strings.isNullOrEmpty(diagList)) ? diagList : "---") +
							"\t" + yearsAtVisit + "\n");
				}

			}
		}
		// DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
		 */

		// Add ID to diagnosis codes with a description but zero occurrences
		int nextDiagnosisID = 0;
		for(Entry<String, Integer> diagnosisCodeStringIDMapEntry : diagnosisCodeStringIdMap.entrySet()) {
			if(diagnosisCodeStringIDMapEntry != null && diagnosisCodeStringIDMapEntry.getKey() != null && diagnosisCodeStringIDMapEntry.getValue() != null &&
					nextDiagnosisID <= diagnosisCodeStringIDMapEntry.getValue()) {
				nextDiagnosisID = diagnosisCodeStringIDMapEntry.getValue() + 1;
			} 
		}
		for(Entry<String, String> diagnosisCodeStringDescriptionMapEntry : diagnosisCodeStringDescriptionMap.entrySet()) {
			if(diagnosisCodeStringDescriptionMapEntry.getKey() != null && diagnosisCodeStringDescriptionMapEntry.getKey().trim().length() > 0 && !diagnosisCodeStringIdMap.containsKey(diagnosisCodeStringDescriptionMapEntry.getKey())) {
				diagnosisCodeStringIdMap.put(diagnosisCodeStringDescriptionMapEntry.getKey(), nextDiagnosisID);
				logger.debug("Added diagnosis with code " + diagnosisCodeStringDescriptionMapEntry.getKey() + ", not mentioned among the diagnoses of patients.");
				nextDiagnosisID++;
			}
		}

		this.populateIndexes();

		logger.debug("");
		logger.debug(this.dataChecks(false));
		logger.debug("");

		logger.debug("+++ LOADED COMOPRBIDITY DATASET ++++");
		logger.debug("++++++++++++++++++++++++++++++++++++");
		logger.info("");
	}


	// Getters
	public List<Patient> getPatientList() {
		return Collections.unmodifiableList(patientList);
	}
	public List<Visit> getVisitList() {
		return Collections.unmodifiableList(visitList);
	}
	public Map<String, Integer> getDiagnosisCodeStringIdMap() {
		return Collections.unmodifiableMap(diagnosisCodeStringIdMap);
	}
	public Map<Integer, String> getDiagnosisIdCodeStringMap() {
		return Collections.unmodifiableMap(diseaseByIDmap_index);
	}
	public Map<String, String> getDiagnosisCodeStringDescriptionMap() {
		return Collections.unmodifiableMap(diagnosisCodeStringDescriptionMap);
	}

	public Map<String, Set<String>> getGroupNameListCodesGroupMap() {
		return Collections.unmodifiableMap(groupNameListCodesGroupMap);
	}
	public Map<String, String> getDiagnosisCodeStringGroupMap() {
		return Collections.unmodifiableMap(diagnosisCodeStringGroupMap);
	}
	public Map<String, Set<String>> getGroupNameListCodesPairingMap() {
		return Collections.unmodifiableMap(groupNameListCodesPairingMap);
	}
	public Map<String, String> getGroupPairignMap() {
		return Collections.unmodifiableMap(groupPairignMap);
	}


	@Override
	public String toString() {
		return "ComorbidityDataset [patientListSize=" + ((patientList != null) ? patientList.size() : "null") + 
				", visitListSize=" + ((visitList != null) ? visitList.size() : "null") + 
				", diagnosisCodeStringIdMapSize=" + ((diagnosisCodeStringIdMap != null) ? diagnosisCodeStringIdMap.size() : "null") + 
				", diagnosisCodeStringDescriptionMapSize=" + ((diagnosisCodeStringDescriptionMap != null) ? diagnosisCodeStringDescriptionMap.size() : "null") + 
				", groupNameListCodesGroupMapSize=" + ((groupNameListCodesGroupMap != null) ? groupNameListCodesGroupMap.size() : "null") + 
				", diagnosisCodeStringGroupMapSize=" + ((diagnosisCodeStringGroupMap != null) ? diagnosisCodeStringGroupMap.size() : "null") + 
				", groupNameListCodesPairingMapSize=" + ((groupNameListCodesPairingMap != null) ? groupNameListCodesPairingMap.size() : "null") + 
				", groupPairignMapSize=" + ((groupPairignMap != null) ? groupPairignMap.size() : "null") + 
				", diseaseByIDmap_indexSize=" + ((diseaseByIDmap_index != null) ? diseaseByIDmap_index.size() : "null") +
				", visitByIDmap_indexSize=" + ((visitByIDmap_index != null) ? visitByIDmap_index.size() : "null") + 
				", patientByIDmap_indexSize=" + ((patientByIDmap_index != null) ? patientByIDmap_index.size() : "null") + 
				", patientIDdiseaseIDSetMap_index=" + ((patientIDdiseaseIDSetMap_index != null) ? patientIDdiseaseIDSetMap_index.size() : "null") + 
				", diseaseIDpatientIDSetMap_index=" + ((diseaseIDpatientIDSetMap_index != null) ? diseaseIDpatientIDSetMap_index.size() : "null") + 
				", diseaseIDsetPatientMap_indexSize=" + ((diseaseIDsetPatientMap_index != null) ? diseaseIDsetPatientMap_index.size() : "null") + 
				", diseaseIDsetVisitMap_indexSize=" + ((diseaseIDsetVisitMap_index != null) ? diseaseIDsetVisitMap_index.size() : "null") +
				", diagnosisCodePairingMap_indexSize=" + ((diagnosisCodePairingMap_index != null) ? diagnosisCodePairingMap_index.size() : "null") +"]";
	}


	/**
	 * Get patient by ID
	 * 
	 * @param patientID
	 * @return
	 */
	public Patient getPatientByID(Integer patientID) {
		Patient retPatient = null;

		if(patientByIDmap_index != null && patientID != null && 
				patientByIDmap_index.containsKey(patientID) && patientByIDmap_index.get(patientID) != null) {
			retPatient = patientByIDmap_index.get(patientID);
		}

		return retPatient;
	}


	/**
	 * Get patient by ID
	 * 
	 * @param patientID
	 * @return
	 */
	public Visit getVisitByID(Integer visitID) {
		Visit retVisit = null;

		if(visitByIDmap_index != null && visitID != null && 
				visitByIDmap_index.containsKey(visitID) && visitByIDmap_index.get(visitID) != null) {
			retVisit = visitByIDmap_index.get(visitID);
		}

		return retVisit;
	}


	/**
	 * Populates a set of indexes in order to speed-up comorbidity calculation
	 * 
	 */
	private void populateIndexes() {

		logger.debug("");
		logger.debug("> Populate indexes...");

		diseaseByIDmap_index = new HashMap<Integer, String>();
		visitByIDmap_index = new HashMap<Integer, Visit>();
		patientByIDmap_index = new HashMap<Integer, Patient>();
		diseaseIDsetPatientMap_index = new HashMap<Integer, Set<Patient>>();
		patientIDdiseaseIDSetMap_index = new HashMap<Integer, Set<Integer>>();
		diseaseIDpatientIDSetMap_index = new HashMap<Integer, Set<Integer>>();
		diseaseIDsetVisitMap_index = new HashMap<Integer, Set<Visit>>();

		int maxDiagnosisInt = 0;
		for(Entry<String, Integer> diagnosisCodeEntry : this.diagnosisCodeStringIdMap.entrySet()) {
			if(diagnosisCodeEntry != null && diagnosisCodeEntry.getKey() != null && diagnosisCodeEntry.getValue() != null) {
				diseaseByIDmap_index.put(diagnosisCodeEntry.getValue(), diagnosisCodeEntry.getKey());
				if(maxDiagnosisInt < diagnosisCodeEntry.getValue()) {
					maxDiagnosisInt = diagnosisCodeEntry.getValue();
				}
			}
		}

		// Add diagnosis group integer ID - STEP A
		// Map<String, String> oldNewGroupNameMap = new HashMap<String, String>();
		for(String groupName : groupNameListCodesGroupMap.keySet()) {
			maxDiagnosisInt++;

			/* Groups are renamed in loadAndIndexData method of COmorbidityMiner
			String oldGroupName = groupName;

			boolean renamedGroup = false;
			while(this.diagnosisCodeStringIdMap.containsKey(groupName)) {
				groupName = groupName + "-";
			}

			if(renamedGroup) {
				oldNewGroupNameMap.put(oldGroupName, groupName);
			}
			 */

			this.diseaseByIDmap_index.put(maxDiagnosisInt, groupName);
			this.diagnosisCodeStringIdMap.put(groupName, maxDiagnosisInt);
		}

		// Add diagnosis group integer ID - STEP B - Rename all the disease of a group with the group code
		for(Patient pat : patientList) {
			if(pat != null && pat.getVisitSet() != null && pat.getVisitSet().size() > 0) {
				for(Visit vis : pat.getVisitSet()) {
					Set<Integer> diagnosisCodesToRemove = new HashSet<Integer>();
					Set<Integer> diagnosisCodesToAdd = new HashSet<Integer>();

					if(vis != null && vis.getDiagnosisCodeSet() != null) {

						for(Integer diagnosisCodeInteger : vis.getDiagnosisCodeSet()) {

							if(diagnosisCodeInteger != null && diseaseByIDmap_index.containsKey(diagnosisCodeInteger) && diseaseByIDmap_index.get(diagnosisCodeInteger) != null) {
								String diagnosisCodeString = diseaseByIDmap_index.get(diagnosisCodeInteger);
								if(diagnosisCodeStringGroupMap.containsKey(diagnosisCodeString) && diagnosisCodeStringGroupMap.get(diagnosisCodeString) != null) {
									diagnosisCodesToRemove.add(diagnosisCodeInteger);
									String diagnosisCodeOfGroupString = diagnosisCodeStringGroupMap.get(diagnosisCodeString);
									if(diagnosisCodeOfGroupString != null) {
										diagnosisCodesToAdd.add(diagnosisCodeStringIdMap.get(diagnosisCodeOfGroupString));
									}
								}
							}
						}
					}

					vis.getDiagnosisCodeSet().removeAll(diagnosisCodesToRemove);
					vis.getDiagnosisCodeSet().addAll(diagnosisCodesToAdd);
				}
			}
		}

		// Add diagnosis group integer ID - STEP C - select and remove all the diagnoses that are in a group
		Set<Integer> diagnosisIDintegerToRemoveSet = new HashSet<Integer>();
		Set<String> diagnosisIDstringToRemoveSet = new HashSet<String>();
		for(String diagnosisCodeToRemove : diagnosisCodeStringGroupMap.keySet()) {
			if(diagnosisCodeToRemove != null && this.diagnosisCodeStringIdMap.containsKey(diagnosisCodeToRemove) && 
					this.diagnosisCodeStringIdMap.get(diagnosisCodeToRemove) != null) {
				diagnosisIDstringToRemoveSet.add(diagnosisCodeToRemove);
				diagnosisIDintegerToRemoveSet.add(this.diagnosisCodeStringIdMap.get(diagnosisCodeToRemove));
			}
		}

		for(Integer diagnosisIDintegerToRemove : diagnosisIDintegerToRemoveSet) {
			if(diagnosisIDintegerToRemove != null && this.diseaseByIDmap_index.containsKey(diagnosisIDintegerToRemove)) {
				this.diseaseByIDmap_index.remove(diagnosisIDintegerToRemove);
			}
		}
		for(String diagnosisIDstringToRemove : diagnosisIDstringToRemoveSet) {
			if(diagnosisIDstringToRemove != null && this.diagnosisCodeStringIdMap.containsKey(diagnosisIDstringToRemove)) {
				this.diagnosisCodeStringIdMap.remove(diagnosisIDstringToRemove);
				if(this.diagnosisCodeStringDescriptionMap.containsKey(diagnosisIDstringToRemove)) {
					this.diagnosisCodeStringDescriptionMap.remove(diagnosisIDstringToRemove);
				}
			}
		}


		/* Add diagnosis group integer ID - STEP D - add values for renamed groups
		// Commented because groups are renamed in loadAndIndexData method of COmorbidityMiner
		for(Entry<String, String> oldNewGroupName : oldNewGroupNameMap.entrySet()) {
			if( oldNewGroupName != null && !Strings.isEmpty(oldNewGroupName.getKey()) && !Strings.isEmpty(oldNewGroupName.getValue()) &&
					groupNameListCodesGroupMap.containsKey(oldNewGroupName.getKey()) && !groupNameListCodesGroupMap.containsKey(oldNewGroupName.getValue()) ) {

				groupNameListCodesGroupMap.put(oldNewGroupName.getValue(), groupNameListCodesGroupMap.get(oldNewGroupName.getKey()));

				Set<String> codesOfNewOrOldGroupList = groupNameListCodesGroupMap.get(oldNewGroupName.getValue());
				for(String codeOfGroup : codesOfNewOrOldGroupList) {
					if(!Strings.isEmpty(codeOfGroup)) {
						diagnosisCodeStringGroupMap.put(codeOfGroup, oldNewGroupName.getValue());
					}
				}

				// Remove old group name from diseaseByIDmap_index and diagnosisCodeStringIdMap
				if(diagnosisCodeStringIdMap.containsKey(oldNewGroupName.getKey()) && diagnosisCodeStringIdMap.get(oldNewGroupName.getKey()) != null) {
					Integer oldGroupId = diagnosisCodeStringIdMap.get(oldNewGroupName.getKey());
					diagnosisCodeStringIdMap.remove(oldGroupId);
					diagnosisCodeStringIdMap.remove(oldNewGroupName.getKey());
				}

			}
		}
		 */

		// Populate diagnosisCodePairingMap
		if(groupPairignMap != null && groupNameListCodesPairingMap != null && groupNameListCodesPairingMap.size() > 0) {
			for(Entry<String, String> groupNamePair : groupPairignMap.entrySet()) {
				if(groupNamePair != null && !Strings.isEmpty(groupNamePair.getKey()) && !Strings.isEmpty(groupNamePair.getValue()) &&
						groupNameListCodesPairingMap.containsKey(groupNamePair.getKey()) && groupNameListCodesPairingMap.get(groupNamePair.getKey()) != null &&
						groupNameListCodesPairingMap.containsKey(groupNamePair.getValue()) && groupNameListCodesPairingMap.get(groupNamePair.getValue()) != null
						) {

					// Get source and destination group of pair
					Set<String> sourceGroupSet = new HashSet();
					sourceGroupSet.addAll(groupNameListCodesPairingMap.get(groupNamePair.getKey()));
					Set<String> destinationGroupSet = new HashSet();
					destinationGroupSet.addAll(groupNameListCodesPairingMap.get(groupNamePair.getValue()));

					for(String diagnosisCodeS : sourceGroupSet) {
						if(!Strings.isEmpty(diagnosisCodeS)) {
							for(String diagnosisCodeD : destinationGroupSet) {
								if(!Strings.isEmpty(diagnosisCodeD)) {

									// Get diagnosis source and destination IDs
									Integer diagnosisCodeSint = (!diagnosisCodeS.trim().toLowerCase().equals("__all_diagnoses_selected__"))
											? this.diagnosisCodeStringIdMap.get(diagnosisCodeS) : null;
											Integer diagnosisCodeDint = (!diagnosisCodeD.trim().toLowerCase().equals("__all_diagnoses_selected__"))
													? this.diagnosisCodeStringIdMap.get(diagnosisCodeD) : null;

													if(diagnosisCodeSint != null && diagnosisCodeDint != null && diagnosisCodeSint != diagnosisCodeDint) {

														if(this.comMiner.getDirectionalityFilter() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() > 0l) {
															// Directionality enabled - add both pairs
															if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeSint)) diagnosisCodePairingMap_index.put(diagnosisCodeSint, new HashSet<Integer>());
															diagnosisCodePairingMap_index.get(diagnosisCodeSint).add(diagnosisCodeDint);

															if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeDint)) diagnosisCodePairingMap_index.put(diagnosisCodeDint, new HashSet<Integer>());
															diagnosisCodePairingMap_index.get(diagnosisCodeDint).add(diagnosisCodeSint);
														}
														else {
															// Directionality not enabled - add one of the two pairs if non is present
															if(
																	(diagnosisCodePairingMap_index.get(diagnosisCodeSint) != null && diagnosisCodePairingMap_index.get(diagnosisCodeSint).contains(diagnosisCodeDint)) ||
																	(diagnosisCodePairingMap_index.get(diagnosisCodeDint) != null && diagnosisCodePairingMap_index.get(diagnosisCodeDint).contains(diagnosisCodeSint))
																	) {
																// Do nothing, one of the two diagnosis pairs is already present
															}
															else {
																if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeSint)) diagnosisCodePairingMap_index.put(diagnosisCodeSint, new HashSet<Integer>());
																diagnosisCodePairingMap_index.get(diagnosisCodeSint).add(diagnosisCodeDint);
															}
														}

													}
													else if(diagnosisCodeSint != null && diagnosisCodeDint == null) {

														for(Integer diagnosisCodeAllElem : diseaseByIDmap_index.keySet()) {

															if(diagnosisCodeSint.intValue() == diagnosisCodeAllElem.intValue()) {
																continue;
															}

															if(this.comMiner.getDirectionalityFilter() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() > 0l) {
																// Directionality enabled - add both pairs
																if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeSint)) diagnosisCodePairingMap_index.put(diagnosisCodeSint, new HashSet<Integer>());
																diagnosisCodePairingMap_index.get(diagnosisCodeSint).add(diagnosisCodeAllElem);

																if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeAllElem)) diagnosisCodePairingMap_index.put(diagnosisCodeAllElem, new HashSet<Integer>());
																diagnosisCodePairingMap_index.get(diagnosisCodeAllElem).add(diagnosisCodeSint);
															}
															else {
																// Directionality not enabled - add one of the two pairs if non is present
																if(
																		(diagnosisCodePairingMap_index.get(diagnosisCodeSint) != null && diagnosisCodePairingMap_index.get(diagnosisCodeSint).contains(diagnosisCodeAllElem)) ||
																		(diagnosisCodePairingMap_index.get(diagnosisCodeAllElem) != null && diagnosisCodePairingMap_index.get(diagnosisCodeAllElem).contains(diagnosisCodeSint))
																		) {
																	// Do nothing, one of the two diagnosis pairs is already present
																}
																else {
																	if(!diagnosisCodePairingMap_index.containsKey(diagnosisCodeSint)) diagnosisCodePairingMap_index.put(diagnosisCodeSint, new HashSet<Integer>());
																	diagnosisCodePairingMap_index.get(diagnosisCodeSint).add(diagnosisCodeAllElem);
																}
															}

														}

													}

								}
							}
						}
					}
				}
			}
		}

		for(Visit visitElem : visitList) {
			if(visitElem != null) {
				visitByIDmap_index.put(visitElem.getIntId(), visitElem);

				for(Integer diseaseID : visitElem.getDiagnosisCodeSet()) {
					if(diseaseIDsetVisitMap_index.containsKey(diseaseID)) {
						Set<Visit> visitSet = diseaseIDsetVisitMap_index.get(diseaseID);
						visitSet.add(visitElem);
					}
					else {
						Set<Visit> visitSet = new HashSet<Visit>();
						visitSet.add(visitElem);
						diseaseIDsetVisitMap_index.put(diseaseID, visitSet);
					}
				}
			}
		}

		for(Patient patientElem : patientList) {
			if(patientElem != null) {
				patientByIDmap_index.put(patientElem.getIntId(), patientElem);

				for(Visit visitOfPatient : patientElem.getVisitSet()) {
					if(visitOfPatient != null && visitOfPatient.getDiagnosisCodeSet().size() > 0) {
						for(Integer diseaseID : visitOfPatient.getDiagnosisCodeSet()) {
							if(diseaseIDsetPatientMap_index.containsKey(diseaseID)) {
								Set<Patient> patientSet = diseaseIDsetPatientMap_index.get(diseaseID);
								patientSet.add(patientElem);
							}
							else {
								Set<Patient> patientSet = new HashSet<Patient>();
								patientSet.add(patientElem);
								diseaseIDsetPatientMap_index.put(diseaseID, patientSet);
							}

							if(!patientIDdiseaseIDSetMap_index.containsKey(patientElem.getIntId())) {
								patientIDdiseaseIDSetMap_index.put(patientElem.getIntId(), new HashSet<Integer>());
							}
							patientIDdiseaseIDSetMap_index.get(patientElem.getIntId()).add(diseaseID);

							if(!diseaseIDpatientIDSetMap_index.containsKey(diseaseID)) {
								diseaseIDpatientIDSetMap_index.put(diseaseID, new HashSet<Integer>());
							}
							diseaseIDpatientIDSetMap_index.get(diseaseID).add(patientElem.getIntId());
						}
					}
				}
			}
		}

		logger.debug(" > Indexes populated");
		logger.debug("");
	}


	/**
	 * Get all paired diagnoses given a name or ID
	 * 
	 * @param diagnosisName
	 * @param diagnosisID
	 * @return
	 */
	public Set<Integer> getPairedDiagnoses(String diagnosisName, Integer diagnosisID) {

		Set<Integer> retSet = new HashSet<Integer>();

		String nameInt = null;
		Integer codeInt = null;
		if(diagnosisName == null && diagnosisID != null && diseaseByIDmap_index.containsKey(diagnosisID) && !Strings.isEmpty(diseaseByIDmap_index.get(diagnosisID))) {
			nameInt = diseaseByIDmap_index.get(diagnosisID);
			codeInt = diagnosisID;
		}
		else if(diagnosisID == null && !Strings.isEmpty(diagnosisName) && diagnosisCodeStringIdMap.containsKey(diagnosisName) && diagnosisCodeStringIdMap.get(diagnosisName) != null) {
			codeInt = diagnosisCodeStringIdMap.get(diagnosisName);
			nameInt = diagnosisName;
		}

		if(!Strings.isEmpty(nameInt) && codeInt != null) {

			if(diagnosisCodeStringGroupMap.containsKey(nameInt) && !Strings.isEmpty(diagnosisCodeStringGroupMap.get(nameInt))) {
				// The diagnosis is in a group
				String groupName = diagnosisCodeStringGroupMap.get(nameInt);
				Integer groupID = diagnosisCodeStringIdMap.get(groupName);
				nameInt = groupName;
				codeInt = groupID;
			}


			if(diagnosisCodePairingMap_index != null && diagnosisCodePairingMap_index.size() > 0 && 
					diagnosisCodePairingMap_index.containsKey(codeInt) && diagnosisCodePairingMap_index.get(codeInt) != null) {
				retSet = new HashSet<Integer>(diagnosisCodePairingMap_index.get(codeInt));
			}
			else if(diagnosisCodePairingMap_index == null || diagnosisCodePairingMap_index.size() == 0) {
				// All diseases are mapped with all diseases
				Set<Integer> newSet = new HashSet<Integer>(diseaseByIDmap_index.keySet());
				newSet.remove(codeInt);
				retSet = newSet;
			}

		}
		else {
			logger.error("Impossible to determine paired diagnoses of diagnosis with name " + ((!Strings.isEmpty(diagnosisName)) ? diagnosisName : "NULL") + 
					" and code: " + ((codeInt != null) ? codeInt : "NULL"));

		}

		boolean removedOriginDiagnosis = retSet.remove(diagnosisID);
		if(removedOriginDiagnosis) {
			System.out.println("Origin of pair is in destinations!!!");
		}

		return Collections.unmodifiableSet(retSet);
	} 



	/**
	 * Returns a textual string explaining eventual inconsistencies found in the dataset.
	 * 
	 * @param detailedLog
	 * @return
	 */
	public String dataChecks(boolean detailedLog) {
		StringBuffer retString = new StringBuffer("");

		retString.append("\n");
		retString.append(">>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<\n");
		retString.append(">>> Comorbidity dataset stats <<<\n");

		retString.append("    *****************************************************************************************************************************************\n");
		retString.append("    *** IMPORTANT: these stats are computed WITHOUT applying any filter, considering the row data imported from the input files / dataset ***\n");
		retString.append("    *****************************************************************************************************************************************\n");

		retString.append("    > Num patients (WITHOUT filters applied - row data): " + patientList.size() + "\n");
		retString.append("    > Num visits (WITHOUT filters applied - row data):   " + visitList.size() + "\n");
		retString.append("    > Num diseases (WITHOUT filters applied - row data): " + diagnosisCodeStringIdMap.size() + ".\n");

		Map<String, Patient> patElemStrIdPatientMap = new HashMap<String, Patient>();

		for(Patient patientElem : patientList) {
			patElemStrIdPatientMap.put(patientElem.getStrId(), patientElem);
		}

		int countVisitsNotAssociatedToAnyPatient = 0;
		for(Visit visitElem : visitList) {
			if(visitElem != null) {
				String patientIdStr = visitElem.getPatientStringId();
				boolean foundPatient = false;
				if(patElemStrIdPatientMap.containsKey(patientIdStr) && patElemStrIdPatientMap.get(patientIdStr) != null) {
					// visitElem.setPatientIntId(patientElem.getIntId()); - Done by constructor
					// patientElem.getVisitSet().add(visitElem); - Done by constructor
					foundPatient = true;
					break;
				}


				if(!foundPatient) {
					if(detailedLog) retString.append("Visit NOT associated to any patient: " + visitElem.toString() + "\n");
					countVisitsNotAssociatedToAnyPatient++;
				}
			}
		}

		int countVisitsWithoutDiagCodes = 0;
		for(Visit visitElem : visitList) {
			if(visitElem != null && visitElem.getDiagnosisCodeSet().size() == 0) {
				if(detailedLog) retString.append("Visit WITHOUT diagnosis codes associated: " + visitElem.toString() + "\n");
				countVisitsWithoutDiagCodes++;
			}
		}


		int countPatientsWithoutDiagCodes = 0;
		for(Patient patientElem : patientList) {
			if(patientElem != null && patientElem.getVisitSet().size() == 0) {
				if(detailedLog) retString.append("Patient WITHOUT visits associated: " + patientElem.getStrId() + "\n");
				countPatientsWithoutDiagCodes++;
			}
		}

		String errorOfDiagnosisCodes = "";
		Map<String, Double> diseaseCodeNumPatients = new HashMap<String, Double>();
		Map<String, Double> diseaseCodePercPatients = new HashMap<String, Double>();
		DecimalFormat decimFormat = new DecimalFormat("#.0000");
		for(Entry<String, Integer> diangCodeIDelem : diagnosisCodeStringIdMap.entrySet()) {
			if(diangCodeIDelem != null && diangCodeIDelem.getKey() != null && diangCodeIDelem.getValue() != null) {
				try {
					Set<Integer> allPatientIDs = this.getAllPatients(null);
					Set<Integer> patientIDs_disA = this.getPatientsWithALLDiseases(null, diangCodeIDelem.getValue());
					double percPatientsWithDisA = (allPatientIDs != null && allPatientIDs.size() > 0) ? ( 100d * ((double) patientIDs_disA.size()) / ((double) allPatientIDs.size())) : 0d;
					diseaseCodeNumPatients.put(diangCodeIDelem.getKey(), (double) patientIDs_disA.size());
					diseaseCodePercPatients.put(diangCodeIDelem.getKey(), percPatientsWithDisA);
				}
				catch(Exception e) {
					/* DO NOTHING */
				}
			}
			else {
				errorOfDiagnosisCodes += ((errorOfDiagnosisCodes.length() == 0) ? "" : "\n") + "    > " + "Error whith the disease with code " + 
						((diangCodeIDelem.getKey() != null) ? diangCodeIDelem.getKey() : "NULL") + " and value " + 
						((diangCodeIDelem.getValue() != null) ? diangCodeIDelem.getValue() : "NULL");
			}
		}

		// List top 30 diseases with more patients as well as all diseases contained in the Index Disease file
		String top15_bottom15_FrequencyOfDisease = "";
		int newLineCounter = 0;
		Map<String, Double> diseaseCodePercPatientsOrdered = GenericUtils.sortByValue(diseaseCodePercPatients, true);
		for(Entry<String, Double> orderedElem : diseaseCodePercPatientsOrdered.entrySet()) {
			newLineCounter++;
			if( newLineCounter <= 15 || (newLineCounter >= diseaseCodePercPatientsOrdered.size() - 16) ) {
				Set<Integer> pairedDiagnoses = getPairedDiagnoses(orderedElem.getKey(), null);

				top15_bottom15_FrequencyOfDisease += ((top15_bottom15_FrequencyOfDisease.length() == 0) ? "" : "\n") + "    >       " + newLineCounter + " - '" + orderedElem.getKey() + "' = " + 
						diseaseCodeNumPatients.get(orderedElem.getKey()).intValue() + " patients (" + decimFormat.format(orderedElem.getValue()) + "% of patients) " +
						((diagnosisCodeStringDescriptionMap.containsKey(orderedElem.getKey()) && diagnosisCodeStringDescriptionMap.get(orderedElem.getKey()) != null) 
								? " > Description: " + diagnosisCodeStringDescriptionMap.get(orderedElem.getKey()) : "") +
						"\n         > paired with '" + ((pairedDiagnoses != null) ? pairedDiagnoses.size() : "0") + "' diseases for comorbidity analysis";
			}
			else {
				continue;
			}
		}

		String listOfDiseaseCodes = "";
		newLineCounter = 0;
		for(Entry<String, String> diagnosisCodeDescElem : diagnosisCodeStringDescriptionMap.entrySet()) {
			if(diagnosisCodeDescElem != null && diagnosisCodeDescElem.getKey() != null) {
				newLineCounter++;
				Set<Integer> pairedDiagnoses = getPairedDiagnoses(diagnosisCodeDescElem.getKey(), null);
				listOfDiseaseCodes += ((listOfDiseaseCodes.length() == 0) ? "" : "\n") + "    >       - " + newLineCounter + " > " 
						+ diagnosisCodeDescElem.getKey() + " = " + ((diagnosisCodeDescElem.getValue() == null) ? "null" : diagnosisCodeDescElem.getValue()) 
						+ " > paired with " + ((pairedDiagnoses != null) ? pairedDiagnoses.size() : "0") + "' diseases for comorbidity analysis";
			}
		}


		// Average patient age at last diagnosis
		Stats stat_patientAge_FIRST_ADMISSION = null;
		try {
			List<Long> ageLastDiag = patientList.stream().map(pat -> pat.getPatientAge(PatientAgeENUM.FIRST_ADMISSION)).filter(patAge -> (patAge >= 0l)).collect(Collectors.toList());
			stat_patientAge_FIRST_ADMISSION = Stats.of(ageLastDiag);
		}
		catch(Exception e) {
			/* DO NOTHING */
		}

		Stats stat_patientAge_FIRST_DIAGNOSTIC = null;
		try {
			List<Long> ageLastDiag = patientList.stream().map(pat -> pat.getPatientAge(PatientAgeENUM.FIRST_DIAGNOSTIC)).filter(patAge -> (patAge >= 0l)).collect(Collectors.toList());
			stat_patientAge_FIRST_DIAGNOSTIC = Stats.of(ageLastDiag);
		}
		catch(Exception e) {
			/* DO NOTHING */
		}

		Stats stat_patientAge_LAST_ADMISSION = null;
		try {
			List<Long> ageLastDiag = patientList.stream().map(pat -> pat.getPatientAge(PatientAgeENUM.LAST_ADMISSION)).filter(patAge -> (patAge >= 0l)).collect(Collectors.toList());
			stat_patientAge_LAST_ADMISSION = Stats.of(ageLastDiag);
		}
		catch(Exception e) {
			/* DO NOTHING */
		}

		Stats stat_patientAge_LAST_DIAGNOSTIC = null;
		try {
			List<Long> ageLastDiag = patientList.stream().map(pat -> pat.getPatientAge(PatientAgeENUM.LAST_DIAGNOSTIC)).filter(patAge -> (patAge >= 0l)).collect(Collectors.toList());
			stat_patientAge_LAST_DIAGNOSTIC = Stats.of(ageLastDiag);
		}
		catch(Exception e) {
			/* DO NOTHING */
		}

		Stats stat_patientAge_EXECUTION_TIME = null;
		try {
			List<Long> ageLastDiag = patientList.stream().map(pat -> pat.getPatientAge(PatientAgeENUM.EXECUTION_TIME)).filter(patAge -> (patAge >= 0l)).collect(Collectors.toList());
			stat_patientAge_EXECUTION_TIME = Stats.of(ageLastDiag);
		}
		catch(Exception e) {
			/* DO NOTHING */
		}


		// Print check results
		retString.append("    > \n");
		retString.append("    > Total visits WITHOUT diagnosis codes associated (over the whole NOT-filtered dataset): " + countVisitsWithoutDiagCodes + " over " + visitList.size() + " visits.\n");
		retString.append("    > Total patients WITHOUT visits associated (over the whole NOT-filtered dataset): " + countPatientsWithoutDiagCodes + " over " + patientList.size() + " patients.\n");
		retString.append("    > Total visits NOT associated to any patient (over the whole NOT-filtered dataset): " + countVisitsNotAssociatedToAnyPatient + " over " + visitList.size() + " visits.\n");
		retString.append("    > \n");

		// Error check output
		if(this.error_visitBeforeBirthDate != null && this.error_visitBeforeBirthDate.size() > 0) {
			retString.append("    > DATA ERROR CHECK: Number of patients with one or more visits that occurred before the birth date (suche visits are ignored): " + this.error_visitBeforeBirthDate.size() + "\n");
			retString.append("    >         Patient IDs: ");
			for(Entry<Patient, List<Visit>> patVisMapEntry : this.error_visitBeforeBirthDate.entrySet()) {
				retString.append("    >      * Patient ID: " + patVisMapEntry.getKey().getStrId() + 
						", birth date: " + ((patVisMapEntry.getKey().getBirthDate() != null) ? dateFormatter.format(patVisMapEntry.getKey().getBirthDate()) : "NULL") + "\n" +
						"                  > Num visits correctly associated: " + ((patVisMapEntry.getKey().getVisitSet() != null) ? patVisMapEntry.getKey().getVisitSet().size() : "0") + "\n" + 
						"                  > Num visits occurred before the birth date: " + patVisMapEntry.getValue().size() + " > " + patVisMapEntry.getValue().stream().map(vis -> " Vis. ID: " + vis.getStrId() + " (" + ((vis.getVisitDate() != null) ? dateFormatter.format(vis.getVisitDate()) : "NULL") + ")").collect(Collectors.joining(", ")) + "\n");
			}
		}
		else {
			retString.append("    > DATA ERROR CHECK: No patients have one or more visits that occurred before the birth date.\n");
		}


		retString.append("    > \n");

		if(stat_patientAge_FIRST_ADMISSION != null) {
			retString.append("    > Patient age statistics: age at first admission date: " + stat_patientAge_FIRST_ADMISSION.toString() + "\n");
		}
		if(stat_patientAge_FIRST_DIAGNOSTIC != null) {
			retString.append("    > Patient age statistics: age at first diagnosis date: " + stat_patientAge_FIRST_DIAGNOSTIC.toString() + "\n");
		}
		if(stat_patientAge_LAST_ADMISSION != null) {
			retString.append("    > Patient age statistics: age at last admission date: " + stat_patientAge_LAST_ADMISSION.toString() + "\n");
		}
		if(stat_patientAge_LAST_DIAGNOSTIC != null) {
			retString.append("    > Patient age statistics: age at last diagnosis date: " + stat_patientAge_LAST_DIAGNOSTIC.toString() + "\n");
		}
		if(stat_patientAge_EXECUTION_TIME != null) {
			retString.append("    > Patient age statistics: age at execution date: " + stat_patientAge_EXECUTION_TIME.toString() + "\n");
		}

		retString.append("    > \n");
		retString.append("    > Most frequent (max 15) / less frequent (max 15) diseases with respect to the percentage of patients (WITHOUT filters applied - row data): \n");
		retString.append((top15_bottom15_FrequencyOfDisease != null) ? top15_bottom15_FrequencyOfDisease : "-");
		retString.append("    > \n");
		retString.append((errorOfDiagnosisCodes != null && errorOfDiagnosisCodes.trim().length() > 0) ? "    > Disease code errors:\n" + errorOfDiagnosisCodes : "");
		retString.append("    > \n");
		retString.append("    > Number of disease codes included in the Disease Description file: " + diagnosisCodeStringDescriptionMap.size() + ".\n");
		if(detailedLog) {
			retString.append("    > \n");
			retString.append("    > List of disease codes considered for comorbidity analysis:\n");
			retString.append((listOfDiseaseCodes != null) ? listOfDiseaseCodes : "-");
		}
		retString.append("    > \n");
		retString.append(">>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<\n");
		retString.append("\n");

		return retString.toString();
	}

	/**
	 * Get the IDs of patients that experimented ALL the diseases listed
	 * 
	 * @param diseaseIDs
	 * @return
	 */
	public Set<Integer> getPatientsWithALLDiseases(ComorbidityPatientFilter patientFilter, Integer... diseaseIDs) {

		if(diseaseIDs == null || diseaseIDs.length == 0) {
			logger.warn("Null diseases IDs");
			return new HashSet<Integer>();
		}

		Set<Integer> diseaseSetToCheck = new HashSet<Integer>();
		for(Integer diseaseID : diseaseIDs) {
			if(diseaseID != null) {
				diseaseSetToCheck.add(diseaseID);
			}			
		}

		Set<Integer> returnPatientIDset = new HashSet<Integer>();

		/*
		Set<Integer> returnPatientIDsetAPPO = new HashSet<Integer>();
		for(Entry<Integer, Set<Integer>> patientIdDiseaseSetEntry : patientIDdiseaseIDSetMap_index.entrySet()) {
			if(patientIdDiseaseSetEntry.getKey() != null && patientIdDiseaseSetEntry.getValue() != null) {

				// Retrieve patient
				Patient patWithDisease = (patientByIDmap_index.containsKey(patientIdDiseaseSetEntry.getKey()) && patientByIDmap_index.get(patientIdDiseaseSetEntry.getKey()) != null) ? 
						patientByIDmap_index.get(patientIdDiseaseSetEntry.getKey()) : null;

				// APPLY FILTER - START
				if(patWithDisease == null || (patientFilter != null && !patientFilter.checkPatientFilters(patWithDisease))) {
					continue;
				}
				// APPLY FILTER - END

				if(patientIdDiseaseSetEntry.getValue().containsAll(diseaseSetToCheck)) {
					returnPatientIDsetAPPO.add(patientIdDiseaseSetEntry.getKey());
				}
			}
		}
		 */

		/*
		Set<Integer> patientIDset = patientIDdiseaseIDSetMap_index.keySet().stream().parallel().filter(
				patientID -> 
				( 
						patientID != null &&
						patientIDdiseaseIDSetMap_index.get(patientID) != null &&
						patientIDdiseaseIDSetMap_index.get(patientID).size() > 0 &&
						patientByIDmap_index.get(patientID) != null && 
						(patientFilter == null || (patientFilter != null &&	patientFilter.checkPatientFilters(patientByIDmap_index.get(patientID))) ) && 
						patientIDdiseaseIDSetMap_index.get(patientID).containsAll(diseaseSetToCheck)))
				.collect(Collectors.toSet());

		returnPatientIDset.addAll(patientIDset);
		 */

		Set<Integer> returnPatientIDset_preFilter = new HashSet<Integer>();
		boolean firstDisease = true;
		for(Integer diseaseIDToCheck : diseaseSetToCheck) {
			Set<Integer> patientIDwithDiseseSet = diseaseIDpatientIDSetMap_index.get(diseaseIDToCheck);
			// If null, it could happen that the disease code is mentioned in the pairing file but does not occur in the patient dataset
			if(patientIDwithDiseseSet == null || patientIDwithDiseseSet.size() == 0) {
				return new HashSet<Integer>();
			}

			if(patientIDwithDiseseSet != null) {
				if(firstDisease) {
					returnPatientIDset_preFilter.addAll(patientIDwithDiseseSet);
					firstDisease = false;
				}
				else {
					returnPatientIDset_preFilter.retainAll(patientIDwithDiseseSet);
				}
			}
		}

		for(Integer patIDtoFilter : returnPatientIDset_preFilter) {
			Patient patToFilter = patientByIDmap_index.get(patIDtoFilter);
			if(patToFilter != null && (patientFilter == null || (patientFilter != null && patientFilter.checkPatientFilters(patToFilter)))) {
				returnPatientIDset.add(patIDtoFilter.intValue());
			}
		}

		return returnPatientIDset;
	}

	/**
	 * Get the IDs of patients that experimented AT LEAST ONE OF the diseases listed
	 * 
	 * @param diseaseIDs
	 * @return
	 */
	public Set<Integer> getPatientsWithATleastONEDiseases(ComorbidityPatientFilter patientFilter, Integer... diseaseIDs) {

		if(diseaseIDs == null || diseaseIDs.length == 0) {
			logger.warn("Null diseases IDs");
			return new HashSet<Integer>();
		}

		Set<Integer> diseaseSetToCheck = new HashSet<Integer>();
		for(Integer diseaseID : diseaseIDs) {
			if(diseaseID != null) {
				diseaseSetToCheck.add(diseaseID);
			}			
		}

		Set<Integer> returnPatientIDset = new HashSet<Integer>();

		/*
		Set<Integer> returnPatientIDsetAPPO = new HashSet<Integer>();
		for(Entry<Integer, Set<Integer>> patientIdDiseaseSetEntry : patientIDdiseaseIDSetMap_index.entrySet()) {
			if(patientIdDiseaseSetEntry.getKey() != null && patientIdDiseaseSetEntry.getValue() != null) {

				// Retrieve patient
				Patient patWithDisease = (patientByIDmap_index.containsKey(patientIdDiseaseSetEntry.getKey()) && patientByIDmap_index.get(patientIdDiseaseSetEntry.getKey()) != null) ? 
						patientByIDmap_index.get(patientIdDiseaseSetEntry.getKey()) : null;

				// APPLY FILTER - START
				if(patWithDisease == null || (patientFilter != null && !patientFilter.checkPatientFilters(patWithDisease))) {
					continue;
				}
				// APPLY FILTER - END

				for(Integer diseaseToCheck : diseaseSetToCheck) {
					if(patientIdDiseaseSetEntry.getValue().contains(diseaseToCheck)) {
						returnPatientIDsetAPPO.add(patientIdDiseaseSetEntry.getKey());
						break;
					}
				}

			}
		}
		 */

		/*
		Set<Integer> patientIDset = patientIDdiseaseIDSetMap_index.keySet().stream().parallel().filter(
				patientID -> 
				( 
						patientID != null &&
						patientIDdiseaseIDSetMap_index.get(patientID) != null &&
						patientIDdiseaseIDSetMap_index.get(patientID).size() > 0 &&
						patientByIDmap_index.get(patientID) != null && 
						(patientFilter == null || (patientFilter != null &&	patientFilter.checkPatientFilters(patientByIDmap_index.get(patientID))) )))
				.filter(patientID -> {
					for(Integer diseaseToCheck : diseaseSetToCheck) {
						if(patientIDdiseaseIDSetMap_index.get(patientID).contains(diseaseToCheck)) {
							return true;
						}
					}
					return false;
				})
				.collect(Collectors.toSet());

		returnPatientIDset.addAll(patientIDset);
		 */

		Set<Integer> returnPatientIDset_preFilter = new HashSet<Integer>();
		for(Integer diseaseIDToCheck : diseaseSetToCheck) {
			Set<Integer> relatedPatientIDset = diseaseIDpatientIDSetMap_index.get(diseaseIDToCheck);
			// If null, it could happen that the disease code is mentioned in the pairing file but does not occur in the patient dataset
			if(relatedPatientIDset != null) {
				returnPatientIDset_preFilter.addAll(relatedPatientIDset);
			}
		}

		for(Integer patIDtoFilter : returnPatientIDset_preFilter) {
			Patient patToFilter = patientByIDmap_index.get(patIDtoFilter);
			if(patToFilter != null && (patientFilter == null || (patientFilter != null && patientFilter.checkPatientFilters(patToFilter)))) {
				returnPatientIDset.add(patIDtoFilter.intValue());
			}
		}

		return returnPatientIDset;
	}

	/**
	 * Get the IDs of all patients
	 * 
	 * @return
	 */
	public Set<Integer> getAllPatients(ComorbidityPatientFilter patientFilter) {

		if(patientFilter == null) {
			return patientByIDmap_index.keySet();
		}
		else {
			Set<Integer> patientIDset = new HashSet<Integer>();

			for(Patient patToFilter : patientList) {
				// APPLY FILTER - START
				if(patientFilter != null && !patientFilter.checkPatientFilters(patToFilter)) {
					continue;
				}
				// APPLY FILTER - END

				patientIDset.add(patToFilter.getIntId());
			}

			return patientIDset;
		}
	}

}
