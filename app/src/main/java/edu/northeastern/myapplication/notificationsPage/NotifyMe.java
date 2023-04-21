package edu.northeastern.myapplication.notificationsPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.Profile.MainActivityProfile;
import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.Discover;
import edu.northeastern.myapplication.discoverpage.RecycleViewClickListener;

public class NotifyMe extends AppCompatActivity {

    private RecyclerView notifyRecyclerView;
    private NotifyAdapter notifyAdapter;
    private DatabaseReference databaseReference;
    private String loggedInUser;
    private List<Notification> notifications = new ArrayList<>();


    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("IN HERE");

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        //checking logged in user
        System.out.println(mUser.getEmail());

        setContentView(R.layout.activity_notify_me);
        System.out.println("IN HERE");
        // find navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set selected item to queue
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);
        // activate nav listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationHandler(this));

        //RUN THREAD
        run_fbThread();

        // set up recycler for notifications
        initNotify();

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
        NotifyMe.fbThread fbThread = new NotifyMe.fbThread();
        new Thread(fbThread).start();


    }

    public void initNotify(){
        System.out.println("IN HERE");
        notifyRecyclerView = findViewById(R.id.notificationsBar);
//        notifications.add(new Notification("Ali","0EVfqn4NZBRzg53IVqhod1SeIfp2"));
//        notifications.add(new Notification("Naruto",  "1h9cxdj4GJeRFGawmUpk990Zg8b2"));
//        notifications.add(new Notification("Fender",  "24XodY2BxvO7QalgHbKbweMvas93"));
//        notifications.add(new Notification("Cebum",    "3EujxeHZTtOYI3gGs5LzI6CQ7K82"));
//        notifications.add(new Notification("Coach",  "3FCWYHmfUSh2YIeqdBVTgPBiNJj1"));

        notifyRecyclerView.setHasFixedSize(true);
        notifyRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //set adapter
        notifyAdapter= new NotifyAdapter(notifications, this);
        notifyRecyclerView.setAdapter(notifyAdapter);
        // add divided b/w links
        DividerItemDecoration decor = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        notifyRecyclerView.addItemDecoration(decor);

        System.out.println("IN END OF NOTIFICATIONS");

        RecycleViewClickListener listener = new RecycleViewClickListener() {
            @Override
            public void onLinkClick(int position) {
                // send to profile page
                Intent profIntent = new Intent(getApplicationContext(), MainActivityProfile.class);
                profIntent.putExtra("profileId", notifications.get(position).getUserID());
                startActivity(profIntent);

            }
        };
        notifyAdapter.setListenerLink(listener);
    }

    public void runFirebase() {

        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userID = databaseReference.child("notification").child(mUser.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot userData : snapshot.getChildren()) {
                        //userdata == a notification userID
                        String userID = userData.getKey();

                        Notification newUser = new Notification(userID);
                        notifications.add(newUser);
                    }
                }
                notifyAdapter.notifyItemRangeInserted(0, notifications.size());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        userID.addListenerForSingleValueEvent(eventListener);
    }

    }