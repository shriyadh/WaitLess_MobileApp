package edu.northeastern.myapplication.queue;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.myapplication.NavigationHandler;
import edu.northeastern.myapplication.R;

public class Queue_home extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    private TextView in_queue;
    private TextView est_wait;
    private Button join_queue_btn;
    private Button leave_queue_btn;
    private boolean user_in_queue;
    private String workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_home);
        // find navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set selected item to queue
        bottomNavigationView.setSelectedItemId(R.id.navigation_queue);
        // activate nav listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationHandler(this));


        in_queue = findViewById(R.id.current_queue);
        est_wait = findViewById(R.id.wait_est);
        est_wait.setVisibility(View.INVISIBLE);
        join_queue_btn = findViewById(R.id.join_queue_btn);
        leave_queue_btn = findViewById(R.id.leave_queue_btn);
        leave_queue_btn.setVisibility(View.INVISIBLE);
        user_in_queue = false;
        workout = "";
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("user_is_in_queue", user_in_queue);
        savedInstanceState.putString("workout", (String) workout);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //************
        // recalc est wait time
        //************

        user_in_queue = savedInstanceState.getBoolean("user_is_in_queue");
        user_in_queue = !user_in_queue;
        workout = savedInstanceState.getString("workout");
        swap_buttons(workout);
    }

    public void qr_scanner(View view) {
        Intent intent = new Intent(this, QR_Scanner.class);
        launcher.launch(intent);
    }

    // receives the data from the QR Scanner
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            String receivedData = result.getData().getStringExtra("data");

            // update text on screen and change queue buttons
            swap_buttons(receivedData);
        }
    });

    public void leave_queue(View view) {
        // Warn user before leaving queue
        AlertDialog alertDialog =
                new AlertDialog.Builder(Queue_home.this).create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("You are about to leave the queue! If you confirm, you will lose" +
                " your position in this queue.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Leave",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        swap_buttons("");
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    private void swap_buttons(String data) {
        if (!user_in_queue) {
            workout = data;
            // update text on screen to queue joined
            in_queue.setText("In Queue: " + data);
            // change queue buttons to 'leave queue'
            join_queue_btn.setVisibility(View.INVISIBLE);
            leave_queue_btn.setVisibility(View.VISIBLE);
            // calc and show estimated wait time
            est_wait.setText(calc_wait_time());
            est_wait.setVisibility(View.VISIBLE);
            user_in_queue = true;
        } else {
            workout = "";
            // update text on screen to default
            in_queue.setText(getResources()
                    .getString(R.string.use_the_button_below_to_join_a_queue));
            // change queue buttons to 'join queue'
            leave_queue_btn.setVisibility(View.INVISIBLE);
            join_queue_btn.setVisibility(View.VISIBLE);
            // hide est wait time
            est_wait.setVisibility(View.INVISIBLE);
            user_in_queue = false;
        }
    }

    private String calc_wait_time() {
        return "Position in Queue: ??\n\nEst. wait time: ??";
    }


}