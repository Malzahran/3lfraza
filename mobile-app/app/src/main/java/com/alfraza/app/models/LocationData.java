package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationData implements Parcelable {
    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };
    private float lat;
    private float longt;
    private int color;
    private String title;
    private String snippet;

    private LocationData(Parcel in) {
        lat = in.readFloat();
        longt = in.readFloat();
        color = in.readInt();
        title = in.readString();
        snippet = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public int getColor() {
        return color;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return longt;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(lat);
        dest.writeFloat(longt);
        dest.writeInt(color);
        dest.writeString(title);
        dest.writeString(snippet);
    }
}
