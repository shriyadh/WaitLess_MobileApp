package edu.northeastern.myapplication.queue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.R;

public class Queue_home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_home);
    }

    public void qr_scanner(View view) {
        Intent intent = new Intent(this, QR_Scanner.class);
        startActivity(intent);
    }
}