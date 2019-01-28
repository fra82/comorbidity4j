package es.imim.ibi.comorbidity4j.loader;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

/**
 * Loader of Diagnosis Data
 * 
 * @author Francesco Ronzano
 *
 */
public class DiagnosisDataLoader extends CoreDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(DiagnosisDataLoader.class);

	// DiagnosisData column names
	public String patient_id = "";
	public String admission_id = "";
	public String diagnosis_code = "";

	public char spreadsheet_column_separator = '\t';
	public char spreadsheet_text_delimiter = 'N';

	public boolean is_first_column_header = false;
	
	public StringBuffer initializeParamsFromSessionObj(UserInputContainer so) {

		StringBuffer resultString = new StringBuffer("");

		// Get column names from property file
		try {
			patient_id = so.getPatientIDcolumn_DD().trim();
			admission_id = so.getVisitIDcolumn_DD().trim();
			diagnosis_code = so.getDiagnosisCodeColumn_DD().trim();
		} catch (Exception e) {
			// e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting column names - EXCEPTION: " + e.getMessage());
			logger.error(" > ERROR: while setting column names - EXCEPTION: " + e.getMessage());
		}

		// Setting column separator, delimiter and header
		spreadsheet_column_separator = so.getColumnSeparatorChar_DD();
		spreadsheet_text_delimiter = so.getColumnTextDelimiterChar_DD();
		is_first_column_header = so.isHasFirstRowHeader_DD();

		return resultString;
	}


	public DataLoadContainer<List<Visit>> loadDataFromReader(Reader inputReader, 
			List<Visit> visitListInput, Map<String, Integer> diagnosisCodeIdStringMap) {

		// Return variables
		StringBuffer errorString = new StringBuffer("");
		StringBuffer warningString = new StringBuffer("");
		int skippedLineCount = 0;
		int duplicatedPatVisitDiagnosis = 0;
		int unexistingPatientOrVisitID = 0;
		
		Map<String, Visit> strPatVisitToVisitMap = new HashMap<String, Visit>();
		for(Visit visitElem : visitListInput) {
			String keyStr = visitElem.getPatientStringId() + "_" + visitElem.getStrId();
			strPatVisitToVisitMap.put(keyStr, visitElem);
		}
		
		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = (spreadsheet_text_delimiter != 'N') 
					? new CSVParserBuilder().withSeparator(spreadsheet_column_separator).withQuoteChar(spreadsheet_text_delimiter).build() 
					: new CSVParserBuilder().withSeparator(spreadsheet_column_separator).build();
			reader = new CSVReaderBuilder(inputReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			errorString.append("\nERROR: while reading CSV contents - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		int patient_id_index = -1;
		int admission_id_index = -1;
		int diagnosis_code_index = -1;
		
		if(!is_first_column_header) {
			try {
				patient_id_index = Integer.valueOf(patient_id.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient ID column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
			
			try {
				admission_id_index = Integer.valueOf(admission_id.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Visit ID column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
			
			try {
				diagnosis_code_index = Integer.valueOf(diagnosis_code.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Diagnosis code column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
		}

		int diagnosisCodeCount = 0;

		Set<String> addedDiagnosisSet = new HashSet<String>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					if(is_first_column_header && lineCount == 1) {
						// Header line
						for(int i = 0; i < nextLine.length; i++) {
							if(nextLine[i] != null) {
								if(nextLine[i].trim().toLowerCase().equals(patient_id.toLowerCase())) {
									patient_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals(admission_id.toLowerCase())) {
									admission_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals(diagnosis_code.toLowerCase())) {
									diagnosis_code_index = i;
								}
							}
						}

						if(patient_id_index == -1 || admission_id_index == -1 || diagnosis_code_index == -1) {
							errorString.append("ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + patient_id + " column. " : "") +
									((admission_id_index == -1) ? "Unable to identify the " + admission_id + " column. " : "") + 
									((diagnosis_code_index == -1) ? "Unable to identify the " + diagnosis_code + " column. " : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NLL").join(nextLine));
							break;
						}
					}
					else {
						// From second line on
						String patient_id_value = nextLine[patient_id_index].trim();
						String admission_id_value = nextLine[admission_id_index].trim();
						String diagnosis_code_value = nextLine[diagnosis_code_index].trim();

						if(!Strings.isNullOrEmpty(patient_id_value) && !Strings.isNullOrEmpty(admission_id_value) && !Strings.isNullOrEmpty(diagnosis_code_value)) {
							
							// System.out.println("Checking patient_id_value: '" + patient_id_value + "' and admission_id_value: '" + admission_id_value + "'");
							
							if(strPatVisitToVisitMap.containsKey(patient_id_value + "_" + admission_id_value)) {

								// System.out.println("       > FOUND patient_id_value: '" + patient_id_value + "' and admission_id_value: '" + admission_id_value + "'");
								
								// If the diagnosis code belongs to a diagnosis group, substitute the diagnosis code string to the diagnosis group string
								/*
								, Map<String, String> diagnosisCodeStringGroupsMap
								if(diagnosisCodeStringGroupsMap != null && diagnosisCodeStringGroupsMap.size() > 0) {
									if(diagnosisCodeStringGroupsMap.containsKey(diagnosis_code_value) && !Strings.isNullOrEmpty(diagnosisCodeStringGroupsMap.get(diagnosis_code_value))) {
										diagnosis_code_value = diagnosisCodeStringGroupsMap.get(diagnosis_code_value);
									}
								}
								*/

								if(!addedDiagnosisSet.contains(patient_id_value + "_" + admission_id_value + "_" + diagnosis_code_value)) {
									Visit visitToEnrichWithDiagnosis = strPatVisitToVisitMap.get(patient_id_value + "_" + admission_id_value);

									if(!diagnosisCodeIdStringMap.containsKey(diagnosis_code_value)) {
										diagnosisCodeCount++;
										diagnosisCodeIdStringMap.put(diagnosis_code_value, diagnosisCodeCount);
									}
									
									visitToEnrichWithDiagnosis.getDiagnosisCodeSet().add(diagnosisCodeIdStringMap.get(diagnosis_code_value));

									addedDiagnosisSet.add(patient_id_value + "_" + admission_id_value + "_" + diagnosis_code_value);
								}
								else {
									skippedLineCount++;
									duplicatedPatVisitDiagnosis++;
									if(skippedLineCount <= 500) {
										warningString.append("\nATTENTION: duplicated (patient ID: " + patient_id_value + ", admission_id: " + admission_id_value + ", diagnosis code: " + diagnosis_code_value + ") "
												+ "while parsing line - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine));
									}
								}
							}
							else {

								// System.out.println("       > !!! NOT FOUND patient_id_value: '" + patient_id_value + "' and admission_id_value: '" + admission_id_value + "'");
								
								skippedLineCount++;
								unexistingPatientOrVisitID++;
								if(skippedLineCount <= 500) {
									warningString.append("\nATTENTION: the patient with ID " + patient_id_value + " does not exist or does not have any admission with ID " + admission_id_value +
											" - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine));
								}
							}

						}
						else {
							skippedLineCount++;
							if(skippedLineCount <= 500) {
								warningString.append("\nERROR: while parsing line - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine));
							}
						}
					}


				} catch (Exception e) {
					skippedLineCount++;
					if(skippedLineCount <= 500) {
						warningString.append("\nERROR: while parsing line - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " - EXCEPTION: " + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorString.append("\nERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		DataLoadContainer<List<Visit>> resultC = new DataLoadContainer<List<Visit>>();
		resultC.errorMsg = errorString.toString();
		resultC.warningMsg = warningString.toString();
		resultC.data = visitListInput;
		
		if(skippedLineCount > 500) {
			resultC.warningMsg = "A total of " + skippedLineCount + " warnings have been generated. Below you can find the first 500 warning messages: " + GenericUtils.newLine + warningString.toString();
			resultC.errorMsg += GenericUtils.newLine + "A total of " + skippedLineCount + " input lines generated a warning message. Please, consider to review the consistency of the uploaded data file.";
		}
		
		resultC.skippedLine_DD = skippedLineCount;
		resultC.duplicatedPatVisitDiagnosis_DD = duplicatedPatVisitDiagnosis;
		resultC.unexistingPatientOrVisitID_DD = unexistingPatientOrVisitID;

		return resultC;
	}

}
