package com.alfraza.app.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alfraza.app.R;
import com.alfraza.app.fragments.SlideshowDialogFragment;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.models.Images;
import com.alfraza.app.models.IntentData;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

import static com.alfraza.app.fragments.SlideshowDialogFragment.newInstance;

//	adapter
public class ImagesPagerAdapter extends PagerAdapter {

    private ArrayList<Images> images;
    private Activity act;
    private Session session;

    public ImagesPagerAdapter(Activity activity, ArrayList<Images> images) {
        this.act = activity;
        this.images = images;
        this.session = new Session(act);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (layoutInflater != null)
            view = layoutInflater.inflate(R.layout.image_pager_preview, container, false);
        if (view != null) {
            final ImageView imageViewPreview = view.findViewById(R.id.image_preview);
            final ProgressBar progress = view.findViewById(R.id.img_progress);
            final Images image = images.get(position);
            if (images.size() > 1 && image.getIntent() == null) {
                imageViewPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("images", images);
                        bundle.putInt("position", position);

                        FragmentTransaction ft = act.getFragmentManager().beginTransaction();
                        SlideshowDialogFragment newFragment = newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");
                    }
                });
            } else if (images.size() > 0 && image.getIntent() != null) {
                imageViewPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentData intentData = image.getIntent();
                        session.NewActivity(intentData, 0);
                    }
                });
            }

            session.imgLoader()
                    .load(image.getMedium())
                    .error(R.drawable.warning_img)
                    .fit()
                    .into(imageViewPreview, new Callback() {
                        @Override
                        public void onSuccess() {
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progress.setVisibility(View.GONE);
                        }
                    });
            container.addView(view);

            return view;
        }
        return false;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}