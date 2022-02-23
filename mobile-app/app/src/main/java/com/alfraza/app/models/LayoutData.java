package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LayoutData implements Parcelable {
    public static final Creator<LayoutData> CREATOR = new Creator<LayoutData>() {
        @Override
        public LayoutData createFromParcel(Parcel in) {
            return new LayoutData(in);
        }

        @Override
        public LayoutData[] newArray(int size) {
            return new LayoutData[size];
        }
    };
    private String abtitle, desc, custom_icon, sptitle, from, to;
    private int layout, layoutland, coloumn, coloumnland, cimg, allowbuttons,
            allowback, allowsearch, allowmore, searchtype, allowtotal,
            showhome, showicon, bartitle, barback,
            indvclk, indvid, click, actionbar, keepOn, orientation, zoom, refresh, refreshInterval;
    private IntentData intentData;
    private FormInputData[] totaldata;
    private FormInputData[] buttons;
    private SpinnerData[] spinner;

    private LayoutData(Parcel in) {
        abtitle = in.readString();
        desc = in.readString();
        custom_icon = in.readString();
        sptitle = in.readString();
        from = in.readString();
        to = in.readString();
        layout = in.readInt();
        layoutland = in.readInt();
        coloumn = in.readInt();
        coloumnland = in.readInt();
        cimg = in.readInt();
        allowbuttons = in.readInt();
        allowback = in.readInt();
        allowsearch = in.readInt();
        allowmore = in.readInt();
        searchtype = in.readInt();
        allowtotal = in.readInt();
        showhome = in.readInt();
        showicon = in.readInt();
        bartitle = in.readInt();
        barback = in.readInt();
        indvclk = in.readInt();
        indvid = in.readInt();
        click = in.readInt();
        actionbar = in.readInt();
        keepOn = in.readInt();
        orientation = in.readInt();
        zoom = in.readInt();
        refresh = in.readInt();
        refreshInterval = in.readInt();
        intentData = in.readParcelable(IntentData.class.getClassLoader());
        totaldata = in.createTypedArray(FormInputData.CREATOR);
        buttons = in.createTypedArray(FormInputData.CREATOR);
        spinner = in.createTypedArray(SpinnerData.CREATOR);
    }

    public String getBarTitle() {
        return abtitle;
    }

    public String getDesc() {
        return desc;
    }

    public String getSpinnertitle() {
        return sptitle;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getLayoutType() {
        return layout;
    }

    public int allowLandGrid() {
        return layoutland;
    }

    public int keepScreenOn() {
        return keepOn;
    }

    public int getOrientation() {
        return orientation;
    }

    public int disableActionBar() {
        return actionbar;
    }

    public int allowBarBack() {
        return barback;
    }

    public int allowLoadMore() {
        return allowmore;
    }

    public int allowBarTitle() {
        return bartitle;
    }

    public int allowTotalData() {
        return allowtotal;
    }

    public int allowSearch() {
        return allowsearch;
    }

    public int AllowButtons() {
        return allowbuttons;
    }

    public int getSearchtype() {
        return searchtype;
    }

    public int showHome() {
        return showhome;
    }

    public int showBaricon() {
        return showicon;
    }

    public int getGridColoumn() {
        return coloumn;
    }

    public int getGridColoumnL() {
        return coloumnland;
    }

    public int HandleIndvd() {
        return indvclk;
    }

    public int Indvid() {
        return indvid;
    }

    public int getClickType() {
        return click;
    }

    public SpinnerData[] getSpinnerData() {
        return spinner;
    }

    public IntentData getIntent() {
        return intentData;
    }

    public FormInputData[] getTotalData() {
        return totaldata;
    }

    public FormInputData[] getButtons() {
        return buttons;
    }

    public int isCircleimg() {
        return cimg;
    }

    public int isZoomAllowed() {
        return zoom;
    }

    public int isRefreshAllowed() {
        return refresh;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public String getCustomicon() {
        return custom_icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abtitle);
        dest.writeString(desc);
        dest.writeString(custom_icon);
        dest.writeString(sptitle);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeInt(layout);
        dest.writeInt(layoutland);
        dest.writeInt(coloumn);
        dest.writeInt(coloumnland);
        dest.writeInt(cimg);
        dest.writeInt(allowbuttons);
        dest.writeInt(allowback);
        dest.writeInt(allowsearch);
        dest.writeInt(allowmore);
        dest.writeInt(searchtype);
        dest.writeInt(allowtotal);
        dest.writeInt(showhome);
        dest.writeInt(showicon);
        dest.writeInt(bartitle);
        dest.writeInt(barback);
        dest.writeInt(indvclk);
        dest.writeInt(indvid);
        dest.writeInt(click);
        dest.writeInt(actionbar);
        dest.writeInt(keepOn);
        dest.writeInt(orientation);
        dest.writeInt(zoom);
        dest.writeInt(refreshInterval);
        dest.writeInt(refresh);
        dest.writeParcelable(intentData, flags);
        dest.writeTypedArray(totaldata, flags);
        dest.writeTypedArray(buttons, flags);
        dest.writeTypedArray(spinner, flags);
    }
}