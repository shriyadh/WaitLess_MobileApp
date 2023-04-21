package edu.northeastern.myapplication.notificationsPage;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.RecycleViewClickListener;

public class NotifyViewHolder extends RecyclerView.ViewHolder {

    TextView notifyTxt;
    ImageView notifyImg;

    Button followBack;



    public NotifyViewHolder(@NonNull View view, RecycleViewClickListener lst) {
        super(view);


        notifyImg = view.findViewById(R.id.follow_req_prof);
        notifyTxt = view.findViewById(R.id.notificationTxtView);
        followBack = view.findViewById(R.id.connect_btn);
        // send notification to clicked user --- change connected -> pending
        followBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // grab the position
                int pos = getLayoutPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    lst.onLinkClick(pos);

                }
            }
        });


    }




}