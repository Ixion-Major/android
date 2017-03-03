package com.flatmates.ixion.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flatmates.ixion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flatmates.ixion.utils.Constants.IS_USER_LOGGED_IN;

public class LoginActivity extends AppCompatActivity {

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
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        if (preferences.getBoolean(IS_USER_LOGGED_IN, false)) {
            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
        } else {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            firebaseauth = FirebaseAuth.getInstance();
            progressdialog = new ProgressDialog(this);
        }

    }

    @OnClick(R.id.button_login)
    public void loginUser() {

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
                            Toast.makeText(getApplicationContext(), "login Successful",
                                    Toast.LENGTH_SHORT).show();
                            progressdialog.dismiss();
                            edittextEmail.setText("");
                            edittextPassword.setText("");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(IS_USER_LOGGED_IN, true);
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to login",
                                    Toast.LENGTH_SHORT).show();
                            progressdialog.dismiss();

                        }
                    }
                });
    }

    @OnClick(R.id.button_signup)
    public void registerUser() {
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
                            Toast.makeText(getApplicationContext(), "Registration Successful",
                                    Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(IS_USER_LOGGED_IN, true);
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
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
