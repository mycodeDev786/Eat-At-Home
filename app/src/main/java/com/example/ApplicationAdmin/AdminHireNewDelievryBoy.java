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

import com.example.eatathome.R;
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

public class AdminHireNewDelievryBoy extends AppCompatActivity {

    private EditText delboyName, delboyPhone, delboyPassword, delboyRegion, delboyCity;

    private String inputdelboyName, inputdelboyPhone, inputdelboyPassword,
            inputdelboyRegion, inputdelboyCity, saveCurrentDate, saveCurrentTime;
    private ImageView inputdelboyImage;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String delboyRandomKey, downloadImageUrl;
    private StorageReference delboyImagesRef;
    private DatabaseReference delboyRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hire_new_delievry_boy);

        delboyName = findViewById(R.id.delboy_name);
        delboyPhone = findViewById(R.id.delboy_phone);
        delboyPassword = findViewById(R.id.delboy_password);
        delboyRegion= findViewById(R.id.delboy_region);
        delboyCity = findViewById(R.id.delboy_city);
        inputdelboyImage = findViewById(R.id.select_delBoy_image);
        loadingBar = new ProgressDialog(this);

        delboyImagesRef = FirebaseStorage.getInstance().getReference().child("Delivery Boy Images");
        delboyRef = FirebaseDatabase.getInstance().getReference().child("Delivery Boy");

        inputdelboyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public void addnewdelboy(View view) {
        validatedellboyData();
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
            inputdelboyImage.setImageURI(imageUri);
        }
    }

    private void validatedellboyData() {

        inputdelboyName = delboyName.getText().toString();
        inputdelboyPhone = delboyPhone.getText().toString();
        inputdelboyPassword = delboyPassword.getText().toString();
        inputdelboyRegion = delboyRegion.getText().toString();
        inputdelboyCity = delboyCity.getText().toString();


        if (imageUri == null)
        {
            Toast.makeText(this, " image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(inputdelboyName))
        {
            delboyName.setError("Please enter delivery boy's name");
        }
        else if (TextUtils.isEmpty(inputdelboyPhone))
        {
            delboyPhone.setError("Please enter  phone number");
        }
        else if (TextUtils.isEmpty(inputdelboyPassword))
        {
            delboyPassword.setError("Please enter password");
        }
        else if (TextUtils.isEmpty(inputdelboyRegion))
        {
            delboyRegion.setError("Please enter Region");
        }
        else if (TextUtils.isEmpty(inputdelboyCity))
        {
            delboyCity.setError("Please enter City");
        }
        else
        {
            storedelboyInformation();
        }
    }

    private void storedelboyInformation() {

        loadingBar.setTitle("Add New Delivery");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new Delivery boy.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        delboyRandomKey = saveCurrentDate + " " + saveCurrentTime;

        DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Delivery Boy")).child(inputdelboyPhone).exists())
                {
                    final StorageReference filePath = delboyImagesRef.child(imageUri.getLastPathSegment() + delboyRandomKey + ".jpg");

                    final UploadTask uploadTask = filePath.putFile(imageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            String message = e.toString();
                            Toast.makeText(AdminHireNewDelievryBoy.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(AdminHireNewDelievryBoy.this, "Delivery boy Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

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

                                        Toast.makeText(AdminHireNewDelievryBoy.this, "got the image Url Successfully...", Toast.LENGTH_SHORT).show();

                                        savedelboyInfoToDatabase();
                                    }
                                }
                            });
                        }
                    });
                }
                else
                {
                    Toast.makeText(AdminHireNewDelievryBoy.this, "This " + inputdelboyPhone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(AdminHireNewDelievryBoy.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(AdminHireNewDelievryBoy.this, ApplicationAdminPannel.class);
//                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void savedelboyInfoToDatabase() {

        HashMap<String, Object> delboyMap = new HashMap<>();
        delboyMap.put("bid", delboyRandomKey);
        delboyMap.put("date", saveCurrentDate);
        delboyMap.put("time", saveCurrentTime);
        delboyMap.put("name", inputdelboyName);
        delboyMap.put("image", downloadImageUrl);
        delboyMap.put("phone", inputdelboyPhone);
        delboyMap.put("password", inputdelboyPassword);
        delboyMap.put("address", inputdelboyRegion);
        delboyMap.put("city", inputdelboyCity);

        delboyRef.child(inputdelboyPhone).updateChildren(delboyMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminHireNewDelievryBoy.this, ApplicationAdminPannel.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminHireNewDelievryBoy.this, "Hired is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminHireNewDelievryBoy.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}