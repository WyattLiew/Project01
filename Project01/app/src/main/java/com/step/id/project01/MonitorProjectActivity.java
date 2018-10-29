package com.step.id.project01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.RecyclerView.projectRecyclerAdapter;
import com.step.id.project01.model.newProjectProvider;

import java.util.ArrayList;

public class MonitorProjectActivity extends AppCompatActivity {

    private static final String TAG = "Monitor_projectActivity";
    private static final String projectID = "projectID";
    private String selectedID;

    private com.step.id.project01.RecyclerView.projectRecyclerAdapter projectRecyclerAdapter;
    private View emptyView;
    private RecyclerView projectRecyclerView;
    private ArrayList<newProjectProvider> listNewProjectProviders = new ArrayList<>();

    //Firebasse
    DatabaseReference databaseNewProject, projectsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_project);

        projectRecyclerView = (RecyclerView) findViewById(R.id.monitorProject_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        selectedID = intent.getStringExtra("userID");
        Log.d(TAG, "MonitorProject Selected ID is: " + selectedID);

        emptyView = findViewById(R.id.monitorProject_empty_view);
        databaseNewProject = FirebaseDatabase.getInstance().getReference();
        projectsRef = databaseNewProject.child("Projects").child(selectedID);


        onRetrieve();

        projectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MonitorProjectActivity.this, projectRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String rowid = listNewProjectProviders.get(position).getId();
                Intent intent = new Intent(MonitorProjectActivity.this, MonitorProjectAddOnActivity.class);
                intent.putExtra(projectID, listNewProjectProviders.get(position).getId());
                intent.putExtra("title",listNewProjectProviders.get(position).getTitle());
                intent.putExtra("UID",selectedID);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void onRetrieve() {

        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewProjectProviders.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    newProjectProvider projects = dataSnapshot1.getValue(newProjectProvider.class);
                    listNewProjectProviders.add(projects);
                }
                projectRecyclerAdapter = new projectRecyclerAdapter(MonitorProjectActivity.this, listNewProjectProviders);
                projectRecyclerView.setAdapter(projectRecyclerAdapter);

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

}
