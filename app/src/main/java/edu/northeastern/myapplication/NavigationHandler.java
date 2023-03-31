package edu.northeastern.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;

import edu.northeastern.myapplication.chats.Chat;
import edu.northeastern.myapplication.discoverpage.Discover;

public class NavigationHandler implements NavigationBarView.OnItemSelectedListener {

    private Activity activity;

    public NavigationHandler(Activity activity) {
        this.activity = activity;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_chat:
                System.out.println("CHATTING");
                Intent chatIntent = new Intent(activity, Chat.class);
                activity.startActivity(chatIntent);
                return true;
            case R.id.navigation_discover:
                // Handle dashboard click
                System.out.println("DISCOVER");
                Intent discoverIntent = new Intent(activity, Discover.class);
                activity.startActivity(discoverIntent);
            case R.id.navigation_profile:
                // Handle notifications click
                System.out.println("PROFILE");
                return true;
            case R.id.navigation_queue:
                // Handle notifications click
                System.out.println("ADD TO QUEUE");
                return true;
            default:
                return false;
        }
    }
}
