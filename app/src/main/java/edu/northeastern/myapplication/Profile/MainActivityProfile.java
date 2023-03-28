package edu.northeastern.myapplication.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

import edu.northeastern.myapplication.R;


// TODO:
//  - Generates id for Firsebase https://stackoverflow.com/questions/28822054/firebase-how-to-generate-a-unique-numeric-id-for-key
//  - Potentially transfer over to using FirebaseAuth instead of FirebaseRealtimeDatabase for profiles

public class MainActivityProfile extends AppCompatActivity implements OnChartValueSelectedListener {
    private Profile currentProfile;
    private ImageButton profileSettingsButton;
    private Button workoutHistoryButton, friendsButton;
    private ImageView profileIcon;
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        profileSettingsButton = findViewById(R.id.imageButtonProfileSettings);
        workoutHistoryButton = findViewById(R.id.buttonWorkoutHistory);
        friendsButton = findViewById(R.id.buttonFriends);

        profileIcon = findViewById(R.id.imageViewProfileIcon);
        profileIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_account_box_24));

        chart = findViewById(R.id.barChartTotalWeight);


        loadProfile();
    }


    public void onClick(View view) {
        int buttonId = view.getId();
        if (buttonId == profileSettingsButton.getId()) {
            // TODO: Add code to go to profile settings page
            Log.w("Profile", "Profile Settings button clicked");
        } else if (buttonId == workoutHistoryButton.getId()) {
            // TODO: Add code to go to workout history page
            Log.w("Profile", "Workout History button clicked");
        } else if (buttonId == friendsButton.getId()) {
            // TODO: Add code to go to friends list page
            Log.w("Profile", "Friends button clicked");
        }
    }

    public void loadProfile() {
        String pid = "-NRVYvTjwCGKqGm9dUIq";
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

    /**
     *
     */
    public void loadProfileTextData() {
        // Get text views
        TextView profileName = findViewById(R.id.textViewProfileName);
        TextView joinedYear = findViewById(R.id.textViewJoinedYear);
        TextView totalWeight = findViewById(R.id.textViewTotalWeight);
        TextView workoutCompleted = findViewById(R.id.textViewWorkoutCompleted);
        TextView profileBio = findViewById(R.id.textViewBioDesc);

        // Format date from milliseconds to MM/YYYY
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentProfile.getJoinedDate());
        String profileJoinedDate = calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);

        // Set text
        profileName.setText(currentProfile.getProfileName());
        joinedYear.setText(profileJoinedDate);
        totalWeight.setText(String.valueOf(currentProfile.getTotalLifted()));
        workoutCompleted.setText(String.valueOf(currentProfile.getTotalWorkouts()));
        profileBio.setText(currentProfile.getProfileBio());
    }
        public void loadProfileImage() {
        new Thread(() -> {
            // Get profile icon from Firebase Storage and set it to the image view using Glide
            FirebaseStorage
                    .getInstance()
                    .getReference("/profileIcons/")
                    .child(currentProfile.getProfileId() + ".jpg")
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

    public void loadProfileGraph() {
        new Thread(() -> {

        }).start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}