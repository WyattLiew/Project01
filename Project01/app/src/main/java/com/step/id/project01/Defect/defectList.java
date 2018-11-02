package com.step.id.project01.Defect;

import android.content.Intent;
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
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.RecyclerView.defectAddOnAdapter;
import com.step.id.project01.model.defect;

import java.util.ArrayList;

public class defectList extends AppCompatActivity {

    private static final String TAG = "defectList";

    private String selectedID, selectedTitle;
    private View emptyView;

    private defectAddOnAdapter defectAddOnAdapter;
    private RecyclerView defectRecyclerView;
    private ArrayList<defect> listNewDefect = new ArrayList<>();

    //Fire base
    DatabaseReference databaseNewDefect, defectRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_list);

        defectRecyclerView = (RecyclerView) findViewById(R.id.defectList_recyclerView);
        defectRecyclerView.setLayoutManager(new LinearLayoutManager(defectList.this));
        defectRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        selectedID = intent.getStringExtra("pendingID");
        selectedTitle = intent.getStringExtra("Title");

        databaseNewDefect = FirebaseDatabase.getInstance().getReference();
        defectRef = databaseNewDefect.child("Defect Add On").child(selectedID);

        emptyView = findViewById(R.id.defect_empty_view);

        onRetrieve();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.defectList_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(defectList.this, defectAddOn.class);
                intent.putExtra("pendingID", selectedID);
                intent.putExtra("Title", selectedTitle);
                startActivity(intent);
            }
        });

        defectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(defectList.this, defectRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                String rowid = listNewDefect.get(position).getId();
                String defect = listNewDefect.get(position).getDefect();
                String date = listNewDefect.get(position).getDate();
                String comment = listNewDefect.get(position).getComments();

                int HideMenu = 1;
                Log.d(TAG, "The row id is: " + rowid);

                Log.d(TAG, "onItemClick: The ID is: " + rowid);
                Intent intent = new Intent(defectList.this, defectAddOn.class);
                intent.putExtra("defectAddOn", rowid);
                intent.putExtra("pendingID", selectedID);
                intent.putExtra("projImage", listNewDefect.get(position).getImgURL());
                intent.putExtra("defect1", defect);
                intent.putExtra("date", date);
                intent.putExtra("Title", selectedTitle);
                intent.putExtra("comments", comment);
                intent.putExtra("HideMenu", HideMenu);
                Log.d(TAG, "The row id is: " + rowid);
                startActivity(intent);
            }
        }));
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
                defectAddOnAdapter = new defectAddOnAdapter(defectList.this, listNewDefect);
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
}
