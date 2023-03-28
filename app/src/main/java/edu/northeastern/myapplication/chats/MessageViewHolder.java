package edu.northeastern.myapplication.chats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.myapplication.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView sentBy;
    public ImageView image;

    public MessageViewHolder(View view) {
        super(view);
        this.sentBy = view.findViewById(R.id.textViewUserSent);
        this.image = view.findViewById(R.id.imageViewStickerSent);
    }
}
