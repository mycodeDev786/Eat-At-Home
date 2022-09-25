package com.example.ApplicationAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.Customer.ConfirmFinalOrderActivity;
import com.example.Customer.CustomerRestMenu;
import com.example.Customer.HomeActivity;
import com.example.Model.DeliveryBoy;
import com.example.Model.Restaurants;
import com.example.Prevalent.Prevalent;
import com.example.RestaurantOwner.RestOwnerPannel;
import com.example.ViewHolder.DeliveryBoyViewHolder;
import com.example.ViewHolder.RestaurantViewHolder;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.paperdb.Paper;

public class AllDelieveryBoys extends AppCompatActivity {

    private DatabaseReference restaurantRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String delID, type = "abc";

    private String cName, cAddress, cCity, cPhone, oDate, restID, oState, oTime, oTotalAmount, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_delievery_boys);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type = getIntent().getStringExtra("owner");
            cName = getIntent().getStringExtra("name");
            cAddress = getIntent().getStringExtra("address");
            cCity = getIntent().getStringExtra("city");
            cPhone = getIntent().getStringExtra("phone");
            oDate = getIntent().getStringExtra("date");
            restID = getIntent().getStringExtra("rID");
            oState = getIntent().getStringExtra("state");
            oTime = getIntent().getStringExtra("time");
            oTotalAmount = getIntent().getStringExtra("totalAmount");
            userID = getIntent().getStringExtra("uid");

        }



        //type = getIntent().getStringExtra("owner");

        restaurantRef = FirebaseDatabase.getInstance().getReference().child("Delivery Boy");
        recyclerView = findViewById(R.id.rv_dlboy);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<DeliveryBoy> options =
                new FirebaseRecyclerOptions.Builder<DeliveryBoy>()
                        .setQuery(restaurantRef, DeliveryBoy.class)
                        .build();


        FirebaseRecyclerAdapter<DeliveryBoy, DeliveryBoyViewHolder> adapter =
                new FirebaseRecyclerAdapter<DeliveryBoy, DeliveryBoyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DeliveryBoyViewHolder holder, final int position, @NonNull final DeliveryBoy model)
                    {
                        holder.txtRestName.setText(model.getName());
                        holder.txtRestAddress.setText(model.getAddress());
                        holder.txtRestCity.setText(model.getCity());
                        Picasso.get().load(model.getImage()).into(holder.imageView);




                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                if (type.equals("Owner"))
                                {

                                    delID = getRef(position).getKey();
                                    requestToDelBoy();

                                }


                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DeliveryBoyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_restaurants_layout, parent, false);
                        DeliveryBoyViewHolder holder = new DeliveryBoyViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void requestToDelBoy() {

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("RequestToRider")
                .child(userID);

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", oTotalAmount);
        ordersMap.put("name", cName);
        ordersMap.put("phone", cPhone);
        ordersMap.put("address", cAddress);
        ordersMap.put("city", cCity);
        ordersMap.put("date", oDate);
        ordersMap.put("rID", restID);
        ordersMap.put("time", oTime);
        ordersMap.put("state", "On the way");
        ordersMap.put("dID", delID);

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    // removing order from User view
                    FirebaseDatabase.getInstance().getReference()
                            .child("Orders")
                            .child(userID)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(AllDelieveryBoys.this, "Request sent to Delivery Boy ", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AllDelieveryBoys.this, RestOwnerPannel.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

}