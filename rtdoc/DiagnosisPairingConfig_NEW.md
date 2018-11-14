![Comorbidity4j](/img/logo.png)
<h1>Customize diagnosis pairing</h1>

When a comorbidity analysis is executed, starting from the patient-disease dataset provided by the user (see: [Patient input file format](InputFileFormat.md)), a set of pairs of diseases is selected in order to evaluate if the relevance of their occurrence in the same patient-disease dataset.  
  
The disese pairs that will be considered in the analysis of comorbidity includes **by default** all pairs of diseases that occur in the patient-disease dataset provded by the user.  
  
Comorbidity4j users have the possibility (optional) to customize the diseses (and thus the disease pairs) that will be considered in the analysis of comorbidities by providing the **Diagnosis pairing file** (see below for a description of the format). Such input file enables the possibility to scope the analysis of comorbidities and to optimize the computation efforts of Comorbidity4j by reducing the pairs of diseases to consider.  

The **Diagnosis pairing file** can be also used to rename diseases for comorbidity analysis: for each disease specified by the *Diagnosis data file* ((see: [Patient input file format](InputFileFormat.md))), it is possible to specify a new name to be used in the comorbidity analysis. As a consequence, if two or more diseases are renamed by means of the same name, they will be merged and thus considered as a single disease for comorbidity analysis.  

If a **Diagnosis pairing file** is not specified / provided, the comorbidity analysis will consider all the possible disease (diagnosis) pairs of the diseases (diagnoses) specified by the *Diagnosis data file* (see: [Patient input file format](InputFileFormat.md)).  

  
Like the set of Patient input files (see: [Patient input file format](InputFileFormat.md)), the **Diagnosis pairing file** is a spreadsheet that:  
  
+ is **TAB or comma-separated and encoded in UTF-8**.  
+ can include any number of columns, provided that the set of columns defined as mandatory in this documentation is present.  
+ the first row is considered to be the header of the same spreadsheet and is exploited to assign the name to each column. The name of a specific column containing a specific type of data can be defined by the user by properly configuring the *columnName* set of properties of the configuration file - see: [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md).  
+ the value of all cellscan include quoted ("cell_text") or non quoted (cell_text) text. In case of quoted text it is important to escape the quotation marks occurring in cell values (example "cell \" text"). In case of non quoted cell values it is important not to use the column separator character (TAB or COMMA) in the cell text.  
   
  
## Diagnosis pairing file  

The **Diagnosis pairing file** (OPTIONAL, <a href="https://raw.githubusercontent.com/fra82/comorbidity4j/master/example/input/diagnosisPairing.csv" target="_blank">download example</a>) specifies the set of diseases (diagnoses) that should be considered for comorbidity analysis - one row per disease (diagnosis). Besides the first row, that provides column names, each row describes a different disease / diagnosis).  

Mandatory columns:  

+ *diagnosis_code*: ID that unambiguously identifies the disease (diagnosis) to study comorbidity of (any String can usef in the [Patient input files](InputFileFormat.md) be used)  
+ *paired_diseases*: this column specifies the approach to generate, starting from the diagnosis_code specified in the same row, all the disease code pairs, including that disease code, that will be considered for comorbidity analysis. The value of the *paired_diseases* column specifies for each disease described in the same row, the set of diseases to associate to such disease in order to generate disease pairs to check for comorbidity. The *paired_diseases* column can have one of the following values:
    - *ALL_DISEASES*: the disease identified by the row of the Index Disease file will be paired with all the diseases occurring in the dataset  
    - *INDEX_DISEASES*: the disease identified by the row of the Index Disease file will be paired with all the diseases occurring in the Index Disease file  
    - *DIS_ID_1,DIS_ID_2,...,DIS_ID_n*: each disease described by a row of the Index Disease file will be paired with the diseases with code specified in the comma separated list of disease codes  
+ *diagnosis_group*: if the value of the column with this name is not empty, the ID of the diseases (ID specified by the column diagnosis_code) will be substituted by such value. As a consquence, all the disease IDs with the same diagnosis_group value will be grouped as a single disease with ID equal to the diagnosis_group value.  


  
IMPORTANT: if you leave empty the value of the *paired_diseases* column for a specific row, the approach to generate the set of the disease code pairs, starting from the disease code mentioned in the row, is the one specified by means of the property 'analysis.defaultDiseasePairingApproach' (one among ALL_DISEASES or INDEX_DISEASES) - see [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md).  
  
Example of **Diagnosis pairing file** format (TAB separated columns - the name of each column can be defined as [Comorbidity analysis parameter](ComorbidityAnalysisParametersConfig.md)):  
```
"diagnosis_code"    "diagnosis_group"  "paired_diseases"
"C0149654"  "GROUP_1"  "INDEX_DISEASES"
"C0080274"  ""  "ALL_DISEASES"
"C0149658"  "GROUP_1"  "C0080274,C0080279"
"C0080279"  ""  "ALL_DISEASES"
```  

In this example of **Diagnosis pairing file**, to construct disease pairs to study for comorbidity:  

+ since the *paired_diseases* column of the first non-header row is 'INDEX_DISEASES', the disease with code C0149654 will be paired with all the diseases mentioned in the **Diagnosis pairing file** (in this example the diseases with code C0080274, C0149658 and C0080279)  
+ since the *paired_diseases* column of the second non-header row is 'ALL_DISEASES', the disease with code C0080274 will be paired with all the diseases mentioned in the *Diagnosis data file* (see: [Patient input file format](InputFileFormat.md))  
+ since the *paired_diseases* column of the third non-header row is 'C0080274,C0080279', the disease with code C0149658 will be paired with all the diseases with code C0080274 and C0080279  

Moreover, since the rows corresponding to the diseases C0149654 and C0149658 have 'GROUP_1' (non empty string) as diagnosis_group column value, both diseases will be merged and referenced by the GROUP_1 diagnosis_code in the comorbidity analysis.  


