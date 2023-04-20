package edu.northeastern.myapplication.discoverpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import edu.northeastern.myapplication.R;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesViewHolder> {

    private List<Profiles> profiles;
    private RecycleViewClickListener listener;

    public ProfilesAdapter(List<Profiles> profiles, Context con) {
        this.profiles = profiles;
    }

    public void setListenerLink(RecycleViewClickListener lst) {
        this.listener = lst;
    }

    @NonNull
    @Override
    public ProfilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discover_connect, parent, false);

        return new ProfilesViewHolder(view, this.listener, this.profiles);    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {
        Profiles curr = profiles.get(position);
        holder.username.setText(curr.getUsername());
        holder.bio.setText(curr.getBio());
        holder.friends.setText("FRIENDS \n" + curr.getFriendListSize());
        holder.workouts.setText("WORKOUTS \n" + curr.getWorkouts());
        String token = curr.getUser_token();

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
                        .into(holder.picture)).addOnFailureListener(e ->
                        Log.w("FollowRviewAdapter_ProfileIcon", "Failed to load profile image", e));
    }).start();


//        FirebaseStorage.getInstance().getReference("/profileIcons").child(token+".jpg")
//                .getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this)
//                        .load(uri)
//                        .
//                )

        //holder.small_icon.set(curr.getUsername());
        //holder.picture.setText(curr.getUsername());

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
