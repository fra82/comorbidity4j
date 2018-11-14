![Comorbidity4j](/img/logo.png)
<h1>Patient input file format</h1>

Comorbidity4j takes as input a datasets of patient data distributed across the following four **Patient input data files** describing respectively (I) patients, (II) visits, (III) diagnoses (diseases) and (IV) optionally providing extended free text descriptions of diagnoses (diseases). It is possible to <a href="https://github.com/fra82/comorbidity4j/raw/master/example/input/comorbidity4j_example_dataset.tar.gz" target="_blank">download an example dataset</a> to inspect the input tabular files that Comorbidity4web expects as input to execute a comorbidity analysis and try the tool.     
  
  
**Input data are interactively uploaded to Comorbidity4j by means of your Web browser, thus allowing to interactively specify their format (column separator, date format, semantics of columns, etc.) and validate their contents.**   
  
+ All these files should be provided as **spreadsheets encoded in UTF-8, with columns separated by TAB, comma or vertical bar**.  
+ The four spreadsheet input files can include any number of columns, provided that the set of columns defined as mandatory in this documentation is present.  
+ The value of all cells of a spreadsheet input file can include quoted ("cell_text") or non quoted (cell_text) text. In case of quoted text it is important to escape the quotation marks occurring in cell values (example "cell \" text"). In case of non quoted cell values it is important not to use the column separator character (TAB, comma or vertical bar) in the cell text.  
  
  
  
  
## Patient data file  

The **Patient data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/patients_comorbidity4j_example.csv" target="_blank">download example</a>) rovides the data describing your patients by means of a tabular dataset (i.e. CSV, TSV - UTF-8-encoded text file).  
  
Each row of the Patient Data file should characterize a single patient by specifying, in different columns, the *patient_id*, *patient_birth_date* and, optionally, the *patient_gender* and *other patient facets* that you would like to use to stratify patients. After uploading the Patient Data file you will interactively define the type of patient-related information specified by each columns.  
  
Columns of the Patient Data file:  
+ *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
+ *patient_birth_date*: (mandatory) Birth date of the patient (the format of date will be automatically parsed while importing data)  
+ *patient_gender*: (optional) Gender of the patient (any set non-empty String can be used - at least two different value to characterize female and male patients should be present)  
+ *patient_facet_1*: (optional) arbitrary feature (set of strings / nominal values) useful to characterize patients (any set of non-empty String can be used - e.g.: each patient can be characterized as: DIABETIC, NOT_DIABETIC, UNKNOWN)  
  
The Patient data file can include an arbitrary number of columns, provided that the previous set of mandatory columns is present.  
  
Patient age (*patient_birth_date*), gender (*patient_gender*) and the arbitrary patient feature (*patient_facet_1*) can be exploited in subsequent steps of the comorbidity analysis so as to filter / stratify patient population in order to execute scoped analyses. Some examples of arbitrary patient features (patient_facet_1 column) includes size of the family (SMALL, MEDIUM, LARGE), ethnicity (ASIAN, AMERICAN, etc.), level of education (ELEMENTARY, HIGH, etc.).  
  
Example of Patient data file format (the name and format of each column are interactively specified when uploading to Comorbidity4j by means of your Web browser):  
```
"patient_id"    "patient_birth_date"    "patient_gender"    "patient_facet_1"
"261"           "22-07-1947"            "F"                 "DIABETIC"
"823"           "01-02-1930"            "M"                 "NOT_DIABETIC"
"911"           "12-12-1980"            "M"                 "NOT_DIABETIC"
```
  
  
  
  
## Visit data file  
  
The **Visit data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/visits_comorbidity4j_example.csv" target="_blank">download example</a>) provides the data describing the visits of your patients by means of a tabular dataset (i.e. CSV, TSV - UTF-8-encoded text file).  
  
Each row of the Visit Data file should characterize a single visit of a patient by specifying, in different columns, the *patient_id*, the *visit_id* and the *visit_date*. After uploading the Visit Data file you will interactively define the type of patient-related information specified by each columns.  
  
Columns of the Visit Data file:  
+ *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
+ *visit_id*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
+ *visit_date*: (mandatory) Visit date (format interactively specified by the user)  
  
The Visit data file can include an arbitrary number of columns, provided that the previous set of mandatory columns is present.  
  
Example of Visit data file format (the name and format of each column are interactively specified when uploading to Comorbidity4j by means of your Web browser):  
```
"patient_id"    "visit_id"  "visit_date"
"261"           "52244"     "12-03-2014"
"823"           "61004"     "21-09-2011"
"911"           "69054"     "12-12-2006"
```
  
  
  
  
## Diagnosis data file
  
The **Diagnosis data file** (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnoses_comorbidity4j_example.csv" target="_blank">download example</a>) provides the data describing the diagnoses spotted for each patient in the context of a specific visit by means of a tabular dataset (i.e. CSV, TSV - UTF-8-encoded text file).  
  
Each row of the Diagnosis Data file should describe the association of a diagnosis (by a diagnosis code - i.e. ICD9, SNOMED-CT, CUI or any other diagnosisidentifier) to a specific patient in a specific visit. These three parameters are provided by specifying, in different columns, the *patient_id*, the *visit_id* and the *diagnosis_code*. After uploading the Diagnosis Data file you will interactively define the type of patient-related information specified by each columns.  
  
  
Columns:  
+ *patient_id*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
+ *visit_id*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
+ *diagnosis_code*: (mandatory) ID that unambiguously identifies the disease / diagnosis related to the visit (any non-empty String can be used - i.e. ICD9, SNOMED-CT, CUI or any other identifier)  
  
The Visit Diagnosis data file can include an arbitrary number of columns, provided that the previous set of mandatory columns is present.  

Example of Diagnosis data file format (the name and format of each column are interactively specified when uploading to Comorbidity4j by means of your Web browser):  
```
"patient_id"    "admission_id"  "diagnosis_code"
"261"           "52244"         "C0260421"
"823"           "61004"         "C0015695"
"911"           "69054"         "C0015695"
```
  
  
  
  
## Diagnosis description file
  
The **Diagnosis description file** (OPTIONAL, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosis_descriptions_comorbidity4j_example.csv" target="_blank">download example</a>) provides a textual description / human-understandable explanation of all or part of the diagnosis codes mentioned in your dataset. Such information can be uploaded by a Diagnosis Description a tabular dataset (i.e. CSV, TSV - UTF-8-encoded text file).  
  
The Diagnosis Description file specifies the extended free-text description of all (or a subset of) the diagnosis codes used to reference the diagnoses of your patients. Each row of the Diagnosis Description file provides the free text description of a specific diagnosis code by means respectively of the *diagnosis_code* and the *diagnosis_description* columns. After uploading the Diagnosis Description file you will interactively define the type of patient-related information specified by each columns.  
  
Each diagnosis of your patients is referenced by means of a diagnosis code (i.e. ICD9, SNOMED-CT, CUI or any other diagnosisidentifier) specified by means of the Diagnosis data file. Sometimes the semantics of these diagnosis codes is difficult to grasp. As a consequence, by providing a Diagnosis Description file it is possible to associate a short textual description to each or part of the diagnosis codes used in your dataset: this description will be used to generate interactive visulaizations of comorbidities.  
  
Remember that if a Diagnosis Description file is not provided, in the interactive visulaizations of comorbidities, each diagnosis will be referenced by its unique identifier (diagnosis code) specified by the Diagnosis data file: this fact could make it difficult to desume the semantics of the disease in the conmorbidity analysis results. It is possible to specify a description of only a subset of the diseases (diagnoses) specified by the Diagnosis data file.  
  
  
Columns:  
+ *diagnosis_code*: (mandatory) ID that unambiguously identifies the disease / diagnosis related to the visit (any non-empty String can be used - i.e. ICD9, SNOMED-CT, CUI or any other identifier)
+ *diagnosis_description*: (mandatory) string that provides a textual description of the diagnosis code.
  
The Diagnosis Description data file can include an arbitrary number of columns, provided that the previous set of mandatory columns is present.
  
    
Example of Diagnosis Description data file format (the name and format of each column are interactively specified when uploading to Comorbidity4j by means of your Web browser):  
```
"diagnosis_code"    "diagnosis_description"
"C0149654"          "Conduct disorder"
"C0080274"          "Retention of urine"
```



includes the extended free text description of diseases (diagnoses) - one row per description of a disease (diagnosis). Besides the first row, that provides column names, each row specifies the description of a specific disease (diagnosis). If this file is not provided, each disease will be referenced in the results of the comorbidity analysis only by its unique identifier specified by the *Diagnosis data file*. If you do not specify any disease description, unique identifiers (that could be ICD-9 or SNOMED ID codes) are used to refer to a disease and this fact could make it difficult to desume the semantics of the disease in the conmorbidity analysis results. It is possible to specify a description of only a subset of the diseases (diagnoses) specified by the *Diagnosis data file*.  


Columns:  

    - *diagnosis_code*: (mandatory) ID that unambiguously identifies the patient (any non-empty String can be used)  
    - *diagnosis_description*: (mandatory) ID that unambiguously identifies the visit / admission (any non-empty String can be used)  
  
  
Example of **Diagnosis description file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"diagnosis_code"	"diagnosis_description"
"C0149654"	"conduct disorder"
"C0080274"	"Retention of urine"
```  
  
  
