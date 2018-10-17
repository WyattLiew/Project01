package com.step.id.project01;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.step.id.project01.firebase.User;

public class registerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName,editTextEmail, editTextPassword,editTextPhone;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        initid();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){

        }
    }

    private void initid() {
        findViewById(R.id.user_register).setOnClickListener(this);
        findViewById(R.id.already_have_an_account).setOnClickListener(this);
        progressBar =(ProgressBar) findViewById(R.id.register_progressBar);
        editTextEmail = (EditText) findViewById(R.id.registerEditTextEmail);
        editTextPassword =(EditText) findViewById(R.id.registerEditTextPassword);
        editTextName =(EditText) findViewById(R.id.registerEditTextName);
        editTextPhone = (EditText) findViewById(R.id.registerEditTextPhone);
    }

    private void registerUser(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if(name.isEmpty()){
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

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

        if(phone.isEmpty()){
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if(phone.length()<8){
            editTextPhone.setError("Enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               progressBar.setVisibility(View.GONE);
               if(task.isSuccessful()){
                   User user = new User(
                           name,
                           email,
                           phone

                   );

                   FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                           .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           progressBar.setVisibility(View.GONE);
                           if(task.isSuccessful()){
                               Toast.makeText(getApplicationContext(),"User Registation Successful",Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

               }else{
                   if(task.getException() instanceof FirebaseAuthUserCollisionException){
                       Toast.makeText(getApplicationContext(),"You are already registered",Toast.LENGTH_SHORT).show();
                   }else {
                       Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
                   }
           }
       });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_register:
                registerUser();
                break;
            case R.id.already_have_an_account:
                finish();
                break;
        }
    }
}
