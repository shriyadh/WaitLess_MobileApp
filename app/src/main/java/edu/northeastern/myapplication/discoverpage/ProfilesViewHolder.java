package edu.northeastern.myapplication.discoverpage;

import android.graphics.Color;
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

import java.util.List;

import edu.northeastern.myapplication.R;

public class ProfilesViewHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public TextView bio;
    public TextView friends;
    public TextView workouts;
    public ImageView picture;
    public Button connect_btn;
    public de.hdodenhof.circleimageview.CircleImageView small_icon;
    public String loggedInUser;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    public List<Profiles> profiles;

    public ProfilesViewHolder(@NonNull View view, RecycleViewClickListener lst, List<Profiles> listOfProfiles) {
        super(view);
        this.username = view.findViewById(R.id.username_main);
        this.bio = view.findViewById(R.id.bio_text);
        this.friends = view.findViewById(R.id.friends);
        this.workouts = view.findViewById(R.id.workouts);
        this.picture = view.findViewById(R.id.post_pic);
        this.connect_btn = view.findViewById(R.id.connect_btn);
        this.small_icon = view.findViewById(R.id.img_profile);
        this.profiles = listOfProfiles;
        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // loggedInUser = "1h9cxdj4GJeRFGawmUpk990Zg8b2";

        assert mUser != null;
        loggedInUser = mUser.getUid();

        //checking logged in user
       // System.out.println(mUser.getEmail());
        //System.out.println(loggedInUser);

        // send notification to clicked user --- change connected -> pending
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getLayoutPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    lst.onLinkClick(pos);


                    connect_btn.setClickable(false);

                    connect_btn.setText("FOLLOWING");
                    int color = ContextCompat.getColor(view.getContext(), R.color.grey);
                    connect_btn.setBackgroundColor(color);

                }
            }
        });

    }
}
