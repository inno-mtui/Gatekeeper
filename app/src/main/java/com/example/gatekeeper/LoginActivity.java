package com.example.gatekeeper;


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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
   private Button buttonLogin;
   private  EditText editTextEmail;
   private EditText editTextPassword;
   private FirebaseAuth mAuth;
   private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
       if (mAuth.getCurrentUser()!=null){
            // Send to registration activity
            finish();
            startActivity(new Intent(getApplicationContext(),RecognitionActivity.class));
        }
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        buttonLogin=findViewById(R.id.btn_login);

        progressDialog=new ProgressDialog(this);

        buttonLogin.setOnClickListener(this);


    }

    private void userLogIn(){
        // Getting the typed email and password to string form
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        //check if the text for email and password are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
        }
        progressDialog.setMessage("Login in please wait.....");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Send to registration Activity
                            finish();
                            startActivity(new Intent(getApplicationContext(),RecognitionActivity.class));
                            progressDialog.dismiss();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(v==buttonLogin){
            userLogIn();
        }
    }
}