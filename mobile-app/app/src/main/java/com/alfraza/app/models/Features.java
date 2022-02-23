package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Features implements Parcelable {

    public String name;
    public List<FeatureItem> features;
    public int id;

    private Features(Parcel in) {
        name = in.readString();
        features = in.createTypedArrayList(FeatureItem.CREATOR);
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(features);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Features> CREATOR = new Creator<Features>() {
        @Override
        public Features createFromParcel(Parcel in) {
            return new Features(in);
        }

        @Override
        public Features[] newArray(int size) {
            return new Features[size];
        }
    };
}