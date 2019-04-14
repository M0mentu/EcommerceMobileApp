package com.example.momentu.ecommercemobileapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class chartActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private String[] emails;
    private int[]orders;
    private int orderscount=0,i=0;
    String getprice,getuser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        firebaseAuth=firebaseAuth.getInstance();


        ref=FirebaseDatabase.getInstance().getReference().child("orders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderscount=0;
                i=0;

                orderscount=(int)dataSnapshot.getChildrenCount();
                orders=new int[orderscount];
                emails=new String[orderscount];

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    getprice=ds.child("price").getValue(String.class);
                    getuser=ds.child("user").getValue(String.class);
                    orders[i]=Integer.parseInt(getprice);
                    emails[i]=getuser;
                    i++;
                }
                seyupchart();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void seyupchart() {
        List<PieEntry> pieEntryList=new ArrayList<>();
        for (int i=0;i<orderscount;i++){
            pieEntryList.add(new PieEntry(orders[i],emails[i]));
        }
        PieDataSet dataSet=new PieDataSet(pieEntryList,"Order history for users");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data=new PieData(dataSet);

        PieChart chart=(PieChart)findViewById(R.id.piechartid);
        chart.setData(data);
        chart.invalidate();
    }
}
