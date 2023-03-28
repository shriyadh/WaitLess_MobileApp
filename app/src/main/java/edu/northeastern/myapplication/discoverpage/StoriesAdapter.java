package edu.northeastern.myapplication.discoverpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.myapplication.R;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesViewHolder> {

    private List<Story> stories;
    private RecycleViewClickListener listener;
    private Context context;


    public StoriesAdapter(List<Story> stories, Context con) {
        this.stories = stories;
        this.context = con;
    }

    public void setListenerLink(RecycleViewClickListener lst) {
        this.listener = lst;
    }


    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discover_story_item, parent, false);

        return new StoriesViewHolder(view, this.listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position) {
        Story curr = stories.get(position);

        if(curr.isSeen()) {
            holder.storyOutline.setCardBackgroundColor(context.getResources().getColor(R.color.grey));

        }


    }



    @Override
    public int getItemCount() {
        return stories.size();
    }
}
