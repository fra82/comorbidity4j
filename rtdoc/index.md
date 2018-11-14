![Comorbidity4j](/img/logo.png)
<h3>Java tool to analyze comorbidities over large datasets of patients</h3>

**Latest version: 1.1**  
**source code on GitHub at <a href="https://github.com/fra82/comorbidity4j">https://github.com/fra82/comorbidity4j</a>**  
  
Comorbidity4j is an open-source java tool tailored to easily perform <a href="https://en.wikipedia.org/wiki/Comorbidity" target="_blank">comorbidity analyses</a>, thus **supporting the analysis of significant cooccurrences of diseases over large datasets of patient data**.  

Given the *demographic information* (birth-date, geneder and, optionally secondary patient features like education level, ethnicity, etc.) and the *history of diseases* of a set of patients, Comorbidity4j performs the comorbidity analysis of (a subset of) such diseases: several widespread measures to identify relevant pairs of cooccurring diseases are computed over the patients' population data provided as input.  
  
A brief walkthrough of the capabilities of Comorbidity4j is provided by the [Overview](Walkthrough.md).  
  
Comorbidity analyses can be executed:  

+ **on your PC** by means of Comorbidity4j, a downloadable Java library. The documentation accessible here explains how to download and execute Comorbidity4j.  
+ **online** by means of [Comorbidity4web](OnlineExecution.md), a Web service that enables users to upload patient datasets and generate on-line interactive visualizations of the results of comorbidity analysis.  
If the patient dataset you have to analyze contains sensitive data, you can download the Comorbidity4j Java library (see: [Executing Comorbidity4j on your pc](LocalExecution.md)) and execute the comorbidity analysis locally in your PC.  

Comorbidity4j is distributed as **open source software** - interested users can access the Java source code on <a href="https://github.com/fra82/comorbidity4j">GitHub / comorbidity4j project</a>. Anyway, the access to the source code is not directly needed to execute Comorbidity4j since we provide the executable version of the tool (as well as the possibility to perform comorbidity analyses online).  
  
**Core features**:  

+ computation of the following comorbidity scores: Relative Risk Index, Odds Ratio (95% confidence interval), Phi Index, Comorbidity Score, Fisher Test (see: [Comorbidity scores](ComorbidityScoresComputed.md))  
+ customization of p-value adjustment approach by choosing one of the following methodologies: BONFERRONI, BENJAMINI-HOCHBERG, HOLM, HOCHBERG, BENJAMINI-YEKUTIELI, HOMMEL (see: [Comorbidity scores](ComorbidityScoresComputed.md))  
+ sex ratio analysis to see if a comorbidity suffered in both, men and women, is equally likely in both sex or if it is more likely in one sex than in another  
+ results of comorbidity analysis as **spreadsheet** (see: [Comorbidity table](ComorbidityTable.md)) as well as by means of **interactive visualizations** (see: [Web-based interactive visualizations](InteractiveVisualizations.md)) that you can open locally with your favourite browser  
+ input data provided by means of a set of spreadsheets describing *demographic information* and *history of diseases* of a set of patients (see: [Patient input file format](InputFileFormat.md))  
+ support for input data formatted in compliance with the <a href="https://github.com/OHDSI/CommonDataModel/wiki" target="_blank">**OHDSI Common Data Model (OMOP)**</a>. (see: [Processing OMOP Common Data Model datasets](InputFileFormatOMOP.md))  
+ support for **multi-thread execution** in order to effectively deal with datasets with thousand patiens and million disease pairs  
+ support for **time directionality** in the analysis of disease pairs  
+ possibility to **customize a wide range of comorbidity analysis aspects** including:  
    - set of disease (diagnosis) pairs to consider for comorbidity analysis (see: [Customize diagnosis pairing](DiagnosisPairingConfig.md))  
    - path and column names of input files (only for local execution)  
    - column separator of input spreadsheets  
    - date format  
    - number of threads (only for local execution)  
    - time directionality of disease pairs when analyzing their comorbidity  
    - patient filters to select subgroups of patients by age and secondary patient features like education level, ethnicity  
    - comorbidity filters to select only disease pairs matching specific values of the computed comorbidity scores  
    - study only the diseases with a minimum number of patients  
    - merge multiple disease into a single one  
  
  
  
**Index**  

* [Overview](Walkthrough.md)  
* [Patient input file format](InputFileFormat.md)  
* [Processing OMOP Common Data Model datasets](InputFileFormatOMOP.md)  
* [Customize diagnosis pairing](DiagnosisPairingConfig.md)  
* [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md)  
* [Executing Comorbidity4j on your pc](LocalExecution.md)  
* [Comorbidity4web: execute comorbidity analyses on-line](OnlineExecution.md)  
* [Comorbidity scores](ComorbidityScoresComputed.md)  
* [Comorbidity table](ComorbidityTable.md)  
* [Web-based interactive visualizations](InteractiveVisualizations.md)
* [Contacts and support](Contacts.md)  

