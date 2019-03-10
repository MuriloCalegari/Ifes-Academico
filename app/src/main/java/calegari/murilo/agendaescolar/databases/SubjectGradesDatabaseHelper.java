package calegari.murilo.agendaescolar.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGrade;

public class SubjectGradesDatabaseHelper extends SQLiteOpenHelper {

    private final String columnGradeSubjectAbbreviation = SubjectGradesEntry.COLUMN_GRADE_SUBJECT_ABBREVIATION;
    private final String tableName = SubjectGradesEntry.TABLE_NAME;
    private final String columnGradeDescription = SubjectGradesEntry.COLUMN_GRADE_DESCRIPTION;
    private final String columnGradeObtained = SubjectGradesEntry.COLUMN_GRADE_OBTAINED;
    private final String columnGradeMaximum = SubjectGradesEntry.COLUMN_GRADE_MAXIMUM;
    private final String columnGradeId = SubjectGradesEntry.COLUMN_GRADE_ID;
    private final String columnIsExtraCredit = SubjectGradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT;
    private Context mContext;

    public static class SubjectGradesEntry implements BaseColumns {
        /*
		I know I should have put all tables inside a single database, but by the time I realized
		this I had already written too many code that relies on this class and now it's just not
		worth it. Please good practice people, do not kill me.
		 */
        public static final String DATABASE_NAME = "schooltools_subject_grades.db";
        public static final String TABLE_NAME = "subjectgrades";
        public static final String COLUMN_GRADE_ID = "gradeid";
        public static final String COLUMN_GRADE_SUBJECT_ABBREVIATION = "subjectabbreviation";
        public static final String COLUMN_GRADE_DESCRIPTION = "gradedescription";
        public static final String COLUMN_GRADE_OBTAINED = "obtainedgrade";
        public static final String COLUMN_GRADE_MAXIMUM = "maximumgrade";
        public static final String COLUMN_GRADE_IS_EXTRA_CREDIT = "isextracredit";
        public static final Integer DATABASE_VERSION = 4;

        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                SubjectGradesEntry.TABLE_NAME + "( " +
                SubjectGradesEntry.COLUMN_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubjectGradesEntry.COLUMN_GRADE_SUBJECT_ABBREVIATION + " TEXT," +
                SubjectGradesEntry.COLUMN_GRADE_DESCRIPTION + " TEXT," +
                SubjectGradesEntry.COLUMN_GRADE_OBTAINED + " REAL," +
                SubjectGradesEntry.COLUMN_GRADE_MAXIMUM + " REAL," +
                SubjectGradesEntry.COLUMN_GRADE_IS_EXTRA_CREDIT + " INTEGER)";

        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SubjectGradesEntry.TABLE_NAME;
    }

    public SubjectGradesDatabaseHelper(@Nullable Context context) {
        super(context, SubjectGradesEntry.DATABASE_NAME, null, SubjectGradesEntry.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(getClass().getCanonicalName(), SubjectGradesEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SubjectGradesEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SubjectGradesEntry.SQL_DELETE_ENTRIES);
        db.execSQL(SubjectGradesEntry.SQL_CREATE_ENTRIES);
    }

    public void insertGrade(SubjectGrade subjectGrade) {
        addToTotalGrade(subjectGrade);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(columnGradeSubjectAbbreviation, subjectGrade.getSubjectAbbreviation());
        contentValues.put(columnGradeDescription, subjectGrade.getGradeDescription());
        contentValues.put(columnGradeObtained, subjectGrade.getObtainedGrade());
        contentValues.put(columnGradeMaximum, subjectGrade.getMaximumGrade());
        contentValues.put(columnIsExtraCredit, subjectGrade.isExtraGrade());

        db.insert(tableName, null, contentValues);
        db.close();
    }

    public void removeGrade(Integer gradeID) {
        removeFromTotalGrade(gradeID);
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(tableName,
                columnGradeId + "=?",
                new String[] {String.valueOf(gradeID)}
        );

        db.close();
    }

    public void deleteAllGrades(String subjectAbbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                tableName,
                columnGradeSubjectAbbreviation + "=?",
                new String[] {subjectAbbreviation}
        );
        db.close();
    }

    public void updateGrade(Integer oldSubjectGradeID, SubjectGrade newSubjectGrade){
        removeFromTotalGrade(oldSubjectGradeID);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(columnGradeDescription, newSubjectGrade.getGradeDescription());
        contentValues.put(columnGradeObtained, newSubjectGrade.getObtainedGrade());
        contentValues.put(columnGradeMaximum, newSubjectGrade.getMaximumGrade());
        contentValues.put(columnIsExtraCredit, newSubjectGrade.isExtraGrade());

        db.update(
                tableName,
                contentValues,
                columnGradeId + "=?",
                new String[] {String.valueOf(oldSubjectGradeID)}
        );

        db.close();
        addToTotalGrade(newSubjectGrade);
    }

    public void addToTotalGrade(SubjectGrade subjectGrade) {
            SubjectDatabaseHelper subjectDatabase = new SubjectDatabaseHelper(mContext);
            subjectDatabase.addGradeData(subjectGrade);
            subjectDatabase.close();
    }

    /**
     * Removes the grade from the subjects database,
     * this method must be called before the grade is
     * removed from the subjectgrades database
     * @param oldSubjectGradeID The ID with which the method will query the database
     */

    public void removeFromTotalGrade(Integer oldSubjectGradeID) {
        SQLiteDatabase subjectGradesDatabase = this.getWritableDatabase();
        SubjectDatabaseHelper subjectDatabase = new SubjectDatabaseHelper(mContext);

        Cursor cursor = subjectGradesDatabase.query(
                tableName,
                new String[] {columnGradeId,columnGradeSubjectAbbreviation,columnGradeObtained,columnGradeMaximum, columnIsExtraCredit},
                columnGradeId + "=?",
                new String[] {String.valueOf(oldSubjectGradeID)},
                null, null, null

        );

        Integer subjectGradeIsExtraCreditIndex = cursor.getColumnIndex(columnIsExtraCredit);
        Integer subjectGradeSubjectAbbreviation = cursor.getColumnIndex(columnGradeSubjectAbbreviation);
        Integer subjectGradeObtainedIndex = cursor.getColumnIndex(columnGradeObtained);
        Integer subjectGradeMaximumIndex = cursor.getColumnIndex(columnGradeMaximum);

        cursor.moveToFirst(); // query should be unique, so it's safe to moveToFirst()

        SubjectGrade subjectGrade = new SubjectGrade(
                cursor.getFloat(subjectGradeObtainedIndex),
                cursor.getFloat(subjectGradeMaximumIndex),
                getBoolean(cursor.getInt(subjectGradeIsExtraCreditIndex))
                );
        subjectGrade.setSubjectAbbreviation(cursor.getString(subjectGradeSubjectAbbreviation));

        subjectDatabase.removeGradeData(subjectGrade);
        cursor.close();

        subjectGradesDatabase.close();
        subjectDatabase.close();
    }

    private boolean getBoolean(int intBoolean) {
        return intBoolean == 1;
    }

    public Cursor getSubjectGradesData(String gradeSubjectAbbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(
                tableName,
                new String[]{columnGradeId, columnGradeDescription, columnGradeObtained, columnGradeMaximum, columnIsExtraCredit},
                columnGradeSubjectAbbreviation + "=?",
                new String[]{gradeSubjectAbbreviation},
                null, null, null
        );
    }

    public void updateSubjectAbbreviation(String oldSubjectAbbreviation, String newSubjectAbbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(columnGradeSubjectAbbreviation, newSubjectAbbreviation);

        db.update(
                tableName,
                contentValues,
                columnGradeSubjectAbbreviation + "=?",
                new String[] {oldSubjectAbbreviation}
        );

        db.close();
    }

}
