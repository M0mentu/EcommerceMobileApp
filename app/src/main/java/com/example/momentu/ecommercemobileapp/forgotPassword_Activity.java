package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword_Activity extends Activity  implements View.OnClickListener {


    EditText emailText;
    Button changeEmailBTN;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_);

        emailText = (EditText) findViewById(R.id.editTextResetemail);
        changeEmailBTN = (Button) findViewById(R.id.resetBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //----------------------------------------------------------
        changeEmailBTN.setOnClickListener((View.OnClickListener) this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v == changeEmailBTN) {
            String email = emailText.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Enter your email id", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(forgotPassword_Activity.this, "Check your Email for further instructions!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(forgotPassword_Activity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
