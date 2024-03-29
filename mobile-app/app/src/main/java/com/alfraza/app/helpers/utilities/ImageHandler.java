package com.alfraza.app.helpers.utilities;

import android.annotation.SuppressLint;
import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

public class ImageHandler {
    @SuppressLint("StaticFieldLeak")
    private static Picasso instance;

    public static Picasso getSharedInstance(Context context) {
        if (instance == null)
            instance = new Picasso.Builder(context).executor(Executors.newSingleThreadExecutor()).indicatorsEnabled(false).build();
        return instance;
    }
}