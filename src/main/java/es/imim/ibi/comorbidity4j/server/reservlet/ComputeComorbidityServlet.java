package es.imim.ibi.comorbidity4j.server.reservlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.nio.channels.Channels;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;
import es.imim.ibi.comorbidity4j.analysis.ComorbidityMinerCache;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.template.TemplateUtils;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;
import es.imim.ibi.comorbidity4j.util.GenericUtils;

/**
 * Servlet to generate comorbidity analysis
 * 
 * @author Francesco Ronzano
 *
 */
public class ComputeComorbidityServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ComputeComorbidityServlet.class);

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

	private static final DateFormat dateFormatID = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");

	public static ExecutorService executor = Executors.newFixedThreadPool(4);

	private static Random rnd = new Random();

	public static String basePathResultStorage = Files.createTempDir().getAbsolutePath();  // "/home/ronzano/comorbidity4web/results/"

	private static DecimalFormat decimFormatThreeDec = new DecimalFormat("#######0.000");
	
	static {
		decimFormatThreeDec.setRoundingMode(RoundingMode.HALF_DOWN);
	}
	
	public void init() throws ServletException {

	}

	public static ImmutablePair<String, String> checkParameterString(String paramString, String paramName) {
		String causeLeft = "";
		String valueRight = "";

		if(Strings.isNullOrEmpty(paramString)) {
			causeLeft = "Null or empty String parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramString;
		}

		return new ImmutablePair<String, String>(causeLeft, valueRight);
	}

	public static ImmutablePair<String, Integer> checkParameterInteger(String paramString, String paramName) {
		String causeLeft = "";
		Integer valueRight = null;

		Integer paramInt = null;
		try {
			paramInt = Integer.valueOf(paramString);
		}
		catch(Exception e) {
			/* Do nothing */
		}

		if(Strings.isNullOrEmpty(paramString) || paramInt == null) {
			causeLeft = "Null, empty or not Integer parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramInt;
		}

		return new ImmutablePair<String, Integer>(causeLeft, valueRight);
	}

	public static ImmutablePair<String, Double> checkParameterDouble(String paramString, String paramName) {
		String causeLeft = "";
		Double valueRight = null;

		Double paramDouble = null;
		try {
			paramDouble = Double.valueOf(paramString);
		}
		catch(Exception e) {
			/* Do nothing */
		}

		if(Strings.isNullOrEmpty(paramString) || paramDouble == null) {
			causeLeft = "Null, empty or not Double parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramDouble;
		}

		return new ImmutablePair<String, Double>(causeLeft, valueRight);
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

		UserInputContainer so = (request.getSession().getAttribute("so") != null) ? (UserInputContainer) request.getSession().getAttribute("so") : null;


		// Generating hash - START
		MessageDigest mdig = null;
		try {
			mdig = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		mdig.update((new String(rnd.nextInt(100000) + "_" + UUID.randomUUID().toString() + "_" + dateFormatID.format(new Date()))).getBytes());
		byte[] digest = mdig.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		String execID = sb.toString();
		// Generating hash - END

		Date currentDate = new Date();
		System.out.println("Received Processing request from IP: " + getClientIp(request) + " on " + dateFormat.format(currentDate) + " with execution ID: " + execID);

		String outputFileNameExecSummary = "executionSummaryPage_" + execID + ".html";
		String fullPathoutOutFileNameExecSummary = basePathResultStorage + outputFileNameExecSummary;
		String outputFileNameResultsHTML = "result_" + execID + ".html";
		String fullPathoutOutputFileNameResultsHTML = basePathResultStorage + outputFileNameResultsHTML;
		String outputFileNameResultsCSV_ALL = "result_" + execID + "_ALL.csv";
		String fullPathoutOutputFileNameResultsCSV_ALL = basePathResultStorage + outputFileNameResultsCSV_ALL;
		String outputFileNameResultsCSV_FEMALE = "result_" + execID + "_FEMALE.csv";
		String fullPathoutOutputFileNameResultsCSV_FEMALE = basePathResultStorage + outputFileNameResultsCSV_FEMALE;
		String outputFileNameResultsCSV_MALE = "result_" + execID + "_MALE.csv";
		String fullPathoutOutputFileNameResultsCSV_MALE = basePathResultStorage + outputFileNameResultsCSV_MALE;
		String outputFileNameResultsZIP = "result_" + execID + ".zip";
		String fullPathoutOutputFileNameResultsZIP = basePathResultStorage + outputFileNameResultsZIP;

		System.out.println(execID + " > Results will be stored to:");
		System.out.println(execID + "       > " + fullPathoutOutFileNameExecSummary);
		System.out.println(execID + "       > " + fullPathoutOutputFileNameResultsHTML);
		System.out.println(execID + "       > " + fullPathoutOutputFileNameResultsCSV_ALL);
		if(so != null && so.isGenderEnabled()) {
			System.out.println(execID + "       > " + fullPathoutOutputFileNameResultsCSV_FEMALE);
			System.out.println(execID + "       > " + fullPathoutOutputFileNameResultsCSV_MALE);
		}
		System.out.println(execID + "       > " + fullPathoutOutputFileNameResultsZIP);


		PrintWriter outRedirect = response.getWriter();

		outRedirect.println("<!DOCTYPE html>");
		outRedirect.println("<html>");
		outRedirect.println("<head>");
		// outRedirect.println("<meta http-equiv='refresh' content='1;url=" + "results?eid=" + execID + "' />");
		outRedirect.println("<script>");
		outRedirect.println("setTimeout(function(){");
		outRedirect.println("  window.location='results?eid=" + execID + "&type=summary'; ");
		outRedirect.println("}, 5000);");
		outRedirect.println("</script>");
		outRedirect.println("</head>");
		outRedirect.println("<body>");
		outRedirect.println("Comorbidity4web: you're going to be redirected to the result Web page...<br/>");
		outRedirect.println("If the result web page doesn't open in 5 seconds, please clinci on <a href='results?eid=" + execID + "&type=summary'>this link</a>.");
		outRedirect.println("</body>");
		outRedirect.println("</html>");
		outRedirect.flush();

		// Get output file summary
		File fileExecSummary = new File(fullPathoutOutFileNameExecSummary);
		System.out.println("Storing results to temporal file: " + fileExecSummary.getAbsolutePath());
		if(fileExecSummary != null && fileExecSummary.exists() && fileExecSummary.isFile()) {
			fileExecSummary.delete();
		}
		fileExecSummary.createNewFile();

		// creates a FileWriter Object
		PrintWriter outExecSummary = new PrintWriter(fileExecSummary);

		// Print common header
		outExecSummary.println(TemplateUtils.generateHTMLcommonHeader(false, execID));
		outExecSummary.flush();

		outExecSummary.println("<script>");
		outExecSummary.println("setTimeout(function(){");
		outExecSummary.println("   if( $('#placeholderHidden').length == 0 ) { window.location.reload(1); }");
		outExecSummary.println("}, 10000);");
		outExecSummary.println("</script>");
		outExecSummary.println("<div style='color: green; border: 1px black solid; padding: 10px; margin: 15px;'>");
		outExecSummary.println("<b><span style='color:#980027;'>From this URL, the progress of comorbidity analysis can be monitored and then the result can be accessed</span></b><br/>");
		outExecSummary.println("<b>This page will automatically be reloaded every 10 seconds, up to the termination of the comorbidity data analysis in the server.</span></b><br/><br/>");
		outExecSummary.println("<b><span style='color:#980027'><b>Depending on the size of your dataset and the load of the server, the data processing could take several minutes: "
				+ "do not close this tab in the meanwhile!</b></span><br/><br/>");


		if(ServerExecConfig.isOnline) {
			outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;You are executing an online analysis of comorbidities. The server "
					+ "has limited resources and can process up to " + ServerExecConfig.maxNumberComorbidityPairsToAnalyze + " pairs of diagnoses. Please, consider to "
					+ "execute locally <a href='https://comorbidity4j.readthedocs.io/en/latest/' traget='_blank'>comorbidity4web</a> to perform more extensive alanyses if these limitations may cause some problem.<br/><br/>");
			outExecSummary.flush();
		}

		outExecSummary.println("<span style='color:blue;'><h2>Data analysis progress messages</h2></span>");
		outExecSummary.flush();



		executeAnalysis(outExecSummary, so, execID,
				fullPathoutOutFileNameExecSummary, fullPathoutOutputFileNameResultsHTML, fullPathoutOutputFileNameResultsCSV_ALL,
				fullPathoutOutputFileNameResultsCSV_FEMALE, fullPathoutOutputFileNameResultsCSV_MALE, fullPathoutOutputFileNameResultsZIP);

		outRedirect.close();
		System.gc();
	}


	public Triple<ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>> executeAnalysis(PrintWriter outExecSummary, UserInputContainer so, String execID,
			String fullPathoutOutFileNameExecSummary, String fullPathoutOutputFileNameResultsHTML, String fullPathoutOutputFileNameResultsCSV_ALL,
			String fullPathoutOutputFileNameResultsCSV_FEMALE, String fullPathoutOutputFileNameResultsCSV_MALE, String fullPathoutOutputFileNameResultsZIP) {

		long startTimeGlobal_PROFILING = -1l;
		long startTimeAll_PROFILING = -1l;
		long endTimeAll_PROFILING = -1l;
		long startTimeFemale_PROFILING = -1l;
		long endTimeFemale_PROFILING = -1l;
		long startTimeMale_PROFILING = -1l;
		long endTimeMale_PROFILING = -1l;
		long startTimeOutputGen_PROFILING = -1l;
		long endTimeOutputGen_PROFILING = -1l;


		// Check if session is still alive
		if(so == null) {

			// Alert user and return
			if(outExecSummary != null) {
				outExecSummary.println("<h1>Your session has expired!</h1>");
				outExecSummary.println("<h3>Go to <a href=\"/\" target=\"_blank\">home</a>");


				outExecSummary.println("</div>");
				outExecSummary.println("<div id='placeholderHidden'></div>");
				outExecSummary.println("</body>");
				outExecSummary.println("</html>");

				outExecSummary.flush();
				outExecSummary.close();
			}
			else {
				System.out.println("Your session has expired!?!");
			}

			return Triple.of(null, null, null);
		}

		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_ALL = ImmutablePair.of(null, null);
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_FEMALE = ImmutablePair.of(null, null);
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_MALE = ImmutablePair.of(null, null);

		try {

			startTimeGlobal_PROFILING = System.currentTimeMillis();

			ComorbidityMiner currentExecutor = new ComorbidityMiner();
			currentExecutor.setEnableMultithread(true);
			currentExecutor.setNumThreads(4);

			currentExecutor.setPatientAgeComputation(so.getPatientAgeComputation_p());
			currentExecutor.setPvalAdjApproach(so.getPvalAdjApproach_p());

			currentExecutor.setGenderEnabled(so.isGenderEnabled());
			currentExecutor.setSexRatioFemaleIdentifier(so.getFemaleIdentifier_p());
			currentExecutor.setSexRatioMaleIdentifier(so.getMaleIdentifier_p());

			// Patient filter
			currentExecutor.setPatientFilter(so.getPatientFilter());
			currentExecutor.setScoreFilter(so.getComorbidityScoreFilter());
			currentExecutor.setDirectionalityFilter(so.getComorbidityDirectionalityFilter());
			
			currentExecutor.setRelativeRiskConfindeceInterval(so.getRelativeRiskConfindeceInterval_p());
			currentExecutor.setOddsRatioConfindeceInterval(so.getOddsRatioConfindeceInterval_p());

			String reduceComorbidityAnalysis = "";

			// Load and index data
			if(outExecSummary != null) {
				outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 1 / 8 - Comorbidity4web: loading data...</b>");
				outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
				outExecSummary.flush();
			}

			System.gc();

			String dataLoadResults = currentExecutor.loadAndIndexData(so);

			// Check at most 30,000 disease pairs to analyze
			// Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> estimatedNumPairsRes = currentExecutor.loadComorbidityPaitsToAnalyze(false);

			/* NOT THREADED
					Pair<ComorbidityMinerCache, List<Pair<Integer, Integer>>> comoCacheAndPairs = currentExecutor.loadComorbidityPaitsToAnalyze(true);
			 */

			startTimeAll_PROFILING = System.currentTimeMillis();

			LoadDataThread loadDataResult_ALL = new LoadDataThread(currentExecutor, true, null);
			Future<Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>>> loadData_Future_ALL = executor.submit(loadDataResult_ALL);
			String waitMessage = "STEP 2 / 8 - Comorbidity4web: pre-processing pairs of diagnoses (females + males)...";
			waitForFuture_preproc(loadData_Future_ALL, outExecSummary, waitMessage, currentExecutor);
			Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> comoCacheAndPairs_ALL = loadData_Future_ALL.get();

			if(outExecSummary != null) {
				outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
				outExecSummary.flush();
			}

			System.gc();

			dataLoadResults += generateDataLoadInfoString(currentExecutor, comoCacheAndPairs_ALL, execID);


			int estimatedNumPairs_ALL = 0;
			for(Entry<Integer, Set<Integer>> disPair : comoCacheAndPairs_ALL.getRight().entrySet()) {
				if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
					estimatedNumPairs_ALL += disPair.getValue().size();
				}
			}
			System.out.println("Estimated com pair (males + females): " + estimatedNumPairs_ALL);

			if(outExecSummary != null) {
				outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 2 / 8 - Comorbidity4web: Estimated number of comorbidity pairs to process (females + males)</b>:&nbsp;" + estimatedNumPairs_ALL + "<br/>");
				outExecSummary.flush();

				if(estimatedNumPairs_ALL > 300000) {
					outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 2 / 8 - Comorbidity4web: <span style='color:red;'>You're going to analyze more than 300,000 pairs of diagnoses (males + females). This could take some time. You can monitor through this web page how tha analysis proceeds and then access its results.</span><br/>");
					outExecSummary.flush();
				}
			}



			/*
			if(estimatedNumPairs > 30000) {
				String errorAlert = "Attention: the number of disease-pairs in your dataset to be analyzed for comorbidities is " + estimatedNumPairs + ".<br/>"
						+ "Comorbidity4web currently supports the analysis of at most 30,000 disease pairs.<br/>"
						+ "You can better scope the set of diseases you would like to analyze for comorbidity and how they should be paired with oter diseases, by providing the "
						+ "Diagnosis Pairing File - see documentation at: <a href='http://comorbidity4j.readthedocs.io/en/latest/DiagnosisPairingConfig/#diagnosis-pairing-file' target='_blank'>http://comorbidity4j.readthedocs.io/en/latest/DiagnosisPairingConfig/#diagnosis-pairing-file</a>.<br/><br/>"
						+ "Please, use the Java tool comorbidity4j (<a href='http://comorbidity4j.readthedocs.io' target='_blank'>http://comorbidity4j.readthedocs.io/</a>). "
						+ "to performe analysis of comorbidities over larger sets of disease pairs.";
				out.println(TemplateUtils.generateHTMLwebFormTemplate(errorAlert));
				out.flush();
				return;
			}
			 */

			// Generate gender-independent analysis - START
			/* NOT THREADED
					resultPairAnalysisMap_ALL = currentExecutor.executeAnalysis(null, comoCacheAndPairs);
			 */

			ExecuteAnalysisThread executeAnalysisResult_ALL = new ExecuteAnalysisThread(currentExecutor, null, comoCacheAndPairs_ALL);
			Future<ImmutablePair<String, Collection<ComorbidityPairResult>>> executeAnalysisResult_ALL_Future = executor.submit(executeAnalysisResult_ALL);
			waitMessage = "STEP 3 / 8 - Comorbidity4web: executing analysis of " + estimatedNumPairs_ALL + " pairs of diagnoses (females + males)...";
			waitForFuture(executeAnalysisResult_ALL_Future, outExecSummary, waitMessage, currentExecutor);
			resultPairAnalysisMap_ALL = executeAnalysisResult_ALL_Future.get();

			if(outExecSummary != null) {
				outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
				outExecSummary.flush();
			}

			System.gc();

			endTimeAll_PROFILING = System.currentTimeMillis();

			if(resultPairAnalysisMap_ALL == null || (resultPairAnalysisMap_ALL.getLeft() != null && resultPairAnalysisMap_ALL.getLeft().trim().length() > 0)) {
				System.out.println("Error while executing comorbidity analysis (Web viz. - ALL): \n" + ((resultPairAnalysisMap_ALL != null) ? resultPairAnalysisMap_ALL.getLeft() : "---"));
			}
			// Generate gender-independent analysis - END

			if(currentExecutor.isGenderEnabled()) {

				startTimeFemale_PROFILING = System.currentTimeMillis();

				// Generate only males and only females analysis - START
				/* NOT THREADED
				resultPairAnalysisMap_FEMALE = currentExecutor.executeAnalysis(true, comoCacheAndPairs);
				 */

				LoadDataThread loadDataResult_FEMALE = new LoadDataThread(currentExecutor, true, true);
				Future<Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>>> loadData_Future_FEMALE = executor.submit(loadDataResult_FEMALE);
				waitMessage = "STEP 4 / 8 - Comorbidity4web: pre-processing pairs of diagnoses (females)...";
				waitForFuture_preproc(loadData_Future_FEMALE, outExecSummary, waitMessage, currentExecutor);
				Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> comoCacheAndPairs_FEMALE = loadData_Future_FEMALE.get();
				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
					outExecSummary.flush();
				}

				System.gc();

				// dataLoadResults += generateDataLoadInfoString(currentExecutor, comoCacheAndPairs_FEMALE, execID);


				int estimatedNumPairs_FEMALE = 0;
				for(Entry<Integer, Set<Integer>> disPair : comoCacheAndPairs_FEMALE.getRight().entrySet()) {
					if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
						estimatedNumPairs_FEMALE += disPair.getValue().size();
					}
				}
				System.out.println("Estimated com pair: " + estimatedNumPairs_FEMALE);

				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 4 / 8 - Comorbidity4web: Estimated number of comorbidity pairs to process (females)</b>:&nbsp;" + estimatedNumPairs_FEMALE + "<br/>");
					outExecSummary.flush();

					if(estimatedNumPairs_FEMALE > 300000) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 4 / 8 - Comorbidity4web: <span style='color:red;'>You're going to analyze more than 300,000 pairs of diagnoses (females). This could take some time. You can monitor through this web page how tha analysis proceeds and then access its results.</span><br/>");
						outExecSummary.flush();
					}
				}


				ExecuteAnalysisThread executeAnalysisResult_FEMALE = new ExecuteAnalysisThread(currentExecutor, true, comoCacheAndPairs_FEMALE);
				Future<ImmutablePair<String, Collection<ComorbidityPairResult>>> executeAnalysisResult_FEMALE_Future = executor.submit(executeAnalysisResult_FEMALE);
				waitMessage = "STEP 5 / 8 - Comorbidity4web: executing analysis of " + estimatedNumPairs_FEMALE + " pairs of diagnoses (females)...";
				waitForFuture(executeAnalysisResult_FEMALE_Future, outExecSummary, waitMessage, currentExecutor);
				resultPairAnalysisMap_FEMALE = executeAnalysisResult_FEMALE_Future.get();

				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
					outExecSummary.flush();
				}

				System.gc();


				endTimeFemale_PROFILING = System.currentTimeMillis();

				if(resultPairAnalysisMap_FEMALE == null || (resultPairAnalysisMap_FEMALE.getLeft() != null && resultPairAnalysisMap_FEMALE.getLeft().trim().length() > 0)) {
					System.out.println("Error while executing comorbidity analysis (Web viz. - FEMALE): \n" + ((resultPairAnalysisMap_FEMALE != null) ? resultPairAnalysisMap_FEMALE.getLeft() : "---"));
				}

				/* NOT THREADED
						resultPairAnalysisMap_MALE = currentExecutor.executeAnalysis(false, comoCacheAndPairs);
				 */

				startTimeMale_PROFILING = System.currentTimeMillis();

				LoadDataThread loadDataResult_MALE = new LoadDataThread(currentExecutor, true, false);
				Future<Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>>> loadData_Future_MALE = executor.submit(loadDataResult_MALE);
				waitMessage = "STEP 6 / 8 - Comorbidity4web: pre-processing pairs of diagnoses (males)...";
				waitForFuture_preproc(loadData_Future_MALE, outExecSummary, waitMessage, currentExecutor);
				Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> comoCacheAndPairs_MALE = loadData_Future_MALE.get();

				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
					outExecSummary.flush();
				}

				System.gc();

				// dataLoadResults += generateDataLoadInfoString(currentExecutor, comoCacheAndPairs_MALE, execID);


				int estimatedNumPairs_MALE = 0;
				for(Entry<Integer, Set<Integer>> disPair : comoCacheAndPairs_MALE.getRight().entrySet()) {
					if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
						estimatedNumPairs_MALE += disPair.getValue().size();
					}
				}
				System.out.println("Estimated com pair: " + estimatedNumPairs_MALE);

				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 6 / 8 - Comorbidity4web: Estimated number of comorbidity pairs to process (males)</b>:&nbsp;" + estimatedNumPairs_MALE + "<br/>");
					outExecSummary.flush();

					if(estimatedNumPairs_MALE > 300000) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>STEP 6 / 8 - Comorbidity4web: <span style='color:red;'>You're going to analyze more than 300,000 pairs of diagnoses (males). This could take some time. You can monitor through this web page how tha analysis proceeds and then access its results.</span><br/>");
						outExecSummary.flush();
					}
				}

				ExecuteAnalysisThread executeAnalysisResult_MALE = new ExecuteAnalysisThread(currentExecutor, true, comoCacheAndPairs_MALE);
				Future<ImmutablePair<String, Collection<ComorbidityPairResult>>> executeAnalysisResult_MALE_Future = executor.submit(executeAnalysisResult_MALE);
				waitMessage = "STEP 7 / 8 - Comorbidity4web: executing analysis of " + estimatedNumPairs_MALE + " pairs of diagnoses (males)...";
				waitForFuture(executeAnalysisResult_MALE_Future, outExecSummary, waitMessage, currentExecutor);
				resultPairAnalysisMap_MALE = executeAnalysisResult_MALE_Future.get();

				if(outExecSummary != null) {
					outExecSummary.println("&nbsp;<b>COMPLETED</b><br/>");
					outExecSummary.flush();
				}

				System.gc();

				endTimeMale_PROFILING = System.currentTimeMillis();

				if(resultPairAnalysisMap_MALE == null || (resultPairAnalysisMap_MALE.getLeft() != null && resultPairAnalysisMap_MALE.getLeft().trim().length() > 0)) {
					System.out.println("Error while executing comorbidity analysis (Web viz. - MALE): \n" + ((resultPairAnalysisMap_MALE != null) ? resultPairAnalysisMap_MALE.getLeft() : "---"));
				}
				// Generate only males and only females analysis - END

			}

			if(resultPairAnalysisMap_ALL == null || (resultPairAnalysisMap_ALL.getLeft() != null && resultPairAnalysisMap_ALL.getLeft().trim().length() > 0)) {
				if(outExecSummary != null) {
					outExecSummary.println(TemplateUtils.generateHTMLwebFormError(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_ALL != null) ? resultPairAnalysisMap_ALL.getLeft() : "---")));
					outExecSummary.flush();
				}
				else {
					System.out.println(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_ALL != null) ? resultPairAnalysisMap_ALL.getLeft() : "---"));
				}
			}
			else if(currentExecutor.isGenderEnabled() && (resultPairAnalysisMap_FEMALE == null || (resultPairAnalysisMap_FEMALE.getLeft() != null && resultPairAnalysisMap_FEMALE.getLeft().trim().length() > 0)) ) {
				if(outExecSummary != null) {
					outExecSummary.println(TemplateUtils.generateHTMLwebFormError(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_FEMALE != null) ? resultPairAnalysisMap_FEMALE.getLeft() : "---")));
					outExecSummary.flush();
				}
				else {
					System.out.println(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_FEMALE != null) ? resultPairAnalysisMap_FEMALE.getLeft() : "---"));
				}

			}
			else if(currentExecutor.isGenderEnabled() && (resultPairAnalysisMap_MALE == null || (resultPairAnalysisMap_MALE.getLeft() != null && resultPairAnalysisMap_MALE.getLeft().trim().length() > 0)) ) {
				if(outExecSummary != null) {
					outExecSummary.println(TemplateUtils.generateHTMLwebFormError(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_MALE != null) ? resultPairAnalysisMap_MALE.getLeft() : "---")));
					outExecSummary.flush();
				}
				else {
					System.out.println(dataLoadResults + "\n\n" + 
							"Error while executing comorbidity analysis: \n" + ((resultPairAnalysisMap_MALE != null) ? resultPairAnalysisMap_MALE.getLeft() : "---"));
				}
			}
			else if(fullPathoutOutFileNameExecSummary != null && fullPathoutOutputFileNameResultsHTML != null && fullPathoutOutputFileNameResultsCSV_ALL != null && 
					fullPathoutOutputFileNameResultsCSV_FEMALE != null && fullPathoutOutputFileNameResultsCSV_MALE != null && fullPathoutOutputFileNameResultsZIP != null) {
				// Only here generate the analysis results

				startTimeOutputGen_PROFILING = System.currentTimeMillis();

				/* NOT THREADED
						out.println(TemplateUtils.generateHTMLanalysisResTemplate(currentExecutor, resultPairAnalysisMap_ALL.getRight(),
							resultPairAnalysisMap_FEMALE.getRight(), resultPairAnalysisMap_MALE.getRight(),
							dataLoadResults, reduceComorbidityAnalysis, false));
				 */
				waitMessage = "STEP 8 / 8 - Comorbidity4web: generating output...";
				Future<String> comorbidityAnalysisThreadResult_ALL = executor.submit(new ComputeComorbidityThread(currentExecutor, resultPairAnalysisMap_ALL.getRight(),
						resultPairAnalysisMap_FEMALE.getRight(), resultPairAnalysisMap_MALE.getRight(),
						dataLoadResults, ((StringUtils.isNotBlank(reduceComorbidityAnalysis)) ? reduceComorbidityAnalysis : null), false, execID, 
						"<ul><li>" + fullPathoutOutputFileNameResultsHTML + "</li><li>" + fullPathoutOutFileNameExecSummary + "</li><li>" + fullPathoutOutputFileNameResultsCSV_ALL + 
						((currentExecutor.isGenderEnabled()) ? "</li><li>" + fullPathoutOutputFileNameResultsCSV_FEMALE + "</li><li>" + fullPathoutOutputFileNameResultsCSV_MALE : "") + "</li><li>" + fullPathoutOutputFileNameResultsZIP + "</li></ul>", 
						true, 9000) );

				waitForFuture(comorbidityAnalysisThreadResult_ALL, outExecSummary, waitMessage);

				if(outExecSummary != null) {
					outExecSummary.flush();
					outExecSummary.println("&nbsp;<b>COMPLETED</b><br/><br/>");
					outExecSummary.flush();
				}

				System.gc();
				Thread.sleep(100);

				String comorbidity4webResults = comorbidityAnalysisThreadResult_ALL.get();

				// Here in the string comorbidity4webResults got the output of the execution

				// 1) Store the HTML output
				try {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>Storing the HTML interactive visualization file, please wait...</span></b><br/>");
						outExecSummary.flush();
					}

					File outputFileResultsHTML = new File(fullPathoutOutputFileNameResultsHTML);
					outputFileResultsHTML.createNewFile();

					FileOutputStream outputFileResultsHTMLos = new FileOutputStream(outputFileResultsHTML.getAbsoluteFile(), false);
					try(Writer writer = Channels.newWriter(outputFileResultsHTMLos.getChannel(), "UTF-8")) {
						writer.append(TemplateUtils.generateHTMLcommonHeader(false, execID));
						writer.append(comorbidity4webResults);
					}

					outputFileResultsHTMLos.close();

					/* APPROACH 2:
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathoutOutputFileNameResultsHTML), "UTF-8"));
					try {
					    out.write(comorbidity4webResults);
					    out.flush();
					} finally {
					    out.close();
					}
					 */
				}
				catch(Exception e) {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>An error occurred while storing the HTML interactive visualization file: " + ((e.getMessage() != null) ? e.getMessage() : "---") + "</span></b><br/>");
						outExecSummary.flush();
					}
				}


				// 2) Store the CSV output file (ALL, FEMALE, MALE)
				try {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>Storing the CSV result file with ALL results, please wait...</span></b><br/>");
						outExecSummary.flush();
					}

					File outputFileResultsCSV = new File(fullPathoutOutputFileNameResultsCSV_ALL);
					outputFileResultsCSV.createNewFile();

					FileOutputStream outputFileResultsCSVos = new FileOutputStream(outputFileResultsCSV.getAbsoluteFile(), false);

					try(Writer writer = Channels.newWriter(outputFileResultsCSVos.getChannel(), "UTF-8")) {
						writer.write(ComorbidityPairResult.toCSVlineHeader(decimFormatThreeDec.format(currentExecutor.getRelativeRiskConfindeceInterval()) ,
								decimFormatThreeDec.format(currentExecutor.getOddsRatioConfindeceInterval())) + "\n");
						writer.flush();

						Integer totalLinesToWrite = resultPairAnalysisMap_ALL.getValue().size();
						Integer totalLinesWritten = 0;
						DecimalFormat decimFormat = new DecimalFormat("#.000");
						Iterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap_ALL.getValue().iterator();
						while(resultPairAnalysisMapIterator.hasNext()){
							ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.next();
							if(comoPair != null && comoPair.getFisherTest() != null) {
								try {
									writer.write(comoPair.toCSVline() + "\n");
									writer.flush();

									totalLinesWritten++;
									if(totalLinesWritten % 10000 == 0) {
										System.out.println("   Stored " + totalLinesWritten + " ALL CSV lines over " + totalLinesToWrite + 
												" (" + ((totalLinesToWrite != null) ? decimFormat.format( (100d * (double) totalLinesWritten) / (double) totalLinesToWrite) : "-" ) + " %)");
									}
								}
								catch(Exception e) {
									e.printStackTrace();
									System.out.println("Error while storing comorbidity analysis results of the pair of diseases (ALL CSV): " + (comoPair != null ? comoPair : "null")  + ".");
								}
							}
							else {
								System.out.println("Error: (ALL CSV) the comorbidity pair has null analysis results; impossible to store this piece of information.");
							}
						}
					}
					finally {
						outputFileResultsCSVos.close();
					}

				}
				catch(Exception e) {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>An error occurred while storing the CSV result file with ALL results: " + ((e.getMessage() != null) ? e.getMessage() : "---") + "</span></b><br/>");
						outExecSummary.flush();
					}
				}


				if(currentExecutor.isGenderEnabled()) {

					try {
						if(outExecSummary != null) {
							outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>Storing the CSV result file with FEMALE results, please wait...</span></b><br/>");
							outExecSummary.flush();
						}

						File outputFileResultsCSV = new File(fullPathoutOutputFileNameResultsCSV_FEMALE);
						outputFileResultsCSV.createNewFile();

						FileOutputStream outputFileResultsCSVos = new FileOutputStream(outputFileResultsCSV.getAbsoluteFile(), false);

						try(Writer writer = Channels.newWriter(outputFileResultsCSVos.getChannel(), "UTF-8")) {
							writer.write(ComorbidityPairResult.toCSVlineHeader(decimFormatThreeDec.format(currentExecutor.getRelativeRiskConfindeceInterval()) ,
									decimFormatThreeDec.format(currentExecutor.getOddsRatioConfindeceInterval())) + "\n");
							writer.flush();

							Integer totalLinesToWrite = resultPairAnalysisMap_FEMALE.getValue().size();
							Integer totalLinesWritten = 0;
							DecimalFormat decimFormat = new DecimalFormat("#.000");
							Iterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap_FEMALE.getValue().iterator();
							while(resultPairAnalysisMapIterator.hasNext()){
								ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.next();
								if(comoPair != null && comoPair.getFisherTest() != null) {
									try {
										writer.write(comoPair.toCSVline() + "\n");
										writer.flush();

										totalLinesWritten++;
										if(totalLinesWritten % 10000 == 0) {
											System.out.println("   Stored " + totalLinesWritten + " ALL CSV lines over " + totalLinesToWrite + 
													" (" + ((totalLinesToWrite != null) ? decimFormat.format( (100d * (double) totalLinesWritten) / (double) totalLinesToWrite) : "-" ) + " %)");
										}
									}
									catch(Exception e) {
										e.printStackTrace();
										System.out.println("Error while storing comorbidity analysis results of the pair of diseases (ALL CSV): " + (comoPair != null ? comoPair : "null")  + ".");
									}
								}
								else {
									System.out.println("Error: (ALL CSV) the comorbidity pair has null analysis results; impossible to store this piece of information.");
								}
							}
						}
						finally {
							outputFileResultsCSVos.close();
						}

					}
					catch(Exception e) {
						if(outExecSummary != null) {
							outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>An error occurred while storing the CSV result file with FEMALE results: " + ((e.getMessage() != null) ? e.getMessage() : "---") + "</span></b><br/>");
							outExecSummary.flush();
						}
					}

					try {
						if(outExecSummary != null) {
							outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>Storing the CSV result file with MALE results, please wait...</span></b><br/>");
							outExecSummary.flush();
						}

						File outputFileResultsCSV = new File(fullPathoutOutputFileNameResultsCSV_MALE);
						outputFileResultsCSV.createNewFile();

						FileOutputStream outputFileResultsCSVos = new FileOutputStream(outputFileResultsCSV.getAbsoluteFile(), false);

						try(Writer writer = Channels.newWriter(outputFileResultsCSVos.getChannel(), "UTF-8")) {
							writer.write(ComorbidityPairResult.toCSVlineHeader(decimFormatThreeDec.format(currentExecutor.getRelativeRiskConfindeceInterval()) ,
									decimFormatThreeDec.format(currentExecutor.getOddsRatioConfindeceInterval())) + "\n");
							writer.flush();

							Integer totalLinesToWrite = resultPairAnalysisMap_MALE.getValue().size();
							Integer totalLinesWritten = 0;
							DecimalFormat decimFormat = new DecimalFormat("#.000");
							Iterator<ComorbidityPairResult> resultPairAnalysisMapIterator = resultPairAnalysisMap_MALE.getValue().iterator();
							while(resultPairAnalysisMapIterator.hasNext()){
								ComorbidityPairResult comoPair = resultPairAnalysisMapIterator.next();
								if(comoPair != null && comoPair.getFisherTest() != null) {
									try {
										writer.write(comoPair.toCSVline() + "\n");
										writer.flush();

										totalLinesWritten++;
										if(totalLinesWritten % 10000 == 0) {
											System.out.println("   Stored " + totalLinesWritten + " ALL CSV lines over " + totalLinesToWrite + 
													" (" + ((totalLinesToWrite != null) ? decimFormat.format( (100d * (double) totalLinesWritten) / (double) totalLinesToWrite) : "-" ) + " %)");
										}
									}
									catch(Exception e) {
										e.printStackTrace();
										System.out.println("Error while storing comorbidity analysis results of the pair of diseases (ALL CSV): " + (comoPair != null ? comoPair : "null")  + ".");
									}
								}
								else {
									System.out.println("Error: (ALL CSV) the comorbidity pair has null analysis results; impossible to store this piece of information.");
								}
							}
						}
						finally {
							outputFileResultsCSVos.close();
						}

					}
					catch(Exception e) {
						if(outExecSummary != null) {
							outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>An error occurred while storing the CSV result file with MALE results: " + ((e.getMessage() != null) ? e.getMessage() : "---") + "</span></b><br/>");
							outExecSummary.flush();
						}
					}

				}


				// 3) Create ZIP file
				try {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>Storing the ZIP result file, please wait...</span></b><br/>");
						outExecSummary.flush();
					}

					/* OLD CODE
					String HTMLoutput = TemplateUtils.generateHTMLcommonHeader(true, execID);
					HTMLoutput += TemplateUtils.generateHTMLanalysisResTemplate(currentExecutor, resultPairAnalysisMap_ALL.getRight(), resultPairAnalysisMap_FEMALE.getRight(), resultPairAnalysisMap_MALE.getRight(),
							dataLoadResults, null, true, execID, basePathResultStorage + "result_" + execID + "_folder");
					 */
					String webOutputDirFullPath = basePathResultStorage + "result_" + execID + "_folder";

					GenericUtils.createDir(webOutputDirFullPath);
					GenericUtils.createDir(webOutputDirFullPath + "/js");
					GenericUtils.createDir(webOutputDirFullPath + "/css");
					GenericUtils.createDir(webOutputDirFullPath + "/css/images");
					GenericUtils.createDir(webOutputDirFullPath + "/img");

					System.out.println("Storing results to temporal folder: " + webOutputDirFullPath);

					Set<String> fileToStore = new HashSet<String>();

					// ADD HTML AND CSV FILES
					fileToStore.add(fullPathoutOutFileNameExecSummary);
					fileToStore.add(fullPathoutOutputFileNameResultsHTML);
					fileToStore.add(fullPathoutOutputFileNameResultsCSV_ALL);
					if(so.isGenderEnabled()) {
						fileToStore.add(fullPathoutOutputFileNameResultsCSV_FEMALE);
						fileToStore.add(fullPathoutOutputFileNameResultsCSV_MALE);
					}

					fileToStore.add("retempl/css/" + "jquery-ui.min.css");
					fileToStore.add("retempl/css/" + "jquery-ui-slider-pips.css");
					fileToStore.add("retempl/css/" + "main.css");
					fileToStore.add("retempl/css/" + "styleNav.css");
					fileToStore.add("retempl/css/" + "tabulator.min.css");
					fileToStore.add("retempl/css/" + "tabulator.min.css.map");
					fileToStore.add("retempl/css/" + "vis.min.css");
					fileToStore.add("retempl/css/" + "selectize.css");

					fileToStore.add("retempl/img/" + "edit.ico");
					fileToStore.add("retempl/img/" + "settings.ico");

					fileToStore.add("retempl/js/" + "indexNav.js");
					fileToStore.add("retempl/js/" + "jquery.form-validator.min.js");
					fileToStore.add("retempl/js/" + "jquery-3.2.1.min.js");
					fileToStore.add("retempl/js/" + "jquery-ui.min.js");
					fileToStore.add("retempl/js/" + "jquery-ui-slider-pips.js");
					fileToStore.add("retempl/js/" + "plotly-latest.min.js");
					fileToStore.add("retempl/js/" + "tabulator.min.js");
					fileToStore.add("retempl/js/" + "vis.min.js");
					fileToStore.add("retempl/js/" + "selectize.min.js");


					fileToStore.add("retempl/css/images/" + "ui-icons_444444_256x240.png");
					fileToStore.add("retempl/css/images/" + "ui-icons_555555_256x240.png");
					fileToStore.add("retempl/css/images/" + "ui-icons_777620_256x240.png");
					fileToStore.add("retempl/css/images/" + "ui-icons_777777_256x240.png");
					fileToStore.add("retempl/css/images/" + "ui-icons_cc0000_256x240.png");
					fileToStore.add("retempl/css/images/" + "ui-icons_ffffff_256x240.png");

					for(String fileName : fileToStore) {
						try {							


							if(fileName.contains("retempl")) {
								InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
								FileUtils.copyInputStreamToFile(is, new File(webOutputDirFullPath + File.separator + fileName.replace("retempl/", "")));
							}
							else {
								InputStream is = FileUtils.openInputStream(new File(fileName));
								FileUtils.copyInputStreamToFile(is, new File(webOutputDirFullPath + File.separator + new File(fileName).getName()));
							}

							/* OLD APPROACH:
							if(fileName.endsWith(".png") || fileName.endsWith(".ico")) {
								InputStream in = (new GenericUtils()).getClass().getResourceAsStream(fileName);
								byte[] byteStream = ByteStreams.toByteArray(in);
								FileUtils.writeByteArrayToFile(new File(), byteStream);
							}
							else {
								String fileContent = GenericUtils.getFile(fileName);
								GenericUtils.storeUTF8stringToFile(fileContent, webOutputDirFullPath + File.separator + fileName.replace("retempl/", ""));
							}
							 */
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}

					FileOutputStream fos = new FileOutputStream(basePathResultStorage + "result_" + execID + ".zip");
					ZipOutputStream zos = new ZipOutputStream(fos);
					addDirToZipArchive(zos, new File(webOutputDirFullPath), null);
					zos.flush();
					fos.flush();
					zos.close();
					fos.close();

					FileUtils.deleteDirectory(new File(webOutputDirFullPath));

				}
				catch(Exception e) {
					if(outExecSummary != null) {
						outExecSummary.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>Comorbidity4web: <span style='color:#980027;'>An error occurred while storing the ZIP result file: " + ((e.getMessage() != null) ? e.getMessage() : "---") + "</span></b><br/>");
						outExecSummary.flush();
					}
				}


				// 3) Add to the exec summary page the link to the output files
				if(outExecSummary != null) {
					outExecSummary.println("");
					outExecSummary.println("<span style='color:blue;'><h2>Results file ready</h2></span>"
							+ "<ul>"
							+ "<li>HTML interactive Web-viz: <a href=\"results?eid=" + execID + "\" target=\"_blank\">Open HTML viz in new page</a></li>"
							+ ((!ServerExecConfig.isOnline) ? "&nbsp;&nbsp;&nbsp;" + "HTML interactive web viz stored locally at: " + fullPathoutOutputFileNameResultsHTML : "")  
							+ "<li>CSV table (ALL): <a href=\"results?eid=" + execID + "&type=csvALL\" target=\"_blank\">Download CSV table (ALL)</a></li>"
							+ ((!ServerExecConfig.isOnline) ? "&nbsp;&nbsp;&nbsp;" + "CSV table (ALL) stored locally at: " + fullPathoutOutputFileNameResultsCSV_ALL : "")  );
					if(so.isGenderEnabled()) {
						outExecSummary.println(
								"<li>CSV table (only FEMALE): <a href=\"results?eid=" + execID + "&type=csvFEMALE\" target=\"_blank\">Download CSV table (only FEMALE)</a></li>"
										+ ((!ServerExecConfig.isOnline) ? "&nbsp;&nbsp;&nbsp;" + "CSV table (only FEMALE) stored locally at: " + fullPathoutOutputFileNameResultsCSV_FEMALE : "")
										+ "<li>CSV table (only MALE): <a href=\"results?eid=" + execID + "&type=csvMALE\" target=\"_blank\">Download CSV table (only MALE)</a></li>"
										+ ((!ServerExecConfig.isOnline) ? "&nbsp;&nbsp;&nbsp;" + "CSV table (only MALE) stored locally at: " + fullPathoutOutputFileNameResultsCSV_MALE : "")  );
					}
					outExecSummary.println("<li>ZIP file with all results to download (stand-alone interactive web visualization and comorbidity analysis in CSV format): <a href=\"results?eid=" + execID + "&type=zip\" target=\"_blank\">Download ZIP file</a></li>"
							+ ((!ServerExecConfig.isOnline) ? "&nbsp;&nbsp;&nbsp;" + "ZIP fil stored locally at: " + fullPathoutOutputFileNameResultsZIP : "")  
							+ "</ul>");
					outExecSummary.flush();



					/*
				int totalPairsConsidered_ALL = (resultPairAnalysisMap_ALL != null && resultPairAnalysisMap_ALL.getRight() != null) ? resultPairAnalysisMap_ALL.getRight().size() : 0;
				int totalPairsConsidered_FEMALE = (resultPairAnalysisMap_FEMALE != null && resultPairAnalysisMap_FEMALE.getRight() != null) ? resultPairAnalysisMap_FEMALE.getRight().size() : 0;
				int totalPairsConsidered_MALE = (resultPairAnalysisMap_MALE != null && resultPairAnalysisMap_MALE.getRight() != null) ? resultPairAnalysisMap_MALE.getRight().size() : 0;
				if(totalPairsConsidered_ALL + totalPairsConsidered_FEMALE + totalPairsConsidered_MALE > 100000) {
					outExecSummary.println("<h2>Since you are browsing more than " + totalPairsConsidered_ALL + " the Web visualization con experiment some slow down in older browsers!</h2>");
					outExecSummary.flush();
				}
					 */

					outExecSummary.println("</div>");
					outExecSummary.println("<div id='placeholderHidden'></div>");
					outExecSummary.println("</body>");
					outExecSummary.println("</html>");

					outExecSummary.flush();
					outExecSummary.close();
				}

				endTimeOutputGen_PROFILING = System.currentTimeMillis();

			}
			else {
				System.out.println("No output storage to file");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			outExecSummary.println(TemplateUtils.generateHTMLwebFormError("An exception occurred while processing data: " + e.getMessage()));
			outExecSummary.flush();
			outExecSummary.close();
		}

		/*
		long startTimeGlobal_PROFILING = -1l;
		long startTimeAll_PROFILING = -1l;
		long endTimeAll_PROFILING = -1l;
		long startTimeFemale_PROFILING = -1l;
		long endTimeFemale_PROFILING = -1l;
		long startTimeMale_PROFILING = -1l;
		long endTimeMale_PROFILING = -1l;
		long startTimeOutputGen_PROFILING = -1l;
		long endTimeOutputGen_PROFILING = -1l;
		 */
		
		System.out.println(" ************************************************************** ");
		System.out.println(" *******************   PROFILING EXEC TIME ******************** ");
		System.out.println(" > Global execution time: " + ((float) (endTimeOutputGen_PROFILING - startTimeGlobal_PROFILING) / 1000f) + " seconds.");
		System.out.println(" > ALL computation: " + ((float) (endTimeAll_PROFILING - startTimeAll_PROFILING) / 1000f) + " seconds.");
		System.out.println("       > Considered " + ((resultPairAnalysisMap_ALL != null && resultPairAnalysisMap_ALL.getRight() != null) ? resultPairAnalysisMap_ALL.getRight().size() : "ERROR") + " comorbidity pairs.");
		System.out.println(" > FEMALE computation: " + ((float) (endTimeFemale_PROFILING - startTimeFemale_PROFILING) / 1000f) + " seconds.");
		System.out.println("       > Considered " + ((resultPairAnalysisMap_FEMALE != null && resultPairAnalysisMap_FEMALE.getRight() != null) ? resultPairAnalysisMap_FEMALE.getRight().size() : "ERROR") + " comorbidity pairs.");
		System.out.println(" > MALE computation: " + ((float) (endTimeMale_PROFILING - startTimeMale_PROFILING) / 1000f) + " seconds.");
		System.out.println("       > Considered " + ((resultPairAnalysisMap_MALE != null && resultPairAnalysisMap_MALE.getRight() != null) ? resultPairAnalysisMap_MALE.getRight().size() : "ERROR") + " comorbidity pairs.");
		System.out.println(" > OUTPUT GENERATION: " + ((float) (endTimeOutputGen_PROFILING - startTimeOutputGen_PROFILING) / 1000f) + " seconds.");
		System.out.println(" ************************************************************** ");
		System.out.println(" ************************************************************** ");


		return org.apache.commons.lang3.tuple.Triple.of(resultPairAnalysisMap_ALL, resultPairAnalysisMap_FEMALE, resultPairAnalysisMap_MALE);
	}

	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName !=null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			System.out.println("+" + zipEntryName);
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			System.out.println("   " + zipEntryName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}

	private static void deleteLinesFromFile(String filename, int startline, int numlines, String outputFileName, boolean deleteInputFile, boolean appendCode) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));

			//String buffer to store contents of the file
			StringBuffer sb=new StringBuffer("");

			//Keep track of the line number
			int linenumber=1;
			String line;

			boolean foundTrigger = false;
			while((line=br.readLine())!=null) {

				//Store each valid line in the string buffer
				if(linenumber<startline||(foundTrigger && linenumber>=startline+numlines)) {
					sb.append(line+"\n");
				}

				if(!foundTrigger && line.contains("triggerDivDEL")) 
					foundTrigger = true;

				if(linenumber == startline && appendCode) {
					sb.append("<script>"+"\n");
					sb.append("$(document).ready(function() {"+"\n");
					sb.append("       $('.fileStoreInfoTD').remove();"+"\n");
					sb.append("});"+"\n");
					sb.append("</script>"+"\n");
				}

				linenumber++;
			}
			if(startline+numlines>linenumber)
				System.out.println("End of file reached.");
			br.close();

			FileWriter fw=new FileWriter(new File(outputFileName));
			//Write entire string buffer into the file
			fw.write(sb.toString());
			fw.close();

			if(deleteInputFile) {
				File inputFileToDel = new File(filename);
				inputFileToDel.delete();
			}
		}
		catch (Exception e) {
			System.out.println("Something went wrong when deleting lines from file " + ((!Strings.isNullOrEmpty(filename)) ? filename : "NULL") + " : " + e.getMessage());
		}
	}


	private void waitForFuture(Future futInstance, PrintWriter out, String waitMessage) {

		if(out != null) {
			out.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>" + waitMessage + "&nbsp; // Time elapsed in seconds, JVM total memory //:&nbsp;</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			out.flush();
		}
		else {
			System.out.println(" - " + waitMessage + " // Time elapsed in seconds, JVM total memory //");
		}

		int waitCycleNum = 0;
		long stratTime = System.currentTimeMillis();
		try {
			if(out != null) {
				out.println("<span style='color:black;'>");
				out.flush();
			}
			while(!futInstance.isDone()) {
				waitCycleNum++;
				if(waitCycleNum % 200 == 1) {
					Runtime instance = Runtime.getRuntime();
					long totalMemoryMb = instance.totalMemory() / (1024 * 1024);
					if(out != null) {
						out.println("<b>" + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb.</b>&nbsp;&gt;&nbsp;");
					}
					else {
						System.out.println(" - " + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb.");
					}
				}
				if(out != null) {
					out.flush();
				}
				Thread.sleep(50);
			}
			if(out != null) {
				out.println("</span>");
				out.flush();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void waitForFuture_preproc(Future futInstance, PrintWriter out, String waitMessage, ComorbidityMiner cm) {

		if(out != null) {
			out.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>" + waitMessage + "&nbsp; // Pairs checked / pairs selected to analyze (time elapsed in seconds - s., JVM total memory) //:&nbsp;</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			out.flush();
		}
		else {
			System.out.println(" - " + waitMessage + " // Pairs checked / pairs selected to analyze (time elapsed in seconds - s., JVM total memory) //");
		}

		int waitCycleNum = 0;
		long stratTime = System.currentTimeMillis();
		try {
			if(out != null) {
				out.println("<span style='color:black;'>");
				out.flush();
			}
			while(!futInstance.isDone()) {
				waitCycleNum++;
				if(waitCycleNum % 200 == 1) {
					Runtime instance = Runtime.getRuntime();
					long totalMemoryMb = instance.totalMemory() / (1024 * 1024);
					if(out != null) {
						out.println("<b>" + NumberFormat.getNumberInstance(Locale.US).format(cm.pairConsidered_preproc.get()) + 
								" checkd of which " + NumberFormat.getNumberInstance(Locale.US).format(cm.pairSelected_preproc.get()) + 
								" selected (" + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb)</b>&nbsp;&gt;&nbsp;");
					}
					else {
						System.out.println(" - " + NumberFormat.getNumberInstance(Locale.US).format(cm.pairConsidered_preproc.get()) + 
								" checkd of which " + NumberFormat.getNumberInstance(Locale.US).format(cm.pairSelected_preproc.get()) + 
								" selected (" + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb)");
					}
				}
				if(out != null) {
					out.flush();
				}
				Thread.sleep(50);
			}

			if(out != null) {
				out.println("</span>");
				out.flush();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	private static DecimalFormat decimFormat = new DecimalFormat("00.000");
	private void waitForFuture(Future futInstance, PrintWriter out, String waitMessage, ComorbidityMiner cm) {

		if(out != null) {
			out.println("&nbsp;&nbsp;&nbsp;<b>&gt;</b>&nbsp;<b>" + waitMessage + "&nbsp; // Step completion percentage (time elapsed in seconds - s., JVM total memory) //:&nbsp;</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			out.flush();
		}
		else {
			System.out.println(" - " + waitMessage + " // Step completion percentage (time elapsed in seconds - s., JVM total memory) //");
		}

		int waitCycleNum = 0;
		long stratTime = System.currentTimeMillis();
		try {
			if(out != null) {
				out.println("<span style='color:black;'>");
				out.flush();
			}
			while(!futInstance.isDone()) {
				waitCycleNum++;
				if(waitCycleNum % 200 == 1) {
					Runtime instance = Runtime.getRuntime();
					long totalMemoryMb = instance.totalMemory() / (1024 * 1024);

					double percentageProcessed = (cm.totalPairsToProcessCounter > 0) ? (double) cm.processedPairsCounter.get() / (double) cm.totalPairsToProcessCounter : 0d;
					if(out != null) {
						out.println("<b>" + decimFormat.format(percentageProcessed * 100d)  + "% (" + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb)</b>&nbsp;&gt;&nbsp;");
					}
					else {
						System.out.println(" - " + decimFormat.format(percentageProcessed * 100d)  + "% (" + ((int) ((System.currentTimeMillis() - stratTime) / 1000l)) + " s, JVM mem: " + totalMemoryMb + " Mb)");
					}
				}
				if(out != null) {
					out.flush();
				}
				Thread.sleep(50);
			}

			if(out != null) {
				out.println("</span>");
				out.flush();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	private static int countLines(String str) {
		if(str == null) {
			return 0;
		}
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}

	private static String generateDataLoadInfoString(ComorbidityMiner currentExecutor, Pair<ComorbidityMinerCache, Map<Integer, Set<Integer>>> comoCacheAndPairs, String execID) {

		// Add info to dataLoadResults - START
		// - Get for each disease in how many mapping it participates
		StringBuffer outputStr = new StringBuffer("");

		int diseasePairsToAnalyzeCount = 0;
		Set<Integer> diseasesIncludedInOneComorPairToStudy = new HashSet<Integer>();
		Map<Integer, Integer> diseaseIDcountMap = new HashMap<Integer, Integer>();

		for(Entry<Integer, Set<Integer>> disPair : comoCacheAndPairs.getValue().entrySet()) {
			if(disPair != null && disPair.getKey() != null && disPair.getValue() != null && disPair.getValue().size() > 0) {
				diseasePairsToAnalyzeCount += disPair.getValue().size();

				diseasesIncludedInOneComorPairToStudy.add(disPair.getKey());
				diseasesIncludedInOneComorPairToStudy.addAll(disPair.getValue());

				if(!diseaseIDcountMap.containsKey(disPair.getKey())) {
					diseaseIDcountMap.put(disPair.getKey(), disPair.getValue().size());
				}

			}
		}
		outputStr.append(execID + " ***** Number of diseases pairs to study " + diseasePairsToAnalyzeCount + ".\n");

		outputStr.append(execID + " ***** Number of diseases of the dataset that are included in at least one disease pair to study " + diseasesIncludedInOneComorPairToStudy.size() + " (over " + currentExecutor.getComorDatasetObj().getDiagnosisCodeStringIdMap().size() + " diseases in input data).\n");

		outputStr.append(execID + " ***** \n");
		outputStr.append(execID + " ***** Top-5 and botom-5 diseases paired with more / less diseases to study comorbidities:\n");
		Map<Integer, Integer> diseaseIDcountMapSorted = GenericUtils.sortByValue(diseaseIDcountMap, true);
		int elemCount = 0;
		for(Entry<Integer, Integer> diseaseIDcountMapSortedEntry : diseaseIDcountMapSorted.entrySet()) {
			elemCount++;

			if((elemCount < 6 || elemCount > (diseaseIDcountMapSorted.size() - 6)) && diseaseIDcountMapSortedEntry != null && diseaseIDcountMapSortedEntry.getKey() != null && diseaseIDcountMapSortedEntry.getValue() != null) {
				String diseaseStr = (currentExecutor.getComorDatasetObj().getDiagnosisIdCodeStringMap().containsKey(diseaseIDcountMapSortedEntry.getKey()) && currentExecutor.getComorDatasetObj().getDiagnosisIdCodeStringMap().get(diseaseIDcountMapSortedEntry.getKey()) != null) ? currentExecutor.getComorDatasetObj().getDiagnosisIdCodeStringMap().get(diseaseIDcountMapSortedEntry.getKey()) : "NO_CODE";
				outputStr.append(execID + " *****        > Disease " + elemCount + " over " + diseaseIDcountMapSorted.size() + " participating in more pairs > DISEASE ID " + diseaseStr + " "
						+ "(" + diseaseIDcountMapSortedEntry.getKey() + ") participates in " + diseaseIDcountMapSortedEntry.getValue() + " disease pairs to study for comorbidity.\n");		
			}
		}
		// Get for each disease in how many mapping it participates - END

		outputStr.append(execID + " ***** Comorbidity dataset created, including  " + (currentExecutor.getComorDatasetObj().getDiagnosisCodeStringIdMap().size() - currentExecutor.getComorDatasetObj().getDiagnosisCodeStringGroupMap().size()) + " diseases.\n");
		outputStr.append(execID + " **************************************************\n");
		outputStr.append(execID + " \n");

		return outputStr.toString();
	}


	public void destroy() {
		// do nothing.
	}
}