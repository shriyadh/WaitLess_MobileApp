package edu.northeastern.myapplication.notificationsPage;

public class Notification {

    private String img;
    private String user;
    private String mutual;

    private String username;



    public Notification( String user, String username){
        this.img = "";
        this.username = username;
        this.user = user;
       // this.mutual = mutual;

    }

    public String getUser(){
        return this.user;
    }

    public String getUserName() {
        return this.username;
    }
    public String getImg(){
        return this.img;
    }
    public  String getMutual(){
        return this.mutual;
    }






}
