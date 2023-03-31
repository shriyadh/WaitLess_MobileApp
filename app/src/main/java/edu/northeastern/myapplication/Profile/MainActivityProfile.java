package edu.northeastern.myapplication.Profile;

import android.graphics.Color;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.Workouts.Workout;


// TODO:
//  - Generates id for Firsebase https://stackoverflow.com/questions/28822054/firebase-how-to-generate-a-unique-numeric-id-for-key
//  - Potentially transfer over to using FirebaseAuth instead of FirebaseRealtimeDatabase for profiles

public class MainActivityProfile extends AppCompatActivity {
    private Profile currentProfile;
    private ImageButton profileSettingsButton;
    private Button workoutHistoryButton, friendsButton;
    private ImageView profileIcon;
    private BarChart chart;
    private List<Workout> workoutList;

    private final String profileId = "-NRVYvTjwCGKqGm9dUIq"; // TODO: Replace with user's id

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
        new Thread(() -> {
            workoutList = new ArrayList<>();
            DatabaseReference profileRef = FirebaseDatabase
                    .getInstance()
                    .getReference("profiles/" + profileId); // TODO: Replace with user's profile id
            DatabaseReference workoutRef = FirebaseDatabase
                    .getInstance()
                    .getReference("workouts/" + profileId); // TODO: Replace with user's profile id
            profileRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentProfile = snapshot.getValue(Profile.class);
//                    for (DataSnapshot workoutSnapshot : snapshot.child("workouts").getChildren()) {
//                        workoutList.add(workoutSnapshot.getValue(Workout.class));
//                    }
                    loadProfileImage();
                    loadProfileTextData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PROFILE_DATA", "Failed to read profile value.", error.toException());
                }
            });

            workoutRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                        workoutList.add(workoutSnapshot.getValue(Workout.class));
                    }
                    loadProfileGraph();
                    loadLastWorkout();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PROFILE_WORKOUTS", "Failed to read workouts value.", error.toException());
                }
            });
        }).start();

    }


    public void loadProfileTextData() {
        // Get text views
        TextView profileName = findViewById(R.id.textViewProfileName);
        TextView joinedYear = findViewById(R.id.textViewJoinedYear);
        TextView totalWeight = findViewById(R.id.textViewTotalWeight);
        TextView workoutCompleted = findViewById(R.id.textViewWorkoutCompleted);
        TextView profileBio = findViewById(R.id.textViewBioDesc);

        // Format total weight and total workouts to include commas
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);
        String formattedTotalWeight = numberFormat.format(currentProfile.getTotalLifted());
        String formattedTotalWorkouts = numberFormat.format(workoutList.size());



        // Format date from milliseconds to MM/YYYY
        String formattedDate = new SimpleDateFormat("MM/yyyy", Locale.US)
                .format(currentProfile.getJoinedDate());


        // Set text
        profileName.setText(currentProfile.getProfileName());
        joinedYear.setText(formattedDate);
        totalWeight.setText(formattedTotalWeight);
        workoutCompleted.setText(formattedTotalWorkouts);
        profileBio.setText(currentProfile.getProfileBio());
    }
        public void loadProfileImage() {
        new Thread(() -> {
            // Get profile icon from Firebase Storage and set it to the image view using Glide
            FirebaseStorage
                    .getInstance()
                    .getReference("/profileIcons/")
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

    public void loadProfileGraph() {
        int totalChest = 0;
        int totalBack = 0;
        int totalArms = 0;
        int totalAbdominal = 0;
        int totalLegs = 0;
        int totalShoulders = 0;

        for (Workout workout : workoutList) {
            totalChest += workout.getChest();
            totalBack += workout.getBack();
            totalArms += workout.getArms();
            totalAbdominal += workout.getAbdominal();
            totalLegs += workout.getLegs();
            totalShoulders += workout.getShoulders();
        }

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, totalChest));
        entries.add(new BarEntry(2f, totalBack));
        entries.add(new BarEntry(3f, totalArms));
        entries.add(new BarEntry(4f, totalAbdominal));
        entries.add(new BarEntry(5f, totalLegs));
        entries.add(new BarEntry(6f, totalShoulders));

        BarDataSet dataSet = new BarDataSet(entries, "Workout History");

        BarData barData = new BarData(dataSet);

        chart.setData(barData);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setValueTextSize(16f);
        chart.getDescription().setEnabled(false);

        // Reload chart
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public void loadLastWorkout() {
        TextView lastWorkoutDate = findViewById(R.id.textViewLastWorkoutDate);
        TextView workout1 = findViewById(R.id.textViewW1);
        TextView workout2 = findViewById(R.id.textViewW2);
        TextView workout3 = findViewById(R.id.textViewW3);
        TextView workout4 = findViewById(R.id.textViewW4);
        TextView workout5 = findViewById(R.id.textViewW5);
        TextView workout6 = findViewById(R.id.textViewW6);
        TextView workoutDuration = findViewById(R.id.textViewWorkoutDuration);

        if (!workoutList.isEmpty()) {
            // Sort workout list based on date reversed
            workoutList.sort(Comparator.comparingLong(Workout::getDate).reversed());

            // Get more recent workout
            Workout lastWorkout = workoutList.get(0);

            // Convert Date into MM/DD/YYYY format
            String formattedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    .format(lastWorkout.getDate());

            // Set text
            lastWorkoutDate.setText(formattedDate);

            workout1.setText(String.valueOf(lastWorkout.getChest()));
            workout2.setText(String.valueOf(lastWorkout.getBack()));
            workout3.setText(String.valueOf(lastWorkout.getArms()));
            workout4.setText(String.valueOf(lastWorkout.getAbdominal()));
            workout5.setText(String.valueOf(lastWorkout.getLegs()));
            workout6.setText(String.valueOf(lastWorkout.getShoulders()));

            workoutDuration.setText(String.valueOf(lastWorkout.getDuration()));
        }
    }
}