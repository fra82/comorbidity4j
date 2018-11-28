package es.imim.ibi.comorbidity4j.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Visit data model
 * 
 * @author Francesco Ronzano
 *
 */
public class Visit {

	private String strId;
	private Integer intId;
	private String patientStringId;
	private Integer patientIntId;
	private Date visitDate;
	private Set<Integer> diagnosisCodeSet = new HashSet<Integer>();


	// Getter and setter
	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public Integer getIntId() {
		return intId;
	}

	public void setIntId(Integer intId) {
		this.intId = intId;
	}

	public String getPatientStringId() {
		return patientStringId;
	}

	public void setPatientStringId(String patientStringId) {
		this.patientStringId = patientStringId;
	}

	public Integer getPatientIntId() {
		return patientIntId;
	}

	public void setPatientIntId(Integer patientIntId) {
		this.patientIntId = patientIntId;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Set<Integer> getDiagnosisCodeSet() {
		return diagnosisCodeSet;
	}

	public void setDiagnosisCodeSet(Set<Integer> diagnosisCodes) {
		this.diagnosisCodeSet = diagnosisCodes;
	}

	@Override
	public String toString() {
		return "Visit [strId=" + ((strId != null) ? strId : "NULL") + 
				", intId=" + ((intId != null) ? intId : "NULL") + 
				", patientStringId=" + ((patientStringId != null) ? patientStringId : "NULL") + 
				", patientIntId=" + ((patientIntId != null) ? patientIntId : "NULL") + 
				", visitDate=" + ((visitDate != null) ? visitDate : "NULL") + 
				", diagnosisCodesSize=" + ((diagnosisCodeSet != null) ? diagnosisCodeSet.size() : "NULL_VISIT_LIST") + "]";
	}


	// Static methods
	public static long getDayBetweenVisits(Visit visitBefore, Visit visitAfter) {
		if(visitBefore != null && visitAfter != null && visitBefore.getVisitDate() != null && visitAfter.getVisitDate() != null) {
			LocalDate visitBeforeLocalDate = visitBefore.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate visitAfterLocalDate = visitAfter.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			return ChronoUnit.DAYS.between(visitBeforeLocalDate, visitAfterLocalDate);
			// long numberOfDays2 = Period.between(visitBeforeLocalDate, visitAfterLocalDate).getDays();
		}
		else {
			return -1l;
		}
	}


}
