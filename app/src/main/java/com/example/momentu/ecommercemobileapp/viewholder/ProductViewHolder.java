package com.example.momentu.ecommercemobileapp.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.momentu.ecommercemobileapp.R;
import com.example.momentu.ecommercemobileapp.interfaces.itemClickListener;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView etProductName,etProductyDesc,etProductQuantity,getEtProductPrice;
    public ImageView imageView;
    private  itemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView)itemView.findViewById(R.id.layout_Product_image);
        etProductName=(TextView)itemView.findViewById(R.id.layout_Product_name);
        etProductyDesc=(TextView) itemView.findViewById(R.id.layout_Product_description);
        etProductQuantity=(TextView) itemView.findViewById(R.id.layout_Product_quantity);
        getEtProductPrice=(TextView) itemView.findViewById(R.id.layout_Product_price);



    }
    public void setItemClickListener(itemClickListener listener)
    {

        this.listener=listener;
    }

    @Override
    public void onClick(View v) {

        listener.onClick(v,getAdapterPosition(),false);
    }
}
