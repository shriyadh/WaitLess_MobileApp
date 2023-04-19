package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.discoverpage.Discover;
import edu.northeastern.myapplication.notificationsPage.NotifyMe;

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

    public void notify(View view) {
        System.out.println("IN main");

        Intent intent = new Intent(this, NotifyMe.class);
        System.out.println("IN HER11E");

        startActivity(intent);
    }

}