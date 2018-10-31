package com.step.id.project01.Defect;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.step.id.project01.Image.BitmapUtils;
import com.step.id.project01.R;
import com.step.id.project01.model.defect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class defectAddOn extends AppCompatActivity {

    private static final String TAG = "defectAddOn";

    private EditText mDefect1,mPendingComment;
    private TextView mProjectDate;
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;




    //Camera
    ImageView projectImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath ;
    private Uri selectImageUrl;
    private Bitmap mResultBitmap;

    // report string
    String reportMessage;

    // For attach image to email
    File pic;

    // Date
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date ="Select a date";

    // Update data
    private String selectedComments,selectedprojDate,selectedDefect1;
    private int HideMenu;
    private String selectedID, selectedDefectID,selectedTitle;
    private String selectedImage;
    private boolean HIDE_MENU =false;

    //Fire base
    private DatabaseReference mDatabaseAddon;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorage;

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
        setContentView(R.layout.activity_defect_add_on);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // init Id
        initId();

        //init Date
        initDate();

        // Update data
        initUpdate();


        mDatabaseAddon = FirebaseDatabase.getInstance().getReference("Defect Add On").child(selectedID);
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("Defect add on").child(selectedTitle);

        //Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        //Check unsaved changes
        initCheckUnsavedChanges();


    }

    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(defectAddOn.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try{
                            photoFile = createImageFile();
                        }
                        catch (IOException e){
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
                    //Intent intent = new Intent();
                    intent.setType("image/*");
                    //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent,"Select picture"),SELECT_FILE);
                    //startActivityForResult(intent, SELECT_FILE);

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



                projectImage.setImageURI(Uri.parse(imageFilePath));
                projectImage.setMinimumHeight(512);
                Log.e("Attachment Path:", imageFilePath);

                selectImageUrl = Uri.fromFile(new File(imageFilePath));
                mResultBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();


                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        pic = new File(root, "pic.png");
                        FileOutputStream out = new FileOutputStream(pic);
                        mResultBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                }

            } else if (requestCode == SELECT_FILE) {


                selectImageUrl = data.getData();
                projectImage.setImageURI(selectImageUrl);
                Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();

                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        pic = new File(root, "pic.png");
                        FileOutputStream out = new FileOutputStream(pic);
                        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                }

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

        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0 ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Thanks for granting Permission", Toast.LENGTH_SHORT).show();;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.action_update).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(true);
            }else {
                menu.findItem(R.id.action_update).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cR.getType(uri));
    }

    public void initId(){

        mDefect1 =(EditText) findViewById(R.id.defect_1);
        mPendingComment =(EditText) findViewById(R.id.defect_comment);
        mProjectDate = (TextView) findViewById(R.id.defect_date);
       projectImage = (ImageView) findViewById(R.id.defect_img);
    }

    public void ShowProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progressbar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog(){

        b.dismiss();
    }

    private void initDate(){
        mProjectDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(defectAddOn.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year ;
                mProjectDate.setText(date);
            }
        };
    }

    @SuppressLint("StringFormatInvalid")
    private String createReportSummary(String date, String defect1, String comments){

        String reportMessage = "Hi ";
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_date,date);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_description);
        reportMessage += "\n" + getString(R.string.report_summary_defect_1,defect1);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_comments,comments);
        reportMessage += "\n" +"\n" + getString(R.string.report_summary_Thank_you);

        return reportMessage;
    }

    public void initCheckUnsavedChanges(){
        mDefect1.setOnTouchListener(mTouchListener);
        mPendingComment.setOnTouchListener(mTouchListener);
        mProjectDate.setOnTouchListener(mTouchListener);
        projectImage.setOnTouchListener(mTouchListener);

    }

    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete,  deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,  discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkEmptyEditText(String defect){

        if(TextUtils.isEmpty(defect)){
            mDefect1.setError("Please fill in the blank.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Create dialog to send email / store data
                final CharSequence[] items = {"Save and email", "Save", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(defectAddOn.this);
                builder.setTitle("Select options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Save and email")) {
                            ShowProgressDialog();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                          //  Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();

                            if (defect1String.length() ==0 || projectDate.length() ==0 ) {
                                checkEmptyEditText(defect1String);
                            }else if(projectDate.matches(date)){
                                Toast.makeText(defectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            } else {
                                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                                        + "." + getFileExtension(selectImageUrl));

                                BitmapUtils.saveImage(defectAddOn.this,mResultBitmap);

                                fileReference.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        HideProgressDialog();
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful()) ;
                                        Uri downloadUri = uriTask.getResult();
                                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                        String id = mDatabaseAddon.push().getKey();
                                        defect defect = new defect(id, downloadUri.toString(),mDefect1.getText().toString(),mProjectDate.getText().toString(),mPendingComment.getText().toString());
                                        mDatabaseAddon.child(id).setValue(defect);

                                        finish();
                                       // Intent intent = new Intent(defectAddOn.this, defectList.class);
                                        //intent.putExtra("pendingID", selectedID);
                                        //intent.putExtra("Title",selectedTitle);
                                        //startActivity(intent);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               HideProgressDialog();
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                // double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                            }
                                        });
                            }
                            reportMessage = createReportSummary(projectDate, defect1String , penCommentString);
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:"));
                            //emailIntent.setType("image/*");
                            //Uri imageUri = Uri.parse("Path:: " + imageFilePath);
                            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reports");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, reportMessage);
                            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(emailIntent);
                            }

                        } else if (items[which].equals("Save")) {
                            ShowProgressDialog();
                            final String projectDate = mProjectDate.getText().toString().trim();
                            final String defect1String = mDefect1.getText().toString().trim();
                            final String penCommentString = mPendingComment.getText().toString().trim();

                            if (selectImageUrl == null) {
                                HideProgressDialog();
                                Toast.makeText(defectAddOn.this, "Image cannot be null.", Toast.LENGTH_SHORT).show();
                            } else if (defect1String.length() ==0 || projectDate.length() ==0 ) {
                                HideProgressDialog();
                                checkEmptyEditText(defect1String);
                           }else if(projectDate.matches(date)){
                                HideProgressDialog();
                               Toast.makeText(defectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            }
                           else{
                                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                                        + "." + getFileExtension(selectImageUrl));

                                BitmapUtils.saveImage(defectAddOn.this,mResultBitmap);

                                fileReference.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        HideProgressDialog();
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful()) ;
                                        Uri downloadUri = uriTask.getResult();
                                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                        String id = mDatabaseAddon.push().getKey();
                                        defect defect = new defect(id, downloadUri.toString(),defect1String,projectDate,penCommentString);
                                        mDatabaseAddon.child(id).setValue(defect);
                                        Intent intent = new Intent(defectAddOn.this, defectList.class);
                                        intent.putExtra("pendingID", selectedID);
                                        intent.putExtra("Title",selectedTitle);
                                        startActivity(intent);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               HideProgressDialog();
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                // double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                            }
                                        });
                           }


                        } else if (items[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;

            case R.id.action_update:
                //Create dialog to send email / store data
                final CharSequence[] items_update = {"Update and email", "Update", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(defectAddOn.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Update and email")) {
                            ShowProgressDialog();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();

                            if (defect1String.length() ==0 || projectDate.length() ==0 ) {
                                checkEmptyEditText(defect1String);
                            }else if(imgBitmap == null){
                                Toast.makeText(defectAddOn.this,"Image cannot be null.",Toast.LENGTH_SHORT).show();
                            }else if(projectDate.matches(date)){
                                Toast.makeText(defectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                try {
                                    File root = Environment.getExternalStorageDirectory();
                                    if (root.canWrite()) {
                                        pic = new File(root, "pic.png");
                                        FileOutputStream out = new FileOutputStream(pic);
                                        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                        out.flush();
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                                }

                                reportMessage = createReportSummary(projectDate, defect1String,penCommentString);
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                emailIntent.setData(Uri.parse("mailto:"));
                                //emailIntent.setType("image/*");
                                //Uri imageUri = Uri.parse("Path:: " + imageFilePath);
                                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reports");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, reportMessage);
                                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(emailIntent);
                                } else {
                                    Toast.makeText(defectAddOn.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                if (selectImageUrl != null) {
                                    StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                                            + "." + getFileExtension(selectImageUrl));

                                    BitmapUtils.saveImage(defectAddOn.this,mResultBitmap);

                                    fileReference.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                           HideProgressDialog();
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;
                                            Uri downloadUri = uriTask.getResult();
                                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedImage);
                                            imageRef.delete();
                                            updateProject(selectedDefectID, downloadUri.toString(), mDefect1.getText().toString(),mProjectDate.getText().toString(),mPendingComment.getText().toString());
                                            Intent intent = new Intent(defectAddOn.this, defectList.class);
                                            //intent.putExtra("pendingID", selectedID);
                                            //intent.putExtra("Title",selectedTitle);
                                            //startActivity(intent);
                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    HideProgressDialog();
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }else {
                                    HideProgressDialog();
                                    DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");

                                    defect defects = new defect(selectedDefectID, selectedImage, defect1String, projectDate, penCommentString);

                                    databaseNewProject.child(selectedID).child(selectedDefectID).setValue(defects);

                                    Toast.makeText(defectAddOn.this, "Project Updated Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(defectAddOn.this, defectList.class);
                                   // intent.putExtra("pendingID", selectedID);
                                   // intent.putExtra("Title",selectedTitle);
                                   // startActivity(intent);
                                    finish();
                                }
                            }

                        } else if (items_update[which].equals("Update")) {
                            ShowProgressDialog();
                            final String projectDate = mProjectDate.getText().toString().trim();
                            final String defect1String = mDefect1.getText().toString().trim();
                            final String penCommentString = mPendingComment.getText().toString().trim();

                            if (defect1String.length() ==0 || projectDate.length() ==0 ) {
                               HideProgressDialog();
                                checkEmptyEditText(defect1String);
                            }else if(projectDate.matches(date)){
                                HideProgressDialog();
                                Toast.makeText(defectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            }

                            if (selectImageUrl != null) {
                                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                                        + "." + getFileExtension(selectImageUrl));

                                BitmapUtils.saveImage(defectAddOn.this,mResultBitmap);

                                fileReference.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        HideProgressDialog();
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful()) ;
                                        Uri downloadUri = uriTask.getResult();
                                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedImage);
                                        imageRef.delete();
                                        updateProject(selectedDefectID, downloadUri.toString(), defect1String, projectDate, penCommentString);
                                        Intent intent = new Intent(defectAddOn.this, defectList.class);
                                        intent.putExtra("pendingID", selectedID);
                                        intent.putExtra("Title",selectedTitle);
                                        startActivity(intent);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                HideProgressDialog();
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }else {
                                HideProgressDialog();
                                DatabaseReference databaseNewProject = FirebaseDatabase.getInstance().getReference("Defect Add On");

                                defect defects = new defect(selectedDefectID, selectedImage, defect1String, projectDate, penCommentString);

                                databaseNewProject.child(selectedID).child(selectedDefectID).setValue(defects);

                                Toast.makeText(defectAddOn.this, "Project Updated Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(defectAddOn.this, defectList.class);
                                intent.putExtra("pendingID", selectedID);
                                intent.putExtra("Title",selectedTitle);
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
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProjectAddOn(selectedDefectID);
                                Intent intent = new Intent(defectAddOn.this, defectList.class);
                                intent.putExtra("pendingID", selectedID);
                                intent.putExtra("tTitle",selectedTitle);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if(!mPendingHasChanged){
                    Intent intent = new Intent(defectAddOn.this, defectList.class);
                    intent.putExtra("pendingID", selectedID);
                    intent.putExtra("Title",selectedTitle);
                    startActivity(intent);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                Intent intent = new Intent(defectAddOn.this, defectList.class);
                                intent.putExtra("pendingID", selectedID);
                                intent.putExtra("Title",selectedTitle);
                                startActivity(intent);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getStringExtra("pendingID");
        selectedTitle = receivedIntent.getStringExtra("Title");
        selectedDefect1 = receivedIntent.getStringExtra("defect1");
        selectedComments = receivedIntent.getStringExtra("comments");
        HideMenu = receivedIntent.getIntExtra("HideMenu",0);

        Log.d(TAG,"Selected ID is: "+selectedID);

        if(HideMenu ==1) {
            selectedImage = receivedIntent.getStringExtra("projImage");
            selectedprojDate = receivedIntent.getStringExtra("date");
            mProjectDate.setText(selectedprojDate);
            selectedDefectID = receivedIntent.getStringExtra("defectAddOn");

            Picasso.get().load(selectedImage)
                    .fit()
                    .centerCrop()
                    .into(projectImage);
        }


        mDefect1.setText(selectedDefect1);
        mPendingComment.setText(selectedComments);



        // Hide save menu
        if(HideMenu == 1){
        HIDE_MENU = true;
        }
    }

    private boolean updateProject(String id, String imgUri, String defectString, String projDate, String comment) {
        DatabaseReference databaseNewDefect = FirebaseDatabase.getInstance().getReference("Defect Add On");

        defect defects = new defect(id, imgUri, defectString, projDate, comment);

        databaseNewDefect.child(selectedID).child(id).setValue(defects);

        Toast.makeText(this, "Defect Updated Successfully", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void deleteProjectAddOn(String id) {
        DatabaseReference deleteDefectAddOn = FirebaseDatabase.getInstance().getReference("Defect Add On");

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedImage);
        imageRef.delete();
        deleteDefectAddOn.child(selectedID).child(id).removeValue();

        Toast.makeText(this, "Defect is deleted", Toast.LENGTH_SHORT).show();
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
}
