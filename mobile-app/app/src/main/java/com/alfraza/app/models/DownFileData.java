package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DownFileData implements Parcelable {

    private String fname, furl, fext;
    private Integer type;

    private DownFileData(Parcel in) {
        fname = in.readString();
        furl = in.readString();
        fext = in.readString();
    }

    public static final Creator<DownFileData> CREATOR = new Creator<DownFileData>() {
        @Override
        public DownFileData createFromParcel(Parcel in) {
            return new DownFileData(in);
        }

        @Override
        public DownFileData[] newArray(int size) {
            return new DownFileData[size];
        }
    };

    public int getType() {
        return type;
    }

    public String getFileName() {
        return fname;
    }

    public String getFileUrl() {
        return furl;
    }

    public String getFileExt() {
        return fext;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fname);
        dest.writeString(furl);
        dest.writeString(fext);
    }
}