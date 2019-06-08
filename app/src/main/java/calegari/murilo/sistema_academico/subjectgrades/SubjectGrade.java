package calegari.murilo.sistema_academico.subjectgrades;

import calegari.murilo.qacadscrapper.utils.Grade;

public class SubjectGrade extends Grade {
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

	public SubjectGrade(Grade grade) {
		this.subjectId = grade.getAcadSubjectId();
		this.setObtainedGrade(grade.getObtainedGrade() * grade.getWeight());
		this.setMaximumGrade(grade.getMaximumGrade() * grade.getWeight());
		this.setGradeDescription(grade.getGradeDescription());
		this.setDate(grade.getDate());
	}

	public SubjectGrade() {}

	public SubjectGrade(Integer gradeId, String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.gradeId = gradeId;
    	super.setGradeDescription(gradeDescription);
		super.setObtainedGrade(obtainedGrade);
		super.setMaximumGrade(maximumGrade);
		this.isExtraGrade = isExtraGrade;
	}

	public SubjectGrade(String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.setGradeDescription(gradeDescription);
		this.setObtainedGrade(obtainedGrade);
		this.setMaximumGrade(maximumGrade);
		this.isExtraGrade = isExtraGrade;
	}

	public SubjectGrade(int subjectId, String gradeDescription, float obtainedGrade, float maximumGrade, boolean isExtraGrade, boolean isObtainedGradeNull) {
		this.subjectId = subjectId;
		this.setGradeDescription(gradeDescription);
		this.setObtainedGrade(obtainedGrade);
		this.setMaximumGrade(maximumGrade);
		this.isExtraGrade = isExtraGrade;
		this.setIsObtainedGradeNull(isObtainedGradeNull);
	}

	public SubjectGrade(float obtainedGrade, float maximumGrade, boolean isExtraGrade) {
		this.setObtainedGrade(obtainedGrade);
		this.setMaximumGrade(maximumGrade);
		this.isExtraGrade = isExtraGrade;
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

	/*

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getAndroidDate() {
		java.time.LocalDate defaultLocalDate = getDate();

		LocalDate.of(defaultLocalDate.getYear(), defaultLocalDate.getMonthValue(), defaultLocalDate.getDayOfMonth());
		return (LocalDate) getDate();
	}

	*/

}
