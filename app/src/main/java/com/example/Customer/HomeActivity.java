package com.example.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Model.Restaurants;
import com.example.Prevalent.Prevalent;
import com.example.ViewHolder.RestaurantViewHolder;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference restaurantRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String type = "user", rPhone;


    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type = getIntent().getExtras().get("admin").toString();
        }


        restaurantRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");


        Paper.init(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("All Restaurants");
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.navmenu1);
        drawerLayout = findViewById(R.id.nav_home_activity);


        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);


            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_cart:

                        Toast.makeText(HomeActivity.this, "cart", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                        intent.putExtra("rPhone", rPhone);
                        startActivity(intent);

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_search:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        intent = new Intent(HomeActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_orders:

                        Toast.makeText(HomeActivity.this, "Orders", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        intent = new Intent(HomeActivity.this, MyOrders.class);
                        startActivity(intent);
                        break;


                    case R.id.nav_settings:


                        intent = new Intent(HomeActivity.this, CustomerSettingActivity.class);
                        startActivity(intent);


                        Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_logout:

                            logout();
                       // Toast.makeText(HomeActivity.this, "logout", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                }

                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    intent.putExtra("rPhone", rPhone);
                    startActivity(intent);

            }
        });


        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Restaurants> options =
                new FirebaseRecyclerOptions.Builder<Restaurants>()
                        .setQuery(restaurantRef, Restaurants.class)
                        .build();


        FirebaseRecyclerAdapter<Restaurants, RestaurantViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurants, RestaurantViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull final Restaurants model)
                    {
                        holder.txtRestName.setText(model.getName());
                        holder.txtRestAddress.setText(model.getAddress());
                        holder.txtRestCity.setText(model.getCity());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        rPhone = model.getPhone();



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {


                                    Intent intent = new Intent(HomeActivity.this, CustomerRestMenu.class);
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void logout() {
        Paper.book().destroy();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}