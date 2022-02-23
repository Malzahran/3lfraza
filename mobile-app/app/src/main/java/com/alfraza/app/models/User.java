package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int uid;
    private int gps, maps;
    private int push, credit;
    private int chat;
    private int city;
    private String balance;
    private int reqphone;
    private String name;
    private String usersname;
    private String phone;
    private String username;
    private String email;
    private String password;
    private String fcmtoken;
    private String oldfcmtoken;
    private String old_password;
    private String new_password;
    private String langcode;
    private String usertype;
    private String role;
    private String code;
    private String profileimg;
    private double latitude;
    private double longitude;
    private int city_group;
    private String address;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private String additional;

    public User() {

    }

    protected User(Parcel in) {
        uid = in.readInt();
        gps = in.readInt();
        maps = in.readInt();
        push = in.readInt();
        credit = in.readInt();
        chat = in.readInt();
        city = in.readInt();
        balance = in.readString();
        reqphone = in.readInt();
        name = in.readString();
        usersname = in.readString();
        phone = in.readString();
        username = in.readString();
        email = in.readString();
        password = in.readString();
        fcmtoken = in.readString();
        oldfcmtoken = in.readString();
        old_password = in.readString();
        new_password = in.readString();
        langcode = in.readString();
        usertype = in.readString();
        role = in.readString();
        code = in.readString();
        profileimg = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        city_group = in.readInt();
        address = in.readString();
        street = in.readString();
        building = in.readString();
        floor = in.readString();
        apartment = in.readString();
        additional = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getUId() {
        return uid;
    }

    public void setUId(int uid) {
        this.uid = uid;
    }

    public int AllowTracking() {
        return gps;
    }

    public int MapsInterval() {
        return maps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public int RequiredPhone() {
        return reqphone;
    }

    public int AllowPush() {
        return push;
    }

    public int AllowChat() {
        return chat;
    }

    public String getBalance() {
        return balance;
    }

    public int AllowCredit() {
        return credit;
    }

    public String getSName() {
        return usersname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getStreet() {
        return street;
    }

    public String getBuilding() {
        return building;
    }

    public String getFloor() {
        return floor;
    }

    public String getApartment() {
        return apartment;
    }

    public String getAdditional() {
        return additional;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public void setOldFcmtoken(String oldfcmtoken) {
        this.oldfcmtoken = oldfcmtoken;
    }

    public void setLangcode(String langcode) {
        this.langcode = langcode;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public void setCityGroup(int cityGroup) {
        this.city_group = cityGroup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(gps);
        dest.writeInt(maps);
        dest.writeInt(push);
        dest.writeInt(credit);
        dest.writeInt(chat);
        dest.writeInt(city);
        dest.writeString(balance);
        dest.writeInt(reqphone);
        dest.writeString(name);
        dest.writeString(usersname);
        dest.writeString(phone);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(fcmtoken);
        dest.writeString(oldfcmtoken);
        dest.writeString(old_password);
        dest.writeString(new_password);
        dest.writeString(langcode);
        dest.writeString(usertype);
        dest.writeString(role);
        dest.writeString(code);
        dest.writeString(profileimg);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(city_group);
        dest.writeString(address);
        dest.writeString(street);
        dest.writeString(building);
        dest.writeString(floor);
        dest.writeString(apartment);
        dest.writeString(additional);
    }
}