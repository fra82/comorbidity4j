package es.imim.ibi.comorbidity4j.analysis;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.math3.distribution.TDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityDirectionalityFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityScoreFilter.GreatLower;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityDataset;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.util.stat.FisherExact;

/**
 * Static methods to compute comorbidity indexes among a pair of diseases
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityPairCalculator {

	private static final Logger logger = LoggerFactory.getLogger(ComorbidityPairCalculator.class);
	
	private static Map<String, ComorbidityPairResult> comorbidityScoresCacheMap = new HashMap<String, ComorbidityPairResult>();
	
	public static ComorbidityPairResult computeComorbidityPair(Integer diseaseA_ID, String diseaseA_CODE, Integer diseaseB_ID, String diseaseB_CODE,
			ComorbidityMiner comMiner, ComorbidityMinerCache computationCache, Boolean femaleMaleAll,
			ComorbidityPatientFilter femaleANDmalePatFilter, ComorbidityPatientFilter femalePatFilter, ComorbidityPatientFilter malePatFilter) {
		
		ComorbidityDataset dataset = comMiner.getComorDatasetObj();

		if(dataset == null || diseaseA_ID == null || diseaseB_ID == null) {
			return null;
		}

		ComorbidityPairResult comPairResult = new ComorbidityPairResult(diseaseA_ID, diseaseB_ID);

		try {
			
			// Cache-aware
			Set<Integer> allPatientIDs = null;
			if(femaleMaleAll == null && computationCache.getAllPatientIDs_ALL() != null) {
				allPatientIDs = computationCache.getAllPatientIDs_ALL();
			}
			else if(femaleMaleAll != null && femaleMaleAll == true && computationCache.getAllPatientIDs_FEMALE() != null) {
				allPatientIDs = computationCache.getAllPatientIDs_FEMALE();
			}
			else if(femaleMaleAll != null && femaleMaleAll == false && computationCache.getAllPatientIDs_MALE() != null) {
				allPatientIDs = computationCache.getAllPatientIDs_MALE();
			}
			else {
				System.out.println(" CHACHE ERROR: NO CHACHE FOR ALL PATS");
			}

			// Cache-aware
			Set<Integer> patientIDs_disA = null;
			if(femaleMaleAll == null && computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseA_ID) != null) {
				patientIDs_disA = computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == true && computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseA_ID) != null) {
				patientIDs_disA = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == false && computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseA_ID) != null) {
				patientIDs_disA = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseA_ID);
			}
			else {
				System.out.println(" CHACHE ERROR: NO CHACHE FOR PATS WITH DISEASE A " + diseaseA_ID + " - " + ( (femaleMaleAll == null) ? "MALES AND FEMALES" : ((femaleMaleAll) ? "FEMALE" : "MALE")));
			}

			// Cache-aware
			Set<Integer> patientIDs_disB = null;
			if(femaleMaleAll == null && computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseB_ID) != null) {
				patientIDs_disB = computationCache.getDiseaseIDpaitnetIDsetMap_ALL().get(diseaseB_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == true && computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseB_ID) != null) {
				patientIDs_disB = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseB_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == false && computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseB_ID) != null) {
				patientIDs_disB = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseB_ID);
			}
			else {
				System.out.println(" CHACHE ERROR: NO CHACHE FOR PATS WITH DISEASE B " + diseaseB_ID + " - " + ( (femaleMaleAll == null) ? "MALES AND FEMALES" : ((femaleMaleAll) ? "FEMALE" : "MALE")));
			}
			
			
			// Compute disease pair Long value
			Set<Integer> patientIDs_ATLEASTONEdisAB = new HashSet<Integer>(patientIDs_disA);
			patientIDs_ATLEASTONEdisAB.addAll(patientIDs_disB);
			/* OLD CODE
			if(femaleMaleAll == null) {
				patientIDs_ATLEASTONEdisAB = comMiner.getComorDatasetObj().getPatientsWithATleastONEDiseases(femaleANDmalePatFilter, diseaseB_ID, diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == true) {
				patientIDs_ATLEASTONEdisAB = comMiner.getComorDatasetObj().getPatientsWithATleastONEDiseases(femalePatFilter, diseaseB_ID, diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == false) {
				patientIDs_ATLEASTONEdisAB = comMiner.getComorDatasetObj().getPatientsWithATleastONEDiseases(malePatFilter, diseaseB_ID, diseaseA_ID);
			}
			*/		
			
			// Cache-aware
			// DIRECTIONALITY FILTER ALREADY APPLIED WHILE CACHING
			// If directionality is enabled, from all patients IDs from the previous four lists 
			// consider only the patients that got diseaseA_ID diagnosed for the first
			// time a number of days >= minDays with respect to the first diagnosis of disease B
			Set<Integer> patientIDs_disAB = null;
			if(femaleMaleAll == null) {
				patientIDs_disAB = comMiner.getComorDatasetObj().getPatientsWithALLDiseases(femaleANDmalePatFilter, diseaseB_ID, diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == true) {
				patientIDs_disAB = comMiner.getComorDatasetObj().getPatientsWithALLDiseases(femalePatFilter, diseaseB_ID, diseaseA_ID);
			}
			else if(femaleMaleAll != null && femaleMaleAll == false) {
				patientIDs_disAB = comMiner.getComorDatasetObj().getPatientsWithALLDiseases(malePatFilter, diseaseB_ID, diseaseA_ID);
			}
			
			if(comMiner.getDirectionalityFilter() != null && comMiner.getDirectionalityFilter().getMinNumDays() != null && comMiner.getDirectionalityFilter().getMinNumDays() > 0l) {
				patientIDs_disAB = applyDirectionalityFilter(comMiner.getDirectionalityFilter(), patientIDs_disAB, diseaseA_ID, diseaseB_ID, comMiner.getComorDatasetObj());	
			}
			
			if(comMiner.getScoreFilter() != null) {
				if(comMiner.getScoreFilter().getMinNumPatientsThresholfFS() != null && comMiner.getScoreFilter().getMinNumPatientsGLFS() != null &&
						(patientIDs_disAB == null ||
						((comMiner.getScoreFilter().getMinNumPatientsGLFS().equals(GreatLower.GREATER_OR_EQUAL_THEN) && patientIDs_disAB.size() < comMiner.getScoreFilter().getMinNumPatientsThresholfFS()) ||
						(comMiner.getScoreFilter().getMinNumPatientsGLFS().equals(GreatLower.LOWER_OR_EQUAL_THEN) && patientIDs_disAB.size() > comMiner.getScoreFilter().getMinNumPatientsThresholfFS())
						)) ){
					return null;
				}
			}
			
			
			// Code names
			comPairResult.setDisAcodeNum(diseaseA_ID);
			comPairResult.setDisBcodeNum(diseaseB_ID);

			comPairResult.setDisAcode(diseaseA_CODE);
			comPairResult.setDisBcode(diseaseB_CODE);
			comPairResult.setDisAname((dataset.getDiagnosisCodeStringDescriptionMap().containsKey(diseaseA_CODE) && dataset.getDiagnosisCodeStringDescriptionMap().get(diseaseA_CODE) != null) ? dataset.getDiagnosisCodeStringDescriptionMap().get(diseaseA_CODE) : "---");
			comPairResult.setDisBname((dataset.getDiagnosisCodeStringDescriptionMap().containsKey(diseaseB_CODE) && dataset.getDiagnosisCodeStringDescriptionMap().get(diseaseB_CODE) != null) ? dataset.getDiagnosisCodeStringDescriptionMap().get(diseaseB_CODE) : "---");

			comPairResult.setPatTotal(allPatientIDs.size());
			comPairResult.setPatWdisA(patientIDs_disA.size());
			comPairResult.setPatWdisB(patientIDs_disB.size());
			comPairResult.setPatWdisAB(patientIDs_disAB.size());
			comPairResult.setPatWdisAnotB(patientIDs_disA.size() - patientIDs_disAB.size());
			comPairResult.setPatWdisBnotA(patientIDs_disB.size() - patientIDs_disAB.size());
			comPairResult.setPatWOdisAB(allPatientIDs.size() - patientIDs_ATLEASTONEdisAB.size());
			
			String comorbidityHash = "H_" + allPatientIDs.size() + "_" + patientIDs_disA.size() + "_" + patientIDs_disB.size() + "_" +
					patientIDs_disAB.size() + "_" + (patientIDs_disA.size() - patientIDs_disAB.size()) + "_" + (patientIDs_disB.size() - patientIDs_disAB.size()) + "_" + (allPatientIDs.size() - patientIDs_ATLEASTONEdisAB.size());
			
			if(comorbidityScoresCacheMap.containsKey(comorbidityHash) && comorbidityScoresCacheMap.get(comorbidityHash) != null) {
				comPairResult.setRelativeRiskIndex(comorbidityScoresCacheMap.get(comorbidityHash).getRelativeRiskIndex());
				comPairResult.setPhiIndex(comorbidityScoresCacheMap.get(comorbidityHash).getPhiIndex());
				comPairResult.setOddsRatioIndex(comorbidityScoresCacheMap.get(comorbidityHash).getOddsRatioIndex());
				comPairResult.setOddsRatio95upper(comorbidityScoresCacheMap.get(comorbidityHash).getOddsRatio95upper());
				comPairResult.setOddsRatio95lower(comorbidityScoresCacheMap.get(comorbidityHash).getOddsRatio95lower());
				comPairResult.setFisherTest(comorbidityScoresCacheMap.get(comorbidityHash).getFisherTest());
				comPairResult.setExpect(comorbidityScoresCacheMap.get(comorbidityHash).getExpect());
				comPairResult.setScore(comorbidityScoresCacheMap.get(comorbidityHash).getScore());
			}
			else {
				// 1) Relative risk (or risk ratio) computation
				// See also: https://select-statistics.co.uk/calculators/confidence-interval-calculator-odds-ratio/
				// >>> R implementation
				// relativeRisk <- (as.numeric(AB)*as.numeric(lenActPa))/as.numeric(disA*disB)
				Double relativeRisk = null;
				try {
					if(comPairResult.getPatWdisAB() == 0) {
						relativeRisk = 0d;
					}
					else {
						Double num = ((double) comPairResult.getPatWdisAB()) * ((double) comPairResult.getPatTotal());
						Double den = ((double) comPairResult.getPatWdisA()) * ((double) comPairResult.getPatWdisB());
						relativeRisk = (den != 0) ? num / den : 0d;
					}
				}
				catch(Exception e) {
					logger.error("Exception computing relative risk - " + e.getMessage());
				}
				comPairResult.setRelativeRiskIndex(relativeRisk);

				// 2) Phi index computation
				// >>> R implementation
				// den <- as.numeric(disA*disB)*(as.numeric(lenActPa)-as.numeric(disA))*(as.numeric(lenActPa)-as.numeric(disB))
				// num <- (as.numeric(AB)*as.numeric(lenActPa))-as.numeric(disA*disB)
				// phi <- ((num)/sqrt(den))
				Double phiIndex = null;
				try {
					Double num = (double) (
							((double) (comPairResult.getPatWdisAB() * comPairResult.getPatTotal())) - 
							((double) (comPairResult.getPatWdisA() * comPairResult.getPatWdisB()))
							);
					if(num == 0d) {
						phiIndex = 0d;
					}
					else {
						Double den = (double) ( 
								((double) (comPairResult.getPatWdisA() * comPairResult.getPatWdisB())) *
								((double) (comPairResult.getPatTotal() - comPairResult.getPatWdisA())) *
								((double) (comPairResult.getPatTotal() - comPairResult.getPatWdisB())) 
								);

						phiIndex = (den != 0) ? num / Math.sqrt(den) : 0d;
					}
				}
				catch(Exception e) {
					logger.error("Exception computing phi index - " + e.getMessage());
				}
				comPairResult.setPhiIndex(phiIndex);


				// 3) Odds ratio computation
				// >>> R implementation
				// oddsRatio <- (as.numeric(AB)*as.numeric(notAB))/(as.numeric(AnotB)*as.numeric(BnotA))
				Double oddsRatio = null;
				try {
					if(comPairResult.getPatWdisAB() == 0 || comPairResult.getPatWOdisAB() == 0) {
						oddsRatio = 0d;
					}
					else {
						Double num = ((double) comPairResult.getPatWdisAB()) * ((double) comPairResult.getPatWOdisAB());
						Double den = ((double) comPairResult.getPatWdisAnotB()) * ((double) comPairResult.getPatWdisBnotA());
						oddsRatio = (den != 0) ? num / den : 0d;
					}
				}
				catch(Exception e) {
					logger.error("Exception computing odds ratio - " + e.getMessage());
				}
				comPairResult.setOddsRatioIndex(oddsRatio);

				// 3.1) 95% confidence interval of Odds Ratio
				// ci = exp(log(or) ± Zα/2­*√1/a + 1/b + 1/c + 1/d) - for a confidence level of 95%, α is 0.05 and the critical value is 1.96
				
				// Calculate critical value - 1.96 for 95% confidence interval
				// https://gist.github.com/gcardone/5536578#file-confidenceintervalapp-java
				TDistribution tDist = new TDistribution(1000000);
	            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - comMiner.getOddsRatioConfindeceInterval()) / 2);
	            
				Double oddsRatio95upper = -10000d;
				Double oddsRatio95lower = -10000d;
				try {
					if(oddsRatio <= 0d) {
						oddsRatio95upper = 0d;
						oddsRatio95lower = 0d;
					}
					else {
						// http://sphweb.bumc.bu.edu/otlt/MPH-Modules/QuantCore/PH717_ComparingFrequencies/PH717_ComparingFrequencies8.html
						
						// Step 1: Calculate the natural log of the risk ratio
						Double logOR = Math.log(oddsRatio);
						
						// Step 2: Calculate the standard error of the log(OR)
						Double SEofLogOR = Math.sqrt( ((double) 1 / (double) comPairResult.getPatWdisAB()) + ((double) 1 / (double) comPairResult.getPatWdisAnotB()) + ((double) 1 / (double) comPairResult.getPatWdisBnotA()) + ((double) 1 / (double) comPairResult.getPatWOdisAB()));
						
						// Step 3: Calculate the lower and upper confidence bounds on the natural log scale and convert the log limits back to a normal scale for odds ratios by taking the antilog using R.
						Double interval = critVal * SEofLogOR;
						oddsRatio95upper = Math.exp(logOR + interval);
						oddsRatio95lower = Math.exp(logOR - interval);
					}
				}
				catch(Exception e) {
					logger.error("Exception computing odds ratio - " + e.getMessage());
				}
				comPairResult.setOddsRatio95upper(oddsRatio95upper);
				comPairResult.setOddsRatio95lower(oddsRatio95lower);
				

				// 4) Exact Fisher Test
				Double twoTailedP = null;
				try {
					int a = comPairResult.getPatWdisAB().intValue();
					int b = comPairResult.getPatWdisAnotB();
					int c = comPairResult.getPatWdisBnotA();
					int d = comPairResult.getPatWOdisAB();
					FisherExact fisherExact = new FisherExact(a + b + c + d + 10);
					twoTailedP = fisherExact.getTwoTailedP(a, b, c, d);
				}
				catch(Exception e) {
					logger.error("Exception computing Fisher test - " + e.getMessage());
				}
				comPairResult.setFisherTest(twoTailedP);


				// 5) Expectation
				// >>> R implementation
				// resultad2$expect <-  as.numeric( resultad2$disA ) * as.numeric( resultad2$disB ) / totPatients
				Double expectIndex = null;
				try {
					if(comPairResult.getPatWdisA() == 0 || comPairResult.getPatWdisB() == 0) {
						expectIndex = 0d;
					}
					else {
						Double num = ((double) comPairResult.getPatWdisA()) * ((double) comPairResult.getPatWdisB());
						Double den = (double) (comPairResult.getPatTotal());
						expectIndex = (den != 0) ? num / den : 0d;
					}
				}
				catch(Exception e) {
					logger.error("Exception computing expect index - " + e.getMessage());
				}
				comPairResult.setExpect(expectIndex);
				
				// 6) Comorbidity Score
				// >>> R implementation
				// resultad2$score  <- log2( ( as.numeric( resultad2$AB ) + 1 ) / ( resultad2$expect + 1) )
				Double score = null;
				try {
					Double num = (double) (comPairResult.getPatWdisAB() + 1);
					Double den = (double) (comPairResult.getExpect() + 1d);
					score = (den != 0) ? (Math.log(num / den) / Math.log(2))  : 0d;
				}
				catch(Exception e) {
					logger.error("Exception computing comorbidity score index - " + e.getMessage());
				}
				comPairResult.setScore(score);
				
				
				comorbidityScoresCacheMap.put(comorbidityHash, comPairResult);
			}
			
			// 7) Comorbidity Sex Ratio
			/** MP **/
			comPairResult.setFemaleWithDisA(0);
			comPairResult.setFemaleWithDisB(0);
			comPairResult.setFemaleWithDisAandB(0);
			comPairResult.setMaleWithDisA(0);
			comPairResult.setMaleWithDisB(0);
			comPairResult.setMaleWithDisAandB(0);
			comPairResult.setSexRatioAB(0d);
			comPairResult.setSexRatioBA(0d);
			/*
			computeSexRatio(diseaseA_ID, diseaseB_ID, comMiner, computationCache, comPairResult, femalePatFilter, malePatFilter);
			*/
			
			if(comMiner.getScoreFilter() != null && !comMiner.getScoreFilter().checkScoreFilters(comPairResult, false)) {
				// The comorbidity pair did not pass the filter
				return null;
			}
			
			
		}
		catch(Exception e) {
			logger.error("Exception analyzing comorbidity pair - " + e.getMessage());
		}

		return comPairResult;
	}
	
	
	
	public static void computeSexRatio(Integer diseaseA_ID, Integer diseaseB_ID, ComorbidityMiner comMiner, ComorbidityMinerCache computationCache, ComorbidityPairResult comPairResult,
			ComorbidityPatientFilter femalePatFilter, ComorbidityPatientFilter malePatFilter) {
		
		Double sexRatioBA = null;
		Double sexRatioAB = null;
		
		try {
			// Compute disease pair Long value
			Long pairLong = diseaseA_ID.longValue() + 100000l * diseaseB_ID.longValue();
			
			// FEMALE
			Set<Integer> femalesWithDiseaseA = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseA_ID);
			Set<Integer> femalesWithDiseaseB = computationCache.getDiseaseIDpaitnetIDsetMap_FEMALE().get(diseaseB_ID);
			
			if(comPairResult != null) {
				comPairResult.setFemaleWithDisA(femalesWithDiseaseA.size());
				comPairResult.setFemaleWithDisB(femalesWithDiseaseB.size());
			}
			
			Set<Integer> femalesWithDiseaseAandB = comMiner.getComorDatasetObj().getPatientsWithALLDiseases(femalePatFilter, diseaseB_ID, diseaseA_ID);
			
			if(comPairResult != null) {
				comPairResult.setFemaleWithDisAandB(femalesWithDiseaseAandB.size());
			}

			// MALE
			Set<Integer> malesWithDiseaseA = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseA_ID);
			Set<Integer> malesWithDiseaseB = computationCache.getDiseaseIDpaitnetIDsetMap_MALE().get(diseaseB_ID);
			
			if(comPairResult != null) {
				comPairResult.setMaleWithDisA(malesWithDiseaseA.size());
				comPairResult.setMaleWithDisB(malesWithDiseaseB.size());
			}
			

			Set<Integer> malesWithDiseaseAandB = comMiner.getComorDatasetObj().getPatientsWithALLDiseases(malePatFilter, diseaseB_ID, diseaseA_ID);
			if(comPairResult != null) {
				comPairResult.setMaleWithDisAandB(malesWithDiseaseAandB.size());
			}
			
			Double num = 1d + ((femalesWithDiseaseAandB.size() > 0) ? ((double) (femalesWithDiseaseB.size())  / (double) (femalesWithDiseaseAandB.size())) : 0d);
			Double den = 1d + ((malesWithDiseaseAandB.size() > 0) ? ((double) (malesWithDiseaseB.size())  / (double) (malesWithDiseaseAandB.size())) : 0d);
			sexRatioBA = (den != 0) ? Math.log(num / den) : 0d;
						
			num = 1d + ((femalesWithDiseaseAandB.size() > 0) ? ((double) (femalesWithDiseaseA.size())  / (double) (femalesWithDiseaseAandB.size())) : 0d);
			den = 1d + ((malesWithDiseaseAandB.size() > 0) ? ((double) (malesWithDiseaseA.size())  / (double) (malesWithDiseaseAandB.size())) : 0d);
			sexRatioAB = (den != 0) ? Math.log(num / den) : 0d; 
			
			if(comPairResult != null) {
				comPairResult.setSexRatioBA(sexRatioBA);
				comPairResult.setSexRatioAB(sexRatioAB);
			}
			
		}
		catch(Exception e) {
			logger.error("Exception computing sex ratio index - " + e.getMessage());
		}
	}
	

	public static Set<Integer> applyDirectionalityFilter(ComorbidityDirectionalityFilter directionalityFilter, 
			Set<Integer> patientIDs_disAB, Integer diseaseA_ID, Integer diseaseB_ID, ComorbidityDataset dataset) {
		// The patient IDs of patientIDs_disAB should be filtered to select all the patients that respect the 
		// criteria defined by the directionality filter
		Set<Integer> patientIDs_disABFiltered = new HashSet<Integer>();
		for(Integer patientID : patientIDs_disAB) {
			if(patientID != null) {
				try {
					Patient patientUnderAnalysis = dataset.getPatientByID(patientID);
					Set<Visit> visitOfPatientSet = patientUnderAnalysis.getVisitSet();

					// First visit with diseaseA_ID
					
					/*
					Visit firstWithDiseaseA = null;
					Visit firstWithDiseaseB = null;
					long daysBetweenAPPO = -1l;
					for(Visit visitPat : visitOfPatientSet) {
						if(visitPat != null && visitPat.getVisitDate() != null && 
								visitPat.getDiagnosisCodeSet() != null) {

							// Check if visitPat has diseaseA_ID is before the firstWithDiseaseA
							if(visitPat.getDiagnosisCodeSet().contains(diseaseA_ID) && 
									(firstWithDiseaseA == null || Visit.getDayBetweenVisits(visitPat, firstWithDiseaseA) > 0) ) {
								firstWithDiseaseA = visitPat;
							}

							// Check if visitPat has diseaseB_ID is before the firstWithDiseaseA
							if(visitPat.getDiagnosisCodeSet().contains(diseaseB_ID) && 
									(firstWithDiseaseB == null || Visit.getDayBetweenVisits(visitPat, firstWithDiseaseB) > 0) ) {
								firstWithDiseaseB = visitPat;
							}
						}
					}
					if(firstWithDiseaseA != null && firstWithDiseaseA.getVisitDate() != null && 
							firstWithDiseaseB != null && firstWithDiseaseB.getVisitDate() != null) {
						LocalDate visitBeforeLocalDate = firstWithDiseaseA.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate visitAfterLocalDate = firstWithDiseaseB.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						daysBetweenAPPO = ChronoUnit.DAYS.between(visitBeforeLocalDate, visitAfterLocalDate);
					}
					*/
					
					Optional<Date> firstVisitDateDisA = visitOfPatientSet.stream().sequential().filter(visPat -> (visPat != null && 
							visPat.getVisitDate() != null && visPat.getDiagnosisCodeSet().contains(diseaseA_ID)))
					.map(visitWithDisA -> visitWithDisA.getVisitDate()).min(Date::compareTo);
					
					Optional<Date> firstVisitDateDisB = visitOfPatientSet.stream().sequential().filter(visPat -> (visPat != null && 
							visPat.getVisitDate() != null && visPat.getDiagnosisCodeSet().contains(diseaseB_ID)))
					.map(visitWithDisB -> visitWithDisB.getVisitDate()).min(Date::compareTo);
					
					// If the first visit where DiseaseA has been diagnosed occurred at least
					// MinNumDays before the first visit where DiseaseB has been diagnosed
					// consider the patient as experimenting disease A and then disease B
					if(firstVisitDateDisA.isPresent() && firstVisitDateDisB.isPresent()) {
						LocalDate visitBeforeLocalDate = firstVisitDateDisA.get().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate visitAfterLocalDate = firstVisitDateDisB.get().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						long daysBetween = ChronoUnit.DAYS.between(visitBeforeLocalDate, visitAfterLocalDate);
						
						if(daysBetween >= directionalityFilter.getMinNumDays()) {
							patientIDs_disABFiltered.add(patientID);
						}
					}
				}
				catch(Exception e) {
					logger.error("Directionality > error while filtering the directionality for patient with ID: " + patientID +
							" (disease A: " + diseaseA_ID + ", disease B: " + diseaseB_ID + ") - " + e.getMessage());
				}
			}
		}

		return patientIDs_disABFiltered;
	}
	
	public static void main(String[] args) {
		TDistribution tDist = new TDistribution(1000000);
        double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - 0.95) / 2);
        System.out.println("> " + critVal);
        
        String TMP_DIR = System.getProperty("java.io.tmpdir");
        System.out.println("> " + TMP_DIR);
	}

}
