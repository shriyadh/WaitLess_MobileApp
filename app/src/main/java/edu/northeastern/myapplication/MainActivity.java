package edu.northeastern.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.LoginRegister.Login;
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
    public void login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}