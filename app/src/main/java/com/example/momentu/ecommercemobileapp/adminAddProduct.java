package com.example.momentu.ecommercemobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class adminAddProduct extends Activity implements View.OnClickListener {

    private String categoryName,productdescription,productprice,productName, saveCurrentDate,saveCurrentTime,productID,imageUrl,productQuantity;
    private Button addNewProductBTN;
    private EditText editTextProductName,editTextProductDesc,editTextProductPrice,editTextProductQuantity;
    private ImageView productImage;
    private static final int Gallerypick=1;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    private StorageReference productimagesRef;
    private DatabaseReference productref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        categoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        productimagesRef=FirebaseStorage.getInstance().getReference().child("product images");
        productref=FirebaseDatabase.getInstance().getReference().child("products");

        addNewProductBTN=(Button)findViewById(R.id.addProductButton);
        editTextProductName=(EditText) findViewById(R.id.editTextProductName);
        editTextProductDesc=(EditText) findViewById(R.id.editTextProductDescription);
        editTextProductPrice=(EditText) findViewById(R.id.editTextProductPrice);
        editTextProductQuantity=(EditText)findViewById(R.id.editTextProductQuantity);
        productImage=(ImageView)findViewById(R.id.productImage);


        progressDialog=new ProgressDialog(this);


        productImage.setOnClickListener(this);
        addNewProductBTN.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v==productImage)
        {
            OpenGallery();
        }
        if (v==addNewProductBTN)
        {
            ValidateProductData();
        }
    }



    //=================opening mobile gallery to pick photo============================
    private void OpenGallery() {
        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,Gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallerypick&&resultCode==RESULT_OK&& data!=null &&data.getData()!=null){
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(productImage);
        }
    }

    private void ValidateProductData() {

        productdescription=editTextProductDesc.getText().toString();
        productName=editTextProductName.getText().toString();
        productprice=editTextProductPrice.getText().toString();
        productQuantity=editTextProductQuantity.getText().toString();

        if (imageUri==null)
        {
            Toast.makeText(getApplicationContext(), "Missing image", Toast.LENGTH_SHORT).show();
        }
        else if (productdescription.isEmpty())
        {
            Toast.makeText(getApplicationContext(), " Product Description is Missing ", Toast.LENGTH_SHORT).show();

        }
        else if (productName.isEmpty())
        {
            Toast.makeText(getApplicationContext(), " Product Name is Missing ", Toast.LENGTH_SHORT).show();

        }
        else if (productprice.isEmpty())
        {
            Toast.makeText(getApplicationContext(), " Product Price is Missing ", Toast.LENGTH_SHORT).show();

        }
        else if(productQuantity.isEmpty())
        {
            Toast.makeText(getApplicationContext(), " Product Quantity is Missing ", Toast.LENGTH_SHORT).show();

        }
        else
        {
            StoreProductInformation();
        }


    }
    private String getfileExtension(Uri uri)
    {

        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton().getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void StoreProductInformation() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productID=saveCurrentDate+saveCurrentTime;

        final StorageReference filepath=productimagesRef.child(imageUri.getLastPathSegment()+productID+getfileExtension(imageUri));

        final UploadTask uploadTask=filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              String message=e.toString();
                Toast.makeText(adminAddProduct.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(adminAddProduct.this, "Image Uploaded Successfully ", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }

                        imageUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                       if (task.isSuccessful())
                       {
                           imageUrl=task.getResult().toString();

                           Toast.makeText(adminAddProduct.this, "image url saved ", Toast.LENGTH_SHORT).show();
                           addProductToDatabase();
                       }

                    }
                });
            }
        });

    }

    //===instead of creating a class for product data
    private void addProductToDatabase() {

        progressDialog.setTitle("Adding product");
        progressDialog.setMessage("Please wait while uploading new product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HashMap<String,Object> productmap=new HashMap<>();
        productmap.put("productid",productID);
        productmap.put("date",saveCurrentDate);
        productmap.put("time",saveCurrentTime);
        productmap.put("description",productdescription);
        productmap.put("image",imageUrl);
        productmap.put("name",productName.toLowerCase());
        productmap.put("category",categoryName);
        productmap.put("price",productprice);
        productmap.put("quantity",productQuantity);


        productref.child(productName).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           
                if (task.isSuccessful())
                {
                    startActivity(new Intent(adminAddProduct.this,adminActivity.class));

                    progressDialog.dismiss();
                    Toast.makeText(adminAddProduct.this, "product added successfully :D", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    progressDialog.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(adminAddProduct.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
