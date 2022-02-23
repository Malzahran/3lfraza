package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UpFile implements Parcelable {

    private String file;
    private Integer type;

    public UpFile(String file, int type) {
        this.file = file;
        this.type = type;
    }


    private UpFile(Parcel in) {
        file = in.readString();
    }

    public static final Creator<UpFile> CREATOR = new Creator<UpFile>() {
        @Override
        public UpFile createFromParcel(Parcel in) {
            return new UpFile(in);
        }

        @Override
        public UpFile[] newArray(int size) {
            return new UpFile[size];
        }
    };

    public int getType() {
        return type;
    }

    public String getFile() {
        return file;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(file);
    }
}