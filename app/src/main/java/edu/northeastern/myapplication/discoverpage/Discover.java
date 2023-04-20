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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.R;

public class Discover extends AppCompatActivity {

    private RecyclerView storiesRecyclerView;
    private RecyclerView profilesRecyclerView;
    private ProfilesAdapter profilesAdapter;
    private StoriesAdapter storiesAdapter;
    private DatabaseReference databaseReference;
    private String loggedInUser;
    private List<Profiles> profilesLst = new ArrayList<>();
    private HashMap<String, Profiles> mapProfiles = new HashMap<>();
    private List<Story> stories = new ArrayList<>();
    private List<String> workoutList;
    private List<String> followIdList;
    private List<String> followIdListStory;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> cameraLauncher;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        loggedInUser = mUser.getUid();

        //checking logged in user
        System.out.println("displaynae" +mUser.getEmail());
        System.out.println(loggedInUser);

        // find navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set selected item to queue
        bottomNavigationView.setSelectedItemId(R.id.navigation_discover);
        // activate nav listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationHandler(this));


        // set up recycler for stories
        initStory();
        // set up recycler for profiles
        initProfiles();

        // get data from firebase for profiles
        run_fbThread();
        run_storyThread();

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

                // add to following list
                // add to followers list
                FirebaseDatabase.getInstance()
                        .getReference("follows/" + loggedInUser + "/" + token_send_notify)
                        .setValue(true);

                FirebaseDatabase.getInstance().getReference("notification/"
                        + token_send_notify + "/" + loggedInUser)
                        .setValue(true);

                //Profiles followedUser = profilesLst.get(position);
                //List<String> followers = followedUser.getTotal_friends();
                //followers.add(loggedInUser);


                Toast.makeText(getApplicationContext(), "You are now following " +
                                profilesLst.get(position).getUsername() + "!",
                        Toast.LENGTH_LONG).show();

                //takePicture();
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

        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //set adapter
        storiesAdapter = new StoriesAdapter(stories, this);
        storiesRecyclerView.setAdapter(storiesAdapter);
        storiesRecyclerView.addItemDecoration(new StoriesDecor(10));

      /*  RecycleViewClickListener listener = new RecycleViewClickListener() {
            @Override
            public void onLinkClick(int position) {
            }

        }*/
    }

    class storyThread implements Runnable{
        @Override
        public void run() {
            try {
                runStories();
                //runFireBaseforOthers();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    private void run_storyThread() {
        Discover.storyThread storyThreading = new Discover.storyThread();
        new Thread(storyThreading).start();
    }

    public void runStories(){

        // first get the list of following for this user

        DatabaseReference followRef = FirebaseDatabase
                .getInstance()
                .getReference("follows")
                .child(loggedInUser);
        // add to following list
        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followIdListStory = new ArrayList<>();
                for (DataSnapshot followSnapshot : snapshot.getChildren()) {
                    //System.out.println("here"+ followSnapshot.getKey());
                    followIdListStory.add(followSnapshot.getKey());
                }

                // then check if they are not logged in user or they are not in the following list
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userID = databaseReference.child("profiles");

                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot userData : snapshot.getChildren()) {

                                if (!userData.getKey().equalsIgnoreCase(loggedInUser)) {
                                    if (followIdListStory == null || !followIdListStory.contains(userData.getKey())) {

                                        String username = userData.child("profileName").getValue().toString();
                                        //System.out.println(username);

                                        String token = userData.getKey();

                                        Story newUser = new Story(username, token);
                                        //newUser.setTotal_friends(followIdList);
                                        stories.add(newUser);
                                    }
                                }
                            }
                        }
                        stories.add(0,new Story("Check-In!", loggedInUser));
                        stories.get(0).setCheckIn(false);

                        storiesAdapter.notifyItemRangeInserted(0, stories.size());

                        //storiesAdapter.scrollToPosition(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                userID.addListenerForSingleValueEvent(eventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    class fbThread implements Runnable {
        @Override
        public void run() {
            try {
                runFirebase();
                //runFireBaseforOthers();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    private void run_fbThread() {
        Discover.fbThread fbThread = new Discover.fbThread();
        new Thread(fbThread).start();
    }



    public void runFirebase() {

        // first get the list of following for this user

        DatabaseReference followRef = FirebaseDatabase
                .getInstance()
                .getReference("follows")
                .child(loggedInUser);

        // add to following list
        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followIdList = new ArrayList<>();
                for (DataSnapshot followSnapshot : snapshot.getChildren()) {
                    //System.out.println("here"+ followSnapshot.getKey());
                    followIdList.add(followSnapshot.getKey());
                }

                // then check if they are not logged in user or they are not in the following list
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userID = databaseReference.child("profiles");

                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot userData : snapshot.getChildren()) {

                                if (!userData.getKey().equalsIgnoreCase(loggedInUser)) {
                                    if (followIdList == null || !followIdList.contains(userData.getKey())) {

                                        String username = userData.child("profileName").getValue().toString();
                                        //System.out.println(username);

                                        String bio = userData.child("profileBio").getValue().toString();
                                        // String friends = userData.child("friends_count").getValue().toString();
                                        //String workouts = userData.child("workout_count").getValue().toString();
                                        String token = userData.getKey();

                                        Profiles newUser = new Profiles(username, bio, token);
                                        //newUser.setTotal_friends(followIdList);
                                        profilesLst.add(newUser);
                                        mapProfiles.put(token, newUser);
                                    }
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


            DatabaseReference friends_following = FirebaseDatabase.getInstance().getReference();
            DatabaseReference friends_follows = friends_following.child("follows");
            ValueEventListener eventListenerFriends = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot userData : snapshot.getChildren()) {

                            if (!userData.getKey().equalsIgnoreCase(loggedInUser)) {
                                System.out.println(mapProfiles);
                                if (mapProfiles.containsKey(userData.getKey())) {
                                    List<String> friend_f = new ArrayList<>();
                                    for (DataSnapshot followSnapshot : snapshot.getChildren()) {
                                        //System.out.println("here"+ followSnapshot.getKey());
                                        friend_f.add(followSnapshot.getKey());
                                    }
                                    System.out.println(friend_f);
                                    mapProfiles.get(userData.getKey()).setTotal_friends(friend_f);

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            friends_follows.addListenerForSingleValueEvent(eventListenerFriends);
        }

        @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PROFILE_follow", "Failed to read follow value.", error.toException());
            }
        });
        System.out.println("here1"+ followIdList);





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




    }

    private void takePicture() {
        // Launch the camera activity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }

    }
}