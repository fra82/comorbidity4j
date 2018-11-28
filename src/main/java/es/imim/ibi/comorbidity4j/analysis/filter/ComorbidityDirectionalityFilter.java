package es.imim.ibi.comorbidity4j.analysis.filter;


/**
 * Holder of parameters to configure the directionality analysis in comorbidity study
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityDirectionalityFilter {

	// Filters
	private Long minNumDays = null;

	// Constructor
	public ComorbidityDirectionalityFilter(Long minNumDays) {
		super();
		this.minNumDays = minNumDays;
	}
	
	
	// Getters
	public Long getMinNumDays() {
		return (minNumDays != null) ? new Long(minNumDays) : null;
	}
	
	public String toString(boolean isHTML) {
		return ((!isHTML) ? "ComorbidityDirectionalityFilter " : "") + "[minNumDays=" + ((minNumDays != null) ? minNumDays : "null") + "]";
	}
	
}
