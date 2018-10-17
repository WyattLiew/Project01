package com.step.id.project01.Project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.step.id.project01.R;
import com.step.id.project01.RecyclerView.projectAddOnAdapter;
import com.step.id.project01.model.ProjectAddOnProvider;
import com.step.id.project01.sqlitedata.ProjectDbHelper;

import java.util.ArrayList;


public class projectList extends AppCompatActivity {
    private static final String TAG = "projectList";

    private String selectedID;

    private SQLiteDatabase sqLiteDatabase;
    private ProjectDbHelper projectDbHelper;
    private projectAddOnAdapter projectAddOnAdapter;
    private Cursor cursor;
    private View emptyView;
    private RecyclerView projectRecyclerView;
    private ArrayList<ProjectAddOnProvider> listNewProjectProviders = new ArrayList<>();

    //Fire base
    DatabaseReference databaseNewProject, projectsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        projectRecyclerView = (RecyclerView) findViewById(R.id.projectList_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(projectList.this));
        projectRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        selectedID = intent.getStringExtra("projectID");
        Log.d(TAG, "project List Selected ID is: " + selectedID);

        databaseNewProject = FirebaseDatabase.getInstance().getReference();
        projectsRef = databaseNewProject.child("Projects Add On").child(selectedID);

        emptyView = findViewById(R.id.project_empty_view);


        onRetrieve();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.projectList_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(projectList.this, projectAddOn.class);
                intent.putExtra("projectID", selectedID);
                startActivity(intent);
            }
        });
/**
 projectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(projectList.this, projectRecyclerView, new RecyclerTouchListener.ClickListener() {
@Override public void onClick(View view, int position) {

int status = listNewProjectProviders.get(position).getStatus();
String date = listNewProjectProviders.get(position).getDate();
String notes = listNewProjectProviders.get(position).getNotes();

ImageView projImage = (ImageView) view.findViewById(R.id.projectList_Image);
projImage.setDrawingCacheEnabled(true);
projImage.buildDrawingCache();
final Bitmap bitmap = projImage.getDrawingCache();
ByteArrayOutputStream stream = new ByteArrayOutputStream();
bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
byte[] bytes = stream.toByteArray();


int HideMenu = 1;

Long rowid = listNewProjectProviders.get(position).getId();
Cursor data = projectDbHelper.getProjectAddOnItemID(rowid);
Log.d(TAG, "The row id is: " + rowid);

int itemID = -1;
while (data.moveToNext()) {
itemID = data.getInt(0);
status = data.getInt(1);
date = data.getString(2);
notes = data.getString(3);
}
if (itemID > -1) {
Log.d(TAG, "onItemClick: The ID is: " + itemID);
Intent intent = new Intent(projectList.this, projectAddOn.class);
intent.putExtra("id", itemID);
intent.putExtra("projectId", selectedID);
intent.putExtra("projImage", bytes);
intent.putExtra("status", status);
intent.putExtra("date", date);
intent.putExtra("notes", notes);
intent.putExtra("HideMenu", HideMenu);
Log.d(TAG, "The row id is: " + rowid);
startActivity(intent);
}

}

@Override public void onLongClick(View view, int position) {
//
}
}));
 **/

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
                projectAddOnAdapter = new projectAddOnAdapter(projectList.this, listNewProjectProviders);
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

}