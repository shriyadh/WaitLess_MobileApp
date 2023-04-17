package edu.northeastern.myapplication.Workouts;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.myapplication.R;

public class WorkoutRviewAdapter extends RecyclerView.Adapter<WorkoutRviewHolder> {

    private List<Workout> workoutList;
    public WorkoutRviewAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutRviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutRviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_item, parent, false));
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

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, workout.getChest()));
        entries.add(new BarEntry(1f, workout.getBack()));
        entries.add(new BarEntry(2f, workout.getArms()));
        entries.add(new BarEntry(3f, workout.getAbdominal()));
        entries.add(new BarEntry(4f, workout.getLegs()));
        entries.add(new BarEntry(5f, workout.getShoulders()));

        Resources res = holder.itemView.getResources();
        String[] xLabels = res.getStringArray(R.array.workout_names);

        XAxis xAxis = holder.workoutChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));


        BarDataSet dataSet = new BarDataSet(entries, ("Workout #" + (workoutList.size() - position)));
        BarData barData = new BarData(dataSet);
        holder.workoutChart.setData(barData);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        holder.workoutChart.getDescription().setEnabled(false);
        holder.workoutChart.getLegend().setEnabled(false);
        holder.workoutChart.animateY(1500);

        // Reload chart
        holder.workoutChart.notifyDataSetChanged();
        holder.workoutChart.invalidate();

    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }
}
