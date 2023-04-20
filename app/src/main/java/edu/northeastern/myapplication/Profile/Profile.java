package edu.northeastern.myapplication.Profile;

import java.util.Date;
import java.util.UUID;

public class Profile {
    private String profileName;
    private String firstname;
    private String lastname;
    private String profileEmail;
    private long joinedDate;
    private String profileBio;


    public Profile(String profileName, String firstname, String lastname, String profileEmail, String profileBio, Long joinedDate) {
        this.profileName = profileName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileEmail = profileEmail;
        this.joinedDate = joinedDate;
        this.profileBio = profileBio;
    }

    public Profile() {
        this.profileName = "User" + UUID.randomUUID();
        this.firstname = "Default First Name";
        this.lastname = "Default Last Name";
        this.profileEmail = "Default Email";
        this.joinedDate = new Date().getTime();
        this.profileBio = "Default Bio";
    }

    // TODO: Potentially don't need
//    protected Profile(Parcel in) {
//        profileName = in.readString();
//        profileEmail = in.readString();
//        joinedDate = in.readLong();
//        profileBio = in.readString();
//    }
//
//    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
//        @Override
//        public Profile createFromParcel(Parcel source) {
//            return new Profile(source);
//        }
//
//        @Override
//        public Profile[] newArray(int size) {
//            return new Profile[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) {
//        dest.writeString(profileName);
//        dest.writeString(profileEmail);
//        dest.writeLong(joinedDate);
//        dest.writeString(profileBio);
//    }

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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
