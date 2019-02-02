package calegari.murilo.agendaescolar.calendar;

public class ClassTime {

	private int subjectId;
	private int timeId;
	private int dayOfTheWeek;
	private String startTime;
	private String endTime;

	public ClassTime(int subjectId, int dayOfTheWeek, String startTime, String endTime) {
		this.subjectId = subjectId;
		this.dayOfTheWeek = dayOfTheWeek;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public ClassTime(int subjectId, int timeId, int dayOfTheWeek, String startTime, String endTime) {
		this.subjectId = subjectId;
		this.dayOfTheWeek = dayOfTheWeek;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getTimeId() {
		return timeId;
	}

	public void setTimeId(int timeId) {
		this.timeId = timeId;
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

	public int getStartTimeHour() {
		return Integer.valueOf(getStartTime().split(":")[0]);
	}

	public int getStartTimeMinute() {
		return Integer.valueOf(getStartTime().split(":")[1]);
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public int getEndTimeHour() {
		return Integer.valueOf(getEndTime().split(":")[0]);
	}

	public int getEndTimeMinute() {
		return Integer.valueOf(getEndTime().split(":")[1]);
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
