package calegari.murilo.agendaescolar.databases;

public class ClassTime {

	private int subjectId;
	private int dayOfTheWeek;
	private String startTime;
	private String endTime;

	public ClassTime(int subjectId, int dayOfTheWeek, String startTime, String endTime) {
		this.subjectId = subjectId;
		this.dayOfTheWeek = dayOfTheWeek;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public int getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(int dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
