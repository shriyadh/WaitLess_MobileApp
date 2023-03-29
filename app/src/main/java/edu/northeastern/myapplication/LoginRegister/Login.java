package edu.northeastern.myapplication.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.myapplication.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void createAccount(View v){
        Intent i = new Intent(this,Register.class);
        startActivity(i);
    }
}