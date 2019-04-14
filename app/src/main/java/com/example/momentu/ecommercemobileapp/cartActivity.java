package com.example.momentu.ecommercemobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaCas;
import android.os.AsyncTask;
import android.os.Message;
import android.se.omapi.Session;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.momentu.ecommercemobileapp.holdTheData.holdTheData;
import com.example.momentu.ecommercemobileapp.models.prodcuts;
import com.example.momentu.ecommercemobileapp.viewholder.cartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class cartActivity extends AppCompatActivity {

    //google maps service parameters
    private static final String TAG = "mapsActivity";
    private static final int ERROR_DIALOG_REQUEST=9001;
    private  Button Mapbtn;
    private String saveCurrentDate,saveCurrentTime,itemsb="",quantityb="",email,etext;
    private ProgressDialog progressDialog;
    private prodcuts pp=new prodcuts();

    private String tempname="",tempquant="";


    javax.mail.Session session=null;
    Context context=null;




    RecyclerView recyclerView;
    private DatabaseReference productRef,testref,useraddressref,useraddorderref,productremove;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private  int  quant,priceup,totalPrice=0,originalprice=0,oquant=0,qz;
    private  Button checkoutBtn,getLocationBTN;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        checkoutBtn=(Button)findViewById(R.id.car_checkout_BTN);
        getLocationBTN=(Button)findViewById(R.id.car_checkLOC_BTN);
        recyclerView=findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog=new ProgressDialog(this);

        firebaseAuth=firebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        userID=user.getUid();
        Log.d("cart_activity", "onCreate: "+userID);
        useraddorderref=FirebaseDatabase.getInstance().getReference().child("orders");
        productRef=FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("cart");
        testref=FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("cart");
        useraddressref=FirebaseDatabase.getInstance().getReference("users").child(userID);
        productremove=FirebaseDatabase.getInstance().getReference().child("products");
        Log.d("cart_activity", "onCreate: "+testref.toString());
        email=firebaseAuth.getCurrentUser().getEmail();




        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                useraddressref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("location")&&dataSnapshot.hasChild("cart")){

                            progressDialog.setTitle("processing order");
                            progressDialog.setMessage("Please wait ");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            pp.setLocation(dataSnapshot.child("location").getValue(String.class));

                            testref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds :dataSnapshot.getChildren()) {


                                        if(ds.child("name").getValue(String.class)!=null)
                                        itemsb+=" "+ds.child("name").getValue(String.class)+" ";
                                        pp.setName(itemsb);




                                        if(ds.child("quantity").getValue(String.class)!=null)
                                        quantityb+=" "+ds.child("quantity").getValue(String.class)+" ";
                                        pp.setQuantity(quantityb);

                                        pp.setPrice(String.valueOf(totalPrice));





                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd, yyyy");
                                    saveCurrentDate=currentdate.format(calendar.getTime());
                                    SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
                                    saveCurrentTime=currentTime.format(calendar.getTime());
                                    HashMap<String,Object> ordermap=new HashMap<>();
                                    String k="Order:"+saveCurrentDate+":"+saveCurrentTime;
                                    ordermap.put("orderID",k);
                                    ordermap.put("price",pp.getPrice());
                                    ordermap.put("items bough", pp.getName());
                                    ordermap.put("quantity",pp.getQuantity());
                                    ordermap.put("location",pp.getLocation());
                                    ordermap.put("user",holdTheData.getCurerentOnlineUser.getName());


                                    useraddorderref.child(k).updateChildren(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {

                                                sendemail();
                                                useraddressref.child("cart").removeValue();
                                                checkoutBtn.setText("CHECK OUT");
                                                progressDialog.dismiss();
                                                Toast.makeText(cartActivity.this, "Order submitted successfully", Toast.LENGTH_SHORT).show();


                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                String message=task.getException().toString();
                                                Toast.makeText(cartActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });


                        }
                        else{
                            Toast.makeText(cartActivity.this, "Delivery location missing or cart is empty          ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });












        if(isServicesOk()) {
            getLocationBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(cartActivity.this, showMap.class));
                }
            });
        }

    }


    private void sendemail() {


        Properties properties=new Properties();
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","465");
        session= javax.mail.Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mohamed.hazem.mahmoud.elasfty@gmail.com","xxxxx");
            }
        });
        RetreiveFeedTask task=new RetreiveFeedTask();
        task.execute();
    }
    class RetreiveFeedTask extends AsyncTask<String,String,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try{
                javax.mail.Message message=new MimeMessage(session);
                message.setFrom(new InternetAddress("warsgp@gmail.com"));
                message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(email));
                message.setSubject("Order submitted successfully");
                message.setContent("Thank you for shopping with us","text/html; charset=utf-8");
                Transport.send(message);

            }
            catch (MessagingException e)
            {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // startActivity(new Intent(cartActivity.this,profileActivity.class));
            Toast.makeText(getApplicationContext(), "message sent to your email", Toast.LENGTH_SHORT).show();
            
        }
    }


    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<prodcuts> options=new FirebaseRecyclerOptions.Builder<prodcuts>().setQuery(productRef,prodcuts.class).build();

        final FirebaseRecyclerAdapter<prodcuts,cartViewHolder> adabter= new FirebaseRecyclerAdapter<prodcuts, cartViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull cartViewHolder holder, int position, @NonNull final prodcuts model) {




                originalprice=0;
                priceup=0;
                quant=Integer.parseInt(model.getQuantity());

                oquant=0;
                priceup=0;
                oquant=Integer.parseInt(model.getOquantity());
                totalPrice+=Integer.parseInt(model.getPrice());


                Log.d("Price", "onBindViewHolder: "+totalPrice);

                holder.ctProductName.setText(model.getName());
                holder.ctProductyDesc.setText(model.getDescription());
                holder.getcartEtProductPrice.setText(model.getPrice()+" EGP");
                holder.ctProductQuantity.setText("Quantity "+model.getQuantity());
                holder.counter.setText(model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder.imageView);



                holder.removebt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productRef.child(model.getName()).removeValue();
                    }
                });
                holder.plusview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quant<=oquant)
                        {
                            originalprice=Integer.parseInt(model.getOprice());
                            quant=Integer.parseInt(model.getQuantity());
                            priceup=Integer.parseInt(model.getPrice());

                            quant++;
                            priceup=quant*originalprice;
                            productRef.child(model.getName()).child("quantity").setValue(String.valueOf(quant));
                            productRef.child(model.getName()).child("price").setValue(String.valueOf(priceup));




                        }

                    }
                });
                holder.minusview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quant>1)
                        {

                            originalprice=Integer.parseInt(model.getOprice());
                            quant=Integer.parseInt(model.getQuantity());
                            priceup=Integer.parseInt(model.getPrice());


                            quant--;
                            priceup=quant*originalprice;
                            totalPrice-=priceup;
                            Log.d("Price", "quant++ "+quant+"+"+originalprice+"="+priceup);
                            productRef.child(model.getName()).child("quantity").setValue(String.valueOf(quant));
                            productRef.child(model.getName()).child("price").setValue(String.valueOf(priceup));


                        }
                    }
                });
                holder.getcartEtProductPrice.setText(model.getPrice()+" EGP");
                holder.ctProductQuantity.setText("Quantity "+model.getQuantity());
                checkoutBtn.setText(String.valueOf(totalPrice));


                testref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        totalPrice=0;
                        for(DataSnapshot ds :dataSnapshot.getChildren()) {
                            prodcuts p=new prodcuts();

                            Log.d("cart_activity", "onDataChange:ds "+ds.child("price")+"--END--");
                            totalPrice+=Integer.parseInt(ds.child("price").getValue(String.class));
                            Log.d("cart_activity", "onDataChange:p "+p.getPrice()+"--END--");
                            checkoutBtn.setText(String.valueOf("Check out "+"Order total:"+totalPrice+" EGP"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public cartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_layout,viewGroup,false);
                cartViewHolder holder=new cartViewHolder(view);

                return holder;
            }
        };
        recyclerView.setAdapter(adabter);
        adabter.startListening();


    }
    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services verison");
        int availble=GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(cartActivity.this);
        if (availble==ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "isServicesOk: google play services working fine");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availble))
        {

            Log.d(TAG, "isServicesOk: an error occured ");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(cartActivity.this,availble,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(), "can't make map request", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}
