package calegari.murilo.ifes_academico.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import calegari.murilo.ifes_academico.calendar.ClassTime;
import calegari.murilo.ifes_academico.subjectgrades.SubjectGrade;
import calegari.murilo.qacadscrapper.utils.Grade;
import calegari.murilo.qacadscrapper.utils.Subject;

@SuppressWarnings("WeakerAccess")
public class DatabaseHelper extends SQLiteOpenHelper {

	public static class GlobalEntry implements BaseColumns {
		public static final String DATABASE_NAME = "schooltoolsdatabase.db";
		public static final int DATABASE_VERSION = 1;
	}

	public static class SubjectsEntry implements BaseColumns {

		public static final String TABLE_NAME = "subjects";
		public static final String TRANSITIONAL_TABLE_NAME = TABLE_NAME + "_transitional";

		public static final String COLUMN_SUBJECT_NAME = "name";
		public static final String COLUMN_SUBJECT_ABBREVIATION = "abbreviation";
		public static final String COLUMN_SUBJECT_PROFESSOR = "professor";
		// This naming is very bad, sorry english speakers
		public static final String COLUMN_SUBJECT_OBTAINED_GRADE = "obtainedgrade";
		public static final String COLUMN_SUBJECT_MAXIMUM_GRADE = "maximumgrade";
		public static final String COLUMN_SUBJECT_ID = "ID";

		protected static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
				TABLE_NAME + " (" + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				COLUMN_SUBJECT_NAME + " TEXT," +
				COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
				COLUMN_SUBJECT_PROFESSOR + " TEXT," +
				COLUMN_SUBJECT_OBTAINED_GRADE + " REAL," +
				COLUMN_SUBJECT_MAXIMUM_GRADE + " REAL)";

		protected static final String SQL_CREATE_TRANSITIONAL_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
				TRANSITIONAL_TABLE_NAME + " (" + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				COLUMN_SUBJECT_NAME + " TEXT," +
				COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
				COLUMN_SUBJECT_PROFESSOR + " TEXT," +
				COLUMN_SUBJECT_OBTAINED_GRADE + " REAL," +
				COLUMN_SUBJECT_MAXIMUM_GRADE + " REAL)";

		protected static final String SQL_DELETE_ENTRIES =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	public static class ScheduleEntry implements BaseColumns {

		public static final String TABLE_NAME = "schedule";

		public static final String COLUMN_CLASS_TIME_ID = "classtimeid";
		public static final String COLUMN_CLASS_SUBJECT_ID = "subjectid";
		public static final String COLUMN_CLASS_DAY = "classday";
		public static final String COLUMN_CLASS_START_TIME = "startime";
		public static final String COLUMN_CLASS_END_TIME = "endtime";

		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
				TABLE_NAME + "( " +
				COLUMN_CLASS_TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				COLUMN_CLASS_SUBJECT_ID + " INTEGER," +
				COLUMN_CLASS_DAY + " INTEGER," +
				COLUMN_CLASS_START_TIME + " TEXT," +
				COLUMN_CLASS_END_TIME + " TEXT)";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	public static class GradesEntry implements BaseColumns {

		public static final String TABLE_NAME = "grades";
		public static final String TRANSITIONAL_TABLE_NAME = TABLE_NAME + "_transitional";

		public static final String COLUMN_GRADE_ID = "gradeid";
		public static final String COLUMN_GRADE_DESCRIPTION = "gradedescription";
		public static final String COLUMN_GRADE_OBTAINED = "obtainedgrade";
		public static final String COLUMN_GRADE_MAXIMUM = "maximumgrade";
		public static final String COLUMN_GRADE_IS_EXTRA_CREDIT = "isextracredit";
		public static final String COLUMN_GRADE_SUBJECT_ID = "subjectid";

		private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
				TABLE_NAME + "( " +
				COLUMN_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				COLUMN_GRADE_DESCRIPTION + " TEXT," +
				COLUMN_GRADE_OBTAINED + " REAL," +
				COLUMN_GRADE_MAXIMUM + " REAL," +
				COLUMN_GRADE_IS_EXTRA_CREDIT + " INTEGER," +
				COLUMN_GRADE_SUBJECT_ID + " INTEGER," +
				"FOREIGN KEY(" + COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + SubjectsEntry.TABLE_NAME + "(" + SubjectsEntry.COLUMN_SUBJECT_ID + "))";

		private static final String SQL_CREATE_TRANSITIONAL_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
				TRANSITIONAL_TABLE_NAME + "( " +
				COLUMN_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				COLUMN_GRADE_DESCRIPTION + " TEXT," +
				COLUMN_GRADE_OBTAINED + " REAL," +
				COLUMN_GRADE_MAXIMUM + " REAL," +
				COLUMN_GRADE_IS_EXTRA_CREDIT + " INTEGER," +
				COLUMN_GRADE_SUBJECT_ID + " INTEGER," +
				"FOREIGN KEY(" + COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + SubjectsEntry.TRANSITIONAL_TABLE_NAME + "(" + SubjectsEntry.COLUMN_SUBJECT_ID + "))";

		private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + GradesEntry.TABLE_NAME;
	}

	final String TAG = getClass().getSimpleName();

	public DatabaseHelper(@Nullable Context context) {
		super(context, GlobalEntry.DATABASE_NAME, null, GlobalEntry.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SubjectsEntry.SQL_CREATE_ENTRIES);
		db.execSQL(ScheduleEntry.SQL_CREATE_ENTRIES);
		db.execSQL(GradesEntry.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void recreateDatabase() {
		Log.d(TAG, "Recreating databases");
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL(GradesEntry.SQL_DELETE_ENTRIES);
		db.execSQL(ScheduleEntry.SQL_DELETE_ENTRIES);
		db.execSQL(SubjectsEntry.SQL_DELETE_ENTRIES);

		db.execSQL(SubjectsEntry.SQL_CREATE_ENTRIES);
		db.execSQL(ScheduleEntry.SQL_CREATE_ENTRIES);
		db.execSQL(GradesEntry.SQL_CREATE_ENTRIES);

		db.close();
	}

	// Related to classTime table

	public int insertClassTime(ClassTime classTime) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(ScheduleEntry.COLUMN_CLASS_SUBJECT_ID, classTime.getSubjectId());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_DAY, classTime.getDayOfTheWeek());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_START_TIME, classTime.getStartTime());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_END_TIME, classTime.getEndTime());

		long id = db.insert(
				ScheduleEntry.TABLE_NAME,
				null,
				contentValues
		);
		db.close();

		return (int) id;
	}

	public List<ClassTime> getSchedule() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ScheduleEntry.TABLE_NAME,
				new String[] {ScheduleEntry.COLUMN_CLASS_SUBJECT_ID, ScheduleEntry.COLUMN_CLASS_TIME_ID, ScheduleEntry.COLUMN_CLASS_DAY, ScheduleEntry.COLUMN_CLASS_START_TIME, ScheduleEntry.COLUMN_CLASS_END_TIME},
				null, null, null, null, null
		);

		int subjectIdIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_SUBJECT_ID);
		int timeIdIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_TIME_ID);
		int dayIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_DAY);
		int startTimeIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_START_TIME);
		int endTimeIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_END_TIME);

		List<ClassTime> classTimeList = new ArrayList<>();

		while(cursor.moveToNext()) {
			ClassTime classTime = new ClassTime(
					cursor.getInt(subjectIdIndex),
					cursor.getInt(timeIdIndex),
					cursor.getInt(dayIndex),
					cursor.getString(startTimeIndex),
					cursor.getString(endTimeIndex)
			);

			classTimeList.add(classTime);
		}

		cursor.close();
		db.close();
		return classTimeList;
	}

	public ClassTime getClassTime(int timeId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(
				ScheduleEntry.TABLE_NAME,
				new String[] {ScheduleEntry.COLUMN_CLASS_SUBJECT_ID, ScheduleEntry.COLUMN_CLASS_TIME_ID, ScheduleEntry.COLUMN_CLASS_DAY, ScheduleEntry.COLUMN_CLASS_START_TIME, ScheduleEntry.COLUMN_CLASS_END_TIME},
				ScheduleEntry.COLUMN_CLASS_TIME_ID + "=?",
				new String[] {String.valueOf(timeId)},
				null, null, null
		);

		cursor.moveToFirst();

		int subjectIdIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_SUBJECT_ID);
		int timeIdIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_TIME_ID);
		int dayIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_DAY);
		int startTimeIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_START_TIME);
		int endTimeIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_CLASS_END_TIME);

		ClassTime classTime = new ClassTime(
				cursor.getInt(subjectIdIndex),
				cursor.getInt(timeIdIndex),
				cursor.getInt(dayIndex),
				cursor.getString(startTimeIndex),
				cursor.getString(endTimeIndex)
		);

		cursor.close();
		db.close();

		return classTime;
	}

	public void deleteClassTime(int timeId) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(
				ScheduleEntry.TABLE_NAME,
				ScheduleEntry.COLUMN_CLASS_TIME_ID + "=?",
				new String[] {String.valueOf(timeId)}
		);

		db.close();
	}

	public void updateClassTime(ClassTime classTime) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(ScheduleEntry.COLUMN_CLASS_SUBJECT_ID, classTime.getSubjectId());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_DAY, classTime.getDayOfTheWeek());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_START_TIME, classTime.getStartTime());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_END_TIME, classTime.getEndTime());

		db.update(
				ScheduleEntry.TABLE_NAME,
				contentValues,
				ScheduleEntry.COLUMN_CLASS_TIME_ID + "=?",
				new String[] {String.valueOf(classTime.getTimeId())}
		);

		db.close();
	}

	public void deleteClasses(int subjectId) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(
				ScheduleEntry.TABLE_NAME,
				ScheduleEntry.COLUMN_CLASS_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)}
		);

		db.close();
	}

	// Related to subjects table

	public int insertSubject(Subject subject) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_NAME, subject.getName());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION, subject.getAbbreviation());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, subject.getObtainedGrade());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, subject.getMaximumGrade());

		long id = db.insert(SubjectsEntry.TABLE_NAME, null, contentValues);
		db.close();

		return (int) id;
	}

	public void removeSubject(int subjectId) {
		removeAllGrades(subjectId);
		deleteClasses(subjectId);

		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(
				SubjectsEntry.TABLE_NAME,
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)}
		);

		db.close();
	}

	public void updateSubject(int subjectId, Subject newSubject) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_NAME, newSubject.getName());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION, newSubject.getAbbreviation());
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR, newSubject.getProfessor());

		db.update(
				SubjectsEntry.TABLE_NAME,
				contentValues,
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)}
		);

		db.close();
	}

	public void addToTotalGrade(SubjectGrade subjectGrade) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(
				SubjectsEntry.TABLE_NAME,
				new String[] {SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE},
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectGrade.getSubjectId())},
				null, null, null
		);

		int subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		int subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		cursor.moveToFirst(); // Id is unique, so it's safe to moveToFirst()

		ContentValues contentValues = new ContentValues();

		float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) + subjectGrade.getObtainedGrade();
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

		if(!subjectGrade.isExtraGrade() && !subjectGrade.isObtainedGradeNull()) {
			float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) + subjectGrade.getMaximumGrade();
			contentValues.put(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
		}

		cursor.close();

		db.update(
				SubjectsEntry.TABLE_NAME,
				contentValues,
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectGrade.getSubjectId())}
		);
		db.close();
	}

	public void removeFromTotalGrade(SubjectGrade subjectGrade) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(
				SubjectsEntry.TABLE_NAME,
				new String[] {SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE},
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectGrade.getSubjectId())},
				null, null, null
		);

		int subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		int subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		cursor.moveToFirst(); // Id is unique, so it's safe to moveToFirst()

		ContentValues contentValues = new ContentValues();

		float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) - subjectGrade.getObtainedGrade();
		contentValues.put(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

		if(!subjectGrade.isExtraGrade() && !subjectGrade.isObtainedGradeNull()) {
			float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) - subjectGrade.getMaximumGrade();
			contentValues.put(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
		}

		cursor.close();

		db.update(
				SubjectsEntry.TABLE_NAME,
				contentValues,
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectGrade.getSubjectId())}
		);
		db.close();
	}

	public List<Subject> getAllSubjects() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Subject> subjectList = new ArrayList<>();

		Cursor subjectsCursor = db.query(
				SubjectsEntry.TABLE_NAME, // Table name
				null, // return all columns
				null,
				null,
				null, null,
				SubjectsEntry.COLUMN_SUBJECT_NAME + " ASC"
		);

		int columnSubjectIdIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_ID);
		int columnSubjectNameIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_NAME);
		int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION);
		int columnSubjectProfessorIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR);
		int columnSubjectObtainedGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		int columnSubjectMaximumGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		while(subjectsCursor.moveToNext()) {
			Subject subject = new Subject();

			subject.setId(subjectsCursor.getInt(columnSubjectIdIndex));
			subject.setName(subjectsCursor.getString(columnSubjectNameIndex));
			subject.setAbbreviation(subjectsCursor.getString(columnSubjectAbbreviationIndex));
			subject.setProfessor(subjectsCursor.getString(columnSubjectProfessorIndex));
			subject.setObtainedGrade(subjectsCursor.getFloat(columnSubjectObtainedGradeIndex));
			subject.setMaximumGrade(subjectsCursor.getFloat(columnSubjectMaximumGradeIndex));

			subjectList.add(subject);
		}

		subjectsCursor.close();
		db.close();
		return subjectList;
	}

	public List<Subject> getAllSubjectsInAverageGradeOrder(){
		SQLiteDatabase db = this.getReadableDatabase();
		List<Subject> subjectList = new ArrayList<>();

		// I'll have to do this in a raw query since the db.query using orderBy wasn't working

		String rawQuery = String.format("SELECT * FROM %s ORDER BY (%s / %s) ASC", SubjectsEntry.TABLE_NAME, SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		Cursor subjectsCursor = db.rawQuery(rawQuery, null);

		/*
		Cursor subjectsCursor = db.query(
				SubjectsEntry.TABLE_NAME, // Table name
				null, // return all columns
				null,
				null,
				null, null,
				SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE + "/" + SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE + " ASC"
		);

		 */

		int columnSubjectIdIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_ID);
		int columnSubjectNameIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_NAME);
		int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION);
		int columnSubjectProfessorIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR);
		int columnSubjectObtainedGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		int columnSubjectMaximumGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		while(subjectsCursor.moveToNext()) {
			Subject subject = new Subject();

			subject.setId(subjectsCursor.getInt(columnSubjectIdIndex));
			subject.setName(subjectsCursor.getString(columnSubjectNameIndex));
			subject.setAbbreviation(subjectsCursor.getString(columnSubjectAbbreviationIndex));
			subject.setProfessor(subjectsCursor.getString(columnSubjectProfessorIndex));
			subject.setObtainedGrade(subjectsCursor.getFloat(columnSubjectObtainedGradeIndex));
			subject.setMaximumGrade(subjectsCursor.getFloat(columnSubjectMaximumGradeIndex));

			subjectList.add(subject);
		}

		subjectsCursor.close();
		db.close();
		return subjectList;
	}

	public Subject getSubject(int subjectId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor subjectsCursor = db.query(
				SubjectsEntry.TABLE_NAME, // Table name
				null,
				SubjectsEntry.COLUMN_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)},
				null, null, null
		);

		int columnSubjectNameIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_NAME);
		int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION);
		int columnSubjectProfessorIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR);
		int columnSubjectObtainedGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		int columnSubjectMaximumGradeIndex = subjectsCursor.getColumnIndex(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		subjectsCursor.moveToFirst();

		Subject subject = new Subject();
		subject.setId(subjectId);
		subject.setName(subjectsCursor.getString(columnSubjectNameIndex));
		subject.setAbbreviation(subjectsCursor.getString(columnSubjectAbbreviationIndex));
		subject.setProfessor(subjectsCursor.getString(columnSubjectProfessorIndex));
		subject.setObtainedGrade(subjectsCursor.getFloat(columnSubjectObtainedGradeIndex));
		subject.setMaximumGrade(subjectsCursor.getFloat(columnSubjectMaximumGradeIndex));

		subjectsCursor.close();
		db.close();
		return subject;

	}

	// Related to subject grades

	public SubjectGrade getGrade(int gradeId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(
				GradesEntry.TABLE_NAME,
				new String[] {GradesEntry.COLUMN_GRADE_OBTAINED, GradesEntry.COLUMN_GRADE_MAXIMUM, GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT, GradesEntry.COLUMN_GRADE_SUBJECT_ID},
				GradesEntry.COLUMN_GRADE_ID + "=?",
				new String[] {String.valueOf(gradeId)},
				null, null, null
		);

		int subjectGradeIsExtraCreditIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT);
		int subjectGradeObtainedIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_OBTAINED);
		int subjectGradeMaximumIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_MAXIMUM);
		int subjectIdIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_SUBJECT_ID);

		cursor.moveToFirst(); // query should be unique, so it's safe to moveToFirst()

		SubjectGrade subjectGrade = new SubjectGrade(
				cursor.getFloat(subjectGradeObtainedIndex),
				cursor.getFloat(subjectGradeMaximumIndex),
				getBoolean(cursor.getInt(subjectGradeIsExtraCreditIndex))
		);

		subjectGrade.setGradeId(gradeId);
		subjectGrade.setSubjectId(cursor.getInt(subjectIdIndex));

		cursor.close();
		db.close();

		return subjectGrade;
	}

	public List<SubjectGrade> getAllGrades(int subjectId) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<SubjectGrade> gradesList = new ArrayList<>();

		Cursor cursor = db.query(
				GradesEntry.TABLE_NAME,
				null,
				GradesEntry.COLUMN_GRADE_SUBJECT_ID + "=?",
				new String[]{String.valueOf(subjectId)},
				null, null, null
		);

		int gradeIdIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_ID);
		int gradeDescriptionIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_DESCRIPTION);
		int obtainedGradeIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_OBTAINED);
		int maximumGradeIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_MAXIMUM);
		int isExtraCreditIndex = cursor.getColumnIndex(GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT);

		while(cursor.moveToNext()) {
			Integer gradeId = cursor.getInt(gradeIdIndex);
			String gradeDescription = cursor.getString(gradeDescriptionIndex);
			float obtainedGrade = cursor.getFloat(obtainedGradeIndex);
			float maximumGrade = cursor.getFloat(maximumGradeIndex);
			boolean isExtraCredit = (cursor.getInt(isExtraCreditIndex) == 1);

			SubjectGrade subjectGrade = new SubjectGrade(gradeId, gradeDescription, obtainedGrade, maximumGrade, isExtraCredit);
			subjectGrade.setSubjectId(subjectId);

			gradesList.add(subjectGrade);
		}

		cursor.close();
		db.close();

		return gradesList;
	}

	public void insertGrade(SubjectGrade subjectGrade) {
		addToTotalGrade(subjectGrade);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(GradesEntry.COLUMN_GRADE_SUBJECT_ID, subjectGrade.getSubjectId());
		contentValues.put(GradesEntry.COLUMN_GRADE_DESCRIPTION, subjectGrade.getGradeDescription());
		contentValues.put(GradesEntry.COLUMN_GRADE_OBTAINED, subjectGrade.getObtainedGrade());
		contentValues.put(GradesEntry.COLUMN_GRADE_MAXIMUM, subjectGrade.getMaximumGrade());
		contentValues.put(GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT, subjectGrade.isExtraGrade());

		db.insert(GradesEntry.TABLE_NAME, null, contentValues);
		db.close();
	}

	public void removeGrade(int gradeID) {
		removeFromTotalGrade(getGrade(gradeID));

		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(GradesEntry.TABLE_NAME,
				GradesEntry.COLUMN_GRADE_ID + "=?",
				new String[] {String.valueOf(gradeID)}
		);

		db.close();
	}

	public void removeAllGrades(int subjectId) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(
				GradesEntry.TABLE_NAME,
				GradesEntry.COLUMN_GRADE_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)}
		);

		db.close();
	}

	public void updateGrade(Integer gradeId, SubjectGrade newSubjectGrade){
		SubjectGrade oldGrade = getGrade(gradeId);
		removeFromTotalGrade(oldGrade);

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(GradesEntry.COLUMN_GRADE_DESCRIPTION, newSubjectGrade.getGradeDescription());
		contentValues.put(GradesEntry.COLUMN_GRADE_OBTAINED, newSubjectGrade.getObtainedGrade());
		contentValues.put(GradesEntry.COLUMN_GRADE_MAXIMUM, newSubjectGrade.getMaximumGrade());
		contentValues.put(GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT, newSubjectGrade.isExtraGrade());

		db.update(
				GradesEntry.TABLE_NAME,
				contentValues,
				GradesEntry.COLUMN_GRADE_ID + "=?",
				new String[] {String.valueOf(gradeId)}
		);

		db.close();

		newSubjectGrade.setSubjectId(oldGrade.getSubjectId());
		addToTotalGrade(newSubjectGrade);
	}

	// General use

	private boolean getBoolean(int intBoolean) {
		return intBoolean == 1;
	}

	// Exclusive to Ifes-Academico

	public void insertQAcadSubjectList(List<Subject> subjectList) {
		Log.d(TAG, "insertQAcadSubjectList: inserting subjectList with " + subjectList.size() + " items");

		for(Subject subject : subjectList) {

			int subjectId = insertSubject(
					new Subject(
							subject.getName(),
							subject.getProfessor(),
							subject.getName().substring(0, 3)
					)
			);

			for(Grade grade : subject.getGradeList()) {
				insertGrade(
						new SubjectGrade(
								subjectId,
								grade.getGradeDescription(),
								grade.getObtainedGrade() * grade.getWeight(),
								grade.getMaximumGrade() * grade.getWeight(),
								false,
								grade.isObtainedGradeNull()
						)
				);
			}
		}
	}

	public void updateSubjectsDatabase(List<Subject> subjects) {
		/*
		In this method, I insert the subjects list into a transitional table,
		then I switch the old and the original tables so the user can't see
		for a long time that the data is missing from the database (if switching
		fragments during update, for example)

		This solution was suggested by MikeT in
		https://stackoverflow.com/questions/55857902/how-to-drop-previous-records-on-android-sqlite-database-only-after-inserting-som/
		 */

		Log.d(TAG, "Updating entire database");

		SQLiteDatabase db = this.getWritableDatabase();
		boolean inTransaction = db.inTransaction();

		if(!inTransaction) {
			Log.v(TAG, "updateSubjectsDatabase: beginning transaction");
			db.beginTransaction();
		}

		Log.v(TAG, "updateSubjectsDatabase: creating transitional tables");

		db.execSQL(SubjectsEntry.SQL_CREATE_TRANSITIONAL_ENTRIES);
		db.execSQL(GradesEntry.SQL_CREATE_TRANSITIONAL_ENTRIES);

		for(Subject subject: subjects) {
			ContentValues subjectContentValue = new ContentValues();
			subjectContentValue.put(SubjectsEntry.COLUMN_SUBJECT_NAME, subject.getName());
			subjectContentValue.put(SubjectsEntry.COLUMN_SUBJECT_ABBREVIATION, subject.getName().substring(0, 3));
			subjectContentValue.put(SubjectsEntry.COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());
			subjectContentValue.put(SubjectsEntry.COLUMN_SUBJECT_OBTAINED_GRADE, subject.getObtainedGrade());
			subjectContentValue.put(SubjectsEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, subject.getMaximumGrade());

			long subjectId = db.insert(SubjectsEntry.TRANSITIONAL_TABLE_NAME, null, subjectContentValue);

			for(Grade grade: subject.getGradeList()) {
				SubjectGrade subjectGrade = new SubjectGrade(
						(int) subjectId,
						grade.getGradeDescription(),
						grade.getObtainedGrade() * grade.getWeight(),
						grade.getMaximumGrade() * grade.getWeight(),
						false,
						grade.isObtainedGradeNull()
				);

				ContentValues gradeContentValue = new ContentValues();
				gradeContentValue.put(GradesEntry.COLUMN_GRADE_SUBJECT_ID, subjectGrade.getSubjectId());
				gradeContentValue.put(GradesEntry.COLUMN_GRADE_DESCRIPTION, subjectGrade.getGradeDescription());
				gradeContentValue.put(GradesEntry.COLUMN_GRADE_OBTAINED, subjectGrade.getObtainedGrade());
				gradeContentValue.put(GradesEntry.COLUMN_GRADE_MAXIMUM, subjectGrade.getMaximumGrade());
				gradeContentValue.put(GradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT, subjectGrade.isExtraGrade());

				db.insert(GradesEntry.TRANSITIONAL_TABLE_NAME, null, gradeContentValue);
			}
		}

		String renameOldGradeTable = String.format("ALTER TABLE %s RENAME TO %s_old", GradesEntry.TABLE_NAME, GradesEntry.TABLE_NAME);
		String renameOldSubjectTable = String.format("ALTER TABLE %s RENAME TO %s_old", SubjectsEntry.TABLE_NAME, SubjectsEntry.TABLE_NAME);

		// SQLite automatically handles the renaming of foreign keys parent tables, since the transitional grades table references
		// the transitional subjects table, when this last one is renamed the reference is also updated.

		String renameNewSubjectTable = String.format("ALTER TABLE %s RENAME TO %s", SubjectsEntry.TRANSITIONAL_TABLE_NAME, SubjectsEntry.TABLE_NAME);
		String renameNewGradeTable = String.format("ALTER TABLE %s RENAME TO %s", GradesEntry.TRANSITIONAL_TABLE_NAME, GradesEntry.TABLE_NAME);

		Log.v(TAG, "updateSubjectsDatabase: switching tables");

		db.execSQL(renameOldGradeTable);
		db.execSQL(renameOldSubjectTable);

		db.execSQL(renameNewGradeTable);
		db.execSQL(renameNewSubjectTable);

		Log.v(TAG, "updateSubjectsDatabase: dropping old tables");

		db.execSQL(String.format("DROP TABLE IF EXISTS %s", GradesEntry.TABLE_NAME + "_old"));
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", SubjectsEntry.TABLE_NAME + "_old"));

		if(!inTransaction) {
			Log.v(TAG, "updateSubjectsDatabase: ending transaction");
			db.setTransactionSuccessful();
			db.endTransaction();
		}

		Log.v(TAG, "updateSubjectsDatabase: writing data to disk");
		db.close();
	}
}