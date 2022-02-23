package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Cart implements Parcelable {

    public Long id;
    public int count;
    public String total;
    public String delivery;
    public String subtotal;
    public String discount;
    public String promo_error;
    public String promo_code;
    public String currency;
    public String notes;
    public String time_label;
    public String time;
    public List<CartItems> data;
    public List<FormInputData> radio;

    private Cart(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        count = in.readInt();
        total = in.readString();
        delivery = in.readString();
        subtotal = in.readString();
        discount = in.readString();
        promo_error = in.readString();
        promo_code = in.readString();
        currency = in.readString();
        notes = in.readString();
        time_label = in.readString();
        time = in.readString();
        data = in.createTypedArrayList(CartItems.CREATOR);
        radio = in.createTypedArrayList(FormInputData.CREATOR);
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
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
        dest.writeInt(count);
        dest.writeString(total);
        dest.writeString(delivery);
        dest.writeString(subtotal);
        dest.writeString(discount);
        dest.writeString(promo_error);
        dest.writeString(promo_code);
        dest.writeString(currency);
        dest.writeString(notes);
        dest.writeString(time_label);
        dest.writeString(time);
        dest.writeTypedList(data);
        dest.writeTypedList(radio);
    }
}
