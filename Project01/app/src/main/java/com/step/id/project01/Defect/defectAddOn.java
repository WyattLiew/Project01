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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.step.id.project01.Image.BitmapUtils;
import com.step.id.project01.R;
import com.step.id.project01.RecyclerView.DefImageAdapter;
import com.step.id.project01.model.defect;
import com.step.id.project01.model.defectImageAddon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class defectAddOn extends AppCompatActivity {

    private static final String TAG = "defectAddOn";

    private EditText mDefect1, mPendingComment;
    private TextView mProjectDate;
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;

    // Upload image
    private RecyclerView mUploadList;
    private ArrayList<defectImageAddon> listNewDefect =new ArrayList<>();

    //Camera
    ImageView projectImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath;
    private Uri selectImageUrl;
    private Bitmap mResultBitmap;

    // report string
    String reportMessage;

    // For attach image to email
    File pic;

    // Date
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date = "select a date";

    // Update data
    private String selectedComments, selectedprojDate, selectedDefect1;
    private int HideMenu;
    private String selectedID, selectedDefectID, selectedTitle;
    private String selectedImage;
    private boolean HIDE_MENU = false;

    private DefImageAdapter defImageAdapter;

    //Fire base
    private DatabaseReference mDatabaseAddon,mDatabaseAddonImages;
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

        mUploadList = (RecyclerView) findViewById(R.id.uploadRecyclerView);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUploadList.setHasFixedSize(true);

        mDatabaseAddon = FirebaseDatabase.getInstance().getReference("Defect Add On").child(selectedID);
        mDatabaseAddonImages = FirebaseDatabase.getInstance().getReference("Defect add on image");
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
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }else{
                        intent.putExtra(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "Select picture"), SELECT_FILE);
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

                int itemList = listNewDefect.size();

                if (itemList < 5) {

                    defectImageAddon s;

                    s = new defectImageAddon();

                    projectImage.setImageURI(Uri.parse(imageFilePath));
                    projectImage.setMinimumHeight(512);

                    selectImageUrl = Uri.fromFile(new File(imageFilePath));
                    mResultBitmap = ((BitmapDrawable) projectImage.getDrawable()).getBitmap();

                    BitmapUtils.saveImage(defectAddOn.this, mResultBitmap);

                    s.setImgURL(Uri.fromFile(new File(imageFilePath)).toString());
                    listNewDefect.add(s);
                    mUploadList.setAdapter(new DefImageAdapter(this, listNewDefect));


                    try {
                        File root = Environment.getExternalStorageDirectory();
                        if (root.canWrite()) {
                            pic = new File(root, "pic.png");
                            FileOutputStream out = new FileOutputStream(pic);
                            mResultBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        }
                    } catch (IOException e) {
                        Log.e("BROKEN", "Could not write file " + e.getMessage());
                    }
                } else {
                    projectImage.setImageURI(Uri.parse(imageFilePath));
                    projectImage.setMinimumHeight(512);
                    mResultBitmap = ((BitmapDrawable) projectImage.getDrawable()).getBitmap();
                    BitmapUtils.saveImage(defectAddOn.this, mResultBitmap);
                    Toast.makeText(this, "Maximum 5 images", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == SELECT_FILE) {

                defectImageAddon s;

                if (data.getClipData() != null) {

                    int itemList = listNewDefect.size();
                    int totalItemsSelected = data.getClipData().getItemCount();

                    if (totalItemsSelected > 5) {
                        Toast.makeText(this, "Maximum 5 images", Toast.LENGTH_SHORT).show();
                    } else {

                        if (itemList + totalItemsSelected >= 6) {
                            listNewDefect.clear();
                            for (int i = 0; i < totalItemsSelected; i++) {

                                s = new defectImageAddon();

                                selectImageUrl = data.getClipData().getItemAt(i).getUri();

                                //s.setName(getFileName(selectImageUrl));

                                s.setImgURL(selectImageUrl.toString());
                                listNewDefect.add(s);
                            }
                            mUploadList.setAdapter(new DefImageAdapter(this, listNewDefect));
                        } else {
                            for (int i = 0; i < totalItemsSelected; i++) {

                                s = new defectImageAddon();

                                //Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                selectImageUrl = data.getClipData().getItemAt(i).getUri();

                                //s.setName(getFileName(selectImageUrl));

                                s.setImgURL(selectImageUrl.toString());
                                listNewDefect.add(s);
                            }
                            mUploadList.setAdapter(new DefImageAdapter(this, listNewDefect));
                        }
                    }
                } else {
                    if (data.getData() != null) {

                        selectImageUrl = data.getData();
                        s = new defectImageAddon();
                        s.setImgURL(selectImageUrl.toString());
                        listNewDefect.add(s);
                        mUploadList.setAdapter(new DefImageAdapter(this,listNewDefect));
                        projectImage.setImageURI(selectImageUrl);
                        Bitmap imgBitmap = ((BitmapDrawable) projectImage.getDrawable()).getBitmap();

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
                    }
                }


            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT > 16) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.action_update).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(true);
            } else {
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

    public void initId() {

        mDefect1 = findViewById(R.id.defect_1);
        mPendingComment =findViewById(R.id.defect_comment);
        mProjectDate = findViewById(R.id.defect_date);
        projectImage = findViewById(R.id.defect_img);
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

    private void initDate() {
        mProjectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(defectAddOn.this,
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
                mProjectDate.setText(date);
            }
        };
    }

    @SuppressLint("StringFormatInvalid")
    private String createReportSummary(String date, String defect1, String comments) {

        String reportMessage = "Hi ";
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_date, date);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_description);
        reportMessage += "\n" + getString(R.string.report_summary_defect_1, defect1);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_comments, comments);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_Thank_you);

        return reportMessage;
    }

    public void initCheckUnsavedChanges() {
        mDefect1.setOnTouchListener(mTouchListener);
        mPendingComment.setOnTouchListener(mTouchListener);
        mProjectDate.setOnTouchListener(mTouchListener);
        projectImage.setOnTouchListener(mTouchListener);

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

    private void checkEmptyEditText(String defect) {

        if (TextUtils.isEmpty(defect)) {
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
                final CharSequence[] items = {"Save", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(defectAddOn.this);
                builder.setTitle("Select options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Save")) {
                            ShowProgressDialog();
                            final String projectDate = mProjectDate.getText().toString().trim();
                            final String defect1String = mDefect1.getText().toString().trim();
                            final String penCommentString = mPendingComment.getText().toString().trim();

                            if (selectImageUrl == null) {
                                HideProgressDialog();
                                Toast.makeText(defectAddOn.this, "Image cannot be null.", Toast.LENGTH_SHORT).show();
                            } else if (defect1String.length() == 0 || projectDate.length() == 0) {
                                HideProgressDialog();
                                checkEmptyEditText(defect1String);
                            } else if (projectDate.matches(date)) {
                                HideProgressDialog();
                                Toast.makeText(defectAddOn.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                            } else {

                                //  BitmapUtils.saveImage(defectAddOn.this, mResultBitmap);

                                final String id = mDatabaseAddon.push().getKey();

                                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                                      + "." + getFileExtension(selectImageUrl));

                                    fileReference.putFile(selectImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            HideProgressDialog();
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;
                                            Uri downloadUri = uriTask.getResult();
                                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                            //String id = mDatabaseAddon.push().getKey();
                                            defect defect = new defect(id, downloadUri.toString(), defect1String, projectDate, penCommentString);
                                            mDatabaseAddon.child(id).setValue(defect);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    HideProgressDialog();
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                for (int i = 0; i < listNewDefect.size(); i++) {

                                    StorageReference fileReferences = mStorageReference.child(System.currentTimeMillis()
                                            + "." + getFileExtension(Uri.parse(listNewDefect.get(i).getImgURL())));

                                    fileReferences.putFile(Uri.parse(listNewDefect.get(i).getImgURL())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            HideProgressDialog();
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;
                                            Uri downloadUri = uriTask.getResult();
                                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                            String imageid = mDatabaseAddonImages.push().getKey();
                                            defectImageAddon defect = new defectImageAddon(imageid, downloadUri.toString());
                                            mDatabaseAddonImages.child(id).child(imageid).setValue(defect);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    HideProgressDialog();
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                Intent intent = new Intent(defectAddOn.this, defectList.class);
                                intent.putExtra("pendingID", selectedID);
                                intent.putExtra("Title",selectedTitle);
                                startActivity(intent);
                            }

                        } else if (items[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mPendingHasChanged) {
                    Intent intent = new Intent(defectAddOn.this, defectList.class);
                    intent.putExtra("pendingID", selectedID);
                    intent.putExtra("Title", selectedTitle);
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
                                intent.putExtra("Title", selectedTitle);
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
        HideMenu = receivedIntent.getIntExtra("HideMenu", 0);

        if (HideMenu == 1) {
            selectedDefectID = receivedIntent.getStringExtra("defectAddOn");
        }

        // Hide save menu
        if (HideMenu == 1) {
            HIDE_MENU = true;
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
}
