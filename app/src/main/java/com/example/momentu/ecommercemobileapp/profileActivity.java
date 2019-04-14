package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.momentu.ecommercemobileapp.holdTheData.holdTheData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.net.URI;

public class profileActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "Tl3ly_eli_by7sl";


    //Design content
    private TextView userEmailTextView;
    private  Button logoutButton;
    private  Button selectImage;
    private Button gotToShop;
    private Button uploadimage;
    private  Button addItemsButton;
    private  TextView usernameTextview;
    private  TextView genderTextview;
    private  TextView phoneTextview;
    private  TextView addressTextview;
    private  TextView nameTextview;
    private  TextView dateTextview;
    private  upload updata;
    boolean thereIsImage=false;

    private MediaPlayer mp;
    private userData userdata;
    private ProgressBar mprogressBar;
    private CircularImageView pimage;
    private ImageView pieimage,cartimage;

    private StorageReference mstorage;
    private static final int gallery_intent=1;


    //other data
    private String userID;
    private String imageURL;
    private Uri muri;

    // Firebase  content
    UserProfileChangeRequest profileChangeRequest;
    private FirebaseUser fbuser;
    private DatabaseReference mRef2;
    private DatabaseReference mRef;
    private StorageReference storageRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mstorage=FirebaseStorage.getInstance().getReference();

        //defining design
        userEmailTextView=(TextView)findViewById(R.id.userEmail);
        logoutButton=(Button)findViewById(R.id.logOutButton);
        selectImage=(Button)findViewById(R.id.profilePicImage);
        uploadimage=(Button)findViewById(R.id.profilePicImage2);
        usernameTextview=(TextView)findViewById(R.id.usernameProfileText);
        genderTextview=(TextView)findViewById(R.id.genderProfileText);
        phoneTextview=(TextView)findViewById(R.id.phoneProfileText);
        addressTextview=(TextView)findViewById(R.id.addressProfileText);
        nameTextview=(TextView)findViewById(R.id.nameProfileText);
        dateTextview=(TextView)findViewById(R.id.dateProfielText);
        pimage=(CircularImageView) findViewById(R.id.profileimageTest);
        mprogressBar=(ProgressBar)findViewById(R.id.progressBar2);
        gotToShop=(Button)findViewById(R.id.goToShopBTN);
        cartimage=(ImageView)findViewById(R.id.profielgotocart);

        //---fire base user
        fbuser=FirebaseAuth.getInstance().getCurrentUser();








        firebaseAuth=firebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        storageRef=FirebaseStorage.getInstance().getReference("uploads");
        mRef=FirebaseDatabase.getInstance().getReference("users");
        mRef2=FirebaseDatabase.getInstance().getReference("users");
        Log.d(TAG, "memedata mref "+mRef.toString()+"<<<<<<<<<<<<");
        FirebaseUser user=firebaseAuth.getCurrentUser();
        userID=user.getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "memedata mref "+dataSnapshot.getRef().toString()+"<<<<<<<<<<<<");

                showUserData(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });












        userEmailTextView.setText("Email: "+user.getEmail());
        uploadimage.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        selectImage.setOnClickListener(this);
        gotToShop.setOnClickListener(this);
        cartimage.setOnClickListener(this);

    }

    private void showUserData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds :dataSnapshot.getChildren()) {
            Log.d(TAG, "\"memedata ds "+ds.child(userID).toString());
            userdata = new userData();
            userdata.setAddress(dataSnapshot.child(userID).getValue(userData.class).getAddress());
            userdata.setDate(dataSnapshot.child(userID).getValue(userData.class).getDate());
            userdata.setGender(dataSnapshot.child(userID).getValue(userData.class).getGender());
            userdata.setName(dataSnapshot.child(userID).getValue(userData.class).getName());
            userdata.setPhone(dataSnapshot.child(userID).getValue(userData.class).getPhone());
            userdata.setUsername(dataSnapshot.child(userID).getValue(userData.class).getUsername());
            boolean s=dataSnapshot.child(userID).child("imageData").exists();
            Log.d(TAG, "showUserData: "+s);
            holdTheData.getCurerentOnlineUser=userdata;



            if (s==true) {
                Log.d(TAG, "showUserData: "+"entered 1");

                imageURL = dataSnapshot.child(userID).child("imageData").getValue(upload.class).getmImageUrl();
                holdTheData.imageURI=imageURL;
                Log.d(TAG, "showUserData: "+imageURL);
            Picasso.get().load(imageURL).into(pimage);
            }
            else
            {
                if (userdata.getGender()=="male")
                    pimage.setImageResource(R.drawable.maleavatar);

                else{
                    pimage.setImageResource(R.drawable.femaleavatar);

                }
            }



            usernameTextview.setText("Welcome: "+userdata.getUsername());
            dateTextview.setText("Date: "+userdata.getDate());
            genderTextview.setText("Gender: "+userdata.getGender());
            nameTextview.setText("Name: "+ userdata.getName());
            phoneTextview.setText("Phone number: "+userdata.getPhone());
            addressTextview.setText("Address: "+userdata.getAddress());


        }

    }



    @Override
    public void onClick(View v) {

        if(v==logoutButton)
        {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
       if (v==gotToShop)
        {
            startActivity(new Intent(getApplicationContext(),shoppingActivity.class));
        }

        if(v==cartimage){
            startActivity(new Intent(profileActivity.this,cartActivity.class));
        }





















        if  (v==selectImage)
        {
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,gallery_intent);
        }
        if (v==uploadimage)
        {
            uploadProfileImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==gallery_intent&&resultCode==RESULT_OK&& data!=null &&data.getData()!=null)
        {
            muri=data.getData();
            Picasso.get().load(muri).into(pimage);
        }
    }
    private String getfileExtension(Uri uri)
    {

        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton().getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadProfileImage() {
        if (muri!=null)
        {

            final StorageReference fileRefrence=mstorage.child(System.currentTimeMillis()+"."+getfileExtension(muri));
            fileRefrence.putFile(muri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Handler handler =new Handler();
                            //delays progress bar by 5 seconds
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mprogressBar.setProgress(0);
                                }
                            },500);
                            Log.d(TAG, "onSuccess: "+uri.toString());
                            Toast.makeText(profileActivity.this,"upload successfull", Toast.LENGTH_LONG).show();
                            updata=new upload(userdata.getUsername()+"profileImage",uri.toString());
                            mRef2.child(userID).child("imageData").setValue(updata);
                            thereIsImage=true;

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(profileActivity.this,"Failed to upload iamge",Toast.LENGTH_LONG).show();
                            thereIsImage=false;

                        }
                    }) .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double  progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mprogressBar.setProgress((int)progress);


                }
            });

        }
        else{
            Toast.makeText(this, "No file selected ", Toast.LENGTH_SHORT).show();
        }


    }

















}
