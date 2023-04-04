package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RequestsActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests2);
        // find navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set selected item to queue
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);
        // activate nav listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationHandler(this));


    }
}