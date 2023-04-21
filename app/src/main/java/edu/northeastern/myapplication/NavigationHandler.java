package edu.northeastern.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;

import edu.northeastern.myapplication.Profile.MainActivityProfile;
import edu.northeastern.myapplication.discoverpage.Discover;
import edu.northeastern.myapplication.notificationsPage.NotifyMe;
import edu.northeastern.myapplication.queue.Queue_home;

public class NavigationHandler implements NavigationBarView.OnItemSelectedListener {

    private Activity activity;

    public NavigationHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_notifications:
                System.out.println("Requests");
                Intent requestIntent = new Intent(activity, NotifyMe.class);
                activity.startActivity(requestIntent);
                break;
            case R.id.navigation_discover:
                // Handle dashboard click
                System.out.println("DISCOVER");
                Intent discoverIntent = new Intent(activity, Discover.class);
                activity.startActivity(discoverIntent);
                break;
            case R.id.navigation_profile:
                // Handle notifications click
                Intent profileIntent = new Intent(activity, MainActivityProfile.class);
                activity.startActivity(profileIntent);
                System.out.println("PROFILE");
                break;
            case R.id.navigation_queue:
                // Handle notifications click
                System.out.println("ADD TO QUEUE");
                Intent queueIntent = new Intent(activity, Queue_home.class);
                queueIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(queueIntent);
                break;
            default:
                return false;
        }
        return false;
    }
}