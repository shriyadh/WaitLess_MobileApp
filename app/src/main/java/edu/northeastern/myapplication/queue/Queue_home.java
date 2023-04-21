package edu.northeastern.myapplication.queue;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

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
    private qThread qt;
    private qjoin_thread qjoin_t;
    private looper e_15;
    private DatabaseReference databaseReference;
    private String set_count;
    private String user;
    private String user_db_key;
    private String machine_key;
    private boolean is_working_out;
    private int wait_time_estimate;
    private List<List<String>> q_list;
    private Vibrator vibrator;
    private boolean first_time;
    private boolean been_warned;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int maxBubbleSize = 125;
    private long maxDuration = 1500L; // milliseconds
    private long stopDelay = 4000L; // milliseconds

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Disable rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        assert mUser != null;
        //checking logged in user

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
        set_count = "";
        is_working_out = false;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        q_list = new ArrayList<>();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("user_is_in_queue", user_in_queue);
        savedInstanceState.putString("workout", (String) workout);
        savedInstanceState.putString("user_db_key", (String) user_db_key);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user_in_queue = savedInstanceState.getBoolean("user_is_in_queue");
        user_in_queue = !user_in_queue;
        workout = savedInstanceState.getString("workout");
        user_db_key = savedInstanceState.getString("user_db_key");
        swap_queue_status(workout);
        if (user_in_queue) {
            create_queue_list();
        }
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

            // ask for num of sets
                // then add to waitlist
            ask_sets();

            // update text on screen and change queue buttons
            swap_queue_status(receivedData);
        }
    });

    public void ask_sets() {
        // ask for number of sets
        AlertDialog.Builder builder = new AlertDialog.Builder(Queue_home.this);
        builder.setTitle("Enter Number of Sets (Default = 3)");

        // Set up the input
        final EditText input = new EditText(Queue_home.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int numSets = 3;
                try {
                    numSets = Integer.parseInt(input.getText().toString());
                } catch (Exception e) {
                    numSets = 3;
                }
                set_count = String.valueOf(numSets);
                // add to queue
                queue_joiner();
                first_time = true;
                been_warned = false;
                every_15();
            }
        });
        builder.show();
    }

    public static String convertToTitleCase(String input) {
        // Split the input string into words based on the underscore character
        String[] words = input.split("_");

        StringBuilder output = new StringBuilder();
        for (String word : words) {
            // Capitalize the first letter
            output.append(Character.toUpperCase(word.charAt(0)))
                    // Append the rest of the word
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return output.toString().trim();
    }

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
                        try {
                            DatabaseReference workout_queue = FirebaseDatabase.getInstance()
                                    .getReference("queues/" + workout + "/" + machine_key);
                            workout_queue.child(user_db_key).removeValue();
                            user_db_key = "";
                            leave_queue_btn.setText("Leave Queue");
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                        swap_queue_status("");
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    private void swap_queue_status(String data) {
        if (!user_in_queue) {
            workout = convertToTitleCase(data);
            // update text on screen to queue joined
            in_queue.setText("In Queue: " + workout);
            // change queue buttons to 'leave queue'
            join_queue_btn.setVisibility(View.INVISIBLE);
            leave_queue_btn.setVisibility(View.VISIBLE);
            // calc and show estimated wait time
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
            machine_key = "";
            user_in_queue = false;
        }
    }

    public void queue_joiner() {
        qjoin_t = new qjoin_thread();
        new Thread(qjoin_t).start();
    }

    class qjoin_thread implements Runnable {
        @Override
        public void run() {
            try {
                DatabaseReference workout_to_join = FirebaseDatabase.getInstance()
                                .getReference("queues/" + workout);
                workout_to_join.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String best_machine = "1";
                        int smallest_wait = Integer.MAX_VALUE;
                        // find machine number with smallest wait time
                        for (DataSnapshot machine : dataSnapshot.getChildren()) {
                            String machine_number = machine.getKey().toString();
                            machine_number = String.valueOf(machine_number
                                    .charAt(machine_number.length() - 1));

                            int curr_sum = 0;
                            for (DataSnapshot working_user : machine.getChildren()) {
                                curr_sum += Integer.valueOf(String.valueOf(working_user.getValue()));
                            }
                            if (curr_sum < smallest_wait) {
                                best_machine = machine_number;
                                smallest_wait = curr_sum;
                            }
                        }
                        // assign user to smallest wait time machine
                        DatabaseReference machine_to_join = FirebaseDatabase.getInstance()
                                .getReference("queues/" + workout + "/" + workout + " " +
                                                best_machine);
                        machine_to_join.push().setValue(set_count, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                String key = databaseReference.getKey();
                                user_db_key = key;
                            }
                        });
                        machine_key = workout + " " + best_machine;
                        create_queue_list();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that may occur
                    }
                });
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    public void create_queue_list() {
        qt = new qThread();
        new Thread(qt).start();
    }

    class qThread implements Runnable {
        @Override
        public void run() {
            try {
                q_list.clear();
                DatabaseReference workout_queue = FirebaseDatabase.getInstance()
                        .getReference("queues/" + workout + "/" + machine_key);
                workout_queue.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot cur_user : dataSnapshot.getChildren()) {
                            List u = new ArrayList<>();
                            u.add(cur_user.getKey().toString());
                            u.add(cur_user.getValue().toString());
                            q_list.add(u);
                        }
                        find_pos_and_waitTime();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that may occur
                    }
                });
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

    }

    private void startBubbles() {
        // Get the root view of the activity or fragment
        View rootView = findViewById(android.R.id.content);

        // Define a bubble-shaped drawable
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.WHITE);
        drawable.setStroke(5, Color.parseColor("#20B2AA"));

        // Define the bubble generator runnable
        Runnable generator = new Runnable() {
            @Override
            public void run() {
                // Create a new bubble view with random size and position
                View bubble = new View(Queue_home.this);
                int size = random.nextInt(maxBubbleSize) + maxBubbleSize / 2;
                int x = random.nextInt(rootView.getWidth() - size);
                bubble.setX(x);
                bubble.setY(0);
                bubble.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                bubble.setBackground(drawable);
                ((ViewGroup)rootView).addView(bubble);

                // Animate the bubble falling to the bottom of the screen
                bubble.animate()
                        .y(rootView.getHeight() - size)
                        .setDuration(random.nextInt((int) maxDuration) + maxDuration / 2)
                        .withEndAction(() -> {
                            // Remove the bubble from the view hierarchy when the animation ends
                            ((ViewGroup)rootView).removeView(bubble);
                        })
                        .start();

                // Schedule the next bubble to be generated
                handler.postDelayed(this, random.nextInt(100) + 100);
            }
        };

        // Start the bubble generator
        handler.post(generator);

        // Stop the bubble generator after a delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(generator);
            }
        }, stopDelay);
    }

    private void user_is_up() {
        startBubbles();
        Toast.makeText(this, "You're up for " + workout + "!",
                Toast.LENGTH_LONG).show();
        if (vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 500, 1000, 500};
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(vibrationEffect);
        }
    }

    private void user_is_next() {
        Toast.makeText(this, "You're next! Head to " + workout,
                Toast.LENGTH_LONG).show();
        if (vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 500, 1000, 500};
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(vibrationEffect);
        }
        been_warned = true;
    }

    private void find_pos_and_waitTime() {
        int counter = 1;
        for (List u : q_list) {
            String ukey = (String) u.get(0);
            if (ukey.equals(user_db_key)) {
                if (counter < 4){
                    is_working_out = true;
                    if (first_time) {
                        user_is_up();
                        first_time = false;
                    }
                    est_wait.setText("You're up - let's get those gains!");
                    leave_queue_btn.setText("All Finished");
                    return;
                } else {
                    wait_time_estimate = (counter - 3) * 2;
                    if (wait_time_estimate == 2 && !been_warned) {
                        user_is_next();
                    }
                }
                break;
            }
            counter++;
        }
        est_wait.setText("Est. wait time: " + String.valueOf(wait_time_estimate) + " min.");
    }

    public void every_15() {
        e_15 = new looper();
        new Thread(e_15).start();
    }

    class looper implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(15000); // wait for 15 seconds
                    if (!user_db_key.isEmpty()) {
                        create_queue_list(); // call create_queue_list() if user_db_key is not empty
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable back button
        Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show();
    }


}