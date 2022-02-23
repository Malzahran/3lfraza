package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Images implements Parcelable {
    private String name;
    private String small, medium, large;
    private String timestamp;
    private IntentData intentData;

    public Images(String name,
                  String image_url) {
        this.name = name;
        this.medium = image_url;
        this.large = image_url;
    }


    private Images(Parcel in) {
        name = in.readString();
        small = in.readString();
        medium = in.readString();
        large = in.readString();
        timestamp = in.readString();
        intentData = in.readParcelable(IntentData.class.getClassLoader());
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public IntentData getIntent() {
        return intentData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(small);
        dest.writeString(medium);
        dest.writeString(large);
        dest.writeString(timestamp);
        dest.writeParcelable(intentData, flags);
    }
}