package edu.northeastern.myapplication.discoverpage;

public class Story {

    private String username;
    private boolean checkedIn;
    private String token;

    public Story(String username, String token) {
        this.username = username;
        this.token = token;
        this.checkedIn = true;
    }

    public String getUser() { return this.username; }
    public boolean isCheckedIn(){
        return this.checkedIn;
    }
    public void setCheckIn(boolean check) {
        this.checkedIn = check;
    }

    public String getToken() {
        return this.token;
    }
}
