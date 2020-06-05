package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import es.imim.ibi.comorbidity4j.loader.VisitDataLoader;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.omop.csv.OMOPutilsCSV;

@Controller
public class VisitDataValidation extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(VisitDataValidation.class);
	
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
	
	@GetMapping(value = "/visitData_1_specifyCSV")
	public String visitData_1_specifyCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		so.resetVisitData();
		so.resetDiagnosisData();
		so.resetDescrDiagnosisData();
		so.resetDiagnosisGroupingData();
		so.resetDiagnosisPairingData();
		
		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before uploading the Visit Data file! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}
		
		System.gc();
		
		setMenu(model, 2, "STEP A: upload file", so);
		return "visitData_1_specifyCSV";
	}


	@GetMapping(value = "/visitData_2_validateCSV")
	public String visitData_2_validateCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Load vars from session
		char columnSeparatorChar = so.getColumnSeparatorChar_VD();
		char columnTextDelimiterChar = so.getColumnTextDelimiterChar_VD();
		boolean hasFirstRowHeader = so.isHasFirstRowHeader_VD();
		String result = so.getVisitData_VD();

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		// If no CSV contents go to the previous step - CSV file selection
		if(result == null || result.length() == 0) {
			checkErrors += "ERROR: while reading Visit Data file contents. Please, try to reload the file.<br/>";
			model.put("errorMessage", checkErrors);
			return visitData_1_specifyCSVg(model, request);
		}


		model.put("fileName", so.getVisitDataFileName_VD());
		model.put("fileSize", fileSizeFormatter.format(so.getVisitDataFileSize_VD()) + "");
		
		
		String lineReadingAlerts = "";
		
		// OMOP CSV pre-processing
		Reader visitDataReader = new StringReader(result); 
		if(so.isOMOP_VD()) {
			ImmutablePair<ImmutablePair<String, String>, Reader> visitListOMOPpreprocessed = OMOPutilsCSV.visitDataPreProcessor(new StringReader(result), so);
			String errorLog = visitListOMOPpreprocessed.getLeft().getLeft();
			lineReadingAlerts = (visitListOMOPpreprocessed.getLeft().getRight() != null) ? visitListOMOPpreprocessed.getLeft().getRight() : "";
			
			visitDataReader = visitListOMOPpreprocessed.getRight();
			
			if(!Strings.isEmpty(errorLog)) {
				model.put("errorMessage", checkErrors);
				model.put("warningMessage", lineReadingAlerts);
				return visitData_1_specifyCSVg(model, request);
			}
		}

		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = (columnTextDelimiterChar != 'N') ? new CSVParserBuilder().withSeparator(columnSeparatorChar).withQuoteChar(columnTextDelimiterChar).build() : new CSVParserBuilder().withSeparator(columnSeparatorChar).build();
			reader = new CSVReaderBuilder(visitDataReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while reading Visit Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
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
			checkErrors += "ERROR: while loading data from Visit Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}


		if(!Strings.isEmpty(lineReadingAlerts)) {
			model.put("warningMessage", lineReadingAlerts);
		}
		
		model.put("numberOfCSVlinesRead", visitDataCSVrecords.size());
		
		if(so.isOMOP_VD()) {
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				model.put("warningMessage", lineReadingAlerts);
				return visitData_1_specifyCSVg(model, request);
			}
			else {
				visitData_3_confirmDataImportp(model, 
						so.getPatientIDcolumn_VD(),
						so.getVisitIDcolumn_VD(),
						so.getVisitStartDateColumn_VD(),
						so.getDateFormat_VD(),
						request);
			}
		}

		// Prepare the selection of the columns
		Map<String, Integer> dateFormatCounter = new HashMap<String, Integer>();
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
		
		System.gc();
		
		setMenu(model, 2, "STEP B: select table columns", so);
		return "visitData_2_validateCSV";

	}

	@PostMapping(value = "/visitData_2_validateCSV")
	public String visitData_2_validateCSVp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Check patient data file
		so.setVisitData_VD("");
		so.setVisitDataFileSize_VD(-1l);
		so.setVisitDataFileName_VD("");
		so.setColumnSeparatorChar_VD('\t');
		so.setColumnTextDelimiterChar_VD('N');
		so.setHasFirstRowHeader_VD(true);
		
		
		// Loading CSV file
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {

			String checkErrors = "";

			@SuppressWarnings("unused")
			Reader visitDataFileReader = null;
			Part visitDataFilePart = null;
			try {
				visitDataFilePart = request.getPart("visitDataFile");
				if(visitDataFilePart != null) {
					visitDataFileReader = new InputStreamReader(visitDataFilePart.getInputStream());

					if(visitDataFilePart.getSize() <= 0l) {
						checkErrors += "ERROR: impossible to read Visit Data file content - the file is empty." + "<br/>";
					}
					else if(ServerExecConfig.isOnline && ServerExecConfig.maxMbSizeVisitDataFile > 0l && (long) visitDataFilePart.getSize() >= (ServerExecConfig.maxMbSizeVisitDataFile * 1048576l)) {
						checkErrors = "ATTENTION: the Visit Data file uploaded is about " + (((int) (visitDataFilePart.getSize() / 1048576)) + 1) + " Mb,  greater than " + ServerExecConfig.maxMbSizeVisitDataFile + "Mb, that is the maximum Patient file size allowed on this server.<br/>" + 
								"Please, upload a smaller file.<br/> The running instance of Comorbidity4web supports analysis of Visit Data files with size equal or smaller than " + ServerExecConfig.maxMbSizeVisitDataFile + " Mb.<br/><br/>"
								+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
								+ "to performe analysis of comorbidities over larger sets of data on your PC." + "<br/>";
					}

					so.setVisitDataFileSize_VD(visitDataFilePart.getSize() / 1048576l);
					so.setVisitDataFileName_VD(visitDataFilePart.getSubmittedFileName() != null ? visitDataFilePart.getSubmittedFileName() : "NO_NAME");
				}
				else {
					checkErrors += "ERROR: Visit Data file not uploaded." + "<br/>";
				}
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Visit Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			} catch (ServletException e) {
				checkErrors += "ERROR: while loading Visit Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
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
			boolean isOMOP_VD = (request.getParameter("isOMOP") == null) ? false : true;

			// If no CSV contents go to the previous step - CSV file selection
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				return visitData_1_specifyCSVg(model, request);
			}


			// Store vars on session
			so.setColumnSeparatorChar_VD(columnSeparatorChar);
			so.setColumnTextDelimiterChar_VD(columnTextDelimiterChar);
			so.setHasFirstRowHeader_VD(hasFirstRowHeader);
			so.setOMOP_VD(isOMOP_VD);
			try {
				String result = IOUtils.toString(visitDataFilePart.getInputStream(), "UTF-8");
				so.setVisitData_VD(result);
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Patient Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			}

			setMenu(model, 2, "STEP B: select table columns", so);
			return visitData_2_validateCSVg(model, request);
		}
		else {
			model.put("errorMessage", "ERROR: somehting went wrong! Please, review the form fields.");
			return visitData_1_specifyCSVg(model, request);
		}

	}


	@GetMapping(value = "/visitData_3_confirmDataImport")
	public String visitData_3_confirmDataImportg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		
		
		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";
		
		model.put("fileName", so.getVisitDataFileName_VD());
		model.put("fileSize", fileSizeFormatter.format(so.getVisitDataFileSize_VD()) + "");
		model.put("patientIDcolumn_VD", so.getPatientIDcolumn_VD());
		model.put("visitIDcolumn_VD", so.getVisitIDcolumn_VD());
		model.put("visitStartDateColumn_VD", so.getVisitStartDateColumn_VD());
		model.put("dateFormat_VD", so.getDateFormat_VD());
		
		if(Strings.isEmpty(so.getVisitDataFileName_VD())) {
			model.put("errorMessage", checkErrors + "Visit data file not selected - pelase upload a valid file."  + "<br/>");
			return visitData_1_specifyCSVg(model, request);
		}
		
		// Show stats on loaded data
		VisitDataLoader vd_loader = new VisitDataLoader();
		vd_loader.initializeParamsFromSessionObj(so);
		
		String checkWarnings = "";
		
		try {
			DataLoadContainer<List<Visit>> loadedData = vd_loader.loadDataFromReader(CharSource.wrap(so.getVisitData_VD()).openStream());
			checkErrors = (loadedData != null && !Strings.isEmpty(loadedData.errorMsg)) ? loadedData.errorMsg  + "<br/>" : "";
			checkWarnings = (loadedData != null && !Strings.isEmpty(loadedData.warningMsg)) ? loadedData.warningMsg  + "<br/>" : "";
			
			model.put("skippedLine_VD", loadedData.skippedLine_VD);
			model.put("unparsableVisitDate_VD", loadedData.unparsableVisitDate_VD);
			model.put("duplicatedVisitID_VD", loadedData.duplicatedVisitID_VD);
			
			model.put("numberVisitsLoaded", (loadedData.data != null) ? loadedData.data.size() : 0);
			
			if(loadedData.data == null || loadedData.data.size() == 0) {
				model.put("errorMessage", checkErrors + "No visit record loaded from the data - pelase upload a valid file."  + "<br/>");
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return visitData_1_specifyCSVg(model, request);
			}
			
			if(ServerExecConfig.isOnline && ServerExecConfig.maxNumberOfVisits > 0l && (long) loadedData.data.size() >= ServerExecConfig.maxNumberOfVisits) {
				checkErrors = "ATTENTION: the Visit Data file uploaded includes a number of visits (" + loadedData.data.size() + ") greater than " + ServerExecConfig.maxNumberOfVisits + ", that is the maximum number of patients allowed to process in this server.<br/>" +
						"Please, upload a file with less visit instances.<br/> The running instance of Comorbidity4web supports analysis of Patient Datasets with a number of visits equal or smaller than " + 
						ServerExecConfig.maxNumberOfVisits + ".<br/><br/>"
						+ "As an alternative, you can locally run comorbidity4j (http://comorbidity4j.readthedocs.io/) "
						+ "to performe analysis of comorbidities over larger sets of data on your PC." + "<br/>";
				model.put("errorMessage", checkErrors);
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return visitData_1_specifyCSVg(model, request);
			}
			
			so.setVisitData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading visit data: " + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "<br/>";
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
		
		setMenu(model, 2, "STEP C: review uploaded data", so);
		return "visitData_3_confirmDataImport";
	}


	@PostMapping(value = "/visitData_3_confirmDataImport")
	public String visitData_3_confirmDataImportp(@ModelAttribute("md") ModelMap model, 
			@RequestParam(value = "patientIDcolumn", required = false) String patientIDcolumn,
			@RequestParam(value = "visitIDcolumn", required = false) String visitIDcolumn,
			@RequestParam(value = "visitDateColumn", required = false) String visitDateColumn,
			@RequestParam(value = "dateFormat", required = false) String dateFormat,
			HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Loading data
		String checkErrors = "";

		// Reset session vars
		so.setPatientIDcolumn_VD("");
		so.setVisitIDcolumn_VD("");
		so.setVisitStartDateColumn_VD("");
		so.setDateFormat_VD("");

		if(!Strings.isEmpty(patientIDcolumn)) {
			so.setPatientIDcolumn_VD(patientIDcolumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Patient ID column name." + "<br/>";
		}

		if(!Strings.isEmpty(visitIDcolumn)) {
			so.setVisitIDcolumn_VD(visitIDcolumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Visit ID column name." + "<br/>";
		}

		if(!Strings.isEmpty(visitDateColumn)) {
			so.setVisitStartDateColumn_VD(visitDateColumn);
		}
		else {
			checkErrors += "ERROR: iImpossible to get Visit Date column name." + "<br/>";
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
				so.setDateFormat_VD(dateFormat);
			}

		}
		else {
			checkErrors += "ERROR: impossible to get format of the Birth Date." + "<br/>";
		}

		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
			return visitData_2_validateCSVg(model, request);
		}
		else {
			setMenu(model, 2, "STEP C: review uploaded data", so);
			return visitData_3_confirmDataImportg(model, request);
		}
	}

}