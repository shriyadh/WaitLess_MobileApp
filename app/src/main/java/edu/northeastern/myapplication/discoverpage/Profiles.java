package edu.northeastern.myapplication.discoverpage;

public class Profiles {

    private String username;
    private String bio;
    private int total_friends;
    private int workouts;
    private long id;
    private String image;


    public Profiles(String username, String bio, String img, int total_friends, int workouts, long id) {
        this.username = username;
        this.bio = bio;
        this.total_friends = total_friends;
        this.workouts = workouts;
        this.id = id;
        this.image = img;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public int getTotal_friends() {
        return total_friends;
    }

    public int getWorkouts() {
        return workouts;
    }

    public long getId() {
        return id;
    }
}
