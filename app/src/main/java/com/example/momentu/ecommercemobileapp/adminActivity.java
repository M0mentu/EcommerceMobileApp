package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class adminActivity extends Activity implements View.OnClickListener  {

    private Button logoutBTN,gochart;
    private FirebaseAuth firebaseAuth;
    private ImageView gamingCat,fashionCat,electronicsCat,mobileCat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //============idssssssssssssss===================================
        logoutBTN=(Button)findViewById(R.id.adminLogoutBTN);
        gamingCat=(ImageView)findViewById(R.id.adminCatGaming);
        fashionCat=(ImageView)findViewById(R.id.adminCatFashion);
        electronicsCat=(ImageView)findViewById(R.id.adminCatElectronics);
        mobileCat=(ImageView)findViewById(R.id.adminCatMobileandTablets);
        gochart=(Button)findViewById(R.id.charytyty);
        //===============================================================





        //=================DATA BASE=============================
        firebaseAuth=firebaseAuth.getInstance();
        //=======================================================


        //===================Clickable objects===================
        logoutBTN.setOnClickListener(this);
        gamingCat.setOnClickListener(this);
        fashionCat.setOnClickListener(this);
        electronicsCat.setOnClickListener(this);
        mobileCat.setOnClickListener(this);
        gochart.setOnClickListener(this);

        //===================Clickable objects===================

    }
    @Override
    public void onClick(View v) {

        if (v == logoutBTN) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        if (v==gamingCat)
        {
            Intent intent=new Intent(getApplicationContext(),adminAddProduct.class);
            intent.putExtra("category","gamingCat");
            startActivity(intent);
        }
        if (v==fashionCat)
        {
            Intent intent=new Intent(getApplicationContext(),adminAddProduct.class);
            intent.putExtra("category","fashionCat");
            startActivity(intent);
        }
        if (v==electronicsCat)
        {
            Intent intent=new Intent(getApplicationContext(),adminAddProduct.class);
            intent.putExtra("category","electronicsCat");
            startActivity(intent);
        }
        if (v==mobileCat)
        {
            Intent intent=new Intent(getApplicationContext(),adminAddProduct.class);
            intent.putExtra("category","mobileCat");
            startActivity(intent);
        }
        if(v==gochart){
            Toast.makeText(this, "dsadsa", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(adminActivity.this,chartActivity.class));
            


        }
    }

}
