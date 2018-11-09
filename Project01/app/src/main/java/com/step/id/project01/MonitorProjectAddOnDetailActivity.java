package com.step.id.project01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.RecyclerView.ProjImageAdapter;
import com.step.id.project01.model.projectImageAddon;

import java.util.ArrayList;

public class MonitorProjectAddOnDetailActivity extends AppCompatActivity {

    private static final String TAG = "MonitorProjectDetailActivity";

    private TextView mStatusTextView, mNotesTextView, mDateTextView;

    private String selectedID, selectedTitle,selectedProjectID,userID;
    private String selectedNotes, selectedDate, selectedStatus;

    private RecyclerView mUploadList;
    private ArrayList<projectImageAddon> listNewProject =new ArrayList<>();
    private ProjImageAdapter projImageAdapter;

    //Fire base
    private DatabaseReference mDatabaseAddonRef,mDatabaseAddonImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_project_add_on_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // init Id
        initId();

        initDetails();

        mUploadList = (RecyclerView) findViewById(R.id.MonitorProjectDetailsRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddonImage = FirebaseDatabase.getInstance().getReference("Project add on image").child(selectedProjectID);

        onRetrieve();

    }
    public void initId() {

        mDateTextView = (TextView)findViewById(R.id.Monitor_projDateTextView);
        mStatusTextView = (TextView)findViewById(R.id.Monitor_statusTextView);
        mNotesTextView = (TextView)findViewById(R.id.Monitor_notesTextView);

    }

    private void initDetails(){
        Intent intent = getIntent();
        selectedID = intent.getStringExtra("projectID");
        selectedTitle = intent.getStringExtra("title");
        selectedProjectID = intent.getStringExtra("projectAddOn");
        selectedStatus = intent.getStringExtra("status");
        selectedNotes = intent.getStringExtra("notes");
        selectedDate = intent.getStringExtra("date");
        userID = intent.getStringExtra("UID");

        mStatusTextView.setText(selectedStatus);
        mNotesTextView.setText(selectedNotes);
        mDateTextView.setText(selectedDate);
    }

    private void onRetrieve() {

        mDatabaseAddonImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewProject.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    projectImageAddon projectImage = dataSnapshot1.getValue(projectImageAddon.class);
                    listNewProject.add(projectImage);
                }
                projImageAdapter = new ProjImageAdapter(MonitorProjectAddOnDetailActivity.this, listNewProject);
                mUploadList.setAdapter(projImageAdapter);

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
                Intent intent = new Intent(MonitorProjectAddOnDetailActivity.this, MonitorProjectAddOnActivity.class);
                intent.putExtra("UID",userID);
                intent.putExtra("projectID",selectedID);
                intent.putExtra("title",selectedTitle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MonitorProjectAddOnDetailActivity.this, MonitorProjectAddOnActivity.class);
        intent.putExtra("UID",userID);
        intent.putExtra("projectID",selectedID);
        intent.putExtra("title",selectedTitle);
        startActivity(intent);
    }
}
