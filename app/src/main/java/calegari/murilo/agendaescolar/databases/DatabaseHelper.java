package calegari.murilo.agendaescolar.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.calendar.ClassTime;

public class DatabaseHelper extends SQLiteOpenHelper {

	private Context context;

	public static class GlobalEntry implements BaseColumns {
		public static final String DATABASE_NAME = "schooltoolsdatabase.db";
		public static final int DATABASE_VERSION = 1;
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

	public DatabaseHelper(@Nullable Context context) {
		super(context, GlobalEntry.DATABASE_NAME, null, GlobalEntry.DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ScheduleEntry.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(ScheduleEntry.SQL_DELETE_ENTRIES);
		db.execSQL(ScheduleEntry.SQL_CREATE_ENTRIES);
	}

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
		SQLiteDatabase db = this.getWritableDatabase();

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
		SQLiteDatabase db = this.getWritableDatabase();

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

	public void deleteSubjectClasses(int subjectId) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(
				ScheduleEntry.TABLE_NAME,
				ScheduleEntry.COLUMN_CLASS_SUBJECT_ID + "=?",
				new String[] {String.valueOf(subjectId)}
		);

		db.close();
	}
}
