![Comorbidity4j](/img/logo.png)
<h1>Executing Comorbidity4j on your pc</h1>

## Before Executing the Comorbidity4j

Comorbidity4j has to be executed by Java 1.8 or newer. As a consequence, as a prerequisite for the execution of Comorbidity4j you need to have <a href="https://www.java.com/en/download/" target="_blank">Java</a> installed.  
  
## Download the JAR package 
The compressed archive (comorbidity4j-3.4-bin.zip) that contains the latest version (LATEST_VERS = 3.4) of comorbidity4j can be downloaded at the following URL:  
  
<a href="https://github.com/fra82/comorbidity4j/releases/tag/3.4" target="_blank">https://github.com/fra82/comorbidity4j/releases/tag/3.4</a>    
  
  
## Comorbidity4j Execution  
  
+ Unzip the archive (comorbidity4j-LATEST_VERS-bin.zip) that contains the latest version of comorbidity4j and open the folder 'comorbidity4j-LATEST_VERS'  
+ Start the comorbidity4j local service (server) by executing the following command from the folder 'comorbidity4j-LATEST-VERS':  
  
```
// Linux users:  
java -cp '/local/path/to/comorbidity4j-LATEST-VERS/comorbidity4j-LATEST-VERS.jar:/local/path/to/comorbidity4j-LATEST-VERS/lib/*' es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
    
// Windows users:  
java -cp "c:\Local\Path\To\comorbidity4j-LATEST-VERS\comorbidity4j-LATEST-VERS.jar;c:\Local\Path\To\comorbidity4j-LATEST-VERS\lib\*" es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
```  
  
+ Open by your favorite Web browser the URL: <a href="http://localhost:8181/comorbidity4web/" target="_blank">http://localhost:8181/comorbidity4web/</a>. Here you can access the Web interface that will drive you throughout the different step and the exploration of results of a comorbidity analysis. The same Web interface can be accessed at <a href="http://comorbidity.eu/comorbidity4web/" target="_blank">http://comorbidity.eu/comorbidity4web/</a> as the COmorbidity4web service, for online execution of comorbidity analyses.  
  
  
  
To change default server port (8181), when starting comorbidity4j local service (server), specify the following program parameter:  
  
```  
// Both Linux and Windows users:  
--server.port=8585  
```  
  
To change the temporary files directory path (the local directory where Comorbidity4web writes partial and final resulta of comorbidity analyses), when starting comorbidity4j local service (server), specify the following program parameter:  
  
```  
// Linux users:  
--spring.servlet.multipart.location=/path/to/temporary/files/directory/  
  
// Windows users:  
--spring.servlet.multipart.location=c:\local\path\to\temporary\files\directory\  
```  
  
  
To specify a custom server port or temporary files directory path, start the server with the following program parameters:  
```  
// Linux users:  
java -cp '/local/path/to/comorbidity4j-LATEST-VERS/comorbidity4j-LATEST-VERS.jar:/local/path/to/comorbidity4j-LATEST-VERS/lib/*' es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
     --server.port=PORT_NUMBER --spring.servlet.multipart.location=/path/to/temporary/files/directory/  
  
// Windows users:  
java -cp "c:\Local\Path\To\comorbidity4j-LATEST-VERS\comorbidity4j-LATEST-VERS.jar;c:\Local\Path\To\comorbidity4j-LATEST-VERS\lib\*" es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
     --server.port=PORT_NUMBER --spring.servlet.multipart.location=c:\local\path\to\temporary\files\directory\  
```  
  
  
<a name="results"></a>  
  
## Results  
  
Once [uploaded the input files](InteractiveInputDataUploadAndValidation.md), [defined the diagnosis grouping and pairing patterns](DiagnosisGroupingAndPairing.md) and the [comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md), the COmorbidity4j Web interface allows to trigger a comorbidity analysis. A new browser tab opens where the user can interactively monitor the progress of the analysis and, once terminated, can access the following set of links to:  
  
+ **open the HTML interactive Web-viz** in a new tab (see [Web-based interactive visualizations](InteractiveVisualizations.md) or open <a href="http://backingdata.org/comorbidity4j/" target="_blank">example of HTML interactive Web-viz</a>)  
+ **download the results as a CSV table** (see [Comorbidity table](ComorbidityTable.md))  
+ **download a ZIP file with all results** (both a stand-alone version of the HTML interactive web visualization and the comorbidity analysis results in CSV format)  
  
  
An example of the Web page where the comorbidity analysis execution can be monitored and the results accessed is:  
  
![Comorbidity analysis monitoring and result link page](/img/web_result_analysis_page_1.png)  
![Comorbidity analysis monitoring and result link page](/img/web_result_analysis_page_2.png)  
  
  
  
  
  
  
  
