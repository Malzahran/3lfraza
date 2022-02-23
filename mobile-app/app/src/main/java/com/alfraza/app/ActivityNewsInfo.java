package com.alfraza.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alfraza.app.adapter.AdapterNewsInfo;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.NewsInfo;
import com.alfraza.app.helpers.utilities.NetworkCheck;
import com.alfraza.app.helpers.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;

public class ActivityNewsInfo extends AppCompatActivity {

    private Session session;
    private RecyclerView recyclerView;
    private AdapterNewsInfo mAdapter;
    private SwipeRefreshLayout swipe_refresh;
    private Call<ServerResponse> response;

    private int post_total = 0;
    private int failed_page = 0;

    @Override
    public void onResume() {
        super.onResume();
    }

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_news_info);
        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.title_activity_news_info);
            Tools.systemBarLolipop(this);
        }
    }

    public void iniComponent() {
        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        mAdapter = new AdapterNewsInfo(this, recyclerView, new ArrayList<NewsInfo>());
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterNewsInfo.OnItemClickListener() {
            @Override
            public void onItemClick(View v, NewsInfo obj, int position) {
                Intent i = new Intent(ActivityNewsInfo.this, ActivityNewsInfoDetails.class);
                i.putExtra(EXTRA_OBJECT_ID, obj.id);
                i.putExtra(EXTRA_FROM_NOTIF, false);
                startActivity(i);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterNewsInfo.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (response != null && response.isExecuted()) response.cancel();
                mAdapter.resetListData();
                requestAction(1);
            }
        });

        requestAction(1);
    }

    private void displayApiResult(final List<NewsInfo> items) {
        mAdapter.insertData(items);
        swipeProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void requestListNewsInfo(final int page_no) {
        MiscData misc = new MiscData();
        misc.setPage(page_no);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NEWS_TYPE);
        request.setSubType(Constants.ALL_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    post_total = resp.total;
                    displayApiResult(resp.news_infos);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListNewsInfo(page_no);
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (response != null && response.isExecuted()) {
            response.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText("no_post");
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(false);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
