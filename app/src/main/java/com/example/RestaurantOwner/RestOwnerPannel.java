package com.example.RestaurantOwner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.Customer.CustomerRestMenu;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;

import io.paperdb.Paper;

public class RestOwnerPannel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_owner_pannel);
    }

    public void addNewproducts(View view) {
        Intent intent = new Intent(this,OwnerAddNewProduct.class);
        startActivity(intent);
    }

    public void maintainProdcutsButton(View view) {

        Intent intent = new Intent(this, OwnerRestMenu.class);
        startActivity(intent);

    }

    public void checkNewOrdersBtn(View view) {

        Intent intent = new Intent(this, OwnerNewOrdersActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logout();
    }

    private void logout() {
        Paper.book().destroy();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}