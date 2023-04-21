package edu.northeastern.myapplication.Workouts;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Workout implements Parcelable {
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
        this.date = Instant.now().toEpochMilli();
        this.duration = 0;
    }

    public Workout(Parcel in) {
        abdominal = in.readInt();
        arms = in.readInt();
        back = in.readInt();
        chest = in.readInt();
        legs = in.readInt();
        shoulders = in.readInt();
        date = in.readLong();
        duration = in.readInt();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(abdominal);
        dest.writeInt(arms);
        dest.writeInt(back);
        dest.writeInt(chest);
        dest.writeInt(legs);
        dest.writeInt(shoulders);
        dest.writeLong(date);
        dest.writeInt(duration);
    }
}
