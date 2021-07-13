package com.example.easyattendence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.easyattendence.Activities.AttendenceActivity;
import com.example.easyattendence.Activities.AttendenceActivity.AttendenceResult;
import com.example.easyattendence.Activities.HistoryActivity;
import com.example.easyattendence.Activities.MainActivity;
import com.example.easyattendence.Students.StudentContract;
import com.example.easyattendence.Students.StudentDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendenceTaken {
    private static final String TAG = MainActivity.class.getSimpleName();
    List<AttendenceResult> resulttaken;
    public static final String STUDENTNAME = "studentname";
    public static final String STUDENTROLLNO = "Studentrollnumber";
    public static final String STUDENTSTATUS = "Studentstatus";
    Context mContext;

    public AttendenceTaken(List<AttendenceResult> results, Context context) {
        this.resulttaken = results;
        this.mContext = context;
        String resultjsonarray = createJsonArray();
        insertvalues(resultjsonarray);
    }

    private void insertvalues(String resultjsonarray) {
        StudentDbHelper helper = new StudentDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        String date = getCurrentDateAndTime();
        boolean b = checkdate(db, date);
        String date1;
        if (b) {
            date1 = changedate(date, db);
        } else {
            date1 = date;
        }
        if (resultjsonarray.length() > 0) {
            ContentValues values = new ContentValues();
            values.put(StudentContract.StudentEntry.COL_RESULT_ARRAY, resultjsonarray);
            values.put(StudentContract.StudentEntry.COL_RESULT_DATE, date1);
            db.insert(StudentContract.StudentEntry.TABLE_RESULT, null, values);
        }
    }

    private String changedate(String date, SQLiteDatabase db) {
        int i = 1;
        while (true) {
            String recheck = date.concat("(" + i + ")");
            if (checkdate(db, recheck)) {
                i++;
            } else {
                date = recheck;
                break;
            }
        }
        return date;
    }

    private boolean checkdate(SQLiteDatabase db, String date) {
        String query = "SELECT * FROM " + StudentContract.StudentEntry.TABLE_RESULT +
                " WHERE " + StudentContract.StudentEntry.COL_RESULT_DATE + "=  '" + date + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            Log.e(TAG, "true more ");
            return true;
        } else {
            cursor.close();
            Log.e(TAG, "false more ");
            return false;
        }

    }

    private String createJsonArray() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (AttendenceResult result : resulttaken) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(STUDENTNAME, result.getSname());
                obj.put(STUDENTROLLNO, result.getSrollno());
                obj.put(STUDENTSTATUS, result.isPresent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
        try {
            json.put(StudentContract.StudentEntry.COL_RESULT_ARRAY, jsonArray);
            Log.e(TAG, jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String getCurrentDateAndTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);
        Log.e(TAG, formattedDate);
        return formattedDate;
    }
}
