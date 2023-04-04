package edu.northeastern.myapplication.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Authenticator;
import java.util.Date;

import edu.northeastern.myapplication.R;
public class Register extends AppCompatActivity {
    EditText inputEmail;
    EditText inputUsername;
    EditText inputPassword;
    EditText inputPasswordConfirm;

    Button registerButton;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // find all the edit texts

        inputEmail = findViewById(R.id.user_email);
        inputPassword = findViewById(R.id.user_password);
        inputUsername = findViewById(R.id.username_input);
        inputPasswordConfirm = findViewById(R.id.user_password_confirm);
        System.out.println("here");
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authenticate();
            }
        });

    }
    public void Authenticate(){
        Toast.makeText(Register.this, "Here!",Toast.LENGTH_LONG).show();
        System.out.println("in authen");
        //GET THE INPUTS FROM THE USER
        String email = inputEmail.getText().toString();
        String passwrd = inputPassword.getText().toString();
        String passwrdConfirm = inputPasswordConfirm.getText().toString();

        // validate https://www.geeksforgeeks.org/implement-email-validator-in-android/
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //check to make sure email is valid
            inputEmail.setError("Please enter valid email address");
            //Toast.makeText(this,"great!", Toast.LENGTH_LONG).show();
        } else if (passwrd.isEmpty()){
            // check to make sure password is not empty
            inputEmail.setError("Please enter a valid email address!");
        }
        else if (!passwrd.equals(passwrdConfirm)){
            // check to make sure the passwords match th
            inputPasswordConfirm.setError("Passwords do not match!");
        }
        else {
            // https://www.geeksforgeeks.org/progressbar-in-android/
            //progressBar.setVisibility(View.VISIBLE);



            // everything checks out
            mAuth.createUserWithEmailAndPassword(email,passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "Registered!",Toast.LENGTH_LONG).show();
                        // store the user's info in the profiles table in the database
                        mUser = mAuth.getCurrentUser();
                        addProfile();
                        //sendToLogin(View);
                    }
                    else {
                        System.out.println("FAIL"+ task.getException());
                        Toast.makeText(Register.this, "Not Registered!" + task.getException(),Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
    }
    public void sendToLogin(View v){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    public void addProfile(){
        // grab the current user's id, join date,  email;
        System.out.println(mUser.getEmail());
        System.out.println(mUser.getUid());
        Date creationDate = new Date(mUser.getMetadata().getCreationTimestamp());
        System.out.println(creationDate);

        String email = mUser.getEmail();
        String uid = mUser.getUid();
        String dateJoined = String.valueOf(creationDate);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); // get the database ref
        DatabaseReference userName = rootRef.child("profiles").child(uid); // check for user uid
        // user doesnt exist so add to database
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    System.out.println("HERE");
                    //create new user
                    DatabaseReference profilesRef = rootRef.child("profiles");

                    // add a uid node
                    profilesRef.child(uid);

                    // Get a reference to the uid node under profiles
                    DatabaseReference uidRef = profilesRef.child(uid);

                    //add joinedDate node
                    uidRef.child("joinedDate").setValue(dateJoined);

                    // add default bio
                    uidRef.child("profileBio").setValue("Hello! I have just joined WaitLess!");

                    // add to username
                    uidRef.child("profileName").setValue(inputUsername.getText().toString());

                    // add to email
                    uidRef.child("profileEmail").setValue(email);

                    // add to friends
                    uidRef.child("friends").setValue("");

                    // add default lifted
                    uidRef.child("totalLifted").setValue(0);

                    // add workouts node
                    uidRef.child("workouts");

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("tag", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userName.addListenerForSingleValueEvent(eventListener);

    }


}