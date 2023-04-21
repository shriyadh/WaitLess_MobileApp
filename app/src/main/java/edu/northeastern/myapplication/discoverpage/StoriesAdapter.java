package edu.northeastern.myapplication.discoverpage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;

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
        String token = curr.getToken();
        if(curr.isCheckedIn()) {
            holder.storyOutline.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
        }
        String username = stories.get(position).getUser();
        int maxLen = 8;

        if(position == 0){
            holder.user.setText(username);
            holder.user.setTextColor(Color.WHITE);
            holder.user.setTypeface(holder.user.getTypeface(), Typeface.BOLD);
        }
        else if(username.length() > maxLen) {
            // Modify the text to add "..." at the end
            String shortenedText = username.substring(0, maxLen) + "...";
            holder.user.setText(shortenedText);

        }
        else{
            holder.user.setText(username);

        }

        new Thread(() -> {
            System.out.println(token);
            FirebaseStorage
                    .getInstance()
                    .getReference("/profileIcons")
                    .child(token + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(holder.itemView.getContext())
                            .load(uri)
                            .placeholder(R.drawable.baseline_account_box_24)
                            .override(275, 275)
                            .apply(new RequestOptions()
                                    .transform(new CenterCrop(),
                                            new RoundedCorners(50)))
                            .into(holder.storyImage)).addOnFailureListener(e ->
                            Log.w("FollowRviewAdapter_ProfileIcon", "Failed to load profile image", e));
        }).start();

    }



    @Override
    public int getItemCount() {
        return stories.size();
    }
}
