package es.imim.ibi.comorbidity4j.server.template;

import java.io.IOException;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONValue;

import com.google.common.base.Strings;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.analysis.ComorbidityMinerCache;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.comorbidity4j.util.GenericUtils;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;


/**
 * HTML generation utilities
 * 
 * @author Francesco Ronzano
 *
 */
public class TemplateUtils {

	private static Random rnd = new Random();
	private static Integer countReloadCSSJS = rnd.nextInt(Integer.MAX_VALUE);

	private static Configuration cfg = null;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormatterCSVfileName = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
	private static SimpleDateFormat dateFormatterTimezone = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss ZZZZ");
	
	private static DecimalFormat decimFormat = new DecimalFormat("#######0.00##");
	private static NumberFormat decimFormatInt = DecimalFormat.getInstance();
	   
	
	static {

		cfg = new Configuration(Configuration.getVersion());
		cfg.setClassForTemplateLoading((new TemplateUtils()).getClass(), "/retempl");
		cfg.setIncompatibleImprovements(new Version(2, 3, 20));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		decimFormat.setRoundingMode(RoundingMode.HALF_UP);
		
		decimFormatInt.setRoundingMode(RoundingMode.FLOOR);
		decimFormatInt.setMinimumFractionDigits(0);
		decimFormatInt.setMaximumFractionDigits(0);
		decimFormatInt.setGroupingUsed(false);
		
		dateFormatterTimezone.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static String generateHTMLcustomMessage(String message) {
		
		Map<String, Object> input = new HashMap<String, Object>();
		
		input.put("message", (message != null) ? message : "");
		
		Template template = null;
		try {
			template = cfg.getTemplate("customMessage.ftl");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringWriter stringWriter = new StringWriter();
		try {
			template.process(input, stringWriter);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}
	
	
	public static String generateHTMLcommonHeader(boolean fileStorage, String executionID) {
		
		Map<String, Object> input = new HashMap<String, Object>();
		
		input.put("CSSJScount", countReloadCSSJS);
		countReloadCSSJS++;
		
		String baseCSS_JSpath = "";
		// if(!fileStorage) {
		//	baseCSS_JSpath = "http://backingdata.org/comorbidity4j/";
		// }
		input.put("baseCSS_JSpath", baseCSS_JSpath);
		
		if(ServerExecConfig.isOnline) {
			input.put("isOnline", "true");
		}
		
		if(!fileStorage) {
			input.put("fileDeleteEnabled", "true");
		}
		
		input.put("execID", executionID + "");
		Date currentDate = new Date();
		input.put("execDate", dateFormatter.format(currentDate));
		
		Template template = null;
		try {
			template = cfg.getTemplate("common_Header.ftl");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringWriter stringWriter = new StringWriter();
		try {
			template.process(input, stringWriter);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public static String generateHTMLanalysisResTemplate(ComorbidityMiner currentExecutor, Collection<ComorbidityPairResult> comorbidityPairs_ALL, 
			Collection<ComorbidityPairResult> comorbidityPairs_FEMALE,  Collection<ComorbidityPairResult> comorbidityPairs_MALE,
			String logString, String errorMsg, boolean fileStorage, String executionID, String storageFullName, String filterResultMessage) {

		Map<String, Object> input = new HashMap<String, Object>();
		
		
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		input.put("generationDate", dateFormatterTimezone.format(dt));
		
		input.put("CSSJScount", countReloadCSSJS);
		countReloadCSSJS++;
		
		input.put("isGenderEnabled", currentExecutor.isGenderEnabled() + "");
		
		// Disease grouping info
		int numGroups = 0;
		String groupDescription = "";
		if(currentExecutor.getComorDatasetObj().getGroupNameListCodesGroupMap() != null && currentExecutor.getComorDatasetObj().getGroupNameListCodesGroupMap().size() > 0) {
			for(Entry<String, Set<String>> gropDefinition : currentExecutor.getComorDatasetObj().getGroupNameListCodesGroupMap().entrySet()) {
				if(gropDefinition != null && !Strings.isNullOrEmpty(gropDefinition.getKey()) && gropDefinition.getValue() != null && gropDefinition.getValue().size() > 0) {
					numGroups++;
					groupDescription += (groupDescription.length() == 0 ? "" : ", ") + "'" + gropDefinition.getKey() + "'" + "(" + gropDefinition.getValue().size() + " diagnoses)";
				}
			}
		}
		
		input.put("disGroup_numGroups", numGroups + "");
		if(!Strings.isNullOrEmpty(groupDescription)) {
			input.put("disGroup_groupNames", groupDescription);
		}
		else {
			input.remove("disGroup_groupNames");
		}
	    
		
	    // Disease pairing info
	    int numPairingPatterns = 0;
	    int numPairsOfDagnoses = 0;
	    String pairDescription = "";
		if(currentExecutor.getComorDatasetObj().getGroupNameListCodesPairingMap() != null && currentExecutor.getComorDatasetObj().getGroupNameListCodesPairingMap().size() > 0) {
			numPairingPatterns = currentExecutor.getComorDatasetObj().getGroupNameListCodesPairingMap().size();
			
			Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> disPairToAnalyzeNoCacheSet = currentExecutor.loadComorbidityPaitsToAnalyze(false, null);
			for(Entry<Integer, Set<Integer>> disPair : disPairToAnalyzeNoCacheSet.getValue().entrySet()) {
				if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
					numPairsOfDagnoses += disPair.getValue().size();
				}
			}
			
			if(currentExecutor.getDirectionalityFilter() != null && currentExecutor.getDirectionalityFilter().getMinNumDays() > 0l) {
				numPairsOfDagnoses = (int) (numPairsOfDagnoses / 2);
			}
			
			pairDescription = "A total of " + numPairingPatterns + " diagnosis pairing pattern(s) has been defined to determine the set of diagnosis pair to study.";
		}
		else {
			pairDescription = "ALL PAIRS OF DIAGNOSES WILL BE STUDIED FOR COMORBIDITY.";
		}
		input.put("disPair_numPairingPatterns", numPairingPatterns + "");
	    input.put("disPair_numPairsToStudy", numPairsOfDagnoses + ((numPairingPatterns > 0) ? "" : " ALL PAIRS OF DIAGNOSES WILL BE STUDIED FOR COMORBIDITY."));
	    input.put("disPair_comments", pairDescription);
	    
	    
		      
		String baseCSS_JSpath = "";
		// if(!fileStorage) {
		//	baseCSS_JSpath = "http://backingdata.org/comorbidity4j/";
		// }
		input.put("baseCSS_JSpath", baseCSS_JSpath);
		
		if(ServerExecConfig.isOnline) {
			input.put("isOnline", "true");
		}
		
		if(!fileStorage) {
			input.put("notFileStorage", "true");
		}
		
		if(!Strings.isNullOrEmpty(storageFullName)) {
			input.put("storageFullName", storageFullName);
		}
		
		if(!Strings.isNullOrEmpty(filterResultMessage)) {
			input.put("filterResultMessage", filterResultMessage);
		}
		
		
		// JSON DATA ***ALL***

		// Min and max values of parameters
		Double minPhi_ALL = null;
		Double maxPhi_ALL = null;
		Double minFDR_ALL = null;
		Double maxFDR_ALL = null;
		Double minFDRadj_ALL = null;
		Double maxFDRadj_ALL = null;
		Double minRrisk_ALL = null;
		Double maxRrisk_ALL = null;
		Double minOddsRatio_ALL = null;
		Double maxOddsRatio_ALL = null;
		Double minCscore_ALL = null;
		Double maxCscore_ALL = null;
		Integer minNumPatients_ALL = null;
		Integer maxNumPatients_ALL = null;

		Set<String> JSONstringSet_ALL = new HashSet<String>();

		if(comorbidityPairs_ALL != null && comorbidityPairs_ALL.size() > 0) {
			
			int comoPairProcessedCount_ALL = 0;
			for(ComorbidityPairResult comoPair_ALL :comorbidityPairs_ALL) {

				if(comoPair_ALL != null && comoPair_ALL.getFisherTest() != null) {
					try {
						if(++comoPairProcessedCount_ALL % 1000 == 0) {
							System.out.println("Generated JSON of " + comoPairProcessedCount_ALL + " comorbidity pairs (ALL) over " + comorbidityPairs_ALL.size());
						}

						JSONstringSet_ALL.add(comoPair_ALL.toJSONline(false));
						// Add reverse pair with same value if directionality filter is disabled
						/*
						if(currentExecutor.getDirectionalityFilter() == null || (currentExecutor.getDirectionalityFilter() != null && currentExecutor.getDirectionalityFilter().getMinNumDays() <= 0l)) {
							JSONstringSet.add(comoPair.toJSONline(true));
						}
						 */

						// Set min max values
						if(comoPair_ALL.getPhiIndex() != null && 
								(minPhi_ALL == null || comoPair_ALL.getPhiIndex() < minPhi_ALL) ) {
							minPhi_ALL = comoPair_ALL.getPhiIndex();
						}

						if(comoPair_ALL.getPhiIndex() != null && 
								(maxPhi_ALL == null || comoPair_ALL.getPhiIndex() > maxPhi_ALL) ) {
							maxPhi_ALL = comoPair_ALL.getPhiIndex();
						}

						if(comoPair_ALL.getFisherTest() != null && 
								(minFDR_ALL == null || comoPair_ALL.getFisherTest() < minFDR_ALL) ) {
							minFDR_ALL = comoPair_ALL.getFisherTest();
						}

						if(comoPair_ALL.getFisherTest() != null && 
								(maxFDR_ALL == null || comoPair_ALL.getFisherTest() > maxFDR_ALL) ) {
							maxFDR_ALL = comoPair_ALL.getFisherTest();
						}

						if(comoPair_ALL.getFisherTestAdjusted() != null && 
								(minFDRadj_ALL == null || comoPair_ALL.getFisherTestAdjusted() < minFDRadj_ALL) ) {
							minFDRadj_ALL = comoPair_ALL.getFisherTestAdjusted();
						}

						if(comoPair_ALL.getFisherTestAdjusted() != null && 
								(maxFDRadj_ALL == null || comoPair_ALL.getFisherTestAdjusted() > maxFDRadj_ALL) ) {
							maxFDRadj_ALL = comoPair_ALL.getFisherTestAdjusted();
						}

						if(comoPair_ALL.getRelativeRiskIndex() != null && 
								(minRrisk_ALL == null || comoPair_ALL.getRelativeRiskIndex() < minRrisk_ALL) ) {
							minRrisk_ALL = comoPair_ALL.getRelativeRiskIndex();
						}

						if(comoPair_ALL.getRelativeRiskIndex() != null && 
								(maxRrisk_ALL == null || comoPair_ALL.getRelativeRiskIndex() > maxRrisk_ALL) ) {
							maxRrisk_ALL = comoPair_ALL.getRelativeRiskIndex();
						}

						if(comoPair_ALL.getOddsRatioIndex() != null && 
								(minOddsRatio_ALL == null || comoPair_ALL.getOddsRatioIndex() < minOddsRatio_ALL) ) {
							minOddsRatio_ALL = comoPair_ALL.getOddsRatioIndex();
						}

						if(comoPair_ALL.getOddsRatioIndex() != null && 
								(maxOddsRatio_ALL == null || comoPair_ALL.getOddsRatioIndex() > maxOddsRatio_ALL) ) {
							maxOddsRatio_ALL = comoPair_ALL.getOddsRatioIndex();
						}

						if(comoPair_ALL.getScore() != null && 
								(minCscore_ALL == null || comoPair_ALL.getScore() < minCscore_ALL) ) {
							minCscore_ALL = comoPair_ALL.getScore();
						}

						if(comoPair_ALL.getScore() != null && 
								(maxCscore_ALL == null || comoPair_ALL.getScore() > maxCscore_ALL) ) {
							maxCscore_ALL = comoPair_ALL.getScore();
						}

						if(comoPair_ALL.getPatWdisAB() != null && 
								(minNumPatients_ALL == null || comoPair_ALL.getPatWdisAB() < minNumPatients_ALL) ) {
							minNumPatients_ALL = comoPair_ALL.getPatWdisAB();
						}

						if(comoPair_ALL.getPatWdisAB() != null && 
								(maxNumPatients_ALL == null || comoPair_ALL.getPatWdisAB() > maxNumPatients_ALL) ) {
							maxNumPatients_ALL = comoPair_ALL.getPatWdisAB();
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						logString += "Error while storing comorbidity analysis results of the pair of diseases: " + (comoPair_ALL != null ? comoPair_ALL : "null")  + ".\n";
					}
				}
				else {
					logString += "The comorbidity pair with Diseases (" + 
								((comoPair_ALL != null && comoPair_ALL.getDisAcodeNum() != null) ? comoPair_ALL.getDisAcodeNum() : "-") + ", " + 
								((comoPair_ALL != null && comoPair_ALL.getDisBcodeNum() != null) ? comoPair_ALL.getDisBcodeNum() : "-") + ") " + 
								"has null analysis results; impossible to store this piece of information.\n";
				}
			}

		}
		
		minPhi_ALL = (minPhi_ALL == null) ? -10d : minPhi_ALL;
		maxPhi_ALL = (maxPhi_ALL == null) ? 10d : maxPhi_ALL;
		minFDR_ALL = 0.0d; // (minFDR_ALL == null) ? -10d : minFDR_ALL;
		maxFDR_ALL = (maxFDR_ALL == null) ? 1d : maxFDR_ALL;
		minFDRadj_ALL = 0.0d; // minFDRadj_ALL = (minFDRadj_ALL == null) ? -10d : minFDRadj_ALL;
		maxFDRadj_ALL = (maxFDRadj_ALL == null) ? 1d : maxFDRadj_ALL;
		minRrisk_ALL = (minRrisk_ALL == null) ? 0d : minRrisk_ALL;
		maxRrisk_ALL = (maxRrisk_ALL == null) ? 1d : maxRrisk_ALL;
		minOddsRatio_ALL = (minOddsRatio_ALL == null) ? 0d : minOddsRatio_ALL;
		maxOddsRatio_ALL = (maxOddsRatio_ALL == null) ? 10d : maxOddsRatio_ALL;
		minCscore_ALL = (minCscore_ALL == null) ? -10d : minCscore_ALL;
		maxCscore_ALL = (maxCscore_ALL == null) ? 10d : maxCscore_ALL;
		minNumPatients_ALL = (minNumPatients_ALL == null) ? 0 : minNumPatients_ALL;
		maxNumPatients_ALL = (maxNumPatients_ALL == null) ? 1000 : maxNumPatients_ALL;

		StringBuilder JSONobj_ALL = new StringBuilder();
		JSONobj_ALL.append("[");
		for(String JSONstring : JSONstringSet_ALL) {
			if(!Strings.isNullOrEmpty(JSONstring)) {
				JSONobj_ALL.append(((JSONobj_ALL.length() > 1) ? ",\n" : "\n") + JSONstring);
			}
		}
		JSONobj_ALL.append("]");
		input.put("JSONobj_ALL", JSONobj_ALL);



		// JSON DATA ***FEMALE***

		// Min and max values of parameters
		Double minPhi_FEMALE = null;
		Double maxPhi_FEMALE = null;
		Double minFDR_FEMALE = null;
		Double maxFDR_FEMALE = null;
		Double minFDRadj_FEMALE = null;
		Double maxFDRadj_FEMALE = null;
		Double minRrisk_FEMALE = null;
		Double maxRrisk_FEMALE = null;
		Double minOddsRatio_FEMALE = null;
		Double maxOddsRatio_FEMALE = null;
		Double minCscore_FEMALE = null;
		Double maxCscore_FEMALE = null;
		Integer minNumPatients_FEMALE = null;
		Integer maxNumPatients_FEMALE = null;

		Set<String> JSONstringSet_FEMALE = new HashSet<String>();

		if(currentExecutor.isGenderEnabled() && comorbidityPairs_FEMALE != null && comorbidityPairs_FEMALE.size() > 0) {
			
			int comoPairProcessedCount_FEMALE = 0;
			for(ComorbidityPairResult comoPair_FEMALE :comorbidityPairs_FEMALE) {

				if(comoPair_FEMALE != null && comoPair_FEMALE.getFisherTest() != null) {
					try {
						if(++comoPairProcessedCount_FEMALE % 1000 == 0) {
							System.out.println("Generated JSON of " + comoPairProcessedCount_FEMALE + " comorbidity pairs (FEMALE) over " + comorbidityPairs_FEMALE.size());
						}

						JSONstringSet_FEMALE.add(comoPair_FEMALE.toJSONline(false));
						// Add reverse pair with same value if directionality filter is disabled
						/*
						if(currentExecutor.getDirectionalityFilter() == null || (currentExecutor.getDirectionalityFilter() != null && currentExecutor.getDirectionalityFilter().getMinNumDays() <= 0l)) {
							JSONstringSet.add(comoPair.toJSONline(true));
						}
						 */

						// Set min max values
						if(comoPair_FEMALE.getPhiIndex() != null && 
								(minPhi_FEMALE == null || comoPair_FEMALE.getPhiIndex() < minPhi_FEMALE) ) {
							minPhi_FEMALE = comoPair_FEMALE.getPhiIndex();
						}

						if(comoPair_FEMALE.getPhiIndex() != null && 
								(maxPhi_FEMALE == null || comoPair_FEMALE.getPhiIndex() > maxPhi_FEMALE) ) {
							maxPhi_FEMALE = comoPair_FEMALE.getPhiIndex();
						}

						if(comoPair_FEMALE.getFisherTest() != null && 
								(minFDR_FEMALE == null || comoPair_FEMALE.getFisherTest() < minFDR_FEMALE) ) {
							minFDR_FEMALE = comoPair_FEMALE.getFisherTest();
						}

						if(comoPair_FEMALE.getFisherTest() != null && 
								(maxFDR_FEMALE == null || comoPair_FEMALE.getFisherTest() > maxFDR_FEMALE) ) {
							maxFDR_FEMALE = comoPair_FEMALE.getFisherTest();
						}

						if(comoPair_FEMALE.getFisherTestAdjusted() != null && 
								(minFDRadj_FEMALE == null || comoPair_FEMALE.getFisherTestAdjusted() < minFDRadj_FEMALE) ) {
							minFDRadj_FEMALE = comoPair_FEMALE.getFisherTestAdjusted();
						}

						if(comoPair_FEMALE.getFisherTestAdjusted() != null && 
								(maxFDRadj_FEMALE == null || comoPair_FEMALE.getFisherTestAdjusted() > maxFDRadj_FEMALE) ) {
							maxFDRadj_FEMALE = comoPair_FEMALE.getFisherTestAdjusted();
						}

						if(comoPair_FEMALE.getRelativeRiskIndex() != null && 
								(minRrisk_FEMALE == null || comoPair_FEMALE.getRelativeRiskIndex() < minRrisk_FEMALE) ) {
							minRrisk_FEMALE = comoPair_FEMALE.getRelativeRiskIndex();
						}

						if(comoPair_FEMALE.getRelativeRiskIndex() != null && 
								(maxRrisk_FEMALE == null || comoPair_FEMALE.getRelativeRiskIndex() > maxRrisk_FEMALE) ) {
							maxRrisk_FEMALE = comoPair_FEMALE.getRelativeRiskIndex();
						}

						if(comoPair_FEMALE.getOddsRatioIndex() != null && 
								(minOddsRatio_FEMALE == null || comoPair_FEMALE.getOddsRatioIndex() < minOddsRatio_FEMALE) ) {
							minOddsRatio_FEMALE = comoPair_FEMALE.getOddsRatioIndex();
						}

						if(comoPair_FEMALE.getOddsRatioIndex() != null && 
								(maxOddsRatio_FEMALE == null || comoPair_FEMALE.getOddsRatioIndex() > maxOddsRatio_FEMALE) ) {
							maxOddsRatio_FEMALE = comoPair_FEMALE.getOddsRatioIndex();
						}

						if(comoPair_FEMALE.getScore() != null && 
								(minCscore_FEMALE == null || comoPair_FEMALE.getScore() < minCscore_FEMALE) ) {
							minCscore_FEMALE = comoPair_FEMALE.getScore();
						}

						if(comoPair_FEMALE.getScore() != null && 
								(maxCscore_FEMALE == null || comoPair_FEMALE.getScore() > maxCscore_FEMALE) ) {
							maxCscore_FEMALE = comoPair_FEMALE.getScore();
						}

						if(comoPair_FEMALE.getPatWdisAB() != null && 
								(minNumPatients_FEMALE == null || comoPair_FEMALE.getPatWdisAB() < minNumPatients_FEMALE) ) {
							minNumPatients_FEMALE = comoPair_FEMALE.getPatWdisAB();
						}

						if(comoPair_FEMALE.getPatWdisAB() != null && 
								(maxNumPatients_FEMALE == null || comoPair_FEMALE.getPatWdisAB() > maxNumPatients_FEMALE) ) {
							maxNumPatients_FEMALE = comoPair_FEMALE.getPatWdisAB();
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						logString += "Error while storing comorbidity analysis results of the pair of diseases: " + (comoPair_FEMALE != null ? comoPair_FEMALE : "null")  + ".\n";
					}
				}
				else {
					logString += "The comorbidity pair with ID (" + 
								((comoPair_FEMALE != null && comoPair_FEMALE.getDisAcodeNum() != null) ? comoPair_FEMALE.getDisAcodeNum() : "-") + ", " + 
								((comoPair_FEMALE != null && comoPair_FEMALE.getDisBcodeNum() != null) ? comoPair_FEMALE.getDisBcodeNum() : "-") + ") " + 
								"has null analysis results; impossible to store this piece of information.\n";
				}
			}

		}
		
		minPhi_FEMALE = (minPhi_FEMALE == null) ? -10d : minPhi_FEMALE;
		maxPhi_FEMALE = (maxPhi_FEMALE == null) ? 10d : maxPhi_FEMALE;
		minFDR_FEMALE = 0.0d; // (minFDR_FEMALE == null) ? -10d : minFDR_FEMALE;
		maxFDR_FEMALE = (maxFDR_FEMALE == null) ? 1d : maxFDR_FEMALE;
		minFDRadj_FEMALE = 0.0d; // (minFDRadj_FEMALE == null) ? -10d : minFDRadj_FEMALE;
		maxFDRadj_FEMALE = (maxFDRadj_FEMALE == null) ? 1d : maxFDRadj_FEMALE;
		minRrisk_FEMALE = (minRrisk_FEMALE == null) ? 0d : minRrisk_FEMALE;
		maxRrisk_FEMALE = (maxRrisk_FEMALE == null) ? 1d : maxRrisk_FEMALE;
		minOddsRatio_FEMALE = (minOddsRatio_FEMALE == null) ? 0d : minOddsRatio_FEMALE;
		maxOddsRatio_FEMALE = (maxOddsRatio_FEMALE == null) ? 10d : maxOddsRatio_FEMALE;
		minCscore_FEMALE = (minCscore_FEMALE == null) ? -10d : minCscore_FEMALE;
		maxCscore_FEMALE = (maxCscore_FEMALE == null) ? 10d : maxCscore_FEMALE;
		minNumPatients_FEMALE = (minNumPatients_FEMALE == null) ? 0 : minNumPatients_FEMALE;
		maxNumPatients_FEMALE = (maxNumPatients_FEMALE == null) ? 1000 : maxNumPatients_FEMALE;

		StringBuilder JSONobj_FEMALE = new StringBuilder();
		JSONobj_FEMALE.append("[");
		for(String JSONstring : JSONstringSet_FEMALE) {
			if(!Strings.isNullOrEmpty(JSONstring)) {
				JSONobj_FEMALE.append(((JSONobj_FEMALE.length() > 1) ? ",\n" : "\n") + JSONstring);
			}
		}
		JSONobj_FEMALE.append("]");
		input.put("JSONobj_FEMALE", JSONobj_FEMALE);



		// JSON DATA ***MALE***

		// Min and max values of parameters
		Double minPhi_MALE = null;
		Double maxPhi_MALE = null;
		Double minFDR_MALE = null;
		Double maxFDR_MALE = null;
		Double minFDRadj_MALE = null;
		Double maxFDRadj_MALE = null;
		Double minRrisk_MALE = null;
		Double maxRrisk_MALE = null;
		Double minOddsRatio_MALE = null;
		Double maxOddsRatio_MALE = null;
		Double minCscore_MALE = null;
		Double maxCscore_MALE = null;
		Integer minNumPatients_MALE = null;
		Integer maxNumPatients_MALE = null;

		Set<String> JSONstringSet_MALE = new HashSet<String>();

		if(currentExecutor.isGenderEnabled() &&  comorbidityPairs_MALE != null && comorbidityPairs_MALE.size() > 0) {

			int comoPairProcessedCount_MALE = 0;
			for(ComorbidityPairResult comoPair_MALE :comorbidityPairs_MALE) {

				if(comoPair_MALE != null && comoPair_MALE.getFisherTest() != null) {
					try {
						if(++comoPairProcessedCount_MALE % 1000 == 0) {
							System.out.println("Generated JSON of " + comoPairProcessedCount_MALE + " comorbidity pairs (MALE) over " + comorbidityPairs_MALE.size());
						}

						JSONstringSet_MALE.add(comoPair_MALE.toJSONline(false));
						// Add reverse pair with same value if directionality filter is disabled
						/*
						if(currentExecutor.getDirectionalityFilter() == null || (currentExecutor.getDirectionalityFilter() != null && currentExecutor.getDirectionalityFilter().getMinNumDays() <= 0l)) {
							JSONstringSet.add(comoPair.toJSONline(true));
						}
						 */

						// Set min max values
						if(comoPair_MALE.getPhiIndex() != null && 
								(minPhi_MALE == null || comoPair_MALE.getPhiIndex() < minPhi_MALE) ) {
							minPhi_MALE = comoPair_MALE.getPhiIndex();
						}

						if(comoPair_MALE.getPhiIndex() != null && 
								(maxPhi_MALE == null || comoPair_MALE.getPhiIndex() > maxPhi_MALE) ) {
							maxPhi_MALE = comoPair_MALE.getPhiIndex();
						}

						if(comoPair_MALE.getFisherTest() != null && 
								(minFDR_MALE == null || comoPair_MALE.getFisherTest() < minFDR_MALE) ) {
							minFDR_MALE = comoPair_MALE.getFisherTest();
						}

						if(comoPair_MALE.getFisherTest() != null && 
								(maxFDR_MALE == null || comoPair_MALE.getFisherTest() > maxFDR_MALE) ) {
							maxFDR_MALE = comoPair_MALE.getFisherTest();
						}

						if(comoPair_MALE.getFisherTestAdjusted() != null && 
								(minFDRadj_MALE == null || comoPair_MALE.getFisherTestAdjusted() < minFDRadj_MALE) ) {
							minFDRadj_MALE = comoPair_MALE.getFisherTestAdjusted();
						}

						if(comoPair_MALE.getFisherTestAdjusted() != null && 
								(maxFDRadj_MALE == null || comoPair_MALE.getFisherTestAdjusted() > maxFDRadj_MALE) ) {
							maxFDRadj_MALE = comoPair_MALE.getFisherTestAdjusted();
						}

						if(comoPair_MALE.getRelativeRiskIndex() != null && 
								(minRrisk_MALE == null || comoPair_MALE.getRelativeRiskIndex() < minRrisk_MALE) ) {
							minRrisk_MALE = comoPair_MALE.getRelativeRiskIndex();
						}

						if(comoPair_MALE.getRelativeRiskIndex() != null && 
								(maxRrisk_MALE == null || comoPair_MALE.getRelativeRiskIndex() > maxRrisk_MALE) ) {
							maxRrisk_MALE = comoPair_MALE.getRelativeRiskIndex();
						}

						if(comoPair_MALE.getOddsRatioIndex() != null && 
								(minOddsRatio_MALE == null || comoPair_MALE.getOddsRatioIndex() < minOddsRatio_MALE) ) {
							minOddsRatio_MALE = comoPair_MALE.getOddsRatioIndex();
						}

						if(comoPair_MALE.getOddsRatioIndex() != null && 
								(maxOddsRatio_MALE == null || comoPair_MALE.getOddsRatioIndex() > maxOddsRatio_MALE) ) {
							maxOddsRatio_MALE = comoPair_MALE.getOddsRatioIndex();
						}

						if(comoPair_MALE.getScore() != null && 
								(minCscore_MALE == null || comoPair_MALE.getScore() < minCscore_MALE) ) {
							minCscore_MALE = comoPair_MALE.getScore();
						}

						if(comoPair_MALE.getScore() != null && 
								(maxCscore_MALE == null || comoPair_MALE.getScore() > maxCscore_MALE) ) {
							maxCscore_MALE = comoPair_MALE.getScore();
						}

						if(comoPair_MALE.getPatWdisAB() != null && 
								(minNumPatients_MALE == null || comoPair_MALE.getPatWdisAB() < minNumPatients_MALE) ) {
							minNumPatients_MALE = comoPair_MALE.getPatWdisAB();
						}

						if(comoPair_MALE.getPatWdisAB() != null && 
								(maxNumPatients_MALE == null || comoPair_MALE.getPatWdisAB() > maxNumPatients_MALE) ) {
							maxNumPatients_MALE = comoPair_MALE.getPatWdisAB();
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						logString += "Error while storing comorbidity analysis results of the pair of diseases: " + (comoPair_MALE != null ? comoPair_MALE : "null")  + ".\n";
					}
				}
				else {
					logString += "The comorbidity pair with ID (" + 
								((comoPair_MALE != null && comoPair_MALE.getDisAcodeNum() != null) ? comoPair_MALE.getDisAcodeNum() : "-") + ", " + 
								((comoPair_MALE != null && comoPair_MALE.getDisBcodeNum() != null) ? comoPair_MALE.getDisBcodeNum() : "-") + ") " + 
								"has null analysis results; impossible to store this piece of information.\n";
				}
			}

		}
		
		minPhi_MALE = (minPhi_MALE == null) ? -10d : minPhi_MALE;
		maxPhi_MALE = (maxPhi_MALE == null) ? 10d : maxPhi_MALE;
		minFDR_MALE = 0.0d; // (minFDR_MALE == null) ? -10d : minFDR_MALE;
		maxFDR_MALE = (maxFDR_MALE == null) ? 1d : maxFDR_MALE;
		minFDRadj_MALE = 0.0d; // (minFDRadj_MALE == null) ? -10d : minFDRadj_MALE;
		maxFDRadj_MALE = (maxFDRadj_MALE == null) ? 1d : maxFDRadj_MALE;
		minRrisk_MALE = (minRrisk_MALE == null) ? 0d : minRrisk_MALE;
		maxRrisk_MALE = (maxRrisk_MALE == null) ? 1d : maxRrisk_MALE;
		minOddsRatio_MALE = (minOddsRatio_MALE == null) ? 0d : minOddsRatio_MALE;
		maxOddsRatio_MALE = (maxOddsRatio_MALE == null) ? 10d : maxOddsRatio_MALE;
		minCscore_MALE = (minCscore_MALE == null) ? -10d : minCscore_MALE;
		maxCscore_MALE = (maxCscore_MALE == null) ? 10d : maxCscore_MALE;
		minNumPatients_MALE = (minNumPatients_MALE == null) ? 0 : minNumPatients_MALE;
		maxNumPatients_MALE = (maxNumPatients_MALE == null) ? 1000 : maxNumPatients_MALE;

		StringBuilder JSONobj_MALE = new StringBuilder();
		JSONobj_MALE.append("[");
		for(String JSONstring : JSONstringSet_MALE) {
			if(!Strings.isNullOrEmpty(JSONstring)) {
				JSONobj_MALE.append(((JSONobj_MALE.length() > 1) ? ",\n" : "\n") + JSONstring);
			}
		}
		JSONobj_MALE.append("]");
		input.put("JSONobj_MALE", JSONobj_MALE);


		// JSON Disease Name Autocomplete
		StringBuilder JSONobj_DiseaseNameAutocomplete = new StringBuilder();
		JSONobj_DiseaseNameAutocomplete.append("[");

		Set<Integer> consideredDiseases = new HashSet<Integer>();
		if(comorbidityPairs_ALL != null && comorbidityPairs_ALL.size() > 0) {
			for(ComorbidityPairResult comoPair_ALL : comorbidityPairs_ALL) {
				try {
					
					if(!consideredDiseases.contains(comoPair_ALL.getDisAcodeNum())) {
						if(!Strings.isNullOrEmpty(comoPair_ALL.getDisAcode())) {

							String JSONobjDiseaseDescriptor = "{" + 
									"'codeInt': " + comoPair_ALL.getDisAcodeNum() + "," +
									"'codeStr': '" + comoPair_ALL.getDisAcode() + "'," +
									"'label': '" + ((comoPair_ALL.getDisAname() != null) ? comoPair_ALL.getDisAname().replace("'", "\"").replace("\n", " ") : "") + " (" + comoPair_ALL.getDisAcode() + ")' " +
									"}";

							JSONobj_DiseaseNameAutocomplete.append(
									((JSONobj_DiseaseNameAutocomplete.length() > 1) ? ",\n" : "\n") + JSONobjDiseaseDescriptor
									);
						}
						consideredDiseases.add(comoPair_ALL.getDisAcodeNum());
					}


					if(!consideredDiseases.contains(comoPair_ALL.getDisBcodeNum())) {
						if(!Strings.isNullOrEmpty(comoPair_ALL.getDisBcode())) {

							String JSONobjDiseaseDescriptor = "{" + 
									"'codeInt': " + comoPair_ALL.getDisBcodeNum() + "," +
									"'codeStr': '" + comoPair_ALL.getDisBcode() + "'," +
									"'label': '" + comoPair_ALL.getDisBcode() + " " + ((comoPair_ALL.getDisBname() != null) ? comoPair_ALL.getDisBname().replace("'", "\"").replace("\n", " ") : "") + "' " +
									"}";

							JSONobj_DiseaseNameAutocomplete.append(
									((JSONobj_DiseaseNameAutocomplete.length() > 1) ? ",\n" : "\n") + JSONobjDiseaseDescriptor
									);
						}
						consideredDiseases.add(comoPair_ALL.getDisBcodeNum());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		JSONobj_DiseaseNameAutocomplete.append("]");
		input.put("JSONobj_DiseaseNameAutocomplete", JSONobj_DiseaseNameAutocomplete);

		// PHI: min max and steps of selectors
		input.put("minPhi_ALL", decimFormat.format(minPhi_ALL));
		input.put("maxPhi_ALL", decimFormat.format(maxPhi_ALL));
		input.put("minPhi_FEMALE", decimFormat.format(minPhi_FEMALE));
		input.put("maxPhi_FEMALE", decimFormat.format(maxPhi_FEMALE));
		input.put("minPhi_MALE", decimFormat.format(minPhi_MALE));
		input.put("maxPhi_MALE", decimFormat.format(maxPhi_MALE));

		Double minPhiPreSelect = minPhi_ALL;
		Double maxPhiPreSelect = maxPhi_ALL;
		Double stepPhi = 0.1;
		if(minPhiPreSelect == null || maxPhiPreSelect == null || minPhiPreSelect >= maxPhiPreSelect) {
			minPhiPreSelect = -1.0;
			maxPhiPreSelect = 1.0;
		}
		else {
			stepPhi = (maxPhiPreSelect - minPhiPreSelect) / 100d;
			String stepPhiStr = roundToFirstNonZero(stepPhi, true);
			stepPhi = Double.valueOf(stepPhiStr);
			stepPhi = Math.abs(stepPhi);
		}
		input.put("stepPhi_ALL", roundToFirstNonZero(stepPhi, true));
		Double minPhiSelect = Double.valueOf(roundToFirstNonZero(minPhiPreSelect, false));
		while(stepPhi != 0d && minPhiSelect >= (minPhiPreSelect - (2 * stepPhi))) { minPhiSelect -= stepPhi; };
		Double maxPhiSelect = Double.valueOf(roundToFirstNonZero(maxPhiPreSelect, false));
		while(stepPhi != 0d && maxPhiSelect <= (maxPhiPreSelect + (2 * stepPhi))) { maxPhiSelect += stepPhi; };
		input.put("initPhi_ALL", decimFormat.format(minPhiSelect));
		input.put("minPhiSelect_ALL", decimFormat.format(minPhiSelect));
		input.put("maxPhiSelect_ALL", decimFormat.format(maxPhiSelect));


		minPhiPreSelect = minPhi_FEMALE;
		maxPhiPreSelect = maxPhi_FEMALE;
		stepPhi = 0.1;
		if(minPhiPreSelect == null || maxPhiPreSelect == null || minPhiPreSelect >= maxPhiPreSelect) {
			minPhiPreSelect = -1.0;
			maxPhiPreSelect = 1.0;
		}
		else {
			stepPhi = (maxPhiPreSelect - minPhiPreSelect) / 100d;
			String stepPhiStr = roundToFirstNonZero(stepPhi, true);
			stepPhi = Double.valueOf(stepPhiStr);
			stepPhi = Math.abs(stepPhi);
		}
		input.put("stepPhi_FEMALE", roundToFirstNonZero(stepPhi, true));
		minPhiSelect = Double.valueOf(roundToFirstNonZero(minPhiPreSelect, false));
		while(stepPhi != 0d && minPhiSelect >= (minPhiPreSelect - (2 * stepPhi))) { minPhiSelect -= stepPhi; };
		maxPhiSelect = Double.valueOf(roundToFirstNonZero(maxPhiPreSelect, false));
		while(stepPhi != 0d && maxPhiSelect <= (maxPhiPreSelect + (2 * stepPhi))) { maxPhiSelect += stepPhi; };
		input.put("initPhi_FEMALE", decimFormat.format(minPhiSelect));
		input.put("minPhiSelect_FEMALE", decimFormat.format(minPhiSelect));
		input.put("maxPhiSelect_FEMALE", decimFormat.format(maxPhiSelect));


		minPhiPreSelect = minPhi_MALE;
		maxPhiPreSelect = maxPhi_MALE;
		stepPhi = 0.1;
		if(minPhiPreSelect == null || maxPhiPreSelect == null || minPhiPreSelect >= maxPhiPreSelect) {
			minPhiPreSelect = -1.0;
			maxPhiPreSelect = 1.0;
		}
		else {
			stepPhi = (maxPhiPreSelect - minPhiPreSelect) / 100d;
			String stepPhiStr = roundToFirstNonZero(stepPhi, true);
			stepPhi = Double.valueOf(stepPhiStr);
			stepPhi = Math.abs(stepPhi);
		}
		input.put("stepPhi_MALE", roundToFirstNonZero(stepPhi, true));
		minPhiSelect = Double.valueOf(roundToFirstNonZero(minPhiPreSelect, false));
		while(stepPhi != 0d && minPhiSelect >= (minPhiPreSelect - (2 * stepPhi))) { minPhiSelect -= stepPhi; };
		maxPhiSelect = Double.valueOf(roundToFirstNonZero(maxPhiPreSelect, false));
		while(stepPhi != 0d && maxPhiSelect <= (maxPhiPreSelect + (2 * stepPhi))) { maxPhiSelect += stepPhi; };
		input.put("initPhi_MALE", decimFormat.format(minPhiSelect));
		input.put("minPhiSelect_MALE", decimFormat.format(minPhiSelect));
		input.put("maxPhiSelect_MALE", decimFormat.format(maxPhiSelect));



		// FDR: min max and steps of selectors
		input.put("minFDR_ALL", decimFormat.format(minFDR_ALL));
		input.put("maxFDR_ALL", decimFormat.format(maxFDR_ALL));
		input.put("minFDR_FEMALE", decimFormat.format(minFDR_FEMALE));
		input.put("maxFDR_FEMALE", decimFormat.format(maxFDR_FEMALE));
		input.put("minFDR_MALE", decimFormat.format(minFDR_MALE));
		input.put("maxFDR_MALE", decimFormat.format(maxFDR_MALE));

		Double minFDRPreSelect = minFDR_ALL;
		Double maxFDRPreSelect = maxFDR_ALL;
		Double stepFDR = 0.1d;
		if(minFDRPreSelect == null || maxFDRPreSelect == null || minFDRPreSelect >= maxFDRPreSelect) {
			minFDRPreSelect = 0.0;
			maxFDRPreSelect = 1.0;
		}
		else {
			stepFDR = (maxFDRPreSelect - minFDRPreSelect) / 100d;
			String stepFDRStr = roundToFirstNonZero(stepFDR, true);
			stepFDR = Double.valueOf(stepFDRStr);
			stepFDR = Math.abs(stepFDR);
		}
		input.put("stepFDR_ALL", roundToFirstNonZero(stepFDR, true));
		Double minFDRSelect = Double.valueOf(roundToFirstNonZero(minFDRPreSelect, false));
		// while(stepFDR != 0d && minFDRSelect >= (minFDRPreSelect - (2 * stepFDR))) { minFDRSelect -= stepFDR; };
		Double maxFDRSelect = Double.valueOf(roundToFirstNonZero(maxFDRPreSelect, false));
		while(stepFDR != 0d && maxFDRSelect <= (maxFDRPreSelect + (2 * stepFDR)) && (maxFDRSelect + stepFDR) <= 1d) { maxFDRSelect += stepFDR; };
		input.put("initFDR_ALL", decimFormat.format(maxFDRSelect));
		input.put("minFDRSelect_ALL", decimFormat.format(minFDRSelect));
		input.put("maxFDRSelect_ALL", decimFormat.format(maxFDRSelect));


		minFDRPreSelect = minFDR_FEMALE;
		maxFDRPreSelect = maxFDR_FEMALE;
		stepFDR = 0.1d;
		if(minFDRPreSelect == null || maxFDRPreSelect == null || minFDRPreSelect >= maxFDRPreSelect) {
			minFDRPreSelect = 0.0;
			maxFDRPreSelect = 1.0;
		}
		else {
			stepFDR = (maxFDRPreSelect - minFDRPreSelect) / 100d;
			String stepFDRStr = roundToFirstNonZero(stepFDR, true);
			stepFDR = Double.valueOf(stepFDRStr);
			stepFDR = Math.abs(stepFDR);
		}
		input.put("stepFDR_FEMALE", roundToFirstNonZero(stepFDR, true));
		minFDRSelect = Double.valueOf(roundToFirstNonZero(minFDRPreSelect, false));
		// while(stepFDR != 0d && minFDRSelect >= (minFDRPreSelect - (2 * stepFDR))) { minFDRSelect -= stepFDR; };
		maxFDRSelect = Double.valueOf(roundToFirstNonZero(maxFDRPreSelect, false));
		while(stepFDR != 0d && maxFDRSelect <= (maxFDRPreSelect + (2 * stepFDR))  && (maxFDRSelect + stepFDR) <= 1d) { maxFDRSelect += stepFDR; };
		input.put("initFDR_FEMALE", decimFormat.format(maxFDRSelect));
		input.put("minFDRSelect_FEMALE", decimFormat.format(minFDRSelect));
		input.put("maxFDRSelect_FEMALE", decimFormat.format(maxFDRSelect));


		minFDRPreSelect = minFDR_MALE;
		maxFDRPreSelect = maxFDR_MALE;
		stepFDR = 0.1d;
		if(minFDRPreSelect == null || maxFDRPreSelect == null || minFDRPreSelect >= maxFDRPreSelect) {
			minFDRPreSelect = 0.0;
			maxFDRPreSelect = 1.0;
		}
		else {
			stepFDR = (maxFDRPreSelect - minFDRPreSelect) / 100d;
			String stepFDRStr = roundToFirstNonZero(stepFDR, true);
			stepFDR = Double.valueOf(stepFDRStr);
			stepFDR = Math.abs(stepFDR);
		}
		input.put("stepFDR_MALE", roundToFirstNonZero(stepFDR, true));
		minFDRSelect = Double.valueOf(roundToFirstNonZero(minFDRPreSelect, false));
		// while(stepFDR != 0d && minFDRSelect >= (minFDRPreSelect - (2 * stepFDR))) { minFDRSelect -= stepFDR; };
		maxFDRSelect = Double.valueOf(roundToFirstNonZero(maxFDRPreSelect, false));
		while(stepFDR != 0d && maxFDRSelect <= (maxFDRPreSelect + (2 * stepFDR)) && (maxFDRSelect + stepFDR) <= 1d) { maxFDRSelect += stepFDR; };
		input.put("initFDR_MALE", decimFormat.format(maxFDRSelect));
		input.put("minFDRSelect_MALE", decimFormat.format(minFDRSelect));
		input.put("maxFDRSelect_MALE", decimFormat.format(maxFDRSelect));



		// FDRadj: min max and steps of selectors
		input.put("minFDRadj_ALL", decimFormat.format(minFDRadj_ALL));
		input.put("maxFDRadj_ALL", decimFormat.format(maxFDRadj_ALL));
		input.put("minFDRadj_FEMALE", decimFormat.format(minFDRadj_FEMALE));
		input.put("maxFDRadj_FEMALE", decimFormat.format(maxFDRadj_FEMALE));
		input.put("minFDRadj_MALE", decimFormat.format(minFDRadj_MALE));
		input.put("maxFDRadj_MALE", decimFormat.format(maxFDRadj_MALE));


		Double minFDRadjPreSelect = minFDRadj_ALL;
		Double maxFDRadjPreSelect = maxFDRadj_ALL;
		Double stepFDRadj = 0.1d;
		if(minFDRadjPreSelect == null || maxFDRadjPreSelect == null || minFDRadjPreSelect >= maxFDRadjPreSelect) {
			minFDRadjPreSelect = 0.0;
			maxFDRadjPreSelect = 1.0;
		}
		else {
			stepFDRadj = (maxFDRadjPreSelect - minFDRadjPreSelect) / 100d;
			String stepFDRadjStr = roundToFirstNonZero(stepFDRadj, true);
			stepFDRadj = Double.valueOf(stepFDRadjStr);
			stepFDRadj = Math.abs(stepFDRadj);
		}
		input.put("stepFDRadj_ALL", roundToFirstNonZero(stepFDRadj, true));
		Double minFDRadjSelect = Double.valueOf(roundToFirstNonZero(minFDRadjPreSelect, false));
		while(stepFDRadj != 0d && minFDRadjSelect >= (minFDRadjPreSelect - (2 * stepFDRadj))) { minFDRadjSelect -= stepFDRadj; };
		Double maxFDRadjSelect = Double.valueOf(roundToFirstNonZero(maxFDRadjPreSelect, false));
		while(stepFDRadj != 0d && maxFDRadjSelect <= (maxFDRadjPreSelect + (2 * stepFDRadj))) { maxFDRadjSelect += stepFDRadj; };
		input.put("initFDRadj_ALL", decimFormat.format(maxFDRadjSelect));
		input.put("minFDRadjSelect_ALL", decimFormat.format(minFDRadjSelect));
		input.put("maxFDRadjSelect_ALL", decimFormat.format(maxFDRadjSelect));


		minFDRadjPreSelect = minFDRadj_FEMALE;
		maxFDRadjPreSelect = maxFDRadj_FEMALE;
		stepFDRadj = 0.1d;
		if(minFDRadjPreSelect == null || maxFDRadjPreSelect == null || minFDRadjPreSelect >= maxFDRadjPreSelect) {
			minFDRadjPreSelect = 0.0;
			maxFDRadjPreSelect = 1.0;
		}
		else {
			stepFDRadj = (maxFDRadjPreSelect - minFDRadjPreSelect) / 100d;
			String stepFDRadjStr = roundToFirstNonZero(stepFDRadj, true);
			stepFDRadj = Double.valueOf(stepFDRadjStr);
			stepFDRadj = Math.abs(stepFDRadj);
		}
		input.put("stepFDRadj_FEMALE", roundToFirstNonZero(stepFDRadj, true));
		minFDRadjSelect = Double.valueOf(roundToFirstNonZero(minFDRadjPreSelect, false));
		while(stepFDRadj != 0d && minFDRadjSelect >= (minFDRadjPreSelect - (2 * stepFDRadj))) { minFDRadjSelect -= stepFDRadj; };
		maxFDRadjSelect = Double.valueOf(roundToFirstNonZero(maxFDRadjPreSelect, false));
		while(stepFDRadj != 0d && maxFDRadjSelect <= (maxFDRadjPreSelect + (2 * stepFDRadj))) { maxFDRadjSelect += stepFDRadj; };
		input.put("initFDRadj_FEMALE", decimFormat.format(maxFDRadjSelect));
		input.put("minFDRadjSelect_FEMALE", decimFormat.format(minFDRadjSelect));
		input.put("maxFDRadjSelect_FEMALE", decimFormat.format(maxFDRadjSelect));

		minFDRadjPreSelect = minFDRadj_MALE;
		maxFDRadjPreSelect = maxFDRadj_MALE;
		stepFDRadj = 0.1d;
		if(minFDRadjPreSelect == null || maxFDRadjPreSelect == null || minFDRadjPreSelect >= maxFDRadjPreSelect) {
			minFDRadjPreSelect = 0.0;
			maxFDRadjPreSelect = 1.0;
		}
		else {
			stepFDRadj = (maxFDRadjPreSelect - minFDRadjPreSelect) / 100d;
			String stepFDRadjStr = roundToFirstNonZero(stepFDRadj, true);
			stepFDRadj = Double.valueOf(stepFDRadjStr);
			stepFDRadj = Math.abs(stepFDRadj);
		}
		input.put("stepFDRadj_MALE", roundToFirstNonZero(stepFDRadj, true));
		minFDRadjSelect = Double.valueOf(roundToFirstNonZero(minFDRadjPreSelect, false));
		while(stepFDRadj != 0d && minFDRadjSelect >= (minFDRadjPreSelect - (2 * stepFDRadj))) { minFDRadjSelect -= stepFDRadj; };
		maxFDRadjSelect = Double.valueOf(roundToFirstNonZero(maxFDRadjPreSelect, false));
		while(stepFDRadj != 0d && maxFDRadjSelect <= (maxFDRadjPreSelect + (2 * stepFDRadj))) { maxFDRadjSelect += stepFDRadj; };
		input.put("initFDRadj_MALE", decimFormat.format(maxFDRadjSelect));
		input.put("minFDRadjSelect_MALE", decimFormat.format(minFDRadjSelect));
		input.put("maxFDRadjSelect_MALE", decimFormat.format(maxFDRadjSelect));



		// Relative risk: min max and steps of selectors
		input.put("minRrisk_ALL", decimFormat.format(minRrisk_ALL));
		input.put("maxRrisk_ALL", decimFormat.format(maxRrisk_ALL));
		input.put("minRrisk_FEMALE", decimFormat.format(minRrisk_FEMALE));
		input.put("maxRrisk_FEMALE", decimFormat.format(maxRrisk_FEMALE));
		input.put("minRrisk_MALE", decimFormat.format(minRrisk_MALE));
		input.put("maxRrisk_MALE", decimFormat.format(maxRrisk_MALE));

		Double minRriskareSelect = minRrisk_ALL;
		Double maxRriskareSelect = maxRrisk_ALL;
		Double stepRrisk = 1d;
		if(minRriskareSelect == null || maxRriskareSelect == null || minRriskareSelect >= maxRriskareSelect) {
			minRriskareSelect = 0.0;
			maxRriskareSelect = 10d;
		}
		else {
			stepRrisk = (maxRriskareSelect - minRriskareSelect) / 100d;
			String stepRriskStr = roundToFirstNonZero(stepRrisk, true);
			stepRrisk = Double.valueOf(stepRriskStr);
			stepRrisk = Math.abs(stepRrisk);
		}
		input.put("stepRrisk_ALL", roundToFirstNonZero(stepRrisk, true));
		Double minRriskSelect = Double.valueOf(roundToFirstNonZero(minRriskareSelect, false));
		while(stepRrisk != 0d && minRriskSelect >= (minRriskareSelect - (2 * stepRrisk))) { minRriskSelect -= stepRrisk; };
		Double maxRriskSelect = Double.valueOf(roundToFirstNonZero(maxRriskareSelect, false));
		while(stepRrisk != 0d && maxRriskSelect <= (maxRriskareSelect + (2 * stepRrisk))) { maxRriskSelect += stepRrisk; };
		input.put("initRrisk_ALL", decimFormat.format(minRriskSelect));
		input.put("minRriskSelect_ALL", decimFormat.format(minRriskSelect));
		input.put("maxRriskSelect_ALL", decimFormat.format(maxRriskSelect));


		minRriskareSelect = minRrisk_FEMALE;
		maxRriskareSelect = maxRrisk_FEMALE;
		stepRrisk = 1d;
		if(minRriskareSelect == null || maxRriskareSelect == null || minRriskareSelect >= maxRriskareSelect) {
			minRriskareSelect = 0.0;
			maxRriskareSelect = 10d;
		}
		else {
			stepRrisk = (maxRriskareSelect - minRriskareSelect) / 100d;
			String stepRriskStr = roundToFirstNonZero(stepRrisk, true);
			stepRrisk = Double.valueOf(stepRriskStr);
			stepRrisk = Math.abs(stepRrisk);
		}
		input.put("stepRrisk_FEMALE", roundToFirstNonZero(stepRrisk, true));
		minRriskSelect = Double.valueOf(roundToFirstNonZero(minRriskareSelect, false));
		while(stepRrisk != 0d && minRriskSelect >= (minRriskareSelect - (2 * stepRrisk))) { minRriskSelect -= stepRrisk; };
		maxRriskSelect = Double.valueOf(roundToFirstNonZero(maxRriskareSelect, false));
		while(stepRrisk != 0d && maxRriskSelect <= (maxRriskareSelect + (2 * stepRrisk))) { maxRriskSelect += stepRrisk; };
		input.put("initRrisk_FEMALE", decimFormat.format(minRriskSelect));
		input.put("minRriskSelect_FEMALE", decimFormat.format(minRriskSelect));
		input.put("maxRriskSelect_FEMALE", decimFormat.format(maxRriskSelect));


		minRriskareSelect = minRrisk_MALE;
		maxRriskareSelect = maxRrisk_MALE;
		stepRrisk = 1d;
		if(minRriskareSelect == null || maxRriskareSelect == null || minRriskareSelect >= maxRriskareSelect) {
			minRriskareSelect = 0.0;
			maxRriskareSelect = 10d;
		}
		else {
			stepRrisk = (maxRriskareSelect - minRriskareSelect) / 100d;
			String stepRriskStr = roundToFirstNonZero(stepRrisk, true);
			stepRrisk = Double.valueOf(stepRriskStr);
			stepRrisk = Math.abs(stepRrisk);
		}
		input.put("stepRrisk_MALE", roundToFirstNonZero(stepRrisk, true));
		minRriskSelect = Double.valueOf(roundToFirstNonZero(minRriskareSelect, false));
		while(stepRrisk != 0d && minRriskSelect >= (minRriskareSelect - (2 * stepRrisk))) { minRriskSelect -= stepRrisk; };
		maxRriskSelect = Double.valueOf(roundToFirstNonZero(maxRriskareSelect, false));
		while(stepRrisk != 0d && maxRriskSelect <= (maxRriskareSelect + (2 * stepRrisk))) { maxRriskSelect += stepRrisk; };
		input.put("initRrisk_MALE", decimFormat.format(minRriskSelect));
		input.put("minRriskSelect_MALE", decimFormat.format(minRriskSelect));
		input.put("maxRriskSelect_MALE", decimFormat.format(maxRriskSelect));


		// Odds ratio: min max and steps of selectors
		input.put("minOddsRatio_ALL", decimFormat.format(minOddsRatio_ALL));
		input.put("maxOddsRatio_ALL", decimFormat.format(maxOddsRatio_ALL));
		input.put("minOddsRatio_FEMALE", decimFormat.format(minOddsRatio_FEMALE));
		input.put("maxOddsRatio_FEMALE", decimFormat.format(maxOddsRatio_FEMALE));
		input.put("minOddsRatio_MALE", decimFormat.format(minOddsRatio_MALE));
		input.put("maxOddsRatio_MALE", decimFormat.format(maxOddsRatio_MALE));


		Double minOddsRatioPreSelect = minOddsRatio_ALL;
		Double maxOddsRatioPreSelect = maxOddsRatio_ALL;
		Double stepOddsRatio = 1d;
		if(minOddsRatioPreSelect == null || maxOddsRatioPreSelect == null || minOddsRatioPreSelect >= maxOddsRatioPreSelect) {
			minOddsRatioPreSelect = 0.0;
			maxOddsRatioPreSelect = 10d;
		}
		else {
			stepOddsRatio = (maxOddsRatioPreSelect - minOddsRatioPreSelect) / 100d;
			String stepOddsRatioStr = roundToFirstNonZero(stepOddsRatio, true);
			stepOddsRatio = Double.valueOf(stepOddsRatioStr);
			stepOddsRatio = Math.abs(stepOddsRatio);
		}
		input.put("stepOddsRatio_ALL", roundToFirstNonZero(stepOddsRatio, true));
		Double minOddsRatioSelect = Double.valueOf(roundToFirstNonZero(minOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && minOddsRatioSelect >= (minOddsRatioPreSelect - (2 * stepOddsRatio))) { minOddsRatioSelect -= stepOddsRatio; };
		Double maxOddsRatioSelect = Double.valueOf(roundToFirstNonZero(maxOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && maxOddsRatioSelect <= (maxOddsRatioPreSelect + (2 * stepOddsRatio))) { maxOddsRatioSelect += stepOddsRatio; };
		input.put("initOddsRatio_ALL", decimFormat.format(minOddsRatioSelect));
		input.put("minOddsRatioSelect_ALL", decimFormat.format(minOddsRatioSelect));
		input.put("maxOddsRatioSelect_ALL", decimFormat.format(maxOddsRatioSelect));


		minOddsRatioPreSelect = minOddsRatio_FEMALE;
		maxOddsRatioPreSelect = maxOddsRatio_FEMALE;
		stepOddsRatio = 1d;
		if(minOddsRatioPreSelect == null || maxOddsRatioPreSelect == null || minOddsRatioPreSelect >= maxOddsRatioPreSelect) {
			minOddsRatioPreSelect = 0.0;
			maxOddsRatioPreSelect = 10d;
		}
		else {
			stepOddsRatio = (maxOddsRatioPreSelect - minOddsRatioPreSelect) / 100d;
			String stepOddsRatioStr = roundToFirstNonZero(stepOddsRatio, true);
			stepOddsRatio = Double.valueOf(stepOddsRatioStr);
			stepOddsRatio = Math.abs(stepOddsRatio);
		}
		input.put("stepOddsRatio_FEMALE", roundToFirstNonZero(stepOddsRatio, true));
		minOddsRatioSelect = Double.valueOf(roundToFirstNonZero(minOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && minOddsRatioSelect >= (minOddsRatioPreSelect - (2 * stepOddsRatio))) { minOddsRatioSelect -= stepOddsRatio; };
		maxOddsRatioSelect = Double.valueOf(roundToFirstNonZero(maxOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && maxOddsRatioSelect <= (maxOddsRatioPreSelect + (2 * stepOddsRatio))) { maxOddsRatioSelect += stepOddsRatio; };
		input.put("initOddsRatio_FEMALE", decimFormat.format(minOddsRatioSelect));
		input.put("minOddsRatioSelect_FEMALE", decimFormat.format(minOddsRatioSelect));
		input.put("maxOddsRatioSelect_FEMALE", decimFormat.format(maxOddsRatioSelect));


		minOddsRatioPreSelect = minOddsRatio_MALE;
		maxOddsRatioPreSelect = maxOddsRatio_MALE;
		stepOddsRatio = 1d;
		if(minOddsRatioPreSelect == null || maxOddsRatioPreSelect == null || minOddsRatioPreSelect >= maxOddsRatioPreSelect) {
			minOddsRatioPreSelect = 0.0;
			maxOddsRatioPreSelect = 10d;
		}
		else {
			stepOddsRatio = (maxOddsRatioPreSelect - minOddsRatioPreSelect) / 100d;
			String stepOddsRatioStr = roundToFirstNonZero(stepOddsRatio, true);
			stepOddsRatio = Double.valueOf(stepOddsRatioStr);
			stepOddsRatio = Math.abs(stepOddsRatio);
		}
		input.put("stepOddsRatio_MALE", roundToFirstNonZero(stepOddsRatio, true));
		minOddsRatioSelect = Double.valueOf(roundToFirstNonZero(minOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && minOddsRatioSelect >= (minOddsRatioPreSelect - (2 * stepOddsRatio))) { minOddsRatioSelect -= stepOddsRatio; };
		maxOddsRatioSelect = Double.valueOf(roundToFirstNonZero(maxOddsRatioPreSelect, false));
		while(stepOddsRatio != 0d && maxOddsRatioSelect <= (maxOddsRatioPreSelect + (2 * stepOddsRatio))) { maxOddsRatioSelect += stepOddsRatio; };
		input.put("initOddsRatio_MALE", decimFormat.format(minOddsRatioSelect));
		input.put("minOddsRatioSelect_MALE", decimFormat.format(minOddsRatioSelect));
		input.put("maxOddsRatioSelect_MALE", decimFormat.format(maxOddsRatioSelect));



		// Comorbidity score: min max and steps of selectors
		input.put("minCscore_ALL", decimFormat.format(minCscore_ALL));
		input.put("maxCscore_ALL", decimFormat.format(maxCscore_ALL));
		input.put("minCscore_FEMALE", decimFormat.format(minCscore_FEMALE));
		input.put("maxCscore_FEMALE", decimFormat.format(maxCscore_FEMALE));
		input.put("minCscore_MALE", decimFormat.format(minCscore_MALE));
		input.put("maxCscore_MALE", decimFormat.format(maxCscore_MALE));


		Double minCscorePreSelect = minCscore_ALL;
		Double maxCscorePreSelect = maxCscore_ALL;
		Double stepCscore = 0.1d;
		if(minCscorePreSelect == null || maxCscorePreSelect == null || minCscorePreSelect >= maxCscorePreSelect) {
			minCscorePreSelect = -5.0;
			maxCscorePreSelect = 5d;
		}
		else {
			stepCscore = (maxCscorePreSelect - minCscorePreSelect) / 100d;
			String stepCscoreStr = roundToFirstNonZero(stepCscore, true);
			stepCscore = Double.valueOf(stepCscoreStr);
			stepCscore = Math.abs(stepCscore);
		}
		input.put("stepCscore_ALL", roundToFirstNonZero(stepCscore, true));
		Double minCscoreSelect = Double.valueOf(roundToFirstNonZero(minCscorePreSelect, false));
		while(stepCscore != 0d && minCscoreSelect >= (minCscorePreSelect - (2 * stepCscore))) { minCscoreSelect -= stepCscore; };
		Double maxCscoreSelect = Double.valueOf(roundToFirstNonZero(maxCscorePreSelect, false));
		while(stepCscore != 0d && maxCscoreSelect <= (maxCscorePreSelect + (2 * stepCscore))) { maxCscoreSelect += stepCscore; };
		input.put("initCscore_ALL", decimFormat.format(minCscoreSelect));
		input.put("minCscoreSelect_ALL", decimFormat.format(minCscoreSelect));
		input.put("maxCscoreSelect_ALL", decimFormat.format(maxCscoreSelect));


		minCscorePreSelect = minCscore_FEMALE;
		maxCscorePreSelect = maxCscore_FEMALE;
		stepCscore = 0.1d;
		if(minCscorePreSelect == null || maxCscorePreSelect == null || minCscorePreSelect >= maxCscorePreSelect) {
			minCscorePreSelect = -5.0;
			maxCscorePreSelect = 5d;
		}
		else {
			stepCscore = (maxCscorePreSelect - minCscorePreSelect) / 100d;
			String stepCscoreStr = roundToFirstNonZero(stepCscore, true);
			stepCscore = Double.valueOf(stepCscoreStr);
			stepCscore = Math.abs(stepCscore);
		}
		input.put("stepCscore_FEMALE", roundToFirstNonZero(stepCscore, true));
		minCscoreSelect = Double.valueOf(roundToFirstNonZero(minCscorePreSelect, false));
		while(stepCscore != 0d && minCscoreSelect >= (minCscorePreSelect - (2 * stepCscore))) { minCscoreSelect -= stepCscore; };
		maxCscoreSelect = Double.valueOf(roundToFirstNonZero(maxCscorePreSelect, false));
		while(stepCscore != 0d && maxCscoreSelect <= (maxCscorePreSelect + (2 * stepCscore))) { maxCscoreSelect += stepCscore; };
		input.put("initCscore_FEMALE", decimFormat.format(minCscoreSelect));
		input.put("minCscoreSelect_FEMALE", decimFormat.format(minCscoreSelect));
		input.put("maxCscoreSelect_FEMALE", decimFormat.format(maxCscoreSelect));


		minCscorePreSelect = minCscore_MALE;
		maxCscorePreSelect = maxCscore_MALE;
		stepCscore = 0.1d;
		if(minCscorePreSelect == null || maxCscorePreSelect == null || minCscorePreSelect >= maxCscorePreSelect) {
			minCscorePreSelect = -5.0;
			maxCscorePreSelect = 5d;
		}
		else {
			stepCscore = (maxCscorePreSelect - minCscorePreSelect) / 100d;
			String stepCscoreStr = roundToFirstNonZero(stepCscore, true);
			stepCscore = Double.valueOf(stepCscoreStr);
			stepCscore = Math.abs(stepCscore);
		}
		input.put("stepCscore_MALE", roundToFirstNonZero(stepCscore, true));
		minCscoreSelect = Double.valueOf(roundToFirstNonZero(minCscorePreSelect, false));
		while(stepCscore != 0d && minCscoreSelect >= (minCscorePreSelect - (2 * stepCscore))) { minCscoreSelect -= stepCscore; };
		maxCscoreSelect = Double.valueOf(roundToFirstNonZero(maxCscorePreSelect, false));
		while(stepCscore != 0d && maxCscoreSelect <= (maxCscorePreSelect + (2 * stepCscore))) { maxCscoreSelect += stepCscore; };
		input.put("initCscore_MALE", decimFormat.format(minCscoreSelect));
		input.put("minCscoreSelect_MALE", decimFormat.format(minCscoreSelect));
		input.put("maxCscoreSelect_MALE", decimFormat.format(maxCscoreSelect));



		// Num patients: min max and steps of selectors
		input.put("minNumPatients_ALL", decimFormatInt.format(minNumPatients_ALL));
		input.put("maxNumPatients_ALL", decimFormatInt.format(maxNumPatients_ALL));
		input.put("minNumPatients_FEMALE", decimFormatInt.format(minNumPatients_FEMALE));
		input.put("maxNumPatients_FEMALE", decimFormatInt.format(maxNumPatients_FEMALE));
		input.put("minNumPatients_MALE", decimFormatInt.format(minNumPatients_MALE));
		input.put("maxNumPatients_MALE", decimFormatInt.format(maxNumPatients_MALE));

		Double stepNumPatients = 1d;
		if(minNumPatients_ALL == null || maxNumPatients_ALL == null || minNumPatients_ALL >= maxNumPatients_ALL) {
			minNumPatients_ALL = 0;
			maxNumPatients_ALL = 100;
		}

		if(minNumPatients_FEMALE == null || maxNumPatients_FEMALE == null || minNumPatients_FEMALE >= maxNumPatients_FEMALE) {
			minNumPatients_FEMALE = 0;
			maxNumPatients_FEMALE = 100;
		}

		if(minNumPatients_MALE == null || maxNumPatients_MALE == null || minNumPatients_MALE >= maxNumPatients_MALE) {
			minNumPatients_MALE = 0;
			maxNumPatients_MALE = 100;
		}

		// If the minimum number of patients is lower than 10, it should be set to 10 for privacy / de-identification purpose
		// (from ALL, FEMALE and MALE)
		Integer minNumPatientsSelect_ALL = minNumPatients_ALL;
		Integer maxNumPatientsSelect_ALL = maxNumPatients_ALL;
		String alertNumPatients_ALL = "";
		if(minNumPatientsSelect_ALL != null && minNumPatientsSelect_ALL < 10) {
			alertNumPatients_ALL = "ATTENTION: to preserve patient privacy, we suggest carry out comorbidity analyses by setting the minimum number of patients experimenting both diseases (Num. of patients cut-off) equal or greater than 10, "
					+ "even if there are disease pairs shared by less than patients in the dataset."; 
		}
		input.put("alertNumPatients_ALL", (alertNumPatients_ALL != null) ? alertNumPatients_ALL : "");
		
		Integer minNumPatientsSelect_FEMALE = minNumPatients_FEMALE;
		Integer maxNumPatientsSelect_FEMALE = maxNumPatients_FEMALE;
		String alertNumPatients_FEMALE = "";
		if(minNumPatientsSelect_ALL != null && minNumPatientsSelect_ALL < 10) {
			alertNumPatients_FEMALE = "ATTENTION: to preserve patient privacy, we suggest carry out comorbidity analyses by setting the minimum number of patients experimenting both diseases (Num. of patients cut-off) equal or greater than 10, "
					+ "even if there are disease pairs shared by less than patients in the dataset."; 
		}
		input.put("alertNumPatients_FEMALE", (alertNumPatients_FEMALE != null) ? alertNumPatients_FEMALE : "");
		
		Integer minNumPatientsSelect_MALE = minNumPatients_MALE;
		Integer maxNumPatientsSelect_MALE = maxNumPatients_MALE;
		String alertNumPatients_MALE = "";
		if(minNumPatientsSelect_ALL != null && minNumPatientsSelect_ALL < 10) {
			alertNumPatients_MALE = "ATTENTION: to preserve patient privacy, we suggest carry out comorbidity analyses by setting the minimum number of patients experimenting both diseases (Num. of patients cut-off) equal or greater than 10, "
					+ "even if there are disease pairs shared by less than patients in the dataset."; 
		}
		input.put("alertNumPatients_MALE", (alertNumPatients_MALE != null) ? alertNumPatients_MALE : "");

		input.put("stepNumPatients_ALL", roundToFirstNonZero(stepNumPatients, true));
		input.put("initNumPatients_ALL", minNumPatients_ALL);
		input.put("minNumPatientsSelect_ALL", minNumPatientsSelect_ALL);
		input.put("maxNumPatientsSelect_ALL", decimFormatInt.format(maxNumPatientsSelect_ALL.doubleValue() + stepNumPatients));

		input.put("stepNumPatients_FEMALE", roundToFirstNonZero(stepNumPatients, true));
		input.put("initNumPatients_FEMALE", minNumPatients_FEMALE);
		input.put("minNumPatientsSelect_FEMALE", minNumPatientsSelect_FEMALE);
		input.put("maxNumPatientsSelect_FEMALE", decimFormatInt.format(maxNumPatientsSelect_FEMALE.doubleValue() + stepNumPatients));

		input.put("stepNumPatients_MALE", roundToFirstNonZero(stepNumPatients, true));
		input.put("initNumPatients_MALE", minNumPatients_MALE);
		input.put("minNumPatientsSelect_MALE", minNumPatientsSelect_MALE);
		input.put("maxNumPatientsSelect_MALE", decimFormatInt.format(maxNumPatientsSelect_MALE.doubleValue() + stepNumPatients));


		// Num male and female patients
		Integer femalePatNumber = 0;
		Integer malePatNumber = 0;
		if(currentExecutor.isGenderEnabled()) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null && pat.getSex() != null) {
					if(currentExecutor.getSexRatioFemaleIdentifier() != null && pat.getSex().equals(currentExecutor.getSexRatioFemaleIdentifier())) {
						femalePatNumber++;
					}
					else if(currentExecutor.getSexRatioMaleIdentifier() != null && pat.getSex().equals(currentExecutor.getSexRatioMaleIdentifier())) {
						malePatNumber++;
					}
				}
			}
		}
		input.put("femalePatNumber", femalePatNumber + "");
		input.put("malePatNumber", malePatNumber + "");


		// Patient dataset overview
		input.put("patientByAgeAndSexDataset_FIRST_ADMISSION", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.FIRST_ADMISSION, true));
		input.put("patientByAgeAndClassificationDataset_FIRST_ADMISSION", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.FIRST_ADMISSION, false));

		input.put("patientByAgeAndSexDataset_FIRST_DIAGNOSTIC", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.FIRST_DIAGNOSTIC, true));
		input.put("patientByAgeAndClassificationDataset_FIRST_DIAGNOSTIC", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.FIRST_DIAGNOSTIC, false));

		input.put("patientByAgeAndSexDataset_LAST_ADMISSION", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.LAST_ADMISSION, true));
		input.put("patientByAgeAndClassificationDataset_LAST_ADMISSION", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.LAST_ADMISSION, false));

		input.put("patientByAgeAndSexDataset_LAST_DIAGNOSTIC", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.LAST_DIAGNOSTIC, true));
		input.put("patientByAgeAndClassificationDataset_LAST_DIAGNOSTIC", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.LAST_DIAGNOSTIC, false));

		input.put("patientByAgeAndSexDataset_EXECUTION_TIME", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.EXECUTION_TIME, true));
		input.put("patientByAgeAndClassificationDataset_EXECUTION_TIME", generateDatasetStats_PatByAgeAndSexOrCalssification(currentExecutor, PatientAgeENUM.EXECUTION_TIME, false));

		input.put("patientByBirthDateAndSexDataset", generateDatasetStats_PatByBirthDateAndSexOrCalssification(currentExecutor, true));
		input.put("patientByBirthDateAndClassificationDataset", generateDatasetStats_PatByBirthDateAndSexOrCalssification(currentExecutor, false));

		input.put("patientFacet1_name", currentExecutor.getUserInputCont().getPatientFacet1column_PD());
		input.put("patientCountBySexDataset", generateDatasetStats_PatCountByFacet(currentExecutor, true));
		input.put("patientCountByClassificationDataset", generateDatasetStats_PatCountByFacet(currentExecutor, false));

		input.put("patientCountByDiseaseAndSexDataset", generateDatasetStats_PatientCountDiseasesByFacet(currentExecutor, true));
		input.put("patientCountByDiseaseAndClassificationDataset", generateDatasetStats_PatientCountDiseasesByFacet(currentExecutor, false));
		// input.put("patientCountByDiseaseAndSexOnlyIndexDataset", generateDatasetStats_PatientCountDiseasesByFacet(currentExecutor, true, true));
		// input.put("patientCountByDiseaseAndClassificationOnlyIndexDataset", generateDatasetStats_PatientCountDiseasesByFacet(currentExecutor, false, true));

		input.put("visitCountByDiseaseAndSexDataset", generateDatasetStats_VisitCountDiseasesByFacet(currentExecutor, true));
		input.put("visitCountByDiseaseAndClassificationDataset", generateDatasetStats_VisitCountDiseasesByFacet(currentExecutor, false));
		// input.put("visitCountByDiseaseAndSexOnlyIndexDataset", generateDatasetStats_VisitCountDiseasesByFacet(currentExecutor, true, true));
		// input.put("visitCountByDiseaseAndClassificationOnlyIndexDataset", generateDatasetStats_VisitCountDiseasesByFacet(currentExecutor, false, true));
		
		input.put("ORconfidenceInterval", currentExecutor.getOddsRatioConfindeceInterval() + "");
		
		// Print log messages and data loading messages
		if(!Strings.isNullOrEmpty(logString)) {
			input.put("dataLoadProcLogMsg", logString);
		}
		
		if(currentExecutor.getUserInputCont() != null && currentExecutor.getUserInputCont().getPatientData_LOADED() != null) {
			String patientDataLog = "";
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getPatientData_LOADED().errorMsg)) {
				patientDataLog += "<b>Patient data, error messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getPatientData_LOADED().errorMsg + "</div>";
			}
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getPatientData_LOADED().warningMsg)) {
				patientDataLog += "<b>Patient data, warning messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getPatientData_LOADED().warningMsg + "</div>";
			}
			
			if(!Strings.isNullOrEmpty(patientDataLog)) {
				String fileName = currentExecutor.getUserInputCont().getPatientDataFileName_PD();
				if(!Strings.isNullOrEmpty(fileName)) {
					patientDataLog = "<b>File name:&nbsp;" + fileName + "</b><br/>" + patientDataLog;
				}
				
				input.put("patientDataLog", patientDataLog);
			}
		}
		
		if(currentExecutor.getUserInputCont() != null && currentExecutor.getUserInputCont().getVisitData_LOADED() != null) {
			String visitDataLog = "";
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getVisitData_LOADED().errorMsg)) {
				visitDataLog += "<b>Visit data, error messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getVisitData_LOADED().errorMsg + "</div>";
			}
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getVisitData_LOADED().warningMsg)) {
				visitDataLog += "<b>Visit data, warning messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getVisitData_LOADED().warningMsg + "</div>";
			}
			
			if(!Strings.isNullOrEmpty(visitDataLog)) {
				String fileName = currentExecutor.getUserInputCont().getVisitDataFileName_VD();
				if(!Strings.isNullOrEmpty(fileName)) {
					visitDataLog = "<b>File name:&nbsp;" + fileName + "</b><br/>" + visitDataLog;
				}
				
				input.put("visitDataLog", visitDataLog);
			}
		}
		
		if(currentExecutor.getUserInputCont() != null && currentExecutor.getUserInputCont().getDiagnosisData_LOADED() != null) {
			String diagnosisDataLog = "";
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getDiagnosisData_LOADED().errorMsg)) {
				diagnosisDataLog += "<b>Diagnosis data, error messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getDiagnosisData_LOADED().errorMsg + "</div>";
			}
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getDiagnosisData_LOADED().warningMsg)) {
				diagnosisDataLog += "<b>Diagnosis data, warning messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getDiagnosisData_LOADED().warningMsg + "</div>";
			}
			
			if(!Strings.isNullOrEmpty(diagnosisDataLog)) {
				String fileName = currentExecutor.getUserInputCont().getDiagnosisDataFileName_DD();
				if(!Strings.isNullOrEmpty(fileName)) {
					diagnosisDataLog = "<b>File name:&nbsp;" + fileName + "</b><br/>" + diagnosisDataLog;
				}
				
				input.put("diagnosisDataLog", diagnosisDataLog);
			}
		}
		
		if(currentExecutor.getUserInputCont() != null && currentExecutor.getUserInputCont().getDescrDiagnosisData_LOADED() != null) {
			String descrDiagnosisDataLog = "";
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getDescrDiagnosisData_LOADED().errorMsg)) {
				descrDiagnosisDataLog += "<b>Diagnosis Description data, error messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getDescrDiagnosisData_LOADED().errorMsg + "</div>";
			}
			if(!Strings.isNullOrEmpty(currentExecutor.getUserInputCont().getDescrDiagnosisData_LOADED().warningMsg)) {
				descrDiagnosisDataLog += "<b>Diagnosis Description data, warning messages generated while loading data:</b><br/>" + "<div style='margin-left: 15px;margin-bottom: 5px;'>" + currentExecutor.getUserInputCont().getDescrDiagnosisData_LOADED().warningMsg + "</div>";
			}
			
			if(!Strings.isNullOrEmpty(descrDiagnosisDataLog)) {
				String fileName = currentExecutor.getUserInputCont().getDescrDiagnosisDataFileName_DDE();
				if(!Strings.isNullOrEmpty(fileName)) {
					descrDiagnosisDataLog = "<b>File name:&nbsp;" + fileName + "</b><br/>" + descrDiagnosisDataLog;
				}
				
				input.put("descrDiagnosisDataLog", descrDiagnosisDataLog);
			}
		}
		
		
		
		// Print error messages
		if(!Strings.isNullOrEmpty(errorMsg)) {
			input.put("errorMsg", errorMsg);
		}

		input.put("execID", executionID + "");
		Date currentDate = new Date();
		input.put("execDate", dateFormatter.format(currentDate));
		input.put("execDateCSVfileName", dateFormatterCSVfileName.format(currentDate));

		input.put("executorObj", currentExecutor);

		input.put("patient_filter", ((currentExecutor.getPatientFilter() != null) ? currentExecutor.getPatientFilter().toString(false, true) : "---"));
		input.put("directionality_filter", ((currentExecutor.getDirectionalityFilter() != null) ? currentExecutor.getDirectionalityFilter().toString(true) : "---"));
		input.put("directionality_filter_numDays", ((currentExecutor.getDirectionalityFilter() != null && currentExecutor.getDirectionalityFilter().getMinNumDays() != null) ? currentExecutor.getDirectionalityFilter().getMinNumDays() : -1l));
		input.put("score_filter", ((currentExecutor.getScoreFilter() != null) ? currentExecutor.getScoreFilter().toString(true) : "---"));



		Template template = null;
		try {
			template = cfg.getTemplate("analysisResults_Body.ftl");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringWriter stringWriter = new StringWriter();
		try {
			template.process(input, stringWriter);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public static String roundToFirstNonZero(Double inputNumber, boolean oneAsFirstDigit) {
		String outputStringNumber = "";

		String inputNumberStr = decimFormat.format(inputNumber);
		if(inputNumberStr.equals("0")) {
			outputStringNumber = "0.0";
		}
		else if(inputNumberStr.startsWith("0.") || inputNumberStr.startsWith("-0.")) {
			// Lower than one number

			outputStringNumber = ((inputNumberStr.startsWith("0.")) ? "0." : "-0.");

			boolean foundFirstNonZeroDigit = false;
			for(int i = ((inputNumberStr.startsWith("0.")) ? 2 : 3); i < inputNumberStr.length(); i++) {
				char c = inputNumberStr.charAt(i);

				if(!foundFirstNonZeroDigit) {
					if(c != '0') {
						outputStringNumber += ((oneAsFirstDigit) ? '1' : c);
						foundFirstNonZeroDigit = true;
					}
					else {
						outputStringNumber += '0';
					}
				}
			}

			if(inputNumberStr.equals("0.") || inputNumberStr.equals("-0.")) {
				outputStringNumber += "0";
			}

		}
		else {
			// Greater than one number

			if(inputNumberStr.contains(".")) inputNumberStr = inputNumberStr.substring(0, inputNumberStr.indexOf("."));

			boolean foundFirstNonZeroDigit = false;
			for(int i = 0; i < inputNumberStr.length(); i++) {
				char c = inputNumberStr.charAt(i);

				if(!foundFirstNonZeroDigit && c == '0') {
					/* SKIP LEADING ZEROS */
				}
				else if(!foundFirstNonZeroDigit && c != '0') {
					outputStringNumber += ((oneAsFirstDigit) ? '1' : c);
					foundFirstNonZeroDigit = true;
				}
				else {
					outputStringNumber += '0';
				}
			}
		}

		return outputStringNumber;
	}
	
	public static String generateHTMLwebFormError(String errorMessage) {

		Map<String, Object> input = new HashMap<String, Object>();
		if(!Strings.isNullOrEmpty(errorMessage)) {
			String splitErrorMessage = "";
			for(String errorMessageLine : errorMessage.split("\n")) {
				splitErrorMessage += errorMessageLine + "<br/>";
			}

			input.put("errorMsg", splitErrorMessage);
		}
		else {
			input.put("errorMsg", "");
		}

		input.put("CSSJScount", countReloadCSSJS);
		countReloadCSSJS++;

		Template template = null;
		try {
			template = cfg.getTemplate("comor4webForm_Error_Body.ftl");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringWriter stringWriter = new StringWriter();
		try {
			template.process(input, stringWriter);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}


	public static String generateDatasetStats_PatByAgeAndSexOrCalssification(ComorbidityMiner currentExecutor, PatientAgeENUM ageComputationMethod, boolean considerSex) {

		if( ageComputationMethod == null || (!considerSex && (currentExecutor.getUserInputCont().getPatientFacet1column_PD() == null || currentExecutor.getUserInputCont().getPatientFacet1column_PD().trim().length() == 0)) ) {
			return "[]";
		}

		String keyTotal = "Total";
		String keyUnspec = "Unspecified";

		Map<Long, Map<String, Integer>> totPatAgeMap = new HashMap<Long, Map<String, Integer>>();

		Set<String> facetNameSet = new HashSet<String>();
		long minAge = -1l;
		long maxAge = -1l;
		if(currentExecutor != null && currentExecutor.getPatientList() != null && currentExecutor.getPatientList().size() > 0) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null) {
					try {
						long patAge = pat.getPatientAge(ageComputationMethod);
						if(patAge != -1 && patAge >= 0l && patAge <= 150l) {

							if(!totPatAgeMap.containsKey(patAge)) totPatAgeMap.put(patAge, new HashMap<String, Integer>());
							Map<String, Integer> counterForAge = totPatAgeMap.get(patAge);

							if(minAge == -1l || minAge > patAge) minAge = patAge;
							if(maxAge == -1l || maxAge < patAge) maxAge = patAge;

							// Total
							if(!counterForAge.containsKey(keyTotal)) counterForAge.put(keyTotal, 0);
							counterForAge.put(keyTotal, counterForAge.get(keyTotal) + 1);
							facetNameSet.add(keyTotal);

							// Sex / classification string
							String keyFacet = "";
							if(considerSex && currentExecutor.isGenderEnabled()) {
								keyFacet = (pat.getSex() != null && pat.getSex().trim().length() > 0) ? pat.getSex().trim() : keyUnspec;
							}
							else {
								keyFacet = (pat.getClassification1() != null && pat.getClassification1().trim().length() > 0) ? pat.getClassification1().trim() : keyUnspec;
							}
							
							if(!considerSex || (considerSex && currentExecutor.isGenderEnabled())) {
								if(!counterForAge.containsKey(keyFacet)) counterForAge.put(keyFacet, 0);
								counterForAge.put(keyFacet, counterForAge.get(keyFacet) + 1);
								facetNameSet.add(keyFacet);
							}
							
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						/* DO NOTHING */
					}

				}
			}
		}

		if(!considerSex && facetNameSet != null && 
				facetNameSet.size() == 2 && facetNameSet.contains(keyTotal) && facetNameSet.contains(keyUnspec)) {
			return "[]";
		}

		// Sanitize min and max age
		if(minAge == -1l) minAge = 0l; 
		if(maxAge == -1l) minAge = 140l; 

		// CSS color list
		List<String> colorList = new ArrayList<String>();
		colorList.add("blue");
		colorList.add("green");
		colorList.add("grey");
		colorList.add("orange");
		colorList.add("purple");
		colorList.add("red");
		colorList.add("yellow");

		// Generate JSON
		StringBuffer retStr = new StringBuffer("[");

		Iterator<String> facetNameSetIter = facetNameSet.iterator();
		int colorListIndex = 0;
		while(facetNameSetIter.hasNext()) {
			String facetName = facetNameSetIter.next();
			if(facetName != null && facetName.trim().length() > 0) {
				retStr.append("{\n");

				// x: [1, 2, 3, 4],
				retStr.append("x: [");
				for(long i = minAge; i <= maxAge; i++) {
					retStr.append(i + ((i < maxAge) ? ", " : ""));
				}
				retStr.append("],\n");

				// y: [3, 5, 10, 9],
				retStr.append("y: [");
				for(long i = minAge; i <= maxAge; i++) {
					Map<String, Integer> totPatAgeMapForAge = totPatAgeMap.get(i);

					Integer totPatients = 0;
					if(totPatAgeMapForAge != null) {
						Integer counterForFacet = totPatAgeMapForAge.get(facetName);
						if(counterForFacet != null && counterForFacet >= 0) {
							totPatients = counterForFacet;
						}
					}

					retStr.append(totPatients + ((i < maxAge) ? ", " : ""));
				}
				retStr.append("],\n");

				retStr.append("type: 'scatter',\n");
				retStr.append("mode: 'lines+markers',\n");
				retStr.append("name: '" + facetName + "',\n");
				retStr.append("line: {\n");
				retStr.append("  color: window.chartColors." + colorList.get(colorListIndex) + ",\n");
				colorListIndex = (colorListIndex + 1) % colorList.size();
				retStr.append("  width: 1\n");
				retStr.append("  }\n");

				if(facetNameSetIter.hasNext()) {
					retStr.append("},\n");
				}
				else {
					retStr.append("}\n");
				}
			}
		}


		retStr.append("]");
		return retStr.toString();
	}


	public static String generateDatasetStats_PatByBirthDateAndSexOrCalssification(ComorbidityMiner currentExecutor, boolean considerSex) {

		if(!considerSex && (currentExecutor.getUserInputCont().getPatientFacet1column_PD() == null || currentExecutor.getUserInputCont().getPatientFacet1column_PD().trim().length() == 0)) {
			return "[]";
		}

		String keyTotal = "Total";
		String keyUnspec = "Unspecified";

		Map<Integer, Map<String, Integer>> totPatBirthYearMap = new HashMap<Integer, Map<String, Integer>>();

		Set<String> facetNameSet = new HashSet<String>();
		int minBirthYear = -1;
		int maxBirthYear = -1;
		if(currentExecutor != null && currentExecutor.getPatientList() != null && currentExecutor.getPatientList().size() > 0) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null) {
					try {
						int patBirthYear = -1;
						if(pat.getBirthDate() != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(pat.getBirthDate());
							patBirthYear = cal.get(Calendar.YEAR);
						}

						if(patBirthYear != -1 && patBirthYear >= 0l) {

							if(!totPatBirthYearMap.containsKey(patBirthYear)) totPatBirthYearMap.put(patBirthYear, new HashMap<String, Integer>());
							Map<String, Integer> counterForAge = totPatBirthYearMap.get(patBirthYear);

							if(minBirthYear == -1l || minBirthYear > patBirthYear) minBirthYear = patBirthYear;
							if(maxBirthYear == -1l || maxBirthYear < patBirthYear) maxBirthYear = patBirthYear;

							// Total
							if(!counterForAge.containsKey(keyTotal)) counterForAge.put(keyTotal, 0);
							counterForAge.put(keyTotal, counterForAge.get(keyTotal) + 1);
							facetNameSet.add(keyTotal);

							// Sex / classification string
							String keyFacet = "";
							if(considerSex && currentExecutor.isGenderEnabled()) {
								keyFacet = (pat.getSex() != null && pat.getSex().trim().length() > 0) ? pat.getSex().trim() : keyUnspec;
							}
							else {
								keyFacet = (pat.getClassification1() != null && pat.getClassification1().trim().length() > 0) ? pat.getClassification1().trim() : keyUnspec;
							}
							
							if(!considerSex || (considerSex && currentExecutor.isGenderEnabled())) {
								if(!counterForAge.containsKey(keyFacet)) counterForAge.put(keyFacet, 0);
								counterForAge.put(keyFacet, counterForAge.get(keyFacet) + 1);
								facetNameSet.add(keyFacet);
							}
							
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						/* DO NOTHING */
					}

				}
			}
		}

		// If not considering set and only Total and Unspecified... return no data
		if(!considerSex && facetNameSet != null && 
				facetNameSet.size() == 2 && facetNameSet.contains(keyTotal) && facetNameSet.contains(keyUnspec)) {
			return "[]";
		}

		// Sanitize min and max age
		if(minBirthYear == -1l) minBirthYear = 1800; 
		if(maxBirthYear == -1l) minBirthYear = 2080; 

		// CSS color list
		List<String> colorList = new ArrayList<String>();
		colorList.add("blue");
		colorList.add("green");
		colorList.add("grey");
		colorList.add("orange");
		colorList.add("purple");
		colorList.add("red");
		colorList.add("yellow");

		// Generate JSON
		StringBuffer retStr = new StringBuffer("[");

		Iterator<String> facetNameSetIter = facetNameSet.iterator();
		int colorListIndex = 0;
		while(facetNameSetIter.hasNext()) {
			String facetName = facetNameSetIter.next();
			if(facetName != null && facetName.trim().length() > 0) {
				retStr.append("{\n");

				// x: [1, 2, 3, 4],
				retStr.append("x: [");
				for(int i = minBirthYear; i <= maxBirthYear; i++) {
					retStr.append(i + ((i < maxBirthYear) ? ", " : ""));
				}
				retStr.append("],\n");

				// y: [3, 5, 10, 9],
				retStr.append("y: [");
				for(int i = minBirthYear; i <= maxBirthYear; i++) {
					Map<String, Integer> totPatAgeMapForAge = totPatBirthYearMap.get(i);

					Integer totPatients = 0;
					if(totPatAgeMapForAge != null) {
						Integer counterForFacet = totPatAgeMapForAge.get(facetName);
						if(counterForFacet != null && counterForFacet >= 0) {
							totPatients = counterForFacet;
						}
					}

					retStr.append(totPatients + ((i < maxBirthYear) ? ", " : ""));
				}
				retStr.append("],\n");

				retStr.append("type: 'scatter',\n");
				retStr.append("mode: 'lines+markers',\n");
				retStr.append("name: '" + facetName + "',\n");
				retStr.append("line: {\n");
				retStr.append("  color: window.chartColors." + colorList.get(colorListIndex) + ",\n");
				colorListIndex = (colorListIndex + 1) % colorList.size();
				retStr.append("  width: 1\n");
				retStr.append("  }\n");

				if(facetNameSetIter.hasNext()) {
					retStr.append("},\n");
				}
				else {
					retStr.append("}\n");
				}
			}
		}


		retStr.append("]");
		return retStr.toString();
	}

	public static String generateDatasetStats_PatCountByFacet(ComorbidityMiner currentExecutor, boolean considerSex) {

		if(!considerSex && (currentExecutor.getUserInputCont().getPatientFacet1column_PD() == null || currentExecutor.getUserInputCont().getPatientFacet1column_PD().trim().length() == 0) ) {
			return "[]";
		}

		String keyUnspec = "Unspecified";

		Map<String, Integer> facetCounter = new HashMap<String, Integer>();
		Integer totalCount = 0;
		if(currentExecutor != null && currentExecutor.getPatientList() != null && currentExecutor.getPatientList().size() > 0) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null) {
					try {
						// Sex / classification string
						String facetValue = keyUnspec;
						if(considerSex && currentExecutor.isGenderEnabled()) {
							facetValue = (pat.getSex() != null && pat.getSex().trim().length() > 0) ? pat.getSex().trim() : keyUnspec;
						}
						else {
							facetValue = (pat.getClassification1() != null && pat.getClassification1().trim().length() > 0) ? pat.getClassification1().trim() : keyUnspec;
						}

						if(!considerSex || (considerSex && currentExecutor.isGenderEnabled()) ) {
							if(!facetCounter.containsKey(facetValue)) facetCounter.put(facetValue, 0);
							facetCounter.put(facetValue, facetCounter.get(facetValue) + 1);
						}

						totalCount += 1;

					}
					catch(Exception e) {
						e.printStackTrace();
						/* DO NOTHING */
					}
				}
			}
		}

		if(!considerSex && facetCounter != null && 
				facetCounter.size() == 1 && facetCounter.containsKey(keyUnspec)) {
			return "[]";
		}

		// Generate JSON
		StringBuffer retStr = new StringBuffer("[{\n");

		// x: ['giraffes', 'orangutans', 'monkeys'],
		retStr.append("x: [ 'Total', ");
		List<String> keyList = new ArrayList<String>(facetCounter.keySet());
		for(int i = 0; i < keyList.size(); i++) {
			String keyValue = keyList.get(i);
			retStr.append("'" + keyValue + "'" +  ((i < keyList.size() - 1) ? ", " : ""));
		}
		retStr.append("],\n");

		// y: [20, 14, 23],
		retStr.append("y: [" + totalCount + ", ");
		for(int i = 0; i < keyList.size(); i++) {
			String keyValue = keyList.get(i);
			retStr.append(facetCounter.get(keyValue) + ((i < keyList.size() - 1) ? ", " : ""));
		}
		retStr.append("],\n");

		retStr.append("type: 'bar'}\n");

		retStr.append("]\n");

		return retStr.toString();
	}


	public static String generateDatasetStats_PatientCountDiseasesByFacet(ComorbidityMiner currentExecutor, boolean considerSex) {

		if(!considerSex && (currentExecutor.getUserInputCont().getPatientFacet1column_PD() == null || currentExecutor.getUserInputCont().getPatientFacet1column_PD().trim().length() == 0)) {
			return "[]";
		}

		String keyUnspec = "Unspecified";

		// Populate maps
		Map<Integer, String> diagnosisIdToCodeMap = new HashMap<Integer, String>();
		for(Entry<String, Integer> diagnosisCodeToIdEntry : currentExecutor.getDiagnosisCodeStringIdMap().entrySet()) {
			diagnosisIdToCodeMap.put(diagnosisCodeToIdEntry.getValue(), diagnosisCodeToIdEntry.getKey());
		}
		Map<Integer, String> diagnosisIdToDescriptionMap = new HashMap<Integer, String>();
		for(Entry<String, String> diagnosisCodeToDescriptionEntry : currentExecutor.getDiagnosisCodeStringDescriptionMap().entrySet()) {
			if(diagnosisCodeToDescriptionEntry != null && diagnosisCodeToDescriptionEntry.getKey() != null && 
					currentExecutor.getDiagnosisCodeStringIdMap().containsKey(diagnosisCodeToDescriptionEntry.getKey())) {
				diagnosisIdToDescriptionMap.put(currentExecutor.getDiagnosisCodeStringIdMap().get(diagnosisCodeToDescriptionEntry.getKey()), diagnosisCodeToDescriptionEntry.getValue());
			}
		}
		
		Set<String> facetSet = new HashSet<String>();

		Map<String, Map<String, Integer>> diseaseFacetCounter = new LinkedHashMap<String, Map<String, Integer>>();
		if(currentExecutor != null && currentExecutor.getPatientList() != null && currentExecutor.getPatientList().size() > 0) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null) {
					try {
						Set<Integer> diseasesAlreadyAssociatedToPatient = new HashSet<Integer>();

						// Sex / classification string
						String facetValue = keyUnspec;
						if(considerSex) {
							facetValue = (pat.getSex() != null && pat.getSex().trim().length() > 0) ? pat.getSex().trim() : keyUnspec;
						}
						else {
							facetValue = (pat.getClassification1() != null && pat.getClassification1().trim().length() > 0) ? pat.getClassification1().trim() : keyUnspec;
						}

						facetSet.add(facetValue);

						Set<Visit> patVisitSet = pat.getVisitSet();
						if(patVisitSet != null && patVisitSet.size() > 0) {
							for(Visit patVisit : patVisitSet) {
								if(patVisit != null) {
									Set<Integer> patVisitDiagnosisSet = patVisit.getDiagnosisCodeSet();
									for(Integer patVisitDiagnosisInt : patVisitDiagnosisSet) {
										if(patVisitDiagnosisInt != null) {
											if(!diseasesAlreadyAssociatedToPatient.contains(patVisitDiagnosisInt) && diagnosisIdToCodeMap.containsKey(patVisitDiagnosisInt)) {

												// Count patient with facet for that disease

												// Generate disease string
												String diseaseString = diagnosisIdToCodeMap.get(patVisitDiagnosisInt);
												String diseaseDescription = (diagnosisIdToDescriptionMap.containsKey(patVisitDiagnosisInt)) ? diagnosisIdToDescriptionMap.get(patVisitDiagnosisInt) : null;
												if(diseaseDescription != null && diseaseDescription.equals(diseaseString)) {
													diseaseDescription = "";
												}
												if(diseaseDescription != null && diseaseDescription.length() > 25) {
													diseaseDescription =  diseaseDescription.substring(0, 24) + "...";
												}
												
												diseaseString = diseaseString + ((diseaseDescription != null) ? " " + diseaseDescription : "");

												if(!diseaseFacetCounter.containsKey(diseaseString)) diseaseFacetCounter.put(diseaseString, new HashMap<String, Integer>());

												Map<String, Integer> facetCounterMapForDisease = diseaseFacetCounter.get(diseaseString);
												if(!facetCounterMapForDisease.containsKey(facetValue)) facetCounterMapForDisease.put(facetValue, 0);
												facetCounterMapForDisease.put(facetValue, facetCounterMapForDisease.get(facetValue) + 1);

												diseasesAlreadyAssociatedToPatient.add(patVisitDiagnosisInt);
											}
										}
									}
								}
							}
						}

					}
					catch(Exception e) {
						e.printStackTrace();
						/* DO NOTHING */
					}
				}
			}
		}

		int counterOfDiseasesWithAllUnspecified = 0;
		for(Entry<String, Map<String, Integer>> diseaseFacet : diseaseFacetCounter.entrySet() ) {
			if(diseaseFacet.getValue() != null && diseaseFacet.getValue().size() == 1) {
				counterOfDiseasesWithAllUnspecified++;
			}
		}

		if(!considerSex && counterOfDiseasesWithAllUnspecified == diseaseFacetCounter.size()) {
			return "[]";
		}

		// Diseases by occurrence descending
		Map<String, Integer> diseaseFrequencyMap = new HashMap<String, Integer>();
		for(Entry<String, Map<String, Integer>> diseaseFacet : diseaseFacetCounter.entrySet() ) {
			if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {

				int facetCounter = 0;
				for(Entry<String, Integer> diseaseFacetEntry : diseaseFacet.getValue().entrySet()) {
					if(diseaseFacetEntry != null && diseaseFacetEntry.getKey() != null && diseaseFacetEntry.getValue() != null) {
						facetCounter += diseaseFacetEntry.getValue();
					}
				}

				diseaseFrequencyMap.put(diseaseFacet.getKey(), facetCounter);
			}
		}

		Map<String, Integer> diseaseFrequencyMapSorted = GenericUtils.sortByValue(diseaseFrequencyMap, true);

		// Generate JSON
		StringBuffer retStr = new StringBuffer("[\n");

		boolean firstFeat = true;
		for(String facetName : facetSet) {
			if(facetName != null) {
				if(!firstFeat) {
					retStr.append(",\n");
				}
				else {
					firstFeat = false;
				}
				retStr.append("{");

				// x: ['giraffes', 'orangutans', 'monkeys'],
				retStr.append("x: [");
				boolean firstDisease = true;
				for(Entry<String, Integer> diseaseFacet : diseaseFrequencyMapSorted.entrySet() ) {
					if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {
						String disease = diseaseFacet.getKey();
						retStr.append(((firstDisease) ? "" : ", ") + "\"" + JSONValue.escape(disease) + "\"");
						firstDisease = false;
					}
				}
				retStr.append("],\n");

				//  y: [20, 14, 23],
				retStr.append("y: [");
				boolean firstFacetValue = true;
				for(Entry<String, Integer> diseaseFacet : diseaseFrequencyMapSorted.entrySet() ) {
					if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {

						int facetValue = 0;
						if(diseaseFacetCounter.containsKey(diseaseFacet.getKey())) {
							for(Entry<String, Integer> diseaseFacetEntry : diseaseFacetCounter.get(diseaseFacet.getKey()).entrySet()) {
								if(diseaseFacetEntry != null && diseaseFacetEntry.getKey() != null && diseaseFacetEntry.getValue() != null && diseaseFacetEntry.getKey().equals(facetName)) {
									facetValue = diseaseFacetEntry.getValue();
								}
							}
						}

						retStr.append(((firstFacetValue) ? "" : ", ") + facetValue);
						firstFacetValue = false;
					}
				}
				retStr.append("],\n");

				retStr.append("name: '" + JSONValue.escape(facetName) + "',\n");
				retStr.append("type: 'bar'\n");

				retStr.append("}");
			}
		}

		retStr.append("]\n");

		return retStr.toString();
	}


	public static String generateDatasetStats_VisitCountDiseasesByFacet(ComorbidityMiner currentExecutor, boolean considerSex) {

		if(!considerSex && (currentExecutor.getUserInputCont().getPatientFacet1column_PD() == null || currentExecutor.getUserInputCont().getPatientFacet1column_PD().trim().length() == 0) ) {
			return "[]";
		}

		String keyUnspec = "Unspecified";

		// Populate maps
		Map<Integer, String> diagnosisIdToCodeMap = new HashMap<Integer, String>();
		for(Entry<String, Integer> diagnosisCodeToIdEntry : currentExecutor.getDiagnosisCodeStringIdMap().entrySet()) {
			diagnosisIdToCodeMap.put(diagnosisCodeToIdEntry.getValue(), diagnosisCodeToIdEntry.getKey());
		}
		Map<Integer, String> diagnosisIdToDescriptionMap = new HashMap<Integer, String>();
		for(Entry<String, String> diagnosisCodeToDescriptionEntry : currentExecutor.getDiagnosisCodeStringDescriptionMap().entrySet()) {
			if(diagnosisCodeToDescriptionEntry != null && diagnosisCodeToDescriptionEntry.getKey() != null && 
					currentExecutor.getDiagnosisCodeStringIdMap().containsKey(diagnosisCodeToDescriptionEntry.getKey())) {
				diagnosisIdToDescriptionMap.put(currentExecutor.getDiagnosisCodeStringIdMap().get(diagnosisCodeToDescriptionEntry.getKey()), diagnosisCodeToDescriptionEntry.getValue());
			}
		}

		Set<String> facetSet = new HashSet<String>();

		Map<String, Map<String, Integer>> diseaseFacetCounter = new LinkedHashMap<String, Map<String, Integer>>();
		if(currentExecutor != null && currentExecutor.getPatientList() != null && currentExecutor.getPatientList().size() > 0) {
			for(Patient pat : currentExecutor.getPatientList()) {
				if(pat != null) {
					try {

						// Sex / classification string
						String facetValue = keyUnspec;
						if(considerSex) {
							facetValue = (pat.getSex() != null && pat.getSex().trim().length() > 0) ? pat.getSex().trim() : keyUnspec;
						}
						else {
							facetValue = (pat.getClassification1() != null && pat.getClassification1().trim().length() > 0) ? pat.getClassification1().trim() : keyUnspec;
						}

						facetSet.add(facetValue);

						Set<Visit> patVisitSet = pat.getVisitSet();
						if(patVisitSet != null && patVisitSet.size() > 0) {
							for(Visit patVisit : patVisitSet) {
								if(patVisit != null) {
									Set<Integer> patVisitDiagnosisSet = patVisit.getDiagnosisCodeSet();
									for(Integer patVisitDiagnosisInt : patVisitDiagnosisSet) {
										if(patVisitDiagnosisInt != null) {
											if(diagnosisIdToCodeMap.containsKey(patVisitDiagnosisInt)) {

												// Count visit with facet for that disease

												// Generate disease string
												String diseaseString = diagnosisIdToCodeMap.get(patVisitDiagnosisInt);
												String diseaseDescription = (diagnosisIdToDescriptionMap.containsKey(patVisitDiagnosisInt)) ? diagnosisIdToDescriptionMap.get(patVisitDiagnosisInt) : null;
												if(diseaseDescription != null && diseaseDescription.equals(diseaseString)) {
													diseaseDescription = "";
												}
												if(diseaseDescription != null && diseaseDescription.length() > 25) {
													diseaseDescription =  diseaseDescription.substring(0, 24) + "...";
												}
												
												diseaseString = diseaseString + ((diseaseDescription != null) ? " " + diseaseDescription : "");

												if(!diseaseFacetCounter.containsKey(diseaseString)) diseaseFacetCounter.put(diseaseString, new HashMap<String, Integer>());

												Map<String, Integer> facetCounterMapForDisease = diseaseFacetCounter.get(diseaseString);
												if(!facetCounterMapForDisease.containsKey(facetValue)) facetCounterMapForDisease.put(facetValue, 0);
												facetCounterMapForDisease.put(facetValue, facetCounterMapForDisease.get(facetValue) + 1);

											}
										}
									}
								}
							}
						}

					}
					catch(Exception e) {
						e.printStackTrace();
						/* DO NOTHING */
					}
				}
			}
		}

		int counterOfDiseasesWithAllUnspecified = 0;
		for(Entry<String, Map<String, Integer>> diseaseFacet : diseaseFacetCounter.entrySet() ) {
			if(diseaseFacet.getValue() != null && diseaseFacet.getValue().size() == 1) {
				counterOfDiseasesWithAllUnspecified++;
			}
		}

		if(!considerSex && counterOfDiseasesWithAllUnspecified == diseaseFacetCounter.size()) {
			return "[]";
		}

		// Diseases by occurrence descending
		Map<String, Integer> diseaseFrequencyMap = new HashMap<String, Integer>();
		for(Entry<String, Map<String, Integer>> diseaseFacet : diseaseFacetCounter.entrySet() ) {
			if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {

				int facetCounter = 0;
				for(Entry<String, Integer> diseaseFacetEntry : diseaseFacet.getValue().entrySet()) {
					if(diseaseFacetEntry != null && diseaseFacetEntry.getKey() != null && diseaseFacetEntry.getValue() != null) {
						facetCounter += diseaseFacetEntry.getValue();
					}
				}

				diseaseFrequencyMap.put(diseaseFacet.getKey(), facetCounter);
			}
		}

		Map<String, Integer> diseaseFrequencyMapSorted = GenericUtils.sortByValue(diseaseFrequencyMap, true);

		// Generate JSON
		StringBuffer retStr = new StringBuffer("[\n");

		boolean firstFeat = true;
		for(String facetName : facetSet) {
			if(facetName != null) {
				if(!firstFeat) {
					retStr.append(",\n");
				}
				else {
					firstFeat = false;
				}
				retStr.append("{");

				// x: ['giraffes', 'orangutans', 'monkeys'],
				retStr.append("x: [");
				boolean firstDisease = true;
				for(Entry<String, Integer> diseaseFacet : diseaseFrequencyMapSorted.entrySet() ) {
					if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {
						String disease = diseaseFacet.getKey();
						retStr.append(((firstDisease) ? "" : ", ") + "\"" + JSONValue.escape(disease) + "\"");
						firstDisease = false;
					}
				}
				retStr.append("],\n");

				//  y: [20, 14, 23],
				retStr.append("y: [");
				boolean firstFacetValue = true;
				for(Entry<String, Integer> diseaseFacet : diseaseFrequencyMapSorted.entrySet() ) {
					if(diseaseFacet.getKey() != null && diseaseFacet.getValue() != null) {

						int facetValue = 0;
						if(diseaseFacetCounter.containsKey(diseaseFacet.getKey())) {
							for(Entry<String, Integer> diseaseFacetEntry : diseaseFacetCounter.get(diseaseFacet.getKey()).entrySet()) {
								if(diseaseFacetEntry != null && diseaseFacetEntry.getKey() != null && diseaseFacetEntry.getValue() != null && diseaseFacetEntry.getKey().equals(facetName)) {
									facetValue = diseaseFacetEntry.getValue();
								}
							}
						}

						retStr.append(((firstFacetValue) ? "" : ", ") + facetValue);
						firstFacetValue = false;
					}
				}
				retStr.append("],\n");

				retStr.append("name: '" + JSONValue.escape(facetName) + "',\n");
				retStr.append("type: 'bar'\n");

				retStr.append("}");
			}
		}

		retStr.append("]\n");

		return retStr.toString();
	}


	public static void main(String[] args) {
		// TemplateUtils.generateHTMLanalysisResTemplate(null, null, null, null);
	}


}
