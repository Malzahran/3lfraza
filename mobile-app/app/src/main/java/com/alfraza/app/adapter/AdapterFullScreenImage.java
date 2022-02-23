package com.alfraza.app.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.TouchImageView;

import java.util.List;

public class AdapterFullScreenImage extends PagerAdapter {

    private Activity act;
    private List<String> imagePaths;

    // constructor
    public AdapterFullScreenImage(Activity activity, List<String> imagePaths) {
        this.act = activity;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TouchImageView imgDisplay;
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_fullscreen_image, container, false);

        imgDisplay = viewLayout.findViewById(R.id.imgDisplay);
        ITUtilities.loadImg(act)
                .load(imagePaths.get(position))
                .error(R.drawable.ic_error)
                .into(imgDisplay);
        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);

    }

}
