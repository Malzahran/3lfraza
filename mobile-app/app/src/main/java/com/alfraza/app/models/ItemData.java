package com.alfraza.app.models;


import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {


    private String title;
    private String title2;
    private String minfo;
    private String minfo2;
    private String state;
    private String desc;
    private String full_desc;
    private String from;
    private String to;
    private String count;
    private String featureTitle;
    private String contact_title;
    private String price;
    private String dscprice;
    private String action;
    private String reservetext;
    private String phone;
    private String sms;
    private String rating_text;
    private String image_url;
    private String item_url, item_url_name;
    private String share_url;
    private String user_name;
    private String convname;
    private String custom_icon;
    private int ftid;
    private int available;
    private int views;
    private int cart;
    private int closed;
    private int status;
    private int added_cart;
    private int click, action_id;
    private float rating;
    private float prc;
    private float dsc_prc;
    private String time;
    private Category[] cats;
    private Images[] album;
    private Video[] video;
    private DownFileData[] files;
    private FormInputData[] buttons;
    private int id;
    private int quantity;
    private int color;
    private int convid;
    private int type;
    private int pid;
    private int currency;
    private int have_album;
    private int have_video;
    private int canrate;
    private IntentData intentData, actionintent, resvintent;
    private LocationData[] location;
    private FeatureCats[] features;

    public ItemData() {

    }

    protected ItemData(Parcel in) {
        title = in.readString();
        title2 = in.readString();
        minfo = in.readString();
        minfo2 = in.readString();
        state = in.readString();
        desc = in.readString();
        full_desc = in.readString();
        from = in.readString();
        to = in.readString();
        count = in.readString();
        featureTitle = in.readString();
        contact_title = in.readString();
        price = in.readString();
        dscprice = in.readString();
        action = in.readString();
        reservetext = in.readString();
        phone = in.readString();
        sms = in.readString();
        rating_text = in.readString();
        image_url = in.readString();
        item_url = in.readString();
        item_url_name = in.readString();
        share_url = in.readString();
        user_name = in.readString();
        convname = in.readString();
        custom_icon = in.readString();
        ftid = in.readInt();
        available = in.readInt();
        views = in.readInt();
        cart = in.readInt();
        closed = in.readInt();
        status = in.readInt();
        added_cart = in.readInt();
        click = in.readInt();
        action_id = in.readInt();
        rating = in.readFloat();
        prc = in.readFloat();
        dsc_prc = in.readFloat();
        time = in.readString();
        cats = in.createTypedArray(Category.CREATOR);
        album = in.createTypedArray(Images.CREATOR);
        video = in.createTypedArray(Video.CREATOR);
        files = in.createTypedArray(DownFileData.CREATOR);
        buttons = in.createTypedArray(FormInputData.CREATOR);
        id = in.readInt();
        quantity = in.readInt();
        color = in.readInt();
        convid = in.readInt();
        type = in.readInt();
        pid = in.readInt();
        currency = in.readInt();
        have_album = in.readInt();
        have_video = in.readInt();
        canrate = in.readInt();
        intentData = in.readParcelable(IntentData.class.getClassLoader());
        actionintent = in.readParcelable(IntentData.class.getClassLoader());
        resvintent = in.readParcelable(IntentData.class.getClassLoader());
        location = in.createTypedArray(LocationData.CREATOR);
        features = in.createTypedArray(FeatureCats.CREATOR);
    }

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public int getConvid() {
        return convid;
    }

    public int getClickType() {
        return click;
    }

    public int getType() {
        return type;
    }

    public IntentData getIntent() {
        return intentData;
    }

    public IntentData getActionintent() {
        return actionintent;
    }

    public IntentData getResvintent() {
        return resvintent;
    }

    public String getConvname() {
        return convname;
    }

    public String getAction() {
        return action;
    }

    public String getCustomicon() {
        return custom_icon;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle2() {
        return title2;
    }

    public String getState() {
        return state;
    }

    public String getMinfo() {
        return minfo;
    }

    public String getMinfo2() {
        return minfo2;
    }

    public String getDesc() {
        return desc;
    }

    public String getFulldesc() {
        return full_desc;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCount() {
        return count;
    }

    public String getImg() {
        return image_url;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return item_url;
    }

    public String getUrlname() {
        return item_url_name;
    }

    public String getShareurl() {
        return share_url;
    }

    public String getPrice() {
        return price;
    }

    public float getPrc() {
        return prc;
    }

    public float getDscPrc() {
        return dsc_prc;
    }

    public String getDscPrice() {
        return dscprice;
    }

    public String getPhone() {
        return phone;
    }

    public String getSms() {
        return sms;
    }

    public String getContacttitle() {
        return contact_title;
    }

    public String getReservetext() {
        return reservetext;
    }

    public String getRatingText() {
        return rating_text;
    }

    public Images[] getAlbumImage() {
        return album;
    }

    public Video[] getVideos() {
        return video;
    }

    public DownFileData[] getFiles() {
        return files;
    }

    public float getRating() {
        return rating;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int haveAlbum() {
        return have_album;
    }

    public int haveVideo() {
        return have_video;
    }

    public LocationData[] getLocations() {
        return location;
    }

    public FeatureCats[] getFeatures() {
        return features;
    }

    public String getUsername() {
        return user_name;
    }

    public int getAvailable() {
        return available;
    }

    public int getFtid() {
        return ftid;
    }

    public int getCart() {
        return cart;
    }

    public int getStatus() {
        return status;
    }

    public int addedCart() {
        return added_cart;
    }

    public FormInputData[] getButtons() {
        return buttons;
    }

    public Category[] getCats() {
        return cats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(title2);
        dest.writeString(minfo);
        dest.writeString(minfo2);
        dest.writeString(state);
        dest.writeString(desc);
        dest.writeString(full_desc);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(count);
        dest.writeString(featureTitle);
        dest.writeString(contact_title);
        dest.writeString(price);
        dest.writeString(dscprice);
        dest.writeString(action);
        dest.writeString(reservetext);
        dest.writeString(phone);
        dest.writeString(sms);
        dest.writeString(rating_text);
        dest.writeString(image_url);
        dest.writeString(item_url);
        dest.writeString(item_url_name);
        dest.writeString(share_url);
        dest.writeString(user_name);
        dest.writeString(convname);
        dest.writeString(custom_icon);
        dest.writeInt(ftid);
        dest.writeInt(available);
        dest.writeInt(views);
        dest.writeInt(cart);
        dest.writeInt(closed);
        dest.writeInt(status);
        dest.writeInt(added_cart);
        dest.writeInt(click);
        dest.writeInt(action_id);
        dest.writeFloat(rating);
        dest.writeFloat(prc);
        dest.writeFloat(dsc_prc);
        dest.writeString(time);
        dest.writeTypedArray(cats, flags);
        dest.writeTypedArray(album, flags);
        dest.writeTypedArray(video, flags);
        dest.writeTypedArray(files, flags);
        dest.writeTypedArray(buttons, flags);
        dest.writeInt(id);
        dest.writeInt(quantity);
        dest.writeInt(color);
        dest.writeInt(convid);
        dest.writeInt(type);
        dest.writeInt(pid);
        dest.writeInt(currency);
        dest.writeInt(have_album);
        dest.writeInt(have_video);
        dest.writeInt(canrate);
        dest.writeParcelable(intentData, flags);
        dest.writeParcelable(actionintent, flags);
        dest.writeParcelable(resvintent, flags);
        dest.writeTypedArray(location, flags);
        dest.writeTypedArray(features, flags);
    }
}