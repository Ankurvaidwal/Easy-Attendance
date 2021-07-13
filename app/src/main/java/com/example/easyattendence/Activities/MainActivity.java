package com.example.easyattendence.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyattendence.Adapters.CursorAdapter;
import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentContract;
import com.example.easyattendence.Students.StudentDbHelper;
import com.example.easyattendence.Students.StudentDbHelper.StudentInfo;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CursorAdapter.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ATTENDENCE_LOADER_ID = 0;
    ExtendedFloatingActionButton fab;
    private boolean ischeckshown = false;
    private CursorAdapter StudentAdapter;
    RecyclerView mRecyclerView;
    List<StudentInfo> mlist;
    StudentDbHelper helper;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideallview();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.Studentrecyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        helper = new StudentDbHelper(this);
        mlist = helper.getlist();
        StudentAdapter = new CursorAdapter(this, this, mlist);
        mRecyclerView.setAdapter(StudentAdapter);
        fab = (ExtendedFloatingActionButton) findViewById(R.id.Take_attendence);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendenceActivity.class);
                startActivity(intent);
            }
        });
        fab.setOnTouchListener(touchListener);
        setTitle(R.string.Classroom);
        LoaderManager.getInstance(this).initLoader(ATTENDENCE_LOADER_ID, null, this);

    }


    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager.getInstance(this).restartLoader(ATTENDENCE_LOADER_ID, null, MainActivity.this);
        mlist = helper.getlist();
        StudentAdapter = new CursorAdapter(this, this, mlist);
        mRecyclerView.setAdapter(StudentAdapter);
        StudentAdapter.swaplist(mlist);
        StudentAdapter.notifyDataSetChanged();
    }

    @Override
    final public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem deleteItem = menu.findItem(R.id.main_delete);
        MenuItem addItem = menu.findItem(R.id.add_student_main);
        if (ischeckshown) {
            deleteItem.setVisible(true);
            addItem.setVisible(false);
        } else {
            deleteItem.setVisible(false);
            addItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem deleteItem = menu.findItem(R.id.main_delete);
        MenuItem historyItem = menu.findItem(R.id.history);
        if (ischeckshown) {
            deleteItem.setVisible(true);
            historyItem.setVisible(false);
        } else {
            deleteItem.setVisible(false);
            historyItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid = item.getItemId();
        if (itemid == R.id.main_delete) {
            showDeleteConfirmationDialog();
        }
        if (itemid == R.id.add_student_main) {
            Intent intent = new Intent(MainActivity.this, AddStudent.class);
            startActivity(intent);
        }
        if (itemid == R.id.history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteselected() {
        int totaldel = 0;
        for (int childCount = mRecyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
            CheckBox getBox = holder.itemView.findViewById(R.id.select_all);
            TextView rollnumberTextView = holder.itemView.findViewById(R.id.RollNumber);
            TextView nameTextView = holder.itemView.findViewById(R.id.studentname);
            String idstring = rollnumberTextView.getText().toString();
            int id = Integer.parseInt(idstring);
            Uri deluri = ContentUris.withAppendedId(StudentContract.StudentEntry.CONTENT_URI, id);
            if (getBox.isChecked()) {
                int delitem = getContentResolver().delete(deluri, null, null);
                int actualPosition = holder.getAdapterPosition();
                mlist.remove(actualPosition);
                StudentAdapter.notifyItemRemoved(actualPosition);
                StudentAdapter.notifyItemRangeChanged(actualPosition, mlist.size());
                totaldel += delitem;

            }
        }
        hideallview();
        Toast.makeText(this, "Total deleted student " + totaldel, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteselected();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (mCursor != null) {
                    deliveresult(mCursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(StudentContract.StudentEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            StudentContract.StudentEntry.COLUMN_ROLL_NO);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliveresult(Cursor cursor) {
                mCursor = cursor;
                super.deliverResult(cursor);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        StudentAdapter.swapCursor(data);
        StudentAdapter.swaplist(mlist);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        StudentAdapter.swaplist(null);
//        StudentAdapter.swapCursor(null);

    }


    private void showallview() {
        ischeckshown = true;
        fab.setVisibility(View.GONE);
        invalidateOptionsMenu();
//        for (int childCount = mRecyclerView.getChildCount(), i = 0; i < childCount; ++i) {
//            final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
//            CheckBox getBox = holder.itemView.findViewById(R.id.select_all);
//            getBox.setVisibility(View.VISIBLE);
//        }
//        View child;
//        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
//            child = mRecyclerView.getChildAt(i);
//            //In case you need to access ViewHolder:
//            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(child);
//            CheckBox getBox = holder.itemView.findViewById(R.id.select_all);
//            if (getBox.isChecked()) {
//                getBox.setChecked(true);
//            }
//        }
        StudentAdapter.notifyDataSetChanged();
    }

    private void hideallview() {
        ischeckshown = false;
        fab.setVisibility(View.VISIBLE);
        StudentAdapter.hideselectall();
        invalidateOptionsMenu();
        StudentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (ischeckshown) {
            hideallview();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void OnItemClick(View v, int position, int _id) {
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        if (!ischeckshown) {
            Intent editIntent = new Intent(this, AddStudent.class);
            editIntent.putExtra(Intent.EXTRA_TEXT, _id);
            Uri getUri = ContentUris.withAppendedId(StudentContract.StudentEntry.CONTENT_URI, _id);
            editIntent.setData(getUri);
            startActivity(editIntent);
        }
    }

    @Override
    public void OnLongPress(View v, int position, int roll_no) {
        showallview();
    }
}

