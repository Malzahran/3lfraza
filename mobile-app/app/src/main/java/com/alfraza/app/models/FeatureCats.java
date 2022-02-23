package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureCats implements Parcelable {

    private String name;
    private FeatureRow[] features;
    private int id;

    private FeatureCats(Parcel in) {
        name = in.readString();
        features = in.createTypedArray(FeatureRow.CREATOR);
        id = in.readInt();
    }

    public static final Creator<FeatureCats> CREATOR = new Creator<FeatureCats>() {
        @Override
        public FeatureCats createFromParcel(Parcel in) {
            return new FeatureCats(in);
        }

        @Override
        public FeatureCats[] newArray(int size) {
            return new FeatureCats[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public FeatureRow[] getFeatures() {
        return features;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedArray(features, i);
        parcel.writeInt(id);
    }
}