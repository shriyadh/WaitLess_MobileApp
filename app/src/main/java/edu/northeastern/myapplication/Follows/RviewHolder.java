package edu.northeastern.myapplication.Follows;

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
    public ToggleButton followButton;


    public RviewHolder(@NonNull View itemView, final FollowClickListener listener) {
        super(itemView);
        this.profileName = itemView.findViewById(R.id.textViewFriendsListUsername);
        this.profileIcon = itemView.findViewById(R.id.imageViewFriendsListIcon);
        this.followButton = itemView.findViewById(R.id.toggleButtonFollowItem);


        itemView.setOnClickListener(view -> {
            if (listener != null) {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFollowClick(position);
                }
            }
        });

        followButton.setOnClickListener(view -> {
            if (listener != null) {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFollowButtonClick(position);
                }
            }
        });
    }
}
