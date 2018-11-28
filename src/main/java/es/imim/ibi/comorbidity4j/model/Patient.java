package es.imim.ibi.comorbidity4j.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Patient data model
 * 
 * @author Francesco Ronzano
 *
 */
public class Patient {

	private String strId;
	private Integer intId;
	private Date birthDate;
	private String sex;
	private String classification1;
	private Set<Visit> visitSet = new HashSet<Visit>();

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

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getClassification1() {
		return classification1;
	}

	public void setClassification1(String classification1) {
		this.classification1 = classification1;
	}

	public Set<Visit> getVisitSet() {
		return visitSet;
	}

	public void setVisitSet(Set<Visit> visitList) {
		this.visitSet = visitList;
	}


	@Override
	public String toString() {
		return "Patient [strId=" + ((strId != null) ? strId : "NULL") + 
				", intId=" + ((intId != null) ? intId : "NULL") + 
				", birthDate=" + ((birthDate != null) ? birthDate : "NULL") + 
				", sex=" + ((sex != null) ? sex : "NULL") + 
				", classification1=" + ((classification1 != null) ? classification1 : "NULL") + 
				", visitListSize=" + ((visitSet != null) ? visitSet.size() : "NULL_VISIT_LIST") + "]";
	}

	// Static methods
	public long getPatientAge(PatientAgeENUM ageComputationMethod) {

		if(ageComputationMethod == null) {
			ageComputationMethod = PatientAgeENUM.FIRST_DIAGNOSTIC;
		}

		if(this.birthDate != null) {

			Date fistAdmission = null;
			Date lastAdmission = null;
			Date fistDiagnostic = null;
			Date lastDiagnostic = null;

			if(this.getVisitSet() != null && this.getVisitSet().size() > 0) {
				for(Visit patientVisit : this.getVisitSet()) {
					if(patientVisit != null && patientVisit.getVisitDate() != null) {
						if(fistAdmission == null || fistAdmission.after(patientVisit.getVisitDate())) {
							fistAdmission = patientVisit.getVisitDate();
						}
						if(lastAdmission == null || lastAdmission.before(patientVisit.getVisitDate())) {
							lastAdmission = patientVisit.getVisitDate();
						}
					}

					if(patientVisit.getDiagnosisCodeSet() != null && patientVisit.getDiagnosisCodeSet().size() > 0) {
						if(fistDiagnostic == null || fistDiagnostic.after(patientVisit.getVisitDate())) {
							fistDiagnostic = patientVisit.getVisitDate();
						}
						if(lastDiagnostic == null || lastDiagnostic.before(patientVisit.getVisitDate())) {
							lastDiagnostic = patientVisit.getVisitDate();
						}
					}
				}
			}


			Date computeAgeUpToDate = null;
			switch(ageComputationMethod) {
			case FIRST_ADMISSION:
				computeAgeUpToDate = fistAdmission;
				break;
			case FIRST_DIAGNOSTIC:
				computeAgeUpToDate = fistDiagnostic;
				break;
			case LAST_ADMISSION:
				computeAgeUpToDate = lastAdmission;
				break;
			case LAST_DIAGNOSTIC:
				computeAgeUpToDate = lastDiagnostic;
				break;
			case EXECUTION_TIME:
				computeAgeUpToDate = new Date();
				break;
			}

			long maxYearsAtVisit = -1l;
			// Get patient age 
			if(computeAgeUpToDate != null) {
				LocalDate birthday = this.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate currentVisitDate = computeAgeUpToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				long yearsAtVisit = ChronoUnit.YEARS.between(birthday, currentVisitDate);
				if(yearsAtVisit > maxYearsAtVisit) {
					maxYearsAtVisit = yearsAtVisit;
				}
			}
			
			/* USE YEARS TO CURRENT DATE AS AGE
			if(maxYearsAtVisit == -1l) {
				LocalDate birthday = this.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			    LocalDate currentDate = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			    maxYearsAtVisit = ChronoUnit.YEARS.between(birthday, currentDate);
			}
			 */			

			return maxYearsAtVisit;
			// or long numberOfYears2 = Period.between(startDate, endDate).getYears();
		}
		else {
			return -1l;
		}

	}

}
