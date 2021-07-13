package com.example.easyattendence.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.easyattendence.Fragments.HistoryFragment;
import com.example.easyattendence.R;
import com.example.easyattendence.Students.StudentContract;
import com.example.easyattendence.Students.StudentDbHelper;
import com.example.easyattendence.helper.AttendenceTaken;
import com.google.android.material.navigation.NavigationView;
import com.example.easyattendence.Activities.AttendenceActivity.AttendenceResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private androidx.appcompat.widget.Toolbar toolbar;
    private NavigationView mNavigationView;
    public List<StudentDbHelper.TakenResult> resultList;
    public StudentDbHelper db;
    private List<AttendenceResult> mattendenceResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Saved Attendance");
        toolbar = findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        db = new StudentDbHelper(this);
        resultList = db.getresultlist();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        AddNewNavMenuItem();
        if (mNavigationView != null)
            mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void AddNewNavMenuItem() {
        Menu menu = mNavigationView.getMenu();
        Menu submenu = menu.addSubMenu("Attendance History");
        for (int i = 0; i < resultList.size(); i++) {
            submenu.add(resultList.get(i).mcurrentDate);
        }
        mNavigationView.invalidate();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        for (int i = 0; i < resultList.size(); i++) {
            if (title == resultList.get(i).mcurrentDate) {
                setTitle(resultList.get(i).mcurrentDate);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                String sqlstringlist = resultList.get(i).mjsonArray;
                mattendenceResults = retrieveList(sqlstringlist);
                break;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.history_list), (ArrayList<AttendenceResult>) mattendenceResults);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.history_list_container, fragment).commit();
        return false;
    }

    public List<AttendenceResult> retrieveList(String SqlList) {
        List<AttendenceActivity.AttendenceResult> attendenceResults = new ArrayList<>();
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(SqlList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (resultObject != null) {
            JSONArray jsonArray = resultObject.optJSONArray(StudentContract.StudentEntry.COL_RESULT_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                AttendenceActivity.AttendenceResult result = null;
                JSONObject arrayObject = null;
                try {
                    arrayObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (arrayObject != null) {
                    try {
                        result = new AttendenceResult(arrayObject.getString(AttendenceTaken.STUDENTNAME),
                                arrayObject.getString(AttendenceTaken.STUDENTROLLNO), arrayObject.getBoolean(AttendenceTaken.STUDENTSTATUS));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (result != null) {
                    attendenceResults.add(result);
                }
            }
        }
        return attendenceResults;
    }
}