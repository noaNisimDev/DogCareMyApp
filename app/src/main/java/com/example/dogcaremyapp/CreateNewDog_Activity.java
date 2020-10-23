package com.example.dogcaremyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class CreateNewDog_Activity extends AppCompatActivity {
    private String TAG = "CreateNewDog_Activity";
    private Button createnewdog_BTN_Adddog;
    private TextInputLayout name;
    private TextInputLayout numOfWalksPerDay;
    private TextInputLayout numOfMealsPerDay;
    private ImageView image;
    private TextView createnewdog_TXT_later;
    private static int RESULT_LOAD_IMG = 1;
    private Uri imageUri = null;
    private Uri defaultDogImageUri = null;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.createnewdog_activity);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mStorageRef.child("img_defaultDogImg.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                defaultDogImageUri = uri; /// The string(file link) that you need
                Log.d(TAG, "onSuccess: the uri is: " + uri);

            }
        });
        findViews();


        createnewdog_BTN_Adddog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int walksPerDay = Integer.valueOf(numOfWalksPerDay.getEditText().getText().toString());
                final int mealsPerDay = Integer.valueOf(numOfMealsPerDay.getEditText().getText().toString());

                if (imageUri != null) {
                    mStorageRef.child(UUID.randomUUID().toString()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Dog dog = new Dog(name.getEditText().getText().toString(), walksPerDay,
                                            mealsPerDay, uri.toString());
                                    addDogFirebase(dog);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Image Uploade", "onFailure: " + e.getMessage());
                        }
                    });
                } else {
                    Dog dog = new Dog(name.getEditText().getText().toString(), walksPerDay,
                            mealsPerDay, defaultDogImageUri.toString());
                    addDogFirebase(dog);


                }
                launchActivity(Main_Activity.class);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        createnewdog_TXT_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(Main_Activity.class);
            }
        });

        createnewdog_BTN_Adddog.setEnabled(false);
        createnewdog_BTN_Adddog.setAlpha(0.5f);

        name.getEditText().addTextChangedListener(watcher);
        numOfWalksPerDay.getEditText().addTextChangedListener(watcher);
        numOfMealsPerDay.getEditText().addTextChangedListener(watcher);
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void launchActivity(Class Main_Activity) {

        Intent intent = new Intent(this, Main_Activity);
        startActivity(intent);
    }

    private void findViews() {
        createnewdog_BTN_Adddog = findViewById(R.id.createnewdog_BTN_Adddog);
        name = findViewById(R.id.createnewdog_TXT_dogname);
        numOfWalksPerDay = findViewById(R.id.createnewdog_TXT_walks);
        numOfMealsPerDay = findViewById((R.id.createnewdog_TXT_meals));
        image = findViewById(R.id.createnewdog_IMG_profile);
        createnewdog_TXT_later = findViewById(R.id.createnewdog_TXT_later);

    }

    private void addDogFirebase(Dog dog) {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dogs").child(dog.getId()).setValue(dog.getId());
        mDatabase.child("dogs").child(dog.getId()).setValue(dog);
    }

    private final TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (name.getEditText().getText().toString().length() > 0 &&
                    numOfWalksPerDay.getEditText().getText().toString().length() > 0 &&
                    numOfMealsPerDay.getEditText().getText().toString().length() > 0) {

                if (Integer.parseInt(numOfWalksPerDay.getEditText().getText().toString()) > 0 &&
                        Integer.parseInt(numOfWalksPerDay.getEditText().getText().toString()) <= 10 &&
                        Integer.parseInt(numOfMealsPerDay.getEditText().getText().toString()) > 0 &&
                        Integer.parseInt(numOfMealsPerDay.getEditText().getText().toString()) <= 10) {
                            createnewdog_BTN_Adddog.setAlpha(1f);
                            createnewdog_BTN_Adddog.setEnabled(true);
                }
                else{
                    createnewdog_BTN_Adddog.setAlpha(0.5f);
                    createnewdog_BTN_Adddog.setEnabled(false);

                }
            }
            else{
                createnewdog_BTN_Adddog.setAlpha(0.5f);
                createnewdog_BTN_Adddog.setEnabled(false);
            }
        }
    };
}