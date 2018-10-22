package com.step.id.project01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.step.id.project01.firebase.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    private TextView profile_name, profile_email, profile_phone;
    private ImageView profile_img;

    private ProgressDialog dialog;
    //Firebasse
    DatabaseReference databaseNewProject, projectsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseNewProject = FirebaseDatabase.getInstance().getReference();
        projectsRef = databaseNewProject.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        initID();
        onRetrieve();

    }

    private void initID() {
        findViewById(R.id.user_changePassword).setOnClickListener(this);
        findViewById(R.id.user_signout).setOnClickListener(this);
        dialog = new ProgressDialog(this);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_email = (TextView) findViewById(R.id.profile_email);
        profile_img = (ImageView) findViewById(R.id.profile_Image);
        profile_phone = (TextView) findViewById(R.id.profile_phone);
    }

    public void onRetrieve() {

        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User projects = dataSnapshot.getValue(User.class);
                String name = projects.getName();
                String email = projects.getEmail();
                String phone = projects.getPhone();
                String img = projects.getImgURL();
                Log.d(TAG,"img URL is: " +img);


                profile_name.setText(name);
                profile_email.setText(email);
                profile_phone.setText(phone);
               // profile_img.setImageURI(Uri.parse(projects.getImgURL()));
                Picasso.get().load(img)
                        .fit()
                        .centerCrop()
                        .into(profile_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showChangePasswordDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.changepassword_dialog, null);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Change Password");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //final EditText editTextCurrentPassword = (EditText) dialogView.findViewById(R.id.changePassword_Current);
        final EditText editTextNewPassword = (EditText) dialogView.findViewById(R.id.changePassword_New);
        final EditText editTextConfirmPassword = (EditText) dialogView.findViewById(R.id.changePassword_Confirm);
        final Button buttonChange = (Button) dialogView.findViewById(R.id.changePassword_Btn);
        final CheckBox showPassword = (CheckBox) dialogView.findViewById(R.id.changePassword_Chk);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTextNewPassword.setTransformationMethod(null);
                    editTextConfirmPassword.setTransformationMethod(null);
                }else{
                    editTextNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                    editTextConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextConfirmPassword.getText().toString())) {
                    editTextConfirmPassword.setError("Please fill in the blank.");
                }else if(TextUtils.isEmpty(editTextNewPassword.getText().toString())) {
                    editTextNewPassword.setError("Please fill in the blank.");
                }else
                    changePassword(editTextNewPassword.getText().toString(), editTextConfirmPassword.getText().toString());
                
            }
        });

    }

    private void changePassword(String Pnew,String Pconfirm){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(Pnew.equals(Pconfirm)) {
                dialog.setMessage("Changing password, please wait...");
                dialog.show();
                user.updatePassword(Pnew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Your password has been changed successfully",Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));

                        }else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Password could not be changed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(),"Oops, that's not the same password as the first one..",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_changePassword:
                showChangePasswordDialog();
                break;
            case R.id.user_signout:
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                Toast.makeText(ProfileActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
