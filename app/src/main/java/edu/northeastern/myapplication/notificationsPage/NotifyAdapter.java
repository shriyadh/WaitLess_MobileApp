package edu.northeastern.myapplication.notificationsPage;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.RecycleViewClickListener;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyViewHolder> {

    private List<Notification> notificationsList;
    private RecycleViewClickListener listener;

    public NotifyAdapter(List<Notification> notificationsList, Context con) {
        this.notificationsList = notificationsList;
    }

    public void setListenerLink(RecycleViewClickListener lst) {
        this.listener = lst;
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.notify_item, parent, false);
        return new NotifyViewHolder(view, this.listener);    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        Notification curr = notificationsList.get(position);
        //holder.notifyImg.setImageResource(R.drawable.accept2);
        String token = curr.getUserID();
        System.out.println(token);


           FirebaseDatabase.getInstance().getReference("profiles/" + token)
                    .child("profileName").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()) {
                               System.out.println(snapshot);
                               String n = snapshot.getValue().toString();
                               holder.notifyTxt.setText(n + " is now \nfollowing you!");
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });



        new Thread(() -> {
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
                            .into(holder.notifyImg)).addOnFailureListener(e ->
                            Log.w("FollowRviewAdapter_ProfileIcon", "Failed to load profile image", e));
        }).start();
    }


    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}

