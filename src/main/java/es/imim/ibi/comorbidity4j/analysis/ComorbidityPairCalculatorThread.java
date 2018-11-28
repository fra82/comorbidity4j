package es.imim.ibi.comorbidity4j.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;

/**
 * Thread to compute comorbidity indexes among a pair of diseases
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityPairCalculatorThread implements Callable<List<ComorbidityPairResult>> {
	
	private static final Logger logger = LoggerFactory.getLogger(ComorbidityPairCalculatorThread.class);
	
	private static AtomicInteger callableCounter = new AtomicInteger(0);
	
	private Long startTime = null;
	private Integer callableID = null;
	private List<Pair<Pair<Integer, String>, Pair<Integer, String>>> diseaseABcodePairList;
	private ComorbidityMiner comorbidityMiner;
	private ComorbidityMinerCache computationCache;
	private Boolean femaleMaleAll;
	private ComorbidityPatientFilter femaleANDmalePatFilter;
	private ComorbidityPatientFilter femalePatFilter;
	private ComorbidityPatientFilter malePatFilter;
	
	
	/** 
	 * Constructor
	 * 
	 * @param diseaseA_ID
	 * @param diseaseA_CODE
	 * @param diseaseB_ID
	 * @param diseaseB_CODE
	 * @param comorbidityMiner
	 */
	public ComorbidityPairCalculatorThread(List<Pair<Pair<Integer, String>, Pair<Integer, String>>> diseaseABcodePairList, 
			ComorbidityMiner comorbidityMiner, ComorbidityMinerCache computationCache, Boolean femaleMaleAll,
			ComorbidityPatientFilter femaleANDmalePatFilter, ComorbidityPatientFilter femalePatFilter, ComorbidityPatientFilter malePatFilter) {
		super();
		this.startTime = System.currentTimeMillis();
		this.callableID = callableCounter.incrementAndGet();
		this.diseaseABcodePairList = diseaseABcodePairList;
		this.comorbidityMiner = comorbidityMiner;
		this.computationCache = computationCache;
		this.femaleMaleAll = femaleMaleAll;
		this.femaleANDmalePatFilter = femaleANDmalePatFilter;
		this.femalePatFilter = femalePatFilter;
		this.malePatFilter = malePatFilter;
	}

	public List<ComorbidityPairResult> call() throws Exception {
		
		List<ComorbidityPairResult> resultComorPairMap = new ArrayList<ComorbidityPairResult>();
		
		if(this.diseaseABcodePairList != null && this.diseaseABcodePairList.size() > 0 && this.comorbidityMiner.getComorDatasetObj() != null) {
			int computedComorbidityPairs = 0;
			Double prevPercProcessedPairsPritnedExt = 0d;
			for(Pair<Pair<Integer, String>, Pair<Integer, String>> diseaseABpair : this.diseaseABcodePairList) {
				if(diseaseABpair != null && diseaseABpair.getLeft() != null && diseaseABpair.getRight() != null) {
					Integer diseaseA_ID = diseaseABpair.getLeft().getLeft();
					Integer diseaseB_ID = diseaseABpair.getRight().getLeft();
					String diseaseA_CODE = diseaseABpair.getLeft().getRight();
					String diseaseB_CODE = diseaseABpair.getRight().getRight();
					
					ComorbidityPairResult pair = ComorbidityPairCalculator.computeComorbidityPair(diseaseA_ID, diseaseA_CODE, diseaseB_ID, diseaseB_CODE, 
							this.comorbidityMiner, this.computationCache, this.femaleMaleAll, femaleANDmalePatFilter, femalePatFilter, malePatFilter);
					
					if(pair != null) {
						resultComorPairMap.add(pair);
					}
					
					this.comorbidityMiner.processedPairsCounter.incrementAndGet();
					computedComorbidityPairs++;
					
					double percProcessedPairs = ((double) computedComorbidityPairs) / this.diseaseABcodePairList.size();
					if(percProcessedPairs > (prevPercProcessedPairsPritnedExt + 0.3d)) {
						prevPercProcessedPairsPritnedExt = percProcessedPairs;
						
						String outString = "THREAD NUMBER: " + this.callableID + " > Processed " + computedComorbidityPairs + " pairs over " + this.diseaseABcodePairList.size() + 
								" (" + percProcessedPairs + "%) in " + ((double) (System.currentTimeMillis() - this.startTime) / (double) 1000) + " seconds." ; 
						logger.debug(outString);
						System.out.println(outString);
					}

				}
			}
		}
		
		return resultComorPairMap;
    }
	
}

