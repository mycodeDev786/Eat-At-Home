package com.example.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Model.Restaurants;
import com.example.ViewHolder.RestaurantViewHolder;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        inputText = findViewById(R.id.search_product_name);
        SearchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));


        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SearchInput = inputText.getText().toString();

                onStart();
            }
        });
    }



    @Override
    protected void onStart()
    {
        super.onStart();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Restaurants");

        FirebaseRecyclerOptions<Restaurants> options =
                new FirebaseRecyclerOptions.Builder<Restaurants>()
                        .setQuery(reference.orderByChild("city").equalTo(SearchInput), Restaurants.class)
                        .build();

        FirebaseRecyclerAdapter<Restaurants, RestaurantViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurants, RestaurantViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull final Restaurants model)
                    {
                        holder.txtRestName.setText(model.getName());
                        holder.txtRestAddress.setText(model.getAddress());
                        holder.txtRestCity.setText( model.getCity());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(SearchActivity.this, CustomerRestMenu.class);
                                intent.putExtra("rName", model.getName());
                                intent.putExtra("rPhone", model.getPhone());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_restaurants_layout, parent, false);
                        RestaurantViewHolder holder = new RestaurantViewHolder(view);
                        return holder;
                    }
                };

        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}