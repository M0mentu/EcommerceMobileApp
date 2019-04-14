package com.example.momentu.ecommercemobileapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class mapsActivity extends AppCompatActivity {

    private static final String TAG = "mapsActivity";
    private static final int ERROR_DIALOG_REQUEST=9001;
    private  Button Mapbtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Mapbtn=(Button)findViewById(R.id.buttonmap);

        if (isServicesOk()){
            Mapbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mapsActivity.this,showMap.class));
                }
            });

        }
    }



    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services verison");
        int availble=GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mapsActivity.this);
        if (availble==ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "isServicesOk: google play services working fine");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availble))
            {

                Log.d(TAG, "isServicesOk: an error occured ");
                Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(mapsActivity.this,availble,ERROR_DIALOG_REQUEST);
                dialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(), "can't make map request", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
