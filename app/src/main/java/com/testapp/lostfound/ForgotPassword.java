package com.testapp.lostfound;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setGradientAnimation();
        findViewById(R.id.send_password_reset_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetMail();
            }
        });
    }

    private void sendPasswordResetMail()    {
        TextInputLayout email = findViewById(R.id.email);
        String sEmail = email.getEditText().getText().toString().trim();

        FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Password reset email has been sent\nPlease check your inbox.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ForgotPassword.this, "Failed to send verification email\nPlease check your Email or Internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setGradientAnimation() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();
    }
}
