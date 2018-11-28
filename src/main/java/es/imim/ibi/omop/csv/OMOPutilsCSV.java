package es.imim.ibi.omop.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

public class OMOPutilsCSV {

	private static final Logger logger = LoggerFactory.getLogger(OMOPutilsCSV.class);

	private static SimpleDateFormat DATEparsInternal = new SimpleDateFormat("dd-MM-yyyy");
	
	private static SimpleDateFormat DATEpars = new SimpleDateFormat("dd-MM-yyyy");
	public static String dateFormat = "";

	/**
	 * Pre-process the PERSON CSV file OMOP / OHDSI Common Data Model in order to convert its contents to the format accepted by Comorbidity4j
	 * to describe patients
	 *   
	 * @param inputReader
	 * @param comorbidityMinerObj
	 * @return
	 */
	public static ImmutablePair<ImmutablePair<String, String>, Reader> patientDataPreProcessor(Reader inputReader, UserInputContainer userInputCont) {
		
		int warningCounter = 0;
		
		String errorString = "";
		String warningString = "";
		
		// Get column names from property file
		userInputCont.setDateFormat_PD("dd-MM-yyyy");
		try {
			dateFormat = userInputCont.getDateFormat_PD().trim();
		} catch (Exception e) {
			e.printStackTrace();
			errorString += GenericUtils.newLine + "ERROR: while setting date format - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Setting date format
		try {
			DATEpars = new SimpleDateFormat(dateFormat);
		} catch (NullPointerException e) {
			e.printStackTrace();
			errorString += GenericUtils.newLine + "ERROR: while setting date format - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			errorString += GenericUtils.newLine + "ERROR: while setting date format - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = new CSVParserBuilder().withSeparator(userInputCont.getColumnSeparatorChar_PD()).withIgnoreQuotations(true).build();
			reader = new CSVReaderBuilder(inputReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			errorString += GenericUtils.newLine + "ERROR: while reading OMOP CSV contents - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		String patient_classification_1 = (userInputCont.getPatientFacet1column_PD() != null) ?
				userInputCont.getPatientFacet1column_PD().trim() : null;

				int patient_id_index = -1;
				int patient_dateBirth_year_index = -1;
				int patient_dateBirth_month_index = -1;
				int patient_dateBirth_day_index = -1;
				int patient_sex_index = -1;
				int patient_classification_1_index = -1;

				StringBuilder buffer = new StringBuilder();

				Set<String> addedPatientIdSet = new HashSet<String>();
				String [] nextLine;
				int lineCount = 0;
				try {
					while ((nextLine = reader.readNext()) != null) {
						try {
							lineCount++;
							if(lineCount == 1) {
								// Header line
								for(int i = 0; i < nextLine.length; i++) {
									if(nextLine[i] != null) {
										if(nextLine[i].trim().toLowerCase().equals("person_id".toLowerCase())) {
											patient_id_index = i;
										}
										else if(nextLine[i].trim().toLowerCase().equals("year_of_birth".toLowerCase())) {
											patient_dateBirth_year_index = i;
										}
										else if(nextLine[i].trim().toLowerCase().equals("month_of_birth".toLowerCase())) {
											patient_dateBirth_month_index = i;
										}
										else if(nextLine[i].trim().toLowerCase().equals("day_of_birth".toLowerCase())) {
											patient_dateBirth_day_index = i;
										}
										else if(nextLine[i].trim().toLowerCase().equals("gender_concept_id".toLowerCase())) {
											patient_sex_index = i;
										}
										else if(patient_classification_1 != null && nextLine[i].trim().toLowerCase().equals(patient_classification_1.toLowerCase())) {
											patient_classification_1_index = i;
										}
									}
								}
								
								// Setting column names to OMOP CDM values
								userInputCont.setPatientIDcolumn_PD("person_id");
								userInputCont.setPatientGenderColumn_PD("gender_concept_id");
								userInputCont.setPatientBirthDateColumn_PD("dateBirth");
								if(patient_classification_1_index != -1) {
									userInputCont.setPatientFacet1column_PD(patient_classification_1);
								}

								// Add header to output
								if(buffer.length() > 0) buffer.append("\n");
								buffer.append("person_id" + userInputCont.getColumnSeparatorChar_PD() + 
										"dateBirth" + userInputCont.getColumnSeparatorChar_PD() +
										"gender_concept_id" + 
										((patient_classification_1_index != -1) ? userInputCont.getColumnSeparatorChar_PD() + patient_classification_1 : "") );

								if(patient_id_index == -1 || patient_dateBirth_year_index == -1 || patient_dateBirth_month_index == -1 || patient_dateBirth_day_index == -1 || patient_sex_index == -1) {
									warningCounter++;
									if(warningCounter < 501) {
										warningString += GenericUtils.newLine + "ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + "person_id" + " column in the OMOP PERSON table. " : "") +
												((patient_dateBirth_year_index == -1) ? "Unable to identify the " + "year_of_birth" + " column in the OMOP PERSON table. " : "") +
												((patient_dateBirth_year_index == -1) ? "Unable to identify the " + "month_of_birth" + " column in the OMOP PERSON table. " : "") +
												((patient_dateBirth_year_index == -1) ? "Unable to identify the " + "day_of_birth" + " column in the OMOP PERSON table. " : "") +
												((patient_sex_index == -1) ? "Unable to identify the " + "gender_concept_id" + " column in the OMOP PERSON table." : "") +
												" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NLL").join(nextLine);
									}
									break;
								}
							}
							else {
								// From second line on
								String patient_id_value = nextLine[patient_id_index].trim();
								String patient_dateBirth_year_value = nextLine[patient_dateBirth_year_index].trim();
								String patient_dateBirth_month_value = nextLine[patient_dateBirth_month_index].trim();
								String patient_dateBirth_day_value = nextLine[patient_dateBirth_day_index].trim();
								String patient_sex_value = nextLine[patient_sex_index].trim();

								if(!Strings.isNullOrEmpty(patient_id_value) && 
										!Strings.isNullOrEmpty(patient_dateBirth_year_value) && 
										!Strings.isNullOrEmpty(patient_sex_value)) {

									// Check if already existing patient
									boolean patientAlreadyExisting = false;
									if(addedPatientIdSet.contains(patient_id_value)) {
										warningCounter++;
										if(warningCounter < 501) {
											warningString += GenericUtils.newLine + "ERROR: patient with ID " + patient_id_value + " already exists - parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
										}
										patientAlreadyExisting = true;
										break;
									}

									if(!patientAlreadyExisting) {

										Date parsedDate = DATEparsInternal.parse(patient_dateBirth_day_value + "-" + patient_dateBirth_month_value + "-" + patient_dateBirth_year_value);

										// Add line to output
										if(buffer.length() > 0) buffer.append("\n");
										buffer.append(patient_id_value + userInputCont.getColumnSeparatorChar_PD() + 
												DATEpars.format(parsedDate) + userInputCont.getColumnSeparatorChar_PD() +
												patient_sex_value + 
												((patient_classification_1_index != -1) ? userInputCont.getColumnSeparatorChar_PD() + nextLine[patient_classification_1_index].trim() : "") );

										addedPatientIdSet.add(patient_id_value);
									}
									else {
										warningCounter++;
										if(warningCounter < 501) {
											warningString += GenericUtils.newLine + "ATTENTION: duplicated patient ID: " + patient_id_value + ") while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
										}
									}
								}
								else {
									warningCounter++;
									if(warningCounter < 501) {
										warningString += GenericUtils.newLine + "ERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
									}
								}
							}


						} catch (Exception e) {
							warningCounter++;
							if(warningCounter < 501) {
								warningString += GenericUtils.newLine + "ERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " - EXCEPTION: " + e.getMessage();
							}
							logger.error(" > EXCEPTION: " + e.getMessage());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorString += GenericUtils.newLine + "ERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage();
					logger.error(" > EXCEPTION: " + e.getMessage());
				}

				return ImmutablePair.of(ImmutablePair.of(errorString, warningString), new BufferedReader(new StringReader(buffer.toString())));
	}


	/**
	 * Pre-process the CONDITION_OCCURRENCE CSV file OMOP / OHDSI Common Data Model in order to convert its contents to the format accepted by Comorbidity4j
	 * to describe patients
	 *   
	 * @param inputReader
	 * @param comorbidityMinerObj
	 * @return
	 */
	public static ImmutablePair<ImmutablePair<String, String>, Reader> diagnosisDataPreProcessor(Reader inputReader, UserInputContainer userInputCont) {

		int warningCounter = 0;
		
		String errorString = "";
		String warningString = "";

		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = new CSVParserBuilder().withSeparator(userInputCont.getColumnSeparatorChar_DD()).withIgnoreQuotations(true).build();
			reader = new CSVReaderBuilder(inputReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			errorString += "\nERROR: while reading OMOP CSV contents - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		int patient_id_index = -1;
		int admission_id_index = -1;
		int diagnosis_code_index = -1;

		StringBuilder buffer = new StringBuilder();

		Set<String> addedDiagnosisSet = new HashSet<String>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					
					if(lineCount == 1) {
						// Header line
						for(int i = 0; i < nextLine.length; i++) {
							if(nextLine[i] != null) {
								if(nextLine[i].trim().toLowerCase().equals("person_id".toLowerCase())) {
									patient_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals("visit_occurrence_id".toLowerCase())) {
									admission_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals("condition_concept_id".toLowerCase())) {
									diagnosis_code_index = i;
								}
							}
						}

						// Setting column names to OMOP CDM values
						userInputCont.setPatientIDcolumn_DD("person_id");
						userInputCont.setVisitIDcolumn_DD("visit_occurrence_id");
						userInputCont.setDiagnosisCodeColumn_DD("condition_concept_id");

						// Add header to output
						if(buffer.length() > 0) buffer.append("\n");
						buffer.append("person_id" + userInputCont.getColumnSeparatorChar_DD() + 
								"visit_occurrence_id" + userInputCont.getColumnSeparatorChar_DD() +
								"condition_concept_id");

						if(patient_id_index == -1 || admission_id_index == -1 || diagnosis_code_index == -1) {
							warningCounter++;
							if(warningCounter < 501) {
								warningString += "ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + "person_id" + " column in the OMOP CONDITION_OCCURRENCE table. " : "") +
									((admission_id_index == -1) ? "Unable to identify the " + "visit_occurrence_id" + " column in the OMOP CONDITION_OCCURRENCE table. " : "") + 
									((diagnosis_code_index == -1) ? "Unable to identify the " + "condition_concept_id" + " column in the OMOP CONDITION_OCCURRENCE table. " : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NLL").join(nextLine);
							}
							break;
						}
					}
					else {
						// From second line on
						String patient_id_value = nextLine[patient_id_index].trim();
						String admission_id_value = nextLine[admission_id_index].trim();
						String diagnosis_code_value = nextLine[diagnosis_code_index].trim();

						if(!Strings.isNullOrEmpty(patient_id_value) && !Strings.isNullOrEmpty(admission_id_value) && !Strings.isNullOrEmpty(diagnosis_code_value)) {

							if(!addedDiagnosisSet.contains(patient_id_value + "_" + admission_id_value + "_" + diagnosis_code_value)) {
								// Add line to output
								if(buffer.length() > 0) buffer.append("\n");
								buffer.append(patient_id_value + userInputCont.getColumnSeparatorChar_DD() + 
										admission_id_value + userInputCont.getColumnSeparatorChar_DD() +
										diagnosis_code_value);

								addedDiagnosisSet.add(patient_id_value + "_" + admission_id_value + "_" + diagnosis_code_value);
							}
							else {
								warningCounter++;
								if(warningCounter < 501) {warningString += "\nATTENTION: duplicated (patient ID: " + patient_id_value + ", admission_id: " + admission_id_value + ", diagnosis: " + diagnosis_code_value + ") "
										+ "while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
								}
							}

						}
						else {
							warningCounter++;
							if(warningCounter < 501) {
								warningString += "\nERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
							}
						}
					}


				} catch (Exception e) {
					warningCounter++;
					if(warningCounter < 501) {
						warningString += "\nERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " - EXCEPTION: " + e.getMessage();
						logger.error(" > EXCEPTION: " + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorString += "\nERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		return ImmutablePair.of(ImmutablePair.of(errorString, warningString), new BufferedReader(new StringReader(buffer.toString())));
	}



	/**
	 * Pre-process the VISIT_OCCURRENCE CSV file OMOP / OHDSI Common Data Model in order to convert its contents to the format accepted by Comorbidity4j
	 * to describe patients
	 *   
	 * @param inputReader
	 * @param comorbidityMinerObj
	 * @return
	 */
	public static ImmutablePair<ImmutablePair<String, String>, Reader> visitDataPreProcessor(Reader inputReader, UserInputContainer userInputCont) {

		int warningCounter = 0;
		
		String errorString = "";
		String warningString = "";

		// Parsing CSV
		CSVReader reader = null;
		CSVParser CSVpars = null;
		try {
			CSVpars = new CSVParserBuilder().withSeparator(userInputCont.getColumnSeparatorChar_VD()).withIgnoreQuotations(true).build();
			reader = new CSVReaderBuilder(inputReader).withCSVParser(CSVpars).build();
		} catch (Exception e) {
			e.printStackTrace();
			errorString += "\nERROR: while reading OMOP CSV contents - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}

		int patient_id_index = -1;
		int admission_id_index = -1;
		int admissionStartDate_index = -1;

		StringBuilder buffer = new StringBuilder();

		Set<String> addedVisitIdSet = new HashSet<String>();
		String [] nextLine;
		int lineCount = 0;
		try {
			while ((nextLine = reader.readNext()) != null) {
				try {
					lineCount++;
					if(lineCount == 1) {
						// Header line
						for(int i = 0; i < nextLine.length; i++) {
							if(nextLine[i] != null) {
								if(nextLine[i].trim().toLowerCase().equals("person_id".toLowerCase())) {
									patient_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals("visit_occurrence_id".toLowerCase())) {
									admission_id_index = i;
								}
								else if(nextLine[i].trim().toLowerCase().equals("visit_start_date".toLowerCase())) {
									admissionStartDate_index = i;
								}
							}
						}

						// Setting column names to OMOP CDM values
						userInputCont.setPatientIDcolumn_VD("person_id");
						userInputCont.setVisitIDcolumn_VD("visit_occurrence_id");
						userInputCont.setVisitStartDateColumn_VD("visit_start_date");

						// Add header to output
						if(buffer.length() > 0) buffer.append("\n");
						buffer.append("person_id" + userInputCont.getColumnSeparatorChar_VD() + 
								"visit_occurrence_id" + userInputCont.getColumnSeparatorChar_VD() +
								"visit_start_date");

						if(patient_id_index == -1 || admission_id_index == -1 || admissionStartDate_index == -1) {
							warningCounter++;
							if(warningCounter < 501) {
								warningString += "ERROR: " + ((patient_id_index == -1) ? "Unable to identify the " + "person_id" + " column in the OMOP VISIT_OCCURRENCE table. " : "") +
									((admission_id_index == -1) ? "Unable to identify the " + "visit_occurrence_id" + " column in the OMOP VISIT_OCCURRENCE table. " : "") + 
									((admissionStartDate_index == -1) ? "Unable to identify the " + "visit_start_date" + " column in the OMOP VISIT_OCCURRENCE table. " : "") +
									" - Parsing line " + lineCount + ": '" + Joiner.on(";").useForNull("NLL").join(nextLine);
							}
							break;
						}
					}
					else {
						// From second line on
						String patient_id_value = nextLine[patient_id_index].trim();
						String admission_id_value = nextLine[admission_id_index].trim();
						String admissionStartDate_value = nextLine[admissionStartDate_index].trim();
						
						// Guess date format
						if(Strings.isNullOrEmpty(userInputCont.getDateFormat_VD())) {
							String datef = ControllerUtil.determineDateFormat(admissionStartDate_value);
							if(!Strings.isNullOrEmpty(datef)) {
								userInputCont.setDateFormat_VD(datef);
							}
						}
						
						if(!Strings.isNullOrEmpty(patient_id_value) && !Strings.isNullOrEmpty(admission_id_value) && !Strings.isNullOrEmpty(admissionStartDate_value)) {

							// Check if already existing visit
							boolean visitAlreadyExisting = false;
							if(addedVisitIdSet.contains(admission_id_value + "_" + patient_id_value)) {
								warningCounter++;
								if(warningCounter < 501) {
									warningString += "\nERROR: visit with ID " + admission_id_value + " related to patient with ID " + patient_id_value + " already exists - parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
								}
								visitAlreadyExisting = true;
								break;
							}


							if(!visitAlreadyExisting) {
								// Add line to output
								if(buffer.length() > 0) buffer.append("\n");
								buffer.append(patient_id_value + userInputCont.getColumnSeparatorChar_VD() + 
										admission_id_value + userInputCont.getColumnSeparatorChar_VD() +
										admissionStartDate_value);

								addedVisitIdSet.add(admission_id_value + "_" + patient_id_value);
							}

						}
						else {
							warningCounter++;
							if(warningCounter < 501) {
								warningString += "\nERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine);
							}
						}
					}


				} catch (Exception e) {
					warningCounter++;
					if(warningCounter < 501) {
						warningString += "\nERROR: while parsing line " + lineCount + ": '" + Joiner.on("; ").useForNull("NLL").join(nextLine) + " - EXCEPTION: " + e.getMessage();
					}
					logger.error(" > EXCEPTION: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorString += "\nERROR:  while loading data from CSV table - EXCEPTION: " + e.getMessage();
			logger.error(" > EXCEPTION: " + e.getMessage());
		}
		
		return ImmutablePair.of(ImmutablePair.of(errorString, warningString), new BufferedReader(new StringReader(buffer.toString())));
	}


}
