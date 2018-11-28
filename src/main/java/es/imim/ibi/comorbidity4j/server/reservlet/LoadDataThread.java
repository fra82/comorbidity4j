package es.imim.ibi.comorbidity4j.server.reservlet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.Pair;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.analysis.ComorbidityMinerCache;

public class LoadDataThread implements Callable<Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>>> {
	
	private ComorbidityMiner currentExecutor;
	private boolean forceRecomputation;
	private Boolean femaleMalePatSelection;
	
	public LoadDataThread(ComorbidityMiner currentExecutor, boolean forceRecomputation, Boolean femaleMalePatSelection) {
		super();
		this.currentExecutor = currentExecutor;
		this.forceRecomputation = forceRecomputation;
		this.femaleMalePatSelection = femaleMalePatSelection;
	}
	
	@Override
	public Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> call() throws Exception {
		return currentExecutor.loadComorbidityPaitsToAnalyze(this.forceRecomputation, this.femaleMalePatSelection);
	}


}
