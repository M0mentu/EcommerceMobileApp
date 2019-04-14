package com.example.momentu.ecommercemobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class product_details_Activity extends AppCompatActivity {


    private ImageView imageView,plus,minus;
    private TextView  nametext,pricetext,desctext,quantitytext,counter;
    private Button addToCartBTN;
    private String postKey;
    private DatabaseReference productRef,cartref;
    private ProgressDialog progressDialog;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fbuser;
    private int count;




    private static final String  TAG ="productDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Log.d(TAG, "onCreate: started.");

        postKey=getIntent().getExtras().get("postKey").toString();
        productRef=FirebaseDatabase.getInstance().getReference().child("products").child(postKey);



        imageView=(ImageView) findViewById(R.id.product_details_imageView);
        plus=(ImageView) findViewById(R.id.counterplus);
        minus=(ImageView) findViewById(R.id.counterminus);

        counter=(TextView)findViewById(R.id.counter);

        nametext=(TextView)findViewById(R.id.product_details_nameEdittext);
        pricetext=(TextView)findViewById(R.id.product_details_priceEdittext);
        desctext=(TextView)findViewById(R.id.product_details_descEdittext);
        quantitytext=(TextView)findViewById(R.id.product_details_quantitydittext);
        addToCartBTN=(Button)findViewById(R.id.product_details_button_addtoCart);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=firebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        userID=user.getUid();
        cartref=FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String name=dataSnapshot.child("name").getValue().toString();
                final String description=dataSnapshot.child("description").getValue().toString();
                final String price=dataSnapshot.child("price").getValue().toString();
                final String quantity=dataSnapshot.child("quantity").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();


                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (count<Integer.parseInt(quantity))
                            count++;
                        counter.setText(String.valueOf(count));
                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (count!=1)
                            count--;
                        counter.setText(String.valueOf(count));
                    }
                });


                nametext.setText(name);
                pricetext.setText(price+" EGP");
                desctext.setText(description);
                quantitytext.setText(quantity+"in stock");
                Picasso.get().load(image).into(imageView);

                addToCartBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItemToCart(name,description,price,counter.getText().toString(),image,quantity);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void addItemToCart(String name, String desc, String price, String quantity, String image,String oquantity) {

        progressDialog.setTitle("Adding product to cart");
        progressDialog.setMessage("Please wait while adding to cart");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        int totalprice=0;
        totalprice= Integer.parseInt(price)*Integer.parseInt(quantity);
        String pricee=String.valueOf(totalprice);

        HashMap<String,Object> productmap=new HashMap<>();
        productmap.put("description",desc);
        productmap.put("image",image);
        productmap.put("name",name);
        productmap.put("price",pricee);
        productmap.put("quantity",quantity);
        productmap.put("oprice",price);
        productmap.put("oquantity",oquantity);


        cartref.child("cart").child(name).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {

                    progressDialog.dismiss();
                    Toast.makeText(product_details_Activity.this, "item added to cart", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    progressDialog.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(product_details_Activity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
