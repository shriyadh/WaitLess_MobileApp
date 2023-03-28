package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    public void queue(View view) {
        Intent intent = new Intent(this, Queue_home.class);
        startActivity(intent);
    }

}