package edu.northeastern.myapplication.discoverpage;

import java.util.List;

public class Profiles {

    private String username;
    private String bio;
    private List<String> total_friends;
    private int total_workouts;
    private String user_token;
    private String image;


    public Profiles(String username, String bio,String user_token) {
        this.username = username;
        this.bio = bio;
        this.user_token = user_token;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String imgURL) {
        this.image = imgURL; // todo
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public void setTotal_friends(List<String> friends) {
        total_friends = friends;
    }

    public int getFriendListSize() {
        return total_friends.size();
    }
    public List<String> getTotal_friends() {
        return total_friends;
    }

    public int getWorkouts() {
        return total_workouts;
    }

    public String getUser_token() {
        return this.user_token;
    }
}
