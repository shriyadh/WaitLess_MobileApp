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
    private List<String> friendsIdList = new ArrayList<>();
    private String profileId;
    private String currProfileId;
    private TextView friendsListTitle;
    private RecyclerView friendsRecyclerView;
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
        setContentView(R.layout.activity_main_friends_list);

        friendsListTitle = findViewById(R.id.textViewFollowListTitle);

        Intent intent = getIntent();
        profileId = intent.getStringExtra("profileId");
        currProfileId = intent.getStringExtra("currProfileId");
        friendsIdList = intent.getStringArrayListExtra("friendsIdList");
        getFriendsListTitle();
        createRecyclerView();
    }

    /**
     *
     */
    private void createRecyclerView() {
        rLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView = findViewById(R.id.friendsListRecyclerView);
        friendsRecyclerView.setHasFixedSize(true);
        rviewAdapter = new RviewAdapter(profileId, friendsIdList);

        FollowClickListener listener = new FollowClickListener() {
            @Override
            public void onFriendClick(int position) {
                Log.v("FriendsList", "Friend Clicked: " + position);
                Intent intent = new Intent(getApplicationContext(), MainActivityProfile.class);
                intent.putExtra("profileId", friendsIdList.get(position));
                intent.putExtra("currProfileId", currProfileId);
                startActivity(intent);
            }

            @Override
            public void onFollowButtonClick(int position) {
                ToggleButton button = Objects.requireNonNull(friendsRecyclerView.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.toggleButtonConnected);
                if (button.isChecked()) {
                    Log.d("FriendsConnection", "Friend Removed: " + position);
                    FirebaseDatabase.getInstance()
                            .getReference("friends/" + currProfileId + "/" + friendsIdList.get(position))
                            .removeValue();
                } else {
                    Log.d("FriendsConnection", "Friend Added: " + position);
                    FirebaseDatabase.getInstance()
                            .getReference("friends/" + currProfileId + "/" + friendsIdList.get(position))
                            .setValue(true);
                }

            }
        };
        rviewAdapter.setOnFriendsClickListener(listener);
        friendsRecyclerView.setLayoutManager(rLayoutManager);
        friendsRecyclerView.setAdapter(rviewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(friendsRecyclerView.getContext(),
                ((LinearLayoutManager) rLayoutManager).getOrientation());
        friendsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void getFriendsListTitle() {
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
                                friendsListTitle.setText(title);
                            }
                        }
                    }
                });
    }
}