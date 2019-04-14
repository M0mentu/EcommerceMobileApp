package com.example.momentu.ecommercemobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class showMap extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "hihihi";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int Location_Per_req_code = 1234;
    private GoogleMap mymap;
    private FusedLocationProviderClient mfFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15F;
    private PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
    private  GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136));



    private AutoCompleteTextView mSearchText;
    private ImageView mGPS;
    private Button saveloc;

    private DatabaseReference locationref;
    private FirebaseAuth firebaseAuth;
    private String userID,location;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mymap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mymap.setMyLocationEnabled(true);
            mymap.getUiSettings().setMyLocationButtonEnabled(false);
                   init();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        firebaseAuth=firebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        userID=user.getUid();
        locationref=FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        mSearchText=(AutoCompleteTextView)findViewById(R.id.search_map);
        mGPS=(ImageView)findViewById(R.id.ic_gps);
        saveloc=(Button)findViewById(R.id.saveloc);

        getLocationPermission();
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting device location");
        mfFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        final Geocoder geocoder=new Geocoder(showMap.this);

        try{
            if (mLocationPermissionGranted){
                final Task location=mfFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                      
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentlocation=(Location) task.getResult();
                            List<Address> list=new ArrayList<>();
                            try{
                                list=geocoder.getFromLocation(currentlocation.getLatitude(),currentlocation.getLongitude(),1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(list.size()>0) {
                                Address address = list.get(0);
                                moveCamera(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
                            }

                        }
                        else{
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(showMap.this, "unable to get cuttent lcoation", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException"+e.getMessage());

        }
    }
    private void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG, "moveCamera: move the camera to lat"+latLng.latitude+", lng"+latLng.longitude);
        mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions options=new MarkerOptions()
                .position(latLng)
                .title(title);

        mymap.addMarker(options);
        location=title;
        saveloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationref.child("location").setValue(location);

            }
        });
        hideSoftKeyobar();


    }

    private void initMap(){
        Log.d(TAG, "initMap: intializing map");
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);// calling map fragment that was done in the layout then setting it up
        mapFragment.getMapAsync(showMap.this);
    }


    private void init(){
        Log.d(TAG, "init: initializing ");

        mGoogleApiClient=new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        mplaceAutocompleteAdapter=new PlaceAutocompleteAdapter(getApplicationContext(),Places.getGeoDataClient(getApplicationContext()),LAT_LNG_BOUNDS,null);

        mSearchText.setAdapter(mplaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId==EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_DONE||event.getAction()==event.ACTION_DOWN
                        ||event.getAction()==event.KEYCODE_ENTER)
                {
                    geoLocate();
                }
                return false;
            }
        });
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: click gps icon");
                getDeviceLocation();
            }
        });
        hideSoftKeyobar();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String searchString=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(showMap.this);
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }
        catch (IOException e)
        {
            Log.d(TAG, "geoLocate: IOException "+e);
        }

        if(list.size()>0){
            Address address=list.get(0);
            Log.d(TAG, "geoLocate: found a location "+address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting permission");
        String[]permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
               FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted=true;
                initMap();

            }
            else
            {
                ActivityCompat.requestPermissions(this,permission,Location_Per_req_code);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permission,Location_Per_req_code);
        }
    }

    private void hideSoftKeyobar(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted=false;
        switch (requestCode)
        {
            case Location_Per_req_code:{
                if(grantResults.length>0)//checking  permissions if all done initialize the map
                {
                    for (int i=0;i<grantResults.length;i++)
                    {
                        if (grantResults[i]!=PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted=false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed ");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted ");

                    mLocationPermissionGranted=true;
                    initMap();



                }
            }
        }
    }


}
