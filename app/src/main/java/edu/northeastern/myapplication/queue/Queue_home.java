package edu.northeastern.myapplication.queue;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    private DatabaseReference databaseReference;
    private String set_count;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_home);

        in_queue = findViewById(R.id.current_queue);
        est_wait = findViewById(R.id.wait_est);
        est_wait.setVisibility(View.INVISIBLE);
        join_queue_btn = findViewById(R.id.join_queue_btn);
        leave_queue_btn = findViewById(R.id.leave_queue_btn);
        leave_queue_btn.setVisibility(View.INVISIBLE);
        user_in_queue = false;
        workout = "";
        set_count = "";
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        user = "SOME_USER";
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
        swap_queue_status(workout);
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
        builder.setTitle("Enter Number of Sets");

        // Set up the input
        final EditText input = new EditText(Queue_home.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int numSets = Integer.parseInt(input.getText().toString());
                set_count = String.valueOf(numSets);
                // add to queue
                queue_joiner();
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
                            DatabaseReference waitlist = FirebaseDatabase.getInstance()
                                    .getReference("queues/" + workout + "/waiting");
                            waitlist.child(user).removeValue();
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
//            pos_and_waitTime_updater();
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

    public void queue_joiner() {
        qjoin_t = new qjoin_thread();
        new Thread(qjoin_t).start();
    }

    class qjoin_thread implements Runnable {
        @Override
        public void run() {
            try {
                DatabaseReference waitlist = FirebaseDatabase.getInstance()
                                .getReference("queues/" + workout + "/waiting");
                waitlist.child(user).setValue(set_count);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    public void pos_and_waitTime_updater() {
        qt = new qThread();
        new Thread(qt).start();
    }

    class qThread implements Runnable {
        @Override
        public void run() {
            try {
                List pos_waitTime = find_pos_and_waitTime();
                est_wait.setText("Position in Queue: " + pos_waitTime.get(0) + "\n\n" +
                        "Est. wait time: " + pos_waitTime.get(1) + " min.");
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

    }

    private List find_pos_and_waitTime() {
        List res = new ArrayList();
        DatabaseReference workout_ref = databaseReference.child(workout);




//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    for (DataSnapshot curr : snapshot.getChildren()) {
//                        System.out.println(curr.toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };

        res.add(1);
        res.add(2);

        return res;
    }


}