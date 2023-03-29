package edu.northeastern.myapplication.LoginRegister;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
        //gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
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
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    // Check if the user has an account already in the database
                    String email = account.getEmail();

                    System.out.println(email);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    System.out.println("HERE1");
                    if (user != null) {
                        System.out.println("HERE2");

//                        System.out.println(user.getEmail());
//                        String uid = user.getUid();
//                        System.out.println("user is signed in!");
                        // User is already signed in, navigate to the second activity
                    } else {
                        System.out.println("HERE3");
                        // handle the case where no user is signed in
                        // Create a new Firebase user account for the signed-in user
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        System.out.println("hereeeee");
                                        // User account created successfully, navigate to the second activity
                                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = user1.getUid();
                                        Log.d(TAG, "User created with UID: " + uid);
                                    } else {
                                        // User account creation failed, display an error message
                                        Log.w(TAG, "User account creation failed", task.getException());

                                    }
                                });
                    }
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
        }
        else {
            // everything checks out
            mAuth.signInWithEmailAndPassword(email, passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Real User!", Toast.LENGTH_LONG).show();
                        System.out.println(mAuth.getCurrentUser().getEmail());
                        System.out.println(mAuth.getCurrentUser().getUid());
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