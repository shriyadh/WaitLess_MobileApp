package edu.northeastern.myapplication.LoginRegister;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.Discover;


public class Login extends AppCompatActivity {
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button googleButton;

    EditText inputEmail;
    EditText inputPassword;

    Button loginButton;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    // ProgressBar progressBar;

    boolean usernameCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    account = task.getResult(ApiException.class);

                    // Check if the user has an account already in the database
                    String email = account.getEmail();

                    System.out.println(email);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    System.out.println("HERE1");
                    if (user != null) {
                        System.out.println("HERE2");

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


//                                        // add to the database
                                          addGoogleProfile();


                                    } else {
                                        // User account creation failed, display an error message
                                        Log.w(TAG, "User account creation failed", task.getException());

                                    }
                                });
                    }
                } catch (ApiException e) {
                  //  Toast.makeText(getApplicationContext(),"cant",Toast.LENGTH_LONG);
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

    // Check if user is already logged in

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
           // Toast.makeText(Login.this, "Already logged in, yay!!", Toast.LENGTH_LONG).show();
            // send user to the discover page
            Intent discoverIntent = new Intent(getApplicationContext(), Discover.class);
            startActivity(discoverIntent);
        }
        else{
          //  Toast.makeText(Login.this, "NOT LOGGED IN", Toast.LENGTH_LONG).show();
        }
    }

    public void askUsername(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(R.layout.signin);

        // Add the buttons
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // check to make sure username is fine
                usernameCreated = true;

            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //still false
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void Authenticate() {
        // Toast.makeText(Login.this, "Here!", Toast.LENGTH_LONG).show();
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
                       // Toast.makeText(Login.this, "Real User!", Toast.LENGTH_LONG).show();
                        System.out.println(mAuth.getCurrentUser().getEmail());
                        System.out.println(mAuth.getCurrentUser().getUid());
                        // send user to the discover page
                        Intent discoverIntent = new Intent(getApplicationContext(), Discover.class);
                        startActivity(discoverIntent);
                    } else {
                        Toast.makeText(Login.this, "This user does not exist!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void createAccount(View v) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    public void addGoogleProfile(){
        // grab the current user's id, join date,  email;
        FirebaseUser userGoogle = FirebaseAuth.getInstance().getCurrentUser();
        String email = userGoogle.getEmail();
        String uid = userGoogle.getUid();
        String user = userGoogle.getDisplayName();
        Date creationDate = new Date(userGoogle.getMetadata().getCreationTimestamp());
        String dateJoined = String.valueOf(creationDate);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); // get the database ref
        DatabaseReference userName = rootRef.child("profiles").child(uid); // check for user uid

        // user doesnt exist so add to database
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
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
                    uidRef.child("profileName").setValue(user);

                    // add to email
                    uidRef.child("profileEmail").setValue(email);

                    // add to friends
                    uidRef.child("friends").setValue("");

                    // add default lifted
                    uidRef.child("totalLifted").setValue(0);

                    // add workouts node
                    uidRef.child("workouts");

                    //workout count
                    uidRef.child("workout_count").setValue(0);

                    // friends count
                    uidRef.child("friends_count").setValue(0);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("tag", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userName.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable back button
        Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show();
    }



}