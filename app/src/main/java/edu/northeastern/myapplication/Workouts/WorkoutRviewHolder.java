package edu.northeastern.myapplication.Workouts;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;

import edu.northeastern.myapplication.R;

public class WorkoutRviewHolder extends RecyclerView.ViewHolder {
    public final TextView workoutNumber;
    public final TextView workout1Total;
    public final TextView workout2Total;
    public final TextView workout3Total;
    public final TextView workout4Total;
    public final TextView workout5Total;
    public final TextView workout6Total;
    public final TextView workoutDuration;
    public final TextView workoutDate;
    public final BarChart workoutChart;


    public WorkoutRviewHolder(@NonNull View itemView) {
        super(itemView);
        this.workoutNumber = itemView.findViewById(R.id.textViewWorkoutNum);
        this.workout1Total = itemView.findViewById(R.id.textViewW1Num);
        this.workout2Total = itemView.findViewById(R.id.textViewW2Num);
        this.workout3Total = itemView.findViewById(R.id.textViewW3Num);
        this.workout4Total = itemView.findViewById(R.id.textViewW4Num);
        this.workout5Total = itemView.findViewById(R.id.textViewW5Num);
        this.workout6Total = itemView.findViewById(R.id.textViewW6Num);
        this.workoutDuration = itemView.findViewById(R.id.textViewDurationValue);
        this.workoutDate = itemView.findViewById(R.id.textViewWorkoutDate);
        this.workoutChart = itemView.findViewById(R.id.workoutItemChart);
    }
}
