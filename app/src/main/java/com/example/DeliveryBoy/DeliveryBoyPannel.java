package com.example.DeliveryBoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ApplicationAdmin.AllDelieveryBoys;
import com.example.Model.AdminOrders;
import com.example.Prevalent.Prevalent;
import com.example.RestaurantOwner.AdminUserProductsActivity;
import com.example.RestaurantOwner.OwnerNewOrdersActivity;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class DeliveryBoyPannel extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    String dBoyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_pannel);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("RequestToRider");


        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        dBoyID = Prevalent.currentOnlineUser.getPhone().trim();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("dID").equalTo(dBoyID),  AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, DeliveryBoyPannel.AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, DeliveryBoyPannel.AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DeliveryBoyPannel.AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhoneNumber.setText("Phone: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount =  RS" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at: " + model.getDate() + "  " + model.getTime());
                        holder.userShippingAddress.setText("Shipping Address: " + model.getAddress() + ", " + model.getCity());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(DeliveryBoyPannel.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryBoyPannel.this);
                                builder.setTitle("Do You Shipped this order ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if (i == 0)
                                        {
                                            String uID = getRef(position).getKey();

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Cart List")
                                                    .child("Admin View")
                                                    .child(uID)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            Toast.makeText(DeliveryBoyPannel.this, "item deleted form Admin View", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Orders")
                                                    .child(uID)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(DeliveryBoyPannel.this, "item Deleted form Orders", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("RequestToRider")
                                                    .child(uID)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(DeliveryBoyPannel.this, "Order Shipped", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            HashMap<String, Object> ordersMap = new HashMap<>();

                                            ordersMap.put("state", "Shipped");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Orders")
                                                    .child("UserView")
                                                    .child(uID)
                                                    .updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(DeliveryBoyPannel.this, "Status Updated Successfully. ", Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                        }
                                        else
                                        {
                                            Toast.makeText(DeliveryBoyPannel.this, "Order Not Shipped", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DeliveryBoyPannel.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();



    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button ShowOrdersBtn;


        public AdminOrdersViewHolder(View itemView)
        {
            super(itemView);


            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    public void Logout(View view) {

        Paper.book().destroy();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}