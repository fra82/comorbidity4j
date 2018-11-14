![Comorbidity4j](/img/logo.png)
<h1>Patient input file format</h1>

Comorbidity4j takes as input a datasets of patient data distributed across the following four **Patient input data files** describing respectively (I) patients, (II) admissions (visits), (III) diagnoses (diseases) and (IV) optionally providing extended free text descriptions of diagnoses (diseases).   
  
  
+ All these files should be provided as **TAB or comma-separated spreadsheets encoded in UTF-8**.  
+ The four spreadsheet input files can include any number of columns, provided that the set of columns defined as mandatory in this documentation is present.  
+ The first row of each spreadsheet is considered to be the header of the same spreadsheet and is exploited to assign the name to each column. The name of a specific column containing a specific type of data - provided by the first row of a spreadsheet - can be defined by the user by properly configuring the *columnName* set of properties of the configuration file - see: [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md).  
+ The value of all cells of a spreadsheet input file can include quoted ("cell_text") or non quoted (cell_text) text. In case of quoted text it is important to escape the quotation marks occurring in cell values (example "cell \" text"). In case of non quoted cell values it is important not to use the column separator character (TAB or COMMA) in the cell text.  
  
    
  
  
## Patient data file  

The **Patient data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/patientData.csv" target="_blank">download example</a>) includes the data describing each patient - one row per patient. Besides the first row, that provides column names, each row describes a different patient).  


Columns:  

+ *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
+ *patient_birth_date*: (mandatory) Birth date of the patient (format specified by the date.format property in the Property file)  
+ *patient_gender*: (mandatory) Gender of the patient (any set non-empty String can be used)  
+ *patient_facet_1*: (optional) arbitrary feature (set of strings / nominal values) useful to characterize patients (any set of non-empty String can be used - e.g.: each patient can be characterized as: DIABETIC, NOT_DIABETIC, UNKNOWN)  


Age (*patient_dateBirth*), gender (*patient_gender*) and the arbitrary patient feature (*patient_facet_1*) can be exploited to filter / stratify patient population in order to execute scoped comobidity analyses. Some examples of arbitrary patient features (*patient_facet_1* column): size of the family, ethnicity, level of education, etc.  
    
Example of **Patient data file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"patient_id"	"patient_birth_date"	"patient_gender"    "patient_facet_1"
"261"	"22-07-1947"	"F" "DIABETIC"
"823"	"01-02-1930"	"M" "NOT_DIABETIC"
```  
  
  
  
## Admission data file  

The **Admission data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/admissionData.csv" target="_blank">download example</a>) includes the data describing each admission (visit) of a patient - one row per patient visit. Besides the first row, that provides column names, each row describes a different patient admission (visit).  


Columns:  

+ *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
+ *admission_id*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
+ *admission_date*: (mandatory) Admission date of the visit / admission (format specified by the date.format property in the Property file)  


Example of **Admission data file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"patient_id"	"admission_id"	"admission_date"
"261"	"52244"	"12-03-2014"
"823"	"61004"	"21-9-2011"
```  
  
  
  
## Diagnosis data file
  
The **Diagnosis data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisData.csv" target="_blank">download example</a>) includes the data describing each disease (diagnosis) associated to a patient in the context of a specific admission (visit) - one row per association of a disease / diagnosis to a patient visit / admission. Besides the first row, that provides column names, each row describes a different association.  


Columns:  

    - *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
    - *admission_id*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
    - *diagnosis_code*: (mandatory) ID that unambiguously identifies the disease / diagnosis related to the visit / admission (any non-empty String can be used)  


Example of **Diagnosis data file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"patient_id"	"admission_id"	"diagnosis_code"
"261"	"52244"	"C0260421"
"823"	"61004"	"C0015695"
```  
  
  
## Diagnosis description file

The **Diagnosis description file** (OPTIONAL, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisDescription.csv" target="_blank">download example</a>) includes the extended free text description of diseases (diagnoses) - one row per description of a disease (diagnosis). Besides the first row, that provides column names, each row specifies the description of a specific disease (diagnosis). If this file is not provided, each disease will be referenced in the results of the comorbidity analysis only by its unique identifier specified by the *Diagnosis data file*. If you do not specify any disease description, unique identifiers (that could be ICD-9 or SNOMED ID codes) are used to refer to a disease and this fact could make it difficult to desume the semantics of the disease in the conmorbidity analysis results. It is possible to specify a description of only a subset of the diseases (diagnoses) specified by the *Diagnosis data file*.  


Columns:  

    - *diagnosis_code*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
    - *diagnosis_description*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
  
  
Example of **Diagnosis description file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"diagnosis_code"	"diagnosis_description"
"C0149654"	"conduct disorder"
"C0080274"	"Retention of urine"
```  
  
  
