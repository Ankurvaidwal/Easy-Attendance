package com.example.easyattendence.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattendence.Activities.AttendenceActivity;
import com.example.easyattendence.R;

import java.util.ArrayList;
import java.util.List;

public class Historyadapter extends RecyclerView.Adapter<Historyadapter.HistoryViewHolder> {
    public Context mContext;
    public List<AttendenceActivity.AttendenceResult> mlistresult;

    public Historyadapter(Context context, ArrayList<AttendenceActivity.AttendenceResult> historylist) {
        this.mContext = context;
        this.mlistresult = historylist;
    }

    @NonNull
    @Override
    public Historyadapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.attendence_view, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mlistresult.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView studentname;
        TextView rollno;
        TextView ispresent;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            studentname = (TextView) itemView.findViewById(R.id.attendence_name);
            rollno = (TextView) itemView.findViewById(R.id.attendence_rollno);
            ispresent = (TextView) itemView.findViewById(R.id.present_or_not);
            ispresent.setVisibility(View.VISIBLE);

        }
    }
}
