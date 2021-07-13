package com.example.easyattendence.Students;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.easyattendence.Activities.MainActivity;
import com.example.easyattendence.Students.StudentContract.StudentEntry;

import java.util.ArrayList;
import java.util.List;

public class StudentDbHelper extends SQLiteOpenHelper {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Name of the database
    private static final String DATABASE = "Student.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    // Constructor
    public StudentDbHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    /**
     * Called when database is created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_CLASS = "CREATE TABLE " + StudentEntry.TABLE_NAME_1 + " (" +
                StudentEntry.COLUMN_ROLL_NO + " INTEGER PRIMARY KEY, " +
                StudentEntry.COLUMN_STUDENT_NAME + " TEXT NOT NULL );";
        final String CREATE_TABLE_RESULT = "CREATE TABLE " + StudentEntry.TABLE_RESULT + " (" +
                StudentEntry.COL_RESULT_ARRAY + " TEXT NOT NULL, " +
                StudentEntry.COL_RESULT_DATE + " TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_RESULT);
    }

    public List<StudentInfo> getlist() {
        List<StudentInfo> list = new ArrayList<StudentInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + StudentEntry.TABLE_NAME_1;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int RollnoIndex = cursor.getColumnIndex(StudentEntry.COLUMN_ROLL_NO);
                int NameIndex = cursor.getColumnIndex(StudentEntry.COLUMN_STUDENT_NAME);
                String Studentname = cursor.getString(NameIndex);
                String StudentRollno = cursor.getString(RollnoIndex);
                boolean ischecked = false;
                boolean ischeck = false;
                list.add(new StudentInfo(Studentname, StudentRollno, ischecked, ischeck));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<TakenResult> getresultlist() {
        List<TakenResult> list = new ArrayList<TakenResult>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + StudentEntry.TABLE_RESULT;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int resultjsonarray = cursor.getColumnIndex(StudentEntry.COL_RESULT_ARRAY);
                int currdate = cursor.getColumnIndex(StudentEntry.COL_RESULT_DATE);
                String Stringdate = cursor.getString(currdate);
                String Stringjsonarray = cursor.getString(resultjsonarray);
                list.add(new TakenResult(Stringjsonarray, Stringdate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + StudentEntry.TABLE_RESULT);

        // create new tables
        onCreate(db);
    }

    public static class StudentInfo {
        public String name;
        public String rollno;
        public boolean ischeckshown;
        public boolean ischeck;

        public StudentInfo(String name, String rollno, boolean ischeckshown, boolean ischeck) {
            this.name = name;
            this.rollno = rollno;
            this.ischeckshown = ischeckshown;
            this.ischeck = ischeck;
        }

        public String getname() {
            return name;
        }

        public String getRollno() {
            return rollno;
        }

        public boolean isIscheck() {
            return ischeck;
        }
    }

    public static class TakenResult {
        public String mjsonArray;
        public String mcurrentDate;

        public TakenResult(String jsonArray, String currentDate) {
            this.mcurrentDate = currentDate;
            this.mjsonArray = jsonArray;
        }
    }
}
