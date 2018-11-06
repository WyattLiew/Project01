package com.step.id.project01.Defect;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.step.id.project01.RecyclerView.DefImageAdapter;
import com.step.id.project01.RecyclerView.RecyclerTouchListener;
import com.step.id.project01.model.defect;
import com.step.id.project01.model.defectImageAddon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class defectDetails extends AppCompatActivity {

    private TextView mDefectTextView, mCommentTextView, mDateTextView;

    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;

    // Upload image
    private RecyclerView mUploadList;
    private ArrayList<defectImageAddon> listNewDefect = new ArrayList<>();
    private DefImageAdapter defImageAdapter;

    // Update data
    private String selectedComment, selectedDate, selectedDefect, selectedImage;
    private int HideMenu;
    private String selectedID, selectedDefectID, selectedTitle;
    private boolean HIDE_MENU = false;

    //Fire base
    private DatabaseReference mDatabaseAddonRef, mDatabaseAddonImages, mDatabaseAddonImage;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorage;

    //Date Picker
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date = "Select a date";

    //Camera
    ImageView projectImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath;
    private Uri selectImageUrl;
    private Bitmap mResultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_details);

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

        mUploadList = (RecyclerView) findViewById(R.id.uploadDetailRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddonRef = FirebaseDatabase.getInstance().getReference("Defect Add On").child(selectedID).child(selectedDefectID);
        mDatabaseAddonImages = FirebaseDatabase.getInstance().getReference("Defect add on image");
        mDatabaseAddonImage = FirebaseDatabase.getInstance().getReference("Defect add on image").child(selectedDefectID);
        mStorageReference = FirebaseStorage.getInstance().getReference("Defect add on").child(selectedTitle);
        mStorage = FirebaseStorage.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.detailsFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

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

        mDateTextView = (TextView) findViewById(R.id.def_defectDate);
        mDefectTextView = (TextView) findViewById(R.id.def_defectTextView);
        mCommentTextView = (TextView) findViewById(R.id.def_commentTextView);
        projectImage = (ImageView) findViewById(R.id.def_backupImg);
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
                selectedDefect = defects != null ? defects.getDefect() : null;
                selectedComment = defects != null ? defects.getComments() : null;
                selectedDate = defects != null ? defects.getDate() : null;
                selectedImage = defects != null ? defects.getImgURL() : null;


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

    private void showEditDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.def_imagedetail_dialog, null);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Editor");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView editTextViewDate = (TextView) dialogView.findViewById(R.id.def_detailsDate);
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
                } else {
                    DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");

                    databaseNewProject.child(selectedID).child(selectedDefectID).child("comments").setValue(editTextNewComment.getText().toString());
                    databaseNewProject.child(selectedID).child(selectedDefectID).child("date").setValue(editTextViewDate.getText().toString());
                    databaseNewProject.child(selectedID).child(selectedDefectID).child("defect").setValue(editTextNewDefect.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(defectDetails.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
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
        DatabaseReference deleteDefectAddOn = FirebaseDatabase.getInstance().getReference("Defect Add On");
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedImage);
        imageRef.delete();
        deleteDefectAddOn.child(selectedID).child(id).removeValue();
        DatabaseReference deleteDefectAddOnImage = FirebaseDatabase.getInstance().getReference("Defect add on image");
        deleteDefectAddOnImage.child(id).removeValue();

        Toast.makeText(this, "Defect is deleted", Toast.LENGTH_SHORT).show();
    }

    //Camera part start
    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(defectDetails.this);
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

                defectImageAddon s;

                s = new defectImageAddon();

                projectImage.setImageURI(Uri.parse(imageFilePath));
                projectImage.setMinimumHeight(512);

                selectImageUrl = Uri.fromFile(new File(imageFilePath));
                mResultBitmap = ((BitmapDrawable) projectImage.getDrawable()).getBitmap();

                BitmapUtils.saveImage(defectDetails.this, mResultBitmap);

                s.setImgURL(Uri.fromFile(new File(imageFilePath)).toString());
                listNewDefect.add(s);
                mUploadList.setAdapter(new DefImageAdapter(this, listNewDefect));

                StorageReference fileReferences = mStorageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(selectImageUrl));

                fileReferences.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        String imageid = mDatabaseAddonImages.push().getKey();
                        defectImageAddon defect = new defectImageAddon(imageid, downloadUri.toString());
                        mDatabaseAddonImages.child(selectedDefectID).child(imageid).setValue(defect);

                        DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");
                        databaseNewProject.child(selectedID).child(selectedDefectID).child("imgURL").setValue(downloadUri.toString());
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

                defectImageAddon s;

                selectImageUrl = data.getData();
                s = new defectImageAddon();
                s.setImgURL(selectImageUrl.toString());
                listNewDefect.add(s);
                mUploadList.setAdapter(new DefImageAdapter(this, listNewDefect));

                StorageReference fileReferences = mStorageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(selectImageUrl));

                fileReferences.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        String imageid = mDatabaseAddonImages.push().getKey();
                        defectImageAddon defect = new defectImageAddon(imageid, downloadUri.toString());
                        mDatabaseAddonImages.child(selectedDefectID).child(imageid).setValue(defect);

                        DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");
                        databaseNewProject.child(selectedID).child(selectedDefectID).child("imgURL").setValue(downloadUri.toString());
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
     * Camera part end
     **/


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
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProjectAddOn(selectedDefectID);
                                Intent intent = new Intent(defectDetails.this, defectList.class);
                                intent.putExtra("pendingID", selectedID);
                                intent.putExtra("Title", selectedTitle);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;
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
