package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class IntentData implements Parcelable {

    public static final Creator<IntentData> CREATOR = new Creator<IntentData>() {
        @Override
        public IntentData createFromParcel(Parcel in) {
            return new IntentData(in);
        }

        @Override
        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };
    private int intent, reqid, reqmid, reqsid, reqaid;
    private String reqtype, reqstype, reqatype, reqptype;


    public IntentData(int intent,
                      int reqid,
                      int reqmid,
                      int reqsid,
                      int reqaid,
                      String reqtype,
                      String reqstype,
                      String reqatype,
                      String reqptype) {
        this.intent = intent;
        this.reqid = reqid;
        this.reqmid = reqmid;
        this.reqsid = reqsid;
        this.reqaid = reqaid;
        this.reqtype = reqtype;
        this.reqstype = reqstype;
        this.reqatype = reqatype;
        this.reqptype = reqptype;
    }

    protected IntentData(Parcel in) {
        intent = in.readInt();
        reqid = in.readInt();
        reqmid = in.readInt();
        reqsid = in.readInt();
        reqaid = in.readInt();
        reqtype = in.readString();
        reqstype = in.readString();
        reqatype = in.readString();
        reqptype = in.readString();
    }

    public int getIntent() {
        return intent;
    }

    public int getReqid() {
        return reqid;
    }

    public int getReqMid() {
        return reqmid;
    }

    public int getReqSid() {
        return reqsid;
    }

    public int getReqAid() {
        return reqaid;
    }

    public String getReqtype() {
        return reqtype;
    }

    public String getReqStype() {
        return reqstype;
    }

    public String getReqAtype() {
        return reqatype;
    }

    public String getReqPtype() {
        return reqptype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(intent);
        dest.writeInt(reqid);
        dest.writeInt(reqmid);
        dest.writeInt(reqsid);
        dest.writeInt(reqaid);
        dest.writeString(reqtype);
        dest.writeString(reqstype);
        dest.writeString(reqatype);
        dest.writeString(reqptype);
    }
}