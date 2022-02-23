package com.alfraza.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.adapter.RecyclerAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.customs.DateDisplayPicker;
import com.alfraza.app.helpers.listener.EndlessRecyclerViewScrollListener;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.FormInputData;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.SpinnerData;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityRecycler extends AppCompatActivity {
    private Session session;
    private Call<ServerResponse> response;
    private ArrayList<ItemData> data;
    private LayoutData layout;
    private DateDisplayPicker from_date, to_date;
    private LinearLayout loading_overlay, warning_overlay, total_values, spinner_layout,
            top_right, top_left, bottom_right, bottom_left, date_layout;
    private TextView actv_title, spinner_title;
    private String reqtype, reqstype, reqatype, searchq = null, from, to;
    private int reqid, reqmid, reqsid, reqaid, spid = 0, getpage = 1;
    private RecyclerAdapter adapter;
    private RecyclerView recycler;
    private LinearLayout headlayout;
    private Spinner spinner;
    private LinearLayoutManager linerLayout;
    private GridLayoutManager gridlayout;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (!isRefreshing) {
                data.clear();
                adapter.notifyDataSetChanged();
                getpage = 1;
                if (layout != null && layout.allowLoadMore() == 1) scrollListener.resetState();
                loadData();
                handler.postDelayed(this, refreshInterval * 1000); // for interval...
            }
        }

    };
    private EndlessRecyclerViewScrollListener scrollListener;
    private MenuItem homeItem, acProgress;
    private MenuItem searchItem;
    private ActionBar aBar;
    private int refreshInterval = 10;
    private boolean firstStart = true, reload = false, autoRefresh = false, isRefreshing = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
        if (autoRefresh) handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reload) reload();
        if (autoRefresh) handler.postDelayed(runnable, refreshInterval * 1000);
    }

    private void reload() {
        data.clear();
        adapter.notifyDataSetChanged();
        getpage = 1;
        if (layout != null && layout.allowLoadMore() == 1) scrollListener.resetState();
        loadData();
    }

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setTitle(getString(R.string.app_name));
        }
        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) initViews();
        else {
            if (extras.getParcelable("intentData") != null) {
                IntentData intentData = extras.getParcelable("intentData");
                if (intentData != null) {
                    reqid = intentData.getReqid();
                    reqmid = intentData.getReqMid();
                    reqsid = intentData.getReqSid();
                    reqaid = intentData.getReqAid();
                    reqtype = intentData.getReqtype();
                    reqstype = intentData.getReqStype();
                    reqatype = intentData.getReqAtype();
                }
            }
            if (extras.getString("searchq") != null) searchq = extras.getString("searchq");
            initViews();
        }
    }

    private void initViews() {
        warning_overlay = findViewById(R.id.warning_overlay);
        date_layout = findViewById(R.id.date_layout);
        from_date = findViewById(R.id.date_from);
        to_date = findViewById(R.id.date_to);
        actv_title = findViewById(R.id.actv_title);
        headlayout = findViewById(R.id.headlayout);
        spinner_layout = findViewById(R.id.spinner_layout);
        spinner_title = findViewById(R.id.spinner_title);
        spinner = findViewById(R.id.spinner);
        top_right = findViewById(R.id.top_right);
        top_left = findViewById(R.id.top_left);
        bottom_right = findViewById(R.id.bottom_right);
        bottom_left = findViewById(R.id.bottom_left);
        total_values = findViewById(R.id.total_values);
        linerLayout = new LinearLayoutManager(this);
        data = new ArrayList<>();
        recycler = findViewById(R.id.card_recycler_view);
    }

    public void setDates() {
        from = from_date.getText().toString();
        to = to_date.getText().toString();
        if (!from.isEmpty() && !to.isEmpty()) {
            data.clear();
            adapter.notifyDataSetChanged();
            loadData();
        }
    }

    private void prepareSpinner(SpinnerData[] spinnerdata) {
        ArrayList<SpinnerData> spinnerList = new ArrayList<>();
        ArrayList<SpinnerData> sdata = new ArrayList<>(Arrays.asList(spinnerdata));
        String sptitle = getResources().getString(R.string.sort_by);
        if (layout.getSpinnertitle() != null) sptitle = layout.getSpinnertitle();
        if (headlayout.getVisibility() == View.GONE) headlayout.setVisibility(View.VISIBLE);
        spinner_layout.setVisibility(View.VISIBLE);
        spinner_title.setText(sptitle);
        if (sdata.size() > 0)
            for (int i = 0; i < sdata.size(); i++) {
                //Add Spinner Data
                spinnerList.add(new SpinnerData(sdata.get(i).getId(), sdata.get(i).getName()));
            }
        ArrayAdapter<SpinnerData> sadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner.setAdapter(sadapter);
        final Activity activity = this;
        final String finalSptitle = sptitle;
        spinner.setOnItemSelectedListener(null);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstStart) {
                    if (data.size() > 0) {
                        data.clear();
                        adapter.notifyDataSetChanged();
                    }
                    ((TextView) view).setTextColor(ContextCompat.getColor(ActivityRecycler.this, R.color.colorPrimary));
                    SpinnerData spin = (SpinnerData) parent.getSelectedItem();
                    spid = spin.getId();
                    data.clear();
                    adapter.notifyDataSetChanged();
                    getpage = 1;
                    if (layout != null && layout.allowLoadMore() == 1) scrollListener.resetState();
                    String selected = spin.getName();
                    Toast.makeText(activity, finalSptitle + selected, Toast.LENGTH_SHORT).show();
                    loadData();
                } else firstStart = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
        homeItem = menu.findItem(R.id.menu_home);
        searchItem = menu.findItem(R.id.menu_search);
        acProgress = menu.findItem(R.id.action_progress);
        searchItem.setVisible(false);
        homeItem.setVisible(false);
        acProgress.setVisible(false);
        loadData();
        try {
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    if (layout != null && layout.getSearchtype() == 2) {
                        data.clear();
                        adapter.notifyDataSetChanged();
                        searchq = null;
                        getpage = 1;
                        if (layout != null && layout.allowLoadMore() == 1)
                            scrollListener.resetState();
                        loadData();
                    }
                    return true;
                }
            });

            final SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (layout != null) {
                        if (layout.getSearchtype() == 1) adapter.getFilter().filter(s);
                        else if (layout.getSearchtype() == 2) {
                            searchq = s;
                            data.clear();
                            adapter.notifyDataSetChanged();
                            getpage = 1;
                            if (layout != null && layout.allowLoadMore() == 1)
                                scrollListener.resetState();
                            loadData();
                        }
                    } else {
                        searchq = s;
                        data.clear();
                        getpage = 1;
                        firstStart = true;
                        loadData();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (layout != null && layout.getSearchtype() == 1)
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

    private void loadData() {
        try {
            if (firstStart && loading_overlay.getVisibility() == View.GONE)
                loading_overlay.setVisibility(View.VISIBLE);
            if (warning_overlay.getVisibility() == View.VISIBLE)
                warning_overlay.setVisibility(View.GONE);
            isRefreshing = true;
            acProgress.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setType(reqatype);
        misc.setItem_id(reqid);
        misc.setMainid(reqmid);
        misc.setSubid(reqsid);
        misc.setAdid(reqaid);
        misc.setSpinner(spid);
        misc.setSearchq(searchq);
        misc.setPage(getpage);
        misc.setFrom(from);
        misc.setTo(to);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(reqtype);
        request.setSubType(reqstype);
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
                                    acProgress.setVisible(false);
                                    layout = resp.getLayoutInfo();
                                    if (firstStart) loading_overlay.setVisibility(View.GONE);
                                    if (resp.getItemsListing() != null && resp.getItemsListing().size() > 0) {
                                        if (firstStart || layout.allowLoadMore() == 0 || autoRefresh)
                                            data.clear();
                                        data.addAll(resp.getItemsListing());
                                    }
                                    if (firstStart && layout != null) {
                                        int orientation = ActivityRecycler.this.getResources().getConfiguration().orientation;
                                        if (layout.getLayoutType() == 1) {
                                            int columns = layout.getGridColoumn();
                                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                                if (layout.getGridColoumnL() != 0)
                                                    columns = layout.getGridColoumnL();
                                                else columns = columns * 2;
                                            }
                                            gridlayout = new GridLayoutManager(ActivityRecycler.this, columns);
                                            recycler.setLayoutManager(gridlayout);
                                        } else if (layout.getLayoutType() == 2) {
                                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                                if (layout.allowLandGrid() != 0) {
                                                    gridlayout = new GridLayoutManager(ActivityRecycler.this, 2);
                                                    recycler.setLayoutManager(gridlayout);
                                                } else recycler.setLayoutManager(linerLayout);
                                            } else recycler.setLayoutManager(linerLayout);
                                        }

                                        adapter = new RecyclerAdapter(ActivityRecycler.this, data, layout);
                                        recycler.setAdapter(adapter);
                                        PrepareLayout();
                                    }
                                    if (layout != null && layout.allowLoadMore() != 0 && firstStart)
                                        addOnScrollLisnter();
                                    else if (layout != null && layout.allowLoadMore() != 0 && !firstStart)
                                        adapter.notifyDataSetChanged();
                                    else adapter.notifyDataSetChanged();
                                    isRefreshing = false;
                                    if (firstStart && layout.getSpinnerData() != null && layout.getSpinnerData().length > 0)
                                        prepareSpinner(layout.getSpinnerData());
                                    if (firstStart) firstStart = false;
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
                                    isRefreshing = false;
                                    acProgress.setVisible(false);
                                    if (!autoRefresh) {
                                        String error = getString(R.string.error_failed_try_again);
                                        if (resp.getMessage() != null) error = resp.getMessage();
                                        if (firstStart) {
                                            warning_overlay.setVisibility(View.VISIBLE);
                                            loading_overlay.setVisibility(View.GONE);
                                            homeItem.setVisible(true);
                                        }
                                        showSnackBar(error, 1, 2);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        isRefreshing = false;
                        acProgress.setVisible(false);
                        if (!autoRefresh) {
                            homeItem.setVisible(true);
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
                    isRefreshing = false;
                    acProgress.setVisible(false);
                    if (!autoRefresh) {
                        if (firstStart) {
                            warning_overlay.setVisibility(View.VISIBLE);
                            loading_overlay.setVisibility(View.GONE);
                            homeItem.setVisible(true);
                        }
                        showSnackBar(getString(R.string.internetcheck), 1, 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addOnScrollLisnter() {
        if (layout.getLayoutType() == 1) {
            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new EndlessRecyclerViewScrollListener(gridlayout) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    getpage++;
                    loadData();
                }
            };
        } else if (layout.getLayoutType() == 2) {
            int orientation = ActivityRecycler.this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (layout.allowLandGrid() != 0) {
                    // Retain an instance so that you can call `resetState()` for fresh searches
                    scrollListener = new EndlessRecyclerViewScrollListener(gridlayout) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            getpage++;
                            loadData();
                        }
                    };
                } else {
                    scrollListener = new EndlessRecyclerViewScrollListener(linerLayout) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            getpage++;
                            loadData();
                        }
                    };
                }

            } else {
                scrollListener = new EndlessRecyclerViewScrollListener(linerLayout) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        getpage++;
                        loadData();
                    }
                };
            }
        }
        recycler.addOnScrollListener(scrollListener);
    }

    private void PrepareLayout() {
        if (layout.isRefreshAllowed() != 0) reload = true;
        if (layout.getRefreshInterval() != 0) {
            refreshInterval = layout.getRefreshInterval();
            handler.postDelayed(runnable, refreshInterval * 1000);
            autoRefresh = true;
        }
        if (layout.disableActionBar() != 0) aBar.hide();
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

        if (layout.keepScreenOn() != 0)
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (layout.allowBarBack() != 0) aBar.setDisplayHomeAsUpEnabled(true);
        else aBar.setDisplayHomeAsUpEnabled(false);
        if (layout.showHome() != 0) homeItem.setVisible(true);
        else homeItem.setVisible(false);
        if (layout.allowSearch() != 0) searchItem.setVisible(true);
        else searchItem.setVisible(false);
        if (layout.showBaricon() != 0) {
            aBar.setHomeButtonEnabled(true);
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setIcon(R.mipmap.ic_launcher);
        } else {
            aBar.setHomeButtonEnabled(false);
            aBar.setDisplayShowHomeEnabled(false);
        }
        if (layout.allowBarTitle() != 0) aBar.setDisplayShowTitleEnabled(true);
        else aBar.setDisplayShowTitleEnabled(false);
        if (layout.getBarTitle() != null && !layout.getBarTitle().isEmpty())
            aBar.setTitle(layout.getBarTitle());
        if (layout.getDesc() != null && !layout.getDesc().isEmpty()) {
            if (headlayout.getVisibility() == View.GONE) headlayout.setVisibility(View.VISIBLE);
            actv_title.setText(layout.getDesc());
            actv_title.setVisibility(View.VISIBLE);
        }
        if (layout.getFrom() != null && !layout.getFrom().isEmpty() && layout.getTo() != null && !layout.getTo().isEmpty()) {
            from = layout.getFrom();
            to = layout.getTo();
            from_date.setText(from);
            to_date.setText(to);
            date_layout.setVisibility(View.VISIBLE);
            if (headlayout.getVisibility() == View.GONE) headlayout.setVisibility(View.VISIBLE);
        }
        if (layout.AllowButtons() != 0) PrepareButtons();
        if (layout.allowTotalData() != 0) PrepareMore();
    }

    private void PrepareMore() {
        if (layout.allowTotalData() != 0) {
            if (layout.getTotalData().length > 0) {
                total_values.removeAllViews();
                FormInputData[] moredata = layout.getTotalData();
                for (final FormInputData mdata : moredata) {
                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    int size = ITUtilities.DpToPx(this.getApplicationContext(), 10f);
                    ll.setPadding(size, size, size, size);
                    ll.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_squ));
                    setMargins(ll, size, size, size, size);
                    if (mdata.getHint() != null && !mdata.getHint().isEmpty()) {
                        TextView tit = new TextView(this);
                        tit.setText(mdata.getHint());
                        tit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        tit.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        ll.addView(tit);
                    }
                    if (mdata.getText() != null && !mdata.getText().isEmpty()) {
                        TextView val = new TextView(this);
                        val.setText(mdata.getText());
                        val.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        val.setGravity(Gravity.CENTER_HORIZONTAL);
                        val.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        ll.addView(val);
                    }
                    total_values.addView(ll);
                }
                total_values.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
            view.setLayoutParams(p);
        }
    }

    private void PrepareButtons() {
        if (layout.AllowButtons() != 0) {
            if (layout.getButtons().length > 0) {
                top_left.removeAllViews();
                top_right.removeAllViews();
                bottom_left.removeAllViews();
                bottom_right.removeAllViews();
                FormInputData[] buttons = layout.getButtons();
                for (final FormInputData button : buttons) {
                    LinearLayout pos = null;
                    switch (button.getPlace()) {
                        case 1:
                            pos = top_left;
                            break;
                        case 2:
                            pos = top_right;
                            break;
                        case 3:
                            pos = bottom_left;
                            break;
                        case 4:
                            pos = bottom_right;
                            break;
                    }
                    AppCompatButton btn = new AppCompatButton(this);
                    int paddingDP = ITUtilities.DpToPx(this.getApplicationContext(), 5f);
                    btn.setPadding(paddingDP, paddingDP, paddingDP, paddingDP);
                    int resources = R.drawable.ripple_effect_dark;
                    if (button.getColor() != 0) {
                        switch (button.getColor()) {
                            case 1:
                                resources = R.drawable.button_red;
                                break;
                            case 2:
                                resources = R.drawable.button_orange;
                                break;
                            case 3:
                                resources = R.drawable.button_green;
                                break;
                            case 4:
                                resources = R.drawable.button_blue;
                                break;
                            case 5:
                                resources = R.drawable.button_purple;
                                break;
                            case 6:
                                resources = R.drawable.round_button_face;
                                break;
                            case 7:
                                resources = R.drawable.round_button_google;
                                break;
                            case 8:
                                resources = R.drawable.button_black;
                                break;
                            case 9:
                                resources = R.drawable.layout_border_oval;
                                break;
                        }
                    }
                    btn.setBackgroundResource(resources);
                    if (button.getId() != 0) btn.setId(button.getId());
                    if (button.getText() != null && !button.getText().isEmpty()) {
                        btn.setText(button.getText());
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        btn.setTextColor(ContextCompat.getColor(this, R.color.White));
                        btn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    }
                    if (button.getIntentData() != null) {
                        btn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                IntentData indata = button.getIntentData();
                                session.NewActivity(indata, 0);
                            }
                        });
                    }
                    if (pos != null) pos.addView(btn);
                }
            }

        }
    }

    private void showSnackBar(String msg, final int action, int length) {
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
                    loadData();
                }
            });

            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}