package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MessagesData implements Parcelable {
    public static final Creator<MessagesData> CREATOR = new Creator<MessagesData>() {
        @Override
        public MessagesData createFromParcel(Parcel in) {
            return new MessagesData(in);
        }

        @Override
        public MessagesData[] newArray(int size) {
            return new MessagesData[size];
        }
    };
    private int id, count, icon, ctype;
    private int type;
    private String text;
    private String desc;
    private String state;
    private String title;
    private String action;
    private String time;
    private String image_url;

    private MessagesData(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        icon = in.readInt();
        ctype = in.readInt();
        type = in.readInt();
        text = in.readString();
        desc = in.readString();
        state = in.readString();
        title = in.readString();
        action = in.readString();
        time = in.readString();
        image_url = in.readString();
    }

    public int getType() {
        return type;
    }

    public int getCtype() {
        return ctype;
    }

    public int getIcon() {
        return icon;
    }

    public String getAction() {
        return action;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getState() {
        return state;
    }

    public String getTime() {
        return time;
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
        dest.writeInt(icon);
        dest.writeInt(ctype);
        dest.writeInt(type);
        dest.writeString(text);
        dest.writeString(desc);
        dest.writeString(state);
        dest.writeString(title);
        dest.writeString(action);
        dest.writeString(time);
        dest.writeString(image_url);
    }
}