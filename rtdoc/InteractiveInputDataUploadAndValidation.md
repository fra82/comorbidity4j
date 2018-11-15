![Comorbidity4j](/img/logo.png)
<h1>Interactive upload and validation of input data</h1>
  
Once connected by means of a Web browser to Comorbidity4j (to a locally running server to to <a href="http://comorbidity.eu/comorbidity4web/" target="_blank">http://comorbidity.eu/comorbidity4web/</a>), Comorbidity4j web application drives the user troughout the interactive upload and validation of input data (see [Patient input file format](InputFileFormat.md) for an overview of the input data format).  
  
The process of input data upload and validation is articulated in four sequential steps, useful to upload respectively the **Patient data file**, the **Visits data file**, the **Diagnosis data file** and, optionally, the **Diagnosis description file**. Below you can find a detailed description of these steps.  

**Do you prefer to directly try Comorbidity4j with an example dataset? <a href="https://github.com/fra82/comorbidity4j/raw/master/example/input/comorbidity4j_example_dataset.tar.gz" target="_blank">Download it here</a> and go to <a href="http://comorbidity.eu/comorbidity4web/" target="_blank">http://comorbidity.eu/comorbidity4web/</a>. To know more about Comorbidity4web, go to: [Comorbidity4web: execute comorbidity analyses on-line](OnlineExecution.md).**  
  
  
## Uploading and validating the Patient data file  
  
The first data to upload is the **Patient data file** that describes the patients of your clinical dataset, one per row (see file format description at: [Patient input file format](InputFileFormat.md#patient-data-file) or <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/patients_comorbidity4j_example.csv" target="_blank">download an example</a>). You will be required to:  
  
+ upload the tabular file (i.e. CSV, TSV - UTF-8-encoded text file) by specifying the column separator and eventual text delimiter:  
  
![Patient data file upload](/img/input_c4web_upload_P.png)  
  
  
+ interactively select the columns that specify the *patient_id*, *patient_birth_date* and, optionally, the *patient_gender* and *other patient facets* columns and specify date format (Comorbidity4j will guess it by processing file content):  
  
![Patient data file column semantics](/img/input_c4web_columns_P.png)  
  
  
+ check the uploaded data by exploring an overview info table:  
  
![Patient data file uploaded data check](/img/input_c4web_checks_P.png)  
  
  
## Uploading and validating the Visit data file  
  
The **Visit data file** describes the visits of the patients of your clinical dataset, one visit per row (see file format description at: [Visit input file format](InputFileFormat.md#visit-data-file) or <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/visits_comorbidity4j_example.csv" target="_blank">download an example</a>). You will be required to:  
  
+ upload the tabular file (i.e. CSV, TSV - UTF-8-encoded text file) by specifying the column separator and eventual text delimiter:
  
![Visit data file upload](/img/input_c4web_upload_V.png)  
  
  
+ interactively select the columns that specify the *patient_id*, the *visit_id* and the *visit_date* columns and specify date format (Comorbidity4j will guess it by processing file content):  
  
![Visit data file column semantics](/img/input_c4web_columns_V.png)  
  
  
+ check the uploaded data by exploring an overview info table:
  
![Visit data file uploaded data check](/img/input_c4web_checks_V.png)  
  
  
## Uploading and validating the Diagnosis data file  
  
The **Diagnosis data file** describes the diagnosis associated to each visit of the patients of your clinical dataset, one visit diagnosis per row (see file format description at: [Diagnosis input file format](InputFileFormat.md#diagnosis-data-file) or <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnoses_comorbidity4j_example.csv" target="_blank">download an example</a>). You will be required to:  
  
+ upload the tabular file (i.e. CSV, TSV - UTF-8-encoded text file) by specifying the column separator and eventual text delimiter:
  
![Visit data file upload](/img/input_c4web_upload_D.png)  
  
  
+ interactively select the columns that specify the *patient_id*, the *visit_id* and the *diagnosis_code* columns:  
  
![Visit data file column semantics](/img/input_c4web_columns_D.png)  
  
  
+ check the uploaded data by exploring an overview info table:
  
![Visit data file uploaded data check](/img/input_c4web_checks_D.png)  
  
  

## Uploading and validating the Diagnosis description file  
  
The **Diagnosis description data file** describes the diagnosis associated to each visit of the patients of your clinical dataset, one visit diagnosis per row (see file format description at: [Diagnosis input file format](InputFileFormat.md#diagnosis-description-file) or <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosis_descriptions_comorbidity4j_example.csv" target="_blank">download an example</a>). You will be required to:  
  
+ upload the tabular file (i.e. CSV, TSV - UTF-8-encoded text file) by specifying the column separator and eventual text delimiter:
  
![Visit data file upload](/img/input_c4web_upload_DD.png)  
  
  
+ interactively select the columns that specify the *diagnosis_code* and the *diagnosis_description* columns:  
  
![Visit data file column semantics](/img/input_c4web_columns_DD.png)  
  
  
+ check the uploaded data by exploring an overview info table:
  
![Visit data file uploaded data check](/img/input_c4web_checks_DD.png)  
  
  
  
  
  
  
  
  



