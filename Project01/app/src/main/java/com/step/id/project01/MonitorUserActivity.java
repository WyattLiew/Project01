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
import com.step.id.project01.RecyclerView.MonitorUserAdapter;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.firebase.User;

import java.util.ArrayList;

public class MonitorUserActivity extends AppCompatActivity {

    public static final String TAG = "MonitorUserActivity";
    private static final String userID = "userID";

    private RecyclerView recyclerView;
    private MonitorUserAdapter monitorUserAdapter;
    private View emptyView;
    private ArrayList<User> listUser = new ArrayList<>();

    //Fire base
    DatabaseReference databaseNewUser, UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_user);

        recyclerView = (RecyclerView) findViewById(R.id.monitorUser_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MonitorUserActivity.this));
        recyclerView.setHasFixedSize(true);

        databaseNewUser = FirebaseDatabase.getInstance().getReference();
        UserRef = databaseNewUser.child("Users");

        emptyView = findViewById(R.id.monitorUser_empty_view);

        onRetrieve();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MonitorUserActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                String rowid = listUser.get(position).getId();
                Log.d(TAG, "The row id is: " + rowid);
                Intent intent = new Intent(MonitorUserActivity.this, MonitorProjectActivity.class);
                intent.putExtra(userID,rowid);
                Log.d(TAG, "The tab 2 row id is: " + rowid);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    public void onRetrieve() {

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listUser.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User projects = dataSnapshot1.getValue(User.class);
                    listUser.add(projects);
                }
                monitorUserAdapter = new MonitorUserAdapter(MonitorUserActivity.this, listUser);
                recyclerView.setAdapter(monitorUserAdapter);

                if (listUser.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
