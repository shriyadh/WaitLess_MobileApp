package edu.northeastern.myapplication.Friends;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.northeastern.myapplication.Profile.Profile;
import edu.northeastern.myapplication.R;

public class RviewAdapter extends RecyclerView.Adapter<RviewHolder>{
    private String currentUserId = "-NRVYvTjwCGKqGm9dUIq";
    private String profileId;
    private List<String> friendsIdList;
    private FriendsClickListener listener;

    public RviewAdapter(String profileId, List<String> friendsIdList) {
        this.profileId = profileId;
        this.friendsIdList = friendsIdList;
    }

    public void setOnFriendsClickListener(FriendsClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist_item, parent, false),
                listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RviewHolder holder, int position) {
        if (friendsIdList.size() > 0) {
            String friendId = friendsIdList.get(position);
            new Thread(() -> {
                // Check if friendID is in the list of friends of the current user
                FirebaseDatabase.getInstance()
                        .getReference("friends/" + currentUserId)
                        .child(friendId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.friendButton.setChecked(!snapshot.exists());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("RviewAdapter", "onCancelled: " + error.getMessage());
                            }
                        });

                FirebaseDatabase.getInstance()
                        .getReference("profiles")
                        .child(friendId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Profile friend = snapshot.getValue(Profile.class);
                                if (friend != null) {
                                    holder.profileName.setText(friend.getProfileName());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("RviewAdapter", "onCancelled: " + error.getMessage());
                            }
                        });
                FirebaseStorage
                        .getInstance()
                        .getReference("/profileIcons")
                        .child(friendId + ".jpg")
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(holder.itemView.getContext())
                                .load(uri)
                                .placeholder(R.drawable.baseline_account_box_24)
                                .override(275, 275)
                                .apply(new RequestOptions()
                                        .transform(new CenterCrop(),
                                                new RoundedCorners(50)))
                                .into(holder.profileIcon)).addOnFailureListener(e ->
                                Log.w("RviewAdapter_ProfileIcon", "Failed to load profile image", e));
            }).start();
        }
    }

    @Override
    public int getItemCount() {
        return friendsIdList.size();
    }
}
