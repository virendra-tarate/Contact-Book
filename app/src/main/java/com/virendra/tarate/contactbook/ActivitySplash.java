package com.virendra.tarate.contactbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ActivitySplash extends AppCompatActivity {

    long time = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //New Runnable Method
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Going from SplashScreen to Main Screen
                Intent mySplash = new Intent(ActivitySplash.this, MainActivity.class);
                startActivity(mySplash);
                //Finish Stack of a Activity
                finish();
            }
        }, time);


    }
}