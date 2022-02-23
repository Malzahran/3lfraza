package com.alfraza.app.helpers.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;

import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.widget.Toast;

import com.alfraza.app.R;
import com.squareup.picasso.Picasso;


public class ITUtilities {

    public ITUtilities() {
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @NonNull
    public static Spanned fromHtml(@NonNull String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            return Html.fromHtml(html);
        }
    }

    public static Picasso loadImg(Context ctx) {
        return ImageHandler.getSharedInstance(ctx);
    }

    public static int DpToPx(Context ctx, float dp) {
        // Convert to pixels
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public static void showToast(Activity act, String msg, int length) {
        int tLength = Toast.LENGTH_SHORT;
        switch (length) {
            case 1:
                tLength = Toast.LENGTH_LONG;
                break;
        }
        Toast.makeText(act, msg, tLength).show();
    }

    public static void shareIntent(Activity act) {
        ShareCompat.IntentBuilder.from(act)
                .setType("text/plain")
                .setChooserTitle(act.getString(R.string.menu_share))
                .setText("http://play.google.com/store/apps/details?id=" + act.getPackageName())
                .startChooser();
    }

    public static Spanned fromHtml(StringBuilder item_name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(String.valueOf(item_name), Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            return Html.fromHtml(String.valueOf(item_name));
        }
    }
}