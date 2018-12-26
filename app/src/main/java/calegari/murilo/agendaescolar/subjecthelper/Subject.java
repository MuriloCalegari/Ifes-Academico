package calegari.murilo.agendaescolar.subjecthelper;

public class Subject {

    private String name;
    private String professor;
    private String abbreviation;

    public Subject(String name, String professor, String abbreviation) {
        this.name = name;
        this.professor = professor;
        this.abbreviation = abbreviation;
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
}
