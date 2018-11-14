![Comorbidity4j](/img/logo.png)
<h1>Overview of Comorbidity4j</h1>

Comorbidity4j is a Java tool that efficiently carries out comorbidity analyses over patient-disease datasets.  

![Comorbidity analysis](/img/como_analysis.png)

**Input data** are provided by a set of spreadsheets useful to describe patients and diseases (referred to as *Patient input files*) and a *Property file* useful to customize several aspects of the analysis of comorbidity. It is possible to provide an optional file to customize the set of disease pairs to study for relevant comorbidities (referred to as *Diagnosis pairing file*). To get more details, go to [Prepare your comorbidity analysis data (section below)](#prepareData).  

Once you have prepared the input dataset and configuration file you can **[execute a comorbidity analysis (section below)](#analysisExecution)** locally on your PC. Comorbidity analyses over small-to-medium sized patient-disease datasets can be executed online by means of the [Comorbidity4web](OnlineExecution.md) service.  
  
The **results of an analysis** of comorbidity includes:  

+ a [Comorbidity table](ComorbidityTable.md) that provides for each pair of diseases a set of [Comorbidity scores](ComorbidityScoresComputed.md)  
+ a set of [Web-based interactive visualizations](InteractiveVisualizations.md) useful to explore several facets of the input patient-disease dataset as well as to apply further filters on the results of comorbidity analyses and visualize these results by means of interactive heatmaps and network graphs  
    
The following image visually shows the main capabilities of Comorbidity4j:  

![Comorbidity4j functional architecture](/img/overview_all.png)


<a name="prepareData"></a>
## Prepare your comorbidity analysis data

Comorbidity4j needs the following input data to carry out a comorbidity analysis:  
  
+ **Patient input files**: describing the *demographic information* (birth-date, gender and, optionally secondary patient features like education level, ethnicity, etc.) and the *history of diseases* of a set of patients. See: [Patient input file format](InputFileFormat.md) for detailed information on the format of each file. In general, the set of Patient input files includes the following four spreadsheets:  
    - **Patient data file**: (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/patientData.csv" target="_blank">download example</a>) list of patients, one per row. Each patient is described by a unique identifier and a set of *demographic information* (birth-date, gender and, optionally secondary patient features like education level, ethnicity, etc.)  
    - **Admission data file**: (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/admissionData.csv" target="_blank">download example</a>) list of visits (admissions) of a patient. Each visit of a patient is described by a unique identifier, the unique identifier of the patient who was attended and the date of the visit  
    - **Diagnosis data file**: (MANDATORY, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisData.csv" target="_blank">download example</a>) list of diseases (diagnoses) that were discovered for a patient in the context of a specific visit (admission). Each disease (diagnosis) includes the unique identifier of the patient, the unique identifier of the visit (admission) and the unique identifier of the disease (diagnosis)  
    - **Diagnosis description file**: (OPTIONAL, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisDescription.csv" target="_blank">download example</a>) list of extended free text description of unique identifiers of diseases (diagnoses). Each row associates the unique identifier of a disease (diagnosis) to an extended free text description of the same disease. If this file is not provided, each disease will be referenced in the results of the comorbidity analysis by its unique identifier (that could be a code (ICD-9, SNOMED ID) from which it is difficult to derive the semantics of the disease  
+ **Diagnosis pairing file**: (OPTIONAL, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisPairing.csv" target="_blank">download example</a>) if provided, this spreadsheet is useful to: (1) customize the disese pairs that will be considered in the analysis of comorbidity; (2) rename one or more diseases (diagnosis) in order to consider them as a single disease for comorbidity analysis. If this file is not provided, all the possible pairs of diseases mentioned in the Patient input files will be considered. See: [Customize diagnosis pairing](DiagnosisPairingConfig.md) for detailed information on the format of this file.   
+ **Property file**: (<a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/comorbidity4j.properties" target="_blank">download template</a>) a configuration file that specifies the parameters (properties) useful to carry out the analysis of comorbidity. By means of this file it is possible to specify the path of the previous set of spreadsheets (both Patient input files and Diagnosis pairing file), enable time directionality analysis, enable and configure multi-threading, enable the support for OMOP Common Data Model input files, choose the p-value adjustment approach in comorbidity analysis, activate filter over input data and output comorbidity scores, etc. See: [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md) for detailed information on the format of this file.   
  
Comorbidity4j can read **Patient input files** in a format compliant with the <a href="https://www.ohdsi.org/data-standardization/" target="_blank">Observational Medical Outcomes Partnership Common Data Model (OMOP CDM)</a> as described into details at [Processing OMOP Common Data Model datasets](InputFileFormatOMOP.md).  
    
  
<a name="analysisExecution"></a>
## Execute a comorbidity analysis
  
Comorbidity4j can be executed in any PC where Java 1.8 or newer is available. Once you have prepared your input dataset as described in [Prepare your comorbidity analysis data](#prepareData), you can execute the tool (Java program) to carry out comorbidity analysis. See [Executing Comorbidity4j on your pc](LocalExecution.md) to get detailed instruction on how to download and execute Comorbidity4j on your PC.  
  
