package com.example.easyattendence.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentDbHelper.StudentInfo;

import java.util.ArrayList;
import java.util.List;

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.StudentViewHolder> {
    private static final String TAG = CursorAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;
    private static List<StudentInfo> mlist;
    private int selectedPosition = -1;

    private final OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View v, int position, int roll_no);

        void OnLongPress(View v, int position, int roll_no);
    }

    public CursorAdapter(Context context, OnItemClickListener listener, List<StudentInfo> list) {
        mlist = new ArrayList<StudentInfo>();
        mContext = context;
        itemClickListener = listener;
        mlist = list;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_detail_view, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
//        mCursor.moveToPosition(position);
//        int nameindex = mCursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_STUDENT_NAME);
//        int rollnoindex = mCursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_ROLL_NO);
//        String studentname = mCursor.getString(nameindex);
//        String rollno = mCursor.getString(rollnoindex);
        String studentname = mlist.get(position).getname();
        String rollno = mlist.get(position).getRollno();
        holder.mname.setText(studentname);
        holder.mrollno.setText(rollno);
        holder.selectBox.setVisibility(mlist.get(position).ischeckshown ? View.VISIBLE : View.GONE);
        holder.selectBox.setOnCheckedChangeListener(null);
        holder.selectBox.setChecked(mlist.get(holder.getAdapterPosition()).isIscheck());
        holder.selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mlist.get(holder.getAdapterPosition()).ischeck = isChecked;
            }
        });
    }

    @Override
    public int getItemCount() {
//        if (mCursor == null) {
//            return 0;
//        }
//        return mCursor.getCount();
        if (mlist == null) {
            return 0;
        }
        return mlist.size();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public void swaplist(List<StudentInfo> list) {
        if (list == null || mlist == list) {
            return;
        }
        list.clear();
        mlist.addAll(list);
        this.notifyDataSetChanged();
    }

    public void showselectall() {
        for (StudentInfo info : mlist) {
            info.ischeckshown = true;
        }
        notifyDataSetChanged();
    }

    public void hideselectall() {
        for (StudentInfo info : mlist) {
            info.ischeckshown = false;
            info.ischeck = false;
        }
        notifyDataSetChanged();
    }

    public void delete(StudentInfo info) {
        mlist.remove(info);
        int position = mlist.indexOf(info);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mrollno;
        TextView mname;
        final CheckBox selectBox;

        public StudentViewHolder(View itemView) {
            super(itemView);
            mrollno = (TextView) itemView.findViewById(R.id.RollNumber);
            mname = (TextView) itemView.findViewById(R.id.studentname);
            selectBox = (CheckBox) itemView.findViewById(R.id.select_all);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int rollno = Integer.parseInt(mrollno.getText().toString());
            itemClickListener.OnItemClick(v, getAdapterPosition(), rollno);
        }

        @Override
        public boolean onLongClick(View v) {
            int rollno = Integer.parseInt(mrollno.getText().toString());
            itemClickListener.OnLongPress(v, getAdapterPosition(), rollno);
            showselectall();
            return true;
        }

    }
}