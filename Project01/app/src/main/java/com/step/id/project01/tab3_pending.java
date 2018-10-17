package com.step.id.project01;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.step.id.project01.sqlitedata.DataProvider;
import com.step.id.project01.sqlitedata.ProjectDbHelper;

import java.io.ByteArrayOutputStream;


public class tab3_pending extends Fragment {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    ProjectDbHelper projectDbHelper;
    Cursor cursor;
    ListAdapter listAdapter;
    private static final String TAG = "tab3_pending";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab3_pending, container, false);

        listView = (ListView) rootView.findViewById(R.id.pending_list);

        // Only display without data
        View emptyView = rootView.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        projectDbHelper = new ProjectDbHelper(getActivity().getApplicationContext());
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.viewData();
        listAdapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.item_project);
        listAdapter.notifyDataSetChanged();
        listView.setAdapter(listAdapter);
        listView.invalidateViews();


        // Cursor data
        initData();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rowID = ((TextView) view.findViewById(R.id.project_id)).getText().toString();
                String pos = ((TextView) view.findViewById(R.id.project_location)).getText().toString();
                String conName = ((TextView) view.findViewById(R.id.project_person)).getText().toString();
                String conNum = ((TextView) view.findViewById(R.id.project_number)).getText().toString();
                String projManager = ((TextView) view.findViewById(R.id.project_manager)).getText().toString();
                String projectDate = ((TextView) view.findViewById(R.id.project_date)).getText().toString();
                String defect1 = ((TextView) view.findViewById(R.id.project_defect_1)).getText().toString();
                String defect2 = ((TextView) view.findViewById(R.id.project_defect_2)).getText().toString();
                String defect3 = ((TextView) view.findViewById(R.id.project_defect_3)).getText().toString();
                String comments = ((TextView) view.findViewById(R.id.project_comment)).getText().toString();
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.id.project_image);

                ImageView projImage = (ImageView) view.findViewById(R.id.project_image);
                projImage.setDrawingCacheEnabled(true);
                projImage.buildDrawingCache();
                final Bitmap bitmap = projImage.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                int HideMenu = 1;

                Cursor data = projectDbHelper.getItemID(rowID); //get the id associated with that name
                Toast.makeText(getActivity(),"This row id is: "+rowID,Toast.LENGTH_SHORT).show();

                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(getActivity(), defectEditor.class);
                    editScreenIntent.putExtra("id", +itemID);
                    editScreenIntent.putExtra("location", pos);
                    editScreenIntent.putExtra("conName", conName);
                    editScreenIntent.putExtra("conNum", conNum);
                    editScreenIntent.putExtra("projManager", projManager);
                    editScreenIntent.putExtra("projDate", projectDate);
                    editScreenIntent.putExtra("defect1", defect1);
                    editScreenIntent.putExtra("defect2", defect2);
                    editScreenIntent.putExtra("defect3", defect3);
                    editScreenIntent.putExtra("comments", comments);
                    editScreenIntent.putExtra("projImage", bytes);
                    editScreenIntent.putExtra("HideMenu", HideMenu);
                    startActivity(editScreenIntent);
                }

            }
        });

        return rootView;

    }

    public void initData(){
        if (cursor.moveToFirst()) {

            do {
                String location, name, number, projManager, projectDate, defect1, defect2, defect3, comments;
                String rowId;
                byte[] img;
                rowId = cursor.getString(0);
                name = cursor.getString(1);
                location = cursor.getString(2);
                number = cursor.getString(3);
                projManager = cursor.getString(4);
                projectDate = cursor.getString(5);
                defect1 = cursor.getString(6);
                defect2 = cursor.getString(7);
                defect3 = cursor.getString(8);
                comments = cursor.getString(9);
                img = cursor.getBlob(10);
                DataProvider dataProvider = new DataProvider(rowId, location, name, number, projManager, projectDate, defect1, defect2, defect3, comments, img);
                listAdapter.add(dataProvider);
                listAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                Log.d(TAG, "The row id is :   " + rowId);
            }
            while (cursor.moveToNext());
            listAdapter.notifyDataSetChanged();
            listView.invalidateViews();

        }

    }

}


