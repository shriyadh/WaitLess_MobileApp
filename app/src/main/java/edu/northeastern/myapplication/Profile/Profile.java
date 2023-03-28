package edu.northeastern.myapplication.Profile;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class Profile {
    private String profileId;
    private String profileName;
    private long joinedDate;
    private Integer totalLifted;
    private Integer totalWorkouts;
    private String profileBio;


    public Profile(String profileName, long joinedDate, Integer totalLifted, Integer totalWorkouts, String profileBio) {
        this.profileName = profileName;
        this.joinedDate = joinedDate;
        this.totalLifted = totalLifted;
        this.totalWorkouts = totalWorkouts;
        this.profileBio = profileBio;
    }

    public Profile() {
        this.profileName = "User" + UUID.randomUUID();
        this.joinedDate = new Date().getTime();
        this.totalLifted = 0;
        this.totalWorkouts = 0;
        this.profileBio = "Default Bio";
    }

    public void uploadProfile() {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profiles");
        DatabaseReference newProfileRef = profileRef.push();
        setProfileId(newProfileRef.getKey());
        profileRef.push().setValue(this);
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Integer getTotalLifted() {
        return totalLifted;
    }

    public void setTotalLifted(Integer totalLifted) {
        this.totalLifted = totalLifted;
    }

    public Integer getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(Integer totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }
}

