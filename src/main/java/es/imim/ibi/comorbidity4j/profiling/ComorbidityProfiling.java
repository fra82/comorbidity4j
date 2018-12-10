package es.imim.ibi.comorbidity4j.profiling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.io.CharSource;

import es.imim.ibi.comorbidity4j.loader.DataLoadContainer;
import es.imim.ibi.comorbidity4j.loader.DescriptionDataLoader;
import es.imim.ibi.comorbidity4j.loader.DiagnosisDataLoader;
import es.imim.ibi.comorbidity4j.loader.PatientDataLoader;
import es.imim.ibi.comorbidity4j.loader.VisitDataLoader;
import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.Visit;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.reservlet.ComputeComorbidityServlet;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.util.stat.AdjMethodENUM;

public class ComorbidityProfiling {


	public static UserInputContainer loadArtificialData(String fullPathOfDataFolder, boolean isGenderEnabled) {
		UserInputContainer retUserInputContainer = new UserInputContainer();

		retUserInputContainer.setGenderEnabled(isGenderEnabled);

		// Patient data
		try {
			InputStream is = new FileInputStream(new File(fullPathOfDataFolder + "patientData.txt"));
			retUserInputContainer.setPatientData_PD(IOUtils.toString(is, "UTF-8")); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		retUserInputContainer.setPatientDataFileSize_PD(-1l);
		retUserInputContainer.setPatientDataFileName_PD("patientData.txt");
		retUserInputContainer.setPatientIDcolumn_PD("patient_id");
		retUserInputContainer.setPatientBirthDateColumn_PD("patient_dateBirth");
		retUserInputContainer.setPatientGenderColumn_PD("patient_sex");
		retUserInputContainer.setColumnSeparatorChar_PD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_PD('N');
		retUserInputContainer.setHasFirstRowHeader_PD(true);
		retUserInputContainer.setDateFormat_PD("yyyy/MM/dd");
		retUserInputContainer.setOMOP_PD(false);

		PatientDataLoader pd_loader = new PatientDataLoader();
		pd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			DataLoadContainer<List<Patient>> loadedData = pd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getPatientData_PD()).openStream(), retUserInputContainer.isGenderEnabled());
			retUserInputContainer.setPatientData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Visit data
		try {
			InputStream is = new FileInputStream(new File(fullPathOfDataFolder + "admissionData.txt"));
			retUserInputContainer.setVisitData_VD(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setVisitDataFileSize_VD(-1l);
		retUserInputContainer.setVisitDataFileName_VD(fullPathOfDataFolder + "admissionData.txt");
		retUserInputContainer.setPatientIDcolumn_VD("patient_id");
		retUserInputContainer.setVisitIDcolumn_VD("admission_id");
		retUserInputContainer.setVisitStartDateColumn_VD("admissionStartDate");
		retUserInputContainer.setColumnSeparatorChar_VD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_VD('N');
		retUserInputContainer.setHasFirstRowHeader_VD(true);
		retUserInputContainer.setDateFormat_VD("yyyy/MM/dd");
		retUserInputContainer.setOMOP_VD(false);

		VisitDataLoader vd_loader = new VisitDataLoader();
		vd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			DataLoadContainer<List<Visit>> loadedData = vd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getVisitData_VD()).openStream());
			retUserInputContainer.setVisitData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Diagnosis data
		try {
			InputStream is = new FileInputStream(new File(fullPathOfDataFolder + "diagnosisData.txt"));
			retUserInputContainer.setDiagnosisData_DD(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setDiagnosisDataFileSize_DD(-1l);
		retUserInputContainer.setDiagnosisDataFileName_DD("diagnosisData.txt");
		retUserInputContainer.setPatientIDcolumn_DD("patient_id");
		retUserInputContainer.setVisitIDcolumn_DD("admission_id");
		retUserInputContainer.setDiagnosisCodeColumn_DD("diagnosis_code");
		retUserInputContainer.setColumnSeparatorChar_DD('\t');
		retUserInputContainer.setColumnTextDelimiterChar_DD('N');
		retUserInputContainer.setHasFirstRowHeader_DD(true);
		retUserInputContainer.setOMOP_DD(false);

		DiagnosisDataLoader dd_loader = new DiagnosisDataLoader();
		dd_loader.initializeParamsFromSessionObj(retUserInputContainer);
		try {
			Map<String, Integer> diagnosisCodeIdStringMap = new HashMap<String, Integer>();
			DataLoadContainer<List<Visit>> loadedData = dd_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getDiagnosisData_DD()).openStream(),
					retUserInputContainer.getVisitData_LOADED().data, diagnosisCodeIdStringMap);

			retUserInputContainer.setDiagnosisData_LOADED(loadedData);
			retUserInputContainer.setDiagnosisCodeIdStringMap(diagnosisCodeIdStringMap);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Diagnosis description data
		try {
			File allIDC = new File(fullPathOfDataFolder + "indexDiseaseCode_ALL.txt");
			File IDC = new File(fullPathOfDataFolder + "indexDiseaseCode.txt");
			InputStream is = null;
			if(allIDC != null && allIDC.exists() && allIDC.isFile()) {
				is = new FileInputStream(allIDC);
			}
			else {
				is = new FileInputStream(IDC);
			}

			retUserInputContainer.setDescrDiagnosisData_DDE(IOUtils.toString(is, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		retUserInputContainer.setDescrDiagnosisDataFileSize_DDE(-1l);
		retUserInputContainer.setDescrDiagnosisDataFileName_DDE("indexDiseaseCode.txt");
		retUserInputContainer.setDiagnosisCodeColumn_DDE("Code");
		retUserInputContainer.setDiagnosisDescriptionColumn_DDE("Description");
		retUserInputContainer.setColumnSeparatorChar_DDE('\t');
		retUserInputContainer.setColumnTextDelimiterChar_DDE('N');
		retUserInputContainer.setHasFirstRowHeader_DDE(true);

		DescriptionDataLoader dde_loader = new DescriptionDataLoader();
		dde_loader.initializeParamsFromSessionObj(retUserInputContainer);

		try {
			DataLoadContainer<Map<String, String>> loadedData = dde_loader.loadDataFromReader(CharSource.wrap(retUserInputContainer.getDescrDiagnosisData_DDE()).openStream());
			retUserInputContainer.setDescrDiagnosisData_LOADED(loadedData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Disase grouping default

		// Disease pairing default

		return retUserInputContainer;
	}

	public static void main(String[] args) {

		List<String> artificialDataPaths = new ArrayList<String>();
		artificialDataPaths.add("/home/ronzano/Desktop/ComorbidityJavaData/OTHER_SW/comoRbidity/DATASET_IMASIS2_D1/extdata/");
		artificialDataPaths.add("/home/ronzano/Desktop/ComorbidityJavaData/OTHER_SW/comoRbidity/DATASET_NHDS10_PU/extdata/");
		artificialDataPaths.add("/home/ronzano/Desktop/ComorbidityJavaData/OTHER_SW/comoRbidity/DATASET_SYNTHEA_1k/extdata/");
		artificialDataPaths.add("/home/ronzano/Desktop/ComorbidityJavaData/OTHER_SW/comoRbidity/DATASET_SYNTHEA_100k/extdata/");

		// No directionality
		for(String artDataPath : artificialDataPaths) {
			System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("Start processing data (NO DIR.): " + artDataPath);

			UserInputContainer artificialDataset = loadArtificialData(artDataPath, true);

			// Parameters
			artificialDataset.setPatientAgeComputation_p(PatientAgeENUM.LAST_DIAGNOSTIC);
			artificialDataset.setPvalAdjApproach_p(AdjMethodENUM.BONFERRONI);

			artificialDataset.setGenderEnabled(true);
			artificialDataset.setFemaleIdentifier_p("FEMALE");
			artificialDataset.setMaleIdentifier_p("MALE");

			artificialDataset.setRelativeRiskConfindeceInterval_p(0.95d);
			artificialDataset.setOddsRatioConfindeceInterval_p(0.95d);


			ComputeComorbidityServlet servletClass = new ComputeComorbidityServlet();
			Triple<ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>> results = servletClass.executeAnalysis(null, artificialDataset, "TEST_1",
					null, null, null, null, null, null);

			// Get the results of the gender-independent and gender-dependent comorbidity analyses
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_ALL = results.getLeft();
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_FEMALE = results.getMiddle();
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_MALE = results.getRight();

			System.out.println("End processing data (NO DIR.): " + artDataPath);

			System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		}


		// Directionality
		for(String artDataPath : artificialDataPaths) {
			System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("Start processing data (DIR.): " + artDataPath);

			UserInputContainer artificialDataset = loadArtificialData(artDataPath, true);

			// Parameters
			artificialDataset.setPatientAgeComputation_p(PatientAgeENUM.LAST_DIAGNOSTIC);
			artificialDataset.setPvalAdjApproach_p(AdjMethodENUM.BONFERRONI);

			artificialDataset.setGenderEnabled(true);
			artificialDataset.setFemaleIdentifier_p("FEMALE");
			artificialDataset.setMaleIdentifier_p("MALE");
			
			// ENABLE DIRECTIONALITY
			artificialDataset.setIsDirectional_p("enab");
			artificialDataset.setDirectMinDays_p(14);
			
			artificialDataset.setOddsRatioConfindeceInterval_p(0.95d);


			ComputeComorbidityServlet servletClass = new ComputeComorbidityServlet();
			Triple<ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>> results = servletClass.executeAnalysis(null, artificialDataset, "TEST_1",
					null, null, null, null, null, null);

			// Get the results of the gender-independent and gender-dependent comorbidity analyses
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_ALL = results.getLeft();
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_FEMALE = results.getMiddle();
			ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_MALE = results.getRight();

			System.out.println("End processing data (DIR.): " + artDataPath);

			System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		}


		ComputeComorbidityServlet.executor.shutdown();

	}

}
