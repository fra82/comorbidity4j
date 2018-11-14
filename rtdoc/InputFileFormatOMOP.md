<h1>Processing OMOP Common Data Model datasets</h1>

The <a href="https://www.ohdsi.org/data-standardization/" target="_blank">Observational Medical Outcomes Partnership Common Data Model (OMOP CDM)</a> defines a shared set of data structures useful to ease tha integration and systematic analysis of disparate observational databases. Such data model has been developed and maintained by the Observational Health Data Sciences and Informatics (OHDSI).  
  
Comorbidity4j can execute comorbidity analysis by processing input data compliant with the <a href="https://github.com/OHDSI/CommonDataModel/wiki" target="_blank">OMOP Common Data Model specifications</a>. Such data model defines a set of tables to standardize the representation of disparate observational databases. To carry out comorbidity analyses, Comorbidity4j will rely on the following three tables:  

+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/PERSON" target="_blank">PERSON table</a>** that, represented as a spreadsheet, represents the **Patient data file**. In particular, Comorbidity4j will consider the follwong columns of the PERSON table:  

    - person_id: unique identifier of the patient  
    - year_of_birth: the tree date-of-birth-related columns are aggregated to build the complete bate of birth of the patient  
    - month_of_birth  
    - day_of_birt:  
    - gender_concept_id: identifier of the geneder of the patient  
  
It is still possible to optionally select another column (see property with name *columnName.patientData.patient_classification_1* at [Comorbidity analysis parameters](ComorbidityAnalysisParameters.md)) of the PERSON table in order to identify an additional arbitrary feature (set of nominal values) useful to characterize patients - e.g. the columns 'race_source_concept_id' or 'ethnicity_source_concept_id'.  

+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/VISIT_OCCURRENCE" target="_blank">VISIT_OCCURRENCE table</a>** that, represented as a spreadsheet, represents the **Admission data file**. In particular, Comorbidity4j will consider the follwing columns of the VISIT_OCCURRENCE table:  

    - person_id: unique identifier of the patient  
    - visit_occurrence_id: unique identifier of the visit of the patient  
    - gender_concept_id: date of the visit of the patient  
  
+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/CONDITION_OCCURRENCE" target="_blank">CONDITION_OCCURRENCE table</a>** that, represented as a spreadsheet, represents the **Diagnosis data file**. In particular, Comorbidity4j will consider the follwing columns of the CONDITION_OCCURRENCE table:  

    - person_id: unique identifier of the patient  
    - visit_occurrence_id: unique identifier of the visit of the patient  
    - gender_concept_id: identifier of the disease associated to the patient during the visit  


**Using OMOP CDM tables as input to comorbidity4j**  

In order to user the PERSON, VISIT_OCCURRENCE and CONDITION_OCCURRENCE tables as input for COmorbidity4j, you need to provide the spreadsheet of each table respectively as **Patient data file**, **Admission data file** and **Diagnosis data file** and set to true in the comorbidity analysis property file (file described in: [Comorbidity analysis parameters](ComorbidityAnalysisParameters.md)) the following three properties:  
  
  
```
###########################################################################
########## OMOP Common Data Model input data ##############################
# If input.omop.patientData is true, the patient data input file (property data.fileName.patientData) will be parsed as a CSV representation of the 
# table 'PERSON' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
# >>>>> IMPORTANT: if the input.omop.patientData property is true, the values of the three columnName.admissionData properties patient_id, patient_dateBirth and 
# >>>>> patient_gender will be ignored. It is expected as patient data input file (property data.fileName.patientData) a CSV file coherent with the 
# >>>>> OMOP Common Data Model specifications of the structure of the table 'PERSON', where the patient_id column name is 'person_id', 
# >>>>> the patient_dateBirth column name is spread across three different columns with names 'year_of_birth', 'month_of_birth', 'day_of_birth' 
# >>>>> and the patient_gender column name is equal to 'gender_concept_id'
# >>>>> The column name specified by the columnName.patientData.patient_classification_1 property value is still valid and can be set equal to the name of a column
# >>>>> of the table 'PERSON' that specifies another nominal label to associate to patients (e.g.: ehtnicity_concept_id) 
input.omop.patientData=true

# If input.omop.admissionData is true, the admission data input file (property data.fileName.admissionData) will be parsed as a CSV representation of the 
# table 'VISIT_OCCURRENCE' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
# >>>>> IMPORTANT: if the input.omop.admissionData property is true, the values of the three columnName.admissionData properties: patient_id, addmission_id 
# >>>>>and admissionStartDate will be ignored. It is expected as admission data input file (property data.fileName.admissionData) a CSV file coherent with 
# >>>>> the OMOP Common Data Model specifications of the structure of the table 'VISIT_OCCURRENCE', where the patient_id column name is 'person_id', 
# >>>>> the admission_id column name is 'visit_occurrence_id' and the admissionStartDate column name is equal to 'visit_start_date' (the 'visit_start_date' 
# >>>>> has o be in the format specified by the 'date.format' property)
input.omop.admissionData=true

# If input.omop.diagnosisData is true, the diagnosis data input file (property data.fileName.diagnosisData) will be parsed as a CSV representation of the 
# table 'CONDITION_OCCURRENCE' compliant with the OMOP Common Data Model (see TABLE COLUMN NAMES section below for more details)
# >>>>> IMPORTANT: if the input.omop.diagnosisData property is true, the values of the three columnName.diagnosisData properties: patient_id, admission_id, 
# >>>>> diagnosis_code will be ignored.
# >>>>> It is expected as diagnosis data input file (property data.fileName.diagnosisData) a CSV file coherent with the OMOP Common Data Model specifications
# >>>>> of the structure of the table 'CONDITION_OCCURRENCE', where the patient_id column name is 'person_id', the admission_id column name is 'visit_occurrence_id' 
# >>>>> and the diagnosis_code column name is equal to 'condition_concept_id'
input.omop.diagnosisData=true
```  

