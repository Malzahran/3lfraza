package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ExpandItemsData implements Parcelable {

    public static final Creator<ExpandItemsData> CREATOR = new Creator<ExpandItemsData>() {
        @Override
        public ExpandItemsData createFromParcel(Parcel in) {
            return new ExpandItemsData(in);
        }

        @Override
        public ExpandItemsData[] newArray(int size) {
            return new ExpandItemsData[size];
        }
    };
    private int id, expand, radio, radiocheck, gps;
    private String title;
    private String desc;
    private String image_url;
    private String action;
    private String url;
    private IntentData intent;

    private ExpandItemsData(Parcel in) {
        id = in.readInt();
        expand = in.readInt();
        radio = in.readInt();
        radiocheck = in.readInt();
        gps = in.readInt();
        title = in.readString();
        desc = in.readString();
        image_url = in.readString();
        action = in.readString();
        url = in.readString();
        intent = in.readParcelable(IntentData.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int AllowExpand() {
        return expand;
    }

    public int EnableRadio() {
        return radio;
    }

    public int DisableRadioCheck() {
        return radiocheck;
    }

    public int AllowGps() {
        return gps;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image_url;
    }

    public String getAction() {
        return action;
    }

    public String getUrl() {
        return url;
    }

    public IntentData getIntent() {
        return intent;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExpandItemsData) {
            ExpandItemsData c = (ExpandItemsData) obj;
            return c.getTitle().equals(title) && c.getId() == id;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(expand);
        dest.writeInt(radio);
        dest.writeInt(radiocheck);
        dest.writeInt(gps);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(image_url);
        dest.writeString(action);
        dest.writeString(url);
        dest.writeParcelable(intent, flags);
    }
}