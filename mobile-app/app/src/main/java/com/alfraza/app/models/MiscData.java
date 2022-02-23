package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MiscData implements Parcelable {

    private String searchq, promo_code;
    private String type, msgtext, from, to;
    private List<FeatureItemSelect> features;
    private float rating;
    private int page, ftid, itemid, mid, sid, aid, userid, lastid, firstid, totalcount, spinner, catid, qty;
    public Long product_id;
    public Long item_id;

    public MiscData() {

    }


    protected MiscData(Parcel in) {
        searchq = in.readString();
        promo_code = in.readString();
        type = in.readString();
        msgtext = in.readString();
        from = in.readString();
        to = in.readString();
        features = in.createTypedArrayList(FeatureItemSelect.CREATOR);
        rating = in.readFloat();
        page = in.readInt();
        ftid = in.readInt();
        itemid = in.readInt();
        mid = in.readInt();
        sid = in.readInt();
        aid = in.readInt();
        userid = in.readInt();
        lastid = in.readInt();
        firstid = in.readInt();
        totalcount = in.readInt();
        spinner = in.readInt();
        catid = in.readInt();
        qty = in.readInt();
        if (in.readByte() == 0) {
            product_id = null;
        } else {
            product_id = in.readLong();
        }
    }

    public static final Creator<MiscData> CREATOR = new Creator<MiscData>() {
        @Override
        public MiscData createFromParcel(Parcel in) {
            return new MiscData(in);
        }

        @Override
        public MiscData[] newArray(int size) {
            return new MiscData[size];
        }
    };

    public void setPage(int page) {
        this.page = page;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public void setSearchq(String searchq) {
        this.searchq = searchq;
    }

    public void setPromoCode(String promoCode) {
        this.promo_code = promoCode;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFeature(int ftid) {
        this.ftid = ftid;
    }

    public void setFeatures(List<FeatureItemSelect> features) {
        this.features = features;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setItem_id(int item_id) {
        this.itemid = item_id;
    }

    public void setMainid(int mid) {
        this.mid = mid;
    }

    public void setSubid(int sid) {
        this.sid = sid;
    }

    public void setAdid(int aid) {
        this.aid = aid;
    }

    public void setMessageText(String msgtext) {
        this.msgtext = msgtext;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setFirstid(int firstid) {
        this.firstid = firstid;
    }

    public void setLastid(int lastid) {
        this.lastid = lastid;
    }

    public void setSpinner(int spinner) {
        this.spinner = spinner;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(searchq);
        dest.writeString(promo_code);
        dest.writeString(type);
        dest.writeString(msgtext);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeTypedList(features);
        dest.writeFloat(rating);
        dest.writeInt(page);
        dest.writeInt(ftid);
        dest.writeInt(itemid);
        dest.writeInt(mid);
        dest.writeInt(sid);
        dest.writeInt(aid);
        dest.writeInt(userid);
        dest.writeInt(lastid);
        dest.writeInt(firstid);
        dest.writeInt(totalcount);
        dest.writeInt(spinner);
        dest.writeInt(catid);
        dest.writeInt(qty);
        if (product_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(product_id);
        }
    }
}