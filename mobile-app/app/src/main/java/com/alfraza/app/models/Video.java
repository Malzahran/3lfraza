package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    private String name;
    private String thumb, videourl;

    private Video(Parcel in) {
        name = in.readString();
        thumb = in.readString();
        videourl = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getThumb() {
        return thumb;
    }

    public String getVideourl() {
        return videourl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(thumb);
        dest.writeString(videourl);
    }
}