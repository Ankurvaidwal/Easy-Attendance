package com.example.easyattendence.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattendence.Activities.AttendenceActivity;
import com.example.easyattendence.Activities.MainActivity;
import com.example.easyattendence.Adapters.Historyadapter;
import com.example.easyattendence.R;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private static final String TAG = Historyadapter.class.getSimpleName();
    ArrayList<AttendenceActivity.AttendenceResult> results;
    private RecyclerView mRecyclerView;
    Historyadapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.history_list_fragment, container, false);
        Context context = getContext();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            results = bundle.getParcelableArrayList(getString(R.string.history_list));
        }
        if (results == null)
            Log.e(TAG, "list is null");
        else
            Log.e(TAG, "list is not null");
        adapter = new Historyadapter(context, results);
        mRecyclerView = (RecyclerView) viewGroup.findViewById(R.id.history_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(adapter);
        return viewGroup;
    }
}
