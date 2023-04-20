package edu.northeastern.myapplication.notificationsPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.Profiles;
import edu.northeastern.myapplication.discoverpage.ProfilesViewHolder;
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
        holder.notifyImg.setImageResource(R.drawable.accept2);

        holder.notifyTxt.setText(curr.getUser() + " has sent you a connect request!\n");

    }



    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}

