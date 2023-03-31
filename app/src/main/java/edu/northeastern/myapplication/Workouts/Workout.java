package edu.northeastern.myapplication.Workouts;

public class Workout {
    private int abdominal;
    private int arms;
    private int back;
    private int chest;
    private int legs;
    private int shoulders;
    private long date;
    private int duration;

    public Workout(int abdominal, int arms, int back, int chest, int legs, int shoulders, long date, int duration) {
        this.abdominal = abdominal;
        this.arms = arms;
        this.back = back;
        this.chest = chest;
        this.legs = legs;
        this.shoulders = shoulders;
        this.date = date;
        this.duration = duration;
    }

    public Workout() {
        this.abdominal = 0;
        this.arms = 0;
        this.back = 0;
        this.chest = 0;
        this.legs = 0;
        this.shoulders = 0;
        this.date = 0;
        this.duration = 0;
    }

    public int getAbdominal() {
        return abdominal;
    }

    public void setAbdominal(int abdominal) {
        this.abdominal = abdominal;
    }

    public int getArms() {
        return arms;
    }

    public void setArms(int arms) {
        this.arms = arms;
    }

    public int getBack() {
        return back;
    }

    public void setBack(int back) {
        this.back = back;
    }

    public int getChest() {
        return chest;
    }

    public void setChest(int chest) {
        this.chest = chest;
    }

    public int getLegs() {
        return legs;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public int getShoulders() {
        return shoulders;
    }

    public void setShoulders(int shoulders) {
        this.shoulders = shoulders;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
