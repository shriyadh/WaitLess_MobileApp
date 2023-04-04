package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.Profile.MainActivityProfile;
import edu.northeastern.myapplication.discoverpage.Discover;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void discover(View view) {
        Intent intent = new Intent(this, Discover.class);
        startActivity(intent);
    }

    // TODO: Remove when done testing
    /**
     * This method is called when the user clicks the "My Profile" button
     */
    public void myProfile(View view) {
        Intent intent = new Intent(this, MainActivityProfile.class);
        startActivity(intent);
    }

}