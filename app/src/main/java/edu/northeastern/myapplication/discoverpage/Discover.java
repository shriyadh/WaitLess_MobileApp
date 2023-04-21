package edu.northeastern.myapplication.discoverpage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;

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
        // Disable rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        loggedInUser = mUser.getUid();

        //checking logged in user
        //System.out.println("displaynae" +mUser.getEmail());
        //System.out.println(loggedInUser);

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
    @Override
    public void onBackPressed() {
        // Do nothing to disable back button
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


                Toast.makeText(getApplicationContext(), "You are now following " +
                                profilesLst.get(position).getUsername() + "!",
                        Toast.LENGTH_LONG).show();

                //takePicture();
            }
        };
        profilesAdapter.setListenerLink(listener);


    }

    public void initStory() {
        storiesRecyclerView = findViewById(R.id.stories);

        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //set adapter
        storiesAdapter = new StoriesAdapter(stories, this);
        storiesRecyclerView.setAdapter(storiesAdapter);
        storiesRecyclerView.addItemDecoration(new StoriesDecor(10));

    }

    class storyThread implements Runnable{
        @Override
        public void run() {
            try {
                runStories();
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
                                    if (followIdListStory != null && followIdListStory.contains(userData.getKey())) {

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
                        //stories.add(0,new Story("Me", loggedInUser, false));

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
                                        String bio = userData.child("profileBio").getValue().toString();

                                        String token = userData.getKey();

                                        Profiles newUser = new Profiles(username, bio, token);
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

        }

        @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PROFILE_follow", "Failed to read follow value.", error.toException());
            }
        });


    }


}