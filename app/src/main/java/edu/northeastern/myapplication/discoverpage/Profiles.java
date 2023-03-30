package edu.northeastern.myapplication.discoverpage;

public class Profiles {

    private String username;
    private String bio;
    private String total_friends;
    private String workouts;
    private String user_token;
    private String image;


    public Profiles(String username, String bio, String img, String total_friends, String workouts, String user_token) {
        this.username = username;
        this.bio = bio;
        this.total_friends = total_friends;
        this.workouts = workouts;
        this.user_token = user_token;
        this.image = img;
    }

    public String getImage() {
        return this.image;
    }
    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getTotal_friends() {
        return total_friends;
    }

    public String getWorkouts() {
        return workouts;
    }

    public String getUser_token() {
        return this.user_token;
    }
}
