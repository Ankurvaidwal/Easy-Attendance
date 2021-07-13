package com.example.easyattendence.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyattendence.Adapters.CursorAdapter;
import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentContract.StudentEntry;
import com.example.easyattendence.Students.StudentDbHelper;
import com.example.easyattendence.Students.StudentDbHelper.StudentInfo;

import java.util.List;

public class AddStudent extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_STUDENT_LOADER = 0;
    private boolean isstudentchanged = false;
    private Button mButton;
    private StudentDbHelper dbHelper;
    StudentInfo info;
    CursorAdapter mAdapter;
    private static List<StudentInfo> list;
    EditText mNameEditText;
    EditText mRollNoEditText;
    Uri getUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isstudentchanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        dbHelper = new StudentDbHelper(this);
        Intent intent = getIntent();
        getUri = intent.getData();
        mButton = (Button) findViewById(R.id.Take_attendence);
        dbHelper = new StudentDbHelper(this);
        mNameEditText = (EditText) findViewById(R.id.NameEditext);
        mRollNoEditText = (EditText) findViewById(R.id.RollnumberEdittext);
        mNameEditText.setOnTouchListener(mTouchListener);
        mRollNoEditText.setOnTouchListener(mTouchListener);
        if (getUri != null) {
            setTitle(getString(R.string.update_student_detail));
            mButton.setText(R.string.Save_detail);
            LoaderManager.getInstance(this).initLoader(EXISTING_STUDENT_LOADER, null, this);
        } else {
            setTitle(getString(R.string.add_new_student));
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewEntry(v);
            }
        });
    }

    public void NewEntry(View v) {
        String studentname = ((EditText) findViewById(R.id.NameEditext)).getText().toString();
        String studentrollno = ((EditText) findViewById(R.id.RollnumberEdittext)).getText().toString();
        if (TextUtils.isEmpty(studentname) || TextUtils.isEmpty(studentrollno) || !isNumericvalid(studentrollno) || (studentname.contains(" ") && !isNameValid(studentname))) {
            Toast.makeText(this, R.string.student_feed_validity, Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(StudentEntry.COLUMN_STUDENT_NAME, studentname);
        values.put(StudentEntry.COLUMN_ROLL_NO, studentrollno);
        if (getUri == null) {
            if (isvalid(studentrollno))
                return;
            Uri uri = getContentResolver().insert(StudentEntry.CONTENT_URI, values);
            list = dbHelper.getlist();
            if (uri != null) {
                Toast.makeText(getBaseContext(), studentname + " is saved into the Classroom", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), studentname + " is not saved into the Classroom", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!isstudentchanged) {
                NavUtils.navigateUpFromSameTask(AddStudent.this);
            }
            int rowsAffected = getContentResolver().update(getUri, values, null, null);
            list = dbHelper.getlist();
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                if (isstudentchanged)
                    Toast.makeText(this, getString(R.string.update_successful),
                            Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (getUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void deletestudent() {
        if (getUri != null) {
            int rowdeleted = getContentResolver().delete(getUri, null, null);
            if (rowdeleted == 0) {
                Toast.makeText(this, "Error in deleting the Student", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Student is deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private boolean isvalid(String studentrollno) {
        boolean temp = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String Sqlquery = "SELECT " + StudentEntry.COLUMN_ROLL_NO + " FROM " + StudentEntry.TABLE_NAME_1 + " WHERE "
                + StudentEntry.COLUMN_ROLL_NO + " = " + studentrollno;
        Cursor cursor = db.rawQuery(Sqlquery, null);
        if (cursor.getCount() > 0) {

            Toast.makeText(getBaseContext(), "Roll Number is already in use", Toast.LENGTH_SHORT).show();

            temp = true;
        }
        cursor.close();
        return temp;
    }

    private boolean isNameValid(String strName) {
        if (strName == null) {
            return false;
        }
        boolean flag = false;
        for (int i = 0; i < strName.length(); i++) {
            char ch = strName.charAt(i);
            if (ch > 'a' && ch < 'z' || ch > 'A' && ch < 'Z') {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isNumericvalid(String strNum) {
        if (strNum == null) {
            return false;
        }
        double d;
        try {
            d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        if (d > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StudentEntry.COLUMN_ROLL_NO,
                StudentEntry.COLUMN_STUDENT_NAME
        };
        return new CursorLoader(this,
                getUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getUri == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int RollnoIndex = data.getColumnIndex(StudentEntry.COLUMN_ROLL_NO);
            int NameIndex = data.getColumnIndex(StudentEntry.COLUMN_STUDENT_NAME);
            String Studentname = data.getString(NameIndex);
            String Rollno = data.getString(RollnoIndex);
            mRollNoEditText.setText(Rollno);
            mNameEditText.setText(Studentname);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRollNoEditText.setText("");
        mNameEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        if (!isstudentchanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showdunsavedchangesdialog(discardButtonClickListener);
    }

    private void showdunsavedchangesdialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.no, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletestudent();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuid = item.getItemId();
        switch (menuid) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}