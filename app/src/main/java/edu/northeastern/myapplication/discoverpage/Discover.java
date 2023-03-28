package edu.northeastern.myapplication.discoverpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.R;

public class Discover extends AppCompatActivity {

    private RecyclerView storiesRecyclerView;
    private RecyclerView profilesRecylerView;
    private ProfilesAdapter profilesAdapter;
    private StoriesAdapter storiesAdapter;
    private List<Profiles> profiles = new ArrayList<>();
    private List<Story> stories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        initStory();
        initProfiles();
    }

    public void initProfiles(){
        profilesRecylerView = findViewById(R.id.profiles);

        profiles.add(new Profiles("Shriya", "Hi", "", 1,1,1,1));
        profiles.add(new Profiles("Gino", "Hi", "", 1,1,1,1));

        profilesRecylerView.setHasFixedSize(true);
        profilesRecylerView.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        profilesAdapter = new ProfilesAdapter(profiles, this);
        profilesRecylerView.setAdapter(profilesAdapter);

        // add divided b/w links
        DividerItemDecoration decor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        profilesRecylerView.addItemDecoration(decor);
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
}