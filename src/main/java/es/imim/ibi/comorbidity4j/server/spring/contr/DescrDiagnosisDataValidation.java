package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import es.imim.ibi.comorbidity4j.loader.DescriptionDataLoader;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;

@Controller
public class DescrDiagnosisDataValidation extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DescrDiagnosisDataValidation.class);
	
	private static DecimalFormat fileSizeFormatter = new DecimalFormat("##0.000");
	
	@GetMapping(value = "/descrDiagnosisData_1_specifyCSV")
	public String descrDiagnosisData_1_specifyCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		so.resetDescrDiagnosisData();
		so.resetDiagnosisGroupingData();
		so.resetDiagnosisPairingData();
		
		
		String checkErrors = "";
		if(so.getPatientData_LOADED() == null || so.getPatientData_LOADED().data == null || so.getPatientData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Patient Data file before uploading the Diagnosis Description Data file! "
					+ "Please, provide a patient data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 1, "STEP A: upload file", so);
			return "patientData_1_specifyCSV";
		}
		
		if(so.getVisitData_LOADED() == null || so.getVisitData_LOADED().data == null || so.getVisitData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Visit Data file before uploading the Diagnosis Description Data file! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 2, "STEP A: upload file", so);
			return "visitData_1_specifyCSV";
		}
		
		if(so.getDiagnosisData_LOADED() == null || so.getDiagnosisData_LOADED().data == null || so.getDiagnosisData_LOADED().data.size() == 0) {
			checkErrors += "You need to upload a valid Diagnosis Data file before uploading the Diagnosis Description Data file! "
					+ "Please, provide a visit data file by the form below.<br/>";
			model.put("errorMessage", checkErrors);
			setMenu(model, 3, "STEP A: upload file", so);
			return "diagnosisData_1_specifyCSV";
		}
		
		System.gc();
		
		setMenu(model, 4, "STEP A: upload file", so);
		return "descrDiagnosisData_1_specifyCSV";
	}


	@GetMapping(value = "/descrDiagnosisData_2_validateCSV")
	public String descrDiagnosisData_2_validateCSVg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		
		// Load vars from session
		char columnSeparatorChar = so.getColumnSeparatorChar_DDE();
		char columnTextDelimiterChar = so.getColumnTextDelimiterChar_DDE();
		boolean hasFirstRowHeader = so.isHasFirstRowHeader_DDE();
		String result = so.getDescrDiagnosisData_DDE();

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";

		// If no CSV contents go to the previous step - CSV file selection
		if(result == null || result.length() == 0) {
			checkErrors += "ERROR: while reading Diagnosis Description Data file contents. Please, try to reload the file.<br/>";
			model.put("errorMessage", checkErrors);
			return descrDiagnosisData_1_specifyCSVg(model, request);
		}


		model.put("fileName", so.getDiagnosisDataFileName_DD());
		model.put("fileSize", fileSizeFormatter.format(so.getDiagnosisDataFileSize_DD()) + "");


		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = (columnTextDelimiterChar != 'N') ? new CSVParserBuilder().withSeparator(columnSeparatorChar).withQuoteChar(columnTextDelimiterChar).build() : new CSVParserBuilder().withSeparator(columnSeparatorChar).build();
			reader = new CSVReaderBuilder(new StringReader(result)).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			checkErrors += "ERROR: while reading Diagnosis Description Data file contents. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Reading all CSV records
		String lineReadingAlerts = "";
		List<String[]> descrDiagnosisDataCSVrecords = new ArrayList<String[]>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					descrDiagnosisDataCSVrecords.add(nextLine);
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
		
		model.put("numberOfCSVlinesRead", descrDiagnosisDataCSVrecords.size());

		// Prepare the selection of the columns
		List<String> columnNameList = new ArrayList<String>();
		try {
			// Header
			List<Map<String, String>> columnList = new ArrayList<Map<String, String>>();
			for(int i = 0; i < descrDiagnosisDataCSVrecords.get(0).length; i++) {

				Map<String, String> columnStringMap = new TreeMap<String, String>();
				columnStringMap.put("title", ((hasFirstRowHeader) ? ((descrDiagnosisDataCSVrecords.get(0)[i] != null) ? descrDiagnosisDataCSVrecords.get(0)[i] : "col_" + i) : "col_" + i));
				columnStringMap.put("field", ((hasFirstRowHeader) ? ((descrDiagnosisDataCSVrecords.get(0)[i] != null) ? descrDiagnosisDataCSVrecords.get(0)[i].replaceAll("[^a-zA-Z0-9]", "_") : "col_" + i) : "col_" + i));
				columnStringMap.put("sorter", "string");
				columnStringMap.put("align", "left");

				columnNameList.add(columnStringMap.get("title"));

				columnList.add(columnStringMap);
			}
			model.put("sampleCSVcolumns", new ObjectMapper().writeValueAsString(columnList));

			// Rows
			List<Map<String, String>> sampleRowList = new ArrayList<Map<String, String>>();
			int rowNum = 0;
			for(String[] row : descrDiagnosisDataCSVrecords) {
				rowNum++;

				if(rowNum == 1 && hasFirstRowHeader) {
					continue;
				}

				if(rowNum > ((hasFirstRowHeader) ? 6 : 5)) break;

				Map<String, String> rowStringMap = new TreeMap<String, String>();
				for(int i = 0; i < row.length; i++) {
					rowStringMap.put(columnList.get(i).get("field"), row[i]);
				}
				sampleRowList.add(rowStringMap);
			}

			model.put("sampleCSVrows", new ObjectMapper().writeValueAsString(sampleRowList));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading sample data / rows from Diagnosis Description Data file. Please, try to reload the file. - EXCEPTION: " + e.getMessage() + "<br/>";
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
		
		setMenu(model, 4, "STEP B: select table columns", so);
		return "descrDiagnosisData_2_validateCSV";

	}

	@PostMapping(value = "/descrDiagnosisData_2_validateCSV")
	public String descrDiagnosisData_2_validateCSVp(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {

		UserInputContainer so = getSessionObj(request);
		

		// Check patient data file
		so.setDescrDiagnosisData_DDE("");
		so.setDescrDiagnosisDataFileSize_DDE(-1l);
		so.setDescrDiagnosisDataFileName_DDE("");
		so.setColumnSeparatorChar_DDE('\t');
		so.setColumnTextDelimiterChar_DDE('N');
		so.setHasFirstRowHeader_DDE(true);
		
		
		// Loading CSV file
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {

			String checkErrors = "";

			@SuppressWarnings("unused")
			Reader diagnosisDataFileReader = null;
			Part diagnosisDataFilePart = null;
			try {
				diagnosisDataFilePart = request.getPart("descrDiagnosisDataFile");
				if(diagnosisDataFilePart != null) {
					diagnosisDataFileReader = new InputStreamReader(diagnosisDataFilePart.getInputStream());

					if(diagnosisDataFilePart.getSize() <= 0l) {
						checkErrors += "ERROR: impossible to read Diagnosis Description Data file content - the file is empty." + "<br/>";
					}
					else if(diagnosisDataFilePart.getSize() >= (100l * 1048576l)) {
						checkErrors = "ATTENTION: the Diagnosis Description Data file uploaded is greater than 25Mb.<br/>"
								+ "Comorbidity4j will soon support the on-line analysis of large dataset of patient-disease informantion.<br/><br/>"
								+ "Currently, you can use the Java tool comorbidity4j (http://comorbidity4j.readthedocs.io/) "
								+ "to performe analysis of comorbidities over larger sets of data on your PC." + "<br/>";
					}

					so.setDescrDiagnosisDataFileSize_DDE(diagnosisDataFilePart.getSize() / 1048576l);
					so.setDescrDiagnosisDataFileName_DDE(diagnosisDataFilePart.getSubmittedFileName() != null ? diagnosisDataFilePart.getSubmittedFileName() : "NO_NAME");
				}
				else {
					checkErrors += "ERROR: Diagnosis Description Data file not uploaded." + "<br/>";
				}
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Diagnosis Description Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			} catch (ServletException e) {
				checkErrors += "ERROR: while loading Diagnosis Description Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
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


			// If no CSV contents go to the previous step - CSV file selection
			if(!Strings.isEmpty(checkErrors)) {
				model.put("errorMessage", checkErrors);
				return  descrDiagnosisData_1_specifyCSVg(model, request);
			}


			// Store vars on session
			so.setColumnSeparatorChar_DDE(columnSeparatorChar);
			so.setColumnTextDelimiterChar_DDE(columnTextDelimiterChar);
			so.setHasFirstRowHeader_DDE(hasFirstRowHeader);
			try {
				String result = IOUtils.toString(diagnosisDataFilePart.getInputStream(), "UTF-8");
				so.setDescrDiagnosisData_DDE(result);
			} catch (IOException e) {
				checkErrors += "ERROR: while loading Diagnosis Description Data file" + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "." + "<br/>";
				logger.error(" > EXCEPTION: " + ((e.getMessage() != null) ? " - " + e.getMessage() : ""));
			}

			setMenu(model, 4, "STEP B: select table columns", so);
			return descrDiagnosisData_2_validateCSVg(model, request);
		}
		else {
			model.put("errorMessage", "ERROR: somehting went wrong! Please, review the form fields.");
			return descrDiagnosisData_1_specifyCSVg(model, request);
		}

	}
	
	
	@GetMapping(value = "/descrDiagnosisData_3_confirmDataImport")
	public String descrDiagnosisData_3_confirmDataImportg(@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Retrieve backtracked errors
		String checkErrors = (model.containsAttribute("errorMessage")) ? (String) model.get("errorMessage") : "";
		
		model.put("fileName", so.getDescrDiagnosisDataFileName_DDE());
		model.put("fileSize", fileSizeFormatter.format(so.getDescrDiagnosisDataFileSize_DDE()) + "");
		model.put("diagnosisCodeColumn_DDE", so.getDiagnosisCodeColumn_DDE());
		model.put("diagnosisDescriptionColumn_DDE", so.getDiagnosisDescriptionColumn_DDE());
		
		if(Strings.isEmpty(so.getDescrDiagnosisDataFileName_DDE())) {
			model.put("errorMessage", checkErrors + "Diagnosis data file not selected - pelase upload a valid file."  + "<br/>");
			return  descrDiagnosisData_1_specifyCSVg(model, request);
		}
		
		// Show stats on loaded data
		DescriptionDataLoader dde_loader = new DescriptionDataLoader();
		dde_loader.initializeParamsFromSessionObj(so);
		
		String checkWarnings = "";
		
		try {
			DataLoadContainer<Map<String, String>> loadedData = dde_loader.loadDataFromReader(CharSource.wrap(so.getDescrDiagnosisData_DDE()).openStream());
			checkErrors = (loadedData != null && !Strings.isEmpty(loadedData.errorMsg)) ? loadedData.errorMsg  + "<br/>" : "";
			checkWarnings = (loadedData != null && !Strings.isEmpty(loadedData.warningMsg)) ? loadedData.warningMsg  + "<br/>" : "";
			
			model.put("skippedLine_DDE", loadedData.skippedLine_DDE);
			model.put("nullOrEmptyDiagnosisDescription_DDE", loadedData.nullOrEmptyDiagnosisDescription_DDE);
			
			model.put("numberDiagnosisDescrLoaded", (loadedData.data != null) ? loadedData.data.size() : 0);
			
			if(loadedData.data == null || loadedData.data.size() == 0) {
				model.put("errorMessage", checkErrors + "No diagnosis description record loaded from the data - pelase upload a valid file."  + "<br/>");
				if(!Strings.isEmpty(checkWarnings)) {
					model.put("warningMessage", checkWarnings);
				}
				else {
					model.remove("warningMessage");
				}
				return  descrDiagnosisData_1_specifyCSVg(model, request);
			}
			
			so.setDescrDiagnosisData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
			checkErrors += "ERROR: while loading diagnosis description data: " + ((e.getMessage() != null) ? " - " + e.getMessage() : "") + "<br/>";
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
		
		setMenu(model, 4, "STEP C: review uploaded data", so);
		return "descrDiagnosisData_3_confirmDataImport";
	}


	@PostMapping(value = "/descrDiagnosisData_3_confirmDataImport")
	public String descrDiagnosisData_3_confirmDataImportp(@ModelAttribute("md") ModelMap model, 
			@RequestParam(value = "diagnosisCodeColumn", required = false) String diagnosisCodeColumn,
			@RequestParam(value = "diagnosisDescriptionColumn", required = false) String diagnosisDescriptionColumn,
			HttpServletRequest request) {
		
		UserInputContainer so = getSessionObj(request);
		

		// Loading data
		String checkErrors = "";

		// Reset session vars
		so.setDiagnosisCodeColumn_DDE("");
		so.setDiagnosisDescriptionColumn_DDE("");

		if(!Strings.isEmpty(diagnosisCodeColumn)) {
			so.setDiagnosisCodeColumn_DDE(diagnosisCodeColumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Diagnosis Code column name." + "<br/>";
		}

		if(!Strings.isEmpty(diagnosisDescriptionColumn)) {
			so.setDiagnosisDescriptionColumn_DDE(diagnosisDescriptionColumn);
		}
		else {
			checkErrors += "ERROR: impossible to get Diagnosis Description column name." + "<br/>";
		}
		
		if(!Strings.isEmpty(checkErrors)) {
			model.put("errorMessage", checkErrors);
			return descrDiagnosisData_2_validateCSVg(model, request);
		}
		else {
			setMenu(model, 4, "STEP C: review uploaded data", so);
			return descrDiagnosisData_3_confirmDataImportg(model, request);
		}
	}

}