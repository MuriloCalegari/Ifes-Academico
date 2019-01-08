package calegari.murilo.agendaescolar.subjecthelper;

public class Subject {

    private String name;
    private String professor;
    private String abbreviation;
    private Float maximumGrade;
    private Float obtainedGrade;

    public Subject(String name, String professor, String abbreviation) {
        this.name = name;
        this.professor = professor;
        this.abbreviation = abbreviation;
    }

    public Subject(String name, String professor, Float maximumGrade, Float obtainedGrade) {
        this.name = name;
        this.professor = professor;
        this.maximumGrade = maximumGrade;
        this.obtainedGrade = obtainedGrade;
    }

    public String getName() {
        return name;
    }

    public String getProfessor() {
        return professor;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Float getMaximumGrade() {
        return maximumGrade;
    }

    public void setMaximumGrade(Float maximumGrade) {
        this.maximumGrade = maximumGrade;
    }

    public Float getObtainedGrade() {
        return obtainedGrade;
    }

    public void setObtainedGrade(Float obtainedGrade) {
        this.obtainedGrade = obtainedGrade;
    }
}
