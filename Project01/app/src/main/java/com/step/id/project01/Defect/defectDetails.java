package com.step.id.project01.Defect;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.step.id.project01.R;
import com.step.id.project01.RecyclerView.DefImageAdapter;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.model.defect;
import com.step.id.project01.model.defectImageAddon;

import java.util.ArrayList;
import java.util.Calendar;

public class defectDetails extends AppCompatActivity{

    private TextView mDefectTextView, mCommentTextView, mDateTextView;

    // Upload image
    private RecyclerView mUploadList;
    private ArrayList<defectImageAddon> listNewDefect =new ArrayList<>();
    private DefImageAdapter defImageAdapter;

    // Update data
    private String selectedComment, selectedDate, selectedDefect;
    private int HideMenu;
    private String selectedID, selectedDefectID, selectedTitle;
    private boolean HIDE_MENU = false;

    //Fire base
    private DatabaseReference mDatabaseAddonRef,mDatabaseAddonImage;
    private FirebaseStorage mStorage;

    //Date Picker
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date = "Select a date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // init Id
        initId();

        // Update data
        initUpdate();

        mUploadList = (RecyclerView) findViewById(R.id.uploadDetailRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddonRef = FirebaseDatabase.getInstance().getReference("Defect Add On").child(selectedID).child(selectedDefectID);
        mDatabaseAddonImage = FirebaseDatabase.getInstance().getReference("Defect add on image").child(selectedDefectID);
        mStorage = FirebaseStorage.getInstance();

        onRetrieveAddon();
        onRetrieve();

        mUploadList.addOnItemTouchListener(new RecyclerTouchListener(defectDetails.this, mUploadList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


            }

            @Override
            public void onLongClick(View view, int position) {



                final String id = listNewDefect.get(position).getId();
                final String imageUri = listNewDefect.get(position).getImgURL();

                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference deleteDefectAddOn = FirebaseDatabase.getInstance().getReference("Defect add on image");

                                deleteDefectAddOn.child(selectedDefectID).child(id).removeValue();

                                StorageReference imageRef = mStorage.getReferenceFromUrl(imageUri);
                                imageRef.delete();

                                Toast.makeText(defectDetails.this, "Image is deleted", Toast.LENGTH_SHORT).show();
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
            }
        }));
    }

    public void initId() {

       mDateTextView = (TextView)findViewById(R.id.def_defectDate);
       mDefectTextView = (TextView)findViewById(R.id.def_defectTextView);
       mCommentTextView = (TextView)findViewById(R.id.def_commentTextView);

    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getStringExtra("pendingID");
        selectedTitle = receivedIntent.getStringExtra("Title");
        HideMenu = receivedIntent.getIntExtra("HideMenu", 0);

        if (HideMenu == 1) {
            selectedDefectID = receivedIntent.getStringExtra("defectAddOn");
        }

        // Hide save menu
        if (HideMenu == 1) {
            HIDE_MENU = true;
        }
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
                defImageAdapter = new DefImageAdapter(defectDetails.this, listNewDefect);
                mUploadList.setAdapter(defImageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onRetrieveAddon() {

        mDatabaseAddonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                defect defects = dataSnapshot.getValue(defect.class);
                selectedDefect = defects.getDefect();
                selectedComment = defects.getComments();
                selectedDate = defects.getDate();


                mDefectTextView.setText(selectedDefect);
                mCommentTextView.setText(selectedComment);
                mDateTextView.setText(selectedDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEditDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.def_imagedetail_dialog, null);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Editor");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView editTextViewDate = (TextView)dialogView.findViewById(R.id.def_detailsDate);
        final EditText editTextNewDefect = (EditText) dialogView.findViewById(R.id.def_detailsDefect);
        final EditText editTextNewComment = (EditText) dialogView.findViewById(R.id.def_detailsComment);
        final Button buttonChange = (Button) dialogView.findViewById(R.id.def_detailsConfirm);

        editTextViewDate.setText(selectedDate);
        editTextNewDefect.setText(selectedDefect);
        editTextNewComment.setText(selectedComment);

        editTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(defectDetails.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                editTextViewDate.setText(date);
            }
        };

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextNewDefect.getText().toString())) {
                    editTextNewDefect.setError("Please fill in the blank.");
                }else {
                    DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");

                    databaseNewProject.child(selectedID).child(selectedDefectID).child("comments").setValue(editTextNewComment.getText().toString());
                    databaseNewProject.child(selectedID).child(selectedDefectID).child("date").setValue(editTextViewDate.getText().toString());
                    databaseNewProject.child(selectedID).child(selectedDefectID).child("defect").setValue(editTextNewDefect.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(defectDetails.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detailslist, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditDialog();
                break;
            case android.R.id.home:
                Intent intent = new Intent(defectDetails.this, defectList.class);
                intent.putExtra("pendingID", selectedID);
                intent.putExtra("Title", selectedTitle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(defectDetails.this, defectList.class);
        intent.putExtra("pendingID", selectedID);
        intent.putExtra("Title", selectedTitle);
        startActivity(intent);
    }

}
