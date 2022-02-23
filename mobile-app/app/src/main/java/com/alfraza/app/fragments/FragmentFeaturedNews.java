package com.alfraza.app.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.alfraza.app.ActivityNewsInfo;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.MiscData;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfraza.app.ActivityStore;
import com.alfraza.app.ActivityNewsInfoDetails;
import com.alfraza.app.R;
import com.alfraza.app.adapter.AdapterFeaturedNews;
import com.alfraza.app.models.NewsInfo;
import com.alfraza.app.helpers.utilities.NetworkCheck;
import com.alfraza.app.helpers.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;

public class FragmentFeaturedNews extends Fragment {

    private Session session;
    private View root_view;
    private ViewPager viewPager;
    private Handler handler = new Handler();
    private Runnable runnableCode = null;
    private AdapterFeaturedNews adapter;
    private Call<ServerResponse> response;
    private TextView features_news_title;
    private View lyt_main_content;
    private LinearLayout layout_dots;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        session = new Session(getActivity());
        root_view = inflater.inflate(R.layout.fragment_featured_news, null);
        initComponent();
        requestFeaturedNews();
        return root_view;
    }

    private void initComponent() {
        lyt_main_content = root_view.findViewById(R.id.lyt_cart);
        features_news_title = root_view.findViewById(R.id.featured_news_title);
        layout_dots = root_view.findViewById(R.id.layout_dots);
        viewPager = root_view.findViewById(R.id.pager);
        ImageButton bt_previous = root_view.findViewById(R.id.bt_previous);
        ImageButton bt_next = root_view.findViewById(R.id.bt_next);
        adapter = new AdapterFeaturedNews(getActivity(), new ArrayList<NewsInfo>());
        lyt_main_content.setVisibility(View.GONE);

        bt_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevAction();
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextAction();
            }
        });
    }

    private void displayResultData(List<NewsInfo> items) {
        adapter.setItems(items);
        viewPager.setAdapter(adapter);

        LayoutParams params = viewPager.getLayoutParams();
        params.height = Tools.getFeaturedNewsImageHeight(getActivity());
        viewPager.setLayoutParams(params);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        features_news_title.setText(adapter.getItem(0).title);
        addBottomDots(layout_dots, adapter.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                NewsInfo cur = adapter.getItem(pos);
                features_news_title.setText(cur.title);
                addBottomDots(layout_dots, adapter.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        startAutoSlider(adapter.getCount());

        adapter.setOnItemClickListener(new AdapterFeaturedNews.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NewsInfo obj) {
                Snackbar.make(root_view, obj.title, Snackbar.LENGTH_SHORT).show();
                if (getActivity() != null && getActivity() instanceof ActivityStore) {
                    Intent i = new Intent(getActivity(), ActivityNewsInfoDetails.class);
                    i.putExtra(EXTRA_OBJECT_ID, obj.id);
                    i.putExtra(EXTRA_FROM_NOTIF, false);
                    startActivity(i);
                }
            }
        });

        lyt_main_content.setVisibility(View.VISIBLE);
        ActivityStore.getInstance().news_load = true;
        ActivityStore.getInstance().showDataLoaded();
    }

    private void requestFeaturedNews() {
        MiscData misc = new MiscData();
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NEWS_TYPE);
        request.setSubType(Constants.FEATURED_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    displayResultData(resp.news_infos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    session.Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    onFailRequest();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        onFailRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    Log.e("onFailure", t.getMessage());
                    if (!call.isCanceled()) onFailRequest();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void startAutoSlider(final int count) {
        runnableCode = new Runnable() {
            @Override
            public void run() {
                int pos = viewPager.getCurrentItem();
                pos = pos + 1;
                if (pos >= count) pos = 0;
                viewPager.setCurrentItem(pos);
                handler.postDelayed(runnableCode, 3000);
            }
        };
        handler.postDelayed(runnableCode, 3000);
    }

    private void prevAction() {
        int pos = viewPager.getCurrentItem();
        pos = pos - 1;
        if (pos < 0) pos = adapter.getCount();
        viewPager.setCurrentItem(pos);
    }

    private void nextAction() {
        int pos = viewPager.getCurrentItem();
        pos = pos + 1;
        if (pos >= adapter.getCount()) pos = 0;
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void onDestroy() {
        if (runnableCode != null) handler.removeCallbacks(runnableCode);
        if (response != null && response.isExecuted()) response.cancel();
        super.onDestroy();
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getActivity());
            int width_height = 10;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(getActivity(), R.color.darkOverlaySoft));
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLight));
        }
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(R.string.msg_failed_load_data);
        } else {
            showFailedView(R.string.no_internet_text);
        }
    }

    private void showFailedView(@StringRes int message) {
        ActivityStore.getInstance().showDialogFailed(message);
    }
}
