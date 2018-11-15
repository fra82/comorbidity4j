<h1>Processing OMOP Common Data Model datasets</h1>

The <a href="https://www.ohdsi.org/data-standardization/" target="_blank">Observational Medical Outcomes Partnership Common Data Model (OMOP CDM)</a> defines a shared set of data structures useful to ease the integration and systematic analysis of disparate observational databases. Such data model has been developed and maintained by the Observational Health Data Sciences and Informatics (OHDSI).  
  
Comorbidity4j can execute comorbidity analysis by processing input data compliant with the <a href="https://github.com/OHDSI/CommonDataModel/wiki" target="_blank">OMOP Common Data Model specifications</a>. Such data model defines a set of tables to standardize the representation of disparate observational databases. To carry out comorbidity analyses, Comorbidity4j will rely on the following three tables of the OMOP CDM:  

+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/PERSON" target="_blank">PERSON table</a>** that, represented as a spreadsheet, constitutes the **Patient data file**. In particular, Comorbidity4j will import data from the following columns of the PERSON table:  

    - *person_id: unique identifier of the patient  
    - *year_of_birth*: the tree date-of-birth-related columns are aggregated to build the complete date of birth of the patient  
    - *month_of_birth*: the tree date-of-birth-related columns are aggregated to build the complete date of birth of the patient  
    - *day_of_birt*: the tree date-of-birth-related columns are aggregated to build the complete date of birth of the patient  
    - *gender_concept_id*: identifier of the gender of the patient  
  
In the process of interactive data upload and validation, it is possible to optionally select another column of the PERSON table in order to identify an additional arbitrary feature (set of nominal values) that will be available to characterize and stratify patients - e.g. the columns 'race_source_concept_id' or 'ethnicity_source_concept_id'.  

+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/VISIT_OCCURRENCE" target="_blank">VISIT_OCCURRENCE table</a>** that, represented as a spreadsheet, constitutes the **Visit data file**. In particular, Comorbidity4j will import data from the following columns of the VISIT_OCCURRENCE table:  

    - *person_id*: unique identifier of the patient  
    - *visit_occurrence_id*: unique identifier of the visit of the patient  
    - *gender_concept_id*: date of the visit of the patient  
  
+ the **<a href="https://github.com/OHDSI/CommonDataModel/wiki/CONDITION_OCCURRENCE" target="_blank">CONDITION_OCCURRENCE table</a>** that, represented as a spreadsheet, constitutes the **Diagnosis data file**. In particular, Comorbidity4j will import data from the following columns of the CONDITION_OCCURRENCE table:  

    - *person_id*: unique identifier of the patient  
    - *visit_occurrence_id*: unique identifier of the visit of the patient  
    - *gender_concept_id*: identifier of the disease associated to the patient during the visit  


**Using OMOP CDM tables as input to comorbidity4j**  

If you want to use the *PERSON*, *VISIT_OCCURRENCE* and *CONDITION_OCCURRENCE* OMOP CDM tables as input for Comorbidity4j, in the process of interactive data upload and validation, it is needed to mark the checkbox that highlight that the **Patient data file**, **VIsit data file** and **Diagnosis data file** are provided in OMOP CDM format.  
  
  
