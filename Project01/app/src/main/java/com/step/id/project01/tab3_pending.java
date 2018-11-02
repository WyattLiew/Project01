package com.step.id.project01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.step.id.project01.Defect.defectList;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.RecyclerView.pendingRecyclerAdapter;
import com.step.id.project01.model.Pending;

import java.util.ArrayList;


public class tab3_pending extends Fragment {

    private static final String TAG = "tab3_pending";
    private static final String pendingID = "pendingID";

    private FloatingActionButton fab;

    private pendingRecyclerAdapter pendingRecyclerAdapter;
    private View emptyView;
    private RecyclerView pendingRecyclerView;
    private ArrayList<Pending> listNewPending = new ArrayList<>();

    //Firebasse
    DatabaseReference databaseNewPending, pendingRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab3_pending, container, false);

        pendingRecyclerView = (RecyclerView) rootView.findViewById(R.id.pending_recyclerView);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pendingRecyclerView.setHasFixedSize(true);

        emptyView = rootView.findViewById(R.id.pending_empty_view);
        databaseNewPending = FirebaseDatabase.getInstance().getReference();
        pendingRef = databaseNewPending.child("Pending").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        fab = (FloatingActionButton) rootView.findViewById(R.id.pendingCard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), defectEditor.class);
                startActivity(intent);
            }
        });

        onRetrieve();

        pendingRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), pendingRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String rowid = listNewPending.get(position).getId();
                Log.d(TAG, "The row id is: " + rowid);
                Intent intent = new Intent(getActivity().getApplicationContext(), defectList.class);
                intent.putExtra(pendingID, listNewPending.get(position).getId());
                intent.putExtra("Title",listNewPending.get(position).getTitle());
                Log.d(TAG, "The tab 2 row id is: " + rowid);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

                String rowid = listNewPending.get(position).getId();
                String title = listNewPending.get(position).getTitle();
                String description = listNewPending.get(position).getDescription();
                String conName = listNewPending.get(position).getName();
                String conNum = listNewPending.get(position).getNumber();
                String conEmail = listNewPending.get(position).getEmail();
                String location = listNewPending.get(position).getLocation();
                String date = listNewPending.get(position).getDate();
                String notes = listNewPending.get(position).getNotes();

                int HideMenu = 1;
                Log.d(TAG, "The row id is: " + rowid);

                Intent intent = new Intent(getActivity().getApplicationContext(), defectEditor.class);
                intent.putExtra(pendingID, rowid);
                intent.putExtra("Title", title);
                intent.putExtra("description", description);
                intent.putExtra("conName", conName);
                intent.putExtra("conNum", conNum);
                intent.putExtra("conEmail", conEmail);
                intent.putExtra("location", location);
                intent.putExtra("date", date);
                intent.putExtra("notes", notes);
                intent.putExtra("HideMenu", HideMenu);
                Log.d(TAG, "The row id is: " + rowid);
                startActivity(intent);
            }

        }));

        return rootView;
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
                pendingRecyclerAdapter = new pendingRecyclerAdapter(getActivity(), listNewPending);
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


