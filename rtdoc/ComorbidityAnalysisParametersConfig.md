![Comorbidity4j](/img/logo.png)
<h1>Setting the parameters to execute a comorbidity analysis</h1>

The parameter to execute a comorbidity analysis by means of Comorbidity4j are determined through a **Property file**.  

The **Property file** enables users to specify the follwing parameter of a comorbidity analysis:  

+ the [full path](#paths) and the [column names](#columnNames) of the four **Input data files** ([Patient input file format](InputFileFormat.md)) and the optional **Diagnosis pairing file** ([Customize diagnosis pairing](DiagnosisPairingConfig.md))  
+ specify the [date format and column separator](#dateColumnSep) of the input **Input data files** and **Diagnosis pairing file** spreadsheets  
+ specify the [path of the output folder](#paths) where Comorgbidity4j will write the results of a comorbidity analysis ([Executing Comorbidity4j on your pc](LocalExecution.md#results))  
+ specify the [default disease pairing approach](#diseasePairing) to use when the Diagnosisis pairing file is used ([Customize diagnosis pairing](DiagnosisPairingConfig.md))  
+ define the [p-value adjustment approach](#pvalueadjust) to be applied to contemplate multiple tests significance analysis  
+ specify [female and male identifiers](#femalemaleid) for sex ratio analysis  
+ specify the [patient age computation approach](#patientage) to determine the age of patients when the set of input patients has to be filtered (see [patient filter settings](#pFilter) below)  
+ enable [multi-thread execution](#thread)  
+ consider [time directionality (temporal order of occurrence of diseases)](#directionality) when analyze comorbidity  
+ [filter the set of input patients](#pFilter) with respect to age, geneder and other facets  
+ [filter comorbidity pairs](#cFilter) form the results by considering the values of the [comorbidity scores](ComorbidityScoresComputed.md computed  
+ [enable the processing of OMOP CDM input files](#omop) compliant with the <a href="https://www.ohdsi.org/data-standardization/" target="_blank">Observational Medical Outcomes Partnership Common Data Model (OMOP CDM)</a> (see [Processing OMOP Common Data Model datasets](InputFileFormatOMOP.md))  


Below you can find an example of **Property file** where all the properties that it is possible to configure in order to carry out a comorbidity analysis are described into details.  

Download the <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/comorbidity4j.properties" target="_blank">**Property file template**</a> described into details below.  
  
  
<a name="paths"></a>
## Input file names and paths
These set of properties are useful to configure the full path of the **Input data files** ([Patient input file format](InputFileFormat.md)) and (optionally) the **Diagnosis pairing file** ([Customize diagnosis pairing](DiagnosisPairingConfig.md)). Also the path of the output folder is specified.  

```
# Full local path of the folder that contains the input data: admissionData, diagnosisData, patientData, diagnosisDescription
data.fullPath=/full/path/to/Comorbidity4jData

# Name of the three MANDATORY files that provides input data: patientData, admissionData and diagnosisData
data.fileName.patientData=patientData.csv
data.fileName.admissionData=admissionData.csv
data.fileName.diagnosisData=diagnosisData.csv
# Name of the OPTIONAL diagnosisDescription file. If left empty / not specified, no diagnosis code descriptions are considered.
data.fileName.diagnosisDescription=diagnosisDescription.csv

# Name of the OPTIONAL analyzedDiseasePairs file, useful to define how the pairs of diseases to study for comorbidities are assembled.
# If the analyzedDiseasePairs file name is left empty / not specified, all the diseases (diagnoses) specified in the diagnosisData will be paired 
# with any other disease (diagnosis) to study comorbidities.
data.fileName.analyzedDiseasePairs=analyzedDiseasePairs.csv

# Output folder to store analysis results
output.folderFullPath=/full/path/to/outputFolder
```  
  
  
<a name="columnNames"></a> 
## Table column names
Names of the columns of the **Input data files** ([Patient input file format](InputFileFormat.md)) and (optionally) the **Diagnosis pairing file** ([Customize diagnosis pairing](DiagnosisPairingConfig.md)).  

```
# Column names of spreadsheet input files: admissionData, diagnosisData, patientData, diagnosisDescription and analyzedDiseasePairs

# --- Names of columns of the file: patientData ---
# >>>>> IMPORTANT: if the input.omop.patientData property is true, the values of the following three columnName.admissionData properties will be ignored.
# >>>>> It is expected as patient data input file (property data.fileName.patientData) a CSV file coherent with the OMOP Common Data Model specifications
# >>>>> of the structure of the table 'PERSON', where the patient_id column name is 'person_id', the patient_dateBirth column name is spread across three different
# >>>>> columns with names 'year_of_birth', 'month_of_birth', 'day_of_birth' and the patient_sex column name is equal to 'sex_concept_id'
# >>>>> The column name specified by the columnName.patientData.patient_classification_1 property value is still valid and can be set equal to the name of a column
# >>>>> of the table 'PERSON' that specifies another nominal label to associate to patients (e.g.: ehtnicity_concept_id) 
# columnName.patientData.patient_id > ID that unambiguously identifies the patient (any String can be used)
columnName.patientData.patient_id=patient_id
# columnName.patientData.patient_birth_date > Birth date of the patient (format specified by the date.format property)
columnName.patientData.patient_birth_date=patient_birth_date
# columnName.patientData.patient_sex > Sex of the patient (any String can be used)
columnName.patientData.patient_sex=patient_sex
# columnName.patientData.patient_facet_1 > Optional, arbitrary feature (set of nominal values) useful to characterize patients
# (any set of non-empty String can be used - e.g.: each patient can be characterized as: DIABETIC, NOT_DIABETIC, UNKNOWN)
columnName.patientData.patient_facet_1=Race

# --- Names of columns of the file: admissionData ---
# >>>>> IMPORTANT: if the input.omop.admissionData property is true, the values of the following columnName.admissionData properties will be ignored.
# >>>>> It is expected as admission data input file (property data.fileName.admissionData) a CSV file coherent with the OMOP Common Data Model specifications
# >>>>> of the structure of the table 'VISIT_OCCURRENCE', where the patient_id column name is 'person_id', the admission_id column name is 'visit_occurrence_id' and the
# >>>>> admissionStartDate column name is equal to 'visit_start_date' (the 'visit_start_date' has o be in the format specified by the 'date.format' property)
# columnName.admissionData.patient_id > ID that unambiguously identifies the patient (any String canbe used)
columnName.admissionData.patient_id=patient_id
# columnName.admissionData.admission_id > ID that unambiguously identifies the visit / admission (any String canbe used)
columnName.admissionData.admission_id=admission_id
# columnName.admissionData.admission_date > Admission date of the visit / admission (format specified by the date.format property)
columnName.admissionData.admission_date=admission_date

# --- Names of columns of the file: diagnosisData ---
# >>>>> IMPORTANT: if the input.omop.diagnosisData property is true, the values of the following columnName.diagnosisData properties will be ignored.
# >>>>> It is expected as diagnosis data input file (property data.fileName.diagnosisData) a CSV file coherent with the OMOP Common Data Model speicification
# >>>>> of the structure of the table 'CONDITION_OCCURRENCE', where the patient_id column name is 'person_id', the admission_id column name is 'visit_occurrence_id' and the
# >>>>> diagnosis_code column name is equal to 'condition_concept_id'
# columnName.diagnosisData.patient_id > ID that unambiguously identifies the patient (any String can be used)
columnName.diagnosisData.patient_id=patient_id
# columnName.diagnosisData.admission_id > ID that unambiguously identifies the visit / admission (any String can be used)
columnName.diagnosisData.admission_id=admission_id
# columnName.diagnosisData.diagnosis_code > ID that unambiguously identifies the diagnosis / disease related to the visit / admission (any String canbe used)
columnName.diagnosisData.diagnosis_code=diagnosis_code

# --- Names of columns of the file: diagnosisDescription ---
# >>>>> IMPORTANT: if the input.omop.diagnosisData property is true the IDs that unambiguously identify the disease should be the 'condition_concept_id' used in
# >>>>> the 'CONDITION_OCCURRENCE' OMOP Common Data Model table.
# columnName.diagnosisDescription.diagnosis_code > ID that unambiguously identifies the disease to study comorbidity of (any String can be used)
columnName.diagnosisDescription.diagnosis_code=diagnosis_code
# columnName.diagnosisDescription.diagnosis_description > description of the disease that is unambiguously identified by the corresponding ID (any String can be used)
columnName.diagnosisDescription.diagnosis_description=diagnosis_description

# --- Names of columns of the file: diagnosisPairing ---
# >>>>> IMPORTANT: if the input.omop.diagnosisData property is true the IDs that unambiguously identify the disease should be the 'condition_concept_id' used in
# >>>>> the 'CONDITION_OCCURRENCE' OMOP Common Data Model table.
# columnName.diagnosisPairing.diagnosis_code > ID that unambiguously identifies the disease to study comorbidity of (any String can be used)
columnName.diagnosisPairing.diagnosis_code=diagnosis_code
# columnName.diagnosisPairing.paired_diseases > this column contains the approach to generate, starting from the  diagnosis_code specified in the same row, all the disease 
# code pairs that include that disease code and will be considered in the comorbidity analysis.
# In general, for each disease described by a row of the Index Disease file (property 'data.fileName.indexDiseaseCode'), the set of diseases to consider in order to select the 
# disease pairs to study can be specified by the paired_diseases column of the same file. This column can have one of the following values:
# - ALL_DISEASES: the disease identified by the row of the Index Disease file will be paired with all the diseases occurring in the dataset
# - INDEX_DISEASES: the disease identified by the row of the Index Disease file will be paired with all the diseases occurring in the Index Disease file
# - DIS_ID_1,DIS_ID_2,...,DIS_ID_n: each disease described by a row of the Index Disease file will be paired with the diseases with code specified in the 
#   comma separated list of disease codes
# IMPORTANT: if you leave empty such property, the approach to generate the set of the disease code pairs, starting from a disease code to study, is the one 
# specified by means of the propserty 'analysis.defaultDiseasePairingApproach' (one among ALL_DISEASES or INDEX_DISEASES)
columnName.diagnosisPairing.paired_diseases=paired_diseases
# columnName.diagnosisPairing.diagnosis_group > group code of the disease that is unambiguously identified by the ID specified by the column diagnosis_code
# If the value of the diagnosis_group column is not empty, the ID of the diseases (ID specified by the column diagnosis_code) will be substituted by such value.
# As a consequence, all the diseases (diagnoses) with the same diagnosis_group value will be merged together as a single disease (diagnosis) for comorbidity analysis.
columnName.diagnosisPairing.diagnosis_group=diagnosis_group
```   
  
  
  
<a name="dateColumnSep"></a>  
## Date format and column separator
The format of the date used in the **Input data files** as well as the column separator of the **Input data files** and the **Diagnosis pairing file**.  
  
```
# Date format used as admission date (admissionData.admissionStartDate of the Admission Data File) 
# and birth date (patientData.patient_dateBirth of the Patient Data File)
date.format=dd-MM-yyyy
# The column separator of the spreadsheet columns: supported values: TAB, COMMA
spreadsheet.columnSeparatorChar=TAB
```  
  
  
<a name="diseasePairing"></a>   
## Default disease pairing approach
If a custom approach to identify the pair of diseases to study for comorbidity is NOT defined by providing a **Diagnosis pairing file** (see [Customize diagnosis pairing](DiagnosisPairingConfig.md) - property data.fileName.diagnosisPairing), this property identifies the default way to associate to a disease mentioned in the **Diagnosis pairing file** all the other diseases to associate so as to generate pairs of diseases to study for comorbidity.  
  
```
# If the Diagnosis pairing file is used (file name specified by property data.fileName.diagnosisPairing), if in some rows of this file the value of the column 
# with name specified by the property 'columnName.diagnosisPairing.paired_diseases' is absent or has an incorrect value (empty, invalid), the following property
# 'analysis.defaultDiseasePairingApproach' is used to define the default approach to pair each disease (row) of the Diagnosis pairing file to other
# diseases so as to form the pairs to study for comorbidity. 
# This property can have one of the following values:
# - ALL_DISEASES: the disease identified by the row of the Diagnosisis pairing file will be paired with all the diseases occurring in the dataset
# - INDEX_DISEASES: the disease identified by the row of the Diagnosisis pairing file will be paired with all the diseases occurring in the Index Disease file
# If this property has an incorrect value (empty, invalid), it will be set equal to INDEX_DISEASES.
analysis.defaultDiseasePairingApproach=INDEX_DISEASES
```  


<a name="pvalueadjust"></a>  
## P-value adjustment approach
This property specifies the p-value adjustment approach for multiple testing that has to be adopted when performing comoridity analysis. To get more information about the p-value adjustment approaches available in Comorbidity4j, please refer to the documentation of the R method <a href="https://stat.ethz.ch/R-manual/R-devel/library/stats/html/p.adjust.html" target="_blank">p.adjust</a>.  

```
# One of the following approaches can be exploited to adjust p-value in case of multiple testing: 
# BONFERRONI, BENJAMINI_HOCHBERG, HOLM, HOCHBERG, BENJAMINI_YEKUTIELI, HOMMEL
# If this property (pvalAdjApproach) has an incorrect value or is left empty, it is set to the default value: BENJAMINI_HOCHBERG
pvalAdjApproach=BONFERRONI
```  


<a name="femalemaleid"></a>  
## Female and male identifiers for sex ratio analysis
This property specifies the values of the female and male identifiers used in the patient_gender column of the *Patient data file* (see [Patient input file format](InputFileFormat.md)) to specify the sex of each patient.  

```
# Identifiers of male and females used in the patient_gender column of the Patient data file, useful to compute the sex ratio parameter of each comorbidity pair
sexRatio.femaleIdentifier=F
sexRatio.maleIdentifier=M
```  



<a name="patientage"></a>  
## Patient age computation approach
This property specifies how the age fo each patient is determined when the set of input patients has to be filtered by age (see [patient filter settings](#pFilter) below).  

```   
# It is possible to filter by age patients that should be analyzed for comorbidities. The age of a patient can be determined by one of the following approaches:
# - FIRST_ADMISSION: the age of the patient at the moment (date) of the first admission (with or without diagnosis associated)
# - FIRST_DIAGNOSTIC: the age of the patient at the moment (date) of the first admission with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)
# - LAST_ADMISSION: the age of the patient at the moment (date) of the last admission (with or without diagnosis associated)
# - LAST_DIAGNOSTIC: the age of the patient at the moment (date) of the last admission with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)
# - EXECUTION_TIME: the age of the patient at execution time
# If this property (patientAgeComputation) has an incorrect value or is left empty, it is set to the default value: LAST_DIAGNOSTIC
patientAgeComputation=LAST_DIAGNOSTIC
```  



<a name="threads"></a>  
## Multi-thread execution
Enable multi-threaded comorbidity analyses and configure the number of threads.
  
```
# If multithread.enable is equal to true, multi-thread comorbidity analysis will be enabled.
# If multi-thread comorbidity analysis is enabled, the multithread.numThreads (integer value > 0) property will be used to 
# specify the number of threads. If this property is empty, two threads will be used.
multithread.enable=true
multithread.numThreads=3
```  

  
<a name="directionality"></a>  
## Time directionality analysis
Enable the time directionality in comorbidity analyses.  
When disease time directionality is not enabled (directionality.enabled=false), a patient is considered to suffer a pair of diseases if she/he experimented both of them during her/his clinical history, independently from the temporal order of the first diagnosis of each disease.  
When disease time directionality is enabled (directionality.enabled=true), a patient is considered to suffer an ordered pair of diseases if the first diagnosis of a disease precedes the first diagnosis of the second disease by a number of days equal or greater of a user defined value (directionality.minNumDays=14).  
  
```
# Directoinality: set the following variable to true to enable time directionality in comorbidity analysis
directionality.enabled=false
# The minimum number of days (integer) that should pass between two diagnosis performed in the context of two visits / admissions
# to consider them temporally different diagnoses and thus to consider such pair of diagnoses occurred in that specific temporal sequence
# in the comorbidity analysis
directionality.minNumDays=14
```  
  
  
 <a name="pFilter"></a> 
## Patient filter
Criteria to filter the set of patients that will be considered for the comorbidity analysis.  
  
```
# This properties enable the selection of patients by age, gender and other facets
# Active patient filters are applied in AND (to be selected a patient should pass all the filters)
# To set a filter as inactive, set the corresponding property to an empty string
# minAge and maxAge admit positive integer values
# gender and classification_1 are strings that are matched against the gender and classification_1 facets of patients
filter.patient.minAge=
filter.patient.maxAge=
filter.patient.facet_1=
```  
  
  
<a name="cFilter"></a> 
## Comorbidity filter
Criteria to filter the disease paris that constitutes the outcome of the comorbidity analysis, mainly based on the values of the [Comorbidity scores](ComorbidityScoresComputed.md) computed.  
  
```
# This properties enable the selection of comorbidity pairs by a set of filters applied to the comorbidity scores
# Active comorbidity filters are applied in AND (to be selected a comorbidity pair should pass all the filters)
# To set a filter as inactive, set the corresponding property to an empty string
# All these properties accept double values, except filter.score.minNumPatients_EQorGREATERthen that should be a positive integer
filter.cscore.relativeRiskIndexThresholdFS_EQorGREATERthen=
filter.cscore.fisherTestThresholdFS_EQorLOWERthen=
filter.cscore.fisherTestAsjustedThresholdFS_EQorLOWERthen=
filter.cscore.oddsRatioIndexThresholdFS_EQorGREATERthen=
filter.cscore.phiIndexThresholdFS_EQorGREATERthen=
filter.cscore.scoreThresholdFS_EQorGREATERthen=
filter.score.minNumPatients_EQorGREATERthen=
```  
  
  
<a name="omop"></a>  
If you are not [processing OMOP Common Data Model datasets](InputFileFormatOMOP.md) as input data, leave the following three properties unspecified or equal to true.  
  
## OMOP Common Data Model input data
  
```
# Leave these three properties to false if you are not considering as input spreadsheets compliant with the Observational Medical Outcomes Partnership Common Data Model.
# If input.omop.patientData is true, the patient data input file (property data.fileName.patientData) will be parsed as a CSV representation of the 
# table 'PERSON' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
input.omop.patientData=false
# If input.omop.admissionData is true, the admission data input file (property data.fileName.admissionData) will be parsed as a CSV representation of the 
# table 'VISIT_OCCURRENCE' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
input.omop.admissionData=false
# If input.omop.diagnosisData is true, the diagnosis data input file (property data.fileName.diagnosisData) will be parsed as a CSV representation of the 
# table 'CONDITION_OCCURRENCE' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
input.omop.diagnosisData=false
```  
  
  




