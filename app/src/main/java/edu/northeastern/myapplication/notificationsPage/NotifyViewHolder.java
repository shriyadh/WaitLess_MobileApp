package edu.northeastern.myapplication.notificationsPage;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.myapplication.R;
import edu.northeastern.myapplication.discoverpage.RecycleViewClickListener;

public class NotifyViewHolder extends RecyclerView.ViewHolder {

    TextView notifyTxt;
    ImageView notifyImg;


    public NotifyViewHolder(@NonNull View view, RecycleViewClickListener lst) {
        super(view);

        notifyImg = view.findViewById(R.id.notificationImg);
        notifyTxt = view.findViewById(R.id.notificationTxtView);

    }
}
