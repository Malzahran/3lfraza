package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureRow implements Parcelable {

    public static final Creator<FeatureRow> CREATOR = new Creator<FeatureRow>() {
        public FeatureRow createFromParcel(Parcel in) {
            return new FeatureRow(in);
        }

        public FeatureRow[] newArray(int size) {
            return new FeatureRow[size];
        }
    };
    private float price;
    private String name;
    private int id;

    private FeatureRow(Parcel in) {
        price = in.readFloat();
        id = in.readInt();
        name = in.readString();
    }

    public float getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FeatureRow) {
            FeatureRow c = (FeatureRow) obj;
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

        dest.writeFloat(price);
        dest.writeInt(id);
        dest.writeString(name);
    }
}