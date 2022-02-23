package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class StoreData implements Parcelable {

    private List<ItemData> items;
    private List<ItemData> newitems;
    private List<ItemData> featitems;


    private StoreData(Parcel in) {
        items = in.createTypedArrayList(ItemData.CREATOR);
        newitems = in.createTypedArrayList(ItemData.CREATOR);
        featitems = in.createTypedArrayList(ItemData.CREATOR);
    }

    public static final Creator<StoreData> CREATOR = new Creator<StoreData>() {
        @Override
        public StoreData createFromParcel(Parcel in) {
            return new StoreData(in);
        }

        @Override
        public StoreData[] newArray(int size) {
            return new StoreData[size];
        }
    };

    public List<ItemData> getItems() {
        return items;
    }

    public List<ItemData> getNewitems() {
        return newitems;
    }

    public List<ItemData> getFeatitems() {
        return featitems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(items);
        dest.writeTypedList(newitems);
        dest.writeTypedList(featitems);
    }
}