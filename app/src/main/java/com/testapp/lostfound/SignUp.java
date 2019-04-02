package com.testapp.lostfound;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;

    SmoothProgressBar progressBar;

    TextInputLayout mEmail ;
    TextInputLayout mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setGradientAnimation();
        initializeVariables();
        setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void setOnClickListeners() {
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);
    }

    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.smooth_progress_bar);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
                    sendVerificationMail();
                }
            }
        };
    }

    private void setGradientAnimation() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Fade fade = new Fade();
        fade.excludeTarget(findViewById(R.id.root_layout),true);
        fade.excludeTarget(findViewById(R.id.lost_and_found),true);
        getWindow().setEnterTransition(fade);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();

    }

    private void createNewUser() {

        String sEmail = mEmail.getEditText().getText().toString().trim();
        String sPassword = mPassword.getEditText().getText().toString();

        if (sEmail.isEmpty()) {
            mEmail.setError("An Email is required");
            mEmail.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            mPassword.setError("A Password is required");
            mPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
        }
        if (sPassword.length() < 6) {
            mPassword.setError("Minimum length of password is 6");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUp.this, "User already exist in the database.", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(SignUp.this, "Invalid Credential!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendVerificationMail() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful() && !isEmailVerified()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this, "Verification Email is sent confirm registration and Sign In", Toast.LENGTH_SHORT).show();
                    mEmail.getEditText().setText("");
                    mPassword.getEditText().setText("");
                    mAuth.signOut();
                }
            }
        });
    }

    private boolean isEmailVerified() {
        try {
            return mAuth.getCurrentUser().isEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up:
                createNewUser();
                break;
            case R.id.sign_in:
                onBackPressed();
                break;
        }
    }
}
