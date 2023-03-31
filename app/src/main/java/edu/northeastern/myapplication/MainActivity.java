package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.LoginRegister.Register;
import edu.northeastern.myapplication.Profile.MainActivityProfile;
import edu.northeastern.myapplication.discoverpage.Discover;
import edu.northeastern.myapplication.queue.QR_Scanner;
import edu.northeastern.myapplication.queue.Queue_home;

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
    public void login(View view) {
        Intent intent = new Intent(this, Register.class);
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

    public void queue(View view) {
        Intent intent = new Intent(this, Queue_home.class);
        startActivity(intent);
    }

}