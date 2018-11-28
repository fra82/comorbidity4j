package es.imim.ibi.comorbidity4j.analysis.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import es.imim.ibi.comorbidity4j.model.Patient;
import es.imim.ibi.comorbidity4j.model.PatientAgeENUM;


/**
 * Holder of parameters to filter patients to include in comorbidity analysis
 * 
 * @author Francesco Ronzano
 *
 */
public class ComorbidityPatientFilter {

	// Filters
	private Long minAgeFP = null;
	private Long maxAgeFP= null;
	private String sexFP = null;
	private String[] facet_1FP = null;
	private PatientAgeENUM ageComputationMethod = null;
	
	// Constructor
	public ComorbidityPatientFilter(Long minAgeFP, Long maxAgeFP, String sexFP, String[] facet_1FP, PatientAgeENUM ageComputationMethod) {
		super();
		this.minAgeFP = minAgeFP;
		this.maxAgeFP = maxAgeFP;
		this.sexFP = sexFP;
		this.facet_1FP = facet_1FP;
		this.ageComputationMethod = ageComputationMethod;
	}
	
	
	// Getters
	public Long getMinAgeFP() {
		return (minAgeFP != null) ? new Long(minAgeFP) : null;
	}
	
	public Long getMaxAgeFP() {
		return (maxAgeFP != null) ? new Long(maxAgeFP) : null;
	}
	
	public String getSexFP() {
		return (sexFP != null) ? new String(sexFP) : null;
	}
	
	public String[] getFacet_1FP() {
		return (facet_1FP != null) ? facet_1FP : null;
	}

	public PatientAgeENUM getAgeComputationMethod() {
		return ageComputationMethod;
	}

	
	public String toString(boolean showSexFilter, boolean isHTML) {
		if(isHTML) {
			return ((minAgeFP != null) ? "minAge = " + minAgeFP + "<br/>" : "") + 
			((maxAgeFP != null) ? "maxAgeFP = " + maxAgeFP + "<br/>" : "") + 
			((facet_1FP != null) ? "facet_1FP = " + Arrays.toString(facet_1FP) + "<br/>" : "") + 
			((showSexFilter) ? ((sexFP != null) ? "sexFP = " + sexFP + "<br/>" : "") : "") +
			((ageComputationMethod != null) ? "ageComputationMethod = " + ageComputationMethod + "<br/>" : "");
		}
		
		return "ComorbidityPatientFilter [minAgeFP=" + ((minAgeFP != null) ? minAgeFP : "null") + 
				", maxAgeFP=" + ((maxAgeFP != null) ? maxAgeFP : "null") + 
				", facet_1FP=" + ((facet_1FP != null) ? Arrays.toString(facet_1FP) : "null") +
				((showSexFilter) ? ", sexFP=" + ((sexFP != null) ? sexFP : "null") : "") +
				", ageComputationMethod=" + ((ageComputationMethod != null) ? ageComputationMethod : "null") + "]";
	}
	
	@Override
	public String toString() {
		return toString(true, false);
	}


	public boolean checkPatientFilters(Patient patientToFilter) {
		
		if(patientToFilter != null) {
			long patientAge = patientToFilter.getPatientAge(ageComputationMethod);
			if(minAgeFP != null && patientAge != -1l && patientAge < minAgeFP) {
				return false;
			}
			
			if(maxAgeFP != null && patientAge != -1l && patientAge > maxAgeFP) {
				return false;
			}
			
			if(sexFP != null) {
				Set<String> patientSexSet = new HashSet<String>();
				if(sexFP.contains(",")) {
					String[] splitSex = sexFP.split(",");
					if(splitSex != null && splitSex.length > 0) {
						for(String splitSexElem : splitSex) {
							if(!Strings.isNullOrEmpty(splitSexElem)) {
								patientSexSet.add(splitSexElem.trim().toLowerCase());
							}
						}
					}
				}
				else {
					patientSexSet.add(sexFP.trim().toLowerCase());
				}
				
				if(patientSexSet == null || !patientSexSet.contains(patientToFilter.getSex().trim().toLowerCase())) {
					return false;
				}
			}
			
			if(facet_1FP != null) {
				Set<String> patientFacet1Set = new HashSet<String>(Arrays.asList(facet_1FP));
				if(patientFacet1Set == null || !patientFacet1Set.stream().map(a -> a.toLowerCase().trim()).collect(Collectors.toList()).contains(patientToFilter.getClassification1().toLowerCase().trim())) {
					return false;
				}
			}
			
			return true;
		}
		else {
			// Patient object is null
			return false;
		}
		
	}
	
	
}
