package edu.northeastern.myapplication.Profile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.UUID;

import edu.northeastern.myapplication.LoginRegister.Login;
import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.R;

public class EditProfile extends AppCompatActivity {
    private Profile currentProfile;
    private ImageView profileIcon;
    private Button saveButton, cancelButton;
    private EditText editUserName, editBio;
    private String profileId;
    private String currProfileId;

    public Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Disable rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        //checking logged in user
        System.out.println(mUser.getEmail());

        // find navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set selected item to queue
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        // activate nav listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationHandler(this));



        Intent intent = getIntent();
        profileId = intent.getStringExtra("profileId");
        currProfileId = intent.getStringExtra("currProfileId");

        editUserName = findViewById(R.id.editTextUserName);
        editBio = findViewById(R.id.editTextBio);
        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);
        profileIcon = findViewById(R.id.editProfileIcon);
        profileIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_account_box_24));


        loadProfile();

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            imageUri = result;
                            profileIcon.setImageURI(imageUri);
                            uploadtoFirebase();
                        }
                    }
                });
    }

    public void loadProfile() {
        // TODO: change pid to unique profile id
        new Thread(() -> {
            DatabaseReference profileRef = FirebaseDatabase
                    .getInstance()
                    .getReference("profiles/" + profileId); // TODO: Replace with user's profile id
            profileRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentProfile = snapshot.getValue(Profile.class);
                    loadProfileImage();
                    loadProfileTextData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Profile", "Failed to read value.", error.toException());
                }
            });
        }).start();
    }

    public void loadProfileTextData() {
        editUserName.setText(currentProfile.getProfileName());
        editBio.setText(currentProfile.getProfileBio());
    }

    public void loadProfileImage() {
        new Thread(() -> {
            // Get profile icon from Firebase Storage and set it to the image view using Glide
            FirebaseStorage
                    .getInstance()
                    .getReference(  "/profileIcons/")
                    .child(profileId + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.baseline_account_box_24)
                            .override(275, 275)
                            .apply(new RequestOptions()
                                    .transform(new CenterCrop(),
                                            new RoundedCorners(50)))
                            .into(profileIcon)).addOnFailureListener(e ->
                            Log.w("Profile", "Failed to load profile image", e));
        }).start();
    }



    public void saveUpdates(View view){
        String newUsername = editUserName.getText().toString();
        String newBio = editBio.getText().toString();

        new Thread(()->{
            // update profile info into database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            // TODO: change path to unique user id
            DatabaseReference profileRef = database.getReference("profiles/" + profileId);
            profileRef.child("profileName").setValue(newUsername);
            profileRef.child("profileBio").setValue(newBio);
        }).start();

        Intent intent = new Intent(EditProfile.this, MainActivityProfile.class);
        startActivity(intent);
        finish();
    }


    public void cancelUpdate(View view){
        Intent intent = new Intent(EditProfile.this, MainActivityProfile.class);
        startActivity(intent);
        finish();
    }

    public void logout(View view){
        mAuth.signOut();
        Intent intent = new Intent(EditProfile.this, Login.class);
        startActivity(intent);
        finish();
    }



    public void uploadNewProfile(View view){
        mGetContent.launch("image/*");
    }

    public void uploadtoFirebase(){
        String picKey = profileId;
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Image Uploading...");
        pd.show();

        StorageReference storageRef = FirebaseStorage
                .getInstance()
                .getReference("/profileIcons")
                .child(picKey + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        String message = e.getMessage();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progess: " + (int)progressPercent + "%");
                    }
                });
    }

}
