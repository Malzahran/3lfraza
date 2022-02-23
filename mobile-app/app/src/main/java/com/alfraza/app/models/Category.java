package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Category implements Parcelable {
    public String name;
    private int id, hassub;
    private String image_url;
    private String color;
    private String brief;
    private int show_title;

    private Category(Parcel in) {
        name = in.readString();
        id = in.readInt();
        hassub = in.readInt();
        image_url = in.readString();
        color = in.readString();
        brief = in.readString();
        show_title = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int hassub() {
        return hassub;
    }

    public String getImg() {
        return image_url;
    }

    public String getColor() {
        return color;
    }

    public String getBrief() {
        return brief;
    }

    public int showTitle() {
        return show_title;
    }

    //to display object as a string in spinner
    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
        dest.writeInt(hassub);
        dest.writeString(image_url);
        dest.writeString(color);
        dest.writeString(brief);
        dest.writeInt(show_title);
    }
}