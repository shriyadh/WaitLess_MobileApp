package edu.northeastern.myapplication.discoverpage;

import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.myapplication.R;

public class StoriesViewHolder extends RecyclerView.ViewHolder {
    CardView storyOutline;

    public StoriesViewHolder(View view, RecycleViewClickListener lst){
        super(view);

        storyOutline = view.findViewById(R.id.outline);
    }
}