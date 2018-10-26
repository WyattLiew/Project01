package com.step.id.project01;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.step.id.project01.Project.projectList;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.RecyclerView.projectRecyclerAdapter;
import com.step.id.project01.sqlitedata.ProjectDbHelper;
import com.step.id.project01.sqlitedata.newProjectProvider;

import java.util.ArrayList;

public class tab2_project extends Fragment {

    private static final String TAG = "tab2_project";
    private static final String projectID = "projectID";

    private SQLiteDatabase sqLiteDatabase;
    private ProjectDbHelper projectDbHelper;
    private projectRecyclerAdapter projectRecyclerAdapter;
    private Cursor cursor;
    private View emptyView;
    private RecyclerView projectRecyclerView;
    private ArrayList<newProjectProvider> listNewProjectProviders = new ArrayList<>();

    //Firebasse
    DatabaseReference databaseNewProject, projectsRef;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_project, container, false);


        projectRecyclerView = (RecyclerView) rootView.findViewById(R.id.project_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectRecyclerView.setHasFixedSize(true);

        emptyView = rootView.findViewById(R.id.project_empty_view);
        databaseNewProject = FirebaseDatabase.getInstance().getReference();
        projectsRef = databaseNewProject.child("Projects").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        onRetrieve();

        projectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), projectRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String rowid = listNewProjectProviders.get(position).getId();
                Log.d(TAG, "The row id is: " + rowid);
                Intent intent = new Intent(getActivity().getApplicationContext(), projectList.class);
                intent.putExtra(projectID, listNewProjectProviders.get(position).getId());
                Log.d(TAG, "The tab 2 row id is: " + rowid);
                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {

                String rowid = listNewProjectProviders.get(position).getId();
                String title = listNewProjectProviders.get(position).getTitle();
                String description = listNewProjectProviders.get(position).getDescription();
                String conName = listNewProjectProviders.get(position).getName();
                String conNum = listNewProjectProviders.get(position).getNumber();
                String conEmail = listNewProjectProviders.get(position).getEmail();
                String location = listNewProjectProviders.get(position).getLocation();
                String date = listNewProjectProviders.get(position).getDate();
                String notes = listNewProjectProviders.get(position).getNotes();
                String clientID = listNewProjectProviders.get(position).getClientID();

                int HideMenu = 1;
                Log.d(TAG, "The row id is: " + rowid);

                Intent intent = new Intent(getActivity().getApplicationContext(), projectEditor.class);
                intent.putExtra(projectID, rowid);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("conName", conName);
                intent.putExtra("conNum", conNum);
                intent.putExtra("conEmail", conEmail);
                intent.putExtra("location", location);
                intent.putExtra("date", date);
                intent.putExtra("notes", notes);
                intent.putExtra("HideMenu", HideMenu);
                intent.putExtra("clientID", clientID);
                Log.d(TAG, "The row id is: " + rowid);
                startActivity(intent);
            }


        }));


        return rootView;
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
                projectRecyclerAdapter = new projectRecyclerAdapter(getActivity(), listNewProjectProviders);
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
