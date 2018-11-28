package es.imim.ibi.comorbidity4j.server.spring;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.util.Strings;

import es.imim.ibi.comorbidity4j.model.Visit;

public class ControllerUtil {
	
	public static int countPatientDiagnoses(UserInputContainer so) {
		int retVal = 0;
		
		if(so != null && so.getDiagnosisData_LOADED() != null && so.getDiagnosisData_LOADED().data != null && so.getDiagnosisData_LOADED().data.size() > 0) {
			for(Visit patientVisit : so.getDiagnosisData_LOADED().data) {
				if(patientVisit != null && patientVisit.getDiagnosisCodeSet() != null) {
					retVal += patientVisit.getDiagnosisCodeSet().size();
				}
			}
		}
		
		return retVal;
	} 
	
	public static Map<String, String> getMenuMap(int step, String mStr, UserInputContainer so) {
		Map<String, String> menuItemsList = new LinkedHashMap<String, String>();
		
		menuItemsList.put("1) Upload patient data" + ((mStr != null && step == 1) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ((step != 1 && so.getPatientData_LOADED() != null && so.getPatientData_LOADED().data != null) ? "<br/><span style='color:red;'>Loaded " + so.getPatientData_LOADED().data.size() + " patient(s).</span>" : "")), null);
		menuItemsList.put("2) Upload visit data" + ((mStr != null && step == 2) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ((step != 2 && so.getVisitData_LOADED() != null && so.getVisitData_LOADED().data != null) ? "<br/><span style='color:red;'>Loaded " + so.getVisitData_LOADED().data.size() + " visit(s).</span>" : "")), null);
		menuItemsList.put("3) Upload diagnosis data" + ((mStr != null && step == 3) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ((step != 3 && so.getDiagnosisData_LOADED() != null && so.getDiagnosisData_LOADED().data != null) ? "<br/><span style='color:red;'>Loaded " + countPatientDiagnoses(so) + " diagnoses.</span>" : "")), null);
		menuItemsList.put("4) Upload diagnosis descr." + ((mStr != null && step == 4) ? ("<br/><span style='color:red;'>" + mStr + "</span>") 
				: ((step > 4 && so.getDescrDiagnosisData_LOADED() != null && so.getDescrDiagnosisData_LOADED().data != null) ? "<br/><span style='color:red;'>Loaded " + so.getDescrDiagnosisData_LOADED().data.size() + " description(s).</span>" : "")), null);
		menuItemsList.put("5) Group diagnoses" + ((mStr != null && step == 5) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ((step > 5 && so.getGroupNameListCodesMap() != null) ? "<br/><span style='color:red;'>Defined " + so.getGroupNameListCodesMap().size() + " group(s).</span>" : "")), null);
		menuItemsList.put("6) Define diagnoses pairs" + ((mStr != null && step == 6) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ((step > 6 && so.getGroupPairignMap() != null) ? "<br/><span style='color:red;'>Defined " + so.getGroupPairignMap().size() + " pairing(s).</span>" : "")), null);
		menuItemsList.put("7) Define comorbidity analysis parameters" + ((mStr != null && step == 7) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ""), null);
		menuItemsList.put("8) Start comorbidity analysis" + ((mStr != null && step == 8) ? "<br/><span style='color:red;'>" + mStr + "</span>" 
				: ""), null);
		
		int i = 1;
		String selectedKey = null;
		for(Entry<String, String> menuElem : menuItemsList.entrySet()) {
			if(i == step) {
				selectedKey = menuElem.getKey();
				break;
			}
			i++;
		}
		
		if(!Strings.isEmpty(selectedKey)) {
			menuItemsList.put(selectedKey, "S");
		}
		
		return menuItemsList;
	}
	
	
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
	    
		// Year, month and day
		put("^\\d{8}$", "yyyyMMdd");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
	    put("^\\d{12}$", "yyyyMMddHHmm");
	    put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
	    
	    // Without T
	    put("^\\d{14}$", "yyyyMMddHHmmss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
	    put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "dd-MM-yyyy HH:mm:ss.SSS");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "MM/dd/yyyy HH:mm:ss.SSS");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd HH:mm:ss.SSS");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "dd MMM yyyy HH:mm:ss.SSS");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}$", "dd MMMM yyyy HH:mm:ss.SSS");
	    
	    // With T
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}$", "dd-MM-yyyy'T'HH:mm");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}$", "MM/dd/yyyy'T'HH:mm");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}$", "yyyy/MM/dd'T'HH:mm");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}$", "dd MMM yyyy'T'HH:mm");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}$", "dd MMMM yyyy'T'HH:mm");
	    put("^\\d{8}T\\d{6}$", "yyyyMMdd'T'HHmmss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy'T'HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ss");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy'T'HH:mm:ss");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd'T'HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy'T'HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy'T'HH:mm:ss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "dd-MM-yyyy'T'HH:mm:ss.SSS");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd'T'HH:mm:ss.SSS");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "MM/dd/yyyy'T'HH:mm:ss.SSS");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd'T'HH:mm:ss.SSS");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "dd MMM yyyy'T'HH:mm:ss.SSS");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}$", "dd MMMM yyyy'T'HH:mm:ss.SSS");
	    
	    
	    // Without T, with Z 
	    // Z means "zero hour offset" also known as "Zulu time" (UTC).
	    // If your strings always have a "Z" you can use:
	    // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	    // format.setTimeZone(TimeZone.getTimeZone("UTC"));
	    put("^\\d{14}Z$", "yyyyMMddHHmmss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}Z$", "dd-MM-yyyy HH:mm'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}Z$", "yyyy-MM-dd HH:mm'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}Z$", "MM/dd/yyyy HH:mm'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}Z$", "yyyy/MM/dd HH:mm'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}Z$", "dd MMM yyyy HH:mm'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}Z$", "dd MMMM yyyy HH:mm'Z'");
	    put("^\\d{14}Z$", "yyyyMMddHHmmss'Z'");
	    put("^\\d{8}\\s\\d{6}Z$", "yyyyMMdd HHmmss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "dd-MM-yyyy HH:mm:ss'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "yyyy-MM-dd HH:mm:ss'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "MM/dd/yyyy HH:mm:ss'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "yyyy/MM/dd HH:mm:ss'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "dd MMM yyyy HH:mm:ss'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}Z$", "dd MMMM yyyy HH:mm:ss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "dd-MM-yyyy HH:mm:ss.SSS'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "yyyy-MM-dd HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "MM/dd/yyyy HH:mm:ss.SSS'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "yyyy/MM/dd HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "dd MMM yyyy HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}Z$", "dd MMMM yyyy HH:mm:ss.SSS'Z'");
	    
	    // With T, with Z
	    // Z means "zero hour offset" also known as "Zulu time" (UTC).
	    // If your strings always have a "Z" you can use:
	    // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	    // format.setTimeZone(TimeZone.getTimeZone("UTC"));
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}Z$", "dd-MM-yyyy'T'HH:mm'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}Z$", "yyyy-MM-dd'T'HH:mm'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}Z$", "MM/dd/yyyy'T'HH:mm'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}Z$", "yyyy/MM/dd'T'HH:mm'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}Z$", "dd MMM yyyy'T'HH:mm'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}Z$", "dd MMMM yyyy'T'HH:mm'Z'");
	    put("^\\d{8}T\\d{6}Z$", "yyyyMMdd'T'HHmmss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}Z$", "dd-MM-yyyy'T'HH:mm:ss'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}Z$", "yyyy-MM-dd'T'HH:mm:ss'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}Z$", "MM/dd/yyyy'T'HH:mm:ss'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}Z$", "yyyy/MM/dd'T'HH:mm:ss'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}Z$", "dd MMM yyyy'T'HH:mm:ss'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}Z$", "dd MMMM yyyy'T'HH:mm:ss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "yyyy/MM/dd'T'HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z$", "dd MMM yyyy'T'HH:mm:ss.SSS'Z'");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}Z$", "dd MMMM yyyy'T'HH:mm:ss.SSS'Z'");
	    
	    
	    // Without T, with timezone
	    put("^\\d{14}\\s[A-Z]{3,4}$", "yyyyMMddHHmmss'Z'");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd-MM-yyyy HH:mm z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy-MM-dd HH:mm z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "MM/dd/yyyy HH:mm z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy/MM/dd HH:mm z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMM yyyy HH:mm z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMMM yyyy HH:mm z");
	    put("^\\d{14}\\s[A-Z]{3,4}$", "yyyyMMddHHmmss z");
	    put("^\\d{8}\\s\\d{6}\\s[A-Z]{3,4}$", "yyyyMMdd HHmmss z");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd-MM-yyyy HH:mm:ss z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy-MM-dd HH:mm:ss z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "MM/dd/yyyy HH:mm:ss z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy/MM/dd HH:mm:ss z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMM yyyy HH:mm:ss z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMMM yyyy HH:mm:ss z");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "dd-MM-yyyy HH:mm:ss.SSS z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "yyyy-MM-dd HH:mm:ss.SSS z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "MM/dd/yyyy HH:mm:ss.SSS z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "yyyy/MM/dd HH:mm:ss.SSS z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "dd MMM yyyy HH:mm:ss.SSS z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}\\s[A-Z]{3,4}$", "dd MMMM yyyy HH:mm:ss.SSS z");
	    
	    // With T, with timezone
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd-MM-yyyy'T'HH:mm z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy-MM-dd'T'HH:mm z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "MM/dd/yyyy'T'HH:mm z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy/MM/dd'T'HH:mm z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMM yyyy'T'HH:mm z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMMM yyyy'T'HH:mm z");
	    put("^\\d{8}T\\d{6}\\s[A-Z]{3,4}$", "yyyyMMdd'T'HHmmss z");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd-MM-yyyy'T'HH:mm:ss z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy-MM-dd'T'HH:mm:ss z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "MM/dd/yyyy'T'HH:mm:ss z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "yyyy/MM/dd'T'HH:mm:ss z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMM yyyy'T'HH:mm:ss z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\s[A-Z]{3,4}$", "dd MMMM yyyy'T'HH:mm:ss z");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "dd-MM-yyyy'T'HH:mm:ss.SSS z");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "yyyy-MM-dd'T'HH:mm:ss.SSS z");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "MM/dd/yyyy'T'HH:mm:ss.SSS z");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "yyyy/MM/dd'T'HH:mm:ss.SSS z");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\s[A-Z]{3,4}$", "dd MMM yyyy'T'HH:mm:ss.SSS z");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}T\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}\\s[A-Z]{3,4}$", "dd MMMM yyyy'T'HH:mm:ss.SSS z");
	    	    
	}};

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
	 * format is unknown. You can simply extend DateUtil with more formats if needed.
	 * 
	 * @param dateString The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
	    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
	        if (dateString.matches(regexp)) {
	            return DATE_FORMAT_REGEXPS.get(regexp);
	        }
	    }
	    return null;
	}
	
	
	public static void main(String[] args) {
		String strToParse = "1976-04-02 13:12:11.323";
		String datef = determineDateFormat(strToParse);
		
		System.out.println("DATEF: " + strToParse + " > " + datef);
	}
	
}
