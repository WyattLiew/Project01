package com.step.id.project01.Project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.step.id.project01.Image.BitmapUtils;
import com.step.id.project01.R;
import com.step.id.project01.RecyclerView.ProjImageAdapter;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.model.ProjectAddOnProvider;
import com.step.id.project01.model.projectImageAddon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class projectDetails extends AppCompatActivity {

    private TextView mStatusTextView, mNotesTextView, mDateTextView;

    private String mProjectStatus = "Completed";
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;

    // Upload image
    private RecyclerView mUploadList;
    private ArrayList<projectImageAddon> listNewProject = new ArrayList<>();
    private ProjImageAdapter projImageAdapter;

    // Update data
    private String selectedNotes, selectedDate, selectedStatus, selectedImage;
    private int HideMenu;
    private String selectedID, selectedProjectID, selectedTitle;
    private boolean HIDE_MENU = false;

    //Fire base
    private DatabaseReference mDatabaseAddonRef, mDatabaseAddonImages, mDatabaseAddonImage;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorage;

    //Date Picker
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date = "Select a date";

    //Camera
    ImageView projectAddOnImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath;
    private Uri selectImageUrl;
    private Bitmap mResultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        // init Id
        initId();

        // Update data
        initUpdate();

        mUploadList = (RecyclerView) findViewById(R.id.projectDetailsUploadRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddonRef = FirebaseDatabase.getInstance().getReference("Projects Add On").child(selectedID).child(selectedProjectID);
        mDatabaseAddonImages = FirebaseDatabase.getInstance().getReference("Project add on image");
        mDatabaseAddonImage = FirebaseDatabase.getInstance().getReference("Project add on image").child(selectedProjectID);
        mStorageReference = FirebaseStorage.getInstance().getReference("Projects Add On").child(selectedTitle);
        mStorage = FirebaseStorage.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.projectDetailsAddOn_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        onRetrieveAddon();
        onRetrieve();

        mUploadList.addOnItemTouchListener(new RecyclerTouchListener(projectDetails.this, mUploadList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


            }

            @Override
            public void onLongClick(View view, int position) {


                final String id = listNewProject.get(position).getId();
                final String imageUri = listNewProject.get(position).getImgURL();

                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference deleteDefectAddOn = FirebaseDatabase.getInstance().getReference("Project add on image");

                                deleteDefectAddOn.child(selectedProjectID).child(id).removeValue();

                                StorageReference imageRef = mStorage.getReferenceFromUrl(imageUri);
                                imageRef.delete();

                                Toast.makeText(projectDetails.this, "Image is deleted", Toast.LENGTH_SHORT).show();
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
            }
        }));
    }

    public void initId() {

        mDateTextView = (TextView) findViewById(R.id.proj_dateTextView);
        mStatusTextView = (TextView) findViewById(R.id.proj_statusTextView);
        mNotesTextView = (TextView) findViewById(R.id.proj_notesTextView);
        projectAddOnImage = (ImageView) findViewById(R.id.proj_backupImg);
    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getStringExtra("projectID");
        selectedTitle = receivedIntent.getStringExtra("title");
        HideMenu = receivedIntent.getIntExtra("HideMenu", 0);

        if (HideMenu == 1) {
            selectedProjectID = receivedIntent.getStringExtra("projectAddOn");
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

                listNewProject.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    projectImageAddon projectImage = dataSnapshot1.getValue(projectImageAddon.class);
                    listNewProject.add(projectImage);
                }
                projImageAdapter = new ProjImageAdapter(projectDetails.this, listNewProject);
                mUploadList.setAdapter(projImageAdapter);

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

                ProjectAddOnProvider projects = dataSnapshot.getValue(ProjectAddOnProvider.class);
                selectedStatus = projects != null ? projects.getStatus() : null;
                selectedNotes = projects != null ? projects.getNotes() : null;
                selectedDate = projects != null ? projects.getDate() : null;
                selectedImage = projects != null ? projects.getImgURL() : null;


                mStatusTextView.setText(selectedStatus);
                mNotesTextView.setText(selectedNotes);
                mDateTextView.setText(selectedDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void ShowProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progressbar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog() {

        b.dismiss();
    }

    private void deleteProjectAddOn(String id) {
        DatabaseReference deleteProjectAddOn = FirebaseDatabase.getInstance().getReference("Projects Add On");

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedImage);
        imageRef.delete();

        deleteProjectAddOn.child(selectedID).child(id).removeValue();

        DatabaseReference deleteDefectAddOnImage = FirebaseDatabase.getInstance().getReference("Project add on image");
        deleteDefectAddOnImage.child(id).removeValue();

        Toast.makeText(this, "Progress is deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * intent Camera code Start
     **/
    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(projectDetails.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.step.id.project01.provider", photoFile);

                            List<ResolveInfo> resolvedIntentActivities = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                                String packageName = resolvedIntentInfo.activityInfo.packageName;
                                getApplicationContext().grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }

                } else if (items[which].equals("Gallery")) {
                    //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Intent intent = new Intent();
                    intent.setType("image/*");
                    //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "Select picture"), SELECT_FILE);
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                ShowProgressDialog();
                projectImageAddon s;

                s = new projectImageAddon();
                projectAddOnImage.setImageURI(Uri.parse(imageFilePath));
                projectAddOnImage.setMinimumHeight(512);

                selectImageUrl = Uri.fromFile(new File(imageFilePath));

                mResultBitmap = ((BitmapDrawable) projectAddOnImage.getDrawable()).getBitmap();

                // Save the image
                BitmapUtils.saveImage(projectDetails.this, mResultBitmap);

                s.setImgURL(Uri.fromFile(new File(imageFilePath)).toString());
                listNewProject.add(s);
                mUploadList.setAdapter(new ProjImageAdapter(this, listNewProject));

                StorageReference fileReferences = mStorageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(selectImageUrl));

                fileReferences.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        String imageid = mDatabaseAddonImages.push().getKey();
                        projectImageAddon project = new projectImageAddon(imageid, downloadUri.toString());
                        mDatabaseAddonImages.child(selectedProjectID).child(imageid).setValue(project);

                        DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Projects Add On");
                        databaseNewProject.child(selectedID).child(selectedProjectID).child("imgURL").setValue(downloadUri.toString());
                        HideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else if (requestCode == SELECT_FILE) {
                ShowProgressDialog();
                projectImageAddon s;

                s = new projectImageAddon();
                selectImageUrl = data.getData();

                s.setImgURL(selectImageUrl.toString());
                listNewProject.add(s);
                mUploadList.setAdapter(new ProjImageAdapter(this, listNewProject));

                StorageReference fileReferences = mStorageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(selectImageUrl));

                fileReferences.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        String imageid = mDatabaseAddonImages.push().getKey();
                        projectImageAddon project = new projectImageAddon(imageid, downloadUri.toString());
                        mDatabaseAddonImages.child(selectedProjectID).child(imageid).setValue(project);

                        DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Projects Add On");
                        databaseNewProject.child(selectedID).child(selectedProjectID).child("imgURL").setValue(downloadUri.toString());
                        HideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
                ;
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * intent Camera code end
     **/

    private void showEditDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.proj_imagedetail_dialog, null);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Editor");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView editTextViewDate = (TextView) dialogView.findViewById(R.id.proj_detailsDate);
        final Spinner mProjectStatusSpinner = (Spinner)dialogView.findViewById(R.id.spinner_detailsStatus);
        final EditText editTextNewComment = (EditText) dialogView.findViewById(R.id.proj_detailsComment);
        final Button buttonChange = (Button) dialogView.findViewById(R.id.proj_detailsConfirm);

        editTextViewDate.setText(selectedDate);
        editTextNewComment.setText(selectedNotes);

        setupSpinner(mProjectStatusSpinner);

        editTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(projectDetails.this,
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
                DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Projects Add On");

                databaseNewProject.child(selectedID).child(selectedProjectID).child("notes").setValue(editTextNewComment.getText().toString());
                databaseNewProject.child(selectedID).child(selectedProjectID).child("date").setValue(editTextViewDate.getText().toString());
                databaseNewProject.child(selectedID).child(selectedProjectID).child("status").setValue(mProjectStatus).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(projectDetails.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }
                        });
            }
        });
    }
    private void setupSpinner(Spinner mProjectStatusSpinner) {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter projectTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_detailStatus_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        projectTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mProjectStatusSpinner.setAdapter(projectTypeSpinnerAdapter);

        if (selectedStatus.equals("Completed")) {
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("Completed");
            mProjectStatusSpinner.setSelection(selectionPosition);
        } else if (selectedStatus.equals("In Progress")) {
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("In Progress");
            mProjectStatusSpinner.setSelection(selectionPosition);
        } else {
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("Deferred");
            mProjectStatusSpinner.setSelection(selectionPosition);
        }

        // Set the integer mSelected to the constant values
        mProjectStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Completed")) {
                        mProjectStatus = "Completed";
                    } else if (selection.equals("In Progress")) {
                        mProjectStatus = "In Progress";
                    } else if (selection.equals("Deferred")) {
                        mProjectStatus = "Deferred";
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProjectStatus = "Completed";
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_projdetailslist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditDialog();
                break;
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProjectAddOn(selectedProjectID);
                                Intent intent = new Intent(projectDetails.this, projectList.class);
                                intent.putExtra("projectID", selectedID);
                                intent.putExtra("title", selectedTitle);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(projectDetails.this, projectList.class);
                intent.putExtra("projectID", selectedID);
                intent.putExtra("title", selectedTitle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(projectDetails.this, projectList.class);
        intent.putExtra("projectID", selectedID);
        intent.putExtra("title", selectedTitle);
        startActivity(intent);
    }
}

