package edu.northeastern.myapplication.discoverpage;

public class Story {

    private String username;
    private boolean checkedIn;

    public Story(String username, boolean checkedIn) {
        this.username = username;
        this.checkedIn = checkedIn;
    }

    public String getUser() { return this.username; }
    public boolean isCheckedIn(){
        return this.checkedIn;
    }
}
