package com.alfraza.app.activities.misc;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.adapter.MessageAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.fragments.MessagesFragment;
import com.alfraza.app.helpers.listener.EndlessRecyclerViewScrollListener;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.MessagesData;
import com.alfraza.app.models.MiscData;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityMessages extends AppCompatActivity {
    private Session session;
    private Call<ServerResponse> response;
    private ArrayList<MessagesData> data;
    private LayoutData layout;
    private LinearLayout loading_overlay, warning_overlay, msglayout, fragmentlayout;
    private TextView actv_title;
    private Fragment frg;
    private String searchq = null;
    private int getpage = 1, total = 0;
    private MessageAdapter adapter;
    private RecyclerView recycler;
    private LinearLayoutManager linerlayout;
    private MenuItem homeitem, acprogress;
    private MenuItem searchItem;
    private ActionBar abar;
    private int pushInterval = 5;
    private boolean firststart = true, fragment = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            pushInterval = session.pref().Pushinterval();
            if (!fragment && !firststart && data.size() > 0) CheckNewMessages();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, pushInterval * 1000);
        if (!fragment) {
            Fragment fragmen = getFragmentManager().findFragmentByTag("messages");
            if (fragmen != null)
                getFragmentManager().beginTransaction().remove(fragmen).commitAllowingStateLoss();
        }
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
        setContentView(R.layout.activity_messages);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        abar = getSupportActionBar();
        if (abar != null) {
            abar.setTitle(getString(R.string.title_activity_messages));
            abar.setDisplayHomeAsUpEnabled(true);
        }
        data = new ArrayList<>();
        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            initViews();
        } else {
            if (extras.getInt("convid") != 0) {
                int convid = extras.getInt("convid");
                int contype = extras.getInt("contype", 0);
                String convtit = extras.getString("contit");
                initViews();
                AllowFragment(1);
                Bundle bundle = new Bundle();
                bundle.putInt("convid", convid);
                bundle.putInt("contype", contype);
                bundle.putString("contit", convtit);
                Fragment fragment = new MessagesFragment();
                //set Fragmentclass Arguments
                fragment.setArguments(bundle);
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                ft.add(R.id.fragment_frame, fragment, "messages");
                ft.commitAllowingStateLoss();
            } else initViews();
        }
    }

    private void initViews() {
        warning_overlay = findViewById(R.id.warning_overlay);
        actv_title = findViewById(R.id.actv_title);
        msglayout = findViewById(R.id.msglayout);
        fragmentlayout = findViewById(R.id.fragmentlayout);
        recycler = findViewById(R.id.msg_recycler);
        linerlayout = new LinearLayoutManager(this);
        adapter = new MessageAdapter(ActivityMessages.this, data, 1);
        recycler.setLayoutManager(linerlayout);
        recycler.setAdapter(adapter);
    }

    public void setFragment(Fragment frg) {
        if (frg instanceof MessagesFragment) {
            this.frg = frg;
            fragment = true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fragment) {
            if (warning_overlay.getVisibility() == View.VISIBLE)
                warning_overlay.setVisibility(View.GONE);
            if (loading_overlay.getVisibility() == View.VISIBLE)
                loading_overlay.setVisibility(View.GONE);
            if (frg != null) ((MessagesFragment) frg).hideSnackBar();
            AllowFragment(2);
        } else finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inside, menu);
        homeitem = menu.findItem(R.id.menu_home);
        searchItem = menu.findItem(R.id.menu_search);
        acprogress = menu.findItem(R.id.action_progress);
        acprogress.setVisible(false);
        homeitem.setVisible(false);
        searchItem.setVisible(false);
        if (!fragment && data.size() == 0) loaddata();

        try {
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    if (!fragment) {
                        data.clear();
                        searchq = null;
                        loaddata();
                    }
                    return true;
                }
            });
            final SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (!fragment) {
                        if (layout != null) {
                            if (layout.getSearchtype() == 1) adapter.getFilter().filter(s);
                            else if (layout.getSearchtype() == 2) {
                                searchq = s;
                                data.clear();
                                getpage = 1;
                                loaddata();
                            }
                        } else {
                            searchq = s;
                            data.clear();
                            getpage = 1;
                            firststart = true;
                            loaddata();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!fragment && layout != null && layout.getSearchtype() == 1)
                        adapter.getFilter().filter(s);
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

    public void AllowFragment(int state) {
        if (state == 1) {
            Fragment fragmen = getFragmentManager().findFragmentByTag("messages");
            if (fragmen != null)
                getFragmentManager().beginTransaction().remove(fragmen).commitAllowingStateLoss();
            msglayout.setVisibility(View.GONE);
            loading_overlay.setVisibility(View.VISIBLE);
            fragmentlayout.setVisibility(View.VISIBLE);
        } else {
            fragment = false;
            Fragment fragmen = getFragmentManager().findFragmentByTag("messages");
            if (fragmen != null)
                getFragmentManager().beginTransaction().remove(fragmen).commitAllowingStateLoss();
            fragmentlayout.setVisibility(View.GONE);
            msglayout.setVisibility(View.VISIBLE);
            if (data.size() == 0) loaddata();
        }
    }

    private void loaddata() {
        try {
            if (firststart && loading_overlay.getVisibility() == View.GONE)
                loading_overlay.setVisibility(View.VISIBLE);
            if (warning_overlay.getVisibility() == View.VISIBLE)
                warning_overlay.setVisibility(View.GONE);
            acprogress.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setSearchq(searchq);
        misc.setPage(getpage);
        misc.setTotalcount(total);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
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
                                    acprogress.setVisible(false);
                                    layout = resp.getLayoutInfo();
                                    if (firststart) {
                                        loading_overlay.setVisibility(View.GONE);
                                        data.clear();
                                    }
                                    if (layout != null && layout.allowLoadMore() == 0) data.clear();
                                    data.addAll((Arrays.asList(resp.getMessages())));
                                    if (firststart && layout != null) {
                                        recycler.setLayoutManager(linerlayout);
                                        adapter = new MessageAdapter(ActivityMessages.this, data, 1);
                                        recycler.setAdapter(adapter);
                                        recycler.setAdapter(adapter);
                                        PrepareLayout();
                                    }

                                    if (firststart && layout != null && layout.allowLoadMore() != 0) {
                                        addOnScrollLisnter();
                                        firststart = false;
                                    } else if (firststart && layout != null && layout.allowLoadMore() == 0)
                                        firststart = false;
                                    else if (!firststart && layout != null && layout.allowLoadMore() != 0)
                                        adapter.notifyDataSetChanged();
                                    total = resp.getTotalcount();
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
                                    if (resp.getMessage() != null) error = resp.getMessage();
                                    acprogress.setVisible(false);
                                    if (firststart) {
                                        warning_overlay.setVisibility(View.VISIBLE);
                                        loading_overlay.setVisibility(View.GONE);
                                        homeitem.setVisible(true);
                                    }
                                    showSnackBar(error, 1, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        acprogress.setVisible(false);
                        homeitem.setVisible(true);
                        if (firststart) {
                            warning_overlay.setVisibility(View.VISIBLE);
                            loading_overlay.setVisibility(View.GONE);
                            showSnackBar(getString(R.string.error_failed_later), 0, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    acprogress.setVisible(false);
                    if (firststart) {
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        homeitem.setVisible(true);
                    }
                    showSnackBar(getString(R.string.internetcheck), 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addOnScrollLisnter() {
        // Retain an instance so that you can call `resetState()` for fresh searches
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linerlayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (!firststart && data.size() >= 20) {
                    getpage++;
                    loaddata();
                }
            }
        };
        recycler.addOnScrollListener(scrollListener);
    }

    private void PrepareLayout() {
        if (layout.disableActionBar() != 0) abar.hide();
        if (layout.getOrientation() != 0)
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

        if (layout.allowBarBack() != 0) abar.setDisplayHomeAsUpEnabled(true);
        else abar.setDisplayHomeAsUpEnabled(false);
        if (layout.showHome() != 0) homeitem.setVisible(true);
        else homeitem.setVisible(false);
        if (layout.allowSearch() != 0) searchItem.setVisible(true);
        else searchItem.setVisible(false);
        if (layout.showBaricon() != 0) {
            abar.setHomeButtonEnabled(true);
            abar.setDisplayShowHomeEnabled(true);
            abar.setIcon(R.mipmap.ic_launcher);
        } else {
            abar.setHomeButtonEnabled(false);
            abar.setDisplayShowHomeEnabled(false);
        }
        if (layout.allowBarTitle() != 0) abar.setDisplayShowTitleEnabled(true);
        else abar.setDisplayShowTitleEnabled(false);
        if (layout.getBarTitle() != null) abar.setTitle(layout.getBarTitle());

        if (layout.getDesc() != null) {
            actv_title.setText(layout.getDesc());
            actv_title.setVisibility(View.VISIBLE);
        }
    }

    private void CheckNewMessages() {
        MiscData misc = new MiscData();
        misc.setSearchq(searchq);
        misc.setPage(getpage);
        misc.setTotalcount(total);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
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
                        acprogress.setVisible(false);
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                if (resp.getMessages().length > 0) {
                                    data.clear();
                                    data.addAll(0, (Arrays.asList(resp.getMessages())));
                                    adapter.notifyDataSetChanged();
                                    total = resp.getTotalcount();
                                    session.showToast(getString(R.string.new_conversation_alert), 0);
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

    public void showSnackBar(String msg, final int action, int length) {
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