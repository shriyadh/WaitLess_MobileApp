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
        View view = layoutInflater.inflate(R.layout.discover_display_profiles, parent, false);

        return new ProfilesViewHolder(view, this.listener);    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {
        Profiles curr = profiles.get(position);

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
