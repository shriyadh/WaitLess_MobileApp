package edu.northeastern.myapplication.notificationsPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.R;

public class NotifyMe extends AppCompatActivity {

    private RecyclerView notifyRecyclerView;
    private NotifyAdapter notifyAdapter;
    private DatabaseReference databaseReference;
    private String loggedInUser;
    private List<Notification> notifications = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("IN HERE");

        setContentView(R.layout.activity_notify_me);
        System.out.println("IN HERE");

        // set up recycler for notifications
        initNotify();

    }

    public void initNotify(){
        System.out.println("IN HERE");
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
        System.out.println("IN END");

    }
}