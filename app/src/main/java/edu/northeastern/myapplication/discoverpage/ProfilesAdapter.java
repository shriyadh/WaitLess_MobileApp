package edu.northeastern.myapplication.discoverpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.myapplication.R;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesViewHolder> {

    private List<Profiles> profiles;
    private RecycleViewClickListener listener;

    public ProfilesAdapter(List<Profiles> profiles, Context con) {
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public ProfilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discover_connect, parent, false);

        return new ProfilesViewHolder(view, this.listener);    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {
        Profiles curr = profiles.get(position);
        holder.username.setText(curr.getUsername());
        holder.bio.setText(curr.getBio());
        holder.friends.setText("FRIENDS \n" + curr.getTotal_friends());
        holder.workouts.setText("WORKOUTS \n" + curr.getWorkouts());

        //holder.small_icon.set(curr.getUsername());
        //holder.picture.setText(curr.getUsername());

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
