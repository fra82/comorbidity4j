package es.imim.ibi.comorbidity4j.server.util;

public class ServerExecConfig {
	
	public static boolean isOnline = false;
	
	public static long maxMbSizePatientDataFile = -1l;
	public static long maxNumberOfPatients = -1l;
	
	public static long maxMbSizeVisitDataFile = -1l;
	public static long maxNumberOfVisits = -1l;
	
	public static long maxMbSizeDiagnosisDataFile = -1l;
	public static long maxNumberOfDiagnoses = -1l;
	
	public static long maxNumberComorbidityPairsToAnalyze = 200000l;
	
}