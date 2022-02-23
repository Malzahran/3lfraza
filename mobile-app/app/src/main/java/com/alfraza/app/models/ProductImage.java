package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductImage implements Parcelable {

    public Long product_id;
    public String name;
    public String full;

    public ProductImage() {

    }

    private ProductImage(Parcel in) {
        if (in.readByte() == 0) {
            product_id = null;
        } else {
            product_id = in.readLong();
        }
        name = in.readString();
        full = in.readString();
    }

    public static final Creator<ProductImage> CREATOR = new Creator<ProductImage>() {
        @Override
        public ProductImage createFromParcel(Parcel in) {
            return new ProductImage(in);
        }

        @Override
        public ProductImage[] newArray(int size) {
            return new ProductImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (product_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(product_id);
        }
        dest.writeString(name);
        dest.writeString(full);
    }
}
