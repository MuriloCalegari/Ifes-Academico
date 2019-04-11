package calegari.murilo.agendaescolar.subjectgrades;

public class SubjectGrade {

    private String gradeDescription;
    private float obtainedGrade;
    private float maximumGrade;
	private Integer gradeId;
	private int subjectId;

	/*
	By default grades are set as not extra,
	adding it or not to maximum grade field
	in the database is handled at SubjectDatabase Helper
	*/
	private boolean isExtraGrade = false;

	public void setIsExtraGrade(boolean isExtraGrade) {
		this.isExtraGrade = isExtraGrade;
	}

	public SubjectGrade() {}

	public SubjectGrade(Integer gradeId, String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.gradeId = gradeId;
    	this.gradeDescription = gradeDescription;
		this.obtainedGrade = obtainedGrade;
		this.maximumGrade = maximumGrade;
		this.isExtraGrade = isExtraGrade;
	}

	public SubjectGrade(String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
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

	public Integer getGradeId() {
		return gradeId;
	}

	public void setGradeId(Integer gradeId) {
		this.gradeId = gradeId;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
}
