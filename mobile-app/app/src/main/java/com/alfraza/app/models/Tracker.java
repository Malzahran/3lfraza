package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Tracker implements Parcelable {
    public static final Creator<Tracker> CREATOR = new Creator<Tracker>() {
        @Override
        public Tracker createFromParcel(Parcel in) {
            return new Tracker(in);
        }

        @Override
        public Tracker[] newArray(int size) {
            return new Tracker[size];
        }
    };
    private double latitude;
    private double longitude;
    private double speed;
    private double accuracy;
    private double altitude;
    private float direction;
    private String eventtype;
    private String distance;
    private String method;

    public Tracker() {

    }

    private Tracker(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        speed = in.readDouble();
        accuracy = in.readDouble();
        altitude = in.readDouble();
        direction = in.readFloat();
        eventtype = in.readString();
        distance = in.readString();
        method = in.readString();
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public void setDirection(Float direction) {
        this.direction = direction;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(speed);
        dest.writeDouble(accuracy);
        dest.writeDouble(altitude);
        dest.writeFloat(direction);
        dest.writeString(eventtype);
        dest.writeString(distance);
        dest.writeString(method);
    }
}
