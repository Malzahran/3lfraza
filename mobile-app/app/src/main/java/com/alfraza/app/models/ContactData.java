package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactData implements Parcelable {

    public static final Creator<ContactData> CREATOR = new Creator<ContactData>() {
        @Override
        public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

        @Override
        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };
    private String name;
    private String phone;
    private String email;
    private String message;

    public ContactData() {

    }

    private ContactData(Parcel in) {
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        message = in.readString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(message);
    }
}