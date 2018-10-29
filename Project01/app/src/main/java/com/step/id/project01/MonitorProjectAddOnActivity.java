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
import com.step.id.project01.RecyclerView.projectAddOnAdapter;
import com.step.id.project01.model.ProjectAddOnProvider;

import java.util.ArrayList;

public class MonitorProjectAddOnActivity extends AppCompatActivity {

    private static final String TAG = "projectList";

    private String selectedID,selectedTitle,userID;
    private com.step.id.project01.RecyclerView.projectAddOnAdapter projectAddOnAdapter;
    private View emptyView;
    private RecyclerView projectRecyclerView;
    private ArrayList<ProjectAddOnProvider> listNewProjectProviders = new ArrayList<>();

    //Fire base
    DatabaseReference databaseNewProject, projectsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_project_add_on);

        projectRecyclerView = (RecyclerView) findViewById(R.id.monitorProjectList_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(MonitorProjectAddOnActivity.this));
        projectRecyclerView.setHasFixedSize(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        userID = intent.getStringExtra("UID");
        selectedID = intent.getStringExtra("projectID");
        selectedTitle = intent.getStringExtra("title");

        databaseNewProject = FirebaseDatabase.getInstance().getReference();
        projectsRef = databaseNewProject.child("Projects Add On").child(selectedID);

        emptyView = findViewById(R.id.project_empty_view);

        onRetrieve();

    }

    public void onRetrieve() {

        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewProjectProviders.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ProjectAddOnProvider projects = dataSnapshot1.getValue(ProjectAddOnProvider.class);
                    listNewProjectProviders.add(projects);
                }
                projectAddOnAdapter = new projectAddOnAdapter(MonitorProjectAddOnActivity.this, listNewProjectProviders);
                projectRecyclerView.setAdapter(projectAddOnAdapter);

                if (listNewProjectProviders.isEmpty()) {
                    projectRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    projectRecyclerView.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(MonitorProjectAddOnActivity.this, MonitorProjectActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MonitorProjectAddOnActivity.this, MonitorProjectActivity.class);
        intent.putExtra("userID",userID);
        startActivity(intent);
    }



}
