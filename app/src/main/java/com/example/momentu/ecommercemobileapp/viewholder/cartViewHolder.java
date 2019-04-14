package com.example.momentu.ecommercemobileapp.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.momentu.ecommercemobileapp.R;
import com.example.momentu.ecommercemobileapp.interfaces.itemClickListener;

public class cartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView ctProductName,ctProductyDesc,ctProductQuantity,getcartEtProductPrice,counter;
    public ImageView imageView,plusview,minusview;
    private itemClickListener listener;
   public Button removebt;

    public cartViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView)itemView.findViewById(R.id.layoutCart_Product_image);
        plusview=(ImageView)itemView.findViewById(R.id.cartcounterplus);
        minusview=(ImageView)itemView.findViewById(R.id.cartcounterminus);
        counter=(TextView)itemView.findViewById(R.id.cartcounter);

        ctProductName=(TextView)itemView.findViewById(R.id.layoutCart_Product_name);
        ctProductyDesc=(TextView) itemView.findViewById(R.id.layoutCart_Product_description);
        ctProductQuantity=(TextView) itemView.findViewById(R.id.layoutCart_Product_quantity);
        getcartEtProductPrice=(TextView) itemView.findViewById(R.id.layoutCart_Product_price);
        removebt=(Button)itemView.findViewById(R.id.remove_item_cart);



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
