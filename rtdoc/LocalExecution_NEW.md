![Comorbidity4j](/img/logo.png)
<h1>Executing Comorbidity4j on your pc</h1>

## Before Executing the Comorbidity4j

Comorbidity4j has to be executed by Java 1.8 or newer. As a consequence, as a prerequisite for the exectuion of Comorbidity4j you need to have <a href="https://www.java.com/en/download/" target="_blank">Java</a> installed.  
  
## Download the JAR package 
The compressed archive that contains the latest version (LATEST_VERS = 1.1) of comorbidity4j can be downloaded at the following URL:  
<a href="https://github.com/fra82/comorbidity4j/releases/download/v1.1/comorbidity4j-1.1-bin.zip" target="_blank">https://github.com/fra82/comorbidity4j/releases/download/v1.1/comorbidity4j-1.1-bin.zip</a>  


## Comorbidity4j Execution  
  
+ Decompress the archive that contains the latest version of comorbidity4j and open the folder 'comorbidity4j-LATEST_VERS'  
+ Create a text file named 'comorbidity.properties' (or use the template file present in the folder 'comorbidity4j-LATEST-VERS'). By means of a text editor, modify the **Property file** named 'comorbidity.properties' by specifying all the required parameters including (go to [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md) for a detailed description of all available configuration options for comorbidity analysis):  
    - the full local path of the four **Patient input files** (spreadsheets) (go to [Patient input file format](InputFileFormat.md) for details on the files format)  
    - the name of the columns of the previous files where to read patient-disease data from  
    - the date format  
    - the output folder to store results to  
    - optionally, enable the time directionality, patient and comorbidity filter parameters that will be used to filter the input and output data of the comorbidity analysis  
    - optionally, enable multi-threaded executions  
    - optionally, specify the p-value adjustment approach to apply  
+ Start the comorbidity analysis by executing the following command from the folder 'comorbidity4j-LATEST-VERS':  
  
```
// Linux users:  
java -cp './comorbidity4j-LATEST-VERS.jar:./lib/*' es.imim.ibi.comorbidity4j.ComorbidityExecutor /full/local/path/to/comorbidity4j-LATEST-VERS/comorbidity.properties  
    
// Windows users:  
java -cp "c:\Full\Local\Path\To\comorbidity4j-LATEST-VERS\comorbidity4j-LATEST-VERS.jar;c:\Full\Local\Path\To\comorbidity4j-LATEST-VERS\lib\*" es.imim.ibi.comorbidity4j.ComorbidityExecutor "c:\Full\Local\Path\To\comorbidity4j-LATEST-VERS\comorbidity.properties"  
```  
  
+ During the execution, several messages concerning the status of the crawling process will be displayed in the standard output  
  
  
<a name="results"></a>
## Result file / data format  
  
Once terminated the analysis, its results are stored in the output folder specified by the property *output.folderFullPath* of the **Property file** (see [Comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md)) and will include the following four items:  
  
* **file: comor_execID_result_XX_XX_XX_MALEandFEMALE.csv**:&nbsp; TAB-separated spreadsheets encoded in UTF-8 including a table with the results of the comorbidity analysis by considering **both females and males**. Each row of this file describes the result of the analysis of a pair of diseases. Go to [Comorbidity table](ComorbidityTable.md) for more details.

* **file: comor_execID_result_XX_XX_XX_onlyFEMALE.csv**:&nbsp; TAB-separated spreadsheets encoded in UTF-8 including a table with the results of the comorbidity analysis by considering **only females**. Each row of this file describes the result of the analysis of a pair of diseases. Go to [Comorbidity table](ComorbidityTable.md) for more details.
  
* **file: comor_execID_result_XX_XX_XX_onlyMALE.csv**:&nbsp; TAB-separated spreadsheets encoded in UTF-8 including a table with the results of the comorbidity analysis by considering **only males**. Each row of this file describes the result of the analysis of a pair of diseases. Go to [Comorbidity table](ComorbidityTable.md) for more details.
   
* **file: comor_execID_parametersAnalysis_XX_XX_XX.params**:&nbsp; textual (UTF-8) summary of all the parameter of the comorbidity analysis together with some stats on the input patients' dataset.  
  
* **file: comor_execID_execLog_XX_XX_XX.log**:&nbsp; textual (UTF-8) output log of the comorbidity analysis.  
  
* **driectory: comor_execID_web_XX_XX_XX**:&nbsp; folder containing the **browser based visualization of the comorbidity analysis results**. This folder contains the file: *comor_execID_web_XX_XX_XX.html*. Open this file with a Web browser to interactively visualize and explore the results of the comorbidity analysis (navigate several facets of the input patient-disease dataset, show sex ratio analysis results of each comorbidity pair and apply further filters on the results of comorbidity analyses and visualize these results by means of interactive heatmaps and network graphs). Go to [Web-based interactive visualizations](InteractiveVisualizations.md) for more details.
  


