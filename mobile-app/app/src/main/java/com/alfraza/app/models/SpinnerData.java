package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SpinnerData implements Parcelable {

    public static final Creator<SpinnerData> CREATOR = new Creator<SpinnerData>() {
        @Override
        public SpinnerData createFromParcel(Parcel in) {
            return new SpinnerData(in);
        }

        @Override
        public SpinnerData[] newArray(int size) {
            return new SpinnerData[size];
        }
    };
    private int id;
    private String name;


    public SpinnerData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private SpinnerData(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //to display object as a string in spinner
    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpinnerData) {
            SpinnerData c = (SpinnerData) obj;
            return c.getName().equals(name) && c.getId() == id;
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}