package edu.northeastern.myapplication.Workouts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.myapplication.R;

public class WorkoutRviewAdapter extends RecyclerView.Adapter<WorkoutRviewHolder> {

    private List<Workout> workoutList;
    private WorkoutListener listener;

    public WorkoutRviewAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    public void setOnWorkoutsListener(WorkoutListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutRviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutRviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_item, parent, false),
                listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutRviewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutNumber.setText(String.valueOf(workoutList.size() - position));
        holder.workout1Total.setText(String.valueOf(workout.getChest()));
        holder.workout2Total.setText(String.valueOf(workout.getBack()));
        holder.workout3Total.setText(String.valueOf(workout.getArms()));
        holder.workout4Total.setText(String.valueOf(workout.getAbdominal()));
        holder.workout5Total.setText(String.valueOf(workout.getLegs()));
        holder.workout6Total.setText(String.valueOf(workout.getShoulders()));
        holder.workoutDuration.setText(String.valueOf(workout.getDuration()));
        holder.workoutDate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US)
                .format(workout.getDate()));

        // TODO: Set up Chart
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, workout.getChest()));
        entries.add(new BarEntry(2f, workout.getBack()));
        entries.add(new BarEntry(3f, workout.getArms()));
        entries.add(new BarEntry(4f, workout.getAbdominal()));
        entries.add(new BarEntry(5f, workout.getLegs()));
        entries.add(new BarEntry(6f, workout.getShoulders()));

        BarDataSet dataSet = new BarDataSet(entries, "Workout History");

        BarData barData = new BarData(dataSet);

        holder.workoutChart.setData(barData);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setValueTextSize(16f);
        holder.workoutChart.getDescription().setEnabled(false);

        // Reload chart
        holder.workoutChart.notifyDataSetChanged();
        holder.workoutChart.invalidate();

    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }
}
