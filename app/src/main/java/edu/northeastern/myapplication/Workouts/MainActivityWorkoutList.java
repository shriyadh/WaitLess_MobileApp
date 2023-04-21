package edu.northeastern.myapplication.Workouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.List;

import edu.northeastern.myapplication.Profile.Profile;
import edu.northeastern.myapplication.R;

public class MainActivityWorkoutList extends AppCompatActivity {
    private List<Workout> workoutList;
    private String profileId;
    private String currProfileId;
    private TextView workoutListTitle, noWorkoutsText;
    private RecyclerView workoutRecyclerView;
    private WorkoutRviewAdapter workoutRviewAdapter;
    private RecyclerView.LayoutManager rLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_workout_list);

        // Get the Intent that started this activity and extract List of workouts and profileId
        Bundle bundle = getIntent().getExtras();
        workoutList = bundle.getParcelableArrayList("workoutList");
        profileId = bundle.getString("profileId");
        currProfileId = bundle.getString("currProfileId");

        workoutListTitle = findViewById(R.id.textViewWorkoutListTitle);
        noWorkoutsText = findViewById(R.id.textViewNoWorkouts);

        if (profileId.equals(currProfileId)) findViewById(R.id.floatingActionButtonAddWorkout).setVisibility(View.VISIBLE);
        else findViewById(R.id.floatingActionButtonAddWorkout).setVisibility(View.INVISIBLE);
        if (workoutList.isEmpty()) {
            noWorkoutsText.setVisibility(TextView.VISIBLE);
        } else {
            noWorkoutsText.setVisibility(TextView.INVISIBLE);
        }
        getWorkoutsListTitle();
        createRecyclerView();
    }

    @Override
    protected void onStop() {
        new Thread(() -> {
            FirebaseDatabase.getInstance()
                    .getReference("workouts")
                    .child(profileId)
                    .setValue(workoutList);
        }).start();
        super.onStop();
    }

    public void onClick(View view) {
        int buttonId = view.getId();
        if (buttonId == R.id.floatingActionButtonAddWorkout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Workout");
            builder.setMessage("Enter the amount of sets and duration for this workout");
            final View customLayout = getLayoutInflater()
                    .inflate(R.layout.add_workout_dialog, null);
            builder.setView(customLayout);
//            builder.setView(R.layout.add_workout_dialog);
            builder.setPositiveButton("Add", (dialog, which) -> {
                EditText abSets = customLayout.findViewById(R.id.editTextNumberAbdominalSets);
                EditText armSets = customLayout.findViewById(R.id.editTextNumberArmSets);
                EditText backSets = customLayout.findViewById(R.id.editTextNumberBackSets);
                EditText chestSets = customLayout.findViewById(R.id.editTextNumberChestSets);
                EditText legSets = customLayout.findViewById(R.id.editTextNumberLegSets);
                EditText shoulderSets = customLayout.findViewById(R.id.editTextNumberShoulderSets);
                EditText duration = customLayout.findViewById(R.id.editTextNumberDuration);
                int abSetsInt = Integer.parseInt(abSets.getText().toString().equals("") ? "0" : abSets.getText().toString());
                int armSetsInt = Integer.parseInt(armSets.getText().toString().equals("") ? "0" : armSets.getText().toString());
                int backSetsInt = Integer.parseInt(backSets.getText().toString().equals("") ? "0" : backSets.getText().toString());
                int chestSetsInt = Integer.parseInt(chestSets.getText().toString().equals("") ? "0" : chestSets.getText().toString());
                int legSetsInt = Integer.parseInt(legSets.getText().toString().equals("") ? "0" : legSets.getText().toString());
                int shoulderSetsInt = Integer.parseInt(shoulderSets.getText().toString().equals("") ? "0" : shoulderSets.getText().toString());
                int durationInt = Integer.parseInt(duration.getText().toString().equals("") ? "0" : duration.getText().toString());
                long date = Instant.now().toEpochMilli();
                if (abSetsInt == 0 && armSetsInt == 0 && backSetsInt == 0 && chestSetsInt == 0 && legSetsInt == 0 && shoulderSetsInt == 0) {
                    Toast.makeText(this, "Please enter at least one set", Toast.LENGTH_SHORT).show();
                    return;
                }
                Workout workout = new Workout(
                        abSetsInt,
                        armSetsInt,
                        backSetsInt,
                        chestSetsInt,
                        legSetsInt,
                        shoulderSetsInt,
                        date,
                        durationInt);
                workoutList.add(0, workout);
                workoutRviewAdapter.notifyItemInserted(0);
                workoutRecyclerView.smoothScrollToPosition(0);
                noWorkoutsText.setVisibility(TextView.INVISIBLE);
                Snackbar.make(view, "Workout added", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            workoutList.remove(0);
                            workoutRviewAdapter.notifyItemRemoved(0);
                            if (workoutList.isEmpty()) {
                                noWorkoutsText.setVisibility(TextView.VISIBLE);
                            }
                        }).show();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });
            builder.show();
        }
    }

    private void getWorkoutsListTitle() {
        if (profileId.equals(currProfileId)) {
            workoutListTitle.setText(R.string.my_workouts);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference("profiles")
                    .child(profileId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Profile profile = dataSnapshot.getValue(Profile.class);
                            if (profile != null) {
                                String title = profile.getProfileName() + "'s Workouts";
                                workoutListTitle.setText(title);
                            } else {
                                Log.w("WorkoutList", "Failed to read value.");
                                String title = "Workouts";
                                workoutListTitle.setText(title);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("WorkoutList", "Failed to read value.", error.toException());
                        }
                    });
        }
    }

    private void createRecyclerView() {
        rLayoutManager = new LinearLayoutManager(this);
        workoutRecyclerView = findViewById(R.id.workoutListRecyclerView);
        workoutRecyclerView.setHasFixedSize(true);
        workoutRviewAdapter = new WorkoutRviewAdapter(workoutList);
        workoutRecyclerView.setAdapter(workoutRviewAdapter);
        workoutRecyclerView.setLayoutManager(rLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(workoutRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        workoutRecyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getLayoutPosition();
                        Workout removedWorkout = workoutList.get(position);
                        workoutList.remove(position);
                        workoutRviewAdapter.notifyItemRemoved(position);
                        Snackbar snackbar = Snackbar
                                .make(viewHolder.itemView.getRootView(), "Workout deleted", Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", v -> {
                            workoutList.add(position, removedWorkout);
                            workoutRviewAdapter.notifyItemInserted(position);
                            workoutRecyclerView.smoothScrollToPosition(position);
                        });
                        snackbar.show();
                    }
                }
        );
        if (profileId.equals(currProfileId)) { //TODO: Replace with Firebase Auth
            itemTouchHelper.attachToRecyclerView(workoutRecyclerView);
        }

    }
}