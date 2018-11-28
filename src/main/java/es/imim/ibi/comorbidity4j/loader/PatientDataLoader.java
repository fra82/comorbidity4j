package es.imim.ibi.comorbidity4j.loader;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

/**
 * Loader of Patient Data
 * 
 * @author Francesco Ronzano
 *
 */
public class PatientDataLoader extends CoreDataLoader {

	private final Logger logger = LoggerFactory.getLogger(PatientDataLoader.class);

	public SimpleDateFormat DATEpars = new SimpleDateFormat("dd-MM-yyyy");
	
	// Load properties
	public String date_format = "";

	// PatientData column names
	public String patient_id = "";
	public String patient_dateBirth = "";
	public String patient_sex = "";
	public String patient_classification_1 = "";
	
	public char spreadsheet_column_separator = '\t';
	public char spreadsheet_text_delimiter = 'N';
	
	public boolean is_first_column_header = false;
	
	public StringBuffer initializeParamsFromSessionObj(UserInputContainer so) {
		
		StringBuffer resultString = new StringBuffer("");
		
		// Get column names from property file
		try {
			patient_id = so.getPatientIDcolumn_PD().trim();
			patient_dateBirth = so.getPatientBirthDateColumn_PD();
			if(so.isGenderEnabled()) {
				patient_sex = so.getPatientGenderColumn_PD();
			}
			patient_classification_1 = (so.getPatientFacet1column_PD() != null) ?
					so.getPatientFacet1column_PD().trim() : null;
		} catch (Exception e) {
			// e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting column names - EXCEPTION: " + e.getMessage());
			logger.error(" > ERROR: while setting column names - EXCEPTION: " + e.getMessage());
		}

		// Setting date format
		try {
			date_format = so.getDateFormat_PD();
			DATEpars = new SimpleDateFormat(date_format);
		} catch (NullPointerException e) {
			e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting date format - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting date format - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}
		
		// Setting column separator, delimiter and header
		spreadsheet_column_separator = so.getColumnSeparatorChar_PD();
		spreadsheet_text_delimiter = so.getColumnTextDelimiterChar_PD();
		is_first_column_header = so.isHasFirstRowHeader_PD();
		
		return resultString;
	}
	
	public DataLoadContainer<List<Patient>> loadDataFromReader(Reader inputReader, boolean genderEnabled) {

		// Return variables
		StringBuffer errorString = new StringBuffer("");
		StringBuffer warningString = new StringBuffer("");
		int skippedLineCount = 0;
		int unparsableDateCount = 0;
		int duplicatedPatientIDcount = 0;
		
		List<Patient> patientList = new ArrayList<Patient>();
		
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
			errorString.append(GenericUtils.newLine + "ERROR: while reading CSV contents - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}


		int patient_id_index = -1;
		int patient_dateBirth_index = -1;
		int patient_sex_index = -1;
		int patient_classification_1_index = -1;
		
		if(!is_first_column_header) {
			try {
				patient_id_index = Integer.valueOf(patient_id.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient ID column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
			
			try {
				patient_dateBirth_index = Integer.valueOf(patient_dateBirth.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient Birth Date column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
			
			if(genderEnabled) {
				try {
					patient_sex_index = Integer.valueOf(patient_sex.toLowerCase().replace("col_", ""));
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient Gender column - EXCEPTION: " + e.getMessage());
					logger.error(" > EXCEPTION: " + e.getMessage());
				}
			}
			
			if(!Strings.isNullOrEmpty(patient_classification_1)) {
				try {
					patient_classification_1_index = Integer.valueOf(patient_classification_1.toLowerCase().replace("col_", ""));
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient Stratification Facet column - EXCEPTION: " + e.getMessage());
					logger.error(" > EXCEPTION: " + e.getMessage());
				}
			}
		}
		
		Set<String> addedPatientIdSet = new HashSet<String>();
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
								else if(nextLine[i].trim().toLowerCase().equals(patient_dateBirth.toLowerCase())) {
									patient_dateBirth_index = i;
								}
								else if(genderEnabled && nextLine[i].trim().toLowerCase().equals(patient_sex.toLowerCase())) {
									patient_sex_index = i;
								}
								else if(!Strings.isNullOrEmpty(patient_classification_1) && nextLine[i].trim().toLowerCase().equals(patient_classification_1.toLowerCase())) {
									patient_classification_1_index = i;
								}
							}
						}

						if(patient_id_index == -1 || patient_dateBirth_index == -1 || (genderEnabled && patient_sex_index == -1)) {
							errorString.append("ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + patient_id + " column. " : "") +
									((patient_dateBirth_index == -1) ? "Unable to identify the " + patient_dateBirth + " column. " : "") + 
									((genderEnabled) ? ((patient_sex_index == -1) ? "Unable to identify the " + patient_sex + " column." : "") : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NULL").join(nextLine));
							break;
						}
					}
					else {
						// From second line on
						String patient_id_value = nextLine[patient_id_index].trim();
						String patient_dateBirth_value = nextLine[patient_dateBirth_index].trim();
						String patient_sex_value = (genderEnabled) ? nextLine[patient_sex_index].trim() : "";

						if(!Strings.isNullOrEmpty(patient_id_value) && !Strings.isNullOrEmpty(patient_dateBirth_value) && 
								(!genderEnabled || (genderEnabled && !Strings.isNullOrEmpty(patient_sex_value)) ) ) {

							// Check if already existing patient
							boolean patientAlreadyExisting = false;
							if(addedPatientIdSet.contains(patient_id_value)) {
								patientAlreadyExisting = true;
								skippedLineCount++;
								duplicatedPatientIDcount++;
								if(skippedLineCount <= 500) {
									warningString.append(GenericUtils.newLine + "WARNING: patient with ID " + patient_id_value + " already exists - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
								}
								continue;
							}

							if(!patientAlreadyExisting) {
								Patient newPatient = new Patient();
								newPatient.setStrId(patient_id_value);
								newPatient.setIntId(lineCount);
								try {
									newPatient.setBirthDate(DATEpars.parse(patient_dateBirth_value));
									
									newPatient.setSex(patient_sex_value);

									if(patient_classification_1_index != -1) {
										String patient_classification1_value = nextLine[patient_classification_1_index].trim();
										newPatient.setClassification1(patient_classification1_value);
									}

									patientList.add(newPatient);
									addedPatientIdSet.add(patient_id_value);	
								}
								catch(ParseException e) {
									skippedLineCount++;
									unparsableDateCount++;
									if(skippedLineCount <= 500) {
										warningString.append(GenericUtils.newLine + "WARNING: impossible to parse birth date of patient - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
									}
								}
							}

						}
						else {
							skippedLineCount++;
							if(skippedLineCount <= 500) {
								warningString.append(GenericUtils.newLine + "WARNING: while parsing line - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
							}
						}
					}


				} catch (Exception e) {
					skippedLineCount++;
					if(skippedLineCount <= 500) {
						warningString.append(GenericUtils.newLine + "WARNING: while parsing line - EXCEPTION: " + e.getMessage() + " - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorString.append(GenericUtils.newLine + "ERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		DataLoadContainer<List<Patient>> resultC = new DataLoadContainer<List<Patient>>();
		resultC.errorMsg = errorString.toString();
		resultC.warningMsg = warningString.toString();
		resultC.data = patientList;
		
		if(skippedLineCount > 500) {
			resultC.warningMsg = "A total of " + skippedLineCount + " warnings have been generated. Below you can find the first 500 warning messages: " + GenericUtils.newLine + warningString.toString();
			resultC.errorMsg += GenericUtils.newLine + "A total of " + skippedLineCount + " input lines generated a warning message. Please, consider to review the consistency of the uploaded data file.";
		}
		
		resultC.skippedLine_PD = skippedLineCount;
		resultC.duplicatedPatientID_PD = duplicatedPatientIDcount;
		resultC.unparsableDate_PD = unparsableDateCount;
		
		return resultC;
	}

}
