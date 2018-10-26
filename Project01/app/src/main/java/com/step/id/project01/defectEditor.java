package com.step.id.project01;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.step.id.project01.model.Pending;

import java.util.Calendar;

public class defectEditor extends AppCompatActivity {

    private EditText mProjectLocation, mContactName, mContactNumber, mContactEmail, mProjectTitle, mProjectDescription, mProjectNotes;

    // update data
    private String selectedLocation, selectednotes, selectedName, selectedNum, selectedEmail, selectedprojDate, selectedTitle, selectedDescription;
    private int HideMenu;
    private String selectedID;
    private boolean HIDE_MENU = false;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String date = "Select a date";

    //Firebase
    DatabaseReference databaseNewDefect;

    //Warn the user about unsaved changes
    private boolean mPendingHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPendingHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_editor);

        initId();
        initDate();

        // Update data
        initUpdate();

        //Check unsaved changes
        initCheckUnsavedChanges();

        databaseNewDefect = FirebaseDatabase.getInstance().getReference("Pending");
    }
    public void initId() {
        mDisplayDate = (TextView) findViewById(R.id.def_Date);
        mProjectTitle = (EditText) findViewById(R.id.def_ReferenceName);
        mProjectDescription = (EditText) findViewById(R.id.def_Description);
        mContactName = (EditText) findViewById(R.id.def_conName);
        mContactNumber = (EditText) findViewById(R.id.def_conNum);
        mContactEmail = (EditText) findViewById(R.id.def_conEmail);
        mProjectLocation = (EditText) findViewById(R.id.def_Location);
        mProjectNotes = (EditText) findViewById(R.id.def_Notes);

    }

    private void initDate() {
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(defectEditor.this,
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
                mDisplayDate.setText(date);
            }
        };
    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedLocation = receivedIntent.getStringExtra("location");
        selectedName = receivedIntent.getStringExtra("conName");
        selectedNum = receivedIntent.getStringExtra("conNum");
        selectedEmail = receivedIntent.getStringExtra("conEmail");
        selectedTitle = receivedIntent.getStringExtra("Title");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectednotes = receivedIntent.getStringExtra("notes");
        HideMenu = receivedIntent.getIntExtra("HideMenu", 0);

        if (HideMenu == 1) {
            selectedprojDate = receivedIntent.getStringExtra("date");
            mDisplayDate.setText(selectedprojDate);
            selectedID = receivedIntent.getStringExtra("pendingID");
        }


        mProjectLocation.setText(selectedLocation);
        mContactName.setText(selectedName);
        mContactNumber.setText(selectedNum);
        mProjectTitle.setText(selectedTitle);
        mProjectDescription.setText(selectedDescription);
        mProjectNotes.setText(selectednotes);
        mContactEmail.setText(selectedEmail);


        // Hide save menu
        if (HideMenu == 1) {
            HIDE_MENU = true;
        }
    }

    private boolean updateProject(String id, String titleString, String descriptionString, String conNameString, String conNumString,String conEmailString, String projectDate, String locationString, String noteString) {
        DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Pending");
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Pending pending = new Pending(id, titleString, descriptionString, conNameString, conNumString,conEmailString, projectDate, locationString, noteString);
        databaseNewProject.child(UID).child(id).setValue(pending);

        Toast.makeText(this, "Pending Updated Successfully", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void deleteProject(String selectedID) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference deleteProject = FirebaseDatabase.getInstance().getReference("Pending");
        DatabaseReference deleteProjectAddOn = FirebaseDatabase.getInstance().getReference("Defect Add On");

        deleteProject.child(UID).child(selectedID).removeValue();
        deleteProjectAddOn.child(selectedID).removeValue();

        Toast.makeText(this, "Pending is deleted", Toast.LENGTH_SHORT).show();
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


    public void initCheckUnsavedChanges() {
        mProjectLocation.setOnTouchListener(mTouchListener);
        mContactName.setOnTouchListener(mTouchListener);
        mContactNumber.setOnTouchListener(mTouchListener);
        mProjectNotes.setOnTouchListener(mTouchListener);
        mProjectDescription.setOnTouchListener(mTouchListener);
        mProjectTitle.setOnTouchListener(mTouchListener);
        mDisplayDate.setOnTouchListener(mTouchListener);
        mContactEmail.setOnTouchListener(mTouchListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    //check empty text
    private void checkEmptyEditText(String locationString, String conNameString, String conNumInt, String projTitle) {

        if (TextUtils.isEmpty(locationString)) {
            mProjectLocation.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(conNameString)) {
            mContactName.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(conNumInt)) {
            mContactNumber.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(projTitle)) {
            mProjectTitle.setError("Please fill in the blank.");
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mPendingHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_def_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.def_action_update).setVisible(true);
                menu.findItem(R.id.def_action_save).setVisible(false);
                menu.findItem(R.id.def_action_delete).setVisible(true);
            } else {
                menu.findItem(R.id.def_action_update).setVisible(false);
                menu.findItem(R.id.def_action_save).setVisible(true);
                menu.findItem(R.id.def_action_delete).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.def_action_save:
                final CharSequence[] items_add = {"Save", "Cancel"};
                AlertDialog.Builder builder_add = new AlertDialog.Builder(defectEditor.this);
                builder_add.setTitle("Select options");
                builder_add.setItems(items_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_add[which].equals("Save")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            // int conNumInt = Integer.parseInt(conNumString);
                            String conEmailString = mContactEmail.getText().toString().trim();
                            String descriptionString = mProjectDescription.getText().toString().trim();
                            String titleString = mProjectTitle.getText().toString().trim();
                            String noteString = mProjectNotes.getText().toString().trim();
                            String projectDate = mDisplayDate.getText().toString().trim();
                            if (locationString.length() == 0 || conNameString.length() == 0 || conNumString.length() == 0 || titleString.length() == 0 || projectDate.length() == 0) {
                                checkEmptyEditText(locationString, conNameString, conNumString, titleString);
                            } else if (projectDate.matches(date)) {
                                Toast.makeText(defectEditor.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                            } else if (conEmailString.isEmpty()) {
                                mContactEmail.setError("Email is required");
                                mContactEmail.requestFocus();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(conEmailString).matches()) {
                                mContactEmail.setError("Please enter a valid email");
                                mContactEmail.requestFocus();
                            } else {
                                String id = databaseNewDefect.push().getKey();
                                String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                Pending pending = new Pending(id, titleString, descriptionString, conNameString, conNumString,conEmailString, projectDate, locationString, noteString);
                                databaseNewDefect.child(UID).child(id).setValue(pending).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Pending added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                // mDbHelper.insert_project(locationString, conNameString, conNumString, projectDate, descriptionString, titleString, noteString);
                                Intent intent = new Intent(defectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else if (items_add[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_add.show();
                return true;
            case R.id.def_action_update:
                final CharSequence[] items_update = {"Update", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(defectEditor.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Update")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            //int conNumInt = Integer.parseInt(conNumString);
                            String conEmailString = mContactEmail.getText().toString().trim();
                            String descriptionString = mProjectDescription.getText().toString().trim();
                            String titleString = mProjectTitle.getText().toString().trim();
                            String noteString = mProjectNotes.getText().toString().trim();
                            String projectDate = mDisplayDate.getText().toString().trim();
                            if (locationString.length() == 0 || conNameString.length() == 0 || conNumString.length() == 0 || titleString.length() == 0 || projectDate.length() == 0) {
                                checkEmptyEditText(locationString, conNameString, conNumString, titleString);
                            } else if (projectDate.matches(date)) {
                                Toast.makeText(defectEditor.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                            } else if (conEmailString.isEmpty()) {
                                mContactEmail.setError("Email is required");
                                mContactEmail.requestFocus();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(conEmailString).matches()) {
                                mContactEmail.setError("Please enter a valid email");
                                mContactEmail.requestFocus();
                            } else {

                                updateProject(selectedID,titleString, descriptionString, conNameString, conNumString,conEmailString, projectDate, locationString, noteString);
                                Intent intent = new Intent(defectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else if (items_update[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_update.show();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.def_action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProject(selectedID);
                                Intent intent = new Intent(defectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;

            case android.R.id.home:
                if (!mPendingHasChanged) {
                    NavUtils.navigateUpFromSameTask(defectEditor.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(defectEditor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
