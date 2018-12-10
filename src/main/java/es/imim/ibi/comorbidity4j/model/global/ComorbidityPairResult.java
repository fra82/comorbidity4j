package es.imim.ibi.comorbidity4j.model.global;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.google.common.base.Strings;

/**
 * Result of the analysis of comorbidity of a pair of diseases
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityPairResult {
	
	private static DecimalFormat decimFormatFiveDec = new DecimalFormat("#######0.00000");
	private static DecimalFormat decimFormatTenDec = new DecimalFormat("#######0.0000000000");
	
	static {
		decimFormatFiveDec.setRoundingMode(RoundingMode.HALF_DOWN);
		decimFormatTenDec.setRoundingMode(RoundingMode.HALF_DOWN);
	}
	
	private Integer disAcodeNum;
	private String disAcode;
	private String disAname;
	private Integer disBcodeNum;
	private String disBcode;
	private String disBname;

	private Integer patTotal;
	private Integer patWdisA;
	private Integer patWdisB;
	private Integer patWdisAB;
	private Integer patWdisAnotB;
	private Integer patWdisBnotA;
	private Integer patWOdisAB;

	private Double relativeRiskIndex;
	private Double relativeRiskCIlower;
	private Double relativeRiskCIupper;
	private Double phiIndex;
	private Double oddsRatioIndex;
	private Double oddsRatioCIlower;
	private Double oddsRatioCIupper;
	private Double fisherTest;
	private Double fisherTestAdjusted;

	private Double expect;
	private Double score;
	
	
	private Integer femaleWithDisA;
	private Integer femaleWithDisB;
	private Integer femaleWithDisAandB;
	private Integer maleWithDisA;
	private Integer maleWithDisB;
	private Integer maleWithDisAandB;
	private Double sexRatioBA;
	private Double sexRatioAB;
	
	
	// Constructors
	public ComorbidityPairResult() {
		super();
	}

	public ComorbidityPairResult(Integer disAcodeNum, Integer disBcodeNum) {
		super();
		this.disAcodeNum = disAcodeNum;
		this.disBcodeNum = disBcodeNum;
	}

	// Getters and setters
	public String getDisAname() {
		return disAname;
	}

	public void setDisAname(String disAname) {
		this.disAname = disAname;
	}

	public String getDisBname() {
		return disBname;
	}

	public void setDisBname(String disBname) {
		this.disBname = disBname;
	}

	public Integer getPatTotal() {
		return patTotal;
	}

	public void setPatTotal(Integer patTotal) {
		this.patTotal = patTotal;
	}

	public Integer getDisAcodeNum() {
		return disAcodeNum;
	}

	public void setDisAcodeNum(Integer disAcodeNum) {
		this.disAcodeNum = disAcodeNum;
	}

	public Integer getDisBcodeNum() {
		return disBcodeNum;
	}

	public void setDisBcodeNum(Integer disBcodeNum) {
		this.disBcodeNum = disBcodeNum;
	}

	public String getDisAcode() {
		return disAcode;
	}

	public void setDisAcode(String disAcode) {
		this.disAcode = disAcode;
	}

	public String getDisBcode() {
		return disBcode;
	}

	public void setDisBcode(String disBcode) {
		this.disBcode = disBcode;
	}

	public Integer getPatWdisA() {
		return patWdisA;
	}

	public void setPatWdisA(Integer patWdisA) {
		this.patWdisA = patWdisA;
	}

	public Integer getPatWdisB() {
		return patWdisB;
	}

	public void setPatWdisB(Integer patWdisB) {
		this.patWdisB = patWdisB;
	}

	public Integer getPatWdisAB() {
		return patWdisAB;
	}

	public void setPatWdisAB(Integer patWdisAB) {
		this.patWdisAB = patWdisAB;
	}

	public Integer getPatWdisAnotB() {
		return patWdisAnotB;
	}

	public void setPatWdisAnotB(Integer patWdisAnotB) {
		this.patWdisAnotB = patWdisAnotB;
	}

	public Integer getPatWdisBnotA() {
		return patWdisBnotA;
	}

	public void setPatWdisBnotA(Integer patWdisBnotA) {
		this.patWdisBnotA = patWdisBnotA;
	}

	public Integer getPatWOdisAB() {
		return patWOdisAB;
	}

	public void setPatWOdisAB(Integer patWOdisAB) {
		this.patWOdisAB = patWOdisAB;
	}

	public Double getRelativeRiskIndex() {
		return relativeRiskIndex;
	}

	public void setRelativeRiskIndex(Double relativeRiskIndex) {
		this.relativeRiskIndex = relativeRiskIndex;
	}

	public Double getRelativeRiskCIupper() {
		return relativeRiskCIupper;
	}

	public void setRelativeRiskCIupper(Double relativeRiskCIupper) {
		this.relativeRiskCIupper = relativeRiskCIupper;
	}

	public Double getRelativeRiskCIlower() {
		return relativeRiskCIlower;
	}

	public void setRelativeRiskCIlower(Double relativeRiskCIlower) {
		this.relativeRiskCIlower = relativeRiskCIlower;
	}

	public Double getPhiIndex() {
		return phiIndex;
	}

	public void setPhiIndex(Double phiIndex) {
		this.phiIndex = phiIndex;
	}

	public Double getOddsRatioIndex() {
		return oddsRatioIndex;
	}

	public void setOddsRatioIndex(Double oddsRatioIndex) {
		this.oddsRatioIndex = oddsRatioIndex;
	}
	
	public Double getOddsRatioCIupper() {
		return oddsRatioCIupper;
	}

	public void setOddsRatioCIupper(Double oddsRatioCIupper) {
		this.oddsRatioCIupper = oddsRatioCIupper;
	}

	public Double getOddsRatioCIlower() {
		return oddsRatioCIlower;
	}

	public void setOddsRatioCIlower(Double oddsRatioCIlower) {
		this.oddsRatioCIlower = oddsRatioCIlower;
	}

	public Double getFisherTest() {
		return fisherTest;
	}

	public void setFisherTest(Double fisherTest) {
		this.fisherTest = fisherTest;
	}

	public Double getFisherTestAdjusted() {
		return fisherTestAdjusted;
	}

	public void setFisherTestAdjusted(Double fisherTestAdjusted) {
		this.fisherTestAdjusted = fisherTestAdjusted;
	}

	public Double getExpect() {
		return expect;
	}

	public void setExpect(Double expect) {
		this.expect = expect;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
	public Integer getFemaleWithDisA() {
		return femaleWithDisA;
	}

	public void setFemaleWithDisA(Integer femaleWithDisA) {
		this.femaleWithDisA = femaleWithDisA;
	}

	public Integer getFemaleWithDisB() {
		return femaleWithDisB;
	}
	
	public void setFemaleWithDisB(Integer femaleWithDisB) {
		this.femaleWithDisB = femaleWithDisB;
	}

	public Integer getFemaleWithDisAandB() {
		return femaleWithDisAandB;
	}

	public void setFemaleWithDisAandB(Integer femaleWithDisAandB) {
		this.femaleWithDisAandB = femaleWithDisAandB;
	}

	public Integer getMaleWithDisA() {
		return maleWithDisA;
	}

	public void setMaleWithDisA(Integer maleWithDisA) {
		this.maleWithDisA = maleWithDisA;
	}
	
	public Integer getMaleWithDisB() {
		return maleWithDisB;
	}

	public void setMaleWithDisB(Integer maleWithDisB) {
		this.maleWithDisB = maleWithDisB;
	}

	public Integer getMaleWithDisAandB() {
		return maleWithDisAandB;
	}

	public void setMaleWithDisAandB(Integer maleWithDisAandB) {
		this.maleWithDisAandB = maleWithDisAandB;
	}

	public Double getSexRatioBA() {
		return sexRatioBA;
	}

	public void setSexRatioBA(Double sexRatioBA) {
		this.sexRatioBA = sexRatioBA;
	}

	public Double getSexRatioAB() {
		return sexRatioAB;
	}

	public void setSexRatioAB(Double sexRatioAB) {
		this.sexRatioAB = sexRatioAB;
	}
	

	@Override
	public String toString() {
		
		return "ComorbidityPairResult [disAcodeNum=" + ((disAcodeNum != null) ? disAcodeNum : "null") +
				", disAcode=" + ((disAcode != null) ? disAcode : "null") +
				", disAname=" + ((disAname != null) ? disAname : "null") +
				", disBcodeNum=" + ((disBcodeNum != null) ? disBcodeNum : "null") +
				", disBcode=" + ((disBcode != null) ? disBcode : "null") +
				", disBname=" + ((disBname != null) ? disBname : "null") +
				", patTotal=" + ((patTotal != null) ? patTotal : "null") +
				", patWdisA=" + ((patWdisA != null) ? patWdisA : "null") + 
				", patWdisB=" + ((patWdisB != null) ? patWdisB : "null") + 
				", patWdisAB=" + ((patWdisAB != null) ? patWdisAB : "null") + 
				", patWdisAnotB=" + ((patWdisAnotB != null) ? patWdisAnotB : "null") + 
				", patWdisBnotA=" + ((patWdisBnotA != null) ? patWdisBnotA : "null") + 
				", patWOdisAB=" + ((patWOdisAB != null) ? patWOdisAB : "null") + 
				", relativeRiskIndex=" + ((relativeRiskIndex != null) ? relativeRiskIndex : "null") + 
				", relativeRiskCIlower=" + ((relativeRiskCIlower != null) ? relativeRiskCIlower : "null") + 
				", relativeRiskCIupper=" + ((relativeRiskCIupper != null) ? relativeRiskCIupper : "null") + 
				", phiIndex=" + ((phiIndex != null) ? phiIndex : "null") + 
				", oddsRatioIndex=" + ((oddsRatioIndex != null) ? oddsRatioIndex : "null") + 
				", oddsRatioCIlower=" + ((oddsRatioCIlower != null) ? oddsRatioCIlower : "null") + 
				", oddsRatioCIupper=" + ((oddsRatioCIupper != null) ? oddsRatioCIupper : "null") + 
				", fisherTest=" + ((fisherTest != null) ? fisherTest : "null") + 
				", fisherTestAdjusted=" + ((fisherTestAdjusted != null) ? fisherTestAdjusted : "null") +
				", expect=" + ((expect != null) ? expect : "null") +
				", score=" + ((score != null) ? score : "null") +
				", femaleWithDisA=" + ((femaleWithDisA != null) ? femaleWithDisA : "null") +
				", femaleWithDisB=" + ((femaleWithDisB != null) ? femaleWithDisB : "null") +
				", femaleWithDisAandB=" + ((femaleWithDisAandB != null) ? femaleWithDisAandB : "null") +
				", maleWithDisA=" + ((maleWithDisA != null) ? maleWithDisA : "null") +
				", maleWithDisB=" + ((maleWithDisB != null) ? maleWithDisB : "null") +
				", maleWithDisAandB=" + ((maleWithDisAandB != null) ? maleWithDisAandB : "null") +
				", sexRatioBA=" + ((sexRatioBA != null) ? sexRatioBA : "null") +
				", sexRatioAB=" + ((sexRatioAB != null) ? sexRatioAB : "null") +"]";
	}

	public static String toCSVlineHeader(String relativeRiskCI, String oddsRationCI) {
		return "disAcodeNum\t" + 
				"disAcode\t" + 
				"disAname\t" +
				"disBcodeNum\t" +
				"disBcode\t" +
				"disBname\t" +
				"patTotal\t" +
				"patWdisA\t" +
				"patWdisB\t" +
				"patWdisAB\t" +
				"patWdisAnotB\t" +
				"patWdisBnotA\t" +
				"patWOdisAB\t" +
				"relativeRiskIndex\t" +
				"relativeRiskCI_" + ((!Strings.isNullOrEmpty(relativeRiskCI)) ? relativeRiskCI + "_": "N_") + "lower\t" +
				"relativeRiskCI_" + ((!Strings.isNullOrEmpty(relativeRiskCI)) ? relativeRiskCI + "_": "N_") + "upper\t" +
				"phiIndex\t" +
				"oddsRatioIndex\t" +
				"oddsRatioCI_" + ((!Strings.isNullOrEmpty(oddsRationCI)) ? oddsRationCI + "_": "N_") + "lower\t" +
				"oddsRatioCI_" + ((!Strings.isNullOrEmpty(oddsRationCI)) ? oddsRationCI + "_": "N_") + "upper\t" +
				"fisherTest\t" +
				"fisherTestAdjusted\t" +
				"expect\t" +
				"score\t" +
				"femaleWithDisA\t" +
				"femaleWithDisB\t" +
				"femaleWithDisAandB\t" +
				"maleWithDisA\t" +
				"maleWithDisB\t" +
				"maleWithDisAandB\t" +
				"sexRatioBA\t" + 
				"sexRatioAB";
	}

	public String toCSVline() {
		return ((disAcodeNum != null) ? disAcodeNum : "null") + "\t" +
				((disAcode != null) ? disAcode.replace("\t", " ").trim() : "null") + "\t" +
				((disAname != null) ? disAname.replace("\t", " ").trim() : "null") + "\t" +
				((disBcodeNum != null) ? disBcodeNum : "null") + "\t" +
				((disBcode != null) ? disBcode.replace("\t", " ").trim() : "null") + "\t" +
				((disBname != null) ? disBname.replace("\t", " ").trim() : "null") + "\t" +
				((patTotal != null) ? patTotal : "null") + "\t" +
				((patWdisA != null) ? patWdisA : "null") + "\t" + 
				((patWdisB != null) ? patWdisB : "null") + "\t" + 
				((patWdisAB != null) ? patWdisAB : "null") + "\t" + 
				((patWdisAnotB != null) ? patWdisAnotB : "null") + "\t" + 
				((patWdisBnotA != null) ? patWdisBnotA : "null") + "\t" + 
				((patWOdisAB != null) ? patWOdisAB : "null") + "\t" + 
				((relativeRiskIndex != null) ? decimFormatFiveDec.format(relativeRiskIndex) : "null") + "\t" + 
				((relativeRiskCIlower != null) ? decimFormatFiveDec.format(relativeRiskCIlower) : "null") + "\t" + 
				((relativeRiskCIupper != null) ? decimFormatFiveDec.format(relativeRiskCIupper) : "null") + "\t" + 
				((phiIndex != null) ? decimFormatFiveDec.format(phiIndex) : "null") + "\t" +
				((oddsRatioIndex != null) ? decimFormatFiveDec.format(oddsRatioIndex) : "null") + "\t" + 
				((oddsRatioCIlower != null) ? decimFormatFiveDec.format(oddsRatioCIlower) : "null") + "\t" + 
				((oddsRatioCIupper != null) ? decimFormatFiveDec.format(oddsRatioCIupper) : "null") + "\t" + 
				((fisherTest != null) ? decimFormatTenDec.format(fisherTest) : "null") + "\t" + 
				((fisherTestAdjusted != null) ? decimFormatTenDec.format(fisherTestAdjusted) : "null") + "\t" +
				((expect != null) ? decimFormatFiveDec.format(expect) : "null") + "\t" +
				((score != null) ? decimFormatFiveDec.format(score) : "null") + "\t" + 
				((femaleWithDisA != null) ? femaleWithDisA : "null") + "\t" + 
				((femaleWithDisB != null) ? femaleWithDisB : "null") + "\t" + 
				((femaleWithDisAandB != null) ? femaleWithDisAandB : "null") + "\t" + 
				((maleWithDisA != null) ? maleWithDisA : "null") + "\t" + 
				((maleWithDisB != null) ? maleWithDisB : "null") + "\t" + 
				((maleWithDisAandB != null) ? maleWithDisAandB : "null") + "\t" + 
				((sexRatioBA != null) ? decimFormatFiveDec.format(sexRatioBA) : "null") + "\t" + 
				((sexRatioAB != null) ? decimFormatFiveDec.format(sexRatioAB) : "null");
	}
	
	public ComorbidityPairResult parseCSVline(String CSVline) {
		
		ComorbidityPairResult retPair = new ComorbidityPairResult();
		
		if(!Strings.isNullOrEmpty(CSVline)) {
			String[] CSVlineSplit = CSVline.split("\t");
			if(CSVlineSplit != null && CSVlineSplit.length == 20) {
				try {
					retPair.setDisAcodeNum(Integer.parseInt(CSVlineSplit[0]));
					retPair.setDisAcode(CSVlineSplit[1]);
					retPair.setDisAname(CSVlineSplit[2]);
					
					retPair.setDisBcodeNum(Integer.parseInt(CSVlineSplit[3]));
					retPair.setDisBcode(CSVlineSplit[4]);
					retPair.setDisBname(CSVlineSplit[5]);
					
					retPair.setPatTotal(Integer.parseInt(CSVlineSplit[6]));
					retPair.setPatWdisA(Integer.parseInt(CSVlineSplit[7]));
					retPair.setPatWdisB(Integer.parseInt(CSVlineSplit[8]));
					retPair.setPatWdisAB(Integer.parseInt(CSVlineSplit[9]));
					retPair.setPatWdisAnotB(Integer.parseInt(CSVlineSplit[10]));
					retPair.setPatWdisBnotA(Integer.parseInt(CSVlineSplit[11]));
					retPair.setPatWOdisAB(Integer.parseInt(CSVlineSplit[12]));
					
					retPair.setRelativeRiskIndex(Double.valueOf(CSVlineSplit[13]));
					retPair.setRelativeRiskCIlower(Double.valueOf(CSVlineSplit[14]));
					retPair.setRelativeRiskCIupper(Double.valueOf(CSVlineSplit[15]));
					retPair.setPhiIndex(Double.valueOf(CSVlineSplit[16]));
					retPair.setOddsRatioIndex(Double.valueOf(CSVlineSplit[17]));
					retPair.setOddsRatioCIlower(Double.valueOf(CSVlineSplit[18]));
					retPair.setOddsRatioCIupper(Double.valueOf(CSVlineSplit[19]));
					retPair.setFisherTest(Double.valueOf(CSVlineSplit[20]));
					retPair.setFisherTestAdjusted(Double.valueOf(CSVlineSplit[21]));
					retPair.setExpect(Double.valueOf(CSVlineSplit[22]));
					retPair.setScore(Double.valueOf(CSVlineSplit[23]));
					
					retPair.setFemaleWithDisA(Integer.valueOf(CSVlineSplit[24]));
					retPair.setFemaleWithDisB(Integer.valueOf(CSVlineSplit[25]));
					retPair.setFemaleWithDisAandB(Integer.valueOf(CSVlineSplit[26]));
					retPair.setMaleWithDisA(Integer.valueOf(CSVlineSplit[27]));
					retPair.setMaleWithDisB(Integer.valueOf(CSVlineSplit[28]));
					retPair.setMaleWithDisAandB(Integer.valueOf(CSVlineSplit[29]));					
					retPair.setSexRatioBA(Double.valueOf(CSVlineSplit[30]));
					retPair.setSexRatioAB(Double.valueOf(CSVlineSplit[31]));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			retPair = null;
		}
		
		return retPair;
	}

	public String toJSONline(boolean reversePair) {
		String dAcodeNum = ((disAcodeNum != null) ? disAcodeNum + "" : "null");
		String dAcode = ((disAcode != null) ? disAcode.replace("\t", " ").replace("'", "\"").trim() : "null");
		String dAcodeName = ((disAname != null) ? disAname.replace("\t", " ").replace("'", "\"").trim() : "null");
		String dBcodeNum = ((disBcodeNum != null) ? disBcodeNum + "" : "null");
		String dBcode = ((disBcode != null) ? disBcode.replace("\t", " ").replace("'", "\"").trim() : "null");
		String dBcodeName = ((disBname != null) ? disBname.replace("\t", " ").replace("'", "\"").trim() : "null");
		
		// Numeric codes with string
		try {
			Long dAcodeLong = Long.valueOf(dAcode.trim());
			dAcode = "D_" + dAcodeLong;
		}
		catch(NumberFormatException nfe) {
			/* Do nothing*/
		}
		
		try {
			Long dBcodeLong = Long.valueOf(dBcode.trim());
			dBcode = "D_" + dBcodeLong;
		}
		catch(NumberFormatException nfe) {
			/* Do nothing*/
		}
		
		if(reversePair) {
			String disAappoNum = dAcodeNum;
			String dAappoCode = dAcode;
			String dAappoName = dAcodeName;
			dAcodeNum = dBcodeNum;
			dAcode = dBcode;
			dAcodeName = dBcodeName;
			dBcodeNum = disAappoNum;
			dBcode = dAappoCode;
			dBcodeName = dAappoName;
		}
		
		String relativeRiskCI_upper = "";
		if(relativeRiskCIupper != null && Double.isInfinite(relativeRiskCIupper)){
			relativeRiskCI_upper = decimFormatFiveDec.format(Double.MAX_VALUE - 1d);
		}
		else {
			relativeRiskCI_upper = ((relativeRiskCIupper != null) ? decimFormatFiveDec.format(relativeRiskCIupper) : "null");
		}


		String relativeRiskCI_lower = "";
		if(relativeRiskCIlower != null && Double.isInfinite(relativeRiskCIlower)){
			relativeRiskCI_lower = decimFormatFiveDec.format(Double.MAX_VALUE - 1d);
		}
		else {
			relativeRiskCI_lower = ((relativeRiskCIlower != null) ? decimFormatFiveDec.format(relativeRiskCIlower) : "null");
		}
		
		String oddsRatioCI_upper = "";
		if(oddsRatioCIupper != null && Double.isInfinite(oddsRatioCIupper)){
			oddsRatioCI_upper = decimFormatFiveDec.format(Double.MAX_VALUE - 1d);
		}
		else {
			oddsRatioCI_upper = ((oddsRatioCIupper != null) ? decimFormatFiveDec.format(oddsRatioCIupper) : "null");
		}
		
		String oddsRatioCI_lower = "";
		if(oddsRatioCIlower != null && Double.isInfinite(oddsRatioCIlower)){
			oddsRatioCI_lower = decimFormatFiveDec.format(Double.MAX_VALUE - 1d);
		}
		else {
			oddsRatioCI_lower = ((oddsRatioCIlower != null) ? decimFormatFiveDec.format(oddsRatioCIlower) : "null");
		}

		return "{" +
				"'disAcodeNum': " + dAcodeNum + "," +
				"'disAcode': '" + dAcode + "'," +
				"'disAname': '" + dAcodeName + "'," +
				"'disBcodeNum': " + dBcodeNum + "," +
				"'disBcode': '" + dBcode + "'," +
				"'disBname': '" + dBcodeName + "'," +
				"'patTotal': " + ((patTotal != null) ? patTotal : "null") + "," +
				"'patWdisA': " + ((patWdisA != null) ? patWdisA : "null") + "," +
				"'patWdisB': " + ((patWdisB != null) ? patWdisB : "null") + "," +
				"'patWdisAB': " + ((patWdisAB != null) ? patWdisAB : "null") + "," +
				"'patWdisAnotB': " + ((patWdisAnotB != null) ? patWdisAnotB : "null") + "," +
				"'patWdisBnotA': " + ((patWdisBnotA != null) ? patWdisBnotA : "null") + "," +
				"'patWOdisAB': " + ((patWOdisAB != null) ? patWOdisAB : "null") + "," +
				"'relativeRiskIndex': " + ((relativeRiskIndex != null) ? decimFormatFiveDec.format(relativeRiskIndex) : "null") + "," +
				"'relativeRiskCI_lower': " + relativeRiskCI_lower + "," +
				"'relativeRiskCI_upper': " + relativeRiskCI_upper + "," +
				"'phiIndex': " + ((phiIndex != null) ? decimFormatFiveDec.format(phiIndex) : "null") + "," +
				"'oddsRatioIndex': " + ((oddsRatioIndex != null) ? decimFormatFiveDec.format(oddsRatioIndex) : "null") + "," +
				"'oddsRatioCI_lower': " + oddsRatioCI_lower + "," +
				"'oddsRatioCI_upper': " + oddsRatioCI_upper + "," +
				"'fisherTest': " + ((fisherTest != null) ? decimFormatTenDec.format(fisherTest) : "null") + "," +
				"'fisherTestAdjusted': " + ((fisherTestAdjusted != null) ? decimFormatTenDec.format(fisherTestAdjusted) : "null") + "," +
				"'expect': " + ((expect != null) ? decimFormatFiveDec.format(expect) : "null") + "," +
				"'score': " + ((score != null) ? decimFormatFiveDec.format(score) : "null") + "," +
				"'femaleWithDisA': " + ((femaleWithDisA != null) ? femaleWithDisA : "null") + "," +
				"'femaleWithDisB': " + ((femaleWithDisB != null) ? femaleWithDisB : "null") + "," +
				"'femaleWithDisAandB': " + ((femaleWithDisAandB != null) ? femaleWithDisAandB : "null") + "," + 
				"'maleWithDisA': " + ((maleWithDisA != null) ? maleWithDisA : "null") + "," + 
				"'maleWithDisB': " + ((maleWithDisB != null) ? maleWithDisB : "null") + "," + 
				"'maleWithDisAandB': " + ((maleWithDisAandB != null) ? maleWithDisAandB : "null") + "," +
				"'sexRatioBA': " + ((sexRatioBA != null) ? decimFormatFiveDec.format(sexRatioBA) : "null") + "," +
				"'sexRatioAB': " + ((sexRatioAB != null) ? decimFormatFiveDec.format(sexRatioAB) : "null") +
				"}";
	}




}
