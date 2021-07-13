package com.example.easyattendence.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattendence.Activities.AttendenceActivity;
import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentDbHelper.StudentInfo;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.AttendenceViewHolder> {
    public List<StudentInfo> mlist;
    public Context mContext;
    OnEmptyListener listener;
    public List<AttendenceActivity.AttendenceResult> mlistresult;
    public boolean isresult;

    public interface OnEmptyListener {
        void emptyrecyclerview();
    }

    public StudentListAdapter(Context context, List<StudentInfo> list, OnEmptyListener listener) {
        this.mlist = list;
        this.mContext = context;
        this.listener = listener;
    }

    public StudentListAdapter(Context context, List<AttendenceActivity.AttendenceResult> listresult, boolean result, OnEmptyListener listener) {
        this.mContext = context;
        this.mlistresult = listresult;
        this.isresult = result;
        this.listener = listener;
    }

    @Override
    public AttendenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.attendence_view, parent, false);
        return new AttendenceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(StudentListAdapter.AttendenceViewHolder holder, int position) {

        if (isresult) {
            String studentname = mlistresult.get(position).getSname();
            String rollno = mlistresult.get(position).getSrollno();
            holder.studentname.setText(studentname);
            holder.rollno.setText(rollno);
            boolean attendence = mlistresult.get(position).isPresent();
            if (attendence) {
                holder.ispresent.setText("P");
            } else {
                holder.ispresent.setText("A");
            }
        } else {
            String studentname = mlist.get(position).getname();
            String rollno = mlist.get(position).getRollno();
            holder.studentname.setText(studentname);
            holder.rollno.setText(rollno);
        }
    }

    public StudentInfo islistempty(int position) {
        StudentInfo info = mlist.remove(position);
        if (mlist.isEmpty()) {
            listener.emptyrecyclerview();
        }
        return info;
    }

    public int rechecklistempty(int position) {
        AttendenceActivity.AttendenceResult result = mlistresult.remove(position);
        if (mlistresult.isEmpty()) {
            listener.emptyrecyclerview();
        }
        String rollno = result.getSrollno();
        int pos = Integer.parseInt(rollno);
        return pos - 1;
    }

    @Override
    public int getItemCount() {
        if (isresult) {
            return mlistresult.size();
        } else
            return mlist.size();
    }

    class AttendenceViewHolder extends RecyclerView.ViewHolder {
        TextView studentname;
        TextView rollno;
        TextView ispresent;

        public AttendenceViewHolder(View itemView) {
            super(itemView);
            studentname = (TextView) itemView.findViewById(R.id.attendence_name);
            rollno = (TextView) itemView.findViewById(R.id.attendence_rollno);
            if (isresult) {
                ispresent = (TextView) itemView.findViewById(R.id.present_or_not);
                ispresent.setVisibility(View.VISIBLE);
            }
        }
    }
}
    