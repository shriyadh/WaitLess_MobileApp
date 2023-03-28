package edu.northeastern.myapplication.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Authenticator;

import edu.northeastern.myapplication.R;
public class Register extends AppCompatActivity {
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordConfirm;

    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        // find all the edit texts
//        inputEmail = findViewById(R.id.user_email);
//        inputPassword = findViewById(R.id.user_password);
//        inputPasswordConfirm = findViewById(R.id.user_password_confirm);
//        System.out.println("here");
//        registerButton = findViewById(R.id.register_button);
//
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Authenticate();
//            }
//        });

    }
//    public void Authenticate(){
//        String email = inputEmail.getText().toString();
//        String passwrd = inputPassword.getText().toString();
//        String passwrdConfirm = inputPasswordConfirm.getText().toString();
//
//        // validate
//        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "Email Verified !", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Enter valid Email address !", Toast.LENGTH_SHORT).show();
//        }
//    }



}