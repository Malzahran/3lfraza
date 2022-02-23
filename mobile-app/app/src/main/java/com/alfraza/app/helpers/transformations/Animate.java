package com.alfraza.app.helpers.transformations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class Animate {
    public static void slideToBottom(final View view, Animation.AnimationListener listener) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(1000);
        if (listener != null) animate.setAnimationListener(listener);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // slide the view from below itself to the current position
    public static void slideToTop(View view, Animation.AnimationListener listener) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(1000);
        if (listener != null) animate.setAnimationListener(listener);
        view.startAnimation(animate);
    }
}
