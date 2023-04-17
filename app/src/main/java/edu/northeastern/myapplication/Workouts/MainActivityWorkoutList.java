package edu.northeastern.myapplication.Workouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        currProfileId = bundle.getString("currProfileId"); //TODO: remove this

        workoutListTitle = findViewById(R.id.textViewWorkoutListTitle);
        noWorkoutsText = findViewById(R.id.textViewNoWorkouts);

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