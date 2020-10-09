package com.example.dogcaremyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Share_Activity extends AppCompatActivity {

    private Button createnewdog_BTN_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.share_activity);

        findViews();

        createnewdog_BTN_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(Main_Activity.class);
            }
        });
    }

    private void launchActivity(Class Main_Activity) {

        Intent intent = new Intent(this, Main_Activity);
        startActivity(intent);
    }

    private void findViews() {
        createnewdog_BTN_share = findViewById(R.id.createnewdog_BTN_share);
    }
}