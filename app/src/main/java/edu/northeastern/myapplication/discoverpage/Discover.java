package edu.northeastern.myapplication.discoverpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.R;

public class Discover extends AppCompatActivity {

    private RecyclerView storiesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        init();
    }

    public void init() {
        storiesRecyclerView = findViewById(R.id.stories);

        List<Story> stories = new ArrayList<>();
        stories.add(new Story(false));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(false));
        stories.add(new Story(true));
        stories.add(new Story(true));
        stories.add(new Story(false));

        StoriesAdapter adapter = new StoriesAdapter(stories, this);
        storiesRecyclerView.setAdapter(adapter);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        storiesRecyclerView.addItemDecoration(new StoriesDecor(10));
    }
}