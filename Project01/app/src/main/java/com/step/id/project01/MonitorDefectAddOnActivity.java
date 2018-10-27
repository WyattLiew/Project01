package com.step.id.project01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.RecyclerView.defectAddOnAdapter;
import com.step.id.project01.model.defect;

import java.util.ArrayList;

public class MonitorDefectAddOnActivity extends AppCompatActivity {

    private static final String TAG = "MonitorAddOnActivity";

    private String selectedID, selectedTitle,userID;
    private View emptyView;

    private com.step.id.project01.RecyclerView.defectAddOnAdapter defectAddOnAdapter;
    private RecyclerView defectRecyclerView;
    private ArrayList<defect> listNewDefect = new ArrayList<>();

    //Fire base
    DatabaseReference databaseNewDefect, defectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_defect_add_on);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        defectRecyclerView = (RecyclerView) findViewById(R.id.monitorDefectList_recyclerView);
        defectRecyclerView.setLayoutManager(new LinearLayoutManager(MonitorDefectAddOnActivity.this));
        defectRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        selectedID = intent.getStringExtra("pendingID");
        userID = intent.getStringExtra("UID");
        selectedTitle = intent.getStringExtra("Title");

        databaseNewDefect = FirebaseDatabase.getInstance().getReference();
        defectRef = databaseNewDefect.child("Defect Add On").child(selectedID);

        emptyView = findViewById(R.id.monitorDefect_empty_view);

        onRetrieve();
    }
    public void onRetrieve() {

        defectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewDefect.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    defect defects = dataSnapshot1.getValue(defect.class);
                    listNewDefect.add(defects);
                }
                defectAddOnAdapter = new defectAddOnAdapter(MonitorDefectAddOnActivity.this, listNewDefect);
                defectRecyclerView.setAdapter(defectAddOnAdapter);

                if (listNewDefect.isEmpty()) {
                    defectRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    defectRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MonitorDefectAddOnActivity.this, MonitorDefectActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MonitorDefectAddOnActivity.this, MonitorDefectActivity.class);
        intent.putExtra("userID",userID);
        startActivity(intent);
    }
}
