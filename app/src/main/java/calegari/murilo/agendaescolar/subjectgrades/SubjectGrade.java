package calegari.murilo.agendaescolar.subjectgrades;

import calegari.murilo.agendaescolar.subjects.Subject;

public class SubjectGrade {

    private String subjectAbbreviation;
    private String gradeDescription;
    private float obtainedGrade;
    private float maximumGrade;

	/*
	By default grades are set as not extra,
	adding it or not to maximum grade field
	in the database is handled at SubjectDatabase Helper
	*/
	private boolean isExtraGrade = false;

    public SubjectGrade(String subjectAbbreviation, String gradeDescription, float obtainedGrade, float maximumGrade) {
        this.subjectAbbreviation = subjectAbbreviation;
        this.gradeDescription = gradeDescription;
        this.obtainedGrade = obtainedGrade;
        this.maximumGrade = maximumGrade;
    }

	public SubjectGrade(String gradeDescription, float obtainedGrade, float maximumGrade) {
		this.gradeDescription = gradeDescription;
		this.obtainedGrade = obtainedGrade;
		this.maximumGrade = maximumGrade;
	}

	public SubjectGrade(float obtainedGrade, float maximumGrade) {
        this.obtainedGrade = obtainedGrade;
        this.maximumGrade = maximumGrade;
    }

	public SubjectGrade(String subjectAbbreviation, String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.subjectAbbreviation = subjectAbbreviation;
		this.gradeDescription = gradeDescription;
		this.obtainedGrade = obtainedGrade;
		this.maximumGrade = maximumGrade;
		this.isExtraGrade = isExtraGrade;
	}

	public SubjectGrade(float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.obtainedGrade = obtainedGrade;
		this.maximumGrade = maximumGrade;
		this.isExtraGrade = isExtraGrade;
	}

    public String getSubjectAbbreviation() {
        return subjectAbbreviation;
    }

    public void setSubjectAbbreviation(String subjectAbbreviation) {
        this.subjectAbbreviation = subjectAbbreviation;
    }

    public String getGradeDescription() {
        return gradeDescription;
    }

    public void setGradeDescription(String gradeDescription) {
        this.gradeDescription = gradeDescription;
    }

    public float getObtainedGrade() {
        return obtainedGrade;
    }

    public void setObtainedGrade(float obtainedGrade) {
        this.obtainedGrade = obtainedGrade;
    }

    public float getMaximumGrade() {
        return maximumGrade;
    }

    public void setMaximumGrade(float maximumGrade) {
        this.maximumGrade = maximumGrade;
    }

	public boolean isExtraGrade() {
		return isExtraGrade;
	}

	public void setExtraGrade(boolean extraGrade) {
		isExtraGrade = extraGrade;
	}
}
