package es.imim.ibi.comorbidity4j.loader;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

/**
 * Loader of Index Disease Data
 * 
 * @author Francesco Ronzano
 *
 */
public class DescriptionDataLoader extends CoreDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(DescriptionDataLoader.class);

	// DiagnosisData column names
	public String code = "";
	public String description = "";

	public char spreadsheet_column_separator = '\t';
	public char spreadsheet_text_delimiter = 'N';

	public boolean is_first_column_header = false;

	public StringBuffer initializeParamsFromSessionObj(UserInputContainer so) {

		StringBuffer resultString = new StringBuffer("");

		// Get column names from property file
		try {
			code = so.getDiagnosisCodeColumn_DDE().trim();
			description = so.getDiagnosisDescriptionColumn_DDE().trim();
		} catch (Exception e) {
			// e.printStackTrace();
			resultString.append(GenericUtils.newLine + "ERROR: while setting column names - EXCEPTION: " + e.getMessage());
			logger.error(" > ERROR: while setting column names - EXCEPTION: " + e.getMessage());
		}

		// Setting column separator, delimiter and header
		spreadsheet_column_separator = so.getColumnSeparatorChar_DDE();
		spreadsheet_text_delimiter = so.getColumnTextDelimiterChar_DDE();
		is_first_column_header = so.isHasFirstRowHeader_DDE();

		return resultString;
	}


	public DataLoadContainer<Map<String, String>> loadDataFromReader(Reader inputReader) {

		// Return variables
		StringBuffer errorString = new StringBuffer("");
		StringBuffer warningString = new StringBuffer("");
		int skippedLineCount = 0;
		int nullOrEmptyDiagnosisDescription = 0;
		
		
		Map<String, String> codeDescriptionMap = new HashMap<String, String>();

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


		int description_index = -1;
		int code_index = -1;
		
		if(!is_first_column_header) {
			try {
				description_index = Integer.valueOf(code.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Patient ID column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
			
			try {
				code_index = Integer.valueOf(description.toLowerCase().replace("col_", ""));
			} catch (Exception e) {
				e.printStackTrace();
				errorString.append(GenericUtils.newLine + "ERROR: while identifying the index of the Visit ID column - EXCEPTION: " + e.getMessage());
				logger.error(" > EXCEPTION: " + e.getMessage());
			}
		}

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
								if(nextLine[i].trim().toLowerCase().equals(description.toLowerCase())) {
									description_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals(code.toLowerCase())) {
									code_index = i;
								}
							}
						}

						if(description_index == -1 || code_index == -1) {
							errorString.append("ERROR: " + ((description_index == -1) ? "Unable to identify the " + description + " column. " : "") +
									((code_index == -1) ? "Unable to identify the " + code + " column. " : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NLL").join(nextLine));
							break;
						}
					}
					else {
						// From second line on
						String code_value = nextLine[code_index].trim();
						String description_value = nextLine[description_index].trim();

						// Adding the map of disease code to description
						if(!Strings.isNullOrEmpty(code_value) && !Strings.isNullOrEmpty(description_value)) {
							codeDescriptionMap.put(code_value, description_value);
						}
						else {
							skippedLineCount++;
							nullOrEmptyDiagnosisDescription++;
							if(skippedLineCount <= 500) {
								warningString.append("\nERROR: while parsing line - null or empty diagnosis code or description" + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine));
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
		
		DataLoadContainer<Map<String, String>> resultC = new DataLoadContainer<Map<String, String>>();
		resultC.errorMsg = errorString.toString();
		resultC.warningMsg = warningString.toString();
		resultC.data = codeDescriptionMap;
		
		if(skippedLineCount > 500) {
			resultC.warningMsg = "A total of " + skippedLineCount + " warnings have been generated. Below you can find the first 500 warning messages: " + GenericUtils.newLine + warningString.toString();
			resultC.errorMsg += GenericUtils.newLine + "A total of " + skippedLineCount + " input lines generated a warning message. Please, consider to review the consistency of the uploaded data file.";
		}
		
		resultC.skippedLine_DDE = skippedLineCount;
		resultC.nullOrEmptyDiagnosisDescription_DDE = nullOrEmptyDiagnosisDescription;

		return resultC;
	}

}
