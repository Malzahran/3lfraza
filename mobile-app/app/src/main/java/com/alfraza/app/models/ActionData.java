package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ActionData implements Parcelable {
    public static final Creator<ActionData> CREATOR = new Creator<ActionData>() {
        @Override
        public ActionData createFromParcel(Parcel in) {
            return new ActionData(in);
        }

        @Override
        public ActionData[] newArray(int size) {
            return new ActionData[size];
        }
    };
    private int type;
    private int disable;
    private String text;
    private IntentData intentData;

    private ActionData(Parcel in) {
        type = in.readInt();
        disable = in.readInt();
        text = in.readString();
        intentData = in.readParcelable(IntentData.class.getClassLoader());
    }

    public int getType() {
        return type;
    }

    public int getDisable() {
        return disable;
    }

    public String getText() {
        return text;
    }

    public IntentData getIntent() {
        return intentData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(disable);
        dest.writeString(text);
        dest.writeParcelable(intentData, flags);
    }
}