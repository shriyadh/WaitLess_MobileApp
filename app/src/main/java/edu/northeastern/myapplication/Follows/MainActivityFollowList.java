package edu.northeastern.myapplication.Follows;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.myapplication.Profile.MainActivityProfile;
import edu.northeastern.myapplication.Profile.Profile;
import edu.northeastern.myapplication.R;

public class MainActivityFollowList extends AppCompatActivity {
    private List<String> followIdList = new ArrayList<>();
    private String profileId;
    private String currProfileId;
    private TextView followListTitle;
    private RecyclerView followRecyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManager;

    /**
     * Called when the activity is starting.  This is where most initialization
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_follow_list);

        followListTitle = findViewById(R.id.textViewFollowListTitle);

        Intent intent = getIntent();
        profileId = intent.getStringExtra("profileId");
        currProfileId = intent.getStringExtra("currProfileId");
        followIdList = intent.getStringArrayListExtra("followIdList");
        getFollowsListTitle();
        createRecyclerView();
    }

    /**
     *
     */
    private void createRecyclerView() {
        rLayoutManager = new LinearLayoutManager(this);
        followRecyclerView = findViewById(R.id.followListRecyclerView);
        followRecyclerView.setHasFixedSize(true);
        rviewAdapter = new RviewAdapter(profileId, followIdList);

        FollowClickListener listener = new FollowClickListener() {
            @Override
            public void onFollowClick(int position) {
                Log.v("FollowsList", "Follow Clicked: " + position);
                Intent intent = new Intent(getApplicationContext(), MainActivityProfile.class);
                intent.putExtra("profileId", followIdList.get(position));
                intent.putExtra("currProfileId", currProfileId);
                startActivity(intent);
            }

            @Override
            public void onFollowButtonClick(int position) {
                ToggleButton button = Objects.requireNonNull(followRecyclerView.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.toggleButtonFollowItem);
                if (button.isChecked()) {
                    Log.d("FollowsConnection", "Follow Removed: " + position);
                    FirebaseDatabase.getInstance()
                            .getReference("follows/" + followIdList.get(position) + "/" + currProfileId)
                            .removeValue();
                } else {
                    Log.d("FollowsConnection", "Follow Added: " + position);
                    FirebaseDatabase.getInstance()
                            .getReference("follows/" + followIdList.get(position) + "/" + currProfileId)
                            .setValue(true);
                }

            }
        };
        rviewAdapter.setOnFollowsClickListener(listener);
        followRecyclerView.setLayoutManager(rLayoutManager);
        followRecyclerView.setAdapter(rviewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(followRecyclerView.getContext(),
                ((LinearLayoutManager) rLayoutManager).getOrientation());
        followRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void getFollowsListTitle() {
        FirebaseDatabase.getInstance()
                .getReference("profiles/" + profileId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot != null) {
                            Profile profile = dataSnapshot.getValue(Profile.class);
                            if (profile != null) {
                                String title = profile.getProfileName() + "'s Followers";
                                followListTitle.setText(title);
                            }
                        }
                    }
                });
    }
}