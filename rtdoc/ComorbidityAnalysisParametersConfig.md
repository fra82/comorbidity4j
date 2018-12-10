![Comorbidity4j](/img/logo.png)
<h1>Configuration of comorbidity analysis parameters</h1>
  
Once connected by means of a Web browser to Comorbidity4j (to a locally running server to to <a href="http://comorbidity.eu/comorbidity4web/" target="_blank">http://comorbidity.eu/comorbidity4web/</a>), interactively uploaded / validated the input data and defined diagnosis grouping and pairing patterns,  **Comorbidity4j web application drives the user throughout the interactive tuning of the comorbidity analysis parameters**.  
  
The following set of parameters can be specified:  
  
+ **directionality** can be enabled in comorbidity analysis. When diagnosis time directionality is not enabled, a patient is considered to suffer a pair of diagnoses if she/he experimented both of them during her/his clinical history, independently from their temporal order. When diagnosis time directionality is enabled, a patient is considered to suffer an ordered pair of diagnoses if the first diagnosis precedes the second diagnosis by a number of days equal or greater than the previous user defined value.  
+ if gender-based analysis is enabled, the **gender identifiers** needs to be specified to compute the sex ratio parameter of each comorbidity pair. The following two values are the ones used in the gender column of the Patient data file in order to specify the gender of each patient.  
+ a specific **approach to compute the age of patients** has to be chosen among the following ones:
    - *FIRST_ADMISSION*: the age of the patient at the moment (date) of the first visit (with or without diagnosis associated)  
    - *FIRST_DIAGNOSTIC*: the age of the patient at the moment (date) of the first visit with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)  
    - *LAST_ADMISSION*: the age of the patient at the moment (date) of the last visit (with or without diagnosis associated)  
    - *LAST_DIAGNOSTIC*: the age of the patient at the moment (date) of the last visit with at least one diagnosis associated (with diagnosis code present among the diagnosis codes considered for comorbidity analysis)  
    - *EXECUTION_TIME*: the age of the patient at execution time  
+ one of **p-value adjustment approach** can be chosen to adjust p-value in case of multiple testing (for more information refer to the R method <a href="https://stat.ethz.ch/R-manual/R-devel/library/stats/html/p.adjust.html" target="_blank">p.adjust</a>).  
+ the **relative risk confidence interval**.  
+ the **odds ratio confidence interval**.  
+ the **patient filters** to select the patients that will be considered in the comorbidity analysis by age and other facets.  
+ the **comorbidity score filters** to select the pairs that will be included in the results of the comorbidity analysis by their [comorbidity scores](ComorbidityScoresComputed.md).  
  
  
Interface to specify comorbidity analysis parameters:  
![Visit data file upload](/img/input_c4web_comorbidityParameters.png)  
  
  
  