package com.alfraza.app.activities.misc;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.adapter.RecyclerAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.listener.EndlessRecyclerViewScrollListener;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.MiscData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityNotifications extends AppCompatActivity {
    private Session session;
    private Call<ServerResponse> response;
    private ArrayList<ItemData> data;
    private LayoutData layout;
    private LinearLayout loading_overlay, warning_overlay;
    private RelativeLayout headlayout;
    private TextView actv_title;
    private String searchq = null;
    private int getpage = 1, notfcount = 0;
    private RecyclerAdapter adapter;
    private RecyclerView recycler;
    private LinearLayoutManager linerlayout;
    private MenuItem homeitem, acprogress;
    private MenuItem searchItem;
    private ActionBar abar;
    private int pushInterval = 5;
    private boolean firststart = true;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            int newnotf = session.pref().GetNotfCount();
            pushInterval = session.pref().Pushinterval();
            if (newnotf != notfcount) {
                updateNtf();
                notfcount = session.pref().GetNotfCount();
            }
            handler.postDelayed(this, pushInterval * 1000); // for interval...
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public void onResume() {
        super.onResume();
        notfcount = session.pref().GetNotfCount();
        handler.postDelayed(runnable, pushInterval * 1000);
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
        setContentView(R.layout.activity_notifications);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        abar = getSupportActionBar();
        if (abar != null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle(getString(R.string.title_activity_notifications));
        }
        initViews();
    }

    private void initViews() {
        warning_overlay = findViewById(R.id.warning_overlay);
        headlayout = findViewById(R.id.headlayout);
        actv_title = findViewById(R.id.actv_title);
        recycler = findViewById(R.id.card_recycler_view);
        data = new ArrayList<>();
        linerlayout = new LinearLayoutManager(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inside, menu);

        homeitem = menu.findItem(R.id.menu_home);
        searchItem = menu.findItem(R.id.menu_search);
        acprogress = menu.findItem(R.id.action_progress);

        searchItem.setVisible(false);
        homeitem.setVisible(false);
        acprogress.setVisible(false);
        loaddata();
        try {
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    if (layout != null) {
                        if (layout.getSearchtype() == 2) {
                            data.clear();
                            searchq = null;
                            getpage = 1;
                            loaddata();
                        }
                    }
                    return true;
                }
            });

            final SearchView searchView = (SearchView) searchItem.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (layout != null) {
                        if (layout.getSearchtype() == 1) {
                            adapter.getFilter().filter(s);
                        } else if (layout.getSearchtype() == 2) {
                            searchq = s;
                            data.clear();
                            getpage = 1;
                            loaddata();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (layout != null) {
                        if (layout.getSearchtype() == 1) {
                            adapter.getFilter().filter(s);
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            session.goHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loaddata() {
        try {
            acprogress.setVisible(true);
            if (firststart && loading_overlay.getVisibility() == View.GONE) {
                loading_overlay.setVisibility(View.VISIBLE);
            }
            if (warning_overlay.getVisibility() == View.VISIBLE) {
                warning_overlay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setSearchq(searchq);
        misc.setPage(getpage);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NOTIFICATIONS);
        request.setSubType("getall");
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
                                    session.pref().SetNotfcount(0);
                                    acprogress.setVisible(false);
                                    layout = resp.getLayoutInfo();
                                    if (firststart) {
                                        loading_overlay.setVisibility(View.GONE);
                                        data.clear();
                                    }
                                    if (layout != null && layout.allowLoadMore() == 0) {
                                        data.clear();
                                    }
                                    data.addAll(resp.getItemsListing());
                                    if (firststart && layout != null) {
                                        recycler.setLayoutManager(linerlayout);
                                        adapter = new RecyclerAdapter(ActivityNotifications.this, data, layout);
                                        recycler.setAdapter(adapter);
                                        PrepareLayout();
                                    }
                                    if (firststart && layout != null && layout.allowLoadMore() != 0) {
                                        addOnScrollListner();
                                        firststart = false;
                                    } else if (firststart && layout != null && layout.allowLoadMore() == 0) {
                                        firststart = false;
                                    } else if (!firststart && layout != null && layout.allowLoadMore() != 0) {
                                        adapter.notifyDataSetChanged();
                                    }
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
                                    String error = getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    acprogress.setVisible(false);
                                    if (firststart) {
                                        warning_overlay.setVisibility(View.VISIBLE);
                                        loading_overlay.setVisibility(View.GONE);
                                        homeitem.setVisible(true);
                                    }
                                    showSnackbar(error, 1, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    } else {
                        try {
                            acprogress.setVisible(false);
                            homeitem.setVisible(true);
                            if (firststart) {
                                warning_overlay.setVisibility(View.VISIBLE);
                                loading_overlay.setVisibility(View.GONE);
                                String error = getString(R.string.error_failed_later);
                                showSnackbar(error, 0, 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    String error = getString(R.string.internetcheck);
                    acprogress.setVisible(false);
                    if (firststart) {
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        homeitem.setVisible(true);
                    }
                    showSnackbar(error, 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateNtf() {
        MiscData misc = new MiscData();
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NOTIFICATIONS);
        request.setSubType("updatenotf");
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                if (resp.getItemsListing().size() > 0) {
                                    data.addAll(0, resp.getItemsListing());
                                    adapter.notifyDataSetChanged();
                                    session.pref().SetNotfcount(0);
                                    session.showToast(getString(R.string.new_notification_alert), 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (resp.getResult().equals(Constants.LOGOUT)) {
                            try {
                                session.Logout();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void addOnScrollListner() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linerlayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                getpage++;
                loaddata();
            }
        };
        recycler.addOnScrollListener(scrollListener);
    }

    private void PrepareLayout() {
        if (layout.disableActionBar() != 0) abar.hide();
        if (layout.getOrientation() != 0) {
            switch (layout.getOrientation()) {
                case 1:
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case 2:
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case 3:
                    session.orientationManager(true);
                    break;
            }
        }

        if (layout.allowBarBack() != 0) {
            abar.setDisplayHomeAsUpEnabled(true);
        } else {
            abar.setDisplayHomeAsUpEnabled(false);
        }
        if (layout.showHome() != 0) {
            homeitem.setVisible(true);
        } else {
            homeitem.setVisible(false);
        }

        if (layout.allowSearch() != 0) {
            searchItem.setVisible(true);
        } else {
            searchItem.setVisible(false);
        }

        if (layout.showBaricon() != 0) {
            abar.setHomeButtonEnabled(true);
            abar.setDisplayShowHomeEnabled(true);
            abar.setIcon(R.mipmap.ic_launcher);
        } else {
            abar.setHomeButtonEnabled(false);
            abar.setDisplayShowHomeEnabled(false);
        }

        if (layout.allowBarTitle() != 0) {
            abar.setDisplayShowTitleEnabled(true);
        } else {
            abar.setDisplayShowTitleEnabled(false);
        }

        if (layout.getBarTitle() != null) {
            abar.setTitle(layout.getBarTitle());
        }

        if (layout.getDesc() != null) {
            headlayout.setVisibility(View.VISIBLE);
            actv_title.setText(layout.getDesc());
        }
    }

    public void showSnackbar(String msg, final int action, int length) {
        int SnLength = Snackbar.LENGTH_SHORT;
        switch (length) {
            case 1:
                SnLength = Snackbar.LENGTH_LONG;
                break;
            case 2:
                SnLength = Snackbar.LENGTH_INDEFINITE;
                break;
        }
        CoordinatorLayout view = findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(view, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loaddata();
                }
            });

            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}
