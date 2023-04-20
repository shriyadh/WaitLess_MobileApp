package edu.northeastern.myapplication.Profile;

import java.time.Instant;
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
        this.profileBio = profileBio;
        this.joinedDate = joinedDate;
    }

    public Profile() {
        this.profileName = "User" + UUID.randomUUID();
        this.firstname = "Default First Name";
        this.lastname = "Default Last Name";
        this.profileEmail = "Default Email";
        this.joinedDate = Instant.now().toEpochMilli();
        this.profileBio = "Default Bio";
    }

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
