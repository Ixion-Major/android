package com.flatmates.ixion;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edittext_email)
    EditText edittextEmail;
    @BindView(R.id.edittext_password)
    EditText edittextPassword;
    @BindView(R.id.button_login)
    Button buttonLogin;
    @BindView(R.id.button_signup)
    Button buttonSignup;
    private ProgressDialog progressdialog;
    private FirebaseAuth firebaseauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseauth = FirebaseAuth.getInstance();
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        progressdialog = new ProgressDialog(this);

    }


    private void loginUser() {

        String email = edittextEmail.getText().toString();
        String password = edittextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressdialog.setMessage("logging User...");
        progressdialog.show();


        firebaseauth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "login Succesfull",
                            Toast.LENGTH_SHORT).show();
                    progressdialog.dismiss();
                    edittextEmail.setText("");
                    edittextPassword.setText("");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to login",
                            Toast.LENGTH_SHORT).show();
                    progressdialog.dismiss();

                }
            }
        });
    }


    private void registerUser() {
        String email = edittextEmail.getText().toString();
        String password = edittextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressdialog.setMessage("Registering User...");
        progressdialog.show();

        firebaseauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration Succesfull",
                            Toast.LENGTH_SHORT).show();
                    progressdialog.dismiss();
                    edittextEmail.setText("");
                    edittextPassword.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to register",
                            Toast.LENGTH_SHORT).show();
                    progressdialog.dismiss();

                }
            }
        });
    }
}
