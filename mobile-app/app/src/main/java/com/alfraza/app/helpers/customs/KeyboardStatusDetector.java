package com.alfraza.app.helpers.customs;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * Detects Keyboard Status changes and fires events only once for each change
 */
public class KeyboardStatusDetector {
    private KeyboardVisibilityListener visibilityListener;

    private boolean keyboardVisible = false;

    public void registerActivity(Activity a) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void registerView(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);

                int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    if (!keyboardVisible) {
                        keyboardVisible = true;
                        if (visibilityListener != null)
                            visibilityListener.onVisibilityChanged(true);
                    }
                } else {
                    if (keyboardVisible) {
                        keyboardVisible = false;
                        if (visibilityListener != null)
                            visibilityListener.onVisibilityChanged(false);
                    }
                }
            }
        });

    }

    public void setVisibilityListener(KeyboardVisibilityListener listener) {
        visibilityListener = listener;
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }
}