package es.imim.ibi.comorbidity4j.utest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.Strings;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import com.opencsv.CSVReader;

import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;
import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;
import es.imim.ibi.comorbidity4j.server.reservlet.ComputeComorbidityServlet;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;
import es.imim.ibi.comorbidity4j.server.spring.contr.DiagnosisGroupingAndPairing;
import es.imim.ibi.comorbidity4j.util.stat.AdjMethodENUM;

public class ComorbidityValuesTest {


	@Test
	public void checkNumberPatients_noDirectionality() {

		// 1) Load dataset
		UserInputContainer synthea1kDataset = DiagnosisGroupingAndPairing.getSynthea1kArtificialUserInputContainer(true);

		// 2) set parameters
		synthea1kDataset.setPatientAgeComputation_p(PatientAgeENUM.LAST_DIAGNOSTIC);
		synthea1kDataset.setPvalAdjApproach_p(AdjMethodENUM.BONFERRONI);

		synthea1kDataset.setGenderEnabled(true);
		synthea1kDataset.setFemaleIdentifier_p("FEMALE");
		synthea1kDataset.setMaleIdentifier_p("MALE");

		synthea1kDataset.setRelativeRiskConfindeceInterval_p(0.95d);
		synthea1kDataset.setOddsRatioConfindeceInterval_p(0.95d);

		// 3) Execute comorbidity analysis
		PrintStream out = System.out;
		PrintStream err = System.out;
		System.setOut(new PrintStream(new OutputStream() {
		    @Override public void write(int b) throws IOException {}
		}));
		System.setErr(new PrintStream(new OutputStream() {
		    @Override public void write(int b) throws IOException {}
		}));
		ComputeComorbidityServlet servletClass = new ComputeComorbidityServlet();
		Triple<ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>> results = servletClass.executeAnalysis(null, synthea1kDataset, "TEST_1",
				null, null, null, null, null, null);
		System.setOut(out);
		System.setErr(err);
		
		// Get the results of the gender-independent and gender-dependent comorbidity analyses
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_ALL = results.getLeft();
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_FEMALE = results.getMiddle();
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_MALE = results.getRight();

		// 4) Load reference values
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("refValues/ALL_Synthea_1k.csv").getFile());
		System.out.println(file.getAbsolutePath());


		// 5) Assertion to check
		SoftAssertions softAssertions = new SoftAssertions();

		int counterOfDiseasePairsTested = 0;
		int counterOfDiseasePairsWithErrors = 0;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
			String[] row = null;
			while ((row = reader.readNext()) != null) {
				try {
					if(Strings.isEmpty(row[0])) {
						continue;
					}

					String diseaseAcode = row[1];
					String diseaseBcode = row[2];
					String disA = row[3];
					String disB = row[4];
					String AB = row[5];
					String AnotB = row[6];
					String BnotA = row[7];
					String notAnotB = row[8];

					for(ComorbidityPairResult cpr : resultPairAnalysisMap_ALL.getRight()) {
						if((cpr.getDisAcode().equals(diseaseAcode) && cpr.getDisBcode().equals(diseaseBcode)) ||
								(cpr.getDisAcode().equals(diseaseBcode) && cpr.getDisBcode().equals(diseaseAcode))) {

							counterOfDiseasePairsTested++;

							if(NumberUtils.isNumber(disA) && NumberUtils.isNumber(disB)) {
								softAssertions.assertThat((cpr.getPatWdisA().intValue() == Integer.valueOf(disA).intValue()) || (cpr.getPatWdisA().intValue() == Integer.valueOf(disB).intValue())).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisA: "  + cpr.getPatWdisA().intValue() + " >>> " + disA + " / " + disB).isTrue();
							}

							if(NumberUtils.isNumber(disA) && NumberUtils.isNumber(disB)) {
								softAssertions.assertThat((cpr.getPatWdisB().intValue() == Integer.valueOf(disA).intValue()) || (cpr.getPatWdisB().intValue() == Integer.valueOf(disB).intValue())).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisB: "  + cpr.getPatWdisB().intValue() + " >>> " + disA + " / " + disB).isTrue();
							}

							if(NumberUtils.isNumber(AB)) {
								softAssertions.assertThat(cpr.getPatWdisAB().intValue() == Integer.valueOf(AB).intValue()).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisAB: "  + cpr.getPatWdisB().intValue() + " >>> " + AB).isTrue();
							}

							if(NumberUtils.isNumber(AnotB) && NumberUtils.isNumber(BnotA)) {
								softAssertions.assertThat((cpr.getPatWdisAnotB().intValue() == Integer.valueOf(AnotB).intValue()) || (cpr.getPatWdisAnotB().intValue() == Integer.valueOf(BnotA).intValue())).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWdisAnotB: "  + cpr.getPatWdisB().intValue() + " >>> " + AnotB + " / " + BnotA).isTrue();
							}

							if(NumberUtils.isNumber(AnotB) && NumberUtils.isNumber(BnotA)) {
								softAssertions.assertThat((cpr.getPatWdisBnotA().intValue() == Integer.valueOf(AnotB).intValue()) || (cpr.getPatWdisBnotA().intValue() == Integer.valueOf(BnotA).intValue())).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWdisBnotA: "  + cpr.getPatWdisB().intValue() + " >>> " + AnotB + " / " + BnotA).isTrue();
							}

							if(NumberUtils.isNumber(notAnotB)) {
								softAssertions.assertThat(cpr.getPatWOdisAB().intValue() == Integer.valueOf(notAnotB).intValue()).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWOdisAB: "  + cpr.getPatWOdisAB().intValue() + " >>> " + notAnotB).isTrue();
							}

							if(softAssertions.errorsCollected().size() > 0) {
								System.out.println("> TEST errors while analyzing diagnosis pair: (" + diseaseAcode + " / " + diseaseBcode + ")");
								int countErrors = 0;
								counterOfDiseasePairsWithErrors++;
								for(Throwable saErr : softAssertions.errorsCollected()) {
									System.out.println("       > ERROR NUMBER: " + ++countErrors + " > " + saErr.getMessage());
								}
							}
							else {
								// System.out.println("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode);
								// System.out.println("   >>> CORRECT VALUES <<<");
							}

							softAssertions = new SoftAssertions();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("\n************************************************************");
		System.out.println("************ NUMBER OF PATIENTS COMPUTATION TEST ***********");
		if(counterOfDiseasePairsWithErrors == 0) {
			System.out.println(" > No errors have been spotted by testing the patient count result of " + counterOfDiseasePairsTested + " diagnosis pairs considered.");
		}
		else {
			System.out.println(" > ATTENTION: there are patient count result errors affecting " + counterOfDiseasePairsWithErrors + " diagnosis pairs over a total of " + counterOfDiseasePairsTested + " diagnosis pairs diagnosis.");
		}
		System.out.println("************************************************************\n");

	}


	@Test
	public void checkScores_noDirectionality() {

		// 1) Load dataset
		UserInputContainer synthea1kDataset = DiagnosisGroupingAndPairing.getSynthea1kArtificialUserInputContainer(true);

		// 2) set parameters
		synthea1kDataset.setPatientAgeComputation_p(PatientAgeENUM.LAST_DIAGNOSTIC);
		synthea1kDataset.setPvalAdjApproach_p(AdjMethodENUM.BONFERRONI);

		synthea1kDataset.setGenderEnabled(true);
		synthea1kDataset.setFemaleIdentifier_p("FEMALE");
		synthea1kDataset.setMaleIdentifier_p("MALE");

		synthea1kDataset.setRelativeRiskConfindeceInterval_p(0.95d);
		synthea1kDataset.setOddsRatioConfindeceInterval_p(0.95d);

		// 3) Execute comorbidity analysis
		PrintStream out = System.out;
		PrintStream err = System.out;
		System.setOut(new PrintStream(new OutputStream() {
		    @Override public void write(int b) throws IOException {}
		}));
		System.setErr(new PrintStream(new OutputStream() {
		    @Override public void write(int b) throws IOException {}
		}));
		ComputeComorbidityServlet servletClass = new ComputeComorbidityServlet();
		Triple<ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>, ImmutablePair<String, Collection<ComorbidityPairResult>>> results = servletClass.executeAnalysis(null, synthea1kDataset, "TEST_1",
				null, null, null, null, null, null);
		System.setOut(out);
		System.setErr(err);
		
		// Get the results of the gender-independent and gender-dependent comorbidity analyses
		ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_ALL = results.getLeft();
		// ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_FEMALE = results.getMiddle();
		// ImmutablePair<String, Collection<ComorbidityPairResult>> resultPairAnalysisMap_MALE = results.getRight();

		// 4) Load reference values
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("refValues/ALL_Synthea_1k.csv").getFile());
		System.out.println(file.getAbsolutePath());


		// 5) Assertion to check
		SoftAssertions softAssertions = new SoftAssertions();

		int counterOfDiseasePairsTested = 0;
		int counterOfDiseasePairsWithErrors = 0;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
			String[] row = null;
			while ((row = reader.readNext()) != null) {
				try {
					if(Strings.isEmpty(row[0])) {
						continue;
					}

					String diseaseAcode = row[1];
					String diseaseBcode = row[2];
					String disA = row[3];
					String disB = row[4];
					String AB = row[5];
					String AnotB = row[6];
					String BnotA = row[7];
					String notAnotB = row[8];
					String fisher = row[9];
					String oddsRatio = row[10];
					String CI = row[11];
					String relativeRisk = row[12];
					String phi = row[13];
					String expect = row[14];
					String score = row[15];

					DecimalFormat df = new DecimalFormat("#.000");
					df.setRoundingMode(RoundingMode.HALF_DOWN);

					for(ComorbidityPairResult cpr : resultPairAnalysisMap_ALL.getRight()) {
						if((cpr.getDisAcode().equals(diseaseAcode) && cpr.getDisBcode().equals(diseaseBcode)) ||
								(cpr.getDisAcode().equals(diseaseBcode) && cpr.getDisBcode().equals(diseaseAcode))) {

							softAssertions.assertThat((cpr.getPatWdisA().intValue() == Integer.valueOf(disA).intValue()) || (cpr.getPatWdisA().intValue() == Integer.valueOf(disB).intValue())).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisA: "  + cpr.getPatWdisA().intValue() + " >>> " + disA + " / " + disB).isTrue();

							softAssertions.assertThat((cpr.getPatWdisB().intValue() == Integer.valueOf(disA).intValue()) || (cpr.getPatWdisB().intValue() == Integer.valueOf(disB).intValue())).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisB: "  + cpr.getPatWdisB().intValue() + " >>> " + disA + " / " + disB).isTrue();

							softAssertions.assertThat(cpr.getPatWdisAB().intValue() == Integer.valueOf(AB).intValue()).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error PatWdisAB: "  + cpr.getPatWdisB().intValue() + " >>> " + AB).isTrue();

							softAssertions.assertThat((cpr.getPatWdisAnotB().intValue() == Integer.valueOf(AnotB).intValue()) || (cpr.getPatWdisAnotB().intValue() == Integer.valueOf(BnotA).intValue())).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWdisAnotB: "  + cpr.getPatWdisB().intValue() + " >>> " + AnotB + " / " + BnotA).isTrue();

							softAssertions.assertThat((cpr.getPatWdisBnotA().intValue() == Integer.valueOf(AnotB).intValue()) || (cpr.getPatWdisBnotA().intValue() == Integer.valueOf(BnotA).intValue())).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWdisBnotA: "  + cpr.getPatWdisB().intValue() + " >>> " + AnotB + " / " + BnotA).isTrue();

							softAssertions.assertThat(cpr.getPatWOdisAB().intValue() == Integer.valueOf(notAnotB).intValue()).
							overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + " > Error getPatWOdisAB: "  + cpr.getPatWOdisAB().intValue() + " >>> " + notAnotB).isTrue();

							// if(softAssertions.errorsCollected().size() == 0) {

							counterOfDiseasePairsTested++;

							// System.out.println("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode);

							if(NumberUtils.isNumber(fisher)) {
								double firstValue = Double.valueOf(df.format(cpr.getFisherTest()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(fisher)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getFisherTest: "  + df.format(cpr.getFisherTest()) + " (" + cpr.getFisherTest() + ") >>> " + df.format(Double.valueOf(fisher)) + " (" + fisher + ")").isTrue();
							}

							if(NumberUtils.isNumber(score)) {
								double firstValue = Double.valueOf(df.format(cpr.getScore()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(score)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getScore: "  + df.format(cpr.getScore()) + " (" + cpr.getScore() + ") >>> " + df.format(Double.valueOf(score)) + " (" + score + ")").isTrue();
							}

							if(NumberUtils.isNumber(oddsRatio)) {
								double firstValue = Double.valueOf(df.format(cpr.getOddsRatioIndex()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(oddsRatio)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getOddsRatioIndex: "  + df.format(cpr.getOddsRatioIndex()) + " (" + cpr.getOddsRatioIndex() + ") >>> " + df.format(Double.valueOf(oddsRatio)) + " (" + oddsRatio + ")").isTrue();
							}

							if(NumberUtils.isNumber(relativeRisk)) {
								double firstValue = Double.valueOf(df.format(cpr.getRelativeRiskIndex()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(relativeRisk)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getRelativeRiskIndex: "  + df.format(cpr.getRelativeRiskIndex()) + " (" + cpr.getRelativeRiskIndex() + ") >>> " + df.format(Double.valueOf(relativeRisk)) + " (" + relativeRisk + ")").isTrue();
							}

							if(NumberUtils.isNumber(phi)) {
								double firstValue = Double.valueOf(df.format(cpr.getPhiIndex()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(phi)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getPhiIndex: "  + df.format(cpr.getPhiIndex()) + " (" + cpr.getPhiIndex() + ") >>> " + df.format(Double.valueOf(phi)) + " (" + phi + ")").isTrue();
							}

							if(NumberUtils.isNumber(expect)) {
								double firstValue = Double.valueOf(df.format(cpr.getExpect()));
								double secondValue = Double.valueOf(df.format(Double.valueOf(expect)));
								double absDiff = Math.abs(firstValue - secondValue);
								
								softAssertions.assertThat( absDiff <= 0.0011d ).
								overridingErrorMessage("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode + 
										" > Error getExpect: "  + df.format(cpr.getExpect()) + " (" + cpr.getExpect() + ") >>> " + df.format(Double.valueOf(expect)) + " (" + expect + ")").isTrue();
							}


							if(softAssertions.errorsCollected().size() > 0) {
								System.out.println("> TEST errors while analyzing diagnosis pair: (" + diseaseAcode + " / " + diseaseBcode + ")");
								int countErrors = 0;
								counterOfDiseasePairsWithErrors++;
								for(Throwable saErr : softAssertions.errorsCollected()) {
									System.out.println("       > ERROR NUMBER: " + ++countErrors + " > " + saErr.getMessage());
								}
							}
							else {
								// System.out.println("Analyzing diseases: " + diseaseAcode + " / " + diseaseBcode);
								// System.out.println("   >>> CORRECT VALUES <<<");
							}

							/*
							}
							else {
								System.out.println("Skipped diseases: " + diseaseAcode + " / " + diseaseBcode);
							}
							 */

							softAssertions = new SoftAssertions();

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("\n************************************************************");
		System.out.println("************ COMORBIDITY SCORES COMPUTATION TEST *************");
		if(counterOfDiseasePairsWithErrors == 0) {
			System.out.println(" > No errors have been spotted by testing the comorbidity analysis result of " + counterOfDiseasePairsTested + " diagnosis pairs considered.");
		}
		else {
			System.out.println(" > ATTENTION: there are comorbidity analysis result errors affecting " + counterOfDiseasePairsWithErrors + " diagnosis pairs over a total of " + counterOfDiseasePairsTested + " diagnosis pairs diagnosis.");
		}
		System.out.println("************************************************************\n");
	}


}
