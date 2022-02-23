package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationData implements Parcelable {
    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
    private int id, count;
    private String type;
    private String title;
    private String desc;
    private String action;
    private String image_url;

    private NotificationData(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        type = in.readString();
        title = in.readString();
        desc = in.readString();
        action = in.readString();
        image_url = in.readString();
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getImg() {
        return image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(action);
        dest.writeString(image_url);
    }
}