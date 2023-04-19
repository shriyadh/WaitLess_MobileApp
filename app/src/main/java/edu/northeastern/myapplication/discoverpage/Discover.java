package edu.northeastern.myapplication.discoverpage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.R;

public class Discover extends AppCompatActivity {

    private RecyclerView storiesRecyclerView;
    private RecyclerView profilesRecyclerView;
    private ProfilesAdapter profilesAdapter;
    private StoriesAdapter storiesAdapter;
    private DatabaseReference databaseReference;
    private String loggedInUser;
    private List<Profiles> profilesLst = new ArrayList<>();
    private List<Story> stories = new ArrayList<>();
    private List<String> workoutList;
    private List<String> followIdList;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        loggedInUser = "1h9cxdj4GJeRFGawmUpk990Zg8b2";


        // set up recycler for stories
        initStory();
        // set up recycler for profiles
        initProfiles();

        // get data from firebase for profiles
        run_fbThread();
    }

    public void initProfiles() {
        profilesRecyclerView = findViewById(R.id.profiles);

        profilesRecyclerView.setHasFixedSize(true);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        profilesAdapter = new ProfilesAdapter(profilesLst, this);
        profilesRecyclerView.setAdapter(profilesAdapter);

        // add divided b/w links
        DividerItemDecoration decor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        profilesRecyclerView.addItemDecoration(decor);

        // set the listener for recycler view
        RecycleViewClickListener listener = new RecycleViewClickListener() {
            @Override
            public void onLinkClick(int position) {
                // send notification to connect to the user clicked
                String token_send_notify = profilesLst.get(position).getUser_token();
                // send notification to this user --> if accepted add to friend list of both, else ignore
                takePicture();
            }
        };
        profilesAdapter.setListenerLink(listener);

        // Initialize the cameraLauncher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // The image was captured successfully
                        // You can retrieve the captured image from the result data
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            // Do something with the imageBitmap
                        }
                    }
                });


    }

    public void initStory() {
        storiesRecyclerView = findViewById(R.id.stories);
        stories.add(new Story("", false));
        stories.add(new Story("", false));
        stories.add(new Story("", true));
        stories.add(new Story("", false));
        stories.add(new Story("", true));
        stories.add(new Story("", false));
        stories.add(new Story("", true));
        stories.add(new Story("", true));
        stories.add(new Story("", false));

        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //set adapter
        storiesAdapter = new StoriesAdapter(stories, this);
        storiesRecyclerView.setAdapter(storiesAdapter);
        storiesRecyclerView.addItemDecoration(new StoriesDecor(10));


    }

    class fbThread implements Runnable {
        @Override
        public void run() {
            try {
                runFirebase();
                runFireBaseforOthers();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    private void run_fbThread() {
        Discover.fbThread fbThread = new Discover.fbThread();
        new Thread(fbThread).start();


    }

    public void runFireBaseforOthers(){

    }

    public void runFirebase() {

        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userID = databaseReference.child("profiles");

        DatabaseReference workoutRef = FirebaseDatabase
                .getInstance()
                .getReference("workouts")
                .child(loggedInUser); // TODO: Replace with user's profile id
        DatabaseReference followRef = FirebaseDatabase
                .getInstance()
                .getReference("follows")
                .child(loggedInUser); // TODO: Replace with user's profile id

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot userData : snapshot.getChildren()) {

                        if (!userData.getKey().equalsIgnoreCase(loggedInUser)) {

                            String username = userData.child("profileName").getValue().toString();
                            String bio = userData.child("profileBio").getValue().toString();
                            //String friends = userData.child("friends_count").getValue().toString();
                            //String workouts = userData.child("workout_count").getValue().toString();
                            String token = userData.getKey();

                            Profiles newUser = new Profiles(username, bio, token);
                            profilesLst.add(newUser);
                        }
                    }
                }
                profilesAdapter.notifyItemRangeInserted(0, profilesLst.size());
                profilesRecyclerView.scrollToPosition(0);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        userID.addListenerForSingleValueEvent(eventListener);

        // add workout list
      /*  workoutRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workoutList = new ArrayList<>();
                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    workoutList.add(workoutSnapshot.getValue(Workout.class));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PROFILE_WORKOUTS", "Failed to read workouts value.", error.toException());
            }
        });*/

        // add friends list
        followRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followIdList = new ArrayList<>();
                for (DataSnapshot followSnapshot : snapshot.getChildren()) {
                    followIdList.add(followSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PROFILE_follow", "Failed to read follow value.", error.toException());
            }
        });



    }



    private void takePicture() {
        // Launch the camera activity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }

    }
}