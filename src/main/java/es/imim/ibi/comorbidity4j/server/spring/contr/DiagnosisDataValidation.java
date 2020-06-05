package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
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
import es.imim.ibi.comorbidity4j.loader.DiagnosisDataLoader;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.omop.csv.OMOPutilsCSV;

@Controller
public class DiagnosisDataValidation extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DiagnosisDataValidation.class);
	
	private static DecimalFormat fileSizeFormatter = null;
	
	static {
		DecimalFormatSymbols otherSymbols_fileSize = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols_fileSize.setDecimalSeparator('.');
		otherSymbols_fileSize.setGroupingSeparator(',');
		fileSizeFormatter = new DecimalFormat("##0.000", otherSymbols_fileSize);
		fileSizeFormatter.setRoundingMode(RoundingMode.HALF_DOWN);
		fileSizeFormatter.setDecimalSeparatorAlwaysShown(true);
		fileSizeFormatter.setGroupingUsed(false);
	}
	
	@GetMapping(value = "/diagnosisData_1_specifyCSV")
	public String diagnosisData_1_specifyCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		so.resetDiagnosisData();
		so.resetDescrDiagnosisData();
		so.resetDiagnosisGroupingData();
		so.resetDiagnosisPairingData();
		
		
		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before uploading the Diagnosis Data file! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}
		
		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before uploading the Diagnosis Data file! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}
		
		System.gc();
		
		setMenu(model, 3, "STEP A: upload file", so);
		return "diagnosisData_1_specifyCSV";
	}


	@GetMapping(value = "/diagnosisData_2_validateCSV")
	public String diagnosisData_2_validateCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		

		// Load vars from session
		char columnSeparatorChar = so.getColumnSeparatorChar_DD();
		char columnTextDelimiterChar = so.getColumnTextDelimiterChar_DD();
		boolean hasFirstRowHeader = so.isHasFirstRowHeader_DD();
		String result = so.getDiagnosisData_DD();

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		// If no CSV contents go to the previous step - CSV file selection
		if(result == null || result.length() == 0) {
			checkErrors += "ERROR: while reading Diagnosis Data file contents. Please, try to reload the file.<br/>";
			model.put("errorMessage", checkErrors);
			return diagnosisData_1_specifyCSVg(model, request);
		}


		model.put("fileName", so.getDiagnosisDataFileName_DD());
		model.put("fileSize", fileSizeFormatter.format(so.getDiagnosisDataFileSize_DD()) + "");

		String lineReadingAlerts = "";
		
		// OMOP CSV pre-processing
		Reader diagnosisDataReader = new StringReader(result); 
		if(so.isOMOP_VD()) {
			ImmutablePair<ImmutablePair<String, String>, Reader> visitListOMOPpreprocessed = OMOPutilsCSV.diagnosisDataPreProcessor(new StringReader(result), so);
			String errorLog = visitListOMOPpreprocessed.getLeft().getLeft();
			lineReadingAlerts = (visitListOMOPpreprocessed.getLeft().getRight() != null) ? visitListOMOPpreprocessed.getLeft().getRight() : "";
			
			diagnosisDataReader = visitListOMOPpreprocessed.getRight();
			
			if(!Strings.isEmpty(errorLog)) {
				model.put("errorMessage", checkErrors);
				model.put("warningMessage", lineReadingAlerts);
				return diagnosisData_1_specifyCSVg(model, request);
			}
		}
		
		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = (columnTextDelimiterChar != 'N') ? new CSVParserBuilder().withSeparator(columnSeparatorChar).withQuoteChar(columnTextDelimiterChar).build() : new CSVParserBuilder().withSeparator(columnSeparatorChar).build();
			reader = new CSVReaderBuilder(diagnosisDataReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while reading Diagnosis Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Reading all CSV records
		List<String[]> visitDataCSVrecords = new ArrayList<String[]>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					visitDataCSVrecords.add(nextLine);
				} catch (Exception e) {
					e.printStackTrace();
					lineReadingAlerts += "WARNING: exception while reading line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " of Patient Data file. - EXCEPTION: " + e.getMessage() + "<br/>";
					logger.error(" > EXCEPTION: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading data from Diagnosis Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}


		if(!Strings.isEmpty(lineReadingAlerts)) {
			model.put("warningMessage", lineReadingAlerts);
		}
		
		model.put("numberOfCSVlinesRead", visitDataCSVrecords.size());
		
		if(so.isOMOP_DD()) {
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				model.put("warningMessage", lineReadingAlerts);
				return diagnosisData_1_specifyCSVg(model, request);
			}
			else {
				diagnosisData_3_confirmDataImportp(model, 
						so.getPatientIDcolumn_DD(),
						so.getVisitIDcolumn_DD(),
						so.getDiagnosisCodeColumn_DD(),
						request);
			}
		}


		// Prepare the selection of the columns
		List<String> columnNameList = new ArrayList<String>();
		try {
			// Header
			List<Map<String, String>> columnList = new ArrayList<Map<String, String>>();
			for(int i = 0; i < visitDataCSVrecords.get(0).length; i++) {

				Map<String, String> columnStringMap = new TreeMap<String, String>();
				columnStringMap.put("title", ((hasFirstRowHeader) ? ((visitDataCSVrecords.get(0)[i] != null) ? visitDataCSVrecords.get(0)[i] : "col_" + i) : "col_" + i));
				columnStringMap.put("field", ((hasFirstRowHeader) ? ((visitDataCSVrecords.get(0)[i] != null) ? visitDataCSVrecords.get(0)[i].replaceAll("[^a-zA-Z0-9]", "_") : "col_" + i) : "col_" + i));
				columnStringMap.put("sorter", "string");
				columnStringMap.put("align", "left");

				columnNameList.add(columnStringMap.get("title"));

				columnList.add(columnStringMap);
			}
			model.put("sampleCSVcolumns", new ObjectMapper().writeValueAsString(columnList));

			// Rows
			List<Map<String, String>> sampleRowList = new ArrayList<Map<String, String>>();
			int rowNum = 0;
			for(String[] row : visitDataCSVrecords) {
				rowNum++;

				if(rowNum == 1 && hasFirstRowHeader) {
					continue;
				}

				if(rowNum > ((hasFirstRowHeader) ? 6 : 5)) break;

				Map<String, String> rowStringMap = new TreeMap<String, String>();
				for(int i = 0; i < row.length; i++) {
					String headerName = (i < columnList.size()) ? columnList.get(i).get("field") : "_UNDEFINED_COLUMN_NAME_" + i;
					rowStringMap.put(headerName, row[i]);
				}
				sampleRowList.add(rowStringMap);
			}

			model.put("sampleCSVrows", new ObjectMapper().writeValueAsString(sampleRowList));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading sample data / rows from Diagnosis Data file. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}
		
		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
		}
		else {
			model.remove("errorMessage");
		}

		model.put("columnNameList", columnNameList);
		
		System.gc();
		
		setMenu(model, 3, "STEP B: select table columns", so);
		return "diagnosisData_2_validateCSV";
	}

	@PostMapping(value = "/diagnosisData_2_validateCSV")
	public String diagnosisData_2_validateCSVp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		
		
		// Check patient data file
		so.setDiagnosisData_DD("");
		so.setDiagnosisDataFileSize_DD(-1l);
		so.setDiagnosisDataFileName_DD("");
		so.setColumnSeparatorChar_DD('\t');
		so.setColumnTextDelimiterChar_DD('N');
		so.setHasFirstRowHeader_DD(true);
		
		
		// Loading CSV file
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {

			String checkErrors = "";

			@SuppressWarnings("unused")
			Reader diagnosisDataFileReader = null;
			Part diagnosisDataFilePart = null;
			try {
				diagnosisDataFilePart = request.getPart("diagnosisDataFile");
				if(diagnosisDataFilePart != null) {
					diagnosisDataFileReader = new InputStreamReader(diagnosisDataFilePart.getInputStream());

					if(diagnosisDataFilePart.getSize() <= 0l) {
						checkErrors += "ERROR: impossible to read Diagnosis Data file content - the file is empty." + "<br/>";
					}
					else if(ServerExecConfig.isOnline && ServerExecConfig.maxMbSizeDiagnosisDataFile > 0l && (long) diagnosisDataFilePart.getSize() >= (ServerExecConfig.maxMbSizeDiagnosisDataFile * 1048576l)) {
						checkErrors = "ATTENTION: the Diagnosis Data file uploaded is about " + (((int) (diagnosisDataFilePart.getSize() / 1048576)) + 1) + " Mb,  greater than " + ServerExecConfig.maxMbSizeDiagnosisDataFile + "Mb, that is the maximum Diagnosis file size allowed on this server.<br/>" +
								"Please, upload a smaller file.<br/>The running instance of Comorbidity4web supports analysis of Diagnosis Data files with size equal or smaller than " + ServerExecConfig.maxMbSizeDiagnosisDataFile + " Mb.<br/><br/>"
								+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
								+ "to performe analysis of comorbidities over larger sets of data on your PC (wihhout this limitation)." + "<br/>";
					}

					so.setDiagnosisDataFileSize_DD(diagnosisDataFilePart.getSize() / 1048576l);
					so.setDiagnosisDataFileName_DD(diagnosisDataFilePart.getSubmittedFileName() != null ? diagnosisDataFilePart.getSubmittedFileName() : "NO_NAME");
				}
				else {
					checkErrors += "ERROR: Diagnosis Data file not uploaded." + "<br/>";
				}
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Diagnosis Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			} catch (ServletException e) {
				checkErrors += "ERROR: while loading Diagnosis Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
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
			
			// Check if the Visit Data are in OMOP format
			boolean isOMOP_DD = (request.getParameter("isOMOP") == null) ? false : true;
			
			// If no CSV contents go to the previous step - CSV file selection
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				return diagnosisData_1_specifyCSVg(model, request);
			}


			// Store vars on session
			so.setColumnSeparatorChar_DD(columnSeparatorChar);
			so.setColumnTextDelimiterChar_DD(columnTextDelimiterChar);
			so.setHasFirstRowHeader_DD(hasFirstRowHeader);
			so.setOMOP_DD(isOMOP_DD);
			try {
				String result = IOUtils.toString(diagnosisDataFilePart.getInputStream(), "UTF-8");
				so.setDiagnosisData_DD(result);
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Diagnosis Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			}

			setMenu(model, 3, "STEP B: select table columns", so);
			return diagnosisData_2_validateCSVg(model, request);
		}
		else {
			model.put("errorMessage", "ERROR: somehting went wrong! Please, review the form fields.");
			return diagnosisData_1_specifyCSVg(model, request);
		}

	}


	@GetMapping(value = "/diagnosisData_3_confirmDataImport")
	public String diagnosisData_3_confirmDataImportg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";
		
		model.put("fileName", so.getDiagnosisDataFileName_DD());
		model.put("fileSize", fileSizeFormatter.format(so.getDiagnosisDataFileSize_DD()) + "");
		model.put("patientIDcolumn_DD", so.getPatientIDcolumn_DD());
		model.put("visitIDcolumn_DD", so.getVisitIDcolumn_DD());
		model.put("diagnosisCodeColumn_DD", so.getDiagnosisCodeColumn_DD());
		
		if(Strings.isEmpty(so.getDiagnosisDataFileName_DD())) {
			model.put("errorMessage", checkErrors + "Diagnosis data file not selected - pelase upload a valid file."  + "<br/>");
			return diagnosisData_1_specifyCSVg(model, request);
		}
		
		// Show stats on loaded data
		DiagnosisDataLoader dd_loader = new DiagnosisDataLoader();
		dd_loader.initializeParamsFromSessionObj(so);
		
		String checkWarnings = "";
		
		try {
			Map<String, Integer> diagnosisCodeIdStringMap = new HashMap<String, Integer>();
			DataLoadContainer<List<Visit>> loadedData = dd_loader.loadDataFromReader(CharSource.wrap(so.getDiagnosisData_DD()).openStream(),
					so.getVisitData_LOADED().data, diagnosisCodeIdStringMap);
			checkErrors = (loadedData != null && !Strings.isEmpty(loadedData.errorMsg)) ? loadedData.errorMsg  + "<br/>" : "";
			checkWarnings = (loadedData != null && !Strings.isEmpty(loadedData.warningMsg)) ? loadedData.warningMsg  + "<br/>" : "";
			
			model.put("skippedLine_DD", loadedData.skippedLine_DD);
			model.put("duplicatedPatVisitDiagnosis_DD", loadedData.duplicatedPatVisitDiagnosis_DD);
			model.put("unexistingPatientOrVisitID_DD", loadedData.unexistingPatientOrVisitID_DD);
			
			if(loadedData.data == null || loadedData.data.size() == 0) {
				model.put("errorMessage", checkErrors + "No diagnosis record loaded from the data - pelase upload a valid file."  + "<br/>");
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return diagnosisData_1_specifyCSVg(model, request);
			}
			
			so.setDiagnosisData_LOADED(loadedData);
			so.setDiagnosisCodeIdStringMap(diagnosisCodeIdStringMap);
			
			int numberOfDiagnosesLoaded = ControllerUtil.countPatientDiagnoses(so);
			model.put("numberDiagnosisLoaded", numberOfDiagnosesLoaded + "");
			
			if(ServerExecConfig.isOnline && ServerExecConfig.maxNumberOfDiagnoses > 0l && (long) numberOfDiagnosesLoaded >= ServerExecConfig.maxNumberOfDiagnoses) {
				checkErrors = "ATTENTION: the Diagnosis Data file uploaded includes a number of diagnoses (" + numberOfDiagnosesLoaded + ") greater than " + ServerExecConfig.maxNumberOfDiagnoses + ", that is the maximum number of diagnoses allowed to process in this server.<br/>" +
						"Please, upload a file with less diagnosis-patients associations.<br/> The running instance of Comorbidity4web supports analysis of Diagnosis Datasets with a number of diagnosis-patient associations equal or smaller than " + 
						ServerExecConfig.maxNumberOfDiagnoses + ".<br/><br/>"
						+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
						+ "to performe analysis of comorbidities over larger sets of data on your PC (wihhout this limitation)." + "<br/>";
				model.put("errorMessage", checkErrors);
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return diagnosisData_1_specifyCSVg(model, request);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading diagnosis data: " + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "<br/>";
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
		
		setMenu(model, 3, "STEP C: review uploaded data", so);
		return "diagnosisData_3_confirmDataImport";
	}


	@PostMapping(value = "/diagnosisData_3_confirmDataImport")
	public String diagnosisData_3_confirmDataImportp(@ModelAttribute("md") ModelMap model, 
			@RequestParam(value = "patientIDcolumn", required = false) String patientIDcolumn,
			@RequestParam(value = "visitIDcolumn", required = false) String visitIDcolumn,
			@RequestParam(value = "diagnosisCodeColumn", required = false) String diagnosisCodeColumn,
			HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		
		
		// Loading data
		String checkErrors = "";

		// Reset session vars
		so.setPatientIDcolumn_DD("");
		so.setVisitIDcolumn_DD("");
		so.setDiagnosisCodeColumn_DD("");

		if(!Strings.isEmpty(patientIDcolumn)) {
			so.setPatientIDcolumn_DD(patientIDcolumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Patient ID column name." + "<br/>";
		}

		if(!Strings.isEmpty(visitIDcolumn)) {
			so.setVisitIDcolumn_DD(visitIDcolumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Visit ID column name." + "<br/>";
		}

		if(!Strings.isEmpty(diagnosisCodeColumn)) {
			so.setDiagnosisCodeColumn_DD(diagnosisCodeColumn);
		}
		else {
			checkErrors += "ERROR: iImpossible to get Diagnosis Code column name." + "<br/>";
		}
		
		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
			return diagnosisData_2_validateCSVg(model, request);
		}
		else {
			setMenu(model, 3, "STEP C: review uploaded data", so);
			return diagnosisData_3_confirmDataImportg(model, request);
		}
	}

}