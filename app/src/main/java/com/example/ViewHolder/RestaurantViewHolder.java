package com.example.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Interface.ItemClickListner;
import com.example.eatathome.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRestName, txtRestAddress, txtRestCity;
    public ImageView imageView;
    public ItemClickListner listner;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.restaurant_image);
        txtRestName = itemView.findViewById(R.id.restaurant_name);
        txtRestAddress = itemView.findViewById(R.id.restaurant_address);
        txtRestCity = itemView.findViewById(R.id.restaurant_city);

    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {

        listner.onClick(view, getAdapterPosition(), false);

    }
}
