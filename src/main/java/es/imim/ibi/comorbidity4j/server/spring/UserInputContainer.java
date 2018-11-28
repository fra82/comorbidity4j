package es.imim.ibi.comorbidity4j.server.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityDirectionalityFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityPatientFilter;
import es.imim.ibi.comorbidity4j.analysis.filter.ComorbidityScoreFilter;
import es.imim.ibi.comorbidity4j.loader.DataLoadContainer;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.util.stat.AdjMethodENUM;

public class UserInputContainer {

	private static Random rnd = new Random();

	private int sessionRnd = -1;

	// PatientDataValidation
	private String patientDataFileName_PD = "";
	private double patientDataFileSize_PD = -1l;
	private String patientData_PD = "";
	private char columnSeparatorChar_PD = '\t';
	private char columnTextDelimiterChar_PD = 'N';
	private boolean hasFirstRowHeader_PD = true;
	private String patientIDcolumn_PD = "";
	private String patientBirthDateColumn_PD = "";
	private String patientGenderColumn_PD = "";
	private String patientFacet1column_PD = "";
	private String dateFormat_PD = "";
	private boolean isOMOP_PD = false;
	private boolean isGenderEnabled = true;
	private DataLoadContainer<List<Patient>> patientData_LOADED = null;

	public void resetPatientData() {
		patientDataFileName_PD = "";
		patientDataFileSize_PD = -1l;
		patientData_PD = "";
		columnSeparatorChar_PD = '\t';
		columnTextDelimiterChar_PD = 'N';
		hasFirstRowHeader_PD = true;
		patientIDcolumn_PD = "";
		patientBirthDateColumn_PD = "";
		patientGenderColumn_PD = "";
		patientFacet1column_PD = "";
		dateFormat_PD = "";
		isOMOP_PD = false;
		isGenderEnabled = true;
		patientData_LOADED = null;
	}


	// VisitDataValidation
	private String visitDataFileName_VD = "";
	private double visitDataFileSize_VD = -1l;
	private String visitData_VD = "";
	private char columnSeparatorChar_VD = '\t';
	private char columnTextDelimiterChar_VD = 'N';
	private boolean hasFirstRowHeader_VD = true;
	private String patientIDcolumn_VD = "";
	private String visitIDcolumn_VD = "";
	private String visitStartDateColumn_VD = "";
	private String dateFormat_VD = "";
	private boolean isOMOP_VD = false;
	private DataLoadContainer<List<Visit>> visitData_LOADED = null;

	public void resetVisitData() {
		visitDataFileName_VD = "";
		visitDataFileSize_VD = -1l;
		visitData_VD = "";
		columnSeparatorChar_VD = '\t';
		columnTextDelimiterChar_VD = 'N';
		hasFirstRowHeader_VD = true;
		patientIDcolumn_VD = "";
		visitIDcolumn_VD = "";
		visitStartDateColumn_VD = "";
		dateFormat_VD = "";
		isOMOP_VD = false;
		visitData_LOADED = null;
	}


	// DiagnosisDataValidation
	private String diagnosisDataFileName_DD = "";
	private double diagnosisDataFileSize_DD = -1l;
	private String diagnosisData_DD = "";
	private char columnSeparatorChar_DD = '\t';
	private char columnTextDelimiterChar_DD = 'N';
	private boolean hasFirstRowHeader_DD = true;
	private String patientIDcolumn_DD = "";
	private String visitIDcolumn_DD = "";
	private String diagnosisCodeColumn_DD = "";
	private boolean isOMOP_DD = false;
	private DataLoadContainer<List<Visit>> diagnosisData_LOADED = null;
	private Map<String, Integer> diagnosisCodeIdStringMap = null;
	
	public void resetDiagnosisData() {
		diagnosisDataFileName_DD = "";
		diagnosisDataFileSize_DD = -1l;
		diagnosisData_DD = "";
		columnSeparatorChar_DD = '\t';
		columnTextDelimiterChar_DD = 'N';
		hasFirstRowHeader_DD = true;
		patientIDcolumn_DD = "";
		visitIDcolumn_DD = "";
		diagnosisCodeColumn_DD = "";
		isOMOP_DD = false;
		diagnosisData_LOADED = null;
		diagnosisCodeIdStringMap = null;
	}
	
	
	// DescrDiagnosisDataValidation
	private String descrDiagnosisDataFileName_DDE = "";
	private double descrDiagnosisDataFileSize_DDE = -1l;
	private String descrDiagnosisData_DDE = "";
	private char columnSeparatorChar_DDE = '\t';
	private char columnTextDelimiterChar_DDE = 'N';
	private boolean hasFirstRowHeader_DDE = true;
	private String diagnosisDescriptionColumn_DDE = "";
	private String diagnosisCodeColumn_DDE = "";
	private DataLoadContainer<Map<String, String>> descrDiagnosisData_LOADED = null;

	public void resetDescrDiagnosisData() {
		descrDiagnosisDataFileName_DDE = "";
		descrDiagnosisDataFileSize_DDE = -1l;
		descrDiagnosisData_DDE = "";
		columnSeparatorChar_DDE = '\t';
		columnTextDelimiterChar_DDE = 'N';
		hasFirstRowHeader_DDE = true;
		diagnosisDescriptionColumn_DDE = "";
		diagnosisCodeColumn_DDE = "";
		descrDiagnosisData_LOADED = null;
	}
	
	
	// Diagnosis code groups
	private Map<String, Set<String>> groupNameListCodesMap = new HashMap<String, Set<String>>();
	public void resetDiagnosisGroupingData() {
		groupNameListCodesMap = new HashMap<String, Set<String>>();
	}
	
	// Diagnosis code pairs
	private Map<String, Set<String>> groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
	private Map<String, String> groupPairignMap = new HashMap<String, String>();
	public void resetDiagnosisPairingData() {
		groupNameListCodesPairingMap = new HashMap<String, Set<String>>();
		groupPairignMap = new HashMap<String, String>();
	}
	
	
	// Analysis parameters
	private String femaleIdentifier_p = null;
	private String maleIdentifier_p = null;
	private String isDirectional_p = null;
	private Integer directMinDays_p = null;
			
	private PatientAgeENUM patientAgeComputation_p = PatientAgeENUM.LAST_DIAGNOSTIC;
	
	private AdjMethodENUM pvalAdjApproach_p = AdjMethodENUM.BENJAMINI_HOCHBERG;
	
	private boolean minAgeEnabled_p = false;
	private Integer FPATminAge_p = null;
	private boolean maxAgeEnabled_p = false;
	private Integer FPATmaxAge_p = null;
	
	private boolean patientFacetFilteringEnabled_p = false;
	private String[] patientFacetsInFilter_p = null;
	
	
	private boolean FCOMscoreEnabled_p = false;
	private String FCOMscoreGreaterLower_p = null;
	private Double FCOMscore_p = null;
	
	private boolean FCOMrriskEnabled_p = false;
	private String FCOMrriskGreaterLower_p = null;
	private Double FCOMrrisk_p = null;
	
	private boolean FCOModdsRatioEnabled_p = false;
	private String FCOModdsRatioGreaterLower_p = null;
	private Double FCOModdsRatio_p = null;
	
	private boolean FCOMphiEnabled_p = false;
	private String FCOMphiGreaterLower_p = null;
	private Double FCOMphi_p = null;
	
	private boolean FCOMfisherAdjEnabled_p = false;
	private String FCOMfisherAdjGreaterLower_p = null;
	private Double FCOMfisherAdj_p = null;
	
	private boolean FCOMminPatEnabled_p = false;
	private String FCOMminPatGreaterLower_p = null;
	private Integer FCOMminPat_p = null;
	
	private Double oddsRatioConfindeceInterval_p = null;
	
	public void resetAnalysisParameters() {
		femaleIdentifier_p = null;
		maleIdentifier_p = null;
		isDirectional_p = null;
		directMinDays_p = null;
				
		patientAgeComputation_p = PatientAgeENUM.LAST_DIAGNOSTIC;
		
		pvalAdjApproach_p = AdjMethodENUM.BENJAMINI_HOCHBERG;
		
		minAgeEnabled_p = false;
		FPATminAge_p = null;
		maxAgeEnabled_p = false;
		FPATmaxAge_p = null;
		
		patientFacetFilteringEnabled_p = false;
		patientFacetsInFilter_p = null;
		
		
		FCOMscoreEnabled_p = false;
		FCOMscoreGreaterLower_p = null;
		FCOMscore_p = null;
		
		FCOMrriskEnabled_p = false;
		FCOMrriskGreaterLower_p = null;
		FCOMrrisk_p = null;
		
		FCOModdsRatioEnabled_p = false;
		FCOModdsRatioGreaterLower_p = null;
		FCOModdsRatio_p = null;
		
		FCOMphiEnabled_p = false;
		FCOMphiGreaterLower_p = null;
		FCOMphi_p = null;
		
		FCOMfisherAdjEnabled_p = false;
		FCOMfisherAdjGreaterLower_p = null;
		FCOMfisherAdj_p = null;
		
		FCOMminPatEnabled_p = false;
		FCOMminPatGreaterLower_p = null;
		FCOMminPat_p = null;
		
		oddsRatioConfindeceInterval_p = null;
	}
	
	
	public UserInputContainer() {
		sessionRnd = rnd.nextInt(100000);
	}

	public int getSessionObj() {
		return sessionRnd;
	}

	public String getPatientDataFileName_PD() {
		return patientDataFileName_PD;
	}

	public void setPatientDataFileName_PD(String patientDataFileName_PD) {
		this.patientDataFileName_PD = patientDataFileName_PD;
	}

	public double getPatientDataFileSize_PD() {
		return patientDataFileSize_PD;
	}

	public void setPatientDataFileSize_PD(double patientDataFileSize_PD) {
		this.patientDataFileSize_PD = patientDataFileSize_PD;
	}

	public String getPatientData_PD() {
		return patientData_PD;
	}

	public void setPatientData_PD(String patientData_PD) {
		this.patientData_PD = patientData_PD;
	}

	public char getColumnSeparatorChar_PD() {
		return columnSeparatorChar_PD;
	}

	public void setColumnSeparatorChar_PD(char columnSeparatorChar_PD) {
		this.columnSeparatorChar_PD = columnSeparatorChar_PD;
	}

	public char getColumnTextDelimiterChar_PD() {
		return columnTextDelimiterChar_PD;
	}

	public void setColumnTextDelimiterChar_PD(char columnTextDelimiterChar_PD) {
		this.columnTextDelimiterChar_PD = columnTextDelimiterChar_PD;
	}

	public boolean isHasFirstRowHeader_PD() {
		return hasFirstRowHeader_PD;
	}

	public void setHasFirstRowHeader_PD(boolean hasFirstRowHeader_PD) {
		this.hasFirstRowHeader_PD = hasFirstRowHeader_PD;
	}

	public String getPatientIDcolumn_PD() {
		return patientIDcolumn_PD;
	}

	public void setPatientIDcolumn_PD(String patientIDcolumn_PD) {
		this.patientIDcolumn_PD = patientIDcolumn_PD;
	}

	public String getPatientBirthDateColumn_PD() {
		return patientBirthDateColumn_PD;
	}

	public void setPatientBirthDateColumn_PD(String patientBirthDateColumn_PD) {
		this.patientBirthDateColumn_PD = patientBirthDateColumn_PD;
	}

	public String getPatientGenderColumn_PD() {
		return patientGenderColumn_PD;
	}

	public void setPatientGenderColumn_PD(String patientGenderColumn_PD) {
		this.patientGenderColumn_PD = patientGenderColumn_PD;
	}

	public String getPatientFacet1column_PD() {
		return patientFacet1column_PD;
	}

	public void setPatientFacet1column_PD(String patientFacet1column_PD) {
		this.patientFacet1column_PD = patientFacet1column_PD;
	}

	public String getDateFormat_PD() {
		return dateFormat_PD;
	}

	public void setDateFormat_PD(String dateFormat_PD) {
		this.dateFormat_PD = dateFormat_PD;
	}
	
	public boolean isOMOP_PD() {
		return isOMOP_PD;
	}
	
	public void setOMOP_PD(boolean isOMOP_PD) {
		this.isOMOP_PD = isOMOP_PD;
	}
	
	public boolean isGenderEnabled() {
		return isGenderEnabled;
	}

	public void setGenderEnabled(boolean isGenderEnabled) {
		this.isGenderEnabled = isGenderEnabled;
	}

	public DataLoadContainer<List<Patient>> getPatientData_LOADED() {
		return patientData_LOADED;
	}

	public void setPatientData_LOADED(DataLoadContainer<List<Patient>> patientData_LOADED) {
		this.patientData_LOADED = patientData_LOADED;
	}

	public String getVisitDataFileName_VD() {
		return visitDataFileName_VD;
	}

	public void setVisitDataFileName_VD(String visitDataFileName_VD) {
		this.visitDataFileName_VD = visitDataFileName_VD;
	}

	public double getVisitDataFileSize_VD() {
		return visitDataFileSize_VD;
	}

	public void setVisitDataFileSize_VD(double visitDataFileSize_VD) {
		this.visitDataFileSize_VD = visitDataFileSize_VD;
	}

	public String getVisitData_VD() {
		return visitData_VD;
	}

	public void setVisitData_VD(String visitData_VD) {
		this.visitData_VD = visitData_VD;
	}

	public char getColumnSeparatorChar_VD() {
		return columnSeparatorChar_VD;
	}

	public void setColumnSeparatorChar_VD(char columnSeparatorChar_VD) {
		this.columnSeparatorChar_VD = columnSeparatorChar_VD;
	}

	public char getColumnTextDelimiterChar_VD() {
		return columnTextDelimiterChar_VD;
	}

	public void setColumnTextDelimiterChar_VD(char columnTextDelimiterChar_VD) {
		this.columnTextDelimiterChar_VD = columnTextDelimiterChar_VD;
	}

	public boolean isHasFirstRowHeader_VD() {
		return hasFirstRowHeader_VD;
	}

	public void setHasFirstRowHeader_VD(boolean hasFirstRowHeader_VD) {
		this.hasFirstRowHeader_VD = hasFirstRowHeader_VD;
	}

	public String getPatientIDcolumn_VD() {
		return patientIDcolumn_VD;
	}

	public void setPatientIDcolumn_VD(String patientIDcolumn_VD) {
		this.patientIDcolumn_VD = patientIDcolumn_VD;
	}

	public String getVisitIDcolumn_VD() {
		return visitIDcolumn_VD;
	}

	public void setVisitIDcolumn_VD(String visitIDcolumn_VD) {
		this.visitIDcolumn_VD = visitIDcolumn_VD;
	}

	public String getVisitStartDateColumn_VD() {
		return visitStartDateColumn_VD;
	}

	public void setVisitStartDateColumn_VD(String visitStartDateColumn_VD) {
		this.visitStartDateColumn_VD = visitStartDateColumn_VD;
	}

	public String getDateFormat_VD() {
		return dateFormat_VD;
	}

	public void setDateFormat_VD(String dateFormat_VD) {
		this.dateFormat_VD = dateFormat_VD;
	}
	
	public boolean isOMOP_VD() {
		return isOMOP_VD;
	}
	
	public void setOMOP_VD(boolean isOMOP_VD) {
		this.isOMOP_VD = isOMOP_VD;
	}
	
	public DataLoadContainer<List<Visit>> getVisitData_LOADED() {
		return visitData_LOADED;
	}

	public void setVisitData_LOADED(DataLoadContainer<List<Visit>> visitData_LOADED) {
		this.visitData_LOADED = visitData_LOADED;
	}

	public String getDiagnosisDataFileName_DD() {
		return diagnosisDataFileName_DD;
	}

	public void setDiagnosisDataFileName_DD(String diagnosisDataFileName_DD) {
		this.diagnosisDataFileName_DD = diagnosisDataFileName_DD;
	}

	public double getDiagnosisDataFileSize_DD() {
		return diagnosisDataFileSize_DD;
	}

	public void setDiagnosisDataFileSize_DD(double diagnosisDataFileSize_DD) {
		this.diagnosisDataFileSize_DD = diagnosisDataFileSize_DD;
	}

	public String getDiagnosisData_DD() {
		return diagnosisData_DD;
	}

	public void setDiagnosisData_DD(String diagnosisData_DD) {
		this.diagnosisData_DD = diagnosisData_DD;
	}

	public char getColumnSeparatorChar_DD() {
		return columnSeparatorChar_DD;
	}

	public void setColumnSeparatorChar_DD(char columnSeparatorChar_DD) {
		this.columnSeparatorChar_DD = columnSeparatorChar_DD;
	}

	public char getColumnTextDelimiterChar_DD() {
		return columnTextDelimiterChar_DD;
	}

	public void setColumnTextDelimiterChar_DD(char columnTextDelimiterChar_DD) {
		this.columnTextDelimiterChar_DD = columnTextDelimiterChar_DD;
	}

	public boolean isHasFirstRowHeader_DD() {
		return hasFirstRowHeader_DD;
	}

	public void setHasFirstRowHeader_DD(boolean hasFirstRowHeader_DD) {
		this.hasFirstRowHeader_DD = hasFirstRowHeader_DD;
	}

	public String getPatientIDcolumn_DD() {
		return patientIDcolumn_DD;
	}

	public void setPatientIDcolumn_DD(String patientIDcolumn_DD) {
		this.patientIDcolumn_DD = patientIDcolumn_DD;
	}

	public String getVisitIDcolumn_DD() {
		return visitIDcolumn_DD;
	}

	public void setVisitIDcolumn_DD(String visitIDcolumn_DD) {
		this.visitIDcolumn_DD = visitIDcolumn_DD;
	}

	public String getDiagnosisCodeColumn_DD() {
		return diagnosisCodeColumn_DD;
	}

	public void setDiagnosisCodeColumn_DD(String diagnosisCodeColumn_DD) {
		this.diagnosisCodeColumn_DD = diagnosisCodeColumn_DD;
	}
	
	public boolean isOMOP_DD() {
		return isOMOP_DD;
	}
	
	public void setOMOP_DD(boolean isOMOP_DD) {
		this.isOMOP_DD = isOMOP_DD;
	}
	
	public DataLoadContainer<List<Visit>> getDiagnosisData_LOADED() {
		return diagnosisData_LOADED;
	}

	public void setDiagnosisData_LOADED(DataLoadContainer<List<Visit>> diagnosisData_LOADED) {
		this.diagnosisData_LOADED = diagnosisData_LOADED;
	}
	
	public Map<String, Integer> getDiagnosisCodeIdStringMap() {
		return diagnosisCodeIdStringMap;
	}

	public void setDiagnosisCodeIdStringMap(Map<String, Integer> diagnosisCodeIdStringMap) {
		this.diagnosisCodeIdStringMap = diagnosisCodeIdStringMap;
	}
	
	public String getDescrDiagnosisDataFileName_DDE() {
		return descrDiagnosisDataFileName_DDE;
	}

	public void setDescrDiagnosisDataFileName_DDE(String descrDiagnosisDataFileName_DDE) {
		this.descrDiagnosisDataFileName_DDE = descrDiagnosisDataFileName_DDE;
	}

	public double getDescrDiagnosisDataFileSize_DDE() {
		return descrDiagnosisDataFileSize_DDE;
	}

	public void setDescrDiagnosisDataFileSize_DDE(double descrDiagnosisDataFileSize_DDE) {
		this.descrDiagnosisDataFileSize_DDE = descrDiagnosisDataFileSize_DDE;
	}

	public String getDescrDiagnosisData_DDE() {
		return descrDiagnosisData_DDE;
	}

	public void setDescrDiagnosisData_DDE(String descrDiagnosisData_DDE) {
		this.descrDiagnosisData_DDE = descrDiagnosisData_DDE;
	}

	public char getColumnSeparatorChar_DDE() {
		return columnSeparatorChar_DDE;
	}

	public void setColumnSeparatorChar_DDE(char columnSeparatorChar_DDE) {
		this.columnSeparatorChar_DDE = columnSeparatorChar_DDE;
	}

	public char getColumnTextDelimiterChar_DDE() {
		return columnTextDelimiterChar_DDE;
	}

	public void setColumnTextDelimiterChar_DDE(char columnTextDelimiterChar_DDE) {
		this.columnTextDelimiterChar_DDE = columnTextDelimiterChar_DDE;
	}

	public boolean isHasFirstRowHeader_DDE() {
		return hasFirstRowHeader_DDE;
	}

	public void setHasFirstRowHeader_DDE(boolean hasFirstRowHeader_DDE) {
		this.hasFirstRowHeader_DDE = hasFirstRowHeader_DDE;
	}

	public String getDiagnosisDescriptionColumn_DDE() {
		return diagnosisDescriptionColumn_DDE;
	}

	public void setDiagnosisDescriptionColumn_DDE(String diagnosisDescriptionColumn_DDE) {
		this.diagnosisDescriptionColumn_DDE = diagnosisDescriptionColumn_DDE;
	}

	public String getDiagnosisCodeColumn_DDE() {
		return diagnosisCodeColumn_DDE;
	}

	public void setDiagnosisCodeColumn_DDE(String diagnosisCodeColumn_DDE) {
		this.diagnosisCodeColumn_DDE = diagnosisCodeColumn_DDE;
	}

	public DataLoadContainer<Map<String, String>> getDescrDiagnosisData_LOADED() {
		return descrDiagnosisData_LOADED;
	}

	public void setDescrDiagnosisData_LOADED(DataLoadContainer<Map<String, String>> descrDiagnosisData_LOADED) {
		this.descrDiagnosisData_LOADED = descrDiagnosisData_LOADED;
	}

	public Map<String, Set<String>> getGroupNameListCodesMap() {
		return groupNameListCodesMap;
	}

	public void setGroupNameListCodesMap(Map<String, Set<String>> groupNameListCodesMap) {
		this.groupNameListCodesMap = groupNameListCodesMap;
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

	public String getFemaleIdentifier_p() {
		return femaleIdentifier_p;
	}

	public void setFemaleIdentifier_p(String femaleIdentifier_p) {
		this.femaleIdentifier_p = femaleIdentifier_p;
	}

	public String getMaleIdentifier_p() {
		return maleIdentifier_p;
	}

	public void setMaleIdentifier_p(String maleIdentifier_p) {
		this.maleIdentifier_p = maleIdentifier_p;
	}

	public String getIsDirectional_p() {
		return isDirectional_p;
	}

	public void setIsDirectional_p(String isDirectional_p) {
		this.isDirectional_p = isDirectional_p;
	}

	public Integer getDirectMinDays_p() {
		return directMinDays_p;
	}

	public void setDirectMinDays_p(Integer directMinDays_p) {
		this.directMinDays_p = directMinDays_p;
	}

	public PatientAgeENUM getPatientAgeComputation_p() {
		return patientAgeComputation_p;
	}

	public void setPatientAgeComputation_p(PatientAgeENUM patientAgeComputation_p) {
		this.patientAgeComputation_p = patientAgeComputation_p;
	}

	public AdjMethodENUM getPvalAdjApproach_p() {
		return pvalAdjApproach_p;
	}

	public void setPvalAdjApproach_p(AdjMethodENUM pvalAdjApproach_p) {
		this.pvalAdjApproach_p = pvalAdjApproach_p;
	}

	public boolean isMinAgeEnabled_p() {
		return minAgeEnabled_p;
	}

	public void setMinAgeEnabled_p(boolean minAgeEnabled_p) {
		this.minAgeEnabled_p = minAgeEnabled_p;
	}

	public Integer getFPATminAge_p() {
		return FPATminAge_p;
	}

	public void setFPATminAge_p(Integer fPATminAge_p) {
		FPATminAge_p = fPATminAge_p;
	}

	public boolean isMaxAgeEnabled_p() {
		return maxAgeEnabled_p;
	}

	public void setMaxAgeEnabled_p(boolean maxAgeEnabled_p) {
		this.maxAgeEnabled_p = maxAgeEnabled_p;
	}

	public Integer getFPATmaxAge_p() {
		return FPATmaxAge_p;
	}

	public void setFPATmaxAge_p(Integer fPATmaxAge_p) {
		FPATmaxAge_p = fPATmaxAge_p;
	}

	public boolean isPatientFacetFilteringEnabled_p() {
		return patientFacetFilteringEnabled_p;
	}

	public void setPatientFacetFilteringEnabled_p(boolean patientFacetFilteringEnabled_p) {
		this.patientFacetFilteringEnabled_p = patientFacetFilteringEnabled_p;
	}

	public String[] getPatientFacetsInFilter_p() {
		return patientFacetsInFilter_p;
	}

	public void setPatientFacetsInFilter_p(String[] patientFacetsInFilter_p) {
		this.patientFacetsInFilter_p = patientFacetsInFilter_p;
	}

	public boolean isFCOMscoreEnabled_p() {
		return FCOMscoreEnabled_p;
	}

	public void setFCOMscoreEnabled_p(boolean fCOMscoreEnabled_p) {
		FCOMscoreEnabled_p = fCOMscoreEnabled_p;
	}

	public String getFCOMscoreGreaterLower_p() {
		return FCOMscoreGreaterLower_p;
	}

	public void setFCOMscoreGreaterLower_p(String fCOMscoreGreaterLower_p) {
		FCOMscoreGreaterLower_p = fCOMscoreGreaterLower_p;
	}

	public Double getFCOMscore_p() {
		return FCOMscore_p;
	}

	public void setFCOMscore_p(Double fCOMscore_p) {
		FCOMscore_p = fCOMscore_p;
	}

	public boolean isFCOMrriskEnabled_p() {
		return FCOMrriskEnabled_p;
	}

	public void setFCOMrriskEnabled_p(boolean fCOMrriskEnabled_p) {
		FCOMrriskEnabled_p = fCOMrriskEnabled_p;
	}

	public String getFCOMrriskGreaterLower_p() {
		return FCOMrriskGreaterLower_p;
	}

	public void setFCOMrriskGreaterLower_p(String fCOMrriskGreaterLower_p) {
		FCOMrriskGreaterLower_p = fCOMrriskGreaterLower_p;
	}

	public Double getFCOMrrisk_p() {
		return FCOMrrisk_p;
	}

	public void setFCOMrrisk_p(Double fCOMrrisk_p) {
		FCOMrrisk_p = fCOMrrisk_p;
	}

	public boolean isFCOModdsRatioEnabled_p() {
		return FCOModdsRatioEnabled_p;
	}

	public void setFCOModdsRatioEnabled_p(boolean fCOModdsRatioEnabled_p) {
		FCOModdsRatioEnabled_p = fCOModdsRatioEnabled_p;
	}

	public String getFCOModdsRatioGreaterLower_p() {
		return FCOModdsRatioGreaterLower_p;
	}

	public void setFCOModdsRatioGreaterLower_p(String fCOModdsRatioGreaterLower_p) {
		FCOModdsRatioGreaterLower_p = fCOModdsRatioGreaterLower_p;
	}

	public Double getFCOModdsRatio_p() {
		return FCOModdsRatio_p;
	}

	public void setFCOModdsRatio_p(Double fCOModdsRatio_p) {
		FCOModdsRatio_p = fCOModdsRatio_p;
	}

	public boolean isFCOMphiEnabled_p() {
		return FCOMphiEnabled_p;
	}

	public void setFCOMphiEnabled_p(boolean fCOMphiEnabled_p) {
		FCOMphiEnabled_p = fCOMphiEnabled_p;
	}

	public String getFCOMphiGreaterLower_p() {
		return FCOMphiGreaterLower_p;
	}

	public void setFCOMphiGreaterLower_p(String fCOMphiGreaterLower_p) {
		FCOMphiGreaterLower_p = fCOMphiGreaterLower_p;
	}

	public Double getFCOMphi_p() {
		return FCOMphi_p;
	}

	public void setFCOMphi_p(Double fCOMphi_p) {
		FCOMphi_p = fCOMphi_p;
	}

	public boolean isFCOMfisherAdjEnabled_p() {
		return FCOMfisherAdjEnabled_p;
	}

	public void setFCOMfisherAdjEnabled_p(boolean fCOMfisherAdjEnabled_p) {
		FCOMfisherAdjEnabled_p = fCOMfisherAdjEnabled_p;
	}

	public String getFCOMfisherAdjGreaterLower_p() {
		return FCOMfisherAdjGreaterLower_p;
	}

	public void setFCOMfisherAdjGreaterLower_p(String fCOMfisherAdjGreaterLower_p) {
		FCOMfisherAdjGreaterLower_p = fCOMfisherAdjGreaterLower_p;
	}

	public Double getFCOMfisherAdj_p() {
		return FCOMfisherAdj_p;
	}

	public void setFCOMfisherAdj_p(Double fCOMfisherAdj_p) {
		FCOMfisherAdj_p = fCOMfisherAdj_p;
	}

	public boolean isFCOMminPatEnabled_p() {
		return FCOMminPatEnabled_p;
	}

	public void setFCOMminPatEnabled_p(boolean fCOMminPatEnabled_p) {
		FCOMminPatEnabled_p = fCOMminPatEnabled_p;
	}

	public String getFCOMminPatGreaterLower_p() {
		return FCOMminPatGreaterLower_p;
	}

	public void setFCOMminPatGreaterLower_p(String fCOMminPatGreaterLower_p) {
		FCOMminPatGreaterLower_p = fCOMminPatGreaterLower_p;
	}

	public Integer getFCOMminPat_p() {
		return FCOMminPat_p;
	}

	public void setFCOMminPat_p(Integer fCOMminPat_p) {
		FCOMminPat_p = fCOMminPat_p;
	}

	public Double getOddsRatioConfindeceInterval_p() {
		return oddsRatioConfindeceInterval_p;
	}

	public void setOddsRatioConfindeceInterval_p(Double oddsRatioConfindeceInterval_p) {
		this.oddsRatioConfindeceInterval_p = oddsRatioConfindeceInterval_p;
	}
	
	
	public ComorbidityPatientFilter getPatientFilter() {
		ComorbidityPatientFilter retFilter = null;
		
		if(this.minAgeEnabled_p || this.maxAgeEnabled_p || this.patientFacetFilteringEnabled_p) {
			
			Long minAgeFP = (this.minAgeEnabled_p && this.FPATminAge_p >= 0) ? new Long(this.FPATminAge_p) : null;
			Long maxAgeFP = (this.maxAgeEnabled_p && this.FPATmaxAge_p >= 0) ? new Long(this.FPATmaxAge_p) : null;
			String[] facet_1FP = (this.patientFacetFilteringEnabled_p && this.patientFacetsInFilter_p != null && this.patientFacetsInFilter_p.length > 0) ? this.patientFacetsInFilter_p : null;
			PatientAgeENUM ageComputationMethod = this.getPatientAgeComputation_p();
			
			retFilter = new ComorbidityPatientFilter(minAgeFP, maxAgeFP, null, facet_1FP, ageComputationMethod);
			
		}
		
		return retFilter;
	}
	
	public ComorbidityScoreFilter getComorbidityScoreFilter() {
		ComorbidityScoreFilter retFilter = null;
		
		if(this.FCOMrrisk_p != null || this.FCOMfisherAdj_p != null || this.FCOModdsRatio_p != null ||
				this.FCOMphi_p != null || this.FCOMscore_p != null || this.FCOMminPat_p != null) {
			
			Double relativeRiskIndexThresholdFS = (this.FCOMrrisk_p != null) ? this.FCOMrrisk_p : null;
			ComorbidityScoreFilter.GreatLower relativeRiskIndexGLFS = 
					(this.FCOMrriskGreaterLower_p != null && this.FCOMrriskGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			Double fisherTestThresholdFS = null;
			ComorbidityScoreFilter.GreatLower fisherTestGLFS = null;
			
			Double fisherTestAsjustedThresholdFS = (this.FCOMfisherAdj_p != null) ? this.FCOMfisherAdj_p : null;
			ComorbidityScoreFilter.GreatLower fisherTestAsjustedGLFS = 
					(this.FCOMfisherAdjGreaterLower_p != null && this.FCOMfisherAdjGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			Double oddsRatioIndexThresholdFS = (this.FCOModdsRatio_p != null) ? this.FCOModdsRatio_p : null;
			ComorbidityScoreFilter.GreatLower oddsRatioIndexGLFS = 
					(this.FCOModdsRatioGreaterLower_p != null && this.FCOModdsRatioGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			Double phiIndexThresholdFS = (this.FCOMphi_p != null) ? this.FCOMphi_p : null;
			ComorbidityScoreFilter.GreatLower phiIndexGLFS = 
					(this.FCOMphiGreaterLower_p != null && this.FCOMphiGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			Double scoreThresholdFS = (this.FCOMscore_p != null) ? this.FCOMscore_p : null;
			ComorbidityScoreFilter.GreatLower scoreGLFS = 
					(this.FCOMscoreGreaterLower_p != null && this.FCOMscoreGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			Integer minNumPatientsThresholfFS = (this.FCOMminPat_p != null) ? this.FCOMminPat_p : null;
			ComorbidityScoreFilter.GreatLower minNumPatientsGLFS = 
					(this.FCOMminPatGreaterLower_p != null && this.FCOMminPatGreaterLower_p.toLowerCase().trim().contains("greater")) ? ComorbidityScoreFilter.GreatLower.GREATER_OR_EQUAL_THEN : ComorbidityScoreFilter.GreatLower.LOWER_OR_EQUAL_THEN;
			
			retFilter = new ComorbidityScoreFilter(relativeRiskIndexThresholdFS, relativeRiskIndexGLFS,
					fisherTestThresholdFS, fisherTestGLFS,
					fisherTestAsjustedThresholdFS, fisherTestAsjustedGLFS,
					oddsRatioIndexThresholdFS, oddsRatioIndexGLFS,
					phiIndexThresholdFS, phiIndexGLFS,
					scoreThresholdFS, scoreGLFS,
					minNumPatientsThresholfFS, minNumPatientsGLFS);
			
		}
		
		return retFilter;
	}

	public ComorbidityDirectionalityFilter getComorbidityDirectionalityFilter() {
		ComorbidityDirectionalityFilter retFilter = null;
		
		if(this.isDirectional_p != null && this.isDirectional_p.toLowerCase().trim().contains("enab") && this.directMinDays_p != null && this.directMinDays_p > 0) {
			retFilter = new ComorbidityDirectionalityFilter(new Long(this.directMinDays_p));
		}
		
		return retFilter;
	}
	
	
}
