package edu.northeastern.myapplication.Follows;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import edu.northeastern.myapplication.Profile.Profile;
import edu.northeastern.myapplication.R;

public class FollowRviewAdapter extends RecyclerView.Adapter<FollowRviewHolder>{
    private String currentUserId = "-NRVYvTjwCGKqGm9dUIq";
    private String profileId;
    private List<String> followIdList;
    private FollowClickListener listener;

    public FollowRviewAdapter(String profileId, List<String> followIdList) {
        this.profileId = profileId;
        this.followIdList = followIdList;
    }

    public void setOnFollowsClickListener(FollowClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FollowRviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowRviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_list_item, parent, false),
                listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowRviewHolder holder, int position) {
        if (followIdList.size() > 0) {
            String followId = followIdList.get(position);
            new Thread(() -> {
                // Check if followID is in the list of follow of the current user
                FirebaseDatabase.getInstance()
                        .getReference("follows/" + followId)
                        .child(currentUserId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.followButton.setChecked(!snapshot.exists());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("FollowRviewAdapter", "onCancelled: " + error.getMessage());
                            }
                        });

                FirebaseDatabase.getInstance()
                        .getReference("profiles")
                        .child(followId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Profile follow = snapshot.getValue(Profile.class);
                                if (follow != null) {
                                    holder.profileName.setText(follow.getProfileName());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("FollowRviewAdapter", "onCancelled: " + error.getMessage());
                            }
                        });
                FirebaseStorage
                        .getInstance()
                        .getReference("/profileIcons")
                        .child(followId + ".jpg")
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(holder.itemView.getContext())
                                .load(uri)
                                .placeholder(R.drawable.baseline_account_box_24)
                                .override(275, 275)
                                .apply(new RequestOptions()
                                        .transform(new CenterCrop(),
                                                new RoundedCorners(50)))
                                .into(holder.profileIcon)).addOnFailureListener(e ->
                                Log.w("FollowRviewAdapter_ProfileIcon", "Failed to load profile image", e));
            }).start();
        }
    }

    @Override
    public int getItemCount() {
        return followIdList.size();
    }
}
