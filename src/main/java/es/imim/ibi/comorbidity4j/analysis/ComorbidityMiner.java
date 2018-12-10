package es.imim.ibi.comorbidity4j.analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityDirectionalityFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityScoreFilter;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityDataset;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;
import es.imim.ibi.comorbidity4j.util.stat.AdjMethodENUM;
import gnu.trove.TCollections;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Core class to execute comorbidity analysis
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityMiner {

	private static final Logger logger = LoggerFactory.getLogger(ComorbidityMiner.class);

	// Static vars
	private static Integer execIDcounter = (new Random()).nextInt(1000000000);

	private static Random rnd = new Random();

	// Exec ID
	private String execID = null;

	private UserInputContainer userInputCont = null;

	// Property file path and property placeholder
	private boolean enableMultithread = false;
	private Integer numThreads = 3;

	private PatientAgeENUM patientAgeComputation = PatientAgeENUM.LAST_DIAGNOSTIC;
	private AdjMethodENUM pvalAdjApproach = AdjMethodENUM.BENJAMINI_HOCHBERG;

	private boolean isGenderEnabled = true;
	private String sexRatioMaleIdentifier = "M";
	private String sexRatioFemaleIdentifier = "F";

	private ComorbidityPatientFilter patientFilter = null;
	private ComorbidityScoreFilter scoreFilter = null;
	private ComorbidityDirectionalityFilter directionalityFilter = null;

	private Double relativeRiskConfindeceInterval = null;
	private Double oddsRatioConfindeceInterval = null;

	// Data loading vars
	private List<Patient> patientList = null;
	private List<Visit> visitList = null;
	private Map<String, Integer> diagnosisCodeStringIdMap = new HashMap<String, Integer>();
	private Map<String, String> diagnosisCodeStringDescriptionMap = new HashMap<String, String>();
	private Map<String, Set<String>> groupNameListCodesGroupMap = new HashMap<String, Set<String>>();
	private Map<String, String> diagnosisCodeStringGroupMap = new HashMap<String, String>();
	private Map<String, Set<String>> groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
	private Map<String, String> groupPairignMap = new HashMap<String, String>();

	private ComorbidityDataset comorDatasetObj = null;

	// Constructor
	public ComorbidityMiner() {
		super();
		execID = "exec_" + execIDcounter++;
	}


	// Setters and getters
	public List<Patient> getPatientList() {
		return patientList;
	}

	public void setPatientList(List<Patient> patientList) {
		this.patientList = patientList;
	}

	public List<Visit> getVisitList() {
		return visitList;
	}

	public void setVisitList(List<Visit> visitList) {
		this.visitList = visitList;
	}

	public Map<String, Integer> getDiagnosisCodeStringIdMap() {
		return diagnosisCodeStringIdMap;
	}

	public void setDiagnosisCodeStringIdMap(Map<String, Integer> diagnosisCodeStringIdMap) {
		this.diagnosisCodeStringIdMap = diagnosisCodeStringIdMap;
	}

	public Map<String, String> getDiagnosisCodeStringDescriptionMap() {
		return diagnosisCodeStringDescriptionMap;
	}

	public void setDiagnosisCodeStringDescriptionMap(Map<String, String> diagnosisCodeStringDescriptionMap) {
		this.diagnosisCodeStringDescriptionMap = diagnosisCodeStringDescriptionMap;
	}

	public Map<String, String> getDiagnosisCodeStringGroupMap() {
		return diagnosisCodeStringGroupMap;
	}

	public void setDiagnosisCodeStringGroupMap(Map<String, String> diagnosisCodeStringGroupMap) {
		this.diagnosisCodeStringGroupMap = diagnosisCodeStringGroupMap;
	}

	public Map<String, Set<String>> getGroupNameListCodesPairingMap() {
		return groupNameListCodesPairingMap;
	}

	public void setGroupNameListCodesPairingMap(Map<String, Set<String>> groupNameListCodesPairingMap) {
		this.groupNameListCodesPairingMap = groupNameListCodesPairingMap;
	}

	public Map<String, String> getGroupPairignMap() {
		return groupPairignMap;
	}

	public void setGroupPairignMap(Map<String, String> groupPairignMap) {
		this.groupPairignMap = groupPairignMap;
	}

	public ComorbidityDataset getComorDatasetObj() {
		return comorDatasetObj;
	}

	public void setComorDatasetObj(ComorbidityDataset comorDatasetObj) {
		this.comorDatasetObj = comorDatasetObj;
	}

	public boolean isEnableMultithread() {
		return enableMultithread;
	}

	public void setEnableMultithread(boolean enableMultithread) {
		this.enableMultithread = enableMultithread;
	}

	public Integer getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(Integer numThreads) {
		this.numThreads = numThreads;
	}

	public PatientAgeENUM getPatientAgeComputation() {
		return patientAgeComputation;
	}

	public void setPatientAgeComputation(PatientAgeENUM patientAgeComputation) {
		this.patientAgeComputation = patientAgeComputation;
	}

	public AdjMethodENUM getPvalAdjApproach() {
		return pvalAdjApproach;
	}

	public void setPvalAdjApproach(AdjMethodENUM pvalAdjApproach) {
		this.pvalAdjApproach = pvalAdjApproach;
	}

	public boolean isGenderEnabled() {
		return isGenderEnabled;
	}

	public void setGenderEnabled(boolean isGenderEnabled) {
		this.isGenderEnabled = isGenderEnabled;
	}

	public String getSexRatioMaleIdentifier() {
		return sexRatioMaleIdentifier;
	}

	public void setSexRatioMaleIdentifier(String sexRatioMaleIdentifier) {
		this.sexRatioMaleIdentifier = sexRatioMaleIdentifier;
	}

	public String getSexRatioFemaleIdentifier() {
		return sexRatioFemaleIdentifier;
	}

	public void setSexRatioFemaleIdentifier(String sexRatioFemaleIdentifier) {
		this.sexRatioFemaleIdentifier = sexRatioFemaleIdentifier;
	}

	public ComorbidityPatientFilter getPatientFilter() {
		return patientFilter;
	}

	public void setPatientFilter(ComorbidityPatientFilter patientFilter) {
		this.patientFilter = patientFilter;
	}

	public ComorbidityScoreFilter getScoreFilter() {
		return scoreFilter;
	}

	public void setScoreFilter(ComorbidityScoreFilter scoreFilter) {
		this.scoreFilter = scoreFilter;
	}

	public ComorbidityDirectionalityFilter getDirectionalityFilter() {
		return directionalityFilter;
	}

	public void setDirectionalityFilter(ComorbidityDirectionalityFilter directionalityFilter) {
		this.directionalityFilter = directionalityFilter;
	}
	
	public Double getRelativeRiskConfindeceInterval() {
		return relativeRiskConfindeceInterval;
	}

	public void setRelativeRiskConfindeceInterval(Double relativeRiskConfindeceInterval) {
		this.relativeRiskConfindeceInterval = relativeRiskConfindeceInterval;
	}

	public Double getOddsRatioConfindeceInterval() {
		return oddsRatioConfindeceInterval;
	}

	public void setOddsRatioConfindeceInterval(Double oddsRatioConfindeceInterval) {
		this.oddsRatioConfindeceInterval = oddsRatioConfindeceInterval;
	}

	public String getExecID() {
		return (execID != null) ? execID : null; 
	}

	public void setExecID(String execID) {
		this.execID = execID;
	}

	public UserInputContainer getUserInputCont() {
		return userInputCont;
	}

	public String loadAndIndexData(UserInputContainer userInputCont) {

		this.userInputCont = userInputCont;

		StringBuffer outputStr = new StringBuffer("");
		// --------------------------------------------------------------
		// 0) Reset vars
		patientList = null;
		visitList = null;
		diagnosisCodeStringIdMap = new HashMap<String, Integer>();
		diagnosisCodeStringDescriptionMap = new HashMap<String, String>();

		// Grouping 
		groupNameListCodesGroupMap = new HashMap<String, Set<String>>(); // For each group of diagnosis defined: key -> the group name / value -> the set of diagnosis codes belonging to the group
		diagnosisCodeStringGroupMap = new HashMap<String, String>(); // For each diagnosis code in a group: key -> the diagnosis code / value -> the group name

		// Pairing
		groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
		groupPairignMap = new HashMap<String, String>();

		// --------------------------------------------------------------
		// 3) Get Visit / Diagnosis Data
		visitList = (userInputCont.getVisitData_LOADED() != null) ? userInputCont.getVisitData_LOADED().data : new ArrayList<Visit>();
		diagnosisCodeStringIdMap = (userInputCont.getDiagnosisCodeIdStringMap() != null) ? userInputCont.getDiagnosisCodeIdStringMap() : new HashMap<String, Integer>();

		// --------------------------------------------------------------
		// 1) Get Studied Disease Pairing and Grouping Data
		// Populate Disease Grouping Map
		if(userInputCont.getGroupNameListCodesMap() != null && userInputCont.getGroupNameListCodesMap().size() > 0) {
			// Be sure that a group name is not equal to a diagnosis code
			Map<String, Set<String>> groupNameListCodesGroupMap_revised = new HashMap<String, Set<String>>();
			for(Entry<String, Set<String>> groupNameCodes : userInputCont.getGroupNameListCodesMap().entrySet()) {
				if(groupNameCodes != null && !Strings.isNullOrEmpty(groupNameCodes.getKey()) && 
						groupNameCodes.getValue() != null && groupNameCodes.getValue().size() > 0) {
					String groupName = groupNameCodes.getKey();

					// Rename group code if a diagnosis code equal to the group code already exists
					while(diagnosisCodeStringIdMap.containsKey(groupName)) {
						groupName = groupName + "-";
					}

					groupNameListCodesGroupMap_revised.put(groupName, groupNameCodes.getValue());
				}
			} 

			// Populate variables groupNameListCodesGroupMap and diagnosisCodeStringGroupMap
			groupNameListCodesGroupMap = groupNameListCodesGroupMap_revised;
			for(Entry<String, Set<String>> groupNameCodes : groupNameListCodesGroupMap.entrySet()) {
				if(groupNameCodes != null && !Strings.isNullOrEmpty(groupNameCodes.getKey()) && 
						groupNameCodes.getValue() != null && groupNameCodes.getValue().size() > 0) {
					String groupName = groupNameCodes.getKey();
					for(String codeInGroup : groupNameCodes.getValue()) {
						if(!Strings.isNullOrEmpty(codeInGroup)) {
							diagnosisCodeStringGroupMap.put(codeInGroup, groupName);
						}
					}
				}
			}
		}

		// Populate Disease Pairing Map
		groupNameListCodesPairingMap = (userInputCont.getGroupNameListCodesPairingMap() != null) ? userInputCont.getGroupNameListCodesPairingMap() : new HashMap<String, Set<String>>();
		groupPairignMap = (userInputCont.getGroupPairignMap() != null) ? userInputCont.getGroupPairignMap() : new HashMap<String, String>();


		// --------------------------------------------------------------
		// 2) Get Patient Data
		patientList = (userInputCont.getPatientData_LOADED() != null) ? userInputCont.getPatientData_LOADED().data : new ArrayList<Patient>();

		// --------------------------------------------------------------
		// 5) Load Diagnosis Description Data (OPTIONAL)
		// For diagnosis codes not in group: set as description the one present in the map userInputCont.getDescrDiagnosisData_LOADED().data
		// For diagnosis codes in group: set the code equal to the group code and the description equal to the number of group elements
		// IMPORTANT: in this way all the group code have a description associated
		if(userInputCont.getDescrDiagnosisData_LOADED() != null && userInputCont.getDescrDiagnosisData_LOADED().data != null && userInputCont.getDescrDiagnosisData_LOADED().data.size() > 0) {

			// Populate Disease Description map
			for(Entry<String, String> codeDescriptionPair : userInputCont.getDescrDiagnosisData_LOADED().data.entrySet()) {
				if(codeDescriptionPair != null && codeDescriptionPair.getKey() != null && codeDescriptionPair.getValue() != null &&
						!Strings.isNullOrEmpty(codeDescriptionPair.getValue())) {
					String diagnosisCode = codeDescriptionPair.getKey();

					// Get the group name as diagnosis code if the diagnosis belongs to a group
					boolean isInGroup = false;
					if(diagnosisCodeStringGroupMap != null && diagnosisCodeStringGroupMap.containsKey(codeDescriptionPair.getKey()) &&
							!Strings.isNullOrEmpty(diagnosisCodeStringGroupMap.get(codeDescriptionPair.getKey())) ) {
						diagnosisCode = diagnosisCodeStringGroupMap.get(codeDescriptionPair.getKey());
						isInGroup = true;
					}

					String diagnosisCodeDescription = codeDescriptionPair.getValue();
					if(isInGroup) {
						diagnosisCodeDescription = "GROUP OF " + 
								((groupNameListCodesGroupMap.containsKey(diagnosisCode) && groupNameListCodesGroupMap.get(diagnosisCode) != null) ? groupNameListCodesGroupMap.get(diagnosisCode).size() : " ") + " DIAGNOSES";
					}

					if(!diagnosisCodeStringDescriptionMap.containsKey(diagnosisCode)) {
						diagnosisCodeStringDescriptionMap.put(diagnosisCode, diagnosisCodeDescription);
					}

				}
			}

		}

		// --------------------------------------------------------------
		// 6) Create the ComorbidityDataset
		comorDatasetObj = new ComorbidityDataset(patientList, visitList, diagnosisCodeStringIdMap, diagnosisCodeStringDescriptionMap,
				groupNameListCodesGroupMap, diagnosisCodeStringGroupMap, groupNameListCodesPairingMap, groupPairignMap, this);
		
		return outputStr.toString();
	}

	/**
	 * Get string describing the loaded dataset
	 * 
	 * @param detailedLog
	 * @return
	 */
	public String printDatasetStats(boolean detailedLog) {
		return (comorDatasetObj != null) ? comorDatasetObj.dataChecks(detailedLog) : "No dataset loaded";
	}

	/**
	 * Get string describing the current comorbidity analysis properties
	 * 
	 * @param detailedLog
	 * @return
	 */
	@Override
	public String toString() {
		return "ComorbidityMiner [execID=" + ((execID != null) ? execID : "null") + 
				"\n enableMultithread=" + enableMultithread + 
				"\n numThreads=" + ((numThreads != null) ? numThreads : "null") + 
				"\n patientAgeComputation=" + ((patientAgeComputation != null) ? patientAgeComputation : "null") +
				"\n pvalAdjApproach=" + ((pvalAdjApproach != null) ? pvalAdjApproach : "null") +
				"\n sexRatioFemaleIdentifier=" + ((sexRatioFemaleIdentifier != null) ? sexRatioFemaleIdentifier : "null") +
				"\n sexRatioMaleIdentifier=" + ((sexRatioMaleIdentifier != null) ? sexRatioMaleIdentifier : "null") +
				"\n patientFilter=" + ((patientFilter != null) ? patientFilter : "null") + 
				"\n scoreFilter=" + ((scoreFilter != null) ? scoreFilter : "null") + 
				"\n directionalityFilter=" + ((directionalityFilter != null) ? directionalityFilter : "null") +
				"\n relativeRiskConfindeceInterval=" + ((relativeRiskConfindeceInterval != null) ? relativeRiskConfindeceInterval : "null") + 
				"\n oddsRatioConfindeceInterval=" + ((oddsRatioConfindeceInterval != null) ? oddsRatioConfindeceInterval : "null") + 
				"\n patientListSize=" + ((patientList != null) ? patientList.size() : "null") + 
				"\n visitList=" + ((visitList != null) ? visitList.size() : "null") +
				"\n diagnosisCodeStringIdMapSize=" + ((diagnosisCodeStringIdMap != null) ? diagnosisCodeStringIdMap.size() : "null") + 
				"\n diagnosisCodeStringDescriptionMapSize=" + ((diagnosisCodeStringDescriptionMap != null) ? diagnosisCodeStringDescriptionMap.size() : "null") + 
				"\n comorDatasetObj=" + ((comorDatasetObj != null) ? comorDatasetObj : "null") + "]";
	}

	/**
	 * Exploited in executeAnalysis() method
	 * 
	 * Populate computation cache by adding:
	 * - for diseases A and B the number of males, females and all
	 * 
	 * @param comorDatasetObj
	 */
	public void populateCache(ComorbidityDataset comorDatasetObj, Integer diseaseA_ID, Integer diseaseB_ID,
			ComorbidityMinerCache computationCache, TIntSet singleDiseaseAddedSet,
			ComorbidityPatientFilter femaleANDmalePatFilter, ComorbidityPatientFilter femalePatFilter, ComorbidityPatientFilter malePatFilter) {

		// Populate computation cache > disease A and B - START	
		if(!singleDiseaseAddedSet.contains(diseaseA_ID)) {
			singleDiseaseAddedSet.add(diseaseA_ID);

			// Both males and females - DISEASE A
			Set<Integer> patientIDs_disA = computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseA_ID);
			if(patientIDs_disA  == null) {
				patientIDs_disA = comorDatasetObj.getPatientsWithALLDiseases(femaleANDmalePatFilter, diseaseA_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_ALL().put(diseaseA_ID, patientIDs_disA);
			}

			// Only Female - DISEASE A
			Set<Integer> femalesWithDiseaseA = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseA_ID);
			if(femalesWithDiseaseA == null) {
				femalesWithDiseaseA = comorDatasetObj.getPatientsWithALLDiseases(femalePatFilter, diseaseA_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().put(diseaseA_ID, femalesWithDiseaseA);
			}

			// Only Male - DISEASE A
			Set<Integer> malesWithDiseaseA = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseA_ID);
			if(malesWithDiseaseA == null) {
				malesWithDiseaseA = comorDatasetObj.getPatientsWithALLDiseases(malePatFilter, diseaseA_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_MALE().put(diseaseA_ID, malesWithDiseaseA);
			}
		}

		if(!singleDiseaseAddedSet.contains(diseaseB_ID)) {
			singleDiseaseAddedSet.add(diseaseB_ID);

			// Both males and females - DISEASE Bs
			Set<Integer> patientIDs_disB = computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseB_ID);
			if(patientIDs_disB == null) {
				patientIDs_disB = comorDatasetObj.getPatientsWithALLDiseases(femaleANDmalePatFilter, diseaseB_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_ALL().put(diseaseB_ID, patientIDs_disB);
			}

			// Only Female - DISEASE B
			Set<Integer> femalesWithDiseaseB = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseB_ID);
			if(femalesWithDiseaseB == null) {
				femalesWithDiseaseB = comorDatasetObj.getPatientsWithALLDiseases(femalePatFilter, diseaseB_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().put(diseaseB_ID, femalesWithDiseaseB);

			}

			// Only Male - DISEASE B
			Set<Integer> malesWithDiseaseB = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseB_ID);
			if(malesWithDiseaseB == null) {
				malesWithDiseaseB = comorDatasetObj.getPatientsWithALLDiseases(malePatFilter, diseaseB_ID);
				computationCache.getDiseaseIDpaitnetIDsetMap_MALE().put(diseaseB_ID, malesWithDiseaseB);
			}
		}
		// Populate computation cache > disease A and B - END		
	}
	
	public AtomicLong pairConsidered_preproc = new AtomicLong(0);
	public AtomicLong pairSelected_preproc = new AtomicLong(0);
	public Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cachedComorbidityPaitsToAnalyze_ALL = null;
	public Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cachedComorbidityPaitsToAnalyze_FEMALE = null;
	public Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cachedComorbidityPaitsToAnalyze_MALE = null;
	public Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> loadComorbidityPaitsToAnalyze(boolean forceRecomputation, Boolean femaleMaleAll) {
		
		pairConsidered_preproc = new AtomicLong(0);
		pairSelected_preproc = new AtomicLong(0);
		
		if(!forceRecomputation && femaleMaleAll == null && cachedComorbidityPaitsToAnalyze_ALL != null) {
			return cachedComorbidityPaitsToAnalyze_ALL;
		}
		
		if(!forceRecomputation && femaleMaleAll == true && cachedComorbidityPaitsToAnalyze_FEMALE != null) {
			return cachedComorbidityPaitsToAnalyze_FEMALE;
		}
		
		if(!forceRecomputation && femaleMaleAll == false && cachedComorbidityPaitsToAnalyze_MALE != null) {
			return cachedComorbidityPaitsToAnalyze_MALE;
		}
		
		boolean populateCache = true;
		
		ComorbidityMinerCache computationCache = new ComorbidityMinerCache();

		// 1) Build all, male and female patient filters and eventually store the filtered patient ID set in the cache
		ComorbidityPatientFilter femaleANDmalePatFilter = (patientFilter != null) ?	new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null, null, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null,	patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, null, null, null);
		ComorbidityPatientFilter femalePatFilter = (patientFilter != null) ? new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null,	this.sexRatioFemaleIdentifier, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null, patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, this.sexRatioFemaleIdentifier, null, null);
		ComorbidityPatientFilter malePatFilter = (patientFilter != null) ? new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null, this.sexRatioMaleIdentifier, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null, patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, this.sexRatioMaleIdentifier, null, null);

		if(populateCache) {
			computationCache.setAllPatientIDs_ALL(comorDatasetObj.getAllPatients(femaleANDmalePatFilter));
			computationCache.setAllPatientIDs_FEMALE(comorDatasetObj.getAllPatients(femalePatFilter));
			computationCache.setAllPatientIDs_MALE(comorDatasetObj.getAllPatients(malePatFilter));
		}
		
		// 2) Check and sanitize multi-thread info
		ExecutorService executor = null;
		List<Future<Map<Integer, TIntSet>>> threadFutureList = null;

		// If enable multi-threading, compute pairs per thread
		if(this.enableMultithread) {
			this.numThreads = (this.numThreads != null && this.numThreads > 1) ? this.numThreads : 2;

			executor = Executors.newFixedThreadPool(this.numThreads);
			threadFutureList = new ArrayList<Future<Map<Integer, TIntSet>>>();
		}
				
		// 3) Compute all the pairs of diseases to analyze and eventually (if populateCache == true) cache info of all diseases in the pair
		TIntSet diseaseIDsCachedSet = TCollections.synchronizedSet(new TIntHashSet());
		
		ComorbidityPatientFilter femaleANDmalePatFilter_SELECTED = null;
		if(femaleMaleAll == null) {
			femaleANDmalePatFilter_SELECTED = femaleANDmalePatFilter;
		}
		else if(femaleMaleAll == true) {
			femaleANDmalePatFilter_SELECTED = femalePatFilter;
		}
		else {
			femaleANDmalePatFilter_SELECTED = malePatFilter;
		}
		
		Map<Integer, TIntSet> diseasePairsToAnalyze = new ConcurrentHashMap<Integer, TIntSet>();
		for(Entry<String, Integer> diagnosisCodeStringID : this.comorDatasetObj.getDiagnosisCodeStringIdMap().entrySet()) {
			if(diagnosisCodeStringID != null && !Strings.isNullOrEmpty(diagnosisCodeStringID.getKey()) && diagnosisCodeStringID.getValue() != null) {
				// Get the diagnosis paired with the diagnosis ID

				Callable<Map<Integer, TIntSet>> worker = new ComorbidityLoaderThread(this, diseasePairsToAnalyze, pairConsidered_preproc,
						pairSelected_preproc, Pair.of(diagnosisCodeStringID.getKey(), diagnosisCodeStringID.getValue()),
						femaleANDmalePatFilter_SELECTED, femaleANDmalePatFilter, femalePatFilter, malePatFilter, 
						computationCache, diseaseIDsCachedSet);
				Future<Map<Integer, TIntSet>> submit = executor.submit(worker);
				threadFutureList.add(submit);
				
			}
		}
		
		// 4) Wait for thread to finish
		
		for (Future<Map<Integer, TIntSet>> threadFutureListElem : threadFutureList) {
			try {
				Map<Integer, TIntSet> pairResultMap = threadFutureListElem.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Map<Integer, Set<Integer>> diseasePairsToAnalyze_Set = new HashMap<Integer, Set<Integer>>();
		
		for(Entry<Integer, TIntSet> diseasePairsToAnalyzeEntry : diseasePairsToAnalyze.entrySet()) {
			if(!diseasePairsToAnalyze_Set.containsKey(diseasePairsToAnalyzeEntry.getKey())) {
				diseasePairsToAnalyze_Set.put(diseasePairsToAnalyzeEntry.getKey(), new HashSet<Integer>());
			}
			
			int[] intArray = diseasePairsToAnalyzeEntry.getValue().toArray();
			for(int index = 0; index < intArray.length; index++) {
				diseasePairsToAnalyze_Set.get(diseasePairsToAnalyzeEntry.getKey()).add(intArray[index]);
			}
			
		}
		
		System.out.println(" *** > Selected " + pairSelected_preproc.get() + " pairs over " + pairConsidered_preproc.get() + " pairs considered.");
		
		if(executor != null) {
			executor.shutdown();
		}
		
		int diseasePairsToAnalyzeCount = 0;
		for(Entry<Integer, Set<Integer>> disPair : diseasePairsToAnalyze_Set.entrySet()) {
			if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
				diseasePairsToAnalyzeCount += disPair.getValue().size();
			}
		}

		String outString = execID + " Total comorbidity pairs to consider " + diseasePairsToAnalyzeCount + " (directionality enabled: " + ((directionalityFilter == null || directionalityFilter.getMinNumDays() == null || directionalityFilter.getMinNumDays() <= 0l) ? "DISABLED" : "ENABLED-" + directionalityFilter.getMinNumDays() + "days") + ") ";
		logger.debug(outString);
		outString += "\n" + outString;
		System.out.println("\n" + outString);

		System.gc();

		// DiseasePairsToAnalyze contains all the disease pairs to analyze for comorbidity 

		/* TO DELETE 
		List<ImmutablePair<String, String>> disisNew = new ArrayList<ImmutablePair<String, String>>();
		for(int i = 0; i < 3; i++) {
			disisNew.add(diseasePairsToAnalyze.get(i));
		}
		diseasePairsToAnalyze = disisNew;
		 */
		
		if(femaleMaleAll == null) {
			cachedComorbidityPaitsToAnalyze_ALL = Pair.of(computationCache, diseasePairsToAnalyze_Set);
			return cachedComorbidityPaitsToAnalyze_ALL;
		}
		else if(femaleMaleAll == true) {
			cachedComorbidityPaitsToAnalyze_FEMALE = Pair.of(computationCache, diseasePairsToAnalyze_Set);
			return cachedComorbidityPaitsToAnalyze_FEMALE;
		}
		else {
			cachedComorbidityPaitsToAnalyze_MALE = Pair.of(computationCache, diseasePairsToAnalyze_Set);
			return cachedComorbidityPaitsToAnalyze_MALE;
		}
		
	}

	/**
	 * Execute comorbidity analysis with current object settings / parameters 
	 * 
	 * @return pair with left side String with error log (null if no error are rpesent while processing data)
	 */
	public AtomicLong processedPairsCounter = new AtomicLong(0);
	public long totalPairsToProcessCounter = 0l;
	public ImmutablePair<String, Collection<ComorbidityPairResult>> executeAnalysis(Boolean femaleMaleAll, Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cacheAndPairs, ComorbidityDataset comorDataset) {

		Map<Integer, Set<Integer>> diseasePairsToAnalyze = cacheAndPairs.getRight();
		ComorbidityMinerCache computationCache = cacheAndPairs.getLeft();

		processedPairsCounter = new AtomicLong(0);
		totalPairsToProcessCounter = 0l;

		int processedPairCountExt = 0;
		double prevPercProcessedPairsPritnedExt = 0.0d;
		DecimalFormat decimFormat = new DecimalFormat("#.000");
		final long analysisStartTimeExt = System.currentTimeMillis();
		String errorStr = "";
		String outString = "";

		totalPairsToProcessCounter = 0;
		for(Entry<Integer, Set<Integer>> disPair : diseasePairsToAnalyze.entrySet()) {
			if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
				totalPairsToProcessCounter += disPair.getValue().size();
			}
		}

		// Return variables
		List<ComorbidityPairResult> resultPairAnalysisList = new ArrayList<ComorbidityPairResult>(); // Preallocation: (int) ((totalPairsToProcessCounter + 50000d))

		// Female and male patient filters
		ComorbidityPatientFilter femaleANDmalePatFilter = (patientFilter != null) ?	new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null, null, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null, patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, null, null, null);
		ComorbidityPatientFilter femalePatFilter = (patientFilter != null) ? new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null,	this.sexRatioFemaleIdentifier, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null, patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, this.sexRatioFemaleIdentifier, null, null);
		ComorbidityPatientFilter malePatFilter = (patientFilter != null) ? new ComorbidityPatientFilter((patientFilter != null && patientFilter.getMinAgeFP() != null) ? new Long(patientFilter.getMinAgeFP()) : null, (patientFilter != null && patientFilter.getMaxAgeFP() != null) ? new Long(patientFilter.getMaxAgeFP()) : null, this.sexRatioMaleIdentifier, (patientFilter != null && patientFilter.getFacet_1FP() != null) ? patientFilter.getFacet_1FP() : null, patientFilter.getAgeComputationMethod()) : new ComorbidityPatientFilter(null, null, this.sexRatioMaleIdentifier, null, null);


		// Check and sanitize multi-thread info
		ExecutorService executor = null;
		List<Pair<Pair<Integer, String>, Pair<Integer, String>>> appoDiseasePairToProcessThread = new ArrayList<Pair<Pair<Integer, String>, Pair<Integer, String>>>();
		LinkedHashMap<Future<List<ComorbidityPairResult>>, Long> threadFutureMap = null;

		// If enable multi-threading, compute pairs per thread
		int pairsPerThread = 100;
		if(this.enableMultithread) {
			this.numThreads = (this.numThreads != null && this.numThreads > 1) ? this.numThreads : 2;

			executor = Executors.newFixedThreadPool(this.numThreads);
			threadFutureMap = new LinkedHashMap<Future<List<ComorbidityPairResult>>, Long>();

			if(this.numThreads != null && this.numThreads > 0) {
				Double pairsPerThreadDouble = ( ((double) totalPairsToProcessCounter) + ((int) (1d + ((double) totalPairsToProcessCounter * 0.001d))) ) / ((double) (this.numThreads));
				pairsPerThread = pairsPerThreadDouble > 1d ? pairsPerThreadDouble.intValue() : 1;
			}

			outString = execID + " START MULTITHREADING EXECUTION WITH A POOL OF " + this.numThreads + " THREADS / SUBMITTING " + pairsPerThread + " DISEASE PAIRS PER THREAD...";
			logger.debug(outString);
			outString += "\n" + outString;
			System.out.println("\n" + outString);
		}

		for(Entry<Integer, Set<Integer>> disPair : diseasePairsToAnalyze.entrySet()) {

			if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
				Integer diseaseA_ID = disPair.getKey();

				for(Integer disPairSet : disPair.getValue()) {

					Integer diseaseB_ID = disPairSet;

					if(diseaseA_ID == diseaseB_ID) {
						continue;
					}

					String diseaseA_CODE = (comorDataset.getDiagnosisIdCodeStringMap() != null && comorDataset.getDiagnosisIdCodeStringMap().containsKey(diseaseA_ID) && comorDataset.getDiagnosisIdCodeStringMap().get(diseaseA_ID) != null) ? comorDataset.getDiagnosisIdCodeStringMap().get(diseaseA_ID) : "NULL";
					String diseaseB_CODE = (comorDataset.getDiagnosisIdCodeStringMap() != null && comorDataset.getDiagnosisIdCodeStringMap().containsKey(diseaseB_ID) && comorDataset.getDiagnosisIdCodeStringMap().get(diseaseB_ID) != null) ? comorDataset.getDiagnosisIdCodeStringMap().get(diseaseB_ID) : "NULL";

					try {
						processedPairCountExt++;

						// Print processing stats
						double percProcessedPairs = ((double) processedPairCountExt) / totalPairsToProcessCounter;
						if(percProcessedPairs > (prevPercProcessedPairsPritnedExt + 0.02d)) {
							prevPercProcessedPairsPritnedExt = percProcessedPairs;
							double secondsElapsed = ((double) (System.currentTimeMillis() - analysisStartTimeExt)) / 1000d;
							double pairsPerSecond = ((secondsElapsed > 0d) ? ( ((double) processedPairCountExt) / secondsElapsed): -1d);

							outString = execID + " " + ((this.enableMultithread) ? "MULTI-THREAD: Ready for processing " : "SINGLE-THREAD> Processed ") + 
									processedPairCountExt + " pairs: " + decimFormat.format(percProcessedPairs * 100d) + "% of " + totalPairsToProcessCounter + " total pairs to process" +
									"\n       Analysis time: " + decimFormat.format(secondsElapsed) + " seconds - avg. " + ((this.enableMultithread) ? "read" : "processed") + " disease pairs per second: " + decimFormat.format(pairsPerSecond);

							logger.debug(outString);
							outString += "\n" + outString;
							System.out.println("\n" + outString);

							System.out.println(GenericUtils.printHeapState());
						}

						// Start processing the pair after checking the directionality filter
						if(!this.enableMultithread) {
							// Execute analysis of pair
							ComorbidityPairResult pair = ComorbidityPairCalculator.computeComorbidityPair(diseaseA_ID, diseaseA_CODE, diseaseB_ID, diseaseB_CODE, this, computationCache, femaleMaleAll,
									femaleANDmalePatFilter, femalePatFilter, malePatFilter);

							if(pair != null) {
								resultPairAnalysisList.add(pair);
								processedPairsCounter.incrementAndGet();
							}
						}
						else {
							Pair<Integer, String> pairToProcess_A = Pair.of(diseaseA_ID, diseaseA_CODE);
							Pair<Integer, String> pairToProcess_B = Pair.of(diseaseB_ID, diseaseB_CODE);
							appoDiseasePairToProcessThread.add(Pair.of(pairToProcess_A, pairToProcess_B));

							if(processedPairCountExt % pairsPerThread == 0) {
								// Create and submit thread and re-init disease pair list
								Callable<List<ComorbidityPairResult>> worker = new ComorbidityPairCalculatorThread(appoDiseasePairToProcessThread, this, computationCache, femaleMaleAll,
										femaleANDmalePatFilter, femalePatFilter, malePatFilter);
								Future<List<ComorbidityPairResult>> submit = executor.submit(worker);
								threadFutureMap.put(submit, null);
								appoDiseasePairToProcessThread = new ArrayList<Pair<Pair<Integer, String>, Pair<Integer, String>>>();
							}

						}

					} catch (Exception e) {
						logger.debug(execID + " ERROR: Exception while processing disease pair: (" +
								diseaseA_CODE + ", " + diseaseB_CODE + ") - " + e.getMessage());
						errorStr += execID + " ERROR: Exception while processing disease pair: (" +
								diseaseA_CODE + ", " + diseaseB_CODE + ") - " + e.getMessage();
					}

				}
			}

		}

		if(appoDiseasePairToProcessThread != null && appoDiseasePairToProcessThread.size() > 0) {
			Callable<List<ComorbidityPairResult>> worker = new ComorbidityPairCalculatorThread(appoDiseasePairToProcessThread, this, computationCache, femaleMaleAll,
					femaleANDmalePatFilter, femalePatFilter, malePatFilter);
			Future<List<ComorbidityPairResult>> submit = executor.submit(worker);
			threadFutureMap.put(submit, null);
			appoDiseasePairToProcessThread = new ArrayList<Pair<Pair<Integer, String>, Pair<Integer, String>>>();
		}


		// Synchronize threads if enabled multi-threading
		if(this.enableMultithread) {

			outString = execID + " WAITING FOR " + threadFutureMap.size() + " THREADS TO FINISH... ";
			logger.debug(outString);
			outString += "\n" + outString;
			System.out.println("\n" + outString);

			double prevPercProcessedPairsPritned = 0.0d;
			final long analysisStartTime = System.currentTimeMillis();

			for (Entry<Future<List<ComorbidityPairResult>>, Long> threadFutureMapElem : threadFutureMap.entrySet()) {
				try {
					List<ComorbidityPairResult> pairResultMap = threadFutureMapElem.getKey().get();
					resultPairAnalysisList.addAll(pairResultMap);

					double percProcessedPairs = ((double) processedPairsCounter.get()) / totalPairsToProcessCounter;
					if(percProcessedPairs > (prevPercProcessedPairsPritned + 0.02d)) {
						prevPercProcessedPairsPritned = percProcessedPairs;
						double secondsElapsed = ((double) (System.currentTimeMillis() - analysisStartTime)) / 1000d;
						double pairsPerSecond = ((secondsElapsed > 0d) ? ( ((double) processedPairsCounter.get()) / secondsElapsed): -1d);

						outString = execID + " MULTI-THREAD: Processed " + processedPairsCounter + " pairs: " + decimFormat.format(percProcessedPairs * 100d) + "% of " + totalPairsToProcessCounter + " total pairs to process" +
								"\n       Analysis time: " + decimFormat.format(secondsElapsed) + " seconds - avg. processed disease pairs per second: " + decimFormat.format(pairsPerSecond);

						logger.debug(outString);
						outString += "\n" + outString;
						System.out.println("\n" + outString);

						System.out.println(GenericUtils.printHeapState());
					}

					/*
					for(Entry<Long, ComorbidityPairResult> pairResultMapElem : pairResultMap.entrySet()) {
						if(pairResultMapElem != null && pairResultMapElem.getKey() != null && pairResultMapElem.getValue() != null) {
							resultPairAnalysisMap.put(pairResultMapElem.getKey(), pairResultMapElem.getValue());
						}
					}
					 */

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			executor.shutdown();
		}


		double secondsElapsed = ((double) (System.currentTimeMillis() - analysisStartTimeExt)) / 1000d;
		double pairsPerSecond = ((secondsElapsed > 0d) ? ( ((double) processedPairCountExt) / secondsElapsed): -1d);
		outString = execID + " ANALYSIS COMPLETED " + ((this.enableMultithread) ? "MULTI-THREAD " : "SINGLE-THREAD ") + 
				"- " + processedPairsCounter + " disease pairs processed" +
				"\n       Analysis time: " + decimFormat.format(secondsElapsed) + " seconds - avg. processed disease pairs per second: " + decimFormat.format(pairsPerSecond);

		logger.debug(outString);
		outString += "\n" + outString;
		System.out.println("\n" + outString);

		System.out.println(GenericUtils.printHeapState());
		System.gc();

		TLongObjectHashMap<ComorbidityPairResult> resultPairAnalysisMap = new TLongObjectHashMap<ComorbidityPairResult>();
		for(ComorbidityPairResult pair : resultPairAnalysisList) {
			if(pair != null && pair.getDisAcodeNum() != null && pair.getDisBcodeNum() != null) {
				Long pairId = pair.getDisAcodeNum().longValue() + 1000000l * pair.getDisBcodeNum().longValue();
				resultPairAnalysisMap.put(pairId, pair);
			}
		}


		// Adjust p-values
		adjustPvalue(resultPairAnalysisMap, this.pvalAdjApproach);

		// APPLY DIAGNOSIS CODE STRING DESCRIPTION INDEX FILTERS - START
		// Delete all the comorbidity pairs that are not identified by an ID
		if(diagnosisCodeStringDescriptionMap != null && diagnosisCodeStringDescriptionMap.size() > 0) {
			TLongObjectIterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap.iterator();
			Set<Long> keyOfPairsToDelete = new HashSet<Long>();
			while(resultPairAnalysisMapIterator.hasNext()){
				resultPairAnalysisMapIterator.advance();
				Long keyValue = resultPairAnalysisMapIterator.key();
				ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.value();

				if(comoPair == null || !diagnosisCodeStringIdMap.containsKey(comoPair.getDisAcode()) || !diagnosisCodeStringIdMap.containsKey(comoPair.getDisBcode()) ) {
					keyOfPairsToDelete.add(keyValue);
				}
			}

			for(Long keyOfPairToDelete : keyOfPairsToDelete) {
				if(keyOfPairToDelete != null) {
					resultPairAnalysisMap.remove(keyOfPairToDelete);
				}
			}
		}
		// APPLY DIAGNOSIS CODE STRING DESCRIPTION INDEX FILTERS - END

		// APPLY SCORE FILTERS ADJUSTED P-VALUE - START
		if(scoreFilter != null) {
			int comorPairsBeforeFiltering = resultPairAnalysisMap.size();

			TLongObjectIterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap.iterator();
			Set<Long> keyOfPairsToDelete = new HashSet<Long>();
			while(resultPairAnalysisMapIterator.hasNext()){
				resultPairAnalysisMapIterator.advance();
				Long keyValue = resultPairAnalysisMapIterator.key();
				ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.value();

				if(comoPair != null) {
					// APPLY SCORE FILTER
					if(!scoreFilter.checkScoreFilters(comoPair, true)) {
						// The comorbidity pair did not pass the filter
						keyOfPairsToDelete.add(keyValue);
						continue;
					}
					// APPLY SCORE FILTER
				}
			}

			for(Long keyOfPairToDelete : keyOfPairsToDelete) {
				if(keyOfPairToDelete != null) {
					resultPairAnalysisMap.remove(keyOfPairToDelete);
				}
			}

			outString = execID + " COMORBIDITY SCORE AND NUM PATIENT FILTER: " + scoreFilter.toString() + "\n" +
					"\n       After filter application selected " + resultPairAnalysisMap.size() + " over " + comorPairsBeforeFiltering;

			logger.debug(outString);
			outString += "\n" + outString;
			System.out.println("\n" + outString);
		}
		// APPLY SCORE FILTERS ADJUSTED P-VALUE - END

		errorStr = (errorStr == null || errorStr.trim().length() == 0) ? null : errorStr;

		return ImmutablePair.of(errorStr, resultPairAnalysisMap.valueCollection());
	}


	/**
	 * From R function 'p.adjust'
	 * 
	 * @param resultPairAnalysisMap
	 * @param adjMethod
	 */
	private void adjustPvalue(TLongObjectHashMap<ComorbidityPairResult> resultPairAnalysisMap, AdjMethodENUM adjMethod) {

		// Get a map with all non null p-values
		Map<Long, Double> pairStrFisherMap = new HashMap<Long, Double>();
		TLongObjectIterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap.iterator();
		while(resultPairAnalysisMapIterator.hasNext()){
			resultPairAnalysisMapIterator.advance();
			Long keyValue = resultPairAnalysisMapIterator.key();
			ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.value();

			if(comoPair != null && comoPair.getFisherTest() != null) {
				pairStrFisherMap.put(keyValue, comoPair.getFisherTest());
			}
		}

		int numPairs = pairStrFisherMap.size();

		// Apply adjustment
		if(numPairs == 1) {
			// If only one comorbidity pair, set the Fisher test value equal to the adjusted Fisher test value for that pair
			TLongObjectIterator<ComorbidityPairResult> resultPairAnalysisMapIterator_int = resultPairAnalysisMap.iterator();
			while(resultPairAnalysisMapIterator_int.hasNext()){
				resultPairAnalysisMapIterator_int.advance();
				// Long keyValue = resultPairAnalysisMapIterator.key();
				ComorbidityPairResult comoPair = resultPairAnalysisMapIterator_int.value();

				comoPair.setFisherTestAdjusted(comoPair.getFisherTest());
			}
		}
		else if(numPairs > 1) {

			if(numPairs == 2 && adjMethod.equals(AdjMethodENUM.HOMMEL)) {
				adjMethod = AdjMethodENUM.HOCHBERG;
			}

			switch(adjMethod) {
			case BONFERRONI:
				for(Entry<Long, Double> pairStrFisherMapEntry : pairStrFisherMap.entrySet()) {
					if(pairStrFisherMapEntry != null && pairStrFisherMapEntry.getKey() != null &&
							pairStrFisherMapEntry.getValue() != null && resultPairAnalysisMap.containsKey(pairStrFisherMapEntry.getKey()) &&
							resultPairAnalysisMap.get(pairStrFisherMapEntry.getKey()) != null) {
						double adjustedVal = pairStrFisherMapEntry.getValue() * ((double) numPairs);
						adjustedVal = (adjustedVal > 1d) ? 1d : adjustedVal;
						resultPairAnalysisMap.get(pairStrFisherMapEntry.getKey()).setFisherTestAdjusted(adjustedVal);
					}
				}
				break;

			case HOLM:

				// 1) Put the individual p-values in ascending order
				Map<Long, Double> pairStrFisherMapSortedIncr_HOLM = GenericUtils.sortByValue(pairStrFisherMap, null);
				List<Long> pairStrFisherMapSortedIncrKeyList_HOLM = new ArrayList<>(pairStrFisherMapSortedIncr_HOLM.keySet());

				// 2) Adjust p-values
				List<Double> newPvalueOrdered = new ArrayList<Double>();
				Double cumMaxVal_HOLM = 0d;
				for(int i = 0; i < pairStrFisherMapSortedIncrKeyList_HOLM.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedIncrKeyList_HOLM.get(i);
					Double valueOfPval = pairStrFisherMap.get(keyOfPval);
					Double newValueOfPval = valueOfPval * (((double) numPairs) - ((double) i + 1) + 1d);

					if(newValueOfPval < cumMaxVal_HOLM) {
						newValueOfPval = cumMaxVal_HOLM;
					}
					else {
						cumMaxVal_HOLM = newValueOfPval;
					}

					newPvalueOrdered.add(newValueOfPval);
				}

				// 3) Set new adjusted p-values
				for(int i = 0; i < pairStrFisherMapSortedIncrKeyList_HOLM.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedIncrKeyList_HOLM.get(i);
					Double adjPvalue = newPvalueOrdered.get(i);
					if(keyOfPval != null && adjPvalue != null &&
							resultPairAnalysisMap.containsKey(keyOfPval) &&
							resultPairAnalysisMap.get(keyOfPval) != null) {
						// Limit to 1 the value of adjusted p-values
						adjPvalue = (adjPvalue > 1d) ? 1d : adjPvalue;
						resultPairAnalysisMap.get(keyOfPval).setFisherTestAdjusted(adjPvalue);
					}
				}

				break;

			case HOMMEL:

				// 1) Put the individual p-values in ascending order
				Map<Long, Double> pairStrFisherMapSortedIncr_HOMMEL = GenericUtils.sortByValue(pairStrFisherMap, null);
				List<Long> pairStrFisherMapSortedIncrKeyList_HOMMEL = new ArrayList<>(pairStrFisherMapSortedIncr_HOMMEL.keySet());

				// 2) Build array of minima
				double[] p = new double[pairStrFisherMapSortedIncrKeyList_HOMMEL.size()];
				Double minVal = null;
				for(int i = 0; i < pairStrFisherMapSortedIncrKeyList_HOMMEL.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedIncrKeyList_HOMMEL.get(i);
					Double valueOfPval = pairStrFisherMap.get(keyOfPval);

					p[i] = valueOfPval;

					Double adjPval = (((double) numPairs) * valueOfPval) / ((double) i + 1);

					if(minVal == null || minVal > adjPval) {
						minVal = adjPval;
					}
				}
				double[] q = new double[numPairs];
				double[] pa = new double[numPairs];
				for(int k = 0; k < q.length; k++) {
					q[k] = minVal;
					pa[k] = minVal;
				}

				// 3) Processing...
				for(int j = numPairs - 1; j >= 2 ; j--) {
					double[] ij = new double[numPairs - j + 1];
					for(int d = 0; d < ij.length; d++) {ij[d] = (double) (d + 1); }

					double[] i2 = new double[j - 1];
					for(int d = 0; d < i2.length; d++) {i2[d] = (double) (d + numPairs - j + 2); }

					Double q1 = null;
					for(int i = 2; i <= j; i++) {
						double pi2 = p[ ((int) i2[i - 2]) - 1 ];
						double adjVal = ((double) j) * pi2 / ((double) i);
						if(q1 == null || q1 > adjVal) q1 = adjVal; 
					}

					for(int ijindex = 0; ijindex < ij.length; ijindex++) {
						int ijVal = ((int) ij[ijindex]) - 1;
						q[ijVal] = ( (((double) j) * p[ijVal]) > q1 ) ? q1 : (((double) j) * p[ijVal]);
					}

					for(int i2index = 0; i2index < i2.length; i2index++) {
						int i2Val = ((int) i2[i2index]) -1;
						q[i2Val] = q[numPairs - j + 1];
					}

					for(int paIndex = 0; paIndex < pa.length; paIndex++) {
						pa[paIndex] = (pa[paIndex] > q[paIndex]) ? pa[paIndex] : q[paIndex];
					}
				}

				// 4) Set adj val
				for(int i = 0; i < pairStrFisherMapSortedIncrKeyList_HOMMEL.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedIncrKeyList_HOMMEL.get(i);
					Double adjPvalue = (pa[i] > p[i]) ? pa[i] : p[i];
					if(keyOfPval != null && adjPvalue != null &&
							resultPairAnalysisMap.containsKey(keyOfPval) &&
							resultPairAnalysisMap.get(keyOfPval) != null) {
						resultPairAnalysisMap.get(keyOfPval).setFisherTestAdjusted(adjPvalue);
					}
				}

				break;

			case BENJAMINI_HOCHBERG:

				// 1) Put the individual p-values in descending order
				Map<Long, Double> pairStrFisherMapSortedDecr_BENJAMINI_HOCHBERG = GenericUtils.sortByValue(pairStrFisherMap, true);
				List<Long> pairStrFisherMapSortedDecrKeyList_BENJAMINI_HOCHBERG = new ArrayList<>(pairStrFisherMapSortedDecr_BENJAMINI_HOCHBERG.keySet());

				// 2) Report adjusted values
				Double cumMinVal_BENJAMINI_HOCHBERG = null;
				for(int i = 0; i < pairStrFisherMapSortedDecrKeyList_BENJAMINI_HOCHBERG.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedDecrKeyList_BENJAMINI_HOCHBERG.get(i);
					Double valueOfPval = pairStrFisherMap.get(keyOfPval);

					Double adjPval = ((double) numPairs / ((double) (numPairs - i))) * valueOfPval;

					if(cumMinVal_BENJAMINI_HOCHBERG == null || cumMinVal_BENJAMINI_HOCHBERG > adjPval) {
						cumMinVal_BENJAMINI_HOCHBERG = adjPval;
					}
					else {
						adjPval = cumMinVal_BENJAMINI_HOCHBERG;
					}

					// Limit to 1 the value of adjusted p-values
					adjPval = (adjPval > 1d) ? 1d : adjPval;
					resultPairAnalysisMap.get(keyOfPval).setFisherTestAdjusted(adjPval);
				}

				break;

			case HOCHBERG:

				// 1) Put the individual p-values in descending order
				Map<Long, Double> pairStrFisherMapSortedDecr_HOCHBERG = GenericUtils.sortByValue(pairStrFisherMap, true);
				List<Long> pairStrFisherMapSortedDecrKeyList_HOCHBERG = new ArrayList<>(pairStrFisherMapSortedDecr_HOCHBERG.keySet());

				// 2) Report adjusted values
				Double cumMinVal_HOCHBERG = null;
				for(int i = 0; i < pairStrFisherMapSortedDecrKeyList_HOCHBERG.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedDecrKeyList_HOCHBERG.get(i);
					Double valueOfPval = pairStrFisherMap.get(keyOfPval);

					Double adjPval = ((double) i + 1) * valueOfPval;

					if(cumMinVal_HOCHBERG == null || cumMinVal_HOCHBERG > adjPval) {
						cumMinVal_HOCHBERG = adjPval;
					}
					else {
						adjPval = cumMinVal_HOCHBERG;
					}

					// Limit to 1 the value of adjusted p-values
					adjPval = (adjPval > 1d) ? 1d : adjPval;
					resultPairAnalysisMap.get(keyOfPval).setFisherTestAdjusted(adjPval);
				}

				break;

			case BENJAMINI_YEKUTIELI:

				// 1) Put the individual p-values in descending order
				Map<Long, Double> pairStrFisherMapSortedDecr_BENJAMINI_YEKUTIELI = GenericUtils.sortByValue(pairStrFisherMap, true);
				List<Long> pairStrFisherMapSortedDecrKeyList_BENJAMINI_YEKUTIELI = new ArrayList<>(pairStrFisherMapSortedDecr_BENJAMINI_YEKUTIELI.keySet());

				// 2) Sum computation
				Double qSum = 0d;
				for(int i = 1; i <= numPairs; i++) {
					qSum += 1d / ((double) i);
				}

				// 2) Report adjusted values
				Double cumMinVal_BENJAMINI_YEKUTIELI = null;
				for(int i = 0; i < pairStrFisherMapSortedDecrKeyList_BENJAMINI_YEKUTIELI.size(); i++) {
					Long keyOfPval = pairStrFisherMapSortedDecrKeyList_BENJAMINI_YEKUTIELI.get(i);
					Double valueOfPval = pairStrFisherMap.get(keyOfPval);

					Double adjPval = qSum * ((double) numPairs / ((double) (numPairs - i))) * valueOfPval;

					if(cumMinVal_BENJAMINI_YEKUTIELI == null || cumMinVal_BENJAMINI_YEKUTIELI > adjPval) {
						cumMinVal_BENJAMINI_YEKUTIELI = adjPval;
					}
					else {
						adjPval = cumMinVal_BENJAMINI_YEKUTIELI;
					}

					// Limit to 1 the value of adjusted p-values
					adjPval = (adjPval > 1d) ? 1d : adjPval;
					resultPairAnalysisMap.get(keyOfPval).setFisherTestAdjusted(adjPval);
				}

				break;

			default:
				// If no adjust method specified set all equal to pvalue, set the Fisher test value equal to the adjusted Fisher test value for that pair
				TLongObjectIterator<ComorbidityPairResult> resultPairAnalysisMapIterator_int = resultPairAnalysisMap.iterator();
				while(resultPairAnalysisMapIterator_int.hasNext()){
					resultPairAnalysisMapIterator_int.advance();
					// Long keyValue = resultPairAnalysisMapIterator.key();
					ComorbidityPairResult comoPair = resultPairAnalysisMapIterator_int.value();

					comoPair.setFisherTestAdjusted(comoPair.getFisherTest());
				}
			}

		}

	}

}
