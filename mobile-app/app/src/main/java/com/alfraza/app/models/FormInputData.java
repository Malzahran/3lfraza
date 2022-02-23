package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FormInputData implements Parcelable {
    public static final Creator<FormInputData> CREATOR = new Creator<FormInputData>() {
        @Override
        public FormInputData createFromParcel(Parcel in) {
            return new FormInputData(in);
        }

        @Override
        public FormInputData[] newArray(int size) {
            return new FormInputData[size];
        }
    };
    private String name, hint, title, text;
    private int id, type, ettype, place, required, min, height, disabled, icon, color;
    private String image_url;
    private String fetch_more;
    private float minimum, maximum;
    private SpinnerData[] spinner;
    private ExpandItemsData[] expanditems;
    private IntentData intentData;

    private FormInputData(Parcel in) {
        name = in.readString();
        hint = in.readString();
        title = in.readString();
        text = in.readString();
        fetch_more = in.readString();
        id = in.readInt();
        color = in.readInt();
        type = in.readInt();
        ettype = in.readInt();
        place = in.readInt();
        required = in.readInt();
        min = in.readInt();
        height = in.readInt();
        disabled = in.readInt();
        icon = in.readInt();
        image_url = in.readString();
        minimum = in.readFloat();
        maximum = in.readFloat();
        spinner = in.createTypedArray(SpinnerData.CREATOR);
        expanditems = in.createTypedArray(ExpandItemsData.CREATOR);
        intentData = in.readParcelable(IntentData.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public int getPlace() {
        return place;
    }

    public int getType() {
        return type;
    }

    public int getEtType() {
        return ettype;
    }

    public int getHeight() {
        return height;
    }

    public int getIcon() {
        return icon;
    }

    public int Disabled() {
        return disabled;
    }

    public int isRequired() {
        return required;
    }

    public int allowMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getHint() {
        return hint;
    }

    public String getText() {
        return text;
    }

    public String getMore() {
        return fetch_more;
    }

    public float MinimumPay() {
        return minimum;
    }

    public float MaximumPay() {
        return maximum;
    }

    public SpinnerData[] getSpinnerData() {
        return spinner;
    }

    public ExpandItemsData[] getExpanditems() {
        return expanditems;
    }

    public IntentData getIntentData() {
        return intentData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(hint);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(fetch_more);
        dest.writeInt(id);
        dest.writeInt(color);
        dest.writeInt(type);
        dest.writeInt(ettype);
        dest.writeInt(place);
        dest.writeInt(required);
        dest.writeInt(min);
        dest.writeInt(height);
        dest.writeInt(disabled);
        dest.writeInt(icon);
        dest.writeString(image_url);
        dest.writeFloat(minimum);
        dest.writeFloat(maximum);
        dest.writeTypedArray(spinner, flags);
        dest.writeTypedArray(expanditems, flags);
        dest.writeParcelable(intentData, flags);
    }
}