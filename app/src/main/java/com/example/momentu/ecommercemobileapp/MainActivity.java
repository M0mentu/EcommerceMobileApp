package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener {

    Button registerButton;
    EditText nameText;
    EditText userText;
    EditText emailText;
    EditText passtext;
    EditText phoneText;
    EditText addressText;
    EditText confirmpasstext;
    TextView goLogin;
    TextView DateTextView;
    DatePickerDialog.OnDateSetListener dateSetListener;
    RadioButton radiomale;
    RadioButton radiofemale;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    Intent intent;
    String date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //-------------defs------------------------------------------------
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        registerButton=(Button)findViewById(R.id.registerBTN);
        nameText=(EditText)findViewById(R.id.editTextFirstName);
        userText=(EditText) findViewById(R.id.editTextUsername);
        phoneText=(EditText) findViewById(R.id.editTextPhone);
        addressText=(EditText) findViewById(R.id.editTextaddress);
        emailText=(EditText)findViewById(R.id.editTextEmail);
        passtext=(EditText)findViewById(R.id.editTextPassword);
        confirmpasstext=(EditText)findViewById(R.id.editTextPasswordConfirm);
        radiomale=(RadioButton)findViewById(R.id.radioButtonMale);
        radiofemale=(RadioButton)findViewById(R.id.radioButtonFemal);
        goLogin=(TextView)findViewById(R.id.textSigninGO);
        DateTextView=(TextView)findViewById(R.id.textviewDate);
        if (firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),profileActivity.class));
        }
        //-------------------------------------------------------------------

        dateSetListener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;

                date=month+"/"+dayOfMonth+"/"+year;
            }
        };
        DateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(MainActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        registerButton.setOnClickListener((View.OnClickListener) this);
        goLogin.setOnClickListener((View.OnClickListener) this);
    }



    private void Registerdata() {
        final String email = emailText.getText().toString().trim();
        final String username = userText.getText().toString().trim();
        final String name = nameText.getText().toString().trim();
        final String password = passtext.getText().toString().trim();
        final String cofirmpass = confirmpasstext.getText().toString();
        final String phone=phoneText.getText().toString();
        final String address=addressText.getText().toString();
        final String gender;
        final String date2;


        if (radiomale.isChecked()){
               gender="male";
        }
        else    {
               gender="female";
        }


        /*if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enusre all fields are filled", Toast.LENGTH_LONG).show();
            return;
        }
        if (password == cofirmpass)
        {
            Toast.makeText(this,"Password fields doesn't match",Toast.LENGTH_LONG).show();
            return;
        }*/
        progressDialog.setMessage("Registering user....");
        progressDialog.show();
        date2=date;


       final userData user=new userData(name,username,phone,address,gender,date2);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {



                                Toast.makeText(MainActivity.this,"Registration successful",Toast.LENGTH_LONG).show();

                                    finish();
                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));


                            }
                            else{
                                Toast.makeText(MainActivity.this,"Registration unsuccessful missing data",Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }
                else{
                    String s=task.getException().toString();
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,profileActivity.class));
        }
    }
    @Override
    public void onClick(View v) {
        if (v==registerButton)
        {
            Registerdata();
        }
        if (v==goLogin)
        {

            intent=new Intent(getApplicationContext(),LoginActivity.class);

            startActivity(intent);

        }

    }
}
