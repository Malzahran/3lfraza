package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CartItems implements Parcelable {

    public Long id;
    public Long order_id = -1L;
    public Long product_id;
    public String product_name;
    public String image;
    public String currency;
    public int amount = 0;
    public int ftid = 0;
    public Long stock = 0L;
    public int a_stock = 0;
    public Double price_item;
    public Long created_at = 0L;
    public List<FeatureItem> features;

    public CartItems() {
    }

    private CartItems(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            order_id = null;
        } else {
            order_id = in.readLong();
        }
        if (in.readByte() == 0) {
            product_id = null;
        } else {
            product_id = in.readLong();
        }
        product_name = in.readString();
        image = in.readString();
        currency = in.readString();
        amount = in.readInt();
        ftid = in.readInt();
        if (in.readByte() == 0) {
            stock = null;
        } else {
            stock = in.readLong();
        }
        a_stock = in.readInt();
        if (in.readByte() == 0) {
            price_item = null;
        } else {
            price_item = in.readDouble();
        }
        if (in.readByte() == 0) {
            created_at = null;
        } else {
            created_at = in.readLong();
        }
        features = in.createTypedArrayList(FeatureItem.CREATOR);
    }

    public static final Creator<CartItems> CREATOR = new Creator<CartItems>() {
        @Override
        public CartItems createFromParcel(Parcel in) {
            return new CartItems(in);
        }

        @Override
        public CartItems[] newArray(int size) {
            return new CartItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (order_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(order_id);
        }
        if (product_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(product_id);
        }
        dest.writeString(product_name);
        dest.writeString(image);
        dest.writeString(currency);
        dest.writeInt(amount);
        dest.writeInt(ftid);
        if (stock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(stock);
        }
        dest.writeInt(a_stock);
        if (price_item == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price_item);
        }
        if (created_at == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(created_at);
        }
        dest.writeTypedList(features);
    }
}
