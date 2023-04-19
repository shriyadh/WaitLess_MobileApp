package edu.northeastern.myapplication.notificationsPage;

public class Notification {

    private String img;
    private String user;
    private String mutual;

    public Notification( String user, String mutual){
        this.img = "";
        this.user = user;
        this.mutual = mutual;

    }

    public String getUser(){
        return this.user;
    }
    public String getImg(){
        return this.img;
    }
    public  String getMutual(){
        return this.mutual;
    }
}