package calegari.murilo.agendaescolar.subjects;

public class Subject {

    private int id;
    private String name;
    private String professor;
    private String abbreviation;
    private Float obtainedGrade = 0f;
    private Float maximumGrade = 0f;

    public Subject() {}

    public Subject(String name, String professor, String abbreviation) {
        this.name = name;
        this.professor = professor;
        this.abbreviation = abbreviation;
    }

    public Subject(String name, String abbreviation, Float obtainedGrade, Float maximumGrade) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.obtainedGrade = obtainedGrade;
        this.maximumGrade = maximumGrade;
    }

    public Subject(String name, Float obtainedGrade, Float maximumGrade) {
        this.name = name;
        this.obtainedGrade = obtainedGrade;
        this.maximumGrade = maximumGrade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
