package calegari.murilo.agendaescolar.databases;

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
import calegari.murilo.agendaescolar.subjectgrades.SubjectGrade;
import calegari.murilo.agendaescolar.subjects.Subject;

public class SubjectDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public SubjectDatabaseHelper(@Nullable Context context) {
        super(context, SubjectEntry.DATABASE_NAME, null, SubjectEntry.DATABASE_VERSION);
        this.context = context;
    }

	public static class SubjectEntry implements BaseColumns {
        public static final String DATABASE_NAME = "schooltools.db";
        public static final String TABLE_NAME = "subjects";
        public static final String COLUMN_SUBJECT_NAME = "name";
        public static final String COLUMN_SUBJECT_ABBREVIATION = "abbreviation";
        public static final String COLUMN_SUBJECT_PROFESSOR = "professor";
        // This naming is very bad, sorry english speakers
        public static final String COLUMN_SUBJECT_OBTAINED_GRADE = "obtainedgrade";
        public static final String COLUMN_SUBJECT_MAXIMUM_GRADE = "maximumgrade";
        public static final String COLUMN_SUBJECT_ID = "ID";
        public static final Integer DATABASE_VERSION = 1;

        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                SubjectEntry.TABLE_NAME + " (" + SubjectEntry.COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_PROFESSOR + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE + " REAL," +
                SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE + " REAL)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SubjectEntry.TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubjectEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SubjectEntry.SQL_DELETE_ENTRIES);
    }

    public void insertData(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_NAME, subject.getName());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, subject.getAbbreviation());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE, 0);
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, 0);

        db.insert(SubjectEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void removeData(String abbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SubjectEntry.TABLE_NAME, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {abbreviation});
        db.close();

        SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(context);
        subjectGradesDatabase.deleteAllGrades(abbreviation);
        subjectGradesDatabase.close();
    }

    /**
     * Replaces rows that matches the old subject abbreviation with the new Subject object parameters
     * @param oldSubjectAbbreviation Old subject abbreviation to be compared in database
     * @param newSubject New subject object to edit database
     */

    public void updateData(String oldSubjectAbbreviation, Subject newSubject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_NAME, newSubject.getName());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, newSubject.getAbbreviation());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_PROFESSOR, newSubject.getProfessor());

        db.update(SubjectEntry.TABLE_NAME, contentValues, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {oldSubjectAbbreviation});
        db.close();

        if(!oldSubjectAbbreviation.equals(newSubject.getAbbreviation())) {
            SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(context);
            subjectGradesDatabase.updateSubjectAbbreviation(oldSubjectAbbreviation, newSubject.getAbbreviation());
        }
    }

    public void addGradeData(SubjectGrade subjectGrade) {
        Log.d(getClass().getName(), "Querying data for " + subjectGrade.getSubjectAbbreviation());

        SQLiteDatabase db = this.getWritableDatabase();
        String subjectAbbreviation = subjectGrade.getSubjectAbbreviation();

        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, // Table name
                new String[] {SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE, SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", // WHERE clause
                new String[] {subjectAbbreviation},
                null, null, null
        );

        Integer subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
        Integer subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

        cursor.moveToFirst(); // Abbreviation is unique, so our desired subject should be the first

        float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) + subjectGrade.getObtainedGrade();
        float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) + subjectGrade.getMaximumGrade();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

        if(!subjectGrade.isExtraGrade()) {
            contentValues.put(SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
        }

        cursor.close();

        db.update(SubjectEntry.TABLE_NAME, contentValues, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {subjectAbbreviation});
        db.close();
    }

    public void removeGradeData(SubjectGrade subjectGrade) {
        /* Yes, I'm just copy and pasting the entire code from addGradeData
         * and just changing + to - in newTotal[...]
         */

        SQLiteDatabase db = this.getWritableDatabase();
        String subjectAbbreviation = subjectGrade.getSubjectAbbreviation();

        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, // Table name
                new String[] {SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE, SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", // WHERE clause
                new String[] {subjectAbbreviation}, // arguments to WHERE clause
                null, null, null
        );

        Integer subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
        Integer subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

        cursor.moveToFirst(); // Abbreviation is unique, so our desired subject should be the first

        float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) - subjectGrade.getObtainedGrade();
        float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) - subjectGrade.getMaximumGrade();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

        if(!subjectGrade.isExtraGrade()) {
            contentValues.put(SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
        }

        cursor.close();

        db.update(SubjectEntry.TABLE_NAME, contentValues, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {subjectAbbreviation});
        db.close();
    }

    public Cursor getAllDataInAlphabeticalOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + SubjectEntry.TABLE_NAME + " ORDER BY " + SubjectEntry.COLUMN_SUBJECT_NAME + " ASC", null);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + SubjectEntry.TABLE_NAME + "", null);
    }

    public Cursor getItemsId() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + SubjectEntry.COLUMN_SUBJECT_ID + " FROM " + SubjectEntry.TABLE_NAME + " ORDER BY " + SubjectEntry.COLUMN_SUBJECT_NAME + " ASC", null);
    }

    public boolean hasObject(String columnName, String entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, new String[] {columnName}, columnName  + "=?", new String[] {entry}, null, null, null);

        if(cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }


    public Cursor getAllDataInAverageGradeOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + SubjectEntry.TABLE_NAME + " ORDER BY (" + SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE + "/" + SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE + ") ASC", null);
    }

    public List<Subject> getAllSubjectsInAlphabeticalOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Subject> subjectList = new ArrayList<>();

        Cursor subjectsCursor = db.query(
                SubjectEntry.TABLE_NAME, // Table name
                new String[] {SubjectEntry.COLUMN_SUBJECT_NAME, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION}, // Columns to return
                null,
                null,
                null, null,
                SubjectEntry.COLUMN_SUBJECT_NAME + " ASC"
        );

        int columnSubjectNameIndex = subjectsCursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_NAME);
        int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION);

        while(subjectsCursor.moveToNext()) {
            Subject subject = new Subject();
            subject.setName(subjectsCursor.getString(columnSubjectNameIndex));
            subject.setAbbreviation(subjectsCursor.getString(columnSubjectAbbreviationIndex));

            subjectList.add(subject);
        }

        return subjectList;
    }

}
