package com.example.easyattendence.Students;

import android.net.Uri;
import android.provider.BaseColumns;

public class StudentContract {

    private StudentContract() {
    }

    //    the authority , which is how the codes knows which content provider to access
    public static final String AUTHORITY = "com.example.easyattendence";

    //    the base content uri ="content //:"+authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //    path for accessing the data in the contract
    public static final String PATH = "easyattendence";

    // StudentEntry an inner class to define the columns of the table
    public static final class StudentEntry implements BaseColumns {
        // StudentEntry content uri = BASE_CONTENT_URI + PATH
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);

        public final static String _ID = "ID";
        // Table Name for class
        public static final String TABLE_NAME_1 = "StudentsAttendence";

        // Table name for saved attendence
        public static final String TABLE_RESULT = "SavedAttendence";

        // column for the roll number
        public static final String COLUMN_ROLL_NO = "Roll_No";

        // column for the student name
        public static final String COLUMN_STUDENT_NAME = "Name";

        // column to save arraylist into the row of the table
        public static final String COL_RESULT_ARRAY = "Result_Array";

        // column to save DATE into the row of the table
        public static final String COL_RESULT_DATE = "Result_Date";
    }
}
