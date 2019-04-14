package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "Tl3ly_eli_by7sl";

    Button addItemsButton;
     Button signinBTN;
     EditText emailEditText;
     EditText PassEditText;
     TextView signupTextView;
     TextView gotorpass;
     ProgressDialog progressDialog;
     FirebaseAuth firebaseAuth;
     CheckBox rememberme;
    MediaPlayer mp;
    private Boolean isadmin=false;
    private SharedPreferences loginPreferences;
    private static final String prefs_name="prefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText=(EditText)findViewById(R.id.loginEmail);
        PassEditText=(EditText)findViewById(R.id.loginPassword);

        rememberme=(CheckBox)findViewById(R.id.RembmerMeCheckBox);
        signupTextView=(TextView)findViewById(R.id.textSigup);
        gotorpass=(TextView)findViewById(R.id.resetView);
        addItemsButton=(Button)findViewById(R.id.goToAdminCenter);

        loginPreferences = getSharedPreferences(prefs_name, MODE_PRIVATE);


        signinBTN=(Button)findViewById(R.id.loginBTN);
        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        gotorpass.setOnClickListener((View.OnClickListener)this);
       signinBTN.setOnClickListener((View.OnClickListener) this);
       signupTextView.setOnClickListener((View.OnClickListener) this);

       getprefrenceDate();

    }


    private void getprefrenceDate() {
        SharedPreferences sp=getSharedPreferences(prefs_name,MODE_PRIVATE);
        if (sp.contains("pref_email")){
            String em=sp.getString("pref_email","not found.");
            emailEditText.setText(em.toString());
        }
        if (sp.contains("pref_pass")){
            String pass=sp.getString("pref_pass","not found.");
            PassEditText.setText(pass.toString());
        }
        if (sp.contains("pref_check")){
            boolean ch=sp.getBoolean("pref_check",false);
            rememberme.setChecked(ch);
        }

    }

    private void userlogin(){
        final String email=emailEditText.getText().toString();
        final String password=PassEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enusre all fields are filled", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            if (rememberme.isChecked()){


                                Boolean ischecked=rememberme.isChecked();
                                SharedPreferences.Editor editor=loginPreferences.edit();
                                editor.putString("pref_email",email);
                                editor.putString("pref_pass",password);
                                editor.putBoolean("pref_check",ischecked);
                                editor.apply();

                            }
                            else {
                             loginPreferences.edit().clear().apply();
                            }
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                           rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.hasChild("admin")){
                                       Toast.makeText(getApplicationContext(), "admin", Toast.LENGTH_SHORT).show();

                                    /*   mp  = MediaPlayer.create(getApplicationContext(),R.raw.loginsound);
                                       mp.start();*/

                                       finish();
                                       startActivity(new Intent(getApplicationContext(),adminActivity.class));
                                   }
                                   else{
                                       Toast.makeText(getApplicationContext(), "not admin", Toast.LENGTH_SHORT).show();
                                      /* mp  = MediaPlayer.create(getApplicationContext(),R.raw.loginsound);
                                       mp.start();*/
                                       finish();
                                       startActivity(new Intent(getApplicationContext(),profileActivity.class));
                                   }

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });


                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()!=null){

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("admin")){
                        Toast.makeText(getApplicationContext(), "admin", Toast.LENGTH_SHORT).show();

                                     /* mp  = MediaPlayer.create(getApplicationContext(),R.raw.loginsound);
                                       mp.start();*/

                        startActivity(new Intent(getApplicationContext(),adminActivity.class));
                        finish();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "not admin", Toast.LENGTH_SHORT).show();
                                      /* mp  = MediaPlayer.create(getApplicationContext(),R.raw.loginsound);
                                       mp.start();*/
                        startActivity(new Intent(getApplicationContext(),profileActivity.class));
                        finish();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }
      @Override
    public void onClick(View v) {
        if (v==signinBTN)
        {
            userlogin();


        }
        if(v==signupTextView)
        {

            startActivity(new Intent(this,MainActivity.class));
        }
        if (v==gotorpass)
        {

            startActivity(new Intent(this,forgotPassword_Activity.class));
        }
    }
}
