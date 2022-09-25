package com.example.ApplicationAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Customer.HomeActivity;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;

import io.paperdb.Paper;

public class ApplicationAdminPannel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_admin_pannel);
    }



    public void logout(View view) {
        logout();

    }

    public void addNewRestaurant(View view) {
        Intent intent = new Intent(this, AddminAddNewRestaurants.class);
        startActivity(intent);
    }

    public void addNewDeliveryBoy(View view) {
        Intent intent = new Intent(this, AdminHireNewDelievryBoy.class);
        startActivity(intent);
    }

    private void logout() {
        Paper.book().destroy();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}