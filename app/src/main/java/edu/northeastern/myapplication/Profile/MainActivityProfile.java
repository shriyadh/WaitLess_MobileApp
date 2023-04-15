package edu.northeastern.myapplication.Profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import edu.northeastern.myapplication.Follows.MainActivityFollowList;
import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.Workouts.MainActivityWorkoutList;
import edu.northeastern.myapplication.Workouts.Workout;


public class MainActivityProfile extends AppCompatActivity {
    private Profile currentProfile;
    private ImageButton profileSettingsButton;
    private ToggleButton followButton;
    private Button workoutListButton, followListButton;
    private ImageView profileIcon;
    private BarChart chart;
    private List<Workout> workoutList;
    private List<String> followIdList;

    private String profileId;
    private final String currentProfileId = "-NRVYvTjwCGKqGm9dUIq"; // TODO: Replace with current user Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        followButton = findViewById(R.id.toggleButtonFollow);
        followButton.setVisibility(View.GONE);
        profileSettingsButton = findViewById(R.id.imageButtonProfileSettings);
        followButton.setVisibility(View.GONE);
        profileSettingsButton.setVisibility(View.GONE);
        workoutListButton = findViewById(R.id.buttonWorkoutHistory);
        followListButton = findViewById(R.id.buttonFollowers);

        profileIcon = findViewById(R.id.imageViewProfileIcon);
        profileIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_account_box_24));

        chart = findViewById(R.id.barChartTotalWeight);

        profileId = getIntent().getStringExtra("profileId");
        if (profileId != null && !profileId.equals(currentProfileId)) {
            followButton.setVisibility(View.VISIBLE);
            getFollowStatus();
        } else {
            profileId = currentProfileId;
            profileSettingsButton.setVisibility(View.VISIBLE);
        }

        loadProfile();
    }

    private void getFollowStatus() {
        FirebaseDatabase.getInstance()
                .getReference("follows/" + profileId)
                .child(currentProfileId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followButton.setChecked(!snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Profile", "Failed to read value.", error.toException());
                    }
                });
    }

    public void onClick(View view) {
        int buttonId = view.getId();
        if (buttonId == profileSettingsButton.getId()) {
            // TODO: Add code to go to profile settings page
            Log.w("Profile", "Profile Settings button clicked");


        } else if (buttonId == workoutListButton.getId()) {
            // TODO: Add code to go to workout history page
            Log.w("Profile", "Workout History button clicked");
            Intent intent = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
            Bundle bundle = new Bundle();
            if (workoutList != null) {
                bundle.putParcelableArrayList("workoutList", (ArrayList<Workout>) workoutList);
            } else {
                bundle.putParcelableArrayList("workoutList", new ArrayList<>());
            }
            intent.putExtras(bundle);
            intent.putExtra("profileId", profileId);
            intent.putExtra("currProfileId", currentProfileId); // TODO: Replace with current user Firebase
            startActivity(intent);

        } else if (buttonId == followListButton.getId()) {
            Intent intent = new Intent(getApplicationContext(), MainActivityFollowList.class);
            intent.putExtra("profileId", profileId);
            intent.putExtra("currProfileId", currentProfileId); // TODO: Replace with current user Firebase
            if (followIdList != null) {
                intent.putStringArrayListExtra("followIdList", (ArrayList<String>) followIdList);
            } else {
                intent.putStringArrayListExtra("followIdList", new ArrayList<>());
            }
            startActivity(intent);
        } else if (buttonId == followButton.getId()) {
            new Thread(() -> {
                DatabaseReference followStatus = FirebaseDatabase
                        .getInstance()
                        .getReference("follows/" + profileId)
                        .child(currentProfileId);
                if (followButton.isChecked()) {
                    followStatus.removeValue();
                } else {
                    followStatus.setValue(true);
                }
            }).start();
        }
    }

    public void loadProfile() {
        new Thread(() -> {
            DatabaseReference profileRef = FirebaseDatabase
                    .getInstance()
                    .getReference("profiles")
                    .child(profileId); // TODO: Replace with user's profile id
            DatabaseReference workoutRef = FirebaseDatabase
                    .getInstance()
                    .getReference("workouts")
                    .child(profileId); // TODO: Replace with user's profile id
            DatabaseReference followRef = FirebaseDatabase
                    .getInstance()
                    .getReference("follows")
                    .child(profileId); // TODO: Replace with user's profile id

            workoutRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    workoutList = new ArrayList<>();
                    for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                        workoutList.add(workoutSnapshot.getValue(Workout.class));
                    }
                    loadWorkoutData();
                    loadWorkoutGraph();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PROFILE_WORKOUTS", "Failed to read workouts value.", error.toException());
                }
            });

            followRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    followIdList = new ArrayList<>();
                    for (DataSnapshot followSnapshot : snapshot.getChildren()) {
                        followIdList.add(followSnapshot.getKey());
                    }
                    loadFollowData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PROFILE_follow", "Failed to read follow value.", error.toException());
                }
            });

            profileRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentProfile = snapshot.getValue(Profile.class);
                    loadProfileData();
                    loadProfileImage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PROFILE_DATA", "Failed to read profile value.", error.toException());
                }
            });

        }).start();

    }

    private void loadFollowData() {
        // Get text view
        TextView followCount = findViewById(R.id.textViewFollowCount);

        // Format number to include commas
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String formattedNumber = numberFormat.format(followIdList.size());

        // Set text
        followCount.setText(formattedNumber);
    }

    public void loadProfileData() {
        // Get text views
        TextView profileName = findViewById(R.id.textViewProfileName);
        TextView joinedYear = findViewById(R.id.textViewJoinedYear);
        TextView profileBio = findViewById(R.id.textViewBioDesc);

        // Format date from milliseconds to MM/YYYY
        String formattedDate = new SimpleDateFormat("MM/yyyy", Locale.US)
                .format(currentProfile.getJoinedDate());

        // Set text
        profileName.setText(currentProfile.getProfileName());
        joinedYear.setText(formattedDate);
        profileBio.setText(currentProfile.getProfileBio());
    }

    public void loadProfileImage() {
        new Thread(() -> {
            // Get profile icon from Firebase Storage and set it to the image view using Glide
            FirebaseStorage
                    .getInstance()
                    .getReference("/profileIcons")
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

    public void loadWorkoutGraph() {
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

    public void loadWorkoutData() {
        // Get text views
        TextView workoutsCompleted = findViewById(R.id.textViewWorkoutCompleted);
        TextView lastWorkoutDate = findViewById(R.id.textViewLastWorkoutDate);
        TextView workout1 = findViewById(R.id.textViewW1);
        TextView workout2 = findViewById(R.id.textViewW2);
        TextView workout3 = findViewById(R.id.textViewW3);
        TextView workout4 = findViewById(R.id.textViewW4);
        TextView workout5 = findViewById(R.id.textViewW5);
        TextView workout6 = findViewById(R.id.textViewW6);
        TextView workoutDuration = findViewById(R.id.textViewWorkoutDuration);

        if (!workoutList.isEmpty()) {
            // Format total weight and total workouts to include commas
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setGroupingUsed(true);
            String formattedTotalWorkouts = numberFormat.format(workoutList.size());
            workoutsCompleted.setText(formattedTotalWorkouts);

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
        } else {
            // Set text
            workoutsCompleted.setText("0");
            lastWorkoutDate.setText("N/A");
            workout1.setText("0");
            workout2.setText("0");
            workout3.setText("0");
            workout4.setText("0");
            workout5.setText("0");
            workout6.setText("0");
            workoutDuration.setText("0");
        }
    }
}