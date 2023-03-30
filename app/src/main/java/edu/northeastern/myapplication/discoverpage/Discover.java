package edu.northeastern.myapplication.discoverpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        loggedInUser = "Shriya";


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
    }

    public void initStory() {
        storiesRecyclerView = findViewById(R.id.stories);
        stories.add(new Story(false));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(true));
        stories.add(new Story(false));

        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //set adapter
        storiesAdapter = new StoriesAdapter(stories, this);
        storiesRecyclerView.setAdapter(storiesAdapter);
        storiesRecyclerView.addItemDecoration(new StoriesDecor(10));

    }

    class fbThread implements Runnable {
        @Override
        public void run(){
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

        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userID = databaseReference.child("profiles");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot userData : snapshot.getChildren()) {

                        if (!userData.getKey().equalsIgnoreCase(loggedInUser)) {

                            String username = userData.child("profileName").getValue().toString();
                            String bio = userData.child("profileBio").getValue().toString();
                            String friends = userData.child("friends_count").getValue().toString();
                            String workouts = userData.child("workout_count").getValue().toString();
                            String token = userData.getKey();

                            Profiles newUser = new Profiles(username, bio, "", friends, workouts, token);
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
    }

}