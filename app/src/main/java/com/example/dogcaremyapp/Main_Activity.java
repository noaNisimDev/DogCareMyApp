package com.example.dogcaremyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Main_Activity extends AppCompatActivity {

    private Button main_BTN_left;
    private Button main_BTN_center;
    private Button main_BTN_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.main_activity);

        findViews();

        main_BTN_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity1(CreateNewDog_Activity.class);
            }
        });

        main_BTN_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity2(Share_Activity.class);
            }
        });
    }

    //from add new dog button to CreateNewDog_Activity
    private void launchActivity1(Class CreateNewDog_Activity) {

        Intent intent = new Intent(this, CreateNewDog_Activity);
        startActivity(intent);
    }

    //from share button to CreateNewDog_Activity
    private void launchActivity2(Class Share_Activity) {

        Intent intent = new Intent(this, Share_Activity);
        startActivity(intent);
    }



    private void findViews(){
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_center = findViewById(R.id.main_BTN_center);
        main_BTN_right = findViewById(R.id.main_BTN_right);
    }
}