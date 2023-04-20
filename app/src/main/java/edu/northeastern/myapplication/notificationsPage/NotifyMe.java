package edu.northeastern.myapplication.notificationsPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.R;
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

        // set up recycler for notifications
        initNotify();

    }

    public void initNotify(){
        System.out.println("IN HERE");
        notifyRecyclerView = findViewById(R.id.notificationsBar);
        notifications.add(new Notification("Ali","0EVfqn4NZBRzg53IVqhod1SeIfp2"));
        notifications.add(new Notification("Naruto",  "1h9cxdj4GJeRFGawmUpk990Zg8b2"));
        notifications.add(new Notification("Fender",  "24XodY2BxvO7QalgHbKbweMvas93"));
        notifications.add(new Notification("Cebum",    "3EujxeHZTtOYI3gGs5LzI6CQ7K82"));
        notifications.add(new Notification("Coach",  "3FCWYHmfUSh2YIeqdBVTgPBiNJj1"));

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
//                //Get user clicked on
//                String followUser = notifications.get(position).getUser();

                //Get username of user clicked on
                String followUsername = notifications.get(position).getUserName();


                System.out.println("follow user" + followUsername);
                System.out.println("logged in user" + mUser.getUid());

                // add to following list(remove from notifications)
                FirebaseDatabase.getInstance()
                        .getReference("follows/" + mUser.getUid() + "/" + followUsername)
                        .setValue(true);

////                // remove from notifications list(table)
                FirebaseDatabase.getInstance()
                        .getReference("notification/" + mUser.getUid() + "/" + followUsername)
                        .removeValue();


            }
        };
        notifyAdapter.setListenerLink(listener);

    }



}