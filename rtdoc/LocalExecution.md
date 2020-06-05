![Comorbidity4j](/img/logo.png)
<h1>Executing Comorbidity4j on your PC</h1>
  
  
Comorbidity4j can be executed on your PC as by starting the Comorbidity4j Web server, also referred to as comorbidity4web; this web server is the same one accessible online at <a href="http://comorbidity.eu/comorbidity4web/">http://comorbidity.eu/comorbidity4web/</a>. The advantages of executing Comorbidity4j locally (on your permises) includes:  
  
+ greater preservation of the privacy of your comorbidity data, since there is no need to send online the data to be analized and get back the results as done when the <a href="http://comorbidity.eu/comorbidity4web/">online service</a> is used;  
  
+ possibility to allocate to the local executing Comorbidity4j Web server more resources (i.e. RAM) and thus to properly process bigger comorbidity dataset with respect to the ones that can be analyzed by the <a href="http://comorbidity.eu/comorbidity4web/">online service</a>. Examples of how the RAM requirements of Comorbidity4j with respect to the size of the dataset to analyze can be found in the [Performance analysis section](Performance.md).  
  
  
## Before Executing the Comorbidity4j

Comorbidity4j has to be executed by Java 1.8 or newer. As a consequence, as a prerequisite for the execution of Comorbidity4j you need to have <a href="https://www.java.com/en/download/" target="_blank">Java</a> installed.  
  
## Download the JAR package 
The compressed archive (comorbidity4j-3.5-bin.zip) that contains the latest version (LATEST_VERS = 3.5) of comorbidity4j can be downloaded at the following URL:  
  
<a href="https://github.com/fra82/comorbidity4j/releases/tag/3.5" target="_blank">https://github.com/fra82/comorbidity4j/releases/tag/3.5</a>    
  
  
## Comorbidity4j Web server Execution  
  
+ Comorbidity4j Web server needs to be able to read / write / delete files from a directory local to the execution machine; this directory is referred to as the temporary files directory and is used by Comorbidity4j to write partial and final results of comorbidity analyses. Before starting the Comorbidity4j Web server you need to create such a directory. The full local path of the temporary files directory has to be specified by means of the program parameter spring.servlet.multipart.location  
+ Comorbidity4j Web server by default listens for requests on the port 8181; a different port number can be specified by means of the program parameter server.port  
  
  
To start Comorbidity4j Web server:  
  
+ Unzip the archive (comorbidity4j-LATEST_VERS-bin.zip) that contains the latest version of comorbidity4j and open the folder 'comorbidity4j-LATEST_VERS'  
+ Start the comorbidity4j local service (server) by executing the following command from the folder 'comorbidity4j-LATEST-VERS':  
  
```  
// Linux users:  
java -cp '/local/path/to/comorbidity4j-LATEST-VERS/comorbidity4j-LATEST-VERS.jar:/local/path/to/comorbidity4j-LATEST-VERS/lib/*' es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
     --server.port=PORT_NUMBER --spring.servlet.multipart.location=/path/to/temporary/files/directory/  
  
// Windows users:  
java -cp "c:\Local\Path\To\comorbidity4j-LATEST-VERS\comorbidity4j-LATEST-VERS.jar;c:\Local\Path\To\comorbidity4j-LATEST-VERS\lib\*" es.imim.ibi.comorbidity4j.server.StartComorbidity4j  
     --server.port=PORT_NUMBER --spring.servlet.multipart.location=c:\local\path\to\temporary\files\directory\  
```  
  
where the folloiwing program execution parameters can be specified:  
  
+ server.port (OPTIONAL, default 8181):  
  
```  
// Both Linux and Windows users:  
--server.port=8181  
```  
  
+ spring.servlet.multipart.location (MANDATORY):  
  
```  
// Linux users:  
--spring.servlet.multipart.location=/path/to/temporary/files/directory/  
  
// Windows users:  
--spring.servlet.multipart.location=c:\local\path\to\temporary\files\directory\  
```  
  
When the server is started, in the standard output (i.e. on the shell) proper log messages will confirm that everything is properly configured and the server is up and running; it this is not the case, alert messages alerting of specific configuration problems are shown.  
  
  
+ Open by your Web browser (possibly Google Chrome) the URL: <a href="http://localhost:8181/comorbidity4web/" target="_blank">http://localhost:8181/comorbidity4web/</a> (in case the server.port has not been provided or is equal to 8181). Here you can access the Web interface that will drive you throughout the different step and the exploration of results of a comorbidity analysis. The same Web interface can be accessed at <a href="http://comorbidity.eu/comorbidity4web/" target="_blank">http://comorbidity.eu/comorbidity4web/</a> as the Comorbidity4web service, for online execution of comorbidity analyses.  
  
  
<a name="results"></a>  
  
## Results  
  
Once [uploaded the input files](InteractiveInputDataUploadAndValidation.md), [defined the diagnosis grouping and pairing patterns](DiagnosisGroupingAndPairing.md) and the [comorbidity analysis parameters](ComorbidityAnalysisParametersConfig.md), the Comorbidity4j Web interface allows to trigger a comorbidity analysis. A new browser tab opens where the user can interactively monitor the progress of the analysis and, once terminated, can access the following set of links to:  
  
+ **open the HTML interactive Web-viz** in a new tab (see [Web-based interactive visualizations](InteractiveVisualizations.md) or open <a href="http://backingdata.org/comorbidity4j/" target="_blank">example of HTML interactive Web-viz</a>)  
+ **download the results as a CSV table** (see [Comorbidity table](ComorbidityTable.md))  
+ **download a ZIP file with all results** (both a stand-alone version of the HTML interactive web visualization and the comorbidity analysis results in CSV format)  
  
  
An example of the Web page where the comorbidity analysis execution can be monitored and the results accessed is:  
  
![Comorbidity analysis monitoring and result link page](/img/web_result_analysis_page_1.png)  
![Comorbidity analysis monitoring and result link page](/img/web_result_analysis_page_2.png)  
  
  
When Comorbidity4j is executed locally, the results of comorbidity analyses are also sotred in the temporary files directory. The full local path of the temporary files directory has to be specified by means of the program parameter spring.servlet.multipart.location. In particulr, for each comorbidity analysis executed, the temporary files directory will store the whole set of results of the analysis (CSV files and HTML visualizations) in a single ZIP file with name equal to result\_COMORBIDITY\_ANALYSIS\_ID.zip (where \_COMORBIDITY\_ANALYSIS\_ID is a unique ID identifying a specific comorbidity analysis).  
  
  
  
  
  
  
  
  
