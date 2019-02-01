package calegari.murilo.agendaescolar.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

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
				COLUMN_CLASS_START_TIME + " DATETIME," +
				COLUMN_CLASS_END_TIME + " DATETIME)";

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

	public void insertClassTime(ClassTime classTime) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(ScheduleEntry.COLUMN_CLASS_SUBJECT_ID, classTime.getSubjectId());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_DAY, classTime.getDayOfTheWeek());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_START_TIME, classTime.getStartTime());
		contentValues.put(ScheduleEntry.COLUMN_CLASS_END_TIME, classTime.getEndTime());

		db.insert(
				ScheduleEntry.TABLE_NAME,
				null,
				contentValues
		);
		db.close();
	}

}
