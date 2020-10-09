package com.example.dogcaremyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Login_Activity extends AppCompatActivity {

    private Button login_BTN_loginregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.login_activity);

        findViews();

        login_BTN_loginregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(CreateNewDog_Activity.class);
            }
        });


    }

    private void launchActivity(Class CreateNewDog_Activity) {

        Intent intent = new Intent(this, CreateNewDog_Activity);
        startActivity(intent);
    }

    private void findViews(){
        login_BTN_loginregister = findViewById(R.id.login_BTN_loginregister);
    }


}