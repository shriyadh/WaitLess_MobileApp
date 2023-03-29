package edu.northeastern.myapplication.LoginRegister;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.myapplication.R;

public class Login extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button googleButton;

    EditText inputEmail;
    EditText inputPassword;

    Button loginButton;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    // ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleButton = findViewById(R.id.btn_google);

        inputEmail = findViewById(R.id.user_email);
        inputPassword = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.login_button);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        //Intent i = new Intent(Login.this, GoogleLogin.class);
        //System.out.println(" created intent");
        //startActivity(i);
        //Intent signInIntent = gsc.getSignInIntent();
        // startActivityForResult(signInIntent,1000);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    task.getResult(ApiException.class);
                    //it works
                    //naviagte to second actvity
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(),"cant",Toast.LENGTH_LONG);
                    throw new RuntimeException(e);
                }
                // Handle the result data here
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = gsc.getSignInIntent();
                launcher.launch(i);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authenticate();
            }
        });
    }

    public void Authenticate() {
        Toast.makeText(Login.this, "Here!", Toast.LENGTH_LONG).show();
        System.out.println("in authen");
        //GET THE INPUTS FROM THE USER
        String email = inputEmail.getText().toString();
        String passwrd = inputPassword.getText().toString();

        // validate https://www.geeksforgeeks.org/implement-email-validator-in-android/
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //check to make sure email is valid
            inputEmail.setError("Please enter valid email address");
            //Toast.makeText(this,"great!", Toast.LENGTH_LONG).show();
        } else if (passwrd.isEmpty()) {
            // check to make sure password is not empty
            inputEmail.setError("Please enter a valid email address!");
        } else {

            // everything checks out
            mAuth.signInWithEmailAndPassword(email, passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Real User!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(Login.this, "UNReal User!", Toast.LENGTH_LONG).show();

                    }
                }
            });


        }
    }

    public void createAccount(View v) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

}