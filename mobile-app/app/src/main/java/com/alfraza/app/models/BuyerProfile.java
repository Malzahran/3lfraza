package com.alfraza.app.models;


import android.os.Parcel;
import android.os.Parcelable;

public class BuyerProfile implements Parcelable {

    public String name;
    public String email;
    public String phone;
    public String street;
    public String building;
    public String floor;
    public String apartment;
    public String additional;
    public String comment;
    public String promo_code;
    public int time_slot;

    public BuyerProfile() {

    }

    private BuyerProfile(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        street = in.readString();
        building = in.readString();
        floor = in.readString();
        apartment = in.readString();
        additional = in.readString();
        comment = in.readString();
        promo_code = in.readString();
        time_slot = in.readInt();
    }

    public static final Creator<BuyerProfile> CREATOR = new Creator<BuyerProfile>() {
        @Override
        public BuyerProfile createFromParcel(Parcel in) {
            return new BuyerProfile(in);
        }

        @Override
        public BuyerProfile[] newArray(int size) {
            return new BuyerProfile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(street);
        dest.writeString(building);
        dest.writeString(floor);
        dest.writeString(apartment);
        dest.writeString(additional);
        dest.writeString(comment);
        dest.writeString(promo_code);
        dest.writeInt(time_slot);
    }
}
