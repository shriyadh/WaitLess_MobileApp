package edu.northeastern.myapplication.notificationsPage;

public class Notification {

    private String img;
    private String user;
    private String mutual;

    private String userID;



    public Notification(String userID){
        this.img = "";
        this.userID = userID;
        this.user = user;
       // this.mutual = mutual;

    }

    public String getUser(){
        return this.user;
    }

    public String getUserID() {
        return this.userID;
    }
    public String getImg(){
        return this.img;
    }
    public  String getMutual(){
        return this.mutual;
    }






}
