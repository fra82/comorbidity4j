package es.imim.ibi.comorbidity4j.server.reservlet;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.template.TemplateUtils;

public class ComputeComorbidityThread implements Callable<String> {
	
	private ComorbidityMiner currentExecutor;
	private Collection<ComorbidityPairResult> comorbidityPairs_ALL;
	private Collection<ComorbidityPairResult> comorbidityPairs_FEMALE;
	private Collection<ComorbidityPairResult> comorbidityPairs_MALE;
	private String logString;
	private String errorMsg;
	private boolean fileStorage;
	private String execID;
	private String storageFullName;
	
	private boolean filterInteresting = false;
	private int maxValueOfPairsNotToApplyFilter = 100000000;
	
	private static DecimalFormat decimFormatFiveDec = null;
	
	static {
		DecimalFormatSymbols otherSymbols_fiveDec = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols_fiveDec.setDecimalSeparator('.');
		otherSymbols_fiveDec.setGroupingSeparator(',');
		decimFormatFiveDec = new DecimalFormat("#######0.00000", otherSymbols_fiveDec);
		decimFormatFiveDec.setRoundingMode(RoundingMode.HALF_DOWN);
		decimFormatFiveDec.setDecimalSeparatorAlwaysShown(true);
		decimFormatFiveDec.setGroupingUsed(false);
	}
	
	public ComputeComorbidityThread(ComorbidityMiner currentExecutor,
			Collection<ComorbidityPairResult> comorbidityPairs_ALL,
			Collection<ComorbidityPairResult> comorbidityPairs_FEMALE,
			Collection<ComorbidityPairResult> comorbidityPairs_MALE, String logString, String errorMsg,
			boolean fileStorage, String execID, String storageFullName,
			boolean filterInteresting, int maxValueOfPairsNotToApplyFilter) {
		
		super();
		this.currentExecutor = currentExecutor;
		this.comorbidityPairs_ALL = comorbidityPairs_ALL;
		this.comorbidityPairs_FEMALE = comorbidityPairs_FEMALE;
		this.comorbidityPairs_MALE = comorbidityPairs_MALE;
		this.logString = logString;
		this.errorMsg = errorMsg;
		this.fileStorage = fileStorage;
		this.execID = execID;
		this.storageFullName = storageFullName;
		this.filterInteresting = filterInteresting;
		this.maxValueOfPairsNotToApplyFilter = maxValueOfPairsNotToApplyFilter;
		
	}



	@Override
	public String call() throws Exception {
		
		Collection<ComorbidityPairResult> comorPair_ALL = new ArrayList<ComorbidityPairResult>();
		Collection<ComorbidityPairResult> comorPair_FEMALE = new ArrayList<ComorbidityPairResult>();
		Collection<ComorbidityPairResult> comorPair_MALE = new ArrayList<ComorbidityPairResult>();
		
		StringBuffer filterResultMessageHTML = new StringBuffer("The total number of diagnosis pairs considered in this analysis is <b>" + 
				(((this.comorbidityPairs_ALL != null) ? this.comorbidityPairs_ALL.size() : 0) + ((this.comorbidityPairs_FEMALE != null) ? this.comorbidityPairs_FEMALE.size() : 0) + ((this.comorbidityPairs_MALE != null) ? this.comorbidityPairs_MALE.size() : 0)) + 
				"</b> of which:" +
				((this.comorbidityPairs_ALL != null) ? " <b>" + this.comorbidityPairs_ALL.size() + "</b> pairs in the gender independent comorbidity analysis" : "") + " " +
				((this.comorbidityPairs_FEMALE != null) ? " <b>" + this.comorbidityPairs_FEMALE.size() + "</b> pairs in the comorbidity analysis of females" : "") + " " +
				((this.comorbidityPairs_MALE != null) ? " <b>" + this.comorbidityPairs_MALE.size() + "</b> pairs in the gender independent comorbidity analysis of males" : "") + ".<br/><br/>");
		
		if(this.filterInteresting) {
			
			if(this.comorbidityPairs_ALL != null && this.comorbidityPairs_ALL.size() > this.maxValueOfPairsNotToApplyFilter) {
				
				// Filter ALL
				if(this.comorbidityPairs_ALL != null && this.comorbidityPairs_ALL.size() > 0) {
					
					List<ComorbidityPairResult> sortedRR = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getRelativeRiskIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedPhi = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getPhiIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedOR = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getOddsRatioIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFish = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getFisherTest(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFishAdj = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getFisherTestAdjusted(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedCscore = this.comorbidityPairs_ALL.stream().sorted( Comparator.comparing(cp -> cp.getScore()) ).collect(Collectors.toList());
					
					comorPair_ALL = new HashSet<ComorbidityPairResult>();
					
					boolean reachedMax = false;
					int indexRR = sortedRR.size() - 1;
					int indexPhi = sortedPhi.size() - 1;
					int indexOR = sortedOR.size() - 1;
					int indexFisher = sortedFish.size() - 1;
					int indexFisherAdj = sortedFishAdj.size() - 1;
					int indexCscore = sortedCscore.size() - 1;
					while(!reachedMax) {
						
						// RRisk > 1
						if(indexRR >= 0) {
							comorPair_ALL.add(sortedRR.get(indexRR--));
						}
						
						// OR > 0
						if(indexOR >= 0) {
							comorPair_ALL.add(sortedOR.get(indexOR--));
						}
						
						// Phi > 0
						if(indexPhi >= 0) {
							comorPair_ALL.add(sortedPhi.get(indexPhi--));
						}
						
						// Fisher > 0
						if(indexFisher >= 0) {
							comorPair_ALL.add(sortedFish.get(indexFisher--));
						}
						
						if(indexFisherAdj >= 0) {
							comorPair_ALL.add(sortedFishAdj.get(indexFisherAdj--));
						}
						
						// CScore > 0
						if(indexCscore >= 0) {
							comorPair_ALL.add(sortedCscore.get(indexCscore--));
						}
						
						
						if(comorPair_ALL.size() > this.maxValueOfPairsNotToApplyFilter) {
							reachedMax = true;
						}
					}
					
					if(comorPair_ALL.size() < this.comorbidityPairs_ALL.size()) {
						// Filter applied
						filterResultMessageHTML.append("<div style='margin-left:25px;margin-bottom:10px;'><span style='color:red;'>IMPORTANT&nbsp;&gt;&nbsp;</span><b>Gender independent comorbidity analysis</b>:&nbsp;the results of this comorbidity analysis include " + this.comorbidityPairs_ALL.size() + " diagnosis pairs.<br/>"
								+ "<b>This number is greater than "
								+ this.maxValueOfPairsNotToApplyFilter + " diagnosis pairs, the maximum allowed number possible to visualize in a Web-browser with acceptable performances.</b><br/>" + 
								"As a consequence, <span style='color:red;'><b>in this Web page you can interactively browse and explore the " + this.maxValueOfPairsNotToApplyFilter + " more relevant diagnosis pairs</b></span>, including:");
						filterResultMessageHTML.append("<ul>");
						filterResultMessageHTML.append("<li>the " + (sortedRR.size() - (indexRR + 1)) + " pairs with greater relative risk</li>");
						filterResultMessageHTML.append("<li>the " + (sortedOR.size() - (indexOR + 1)) + " pairs with greater odds ratio</li>");
						filterResultMessageHTML.append("<li>the " + (sortedPhi.size() - (indexPhi + 1)) + " pairs with greater phi index</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFish.size() - (indexFisher + 1)) + " pairs with lower Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFishAdj.size() - (indexFisherAdj + 1)) + " pairs with lower adjusted Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedCscore.size() - (indexCscore + 1)) + " pairs with greater comorbidity score</li>");
						filterResultMessageHTML.append("</ul>");
						filterResultMessageHTML.append("<span style='color:red;'><b>To get a table with the comorbidity analysis or all " + this.comorbidityPairs_ALL.size() + " diagnosis pairs considered, please download the related CSV file from the link below.</b></span></div>");
					}
					
					System.out.println("Reduced num pairs ALL from " + this.comorbidityPairs_ALL.size() + " to " + comorPair_ALL.size());
				}
			}
			else {
				comorPair_ALL = this.comorbidityPairs_ALL;
			}
			
			if(this.comorbidityPairs_FEMALE != null && this.comorbidityPairs_FEMALE.size() > this.maxValueOfPairsNotToApplyFilter) {
				
				// Filter ALL
				if(this.comorbidityPairs_FEMALE != null && this.comorbidityPairs_FEMALE.size() > 0) {
					
					List<ComorbidityPairResult> sortedRR = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getRelativeRiskIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedPhi = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getPhiIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedOR = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getOddsRatioIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFish = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getFisherTest(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFishAdj = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getFisherTestAdjusted(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedCscore = this.comorbidityPairs_FEMALE.stream().sorted( Comparator.comparing(cp -> cp.getScore()) ).collect(Collectors.toList());
					
					comorPair_FEMALE = new HashSet<ComorbidityPairResult>();
					
					boolean reachedMax = false;
					int indexRR = sortedRR.size() - 1;
					int indexPhi = sortedPhi.size() - 1;
					int indexOR = sortedOR.size() - 1;
					int indexFisher = sortedFish.size() - 1;
					int indexFisherAdj = sortedFishAdj.size() - 1;
					int indexCscore = sortedCscore.size() - 1;
					while(!reachedMax) {
						
						// RRisk > 1
						if(indexRR >= 0) {
							comorPair_FEMALE.add(sortedRR.get(indexRR--));
						}
						
						// OR > 0
						if(indexOR >= 0) {
							comorPair_FEMALE.add(sortedOR.get(indexOR--));
						}
						
						// Phi > 0
						if(indexPhi >= 0) {
							comorPair_FEMALE.add(sortedPhi.get(indexPhi--));
						}
						
						// Fisher > 0
						if(indexFisher >= 0) {
							comorPair_FEMALE.add(sortedFish.get(indexFisher--));
						}
						
						if(indexFisherAdj >= 0) {
							comorPair_FEMALE.add(sortedFishAdj.get(indexFisherAdj--));
						}
						
						// CScore > 0
						if(indexCscore >= 0) {
							comorPair_FEMALE.add(sortedCscore.get(indexCscore--));
						}
						
						
						if(comorPair_FEMALE.size() > this.maxValueOfPairsNotToApplyFilter) {
							reachedMax = true;
						}
					}
					
					if(comorPair_FEMALE.size() < this.comorbidityPairs_FEMALE.size()) {
						// Filter applied
						filterResultMessageHTML.append("<div style='margin-left:25px;margin-bottom:10px;'><span style='color:red;'>IMPORTANT&nbsp;&gt;&nbsp;</span><b>Comorbidity analysis of females</b>:&nbsp;the results of this comorbidity analysis include " + this.comorbidityPairs_FEMALE.size() + " diagnosis pairs.<br/>"
								+ "<b>This number is greater than "
							+ this.maxValueOfPairsNotToApplyFilter + " diagnosis pairs, the maximum allowed number possible to visualize in a Web-browser with acceptable performances.</b><br/>" + 
							"As a consequence, <span style='color:red;'><b>in this Web page you can interactively browse and explore the " + this.maxValueOfPairsNotToApplyFilter + " more relevant diagnosis pairs</b></span>, including:");
						filterResultMessageHTML.append("<ul>");
						filterResultMessageHTML.append("<li>the " + (sortedRR.size() - (indexRR + 1)) + " pairs with greater relative risk</li>");
						filterResultMessageHTML.append("<li>the " + (sortedOR.size() - (indexOR + 1)) + " pairs with greater odds ratio</li>");
						filterResultMessageHTML.append("<li>the " + (sortedPhi.size() - (indexPhi + 1)) + " pairs with greater phi index</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFish.size() - (indexFisher + 1)) + " pairs with lower Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFishAdj.size() - (indexFisherAdj + 1)) + " pairs with lower adjusted Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedCscore.size() - (indexCscore + 1)) + " pairs with greater comorbidity score</li>");
						filterResultMessageHTML.append("</ul>");
						filterResultMessageHTML.append("<span style='color:red;'><b>To get a table with the comorbidity analysis or all " + this.comorbidityPairs_FEMALE.size() + " diagnosis pairs considered, please download the related CSV file from the link below.</b></span></div>");
					}
					
					System.out.println("Reduced num pairs FEMALE from " + this.comorbidityPairs_FEMALE.size() + " to " + comorPair_ALL.size());
				}
			}
			else {
				comorPair_FEMALE = this.comorbidityPairs_FEMALE;
			}
			
			if(this.comorbidityPairs_MALE != null && this.comorbidityPairs_MALE.size() > this.maxValueOfPairsNotToApplyFilter) {
				
				// Filter ALL
				if(this.comorbidityPairs_MALE != null && this.comorbidityPairs_MALE.size() > 0) {
					
					List<ComorbidityPairResult> sortedRR = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getRelativeRiskIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedPhi = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getPhiIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedOR = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getOddsRatioIndex()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFish = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getFisherTest(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedFishAdj = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getFisherTestAdjusted(), Comparator.reverseOrder()) ).collect(Collectors.toList());
					List<ComorbidityPairResult> sortedCscore = this.comorbidityPairs_MALE.stream().sorted( Comparator.comparing(cp -> cp.getScore()) ).collect(Collectors.toList());
					
					comorPair_MALE = new HashSet<ComorbidityPairResult>();
					
					boolean reachedMax = false;
					int indexRR = sortedRR.size() - 1;
					int indexPhi = sortedPhi.size() - 1;
					int indexOR = sortedOR.size() - 1;
					int indexFisher = sortedFish.size() - 1;
					int indexFisherAdj = sortedFishAdj.size() - 1;
					int indexCscore = sortedCscore.size() - 1;
					while(!reachedMax) {
						
						// RRisk > 1
						if(indexRR >= 0) {
							comorPair_MALE.add(sortedRR.get(indexRR--));
						}
						
						// OR > 0
						if(indexOR >= 0) {
							comorPair_MALE.add(sortedOR.get(indexOR--));
						}
						
						// Phi > 0
						if(indexPhi >= 0) {
							comorPair_MALE.add(sortedPhi.get(indexPhi--));
						}
						
						// Fisher > 0
						if(indexFisher >= 0) {
							comorPair_MALE.add(sortedFish.get(indexFisher--));
						}
						
						if(indexFisherAdj >= 0) {
							comorPair_MALE.add(sortedFishAdj.get(indexFisherAdj--));
						}
						
						// CScore > 0
						if(indexCscore >= 0) {
							comorPair_MALE.add(sortedCscore.get(indexCscore--));
						}
						
						
						if(comorPair_MALE.size() > this.maxValueOfPairsNotToApplyFilter) {
							reachedMax = true;
						}
					}
					
					if(comorPair_MALE.size() < this.comorbidityPairs_MALE.size()) {
						// Filter applied
						filterResultMessageHTML.append("<div style='margin-left:25px;margin-bottom:10px;'><span style='color:red;'>IMPORTANT&nbsp;&gt;&nbsp;</span><b>Comorbidity analysis of males</b>:&nbsp;the results of this comorbidity analysis include " + this.comorbidityPairs_MALE.size() + " diagnosis pairs.<br/>"
								+ "This number is greater than "
								+ this.maxValueOfPairsNotToApplyFilter + " diagnosis pairs, the maximum allowed number possible to visualize in a Web-browser with acceptable performances.</b><br/>" + 
								"As a consequence, <span style='color:red;'><b>in this Web page you can interactively browse and explore the " + this.maxValueOfPairsNotToApplyFilter + " more relevant diagnosis pairs</b></span>, including:");
						filterResultMessageHTML.append("<ul>");
						filterResultMessageHTML.append("<li>the " + (sortedRR.size() - (indexRR + 1)) + " pairs with greater relative risk</li>");
						filterResultMessageHTML.append("<li>the " + (sortedOR.size() - (indexOR + 1)) + " pairs with greater odds ratio</li>");
						filterResultMessageHTML.append("<li>the " + (sortedPhi.size() - (indexPhi + 1)) + " pairs with greater phi index</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFish.size() - (indexFisher + 1)) + " pairs with lower Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedFishAdj.size() - (indexFisherAdj + 1)) + " pairs with lower adjusted Fisher score / p-value</li>");
						filterResultMessageHTML.append("<li>the " + (sortedCscore.size() - (indexCscore + 1)) + " pairs with greater comorbidity score</li>");
						filterResultMessageHTML.append("<span style='color:red;'><b>To get a table with the comorbidity analysis or all " + this.comorbidityPairs_MALE.size() + " diagnosis pairs considered, please download the related CSV file from the link below.</b></span></div>");
					}
					
					System.out.println("Reduced num pairs MALE from " + this.comorbidityPairs_MALE.size() + " to " + comorPair_ALL.size());
				}
			}
			else {
				comorPair_MALE = this.comorbidityPairs_MALE;
			}
			
		}
		else {
			comorPair_ALL = this.comorbidityPairs_ALL;
			comorPair_FEMALE = this.comorbidityPairs_FEMALE;
			comorPair_MALE = this.comorbidityPairs_MALE;
		}
		
		
		
		return TemplateUtils.generateHTMLanalysisResTemplate(this.currentExecutor, comorPair_ALL,
				comorPair_FEMALE, comorPair_MALE,
				this.logString, this.errorMsg, this.fileStorage, this.execID, this.storageFullName, filterResultMessageHTML.toString());
	}


}
