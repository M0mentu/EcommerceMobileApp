package com.example.momentu.ecommercemobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.momentu.ecommercemobileapp.holdTheData.holdTheData;
import com.example.momentu.ecommercemobileapp.models.prodcuts;

import com.example.momentu.ecommercemobileapp.viewholder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;


public class shoppingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mRef;
    private DatabaseReference productRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    private static final String TAG = "Tl3ly_eli_by7sl";

    private StorageReference storageRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DrawerLayout drawer;
    private TextView usernameView;
    private FirebaseUser fbuser;
    private userData userdata;
    private String userID;
    private CircularImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

       /* recyclerView=findViewById(R.id.recycler_shopping);*//*
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/

        productRef=FirebaseDatabase.getInstance().getReference().child("products");
        Paper.init(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer=findViewById(R.id.shop_layout);
        firebaseAuth=firebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        mRef=database.getReference("users");
        fbuser=firebaseAuth.getCurrentUser();
        userID=fbuser.getUid();







        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview=navigationView.getHeaderView(0);


        usernameView=headerview.findViewById(R.id.usernameShopping);
        profileImage=headerview.findViewById(R.id.headerProfilePic);
        usernameView.setText(holdTheData.getCurerentOnlineUser.getUsername());
        Picasso.get().load(Uri.parse(holdTheData.imageURI)).into(profileImage);




        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new gamingFragment()).commit();
        navigationView.setCheckedItem(R.id.gamingMenuID);}
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.fashionMenuID:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new fashionFragment()).commit();
                break;
            case R.id.gamingMenuID:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new gamingFragment()).commit();
                break;
            case R.id.mobAndTabletsMenuID:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new mobileandtabletFragment()).commit();
                break;
            case R.id.electronics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new electronicsFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_goProfile:
                startActivity(new Intent(getApplicationContext(),profileActivity.class));
                break;
            case R.id.nav_cart:
                startActivity(new Intent(getApplicationContext(),cartActivity.class));

                break;
            case R.id.SearchMenuID:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new fragment_search()).commit();
                break;
            case R.id.nav_logOut:
            {
                Paper.book().destroy();
                Intent intent=new Intent(shoppingActivity.this,LoginActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/*

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<prodcuts> options=new FirebaseRecyclerOptions.Builder<prodcuts>().setQuery(productRef,prodcuts.class).build();
        FirebaseRecyclerAdapter<prodcuts,ProductViewHolder> adabter= new FirebaseRecyclerAdapter<prodcuts, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull prodcuts model) {
                holder.etProductName.setText(model.getName());
                holder.etProductyDesc.setText(model.getDescription());
                holder.getEtProductPrice.setText(model.getPrice()+" EGP");
                holder.etProductQuantity.setText("Quantity "+model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder.imageView);


            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_layout,viewGroup,false);
                ProductViewHolder holder=new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adabter);
adabter.startListening();

    }
*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

}
