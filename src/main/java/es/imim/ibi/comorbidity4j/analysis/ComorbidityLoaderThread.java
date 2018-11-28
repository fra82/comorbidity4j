package es.imim.ibi.comorbidity4j.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityScoreFilter.GreatLower;
import gnu.trove.TCollections;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Thread to compute comorbidity indexes among a pair of diseases
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityLoaderThread implements Callable<Map<Integer, TIntSet>> {
	
	private static final Logger logger = LoggerFactory.getLogger(ComorbidityLoaderThread.class);
	
	private Map<Integer, TIntSet> diseasePairsToAnalyze = null;
	private ComorbidityMiner comMiner = null;
	private AtomicLong pairConsidered_preproc = null;
	private AtomicLong pairSelected_preproc = null;
	private Pair<String, Integer> diagnosisCodeStringID = null;
	private ComorbidityPatientFilter femaleANDmalePatFilter_SELECTED = null;
	private ComorbidityPatientFilter femaleANDmalePatFilter = null;
	private ComorbidityPatientFilter femalePatFilter = null;
	private ComorbidityPatientFilter malePatFilter = null;
	private ComorbidityMinerCache computationCache = null;
	private TIntSet diseaseIDsCachedSet = null;
	
	/**
	 * 	
	 * @param pairedDiagnoses
	 * @param comMiner
	 * @param diseasePairsToAnalyze
	 * @param pairConsidered_preproc
	 * @param pairSelected_preproc
	 * @param diagnosisCodeStringID
	 * @param femaleANDmalePatFilter_SELECTED
	 * @param femaleANDmalePatFilter
	 * @param femalePatFilter
	 * @param malePatFilter
	 * @param computationCache
	 * @param diseaseIDsCachedSet
	 */
	public ComorbidityLoaderThread(ComorbidityMiner comMiner, Map<Integer, TIntSet> diseasePairsToAnalyze, 
			AtomicLong pairConsidered_preproc, AtomicLong pairSelected_preproc, Pair<String, Integer> diagnosisCodeStringID,
			ComorbidityPatientFilter femaleANDmalePatFilter_SELECTED,
			ComorbidityPatientFilter femaleANDmalePatFilter, ComorbidityPatientFilter femalePatFilter,
			ComorbidityPatientFilter malePatFilter, ComorbidityMinerCache computationCache,
			TIntSet diseaseIDsCachedSet) {
		super();
		this.diseasePairsToAnalyze = diseasePairsToAnalyze;
		this.comMiner = comMiner;
		this.pairConsidered_preproc = pairConsidered_preproc;
		this.pairSelected_preproc = pairSelected_preproc;
		this.diagnosisCodeStringID = diagnosisCodeStringID;
		this.femaleANDmalePatFilter_SELECTED = femaleANDmalePatFilter_SELECTED;
		this.femaleANDmalePatFilter = femaleANDmalePatFilter;
		this.femalePatFilter = femalePatFilter;
		this.malePatFilter = malePatFilter;
		this.computationCache = computationCache;
		this.diseaseIDsCachedSet = diseaseIDsCachedSet;
	}
	

	public Map<Integer, TIntSet> call() throws Exception {
		
		boolean populateCache = true;
				
		Set<Integer> pairedDiagnoses = this.comMiner.getComorDatasetObj().getPairedDiagnoses(null, diagnosisCodeStringID.getValue());
		
		if(pairedDiagnoses != null && pairedDiagnoses.size() > 0) {
			for(Integer pairedDiagnosis : pairedDiagnoses) {
				Integer diseaseA_ID = diagnosisCodeStringID.getValue();
				Integer diseaseB_ID = pairedDiagnosis;

				if(diseaseA_ID == diseaseB_ID) {
					continue;
				}
				
				pairConsidered_preproc.incrementAndGet();
				
				// Check if at least one patient exists with both diseases, if not ignore pair
				Set<Integer> numPatientWithDisAandB_Set_ALL = this.comMiner.getComorDatasetObj().getPatientsWithALLDiseases(this.femaleANDmalePatFilter_SELECTED, diseaseB_ID, diseaseA_ID);
				if(this.comMiner.getDirectionalityFilter() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() != null && this.comMiner.getDirectionalityFilter().getMinNumDays() > 0l) {
					numPatientWithDisAandB_Set_ALL = ComorbidityPairCalculator.applyDirectionalityFilter(this.comMiner.getDirectionalityFilter(), numPatientWithDisAandB_Set_ALL, diseaseA_ID, diseaseB_ID, this.comMiner.getComorDatasetObj());	
				}

				if(this.comMiner.getScoreFilter() != null) {
					if(this.comMiner.getScoreFilter().getMinNumPatientsThresholfFS() != null && this.comMiner.getScoreFilter().getMinNumPatientsGLFS() != null &&
							(numPatientWithDisAandB_Set_ALL == null ||
							((this.comMiner.getScoreFilter().getMinNumPatientsGLFS().equals(GreatLower.GREATER_OR_EQUAL_THEN) && numPatientWithDisAandB_Set_ALL.size() < this.comMiner.getScoreFilter().getMinNumPatientsThresholfFS()) ||
									(this.comMiner.getScoreFilter().getMinNumPatientsGLFS().equals(GreatLower.LOWER_OR_EQUAL_THEN) && numPatientWithDisAandB_Set_ALL.size() > this.comMiner.getScoreFilter().getMinNumPatientsThresholfFS())
									)) ){
						numPatientWithDisAandB_Set_ALL = new HashSet<Integer>();
					}
				}
				
				/** MP **/
				if(numPatientWithDisAandB_Set_ALL.size() < 1) {
					continue;
				}
				
				
				if(pairConsidered_preproc.get() % 700000 == 0) {
					System.out.println(" *** > Selected " + pairSelected_preproc.get() + " pairs over " + pairConsidered_preproc.get() + " pairs considered...");
				}
				
				if(populateCache) {
					this.comMiner.populateCache(this.comMiner.getComorDatasetObj(), diseaseA_ID, diseaseB_ID,
							computationCache, diseaseIDsCachedSet,
							femaleANDmalePatFilter, femalePatFilter, malePatFilter);
				}
				
				
				addPair(comMiner, diseasePairsToAnalyze, diseaseA_ID, diseaseB_ID, pairConsidered_preproc, pairSelected_preproc);
			}
		}
		
		return diseasePairsToAnalyze;
    }
	
	
	private synchronized static void addPair(ComorbidityMiner comMiner, Map<Integer, TIntSet> diseasePairsToAnalyze, Integer diseaseA_ID, Integer diseaseB_ID,
			AtomicLong pairConsidered_preproc, AtomicLong pairSelected_preproc) {
		
		//If directionality is enabled, add both direction if not only one
		if( comMiner.getDirectionalityFilter() == null || comMiner.getDirectionalityFilter().getMinNumDays() == null || comMiner.getDirectionalityFilter().getMinNumDays() <= 0l ) {
			// Directionality filter disabled
			if( (diseasePairsToAnalyze.containsKey(diseaseA_ID) && diseasePairsToAnalyze.get(diseaseA_ID) != null && diseasePairsToAnalyze.get(diseaseA_ID).contains(diseaseB_ID)) ||
					(diseasePairsToAnalyze.containsKey(diseaseB_ID) && diseasePairsToAnalyze.get(diseaseB_ID) != null && diseasePairsToAnalyze.get(diseaseB_ID).contains(diseaseA_ID)) ) {
				// Skip, already added on of the pairs (diseaseA_ID, diseaseB_ID) or (diseaseB_ID, diseaseA_ID)
			}
			else {
				if(!diseasePairsToAnalyze.containsKey(diseaseA_ID) || diseasePairsToAnalyze.get(diseaseA_ID) == null) {
					diseasePairsToAnalyze.put(diseaseA_ID, TCollections.synchronizedSet(new TIntHashSet()));
				}
				diseasePairsToAnalyze.get(diseaseA_ID).add(diseaseB_ID);
				pairSelected_preproc.incrementAndGet();
			}
		}
		else {
			// Directionality filter enabled
			if(!diseasePairsToAnalyze.containsKey(diseaseA_ID) || diseasePairsToAnalyze.get(diseaseA_ID) == null) {
				diseasePairsToAnalyze.put(diseaseA_ID, TCollections.synchronizedSet(new TIntHashSet()));
			}
			diseasePairsToAnalyze.get(diseaseA_ID).add(diseaseB_ID);
			pairSelected_preproc.incrementAndGet();

			if(!diseasePairsToAnalyze.containsKey(diseaseB_ID) || diseasePairsToAnalyze.get(diseaseB_ID) == null) {
				diseasePairsToAnalyze.put(diseaseB_ID, TCollections.synchronizedSet(new TIntHashSet()));
			}
			diseasePairsToAnalyze.get(diseaseB_ID).add(diseaseA_ID);
			pairSelected_preproc.incrementAndGet();
		}
	} 
	
}

