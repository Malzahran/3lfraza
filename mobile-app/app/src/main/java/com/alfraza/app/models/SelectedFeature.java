package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedFeature implements Parcelable {

    public static final Creator<SelectedFeature> CREATOR = new Creator<SelectedFeature>() {
        public SelectedFeature createFromParcel(Parcel in) {
            return new SelectedFeature(in);
        }

        public SelectedFeature[] newArray(int size) {
            return new SelectedFeature[size];
        }
    };
    private float price;
    private int id;
    private int sid;

    public SelectedFeature(int sid, int id, float price) {
        this.sid = sid;
        this.id = id;
        this.price = price;
    }

    private SelectedFeature(Parcel in) {
        price = in.readFloat();
        id = in.readInt();
        sid = in.readInt();
    }

    public float getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getSid() {
        return sid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeFloat(price);
        dest.writeInt(id);
        dest.writeInt(sid);
    }
}