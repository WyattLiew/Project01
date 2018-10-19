package com.step.id.project01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText passwordEmail;
    private Button resetPassword;
    private ProgressDialog dialog;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initID();
    }

    private void initID() {
        passwordEmail = (EditText)findViewById(R.id.forgot_emailText);
        findViewById(R.id.forgot_emailSend).setOnClickListener(this);
        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void resetPassword(){
        String userEmail = passwordEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)){
            passwordEmail.setError("Please enter your registered Email address");
        }else{
            dialog.setMessage("Sending email, please wait...");
            dialog.show();
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Password reset email sent.",Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                    }else{
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forgot_emailSend:
                resetPassword();
                break;
        }
    }
}
