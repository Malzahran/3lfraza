package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {

    public Long id;
    public int a_rating;
    public float rating;
    public String notes;
    public String refuse;
    public String code;
    public String sub_total_fees;
    public String total_fees;
    public String delivery_fees;
    public String status = "";
    public String time = "";
    public Long created_at = System.currentTimeMillis();
    public List<CartItems> cart_list = new ArrayList<>();

    public Order() {
    }

    protected Order(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        a_rating = in.readInt();
        rating = in.readFloat();
        notes = in.readString();
        refuse = in.readString();
        code = in.readString();
        sub_total_fees = in.readString();
        total_fees = in.readString();
        delivery_fees = in.readString();
        status = in.readString();
        time = in.readString();
        if (in.readByte() == 0) {
            created_at = null;
        } else {
            created_at = in.readLong();
        }
        cart_list = in.createTypedArrayList(CartItems.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
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
        dest.writeInt(a_rating);
        dest.writeFloat(rating);
        dest.writeString(notes);
        dest.writeString(refuse);
        dest.writeString(code);
        dest.writeString(sub_total_fees);
        dest.writeString(total_fees);
        dest.writeString(delivery_fees);
        dest.writeString(status);
        dest.writeString(time);
        if (created_at == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(created_at);
        }
        dest.writeTypedList(cart_list);
    }
}



