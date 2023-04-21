package edu.northeastern.myapplication.discoverpage;

public class Profiles {

    private String username;
    private String bio;
    private String user_token;

    public Profiles(String username, String bio,String user_token) {
        this.username = username;
        this.bio = bio;
        this.user_token = user_token;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getUser_token() {
        return this.user_token;
    }
}
