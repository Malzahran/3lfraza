package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class FeatureItem implements Parcelable {
    public double price;
    public double dsc_price;
    public String name;
    public int id;


    private FeatureItem(Parcel in) {
        price = in.readDouble();
        dsc_price = in.readDouble();
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<FeatureItem> CREATOR = new Creator<FeatureItem>() {
        @Override
        public FeatureItem createFromParcel(Parcel in) {
            return new FeatureItem(in);
        }

        @Override
        public FeatureItem[] newArray(int size) {
            return new FeatureItem[size];
        }
    };

    //to display object as a string in spinner
    @NonNull
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
        dest.writeDouble(price);
        dest.writeDouble(dsc_price);
        dest.writeString(name);
        dest.writeInt(id);
    }
}