package com.flatmates.ixion.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flatmates.ixion.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

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
    @BindView(R.id.button_register)
    Button buttonRegister;
    @BindView(R.id.button_google_login)
    SignInButton buttonGoogleLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ProgressDialog progressdialog;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences preferences;
    GoogleSignInOptions googleSignInOptions;
    GoogleApiClient googleApiClient;

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        if (preferences.getBoolean(IS_USER_LOGGED_IN, false)) {
            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
        } else {
            setContentView(R.layout.activity_main);
            setSupportActionBar(toolbar);
            ButterKnife.bind(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

            edittextPassword.setTextColor(getResources().getColor(android.R.color.white));
            edittextEmail.setTextColor(getResources().getColor(android.R.color.white));

            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "onConnectionFailed: " + connectionResult);
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();

            firebaseAuth = FirebaseAuth.getInstance();
            progressdialog = new ProgressDialog(this);

            buttonGoogleLogin.setSize(SignInButton.SIZE_WIDE);
            //TODO: DAMAN: facebook login https://firebase.google.com/docs/auth/android/facebook-login

        }

    }

    @OnClick(R.id.button_login)
    public void loginUser() {

        final String email = edittextEmail.getText().toString();
        final String password = edittextPassword.getText().toString();

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

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).
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
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progressdialog.dismiss();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button_register)
    public void registerUser() {

        final String email = edittextEmail.getText().toString();
        final String password = edittextPassword.getText().toString();

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

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
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
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progressdialog.dismiss();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.button_google_login)
    public void signInUserUsingGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
            } else {
                //TODO: check error
                // Signed out, show unauthenticated UI.
                Toast.makeText(this, "Unable to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Unable to sign in",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign in Successful",
                                    Toast.LENGTH_SHORT).show();
                            progressdialog.dismiss();
                            edittextEmail.setText("");
                            edittextPassword.setText("");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(IS_USER_LOGGED_IN, true);
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                        }
                    }
                });
    }


}
