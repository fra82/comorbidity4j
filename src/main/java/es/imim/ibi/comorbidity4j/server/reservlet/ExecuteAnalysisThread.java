package es.imim.ibi.comorbidity4j.server.reservlet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.analysis.ComorbidityMinerCache;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;

public class ExecuteAnalysisThread implements Callable<ImmutablePair<String, Collection<ComorbidityPairResult>>> {
	
	private ComorbidityMiner currentExecutor;
	private Boolean femaleMaleAll;
	private Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cacheAndPairs;
	
	public ExecuteAnalysisThread(ComorbidityMiner currentExecutor, Boolean femaleMaleAll,  Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> cacheAndPairs) {
		super();
		this.currentExecutor = currentExecutor;
		this.femaleMaleAll = femaleMaleAll;
		this.cacheAndPairs = cacheAndPairs;
	}
	
	@Override
	public ImmutablePair<String, Collection<ComorbidityPairResult>> call() throws Exception {
		return currentExecutor.executeAnalysis(femaleMaleAll, cacheAndPairs, currentExecutor.getComorDatasetObj());
	}


}
