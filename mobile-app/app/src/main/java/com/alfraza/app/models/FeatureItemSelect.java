package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureItemSelect implements Parcelable {
    public double price;
    public double dsc_price;
    public int id;
    public int sid;

    public FeatureItemSelect(int sid, int id, double price, double dsc_price) {
        this.sid = sid;
        this.id = id;
        this.price = price;
        this.dsc_price = dsc_price;
    }

    private FeatureItemSelect(Parcel in) {
        price = in.readDouble();
        dsc_price = in.readDouble();
        id = in.readInt();
        sid = in.readInt();
    }

    public static final Creator<FeatureItemSelect> CREATOR = new Creator<FeatureItemSelect>() {
        @Override
        public FeatureItemSelect createFromParcel(Parcel in) {
            return new FeatureItemSelect(in);
        }

        @Override
        public FeatureItemSelect[] newArray(int size) {
            return new FeatureItemSelect[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(price);
        dest.writeDouble(dsc_price);
        dest.writeInt(id);
        dest.writeInt(sid);
    }
}