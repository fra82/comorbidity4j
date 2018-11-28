package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.io.CharSource;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import es.imim.ibi.comorbidity4j.loader.DataLoadContainer;
import es.imim.ibi.comorbidity4j.loader.PatientDataLoader;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.omop.csv.OMOPutilsCSV;

@Controller
public class PatientDataValidation extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(PatientDataValidation.class);
	
	private static DecimalFormat fileSizeFormatter = new DecimalFormat("##0.000");
	
	@GetMapping(value = "/patientData_1_specifyCSV")
	public String patientData_1_specifyCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		request.getSession().invalidate();
		
		UserInputContainer so = getSessionObj(request);
		so.resetPatientData();
		so.resetVisitData();
		so.resetDiagnosisData();
		so.resetDescrDiagnosisData();
		so.resetDiagnosisGroupingData();
		so.resetDiagnosisPairingData();
		
		System.gc();
		
		setMenu(model, 1, "STEP A: upload file", so);
		return "patientData_1_specifyCSV";
	}


	@GetMapping(value = "/patientData_2_validateCSV")
	public String patientData_2_validateCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);


		// Load vars from session
		char columnSeparatorChar = so.getColumnSeparatorChar_PD();
		char columnTextDelimiterChar = so.getColumnTextDelimiterChar_PD();
		boolean hasFirstRowHeader = so.isHasFirstRowHeader_PD();
		String result = so.getPatientData_PD();

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		// If no CSV contents go to the previous step - CSV file selection
		if(result == null || result.length() == 0) {
			checkErrors += "ERROR: while reading Patient Data file contents. Please, try to reload the file.<br/>";
			model.put("errorMessage", checkErrors);
			return patientData_1_specifyCSVg(model, request);
		}


		model.put("fileName", so.getPatientDataFileName_PD());
		model.put("fileSize", fileSizeFormatter.format(so.getPatientDataFileSize_PD()) + "");
		
		
		String lineReadingAlerts = "";
		
		// OMOP CSV pre-processing
		Reader patientDataReader = new StringReader(result); 
		if(so.isOMOP_PD()) {
			ImmutablePair<ImmutablePair<String, String>, Reader> patientListOMOPpreprocessed = OMOPutilsCSV.patientDataPreProcessor(new StringReader(result), so);
			String errorLog = patientListOMOPpreprocessed.getLeft().getLeft();
			lineReadingAlerts = (patientListOMOPpreprocessed.getLeft().getRight() != null) ? patientListOMOPpreprocessed.getLeft().getRight() : "";
			
			patientDataReader = patientListOMOPpreprocessed.getRight();
			
			if(!Strings.isEmpty(errorLog)) {
				model.put("errorMessage", checkErrors);
				model.put("warningMessage", lineReadingAlerts);
				return patientData_1_specifyCSVg(model, request);
			}
		}
		
		
		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = (columnTextDelimiterChar != 'N') ? new CSVParserBuilder().withSeparator(columnSeparatorChar).withQuoteChar(columnTextDelimiterChar).build() : new CSVParserBuilder().withSeparator(columnSeparatorChar).build();
			reader = new CSVReaderBuilder(patientDataReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while reading Patient Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Reading all CSV records
		List<String[]> patientDataCSVrecords = new ArrayList<String[]>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					patientDataCSVrecords.add(nextLine);
				} catch (Exception e) {
					e.printStackTrace();
					lineReadingAlerts += "WARNING: exception while reading line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " of Patient Data file. - EXCEPTION: " + e.getMessage() + "<br/>";
					logger.error(" > EXCEPTION: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading data from Patient Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}


		if(!Strings.isEmpty(lineReadingAlerts)) {
			model.put("warningMessage", lineReadingAlerts);
		}
		
		model.put("numberOfCSVlinesRead", patientDataCSVrecords.size());
		
		// Prepare the selection of the columns
		Map<String, Integer> dateFormatCounter = new HashMap<String, Integer>();
		List<String> columnNameList = new ArrayList<String>();
		try {
			// Header
			List<Map<String, String>> columnList = new ArrayList<Map<String, String>>();
			for(int i = 0; i < patientDataCSVrecords.get(0).length; i++) {

				Map<String, String> columnStringMap = new TreeMap<String, String>();
				columnStringMap.put("title", ((hasFirstRowHeader) ? ((patientDataCSVrecords.get(0)[i] != null) ? patientDataCSVrecords.get(0)[i] : "col_" + i) : "col_" + i));
				columnStringMap.put("field", ((hasFirstRowHeader) ? ((patientDataCSVrecords.get(0)[i] != null) ? patientDataCSVrecords.get(0)[i].replaceAll("[^a-zA-Z0-9]", "_") : "col_" + i) : "col_" + i));
				columnStringMap.put("sorter", "string");
				columnStringMap.put("align", "left");

				columnNameList.add(columnStringMap.get("title"));

				columnList.add(columnStringMap);
			}
			model.put("sampleCSVcolumns", new ObjectMapper().writeValueAsString(columnList));

			// Rows
			List<Map<String, String>> sampleRowList = new ArrayList<Map<String, String>>();
			int rowNum = 0;
			for(String[] row : patientDataCSVrecords) {
				rowNum++;

				if(rowNum == 1 && hasFirstRowHeader) {
					continue;
				}

				if(rowNum > ((hasFirstRowHeader) ? 6 : 5)) break;

				Map<String, String> rowStringMap = new TreeMap<String, String>();
				for(int i = 0; i < row.length; i++) {
					rowStringMap.put(columnList.get(i).get("field"), row[i]);

					String dateFormatGuessed = ControllerUtil.determineDateFormat(row[i]);
					if(!Strings.isEmpty(dateFormatGuessed)) {
						if(!dateFormatCounter.containsKey(dateFormatGuessed)) {
							dateFormatCounter.put(dateFormatGuessed, 0);
						}
						int currentVal = dateFormatCounter.get(dateFormatGuessed);
						dateFormatCounter.put(dateFormatGuessed, currentVal + 1);
					}

				}
				sampleRowList.add(rowStringMap);
			}

			model.put("sampleCSVrows", new ObjectMapper().writeValueAsString(sampleRowList));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading sample data / rows from Patient Data file. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Get the most common date format
		if(dateFormatCounter != null && dateFormatCounter.size() > 0) {
			LinkedHashMap<String, Integer> dateFormatCounterSortedDesc = dateFormatCounter.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
			for(Entry<String, Integer> elem : dateFormatCounterSortedDesc.entrySet()) {
				model.put("guessedDateFormat", elem.getKey() + "");
				break;
			}
		}

		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
		}
		else {
			model.remove("errorMessage");
		}

		model.put("columnNameList", columnNameList);
		
		if(so.isOMOP_PD()) {
			model.put("isOMOP", "true");
		}
		
		model.put("columnNameList", columnNameList);
		
		System.gc();
		
		setMenu(model, 1, "STEP B: select table columns", so);
		return "patientData_2_validateCSV";

	}

	@PostMapping(value = "/patientData_2_validateCSV")
	public String patientData_2_validateCSVp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		// Check patient data file
		so.setPatientData_PD("");
		so.setPatientDataFileSize_PD(-1l);
		so.setPatientDataFileName_PD("");
		so.setColumnSeparatorChar_PD('\t');
		so.setColumnTextDelimiterChar_PD('N');
		so.setHasFirstRowHeader_PD(true);
		so.setOMOP_PD(false);

		// Loading CSV file
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {

			String checkErrors = "";

			@SuppressWarnings("unused")
			Reader patientDataFileReader = null;
			Part patientDataFilePart = null;
			try {
				patientDataFilePart = request.getPart("patientDataFile");
				if(patientDataFilePart != null) {
					patientDataFileReader = new InputStreamReader(patientDataFilePart.getInputStream());

					if(patientDataFilePart.getSize() <= 0l) {
						checkErrors += "ERROR: impossible to read Patient Data file content - the file is empty." + "<br/>";
					}
					else if(ServerExecConfig.isOnline && ServerExecConfig.maxMbSizePatientDataFile > 0l && (long) patientDataFilePart.getSize() >= (ServerExecConfig.maxMbSizePatientDataFile * 1048576l)) {
						checkErrors = "ATTENTION: the Patient Data file uploaded is about " + (((int) (patientDataFilePart.getSize() / 1048576)) + 1) + " Mb, greater than " + ServerExecConfig.maxMbSizePatientDataFile + "Mb, that is the maximum Patient file size allowed on this server.<br/>" +
								"Please, upload a smaller file.<br/> The running instance of Comorbidity4web supports analysis of Patient Data files with size equal or smaller than " + ServerExecConfig.maxMbSizePatientDataFile + " Mb.<br/><br/>"
								+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
								+ "to performe analysis of comorbidities over larger sets of data on your PC." + "<br/>";
					}

					so.setPatientDataFileSize_PD((double) patientDataFilePart.getSize() / 1048576d);
					so.setPatientDataFileName_PD(patientDataFilePart.getSubmittedFileName() != null ? patientDataFilePart.getSubmittedFileName() : "NO_NAME");
				}
				else {
					checkErrors += "ERROR: Patient Data file not uploaded." + "<br/>";
				}
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Patient Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			} catch (ServletException e) {
				checkErrors += "ERROR: while loading Patient Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			}


			// Check spreadsheet column separator
			ImmutablePair<String, String> columnSeparator_check = checkParameterString(request.getParameter("columnSeparator"), "columnSeparator");
			String columnSeparator = (columnSeparator_check.getLeft() == null) ? columnSeparator_check.getRight() : null;
			char columnSeparatorChar = '\t';
			checkErrors += (columnSeparator_check.getLeft() != null) ? columnSeparator_check.getLeft() + "\n" : "";
			if(columnSeparator != null && columnSeparator.trim().toLowerCase().equals("commasep")) {
				columnSeparatorChar = ',';
			}
			else if(columnSeparator != null && columnSeparator.trim().toLowerCase().equals("tabsep")) {
				columnSeparatorChar = '\t';
			}
			else if(columnSeparator != null && columnSeparator.trim().toLowerCase().equals("verticalbarsep")) {
				columnSeparatorChar = '|';
			}
			else {
				checkErrors += "ERROR: invalid spreadsheet column separator (" + ((columnSeparator != null) ? columnSeparator : "null") + ")" + "<br/>";
			}


			// Check spreadsheet column text delimiter
			ImmutablePair<String, String> columnTextDelimiter_check = checkParameterString(request.getParameter("columnTextDelimiter"), "columnTextDelimiter");
			String columnTextDelimiter = (columnTextDelimiter_check.getLeft() == null) ? columnTextDelimiter_check.getRight() : null;
			char columnTextDelimiterChar = 'N';
			checkErrors += (columnTextDelimiter_check.getLeft() != null) ? columnTextDelimiter_check.getLeft() + "\n" : "";
			if(columnTextDelimiter != null && columnTextDelimiter.trim().toLowerCase().equals("none")) {
				columnTextDelimiterChar = 'N';
			}
			else if(columnTextDelimiter != null && columnTextDelimiter.trim().toLowerCase().equals("doublequotes")) {
				columnTextDelimiterChar = '"';
			}
			else if(columnTextDelimiter != null && columnTextDelimiter.trim().toLowerCase().equals("singlequote")) {
				columnTextDelimiterChar = '\'';
			}
			else {
				checkErrors += "ERROR: invalid spreadsheet column text delimiter (" + ((columnSeparator != null) ? columnSeparator : "null") + ")" + "<br/>";
			}


			// Check if the first row is an header
			boolean hasFirstRowHeader = (request.getParameter("isFirstRowHeader") == null) ? false : true;

			// Check if the Patient Data are in OMOP format
			boolean isOMOP_PD = (request.getParameter("isOMOP") == null) ? false : true;

			// If no CSV contents go to the previous step - CSV file selection
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				return patientData_1_specifyCSVg(model, request);
			}


			// Store vars on session
			so.setColumnSeparatorChar_PD(columnSeparatorChar);
			so.setColumnTextDelimiterChar_PD(columnTextDelimiterChar);
			so.setHasFirstRowHeader_PD(hasFirstRowHeader);
			so.setOMOP_PD(isOMOP_PD);
			try {
				String result = IOUtils.toString(patientDataFilePart.getInputStream(), "UTF-8");
				so.setPatientData_PD(result);
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Patient Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			}

			setMenu(model, 1, "STEP B: select table columns", so);
			return patientData_2_validateCSVg(model, request);
		}
		else {
			model.put("errorMessage", "ERROR: somehting went wrong! Please, review the form fields.");
			return patientData_1_specifyCSVg(model, request);
		}

	}


	@GetMapping(value = "/patientData_3_confirmDataImport")
	public String patientData_3_confirmDataImportg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		model.put("fileName", so.getPatientDataFileName_PD());
		model.put("fileSize", fileSizeFormatter.format(so.getPatientDataFileSize_PD()) + "");
		model.put("patientIDcolumn_PD", so.getPatientIDcolumn_PD());
		model.put("patientBirthDateColumn_PD", so.getPatientBirthDateColumn_PD());
		model.put("patientGenderColumn_PD", so.getPatientGenderColumn_PD());
		model.put("patientFacet1column_PD", (so.getPatientFacet1column_PD() == null || so.getPatientFacet1column_PD().toLowerCase().equals("__select_optional__")) ? "" : so.getPatientFacet1column_PD());
		model.put("dateFormat_PD", so.getDateFormat_PD());
		model.put("isGenderEnabled", so.isGenderEnabled() + "");
		
		if(Strings.isEmpty(so.getPatientDataFileName_PD())) {
			model.put("errorMessage", checkErrors + "Patient data file not selected - pelase upload a valid file."  + "<br/>");
			return patientData_1_specifyCSVg(model, request);
		}

		// Show stats on loaded data
		PatientDataLoader pd_loader = new PatientDataLoader();
		pd_loader.initializeParamsFromSessionObj(so);

		String checkWarnings = "";

		try {
			DataLoadContainer<List<Patient>> loadedData = pd_loader.loadDataFromReader(CharSource.wrap(so.getPatientData_PD()).openStream(), so.isGenderEnabled());
			checkErrors = (loadedData != null && !Strings.isEmpty(loadedData.errorMsg)) ? loadedData.errorMsg  + "<br/>" : "";
			checkWarnings = (loadedData != null && !Strings.isEmpty(loadedData.warningMsg)) ? loadedData.warningMsg  + "<br/>" : "";

			model.put("skippedLine_PD", loadedData.skippedLine_PD);
			model.put("unparsableDate_PD", loadedData.unparsableDate_PD);
			model.put("duplicatedPatientID_PD", loadedData.duplicatedPatientID_PD);

			model.put("numberPatientsLoaded", (loadedData.data != null) ? loadedData.data.size() : 0);

			if(loadedData.data == null || loadedData.data.size() == 0) {
				model.put("errorMessage", checkErrors + "No patient record loaded from the data - pelase upload a valid file."  + "<br/>");
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return patientData_1_specifyCSVg(model, request);
			}
			
			// Check number of female and male sex identifier
			if(so.isGenderEnabled()) {
				Set<String> maleFemaleIdentifiers = new HashSet<String>();
				for(Patient pt : loadedData.data) {
					if(pt != null && StringUtils.isNotBlank(pt.getSex())) {
						maleFemaleIdentifiers.add(pt.getSex());
					}
				}
				if(maleFemaleIdentifiers == null || maleFemaleIdentifiers.size() < 2) {
					model.put("errorMessage", checkErrors + "The dataset provided specifies " + 
							((maleFemaleIdentifiers != null) ? maleFemaleIdentifiers.size() : "0") + " different gender identifiers. " +
							"At least 2 different gender identifiers should be present in the patient dataset. Please, disable the gender-basd analysis  when loading the patient dataset " +
							"or correct this issue and pelase upload a valid file."  + "<br/>");
					if(!Strings.isEmpty(checkWarnings)) {
						model.put("warningMessage", checkWarnings);
					}
					else {
						model.remove("warningMessage");
					}
					return patientData_1_specifyCSVg(model, request);
				}
				
			}
			
			if(ServerExecConfig.isOnline && ServerExecConfig.maxNumberOfPatients > 0l && (long) loadedData.data.size() >= ServerExecConfig.maxNumberOfPatients) {
				checkErrors = "ATTENTION: the Patient Data file uploaded includes a number of patients (" + loadedData.data.size() + ") greater than " + ServerExecConfig.maxNumberOfPatients + ", that is the maximum number of patients allowed to process in this server.<br/>" +
						"Please, upload a file with less patient instances.<br/> The running instance of Comorbidity4web supports analysis of Patient Datasets with a number of patients equal or smaller than " + 
						ServerExecConfig.maxNumberOfPatients + ".<br/><br/>"
						+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
						+ "to performe analysis of comorbidities over larger sets of data on your PC." + "<br/>";
				model.put("errorMessage", checkErrors);
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return patientData_1_specifyCSVg(model, request);
			}

			so.setPatientData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading patient data: " + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "<br/>";
			logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
		}
		
		if(!Strings.isEmpty(checkWarnings)) {
			model.put("warningMessage", checkWarnings);
		}
		else {
			model.remove("warningMessage");
		}

		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
		}
		else {
			model.remove("errorMessage");
		}
		
		System.gc();
		
		setMenu(model, 1, "STEP C: review uploaded data", so);
		return "patientData_3_confirmDataImport";
	}


	@PostMapping(value = "/patientData_3_confirmDataImport")
	public String patientData_3_confirmDataImportp(@ModelAttribute("md") ModelMap model, 
			@RequestParam(value = "patientIDcolumn", required = false) String patientIDcolumn,
			@RequestParam(value = "patientBirthDateColumn", required = false) String patientBirthDateColumn,
			@RequestParam(value = "patientGenderColumn", required = false) String patientGenderColumn,
			@RequestParam(value = "patientFacet1column", required = false) String patientFacet1column,
			@RequestParam(value = "patGenderAnalysisEnabled_name", required = false) String genderEnabledValue,
			@RequestParam(value = "dateFormat", required = false) String dateFormat,
			HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		setMenu(model, 1, "STEP C: review uploaded data", so);


		// Loading data
		String checkErrors = "";

		// Reset session vars
		so.setPatientIDcolumn_PD("");
		so.setPatientBirthDateColumn_PD("");
		so.setPatientGenderColumn_PD("");
		so.setPatientFacet1column_PD("");
		so.setDateFormat_PD("");


		if(!Strings.isEmpty(patientIDcolumn)) {
			so.setPatientIDcolumn_PD(patientIDcolumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Patient ID column name." + "<br/>";
		}

		if(!Strings.isEmpty(patientBirthDateColumn)) {
			so.setPatientBirthDateColumn_PD(patientBirthDateColumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Patient Birth Date column name." + "<br/>";
		}
		
		so.setGenderEnabled(false);
		if(!Strings.isEmpty(genderEnabledValue)) {
			so.setGenderEnabled(true);
			
			if(!Strings.isEmpty(patientGenderColumn)) {
				so.setPatientGenderColumn_PD(patientGenderColumn);
			}
			else {
				checkErrors += "ERROR: impossible to get Patient Gender column name." + "<br/>";
			}
		}

		if(!Strings.isEmpty(patientFacet1column) && !patientFacet1column.toLowerCase().equals("__select_optional__")) {
			so.setPatientFacet1column_PD(patientFacet1column);
		}

		if(!Strings.isEmpty(dateFormat)) {

			boolean correctDateFormat = true;

			try {
				@SuppressWarnings("unused")
				SimpleDateFormat DATEpars = new SimpleDateFormat(dateFormat);
			} catch (NullPointerException e) {
				checkErrors += "ERROR: date format exception: " + e.getMessage() + ", specify a correct the date format." + "<br/>";
				correctDateFormat = false;
			} catch (IllegalArgumentException e) {
				checkErrors += "ERROR: date format exception: " + e.getMessage() + ", specify a correct the date format." + "<br/>";
				correctDateFormat = false;
			}

			if(correctDateFormat) {
				so.setDateFormat_PD(dateFormat);
			}

		}
		else {
			checkErrors += "ERROR: impossible to get format of the Birth Date." + "<br/>";
		}
		
		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
			return patientData_2_validateCSVg(model, request);
		}
		else {
			return patientData_3_confirmDataImportg(model, request);
		}
	}

}