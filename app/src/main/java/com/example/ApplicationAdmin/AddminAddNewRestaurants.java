package com.example.ApplicationAdmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.example.eatathome.RegisterActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddminAddNewRestaurants extends AppCompatActivity {

    private EditText restName, restOwnerName, restOwnerPhone, restOwnerPassword, restAddress, restCity;

    private String inputResName, inputRestOwnerName, inputRestOwnerPhone, inputResOwnerPassword,
            inputRestAddress, inputRestCity, saveCurrentDate, saveCurrentTime;
    private ImageView inputRestImage;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String restaurantRandomKey, downloadImageUrl;
    private StorageReference restaurantImagesRef;
    private DatabaseReference restaurantRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmin_add_new_restaurants);

        restName = findViewById(R.id.rest_name);
        restOwnerPhone = findViewById(R.id.rest_owner_phone);
        restOwnerPassword = findViewById(R.id.rest_owner_password);
        restOwnerName = findViewById(R.id.rest_owner_name);
        restAddress= findViewById(R.id.rest_address);
        restCity = findViewById(R.id.rest_city);
        inputRestImage = findViewById(R.id.select_rest_image);
        loadingBar = new ProgressDialog(this);

        restaurantImagesRef = FirebaseStorage.getInstance().getReference().child("Restaurant Images");
        restaurantRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");

        inputRestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public void addNewRest(View view) {

        validateRestData();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imageUri = data.getData();
            inputRestImage.setImageURI(imageUri);
        }
    }

    private void validateRestData() {
        inputResName = restName.getText().toString();
        inputRestOwnerName = restOwnerName.getText().toString();
        inputRestOwnerPhone = restOwnerPhone.getText().toString();
        inputResOwnerPassword = restOwnerPassword.getText().toString();
        inputRestAddress = restAddress.getText().toString();
        inputRestCity = restCity.getText().toString();


        if (imageUri == null)
        {
            Toast.makeText(this, "Restaurant image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(inputResName))
        {
            restName.setError("Please enter restaurant name");
        }
        else if (TextUtils.isEmpty(inputRestOwnerName))
        {
            restOwnerName.setError("Please enter restaurant Owner name");
        }
        else if (TextUtils.isEmpty(inputRestOwnerPhone))
        {
            restName.setError("Please enter restaurant owner phone number");
        }
        else if (TextUtils.isEmpty(inputResOwnerPassword))
        {
            restName.setError("Please enter restaurant owner password");
        }
        else if (TextUtils.isEmpty(inputRestAddress))
        {
            restName.setError("Please enter restaurant address");
        }
        else if (TextUtils.isEmpty(inputRestCity))
        {
            restName.setError("Please enter restaurant City");
        }
        else
        {
            storeProductInformation();
        }
    }

    private void storeProductInformation() {

        loadingBar.setTitle("Add New Restaurant");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new Restaurant.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        restaurantRandomKey = saveCurrentDate + " " + saveCurrentTime;

        DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Restaurants")).child(inputRestOwnerPhone).exists())
                {
                    final StorageReference filePath = restaurantImagesRef.child(imageUri.getLastPathSegment() + restaurantRandomKey + ".jpg");

                    final UploadTask uploadTask = filePath.putFile(imageUri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            String message = e.toString();
                            Toast.makeText(AddminAddNewRestaurants.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(AddminAddNewRestaurants.this, "Restaurant Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                                {
                                    if (!task.isSuccessful())
                                    {
                                        throw task.getException();
                                    }

                                    downloadImageUrl = filePath.getDownloadUrl().toString();
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        downloadImageUrl = task.getResult().toString();

                                        Toast.makeText(AddminAddNewRestaurants.this, "got the Restaurant image Url Successfully...", Toast.LENGTH_SHORT).show();

                                        saveRestaurantInfoToDatabase();
                                    }
                                }
                            });
                        }
                    });

                }
               else
                {
                    Toast.makeText(AddminAddNewRestaurants.this, "This " + inputRestOwnerPhone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(AddminAddNewRestaurants.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(AddminAddNewRestaurants.this, ApplicationAdminPannel.class);
//                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void saveRestaurantInfoToDatabase() {

         HashMap<String, Object> restaurantMap = new HashMap<>();
        restaurantMap.put("rid", restaurantRandomKey);
        restaurantMap.put("date", saveCurrentDate);
        restaurantMap.put("time", saveCurrentTime);
        restaurantMap.put("name", inputResName);
        restaurantMap.put("image", downloadImageUrl);
        restaurantMap.put("ownerName", inputRestOwnerName);
        restaurantMap.put("phone", inputRestOwnerPhone);
        restaurantMap.put("password", inputResOwnerPassword);
        restaurantMap.put("address", inputRestAddress);
        restaurantMap.put("city", inputRestCity);

        restaurantRef.child(inputRestOwnerPhone).updateChildren(restaurantMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AddminAddNewRestaurants.this, ApplicationAdminPannel.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddminAddNewRestaurants.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddminAddNewRestaurants.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}