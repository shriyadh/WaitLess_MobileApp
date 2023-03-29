package edu.northeastern.myapplication.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

import edu.northeastern.myapplication.R;

public class EditProfile extends AppCompatActivity {
    private Profile currentProfile;
    private ImageView profileIcon;
    private Button saveButton, cancelButton;
    private EditText editUserName, editBio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editUserName = findViewById(R.id.editTextUserName);
        editBio = findViewById(R.id.editTextBio);
        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);
        profileIcon = findViewById(R.id.editProfileIcon);
        profileIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_account_box_24));


        loadProfile();
//        System.out.println(currentProfile.getProfileId());
    }

    public void loadProfile() {
        // TODO: change pid to unique profile id
        String pid = "1";
        new Thread(() -> {
            DatabaseReference profileRef = FirebaseDatabase
                    .getInstance()
                    .getReference("profiles/" + pid); // TODO: Replace with user's profile id
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
        // Get text views
//        TextView profileName = findViewById(R.id.textViewProfileName);
//        TextView joinedYear = findViewById(R.id.textViewJoinedYear);
//        TextView totalWeight = findViewById(R.id.textViewTotalWeight);
//        TextView workoutCompleted = findViewById(R.id.textViewWorkoutCompleted);
//        TextView profileBio = findViewById(R.id.textViewBioDesc);

        // Set text
        editUserName.setText(currentProfile.getProfileName());
        editBio.setText(currentProfile.getProfileBio());
    }

    public void loadProfileImage() {
        new Thread(() -> {
            // Get profile icon from Firebase Storage and set it to the image view using Glide
            FirebaseStorage
                    .getInstance()
                    .getReference("/profileIcons/")
                    .child("TestImage.jpg")
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
            DatabaseReference profileRef = database.getReference("profiles/1");
            profileRef.child("profileName").setValue(newUsername);
            profileRef.child("profileBio").setValue(newBio);
        }).start();

        Intent intent = new Intent(EditProfile.this, MainActivityProfile.class);
        startActivity(intent);
        finish();
    }

//    public void saveUpdate(View view){
//        String newUsername = editUserName.getText().toString();
//        String newBio = editBio.getText().toString();
//        Toast.makeText(this, currentProfile.getProfileId(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, newUsername, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, newBio, Toast.LENGTH_SHORT).show();
//
//
//        new Thread(() -> {
//            // update profile info in database
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference userRef = database.getReference("profiles/" + currentProfile.getProfileId());
//            userRef.child("profileName").setValue(newUsername);
//            userRef.child("profileBio").setValue(newBio);
//        }).start();
//    }

    public void cancelUpdate(View view){
        Intent intent = new Intent(EditProfile.this, MainActivityProfile.class);
        startActivity(intent);
        finish();
    }
}


/**
 *     // save updated data and pass it to previous activity which is profile activity
 * //        Intent updated = new Intent();
 * //        updated.putExtra("userName", newUsername);
 * //        updated.putExtra("newBio", newBio);
 * //        setResult(EditProfile.RESULT_OK, updated);
 * //
 * //        finish();
 *         Intent intent = new Intent(EditProfile.this, MainActivityProfile.class);
 *         startActivity(intent);
 *         finish();
 */