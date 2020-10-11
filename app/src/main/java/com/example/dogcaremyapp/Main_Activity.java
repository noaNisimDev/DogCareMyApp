package com.example.dogcaremyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Main_Activity extends AppCompatActivity {

    private Button main_BTN_left;
    private Button main_BTN_center;
    private Button main_BTN_right;
    private Button main_BTN_logout;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.main_activity);

        findViews();

        main_BTN_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(CreateNewDog_Activity.class);
            }
        });

        main_BTN_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(Share_Activity.class);
            }
        });

        main_BTN_logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mAuth.signOut();
                //TODO - go to login screen
            }
        });
    }

    //from button to Activity
    private void launchActivity(Class Activity) {

        Intent intent = new Intent(this, Activity);
        startActivity(intent);
    }


    private void findViews(){
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_center = findViewById(R.id.main_BTN_center);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_BTN_logout = findViewById(R.id.main_BTN_logout);
    }
}