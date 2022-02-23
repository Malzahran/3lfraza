package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

public class Product implements Parcelable {

    public Long id;
    public int a_cart;
    public int a_notify;
    public String name;
    public String image;
    public String full_image;
    public String currency;
    public Double price;
    public Double price_discount;
    public Long stock;
    public String description;
    public int status;
    public List<Category> categories;
    public List<ProductImage> product_images;
    public List<Features> features;

    private Product(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        a_cart = in.readInt();
        a_notify = in.readInt();
        name = in.readString();
        image = in.readString();
        currency = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            price_discount = null;
        } else {
            price_discount = in.readDouble();
        }
        if (in.readByte() == 0) {
            stock = null;
        } else {
            stock = in.readLong();
        }
        description = in.readString();
        status = in.readInt();
        categories = in.createTypedArrayList(Category.CREATOR);
        product_images = in.createTypedArrayList(ProductImage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(a_cart);
        dest.writeInt(a_notify);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(currency);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        if (price_discount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price_discount);
        }
        if (stock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(stock);
        }
        dest.writeString(description);
        dest.writeInt(status);
        dest.writeTypedList(categories);
        dest.writeTypedList(product_images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
