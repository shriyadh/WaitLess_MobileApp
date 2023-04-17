package edu.northeastern.myapplication.notificationsPage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.Profiles;
import edu.northeastern.myapplication.discoverpage.ProfilesAdapter;
import edu.northeastern.myapplication.discoverpage.StoriesAdapter;
import edu.northeastern.myapplication.discoverpage.StoriesDecor;
import edu.northeastern.myapplication.discoverpage.Story;

public class NotifyMe extends AppCompatActivity {

    private RecyclerView notifyRecyclerView;
    private NotifyAdapter notifyAdapter;
    private DatabaseReference databaseReference;
    private String loggedInUser;
    private List<Notification> notifications = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_me);
        // set up recycler for notifications
        initNotify();

    }

    public void initNotify(){
        notifyRecyclerView = findViewById(R.id.notificationsBar);
        notifications.add(new Notification("Shriya", "200"));
        notifications.add(new Notification("Mariah",  "200"));
        notifications.add(new Notification("Gino",  "300"));
        notifications.add(new Notification("Ted",  "400"));
        notifications.add(new Notification("Mitch",  "500"));

        notifyRecyclerView.setHasFixedSize(true);
        notifyRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //set adapter
        notifyAdapter= new NotifyAdapter(notifications, this);
        notifyRecyclerView.setAdapter(notifyAdapter);
        // add divided b/w links
        DividerItemDecoration decor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        notifyRecyclerView.addItemDecoration(decor);

    }
}