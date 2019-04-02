package com.testapp.lostfound;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;

    SmoothProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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

    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.smooth_progress_bar);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() != null && isEmailVerified() )    {
                    Toast.makeText(SignIn.this, "Signing In.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignIn.this,MainActivity.class));
                    finish();
                }
                else if(mAuth.getCurrentUser() != null){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignIn.this, "Verification Email sent.\nYou can sing in once you are verified!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
    }

    private void setGradientAnimation() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();
    }

    private void setOnClickListeners() {
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.forgot_pass).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sign_in:
                checkCredentialAndSignIn();
                break;
            case R.id.sign_up:
                Intent intent = new Intent(SignIn.this, SignUp.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(findViewById(R.id.sign_in), "sign_in_transition");
                pairs[1] = new Pair<View, String>(findViewById(R.id.sign_up), "sign_up_transition");
                pairs[2] = new Pair<View,String>(findViewById(R.id.forgot_pass) , "text_view_transition");
                //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this,btSignIn, ViewCompat.getTransitionName(btSignIn));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);
                startActivity(intent, options.toBundle());
                break;
            case R.id.forgot_pass:
                startActivity(new Intent(this,ForgotPassword.class));
                break;
        }
    }

    private void checkCredentialAndSignIn() {
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout password = findViewById(R.id.pass);

        String sEmail = email.getEditText().getText().toString().trim();
        String sPassword = password.getEditText().getText().toString().trim();

        if (sEmail.isEmpty()) {
            email.setError("An Email is required");
            email.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("A Password is required");
            password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
        }
        if (sPassword.length() < 6) {
            password.setError("Minimum length of password is 6");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(SignIn.this, "Invalid Details!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean isEmailVerified() {
        try {
            return mAuth.getCurrentUser().isEmailVerified();
        }
        catch (Exception e) {
            return false;
        }
    }
}
