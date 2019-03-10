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

import static calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper.SubjectEntry.*;

public class SubjectDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public SubjectDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        protected static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SUBJECT_NAME + " TEXT," +
                COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
                COLUMN_SUBJECT_PROFESSOR + " TEXT," +
                COLUMN_SUBJECT_OBTAINED_GRADE + " REAL," +
                COLUMN_SUBJECT_MAXIMUM_GRADE + " REAL)";

        protected static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void insertData(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT_NAME, subject.getName());
        contentValues.put(COLUMN_SUBJECT_ABBREVIATION, subject.getAbbreviation());
        contentValues.put(COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());
        contentValues.put(COLUMN_SUBJECT_OBTAINED_GRADE, 0);
        contentValues.put(COLUMN_SUBJECT_MAXIMUM_GRADE, 0);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void removeData(String abbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[] {COLUMN_SUBJECT_ID},
                COLUMN_SUBJECT_ABBREVIATION + "=?",
                new String[]{abbreviation},
                null, null, null
        );

        int columnSubjectIdIndex = cursor.getColumnIndex(COLUMN_SUBJECT_ID);
        cursor.moveToFirst();
        int subjectId = cursor.getInt(columnSubjectIdIndex);

        cursor.close();

        db.delete(TABLE_NAME, COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {abbreviation});
        db.close();

        SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(context);
        subjectGradesDatabase.deleteAllGrades(abbreviation);
        subjectGradesDatabase.close();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteSubjectClasses(subjectId);
        databaseHelper.close();
    }

    /**
     * Replaces rows that matches the old subject abbreviation with the new Subject object parameters
     * @param oldSubjectAbbreviation Old subject abbreviation to be compared in database
     * @param newSubject New subject object to edit database
     */

    public void updateData(String oldSubjectAbbreviation, Subject newSubject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT_NAME, newSubject.getName());
        contentValues.put(COLUMN_SUBJECT_ABBREVIATION, newSubject.getAbbreviation());
        contentValues.put(COLUMN_SUBJECT_PROFESSOR, newSubject.getProfessor());

        db.update(TABLE_NAME, contentValues, COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {oldSubjectAbbreviation});
        db.close();

        if(!oldSubjectAbbreviation.equals(newSubject.getAbbreviation())) {
            SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(context);
            subjectGradesDatabase.updateSubjectAbbreviation(oldSubjectAbbreviation, newSubject.getAbbreviation());
            subjectGradesDatabase.close();
        }
    }

    public void addGradeData(SubjectGrade subjectGrade) {
        Log.d(getClass().getName(), "Querying data for " + subjectGrade.getSubjectAbbreviation());

        SQLiteDatabase db = this.getWritableDatabase();
        String subjectAbbreviation = subjectGrade.getSubjectAbbreviation();

        Cursor cursor = db.query(TABLE_NAME, // Table name
                new String[] {COLUMN_SUBJECT_OBTAINED_GRADE, COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                COLUMN_SUBJECT_ABBREVIATION + "=?", // WHERE clause
                new String[] {subjectAbbreviation},
                null, null, null
        );

        Integer subjectObtainedGradeIndex = cursor.getColumnIndex(COLUMN_SUBJECT_OBTAINED_GRADE);
        Integer subjectMaximumGradeIndex = cursor.getColumnIndex(COLUMN_SUBJECT_MAXIMUM_GRADE);

        cursor.moveToFirst(); // Abbreviation is unique, so our desired subject should be the first

        float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) + subjectGrade.getObtainedGrade();
        float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) + subjectGrade.getMaximumGrade();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

        if(!subjectGrade.isExtraGrade()) {
            contentValues.put(COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
        }

        cursor.close();

        db.update(TABLE_NAME, contentValues, COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {subjectAbbreviation});
        db.close();
    }

    public void removeGradeData(SubjectGrade subjectGrade) {
        /* Yes, I'm just copy and pasting the entire code from addGradeData
         * and just changing + to - in newTotal[...]
         */

        SQLiteDatabase db = this.getWritableDatabase();
        String subjectAbbreviation = subjectGrade.getSubjectAbbreviation();

        Cursor cursor = db.query(TABLE_NAME, // Table name
                new String[] {COLUMN_SUBJECT_OBTAINED_GRADE, COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                COLUMN_SUBJECT_ABBREVIATION + "=?", // WHERE clause
                new String[] {subjectAbbreviation}, // arguments to WHERE clause
                null, null, null
        );

        Integer subjectObtainedGradeIndex = cursor.getColumnIndex(COLUMN_SUBJECT_OBTAINED_GRADE);
        Integer subjectMaximumGradeIndex = cursor.getColumnIndex(COLUMN_SUBJECT_MAXIMUM_GRADE);

        cursor.moveToFirst(); // Abbreviation is unique, so our desired subject should be the first

        float newTotalObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex) - subjectGrade.getObtainedGrade();
        float newTotalMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex) - subjectGrade.getMaximumGrade();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT_OBTAINED_GRADE, newTotalObtainedGrade);

        if(!subjectGrade.isExtraGrade()) {
            contentValues.put(COLUMN_SUBJECT_MAXIMUM_GRADE, newTotalMaximumGrade);
        }

        cursor.close();

        db.update(TABLE_NAME, contentValues, COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {subjectAbbreviation});
        db.close();
    }

    public Cursor getAllDataInAlphabeticalOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_SUBJECT_NAME + " ASC", null);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + "", null);
    }

    public Cursor getItemsId() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + SubjectEntry.COLUMN_SUBJECT_ID + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_SUBJECT_NAME + " ASC", null);
    }

    public boolean hasObject(String columnName, String entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {columnName}, columnName  + "=?", new String[] {entry}, null, null, null);

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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY (" + COLUMN_SUBJECT_OBTAINED_GRADE + "/" + COLUMN_SUBJECT_MAXIMUM_GRADE + ") ASC", null);
        db.close();
        return cursor;
    }

    public List<Subject> getAllSubjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Subject> subjectList = new ArrayList<>();

        Cursor subjectsCursor = db.query(
                TABLE_NAME, // Table name
                new String[] {COLUMN_SUBJECT_ID, COLUMN_SUBJECT_NAME, COLUMN_SUBJECT_ABBREVIATION, COLUMN_SUBJECT_PROFESSOR, COLUMN_SUBJECT_OBTAINED_GRADE, COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                null,
                null,
                null, null,
                COLUMN_SUBJECT_NAME + " ASC"
        );

        int columnSubjectIdIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_ID);
        int columnSubjectNameIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_NAME);
        int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_ABBREVIATION);
        int columnSubjectProfessorIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_PROFESSOR);
        int columnSubjectObtainedGradeIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_OBTAINED_GRADE);
        int columnSubjectMaximumGradeIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_MAXIMUM_GRADE);

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

        db.close();
        subjectsCursor.close();
        return subjectList;
    }

    public Subject getSubject(int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String TAG = getClass().getSimpleName();

        Log.d(TAG, String.valueOf(subjectId));

        Cursor subjectsCursor = db.query(
                TABLE_NAME, // Table name
                new String[] {COLUMN_SUBJECT_ID, COLUMN_SUBJECT_NAME, COLUMN_SUBJECT_ABBREVIATION, COLUMN_SUBJECT_PROFESSOR, COLUMN_SUBJECT_OBTAINED_GRADE, COLUMN_SUBJECT_MAXIMUM_GRADE}, // Columns to return
                COLUMN_SUBJECT_ID + "=?",
                new String[] {String.valueOf(subjectId)},
                null, null, null
        );

        int columnSubjectNameIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_NAME);
        int columnSubjectAbbreviationIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_ABBREVIATION);
        int columnSubjectProfessorIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_PROFESSOR);
        int columnSubjectObtainedGradeIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_OBTAINED_GRADE);
        int columnSubjectMaximumGradeIndex = subjectsCursor.getColumnIndex(COLUMN_SUBJECT_MAXIMUM_GRADE);

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

}
