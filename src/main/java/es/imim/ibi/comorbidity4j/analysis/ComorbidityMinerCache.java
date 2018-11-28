package es.imim.ibi.comorbidity4j.analysis;

import java.util.Set;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Cache object for comorbidity analysis
 * 
 * @author ronzano
 *
 */
public class ComorbidityMinerCache {
	
	private static final int indexOfAllPatIDset = -1000000;
	
	// Instantiate thread-safe cache
	private TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_ALL = TCollections.synchronizedMap(new TIntObjectHashMap<Set<Integer>>(500000));
	
	private TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_FEMALE = TCollections.synchronizedMap(new TIntObjectHashMap<Set<Integer>>(500000));
	
	private TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_MALE = TCollections.synchronizedMap(new TIntObjectHashMap<Set<Integer>>(500000));
	
	// Setters and getters		
	public TIntObjectMap<Set<Integer>> getDiseaseIDpaitnetIDsetMap_ALL() {
		return diseaseIDpaitnetIDsetMap_ALL;
	}

	public void setDiseaseIDpaitnetIDsetMap_ALL(TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_ALL) {
		this.diseaseIDpaitnetIDsetMap_ALL = diseaseIDpaitnetIDsetMap_ALL;
	}
	
	public TIntObjectMap<Set<Integer>> getDiseaseIDpaitnetIDsetMap_FEMALE() {
		return diseaseIDpaitnetIDsetMap_FEMALE;
	}

	public void setDiseaseIDpaitnetIDsetMap_FEMALE(TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_FEMALE) {
		this.diseaseIDpaitnetIDsetMap_FEMALE = diseaseIDpaitnetIDsetMap_FEMALE;
	}

	public TIntObjectMap<Set<Integer>> getDiseaseIDpaitnetIDsetMap_MALE() {
		return diseaseIDpaitnetIDsetMap_MALE;
	}

	public void setDiseaseIDpaitnetIDsetMap_MALE(TIntObjectMap<Set<Integer>> diseaseIDpaitnetIDsetMap_MALE) {
		this.diseaseIDpaitnetIDsetMap_MALE = diseaseIDpaitnetIDsetMap_MALE;
	}
	
	

	// Other methods
	public synchronized Set<Integer> setAllPatientIDs_ALL(Set<Integer> allPatIDsSet) {
		return diseaseIDpaitnetIDsetMap_ALL.put(indexOfAllPatIDset, allPatIDsSet);
	}
	
	public synchronized Set<Integer> getAllPatientIDs_ALL() {
		return diseaseIDpaitnetIDsetMap_ALL.get(indexOfAllPatIDset);
	}
	
	public synchronized Set<Integer> setAllPatientIDs_FEMALE(Set<Integer> allPatIDsSet) {
		return diseaseIDpaitnetIDsetMap_FEMALE.put(indexOfAllPatIDset, allPatIDsSet);
	}
	
	public synchronized Set<Integer> getAllPatientIDs_FEMALE() {
		return diseaseIDpaitnetIDsetMap_FEMALE.get(indexOfAllPatIDset);
	}
	
	public synchronized Set<Integer> setAllPatientIDs_MALE(Set<Integer> allPatIDsSet) {
		return diseaseIDpaitnetIDsetMap_MALE.put(indexOfAllPatIDset, allPatIDsSet);
	}
	
	public synchronized Set<Integer> getAllPatientIDs_MALE() {
		return diseaseIDpaitnetIDsetMap_MALE.get(indexOfAllPatIDset);
	}
}
