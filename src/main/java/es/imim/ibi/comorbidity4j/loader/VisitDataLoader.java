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

import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

/**
 * Loader of Admission Data
 * 
 * @author Francesco Ronzano
 *
 */
public class VisitDataLoader extends CoreDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(VisitDataLoader.class);

	public SimpleDateFormat DATEpars = new SimpleDateFormat("dd-MM-yyyy");

	public String date_format = "";

	// AdmissionData column names
	public String patient_id = "";
	public String admission_id = "";
	public String admissionStartDate = "";

	public char spreadsheet_column_separator = '\t';
	public char spreadsheet_text_delimiter = 'N';

	public boolean is_first_column_header = false;
	
	public StringBuffer initializeParamsFromSessionObj(UserInputContainer so) {

		StringBuffer resultString = new StringBuffer("");

		// Get column names from property file
		try {
			patient_id = so.getPatientIDcolumn_VD().trim();
			admission_id = so.getVisitIDcolumn_VD().trim();
			admissionStartDate = so.getVisitStartDateColumn_VD().trim();
		} catch (Exception e) {
			// e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting column names - EXCEPTION: " + e.getMessage());
			logger.error(" > ERROR: while setting column names - EXCEPTION: " + e.getMessage());
		}

		// Setting date format
		try {
			date_format = so.getDateFormat_VD();
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


	public DataLoadContainer<List<Visit>> loadDataFromReader(Reader inputReader) {

		// Return variables
		StringBuffer errorString = new StringBuffer("");
		StringBuffer warningString = new StringBuffer("");
		int skippedLineCount = 0;
		int unparsableVisitDateCount = 0;
		int duplicatedVisitIDcount = 0;
		
		List<Visit> visitList = new ArrayList<Visit>();

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
		int admission_id_index = -1;
		int admissionStartDate_index = -1;
		
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
				admissionStartDate_index = Integer.valueOf(admissionStartDate.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Visit date column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
		}

		Set<String> addedVisitIdSet = new HashSet<String>();
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
								else if(nextLine[i].trim().toLowerCase().equals(admissionStartDate.toLowerCase())) {
									admissionStartDate_index = i;
								}
							}
						}

						if(patient_id_index == -1 || admission_id_index == -1 || admissionStartDate_index == -1) {
							errorString.append("ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + patient_id + " column. " : "") +
									((admission_id_index == -1) ? "Unable to identify the " + admission_id + " column. " : "") + 
									((admissionStartDate_index == -1) ? "Unable to identify the " + admissionStartDate + " column. " : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NULL").join(nextLine));
							break;
						}
					}
					else {
						// From second line on
						String patient_id_value = nextLine[patient_id_index].trim();
						String admission_id_value = nextLine[admission_id_index].trim();
						String admissionStartDate_value = nextLine[admissionStartDate_index].trim();

						if(!Strings.isNullOrEmpty(patient_id_value) && !Strings.isNullOrEmpty(admission_id_value) && !Strings.isNullOrEmpty(admissionStartDate_value)) {

							// Check if already existing visit
							boolean visitAlreadyExisting = false;
							if(addedVisitIdSet.contains(admission_id_value + "_" + patient_id_value)) {
								visitAlreadyExisting = true;
								skippedLineCount++;
								duplicatedVisitIDcount++;
								if(skippedLineCount <= 500) {
									warningString.append(GenericUtils.newLine + "ERROR: visit with ID " + admission_id_value + " related to patient with ID " + patient_id_value + " already exists - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
								}
								continue;
							}
							
							if(!visitAlreadyExisting) {
								Visit newVisit = new Visit();
								newVisit.setStrId(admission_id_value);
								newVisit.setIntId(lineCount);
								try {
									newVisit.setVisitDate(DATEpars.parse(admissionStartDate_value));
									newVisit.setPatientStringId(patient_id_value.trim());
	
									visitList.add(newVisit);
									addedVisitIdSet.add(admission_id_value + "_" + patient_id_value);
								}
								catch(ParseException e) {
									skippedLineCount++;
									unparsableVisitDateCount++;
									if(skippedLineCount <= 500) {
										warningString.append(GenericUtils.newLine + "WARNING: impossible to parse visit date - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
									}
								}
							}

						}
						else {
							skippedLineCount++;
							if(skippedLineCount <= 500) {
								warningString.append(GenericUtils.newLine + "ERROR: while parsing line - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
							}
						}
					}
					
				} catch (Exception e) {
					skippedLineCount++;
					if(skippedLineCount <= 500) {
						warningString.append(GenericUtils.newLine + "ERROR: while parsing line - EXCEPTION: " + e.getMessage() + " - skipped line " + lineCount + ": '" + Joiner.on("; ").useForNull("NULL").join(nextLine));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorString.append(GenericUtils.newLine + "ERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage());
			logger.error(" > EXCEPTION: " + e.getMessage());
		}
		
		DataLoadContainer<List<Visit>> resultC = new DataLoadContainer<List<Visit>>();
		resultC.errorMsg = errorString.toString();
		resultC.warningMsg = warningString.toString();
		resultC.data = visitList;
		
		if(skippedLineCount > 500) {
			resultC.warningMsg = "A total of " + skippedLineCount + " warnings have been generated. Below you can find the first 500 warning messages: " + GenericUtils.newLine + warningString.toString();
			resultC.errorMsg += GenericUtils.newLine + "A total of " + skippedLineCount + " input lines generated a warning message. Please, consider to review the consistency of the uploaded data file.";
		}
		
		resultC.skippedLine_VD = skippedLineCount;
		resultC.unparsableVisitDate_VD = unparsableVisitDateCount;
		resultC.duplicatedVisitID_VD = duplicatedVisitIDcount;

		return resultC;
	}

}
