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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.RecyclerView.DefImageAdapter;
import com.step.id.project01.model.defectImageAddon;

import java.util.ArrayList;

public class MonitorDefectAddOnDetailActivity extends AppCompatActivity {
    private static final String TAG = "MonitorAddOnDetailActivity";

    private TextView mDefectTextView, mCommentTextView, mDateTextView;

    private String selectedID, selectedTitle,selectedDefectID,userID;
    private String selectedComment, selectedDate, selectedDefect;
    private View emptyView;

    private RecyclerView mUploadList;
    private ArrayList<defectImageAddon> listNewDefect =new ArrayList<>();
    private DefImageAdapter defImageAdapter;

    //Fire base
    private DatabaseReference mDatabaseAddonRef,mDatabaseAddonImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_defect_add_on_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // init Id
        initId();

        initDetails();

        mUploadList = (RecyclerView) findViewById(R.id.MonitorDetailRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddonImage = FirebaseDatabase.getInstance().getReference("Defect add on image").child(selectedDefectID);

        onRetrieve();

    }
    public void initId() {

        mDateTextView = (TextView)findViewById(R.id.Monitor_defectDate);
        mDefectTextView = (TextView)findViewById(R.id.Monitor_defectTextView);
        mCommentTextView = (TextView)findViewById(R.id.Monitor_commentTextView);

    }

    private void initDetails(){
        Intent intent = getIntent();
        selectedID = intent.getStringExtra("pendingID");
        selectedDefectID = intent.getStringExtra("defectAddon");
        selectedDefect = intent.getStringExtra("defect");
        selectedComment = intent.getStringExtra("comment");
        selectedDate = intent.getStringExtra("date");
        userID = intent.getStringExtra("UID");
        selectedTitle = intent.getStringExtra("Title");

        mDefectTextView.setText(selectedDefect);
        mCommentTextView.setText(selectedComment);
        mDateTextView.setText(selectedDate);
    }

    private void onRetrieve() {

        mDatabaseAddonImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewDefect.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    defectImageAddon defectImage = dataSnapshot1.getValue(defectImageAddon.class);
                    listNewDefect.add(defectImage);
                }
                defImageAdapter = new DefImageAdapter(MonitorDefectAddOnDetailActivity.this, listNewDefect);
                mUploadList.setAdapter(defImageAdapter);

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
                Intent intent = new Intent(MonitorDefectAddOnDetailActivity.this, MonitorDefectAddOnActivity.class);
                intent.putExtra("UID",userID);
                intent.putExtra("pendingID",selectedID);
                intent.putExtra("Title",selectedTitle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MonitorDefectAddOnDetailActivity.this, MonitorDefectAddOnActivity.class);
        intent.putExtra("UID",userID);
        intent.putExtra("pendingID",selectedID);
        intent.putExtra("Title",selectedTitle);
        startActivity(intent);
    }
}
