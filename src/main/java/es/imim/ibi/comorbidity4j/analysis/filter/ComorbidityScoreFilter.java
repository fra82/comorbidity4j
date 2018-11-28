package es.imim.ibi.comorbidity4j.analysis.filter;

import es.imim.ibi.comorbidity4j.model.global.ComorbidityPairResult;

/**
 * Holder of parameters to filter score of the results of comorbidity analysis
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityScoreFilter {

	public enum GreatLower {GREATER_OR_EQUAL_THEN, LOWER_OR_EQUAL_THEN};

	// Filters
	private Double relativeRiskIndexThresholdFS = null;
	private GreatLower relativeRiskIndexGLFS = null;

	private Double fisherTestThresholdFS = null;
	private GreatLower fisherTestGLFS = null;

	private Double fisherTestAsjustedThresholdFS = null;
	private GreatLower fisherTestAsjustedGLFS = null;

	private Double oddsRatioIndexThresholdFS = null;
	private GreatLower oddsRatioIndexGLFS = null;

	private Double phiIndexThresholdFS = null;
	private GreatLower phiIndexGLFS = null;

	private Double scoreThresholdFS = null;
	private GreatLower scoreGLFS = null;

	private Integer minNumPatientsThresholfFS = null;
	private GreatLower minNumPatientsGLFS = null;


	// Constructors
	public ComorbidityScoreFilter() {
		super();
	}

	public ComorbidityScoreFilter(Double relativeRiskIndexThresholdFS, GreatLower relativeRiskIndexGLFS,
			Double fisherTestThresholdFS, GreatLower fisherTestGLFS,
			Double fisherTestAsjustedThresholdFS, GreatLower fisherTestAsjustedGLFS,
			Double oddsRatioIndexThresholdFS, GreatLower oddsRatioIndexGLFS,
			Double phiIndexThresholdFS, GreatLower phiIndexGLFS,
			Double scoreThresholdFS, GreatLower scoreGLFS,
			Integer minNumPatientsThresholfFS, GreatLower minNumPatientsGLFS) {
		super();
		this.relativeRiskIndexThresholdFS = relativeRiskIndexThresholdFS;
		this.relativeRiskIndexGLFS = relativeRiskIndexGLFS;

		this.fisherTestThresholdFS = fisherTestThresholdFS;
		this.fisherTestGLFS = fisherTestGLFS;

		this.fisherTestAsjustedThresholdFS = fisherTestAsjustedThresholdFS;
		this.fisherTestAsjustedGLFS = fisherTestAsjustedGLFS;

		this.oddsRatioIndexThresholdFS = oddsRatioIndexThresholdFS;
		this.oddsRatioIndexGLFS = oddsRatioIndexGLFS;

		this.phiIndexThresholdFS = phiIndexThresholdFS;
		this.phiIndexGLFS = phiIndexGLFS;

		this.scoreThresholdFS = scoreThresholdFS;
		this.scoreGLFS = scoreGLFS;

		this.minNumPatientsThresholfFS = minNumPatientsThresholfFS;
		this.minNumPatientsGLFS = minNumPatientsGLFS;
	}


	// Getters
	public Double getRelativeRiskIndexThresholdFS() {
		return relativeRiskIndexThresholdFS;
	}

	public void setRelativeRiskIndexThresholdFS(Double relativeRiskIndexThresholdFS) {
		this.relativeRiskIndexThresholdFS = relativeRiskIndexThresholdFS;
	}

	public Double getFisherTestThresholdFS() {
		return fisherTestThresholdFS;
	}

	public void setFisherTestThresholdFS(Double fisherTestThresholdFS) {
		this.fisherTestThresholdFS = fisherTestThresholdFS;
	}

	public Double getFisherTestAsjustedThresholdFS() {
		return fisherTestAsjustedThresholdFS;
	}

	public void setFisherTestAsjustedThresholdFS(Double fisherTestAsjustedThresholdFS) {
		this.fisherTestAsjustedThresholdFS = fisherTestAsjustedThresholdFS;
	}

	public Double getOddsRatioIndexThresholdFS() {
		return oddsRatioIndexThresholdFS;
	}

	public void setOddsRatioIndexThresholdFS(Double oddsRatioIndexThresholdFS) {
		this.oddsRatioIndexThresholdFS = oddsRatioIndexThresholdFS;
	}

	public Double getPhiIndexThresholdFS() {
		return phiIndexThresholdFS;
	}

	public void setPhiIndexThresholdFS(Double phiIndexThresholdFS) {
		this.phiIndexThresholdFS = phiIndexThresholdFS;
	}

	public Double getScoreThresholdFS() {
		return scoreThresholdFS;
	}

	public void setScoreThresholdFS(Double scoreThresholdFS) {
		this.scoreThresholdFS = scoreThresholdFS;
	}

	public Integer getMinNumPatientsThresholfFS() {
		return minNumPatientsThresholfFS;
	}

	public void setMinNumPatientsThresholfFS(Integer minNumPatientsThresholfFS) {
		this.minNumPatientsThresholfFS = minNumPatientsThresholfFS;
	}

	public GreatLower getRelativeRiskIndexGLFS() {
		return relativeRiskIndexGLFS;
	}

	public void setRelativeRiskIndexGLFS(GreatLower relativeRiskIndexGLFS) {
		this.relativeRiskIndexGLFS = relativeRiskIndexGLFS;
	}

	public GreatLower getFisherTestGLFS() {
		return fisherTestGLFS;
	}

	public void setFisherTestGLFS(GreatLower fisherTestGLFS) {
		this.fisherTestGLFS = fisherTestGLFS;
	}

	public GreatLower getFisherTestAsjustedGLFS() {
		return fisherTestAsjustedGLFS;
	}

	public void setFisherTestAsjustedGLFS(GreatLower fisherTestAsjustedGLFS) {
		this.fisherTestAsjustedGLFS = fisherTestAsjustedGLFS;
	}

	public GreatLower getOddsRatioIndexGLFS() {
		return oddsRatioIndexGLFS;
	}

	public void setOddsRatioIndexGLFS(GreatLower oddsRatioIndexGLFS) {
		this.oddsRatioIndexGLFS = oddsRatioIndexGLFS;
	}

	public GreatLower getPhiIndexGLFS() {
		return phiIndexGLFS;
	}

	public void setPhiIndexGLFS(GreatLower phiIndexGLFS) {
		this.phiIndexGLFS = phiIndexGLFS;
	}

	public GreatLower getScoreGLFS() {
		return scoreGLFS;
	}

	public void setScoreGLFS(GreatLower scoreGLFS) {
		this.scoreGLFS = scoreGLFS;
	}

	public GreatLower getMinNumPatientsGLFS() {
		return minNumPatientsGLFS;
	}

	public void setMinNumPatientsGLFS(GreatLower minNumPatientsGLFS) {
		this.minNumPatientsGLFS = minNumPatientsGLFS;
	}
	
	public String toString(boolean isHTML) {
		if(isHTML) {
			return ((relativeRiskIndexThresholdFS != null) ? "relativeRiskIndexThreshold = " + ((relativeRiskIndexGLFS != null) ? relativeRiskIndexGLFS + " " : "null") + relativeRiskIndexThresholdFS + "<br/>" : "") + 
			((fisherTestThresholdFS != null) ? "fisherTestThreshold = " + ((fisherTestGLFS != null) ? fisherTestGLFS : "null") + fisherTestThresholdFS + " " + "<br/>" : "") + 
			((fisherTestAsjustedThresholdFS != null) ? "fisherTestAsjustedThreshold = " + ((fisherTestAsjustedGLFS != null) ? fisherTestAsjustedGLFS + " " : "null") + fisherTestAsjustedThresholdFS + "<br/>" : "") + 
			((oddsRatioIndexThresholdFS != null) ? "oddsRatioIndexThreshold = " + ((oddsRatioIndexGLFS != null) ? oddsRatioIndexGLFS + " " : "null") + oddsRatioIndexThresholdFS + "<br/>" : "") + 
			((phiIndexThresholdFS != null) ? "phiIndexThreshold = " + ((phiIndexGLFS != null) ? phiIndexGLFS + " " : "null") + phiIndexThresholdFS + "<br/>" : "") + 
			((scoreThresholdFS != null) ? "scoreThreshold = " + ((scoreGLFS != null) ? scoreGLFS + " " : "null") + scoreThresholdFS + "<br/>" : "") + 
			((minNumPatientsThresholfFS != null) ? "minNumPatientsThresholf = " + ((minNumPatientsGLFS != null) ? minNumPatientsGLFS + " " : "null") + minNumPatientsThresholfFS + "<br/>" : "");
		}
		
		return "ComorbidityScoreFilter [relativeRiskIndexThresholdFS=" + ((relativeRiskIndexThresholdFS != null) ? relativeRiskIndexThresholdFS : "null") + 
				"relativeRiskIndexGLFS=" + ((relativeRiskIndexGLFS != null) ? relativeRiskIndexGLFS : "null") + 
				", fisherTestThresholdFS=" + ((fisherTestThresholdFS != null) ? fisherTestThresholdFS : "null") + 
				", fisherTestGLFS=" + ((fisherTestGLFS != null) ? fisherTestGLFS : "null") + 
				", fisherTestAsjustedThresholdFS=" + ((fisherTestAsjustedThresholdFS != null) ? fisherTestAsjustedThresholdFS : "null") + 
				", fisherTestAsjustedGLFS=" + ((fisherTestAsjustedGLFS != null) ? fisherTestAsjustedGLFS : "null") + 
				", oddsRatioIndexThresholdFS=" + ((oddsRatioIndexThresholdFS != null) ? oddsRatioIndexThresholdFS : "null") + 
				", oddsRatioIndexGLFS=" + ((oddsRatioIndexGLFS != null) ? oddsRatioIndexGLFS : "null") + 
				", phiIndexThresholdFS=" + ((phiIndexThresholdFS != null) ? phiIndexThresholdFS : "null") + 
				", phiIndexGLFS=" + ((phiIndexGLFS != null) ? phiIndexGLFS : "null") + 
				", scoreThresholdFS=" + ((scoreThresholdFS != null) ? scoreThresholdFS : "null") +
				", scoreGLFS=" + ((scoreGLFS != null) ? scoreGLFS : "null") +
				", minNumPatientsThresholfFS=" + ((minNumPatientsThresholfFS != null) ? minNumPatientsThresholfFS : "null") + 
				", minNumPatientsGLFS=" + ((minNumPatientsGLFS != null) ? minNumPatientsGLFS : "null") + "]";
	}


	public boolean checkScoreFilters(ComorbidityPairResult pairResultToFilter, boolean checkAdjustedPval) {

		if(pairResultToFilter != null) {
			if(checkAdjustedPval) {
				if(fisherTestAsjustedThresholdFS != null && fisherTestAsjustedGLFS != null &&
						(pairResultToFilter.getFisherTestAdjusted() == null || 
						((fisherTestAsjustedGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getFisherTestAdjusted() < fisherTestAsjustedThresholdFS) ||
								(fisherTestAsjustedGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getFisherTestAdjusted() > fisherTestAsjustedThresholdFS)
								)) ){
					return false;
				}
			}
			else {
				if(relativeRiskIndexThresholdFS != null && relativeRiskIndexGLFS != null &&
						(pairResultToFilter.getRelativeRiskIndex() == null || 
						((relativeRiskIndexGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getRelativeRiskIndex() < relativeRiskIndexThresholdFS) ||
								(relativeRiskIndexGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getRelativeRiskIndex() > relativeRiskIndexThresholdFS)
								)) ){
					return false;
				}

				if(fisherTestThresholdFS != null && fisherTestGLFS != null &&
						(pairResultToFilter.getFisherTest() == null || 
						((fisherTestGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getFisherTest() < fisherTestThresholdFS) ||
								(fisherTestGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getFisherTest() > fisherTestThresholdFS)
								)) ){
					return false;
				}

				if(oddsRatioIndexThresholdFS != null && oddsRatioIndexGLFS != null &&
						(pairResultToFilter.getOddsRatioIndex() == null ||
						((oddsRatioIndexGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getOddsRatioIndex() < oddsRatioIndexThresholdFS) ||
								(oddsRatioIndexGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getOddsRatioIndex() > oddsRatioIndexThresholdFS)
								)) ){
					return false;
				}

				if(phiIndexThresholdFS != null && phiIndexGLFS != null &&
						(pairResultToFilter.getPhiIndex() == null ||
						((phiIndexGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getPhiIndex() < phiIndexThresholdFS) ||
								(phiIndexGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getPhiIndex() > phiIndexThresholdFS)
								)) ){
					return false;
				}

				if(scoreThresholdFS != null && scoreGLFS != null &&
						(pairResultToFilter.getScore() == null ||
						((scoreGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getScore() < scoreThresholdFS) ||
								(scoreGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getScore() > scoreThresholdFS)
								)) ){
					return false;
				}

				if(minNumPatientsThresholfFS != null && minNumPatientsGLFS != null &&
						(pairResultToFilter.getPatWdisAB() == null ||
						((minNumPatientsGLFS.equals(GreatLower.GREATER_OR_EQUAL_THEN) && pairResultToFilter.getPatWdisAB() < minNumPatientsThresholfFS) ||
								(minNumPatientsGLFS.equals(GreatLower.LOWER_OR_EQUAL_THEN) && pairResultToFilter.getPatWdisAB() > minNumPatientsThresholfFS)
								)) ){
					return false;
				}
			}

			return true;
		}
		else {
			// ComorbidityPairResult object is null
			return false;
		}

	}


}
