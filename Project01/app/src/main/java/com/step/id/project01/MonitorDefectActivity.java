package com.step.id.project01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.RecyclerView.pendingRecyclerAdapter;
import com.step.id.project01.model.Pending;

import java.util.ArrayList;

public class MonitorDefectActivity extends AppCompatActivity {

    private static final String TAG = "MonitorDefectActivity";
    private static final String pendingID = "pendingID";
    private String selectedID;

    private com.step.id.project01.RecyclerView.pendingRecyclerAdapter pendingRecyclerAdapter;
    private View emptyView;
    private RecyclerView pendingRecyclerView;
    private ArrayList<Pending> listNewPending = new ArrayList<>();

    //Firebasse
    DatabaseReference databaseNewPending, pendingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_defect);

        pendingRecyclerView = (RecyclerView) findViewById(R.id.monitorDefect_recyclerView);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        selectedID = intent.getStringExtra("userID");

        emptyView = findViewById(R.id.monitorDefect_empty_view);
        databaseNewPending = FirebaseDatabase.getInstance().getReference();
        pendingRef = databaseNewPending.child("Pending").child(selectedID);


        onRetrieve();

        pendingRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MonitorDefectActivity.this, pendingRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String rowid = listNewPending.get(position).getId();
                Intent intent = new Intent(MonitorDefectActivity.this, MonitorDefectAddOnActivity.class);
                intent.putExtra(pendingID, listNewPending.get(position).getId());
                intent.putExtra("Title",listNewPending.get(position).getTitle());
                intent.putExtra("UID",selectedID);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                //
            }

        }));
    }
    public void onRetrieve() {

        pendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNewPending.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Pending projects = dataSnapshot1.getValue(Pending.class);
                    listNewPending.add(projects);
                }
                pendingRecyclerAdapter = new pendingRecyclerAdapter(MonitorDefectActivity.this, listNewPending);
                pendingRecyclerView.setAdapter(pendingRecyclerAdapter);

                if (listNewPending.isEmpty()) {
                    pendingRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    pendingRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
