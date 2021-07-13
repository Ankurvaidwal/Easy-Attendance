package com.example.easyattendence.Students;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.easyattendence.Students.StudentContract.StudentEntry;

public class StudentContentProvider extends ContentProvider {

    private static final int ATTENDENCE = 100;
    private static final int ATTENDENCE_ID = 101;

//  Uri Matcher static variable

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        All paths added to the UriMatcher have a corresponding int.
        uriMatcher.addURI(StudentContract.AUTHORITY, StudentContract.PATH, ATTENDENCE);
        uriMatcher.addURI(StudentContract.AUTHORITY, StudentContract.PATH + "/#", ATTENDENCE_ID);
        return uriMatcher;
    }

    private StudentDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new StudentDbHelper(context);
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor mCursor;
        switch (match) {
            case ATTENDENCE:
                mCursor = db.query(StudentEntry.TABLE_NAME_1,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ATTENDENCE_ID:
                selection = StudentEntry.COLUMN_ROLL_NO + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                mCursor = db.query(StudentEntry.TABLE_NAME_1, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnuri;
        switch (match) {
            case ATTENDENCE:
                long id = db.insert(StudentEntry.TABLE_NAME_1, null, values);
                if (id > 0) {
                    returnuri = ContentUris.withAppendedId(StudentEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnuri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ATTENDENCE:
                return db.delete(StudentEntry.TABLE_NAME_1, selection, selectionArgs);
            case ATTENDENCE_ID:
                selection = StudentEntry.COLUMN_ROLL_NO + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(StudentEntry.TABLE_NAME_1, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ATTENDENCE:
                return updatestudent(uri, values, selection, selectionArgs);
            case ATTENDENCE_ID:
                selection = StudentEntry.COLUMN_ROLL_NO + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatestudent(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updatestudent(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StudentEntry.COLUMN_STUDENT_NAME)) {
            String name = values.getAsString(StudentEntry.COLUMN_STUDENT_NAME);
            if (name == null) {
                Toast.makeText(getContext(), "Student must have a name", Toast.LENGTH_SHORT).show();
            }
        }
        if (values.containsKey(StudentEntry.COLUMN_ROLL_NO)) {
            Integer rollno = values.getAsInteger(StudentEntry.COLUMN_ROLL_NO);
            if (rollno != null && rollno < 1) {
                Toast.makeText(getContext(), "Not a valid roll number", Toast.LENGTH_SHORT).show();
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int detailupdated = db.update(StudentEntry.TABLE_NAME_1, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (detailupdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return detailupdated;
    }

}
