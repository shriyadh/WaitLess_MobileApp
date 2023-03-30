package edu.northeastern.myapplication.discoverpage;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.myapplication.R;

public class ProfilesViewHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public TextView bio;
    public TextView friends;
    public TextView workouts;
    public ImageView picture;
    public Button connect_btn;
    public de.hdodenhof.circleimageview.CircleImageView small_icon;


    public ProfilesViewHolder(@NonNull View view, RecycleViewClickListener lst) {
        super(view);
        this.username = view.findViewById(R.id.username_main);
        this.bio = view.findViewById(R.id.bio_text);
        this.friends = view.findViewById(R.id.friends);
        this.workouts = view.findViewById(R.id.workouts);
        this.picture = view.findViewById(R.id.post_pic);
        this.connect_btn = view.findViewById(R.id.connect_btn);
        this.small_icon = view.findViewById(R.id.img_profile);


        // send notification to clicked user --- change connected -> pending
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getLayoutPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    lst.onLinkClick(pos);

                    connect_btn.setClickable(false);

                    connect_btn.setText("REQUESTED");
                    int color = ContextCompat.getColor(view.getContext(), R.color.grey);
                    connect_btn.setBackgroundColor(color);


                }
            }
        });

    }
}
