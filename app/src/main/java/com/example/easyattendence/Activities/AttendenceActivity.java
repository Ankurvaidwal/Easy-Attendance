package com.example.easyattendence.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.easyattendence.Adapters.StudentListAdapter;
import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentDbHelper;
import com.example.easyattendence.Students.StudentDbHelper.StudentInfo;
import com.example.easyattendence.helper.AttendenceTaken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendenceActivity extends AppCompatActivity implements StudentListAdapter.OnEmptyListener {
    private static final String TAG = AttendenceActivity.class.getSimpleName();
    private StudentDbHelper dbHelper;
    private List<StudentInfo> mlist;
    private List<StudentInfo> mlistdummy;
    StudentListAdapter adapter;
    private RecyclerView mRecyclerView;
    private ConstraintLayout viewLayout;
    private List<AttendenceResult> resultList;
    private List<AttendenceResult> rechecklist;
    private Button recheckButton;
    private Button saveButton;
    Context mcontext;
    private boolean checking = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            viewLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        mcontext = getApplicationContext();
        setTitle(getString(R.string.attendence_title));
        dbHelper = new StudentDbHelper(this);
        recheckButton = (Button) findViewById(R.id.check_button);
        saveButton = (Button) findViewById(R.id.save_button);
        viewLayout = (ConstraintLayout) findViewById(R.id.save_or_recheck);
        mlist = dbHelper.getlist();
        resultList = new ArrayList<>();
        rechecklist = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.attendence_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mlistdummy = mlist;
        adapter = new StudentListAdapter(this, mlistdummy, this);
        mRecyclerView.setAdapter(adapter);
        recheckButton.setOnTouchListener(touchListener);
        setTouchListener();

        recheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checking = true;
                invalidateOptionsMenu();
                Collections.sort(resultList);
                rechecklist.clear();
                rechecklist.addAll(resultList);
                adapter = new StudentListAdapter(AttendenceActivity.this, rechecklist, true, AttendenceActivity.this);
                mRecyclerView.setAdapter(adapter);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttendenceTaken taken = new AttendenceTaken(resultList, AttendenceActivity.this);
                Toast.makeText(AttendenceActivity.this, "Attendence is saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void emptyrecyclerview() {
        viewLayout.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        checking = false;
        invalidateOptionsMenu();
    }

    public void setTouchListener() {
        ItemTouchHelper touchHelper =
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        super.clearView(recyclerView, viewHolder);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            // Get RecyclerView item from the ViewHolder
                            View itemView = viewHolder.itemView;

                            Paint p = new Paint();
                            if (dX > 0) {
                                /* Set your color for positive displacement */

                                p.setColor(getResources().getColor(R.color.green));
                                // Draw Rect with varying right side, equal to displacement dX
                                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                        (float) itemView.getBottom(), p);
                            }
                            else {
                                /* Set your color for negative displacement */
                                p.setColor(getResources().getColor(R.color.red));
                                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                        (float) itemView.getRight(), (float) itemView.getBottom(), p);

                            }

                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }

                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        if (direction == ItemTouchHelper.LEFT) {
                            if (checking) {
                                int pos = adapter.rechecklistempty(position);
                                AttendenceResult result = resultList.get(pos);
                                result.setSpresent(false);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeRemoved(position, rechecklist.size());
                            } else {
                                StudentInfo info = adapter.islistempty(position);
                                resultList.add(new AttendenceResult(info.getname(), info.getRollno(), false));
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeRemoved(position, mlistdummy.size());
                            }
                            adapter.notifyDataSetChanged();
                        } else if (direction == ItemTouchHelper.RIGHT) {
                            if (checking) {
                                int pos = adapter.rechecklistempty(position);
                                AttendenceResult result = resultList.get(pos);
                                result.setSpresent(true);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeRemoved(position, rechecklist.size());
                            } else {
                                StudentInfo info = adapter.islistempty(position);
                                resultList.add(new AttendenceResult(info.getname(), info.getRollno(), true));
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeRemoved(position, mlistdummy.size());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        touchHelper.attachToRecyclerView(mRecyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendence_menu, menu);
        MenuItem terminateItem = menu.findItem(R.id.terminate);
        if (checking) {
            terminateItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.terminate) {
            showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.terminate_operation);
        builder.setPositiveButton(R.string.terminate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                rechecklist.clear();
                adapter.notifyDataSetChanged();
                viewLayout.setVisibility(View.VISIBLE);
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
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.stop_attendence);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

    public static class AttendenceResult implements Comparable<AttendenceResult>, Parcelable {
        String Sname;
        String Srollno;
        boolean Spresent;

        AttendenceResult(String sname, String srollno, boolean present) {
            this.Sname = sname;
            this.Srollno = srollno;
            this.Spresent = present;
        }

        protected AttendenceResult(Parcel in) {
            Sname = in.readString();
            Srollno = in.readString();
            Spresent = in.readByte() != 0;
        }

        public String getSname() {
            return Sname;
        }


        public String getSrollno() {
            return Srollno;
        }

        public boolean isPresent() {
            return Spresent;
        }

        public void setSname(String sname) {
            Sname = sname;
        }

        public void setSrollno(String srollno) {
            Srollno = srollno;
        }

        public void setSpresent(boolean spresent) {
            Spresent = spresent;
        }

        @Override
        public int compareTo(AttendenceResult o) {
            return Srollno.compareTo(o.Srollno);
        }

        public static final Creator<AttendenceResult> CREATOR = new Creator<AttendenceResult>() {
            @Override
            public AttendenceResult createFromParcel(Parcel in) {
                return new AttendenceResult(in);
            }

            @Override
            public AttendenceResult[] newArray(int size) {
                return new AttendenceResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Sname);
            dest.writeString(Srollno);
            dest.writeByte((byte) (Spresent ? 1 : 0));
        }
    }
}