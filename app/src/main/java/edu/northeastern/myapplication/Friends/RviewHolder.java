package edu.northeastern.myapplication.Friends;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.myapplication.R;

public class RviewHolder extends RecyclerView.ViewHolder {

    public final TextView profileName;
    public ImageView profileIcon;
    public ToggleButton friendButton;


    public RviewHolder(@NonNull View itemView, final FriendsClickListener listener) {
        super(itemView);
        this.profileName = itemView.findViewById(R.id.textViewFriendsListUsername);
        this.profileIcon = itemView.findViewById(R.id.imageViewFriendsListIcon);
        this.friendButton = itemView.findViewById(R.id.toggleButtonConnected);


        itemView.setOnClickListener(view -> {
            if (listener != null) {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFriendClick(position);
                }
            }
        });

        friendButton.setOnClickListener(view -> {
            if (listener != null) {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFriendButtonClick(position);
                }
            }
        });
    }
}
