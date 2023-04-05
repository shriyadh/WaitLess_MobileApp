package edu.northeastern.myapplication.Profile;

import java.util.Date;
import java.util.UUID;

public class Profile {
    //    private String profileId;
    private String profileName;
    private String profileEmail;
    private long joinedDate;
    private String profileBio;


    public Profile(String profileName, String profileEmail, long joinedDate, String profileBio) {
        this.profileName = profileName;
        this.profileEmail = profileEmail;
        this.joinedDate = joinedDate;
        this.profileBio = profileBio;
    }

    public Profile() {
        this.profileName = "User" + UUID.randomUUID();
        this.profileEmail = "Default Email";
        this.joinedDate = new Date().getTime();
        this.profileBio = "Default Bio";
    }

//    TODO: Potentially don't need profile to upload profile to database
//    public void uploadProfile() {
//        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profiles");
//        DatabaseReference newProfileRef = profileRef.push();
//        setProfileId(newProfileRef.getKey());
//        profileRef.push().setValue(this);
//    }
//
//    TODO: Potentially don't need to include profile id in profile
//    public String getProfileId() {
//        return profileId;
//    }
//    public void setProfileId(String profileId) {
//        this.profileId = profileId;
//    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }


    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }
}

