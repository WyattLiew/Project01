package com.step.id.project01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private CheckBox mCheckBoxRemember;

    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mPrefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        initid();

        //Checkbox remember me
        getPreferencesData();

    }


    private void initid() {
        findViewById(R.id.user_login).setOnClickListener(this);
        findViewById(R.id.user_signup).setOnClickListener(this);
        findViewById(R.id.user_forgotPassword).setOnClickListener(this);
        progressBar =(ProgressBar) findViewById(R.id.login_progressBar);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword =(EditText) findViewById(R.id.editTextPassword);
        mCheckBoxRemember = (CheckBox) findViewById(R.id.user_rememberMe);
    }

    private void getPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_name")){
            String u = sp.getString("pref_name","none");
            editTextEmail.setText(u.toString());
        }
        if(sp.contains("pref_pass")){
            String password = sp.getString("pref_pass","none");
            editTextPassword.setText(password.toString());
        }
        if (sp.contains("pref_check")){
            Boolean b = sp.getBoolean("pref_check",false);
            mCheckBoxRemember.setChecked(b);
        }
    }


    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if(mCheckBoxRemember.isChecked()){
            Boolean boolisChecked = mCheckBoxRemember.isChecked();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("pref_name",editTextEmail.getText().toString());
            editor.putString("pref_pass",editTextPassword.getText().toString());
            editor.putBoolean("pref_check",boolisChecked);
            editor.apply();
        }else{
            mPrefs.edit().clear().apply();
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                   // if(mAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    //}else{
                      //  Toast.makeText(getApplicationContext(),"Please verify your email address",Toast.LENGTH_SHORT).show();
                   // }
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        editTextEmail.getText().clear();
        editTextPassword.getText().clear();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_login:
                userLogin();
                break;
            case  R.id.user_signup:
                Intent intent = new Intent (getApplicationContext(),registerActivity.class);
                startActivity(intent);
                break;
            case R.id.user_forgotPassword:
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                break;
        }
    }
}
