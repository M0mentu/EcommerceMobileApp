package com.example.momentu.ecommercemobileapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
public class gamingFragment extends Fragment {
    RecyclerView recyclerView;
    private DatabaseReference productRef;
    RecyclerView.LayoutManager layoutManager;


    public gamingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_gaming,container,false);

        recyclerView=rootView.findViewById(R.id.recycler_gaming);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        productRef=FirebaseDatabase.getInstance().getReference().child("products");

        Paper.init(rootView.getContext());




        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<prodcuts> options=new FirebaseRecyclerOptions.Builder<prodcuts>().setQuery(productRef.orderByChild("category").equalTo("gamingCat"),prodcuts.class).build();

        final FirebaseRecyclerAdapter<prodcuts,ProductViewHolder> adabter= new FirebaseRecyclerAdapter<prodcuts, ProductViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull prodcuts model) {

             final   String productKey=getRef(position).getKey();  //getting the key of the item position




                    holder.etProductName.setText(model.getName());
                    holder.etProductyDesc.setText(model.getDescription());
                    holder.getEtProductPrice.setText(model.getPrice()+" EGP");
                    holder.etProductQuantity.setText("Quantity "+model.getQuantity());
                    Picasso.get().load(model.getImage()).into(holder.imageView);


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent clickProductIntent=new Intent(getActivity(),product_details_Activity.class);
                            clickProductIntent.putExtra("postKey",productKey);
                            startActivity(clickProductIntent);
                        }
                    });

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

}
