package es.imim.ibi.comorbidity4j.server.reservlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import es.imim.ibi.comorbidity4j.server.template.TemplateUtils;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;

/**
 * Servlet to get data for comorbidity analysis
 * 
 * @author Francesco Ronzano
 *
 */
public class ResultLoaderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ResultLoaderServlet.class);

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

	public void init() throws ServletException {

	}

	private static String getClientIp(HttpServletRequest request) {
		String remoteAddr = "NO_IP";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}

		return remoteAddr;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Date currentDate = new Date();
		System.out.println("Received Result request from IP: " + getClientIp(request) + " on " + dateFormat.format(currentDate));
		
		String execID = request.getParameter("eid");
		if(execID == null) {
			OutputStream outStr = response.getOutputStream();
			PrintWriter out = new PrintWriter(outStr);
			out.write(TemplateUtils.generateHTMLcommonHeader(false, execID));
			out.write(TemplateUtils.generateHTMLcustomMessage("Impossible to load Comorbidity4web results!<br/>"
					+ "Parameter " + "eid" + " not specified."));
			out.flush();
			return;
		}
		
		// Manage file type and headers
		String fileType = request.getParameter("type");
		if( fileType == null || (!fileType.toLowerCase().trim().equals("zip") && !fileType.toLowerCase().trim().equals("summary") && !fileType.toLowerCase().trim().equals("csvall") && !fileType.toLowerCase().trim().equals("csvfemale") && !fileType.toLowerCase().trim().equals("csvmale")) ) {
			fileType = "HTML";
		}
		
		if( fileType != null && fileType.toLowerCase().trim().equals("zip") ) {
			response.setContentType("application/x-msdownload");            
			response.setHeader("Content-disposition", "attachment; filename="+ "result_" + execID + ".zip");
		}
		else if( fileType != null && (fileType.toLowerCase().trim().equals("summary") || fileType.toLowerCase().trim().equals("html")) ) {
			response.setContentType("text/html");
		}
		else {
			String resultFileAppo = "result_" + execID + "_ALL.csv";
			if(fileType != null && fileType.toLowerCase().trim().equals("csvfemale")) {
				resultFileAppo = "result_" + execID + "_FEMALE.csv";
			}
			else if(fileType != null && fileType.toLowerCase().trim().equals("csvmale")) {
				resultFileAppo = "result_" + execID + "_MALE.csv";
			}
			response.setHeader("Content-disposition", "attachment; filename="+ resultFileAppo);
			response.setContentType("application/CSV"); 
		}
		
		
		OutputStream outStr = response.getOutputStream();
		PrintWriter out = new PrintWriter(outStr);
		
		String actionType = request.getParameter("action");
		
		// Delete all results
		if(actionType != null && actionType.trim().toLowerCase().equals("delete")) {
			System.out.println("Request deletion of all data with ID: " + execID);
			
			String outputDeletionMessage = "";
			
			File ZIPresultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + ".zip");
			if(ZIPresultFile != null && ZIPresultFile.exists() && ZIPresultFile.exists()) {
				ZIPresultFile.delete();
				outputDeletionMessage += "The ZIP file with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			File HTMLresultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + ".html");
			if(HTMLresultFile != null && HTMLresultFile.exists() && HTMLresultFile.exists()) {
				HTMLresultFile.delete();
				outputDeletionMessage += "The HTML file with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			File HTMLsummaryFile = new File(ComputeComorbidityServlet.basePathResultStorage + "executionSummaryPage_" + execID + ".html");
			if(HTMLsummaryFile != null && HTMLsummaryFile.exists() && HTMLsummaryFile.exists()) {
				HTMLresultFile.delete();
				outputDeletionMessage += "The HTML summary file with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			File CSVFile_ALL = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_ALL.csv");
			if(CSVFile_ALL != null && CSVFile_ALL.exists() && CSVFile_ALL.exists()) {
				CSVFile_ALL.delete();
				outputDeletionMessage += "The CSV file with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			File CSVFile_FEMALE = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_FEMALE.csv");
			if(CSVFile_FEMALE != null && CSVFile_FEMALE.exists() && CSVFile_FEMALE.exists()) {
				CSVFile_FEMALE.delete();
				outputDeletionMessage += "The CSV file (only FEMALE) with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			File CSVFile_MALE = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_MALE.csv");
			if(CSVFile_MALE != null && CSVFile_MALE.exists() && CSVFile_MALE.exists()) {
				CSVFile_MALE.delete();
				outputDeletionMessage += "The CSV file (only MALE) with results of the comorbidity analysis with id " + execID + " have been permanently deleted." + "<br/>";
			}
			
			FileUtils.write(HTMLsummaryFile, TemplateUtils.generateHTMLcommonHeader(false, execID) + TemplateUtils.generateHTMLcustomMessage("The results of this comorbidity analysis with id " + execID + " " + 
					"have been permanently deleted.<br/><br/>" + outputDeletionMessage + "<br/><br/>" + 
					"Comorbidity4web results are automatically permanently deleted from the server storage 24 hours after their generation or before if the deletion is triggered manually by the user from the result page."));
			
			out.write(TemplateUtils.generateHTMLcommonHeader(false, execID));
			out.write(TemplateUtils.generateHTMLcustomMessage("Permanently deleted comorbidity analysis results with id=" + execID + ".<br/>"));
			out.flush();
			return;
		}
		
		
		File resultFile = null;
		if(fileType != null && fileType.toLowerCase().trim().equals("zip")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + ".zip");
		}
		else if(fileType != null && fileType.toLowerCase().trim().equals("summary")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "executionSummaryPage_" + execID + ".html");
		}
		else if(fileType != null && fileType.toLowerCase().trim().equals("html")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + ".html");
		}
		else if(fileType != null && fileType.toLowerCase().trim().equals("csvall")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_ALL.csv");
		}
		else if(fileType != null && fileType.toLowerCase().trim().equals("csvfemale")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_FEMALE.csv");
		}
		else if(fileType != null && fileType.toLowerCase().trim().equals("csvmale")) {
			resultFile = new File(ComputeComorbidityServlet.basePathResultStorage + "result_" + execID + "_MALE.csv");
		}
		
		// Message if file is not available required file
		if(resultFile == null || !resultFile.exists() || !resultFile.isFile()) {
			
			if(fileType != null && fileType.toLowerCase().trim().equals("zip")) {
				out.write(TemplateUtils.generateHTMLcommonHeader(false, execID));
				out.write(TemplateUtils.generateHTMLcustomMessage("Impossible to load Comorbidity4web results in " + fileType + " format!<br/>"
						+ "You may have already downloaded once the zipped result file of this comorbidity analysis id=" + execID + ".<br/>"
						+ "For privacy-preservation issues, comorbidity analysis results are available for a single download, then they are permanently deleted from the server storage."));
				out.flush();
				return;
			}
			else {
				out.write(TemplateUtils.generateHTMLcommonHeader(false, execID));
				out.write(TemplateUtils.generateHTMLcustomMessage("Impossible to load Comorbidity4web results in " + fileType + " format!<br/>" +
						"Results of comorbidity analysis with id=" + execID + " do not exist.<br/>" + 
						"Remember that results can be accessed on-line during the 24 hours after their generation, then they are permanently deleted from the server storage."));
				out.flush();
				return;
			}
			
		}
		else {

			FileInputStream in = new FileInputStream(resultFile);
			IOUtils.copy(in, response.getOutputStream());
			
			/*
			if( fileType != null && fileType.toLowerCase().trim().equals("zip") && ServerExecConfig.isOnline) {
				// Delete file from server
				System.out.println("Deleting ZIP file with results with ID: " + execID + "...");
				resultFile.delete();
			}
			*/
			
		}
		
		out.close();
		outStr.close();
		
		// Delete session
		System.gc();
		
	    return;
		
	}

	public static ImmutablePair<String, String> checkParameterString(String paramString, String paramName) {
		String causeLeft = "";
		String valueRight = "";

		ImmutablePair<String, String> retPair = new ImmutablePair<String, String>(causeLeft, valueRight);

		if(Strings.isNullOrEmpty(paramString)) {
			causeLeft = "Null or empty String parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramString;
		}

		return retPair;
	}

	public void destroy() {
		// do nothing.
	}
}