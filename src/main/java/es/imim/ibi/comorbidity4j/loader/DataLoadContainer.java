package es.imim.ibi.comorbidity4j.loader;

public class DataLoadContainer<T> {
	
	public T data;
	public String errorMsg = "";
	public String warningMsg = "";
	
	public int skippedLine_PD = 0;
	public int unparsableDate_PD = 0;
	public int duplicatedPatientID_PD = 0;
	
	public int skippedLine_VD = 0;
	public int unparsableVisitDate_VD = 0;
	public int duplicatedVisitID_VD = 0;
	
	public int skippedLine_DD = 0;
	public int duplicatedPatVisitDiagnosis_DD = 0;
	public int unexistingPatientOrVisitID_DD = 0;
	
	public int skippedLine_DDE = 0;
	public int nullOrEmptyDiagnosisDescription_DDE = 0;
	
}
