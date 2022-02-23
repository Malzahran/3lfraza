package com.alfraza.app;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.alfraza.app.adapter.DownloadFilesAdapter;
import com.alfraza.app.adapter.ImagesPagerAdapter;
import com.alfraza.app.adapter.VideoAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.DownFileData;
import com.alfraza.app.models.FormInputData;
import com.alfraza.app.models.Images;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.LocationData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.Video;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import klogi.com.RtlViewPager;
import retrofit2.Call;
import retrofit2.Callback;

public class ActivityView extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private Session session;
    private Call<ServerResponse> response;
    private ItemData item;
    private ArrayList<DownFileData> files;
    private ArrayList<LocationData> mapdata;
    private GoogleMap mMap;
    private LayoutData layout;
    private LinearLayout loading_overlay, warning_overlay, maps_layout, total_values,
            top_right, top_left, bottom_right, bottom_left,
            timelayout, titlelayout, pricelayout, statelayout;
    private LinearLayout dotsLayout;
    private CardView downfiles, videoscard, desclayout, contactlayout, moreinfolayout;
    private TextView tv_time, tv_title, tv_title2, tv_price, tv_contact, tv_phone, tv_sms, tv_web, tv_desc, tv_minfo1, tv_minfo2, tv_state;
    private ImageView imageview;
    private ViewPager mViewPager;
    private ImagesPagerAdapter PagerAdapter;
    private ViewPager.OnPageChangeListener PagerOnChange;
    private ArrayList<Images> images;
    private ArrayList<Video> videos;
    private DownloadFilesAdapter adapter;
    private VideoAdapter vadapter;
    private AppCompatButton btn_reserve, btn_action;
    private String reqtype, reqstype, reqatype, reqptype;
    private int reqid, reqmid, reqsid, reqaid;
    private MenuItem homeitem;
    private MenuItem shareItem;
    private MenuItem acprogress;
    private ShareActionProvider mShareActionProvider;
    private CollapsingToolbarLayout collapsingToolbar;
    private DownloadManager downloadManager;
    private ActionBar aBar;
    private String filename, fileurl, fileExt;
    private boolean pendingd = false;

    @Override
    public void onDestroy() {
        session.destroySession();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
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
        setContentView(R.layout.activity_view);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setTitle(getString(R.string.app_name));
        }
        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            initViews();
        } else {
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
                    reqptype = intentData.getReqPtype();
                }
            }
            initViews();
        }
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        warning_overlay = findViewById(R.id.warning_overlay);
        maps_layout = findViewById(R.id.maps_layout);
        tv_time = findViewById(R.id.tv_time);
        tv_price = findViewById(R.id.tv_price);
        tv_title = findViewById(R.id.tv_title);
        tv_title2 = findViewById(R.id.tv_title2);
        tv_desc = findViewById(R.id.tv_desc);
        tv_minfo1 = findViewById(R.id.tv_minfo1);
        tv_minfo2 = findViewById(R.id.tv_minfo2);
        tv_contact = findViewById(R.id.tv_contact);
        tv_phone = findViewById(R.id.tv_phone);
        tv_sms = findViewById(R.id.tv_sms);
        tv_web = findViewById(R.id.tv_web);
        tv_state = findViewById(R.id.tv_state);
        imageview = findViewById(R.id.imageview);
        btn_reserve = findViewById(R.id.btn_reserve);
        btn_action = findViewById(R.id.btn_action);
        top_right = findViewById(R.id.top_right);
        top_left = findViewById(R.id.top_left);
        bottom_right = findViewById(R.id.bottom_right);
        bottom_left = findViewById(R.id.bottom_left);
        statelayout = findViewById(R.id.statelayout);
        total_values = findViewById(R.id.total_values);
        timelayout = findViewById(R.id.timelayout);
        pricelayout = findViewById(R.id.pricelayout);
        titlelayout = findViewById(R.id.titlelayout);
        desclayout = findViewById(R.id.desclayout);
        contactlayout = findViewById(R.id.contactlayout);
        moreinfolayout = findViewById(R.id.moreinfolayout);
        downfiles = findViewById(R.id.downfiles);
        videoscard = findViewById(R.id.videoscard);
        item = new ItemData();
        files = new ArrayList<>();
        images = new ArrayList<>();
        videos = new ArrayList<>();
        mapdata = new ArrayList<>();
        RecyclerView recycler = findViewById(R.id.files_recycler);
        RecyclerView video_recycler = findViewById(R.id.video_recycler);
        LinearLayoutManager linerLayout = new LinearLayoutManager(this);
        GridLayoutManager gridlayout = new GridLayoutManager(this, 2);
        adapter = new DownloadFilesAdapter(this, files);
        vadapter = new VideoAdapter(this, videos);
        recycler.setLayoutManager(linerLayout);
        recycler.setAdapter(adapter);
        video_recycler.setLayoutManager(gridlayout);
        video_recycler.setAdapter(vadapter);

        PagerOnChange = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        mViewPager = (RtlViewPager) findViewById(R.id.pager_container);
        dotsLayout = findViewById(R.id.layoutDotspager);
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
        menuInflater.inflate(R.menu.menu_share, menu);
        shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        shareItem.setVisible(false);
        homeitem = menu.findItem(R.id.menu_home);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        acprogress = menu.findItem(R.id.action_progress);
        searchItem.setVisible(false);
        homeitem.setVisible(false);
        acprogress.setVisible(false);
        loadData();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        maps_layout.setVisibility(View.VISIBLE);
        if (mapdata.size() > 0) {
            addMarkersToMap(mapdata);
        }
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (fileurl != null && filename != null && pendingd) {
                DownloadFile(fileurl, filename, fileExt);
            }
            // permission granted

        }
    }

    public void DownloadFile(String url, String fname, String ext) {
        filename = fname;
        fileExt = ext;
        fileurl = url;
        URL furl = null; // missing 'http://' will cause crashed
        try {
            furl = session.get_url(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (furl != null) {
            if (!isStoragePermissionGranted()) {
                pendingd = true;
                return;
            }
            Uri dURL = null;
            try {
                dURL = Uri.parse(furl.toURI().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (dURL != null) {
                DownloadManager.Request request = new DownloadManager.Request(dURL);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(true);
                request.setTitle(getString(R.string.app_name));
                request.setDescription(filename);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + Constants.DOWNLOAD_BASE + "/" + "/" + filename + "." + fileExt);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                downloadManager.enqueue(request);
                session.showToast(getString(R.string.downloading), 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reserve:
                if (item != null) {
                    if (item.getResvintent() != null) session.NewActivity(item.getResvintent(), 0);
                    else ReserveProccess();
                }
                break;
            case R.id.btn_action:
                if (item != null && item.getActionintent() != null)
                    session.NewActivity(item.getActionintent(), 0);
                break;
        }
    }

    private void loadData() {
        try {
            acprogress.setVisible(true);
            if (loading_overlay.getVisibility() == View.GONE) {
                loading_overlay.setVisibility(View.VISIBLE);
            }
            if (warning_overlay.getVisibility() == View.VISIBLE) {
                warning_overlay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setType(reqatype);
        misc.setItem_id(reqid);
        misc.setMainid(reqmid);
        misc.setSubid(reqsid);
        misc.setAdid(reqaid);

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
                                    acprogress.setVisible(false);
                                    loading_overlay.setVisibility(View.GONE);
                                    layout = resp.getLayoutInfo();
                                    item = resp.getItemInfo();
                                    if (layout != null && item != null) {
                                        PrepareLayout();
                                        PrepareData();
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
                                    warning_overlay.setVisibility(View.VISIBLE);
                                    loading_overlay.setVisibility(View.GONE);
                                    homeitem.setVisible(true);
                                    acprogress.setVisible(false);
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
                            warning_overlay.setVisibility(View.VISIBLE);
                            loading_overlay.setVisibility(View.GONE);
                            String error = getString(R.string.error_failed_later);
                            showSnackbar(error, 0, 1);
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
                    warning_overlay.setVisibility(View.VISIBLE);
                    loading_overlay.setVisibility(View.GONE);
                    homeitem.setVisible(true);
                    showSnackbar(error, 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepareMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    private void PrepareLayout() {
        if (layout.allowBarBack() != 0)
            aBar.setDisplayHomeAsUpEnabled(true);
        else aBar.setDisplayHomeAsUpEnabled(false);

        if (layout.showHome() != 0) homeitem.setVisible(true);
        else homeitem.setVisible(false);

        if (layout.showBaricon() != 0) {
            aBar.setHomeButtonEnabled(true);
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setIcon(R.mipmap.ic_launcher);
        } else {
            aBar.setHomeButtonEnabled(false);
            aBar.setDisplayShowHomeEnabled(false);
        }

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

        if (layout.allowBarTitle() != 0) {
            if (item.getImg() != null && !item.getImg().isEmpty())
                collapsingToolbar.setTitleEnabled(true);
            else collapsingToolbar.setTitleEnabled(false);
            aBar.setDisplayShowTitleEnabled(true);
        } else {
            collapsingToolbar.setTitleEnabled(false);
            aBar.setDisplayShowTitleEnabled(false);
        }

        if (layout.getBarTitle() != null && !layout.getBarTitle().isEmpty()) {
            collapsingToolbar.setTitle(layout.getBarTitle());
            aBar.setTitle(layout.getBarTitle());
        }

        if (layout.disableActionBar() != 0) {
            aBar.hide();
            collapsingToolbar.setTitleEnabled(false);
        }

        if (layout.AllowButtons() != 0) PrepareButtons();

        if (layout.allowTotalData() != 0) PrepareMore();
    }

    private void PrepareMore() {
        if (layout.allowTotalData() != 0) {
            if (layout.getTotalData().length != 0) {
                total_values.removeAllViews();
                FormInputData[] moredata = layout.getTotalData();
                for (final FormInputData mdata : moredata) {
                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(10, 10, 10, 10);
                    ll.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_squ));
                    if (mdata.getHint() != null) {
                        TextView tit = new TextView(this);
                        tit.setText(mdata.getHint());
                        tit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        tit.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        ll.addView(tit);
                    }
                    if (mdata.getText() != null) {
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

    private void PrepareButtons() {
        if (layout.AllowButtons() != 0) {
            if (layout.getButtons().length != 0) {
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
                    if (button.getId() != 0) {
                        btn.setId(button.getId());
                    }
                    if (button.getText() != null) {
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
                    if (pos != null) {
                        pos.addView(btn);
                    }
                }
            }

        }
    }

    private void PrepareData() {

        if (item.getFiles() != null && item.getFiles().length > 0) {
            files.clear();
            files.addAll((Arrays.asList(item.getFiles())));
            adapter.notifyDataSetChanged();
            downfiles.setVisibility(View.VISIBLE);
        }

        if (item.getTime() != null && !item.getTime().isEmpty()) {
            tv_time.setText(item.getTime());
            timelayout.setVisibility(View.VISIBLE);
        }

        if (item.getPrice() != null && !item.getPrice().isEmpty()) {
            tv_price.setText(item.getPrice());
            if (item.getReservetext() != null && !item.getReservetext().isEmpty())
                btn_reserve.setText(item.getReservetext());
            btn_reserve.setOnClickListener(this);
            pricelayout.setVisibility(View.VISIBLE);
        }

        if (item.getAction() != null && !item.getAction().isEmpty() && item.getActionintent() != null) {
            btn_action.setText(item.getAction());
            btn_action.setOnClickListener(this);
            btn_action.setVisibility(View.VISIBLE);
        }

        if (item.getTitle() != null && !item.getTitle().isEmpty()) {
            tv_title.setText(item.getTitle());
            tv_title.setVisibility(View.VISIBLE);
            titlelayout.setVisibility(View.VISIBLE);
        }

        if (item.getTitle2() != null && !item.getTitle2().isEmpty()) {
            tv_title2.setText(item.getTitle2());
            tv_title2.setVisibility(View.VISIBLE);
            titlelayout.setVisibility(View.VISIBLE);
        }

        if (item.getDesc() != null && !item.getDesc().isEmpty()) {
            tv_desc.setText(ITUtilities.fromHtml(item.getDesc()));
            desclayout.setVisibility(View.VISIBLE);
        }

        if (item.getMinfo() != null && !item.getMinfo().isEmpty()) {
            tv_minfo1.setText(item.getMinfo());
            tv_minfo1.setVisibility(View.VISIBLE);
            moreinfolayout.setVisibility(View.VISIBLE);
        }

        if (item.getMinfo2() != null && !item.getMinfo2().isEmpty()) {
            tv_minfo2.setText(item.getMinfo2());
            tv_minfo2.setVisibility(View.VISIBLE);
            moreinfolayout.setVisibility(View.VISIBLE);
        }

        if (item.getContacttitle() != null && !item.getContacttitle().isEmpty()) {
            tv_contact.setText(item.getContacttitle());
            tv_contact.setVisibility(View.VISIBLE);

        }

        if (item.getPhone() != null && !item.getPhone().isEmpty()) {
            tv_phone.setText(String.format("%s %s", getString(R.string.phone_label), item.getPhone()));
            if (contactlayout.getVisibility() == View.GONE) {
                contactlayout.setVisibility(View.VISIBLE);
            }
            tv_phone.setVisibility(View.VISIBLE);
            tv_phone.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    session.CallNumber(item.getPhone());
                }
            });
        }
        if (item.getSms() != null && !item.getSms().isEmpty()) {
            tv_sms.setText(String.format("%s %s", getString(R.string.sms_label), item.getSms()));
            tv_sms.setVisibility(View.VISIBLE);
            tv_sms.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    session.SendSms(item.getSms());
                }
            });
            if (contactlayout.getVisibility() == View.GONE)
                contactlayout.setVisibility(View.VISIBLE);
        }

        if (item.getUrl() != null && !item.getUrl().isEmpty()) {
            if (item.getUrlname() != null && !item.getUrlname().isEmpty())
                tv_web.setText(item.getUrlname());
            else tv_web.setText(getString(R.string.open_link));
            if (contactlayout.getVisibility() == View.GONE)
                contactlayout.setVisibility(View.VISIBLE);
            tv_web.setVisibility(View.VISIBLE);
            tv_web.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    URL uri = null; // missing 'http://' will cause crashed
                    try {
                        uri = session.get_url(item.getUrl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (uri != null) {
                        session.OpenLink(uri);
                    }
                }
            });
        }
        if (item.getImg() != null && !item.getImg().isEmpty()) {
            session.imgLoader()
                    .load(item.getImg())
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(imageview);
            imageview.setVisibility(View.VISIBLE);
        }

        if (item.haveAlbum() != 0 && item.getAlbumImage() != null && item.getAlbumImage().length > 0) {
            images.clear();
            images.addAll((Arrays.asList(item.getAlbumImage())));
            PagerAdapter = new ImagesPagerAdapter(this, images);
            mViewPager.setAdapter(PagerAdapter);
            mViewPager.addOnPageChangeListener(PagerOnChange);
            if (images.size() > 1) addBottomDots(0);
            mViewPager.setVisibility(View.VISIBLE);
        }

        if (item.getLocations() != null && item.getLocations().length > 0) {
            mapdata.clear();
            mapdata.addAll((Arrays.asList(item.getLocations())));
            prepareMap();
        }

        if (item.haveVideo() != 0 && item.getVideos() != null && item.getVideos().length > 0) {
            videos.clear();
            videos.addAll((Arrays.asList(item.getVideos())));
            vadapter.notifyDataSetChanged();
            videoscard.setVisibility(View.VISIBLE);
        }

        if (item.getState() != null && !item.getState().isEmpty()) {
            tv_state.setText(item.getState());
            statelayout.setVisibility(View.VISIBLE);
        }

        if (item.getShareurl() != null && !item.getShareurl().isEmpty()) {
            if (mShareActionProvider != null) {
                shareItem.setVisible(true);
                mShareActionProvider.setShareIntent(getShareIntent());
            } else {
                Log.d("Share prov", "Share action provider is null");
            }
        }
    }

    private void addBottomDots(int position) {
        TextView[] dots = new TextView[PagerAdapter.getCount()];
        dotsLayout.removeAllViews();
        dotsLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < PagerAdapter.getCount(); i++) {
            dots[i] = new TextView(this);
            dots[i].setText(ITUtilities.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.White));
            dotsLayout.addView(dots[i]);
        }
        if (PagerAdapter.getCount() > 0)
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    private void ReserveProccess() {

        try {
            acprogress.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setType(reqatype);
        misc.setItem_id(reqid);
        misc.setMainid(reqmid);
        misc.setSubid(reqsid);
        misc.setAdid(reqaid);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(reqtype);
        request.setSubType(reqptype);
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
                                    session.showToast(getString(R.string.success_done), 1);
                                    pricelayout.setVisibility(View.GONE);
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
                                    acprogress.setVisible(false);
                                    String error = getString(R.string.error_failed);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    session.showToast(error, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        acprogress.setVisible(false);
                        String error = getString(R.string.error_failed_later);
                        session.showToast(error, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    acprogress.setVisible(false);
                    session.showToast(getString(R.string.internetcheck), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Returns a share intent
     */
    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (item != null) {
            if (item.getTitle2() != null) {
                intent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle2());
            }
            if (item.getShareurl() != null) {
                intent.putExtra(Intent.EXTRA_TEXT, item.getShareurl());
            }
        }
        return intent;
    }

    private void addMarkersToMap(ArrayList<LocationData> data) {
        mMap.clear();
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (int i = 0; i < data.size(); i++) {
            LatLng ll = new LatLng(data.get(i).getLat(), data.get(i).getLon());
            b.include(ll);
            BitmapDescriptor bitmapMarker;
            switch (data.get(i).getColor()) {
                case 0:
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case 1:
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case 2:
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    break;
                default:
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
            }
            mMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .title(data.get(i).getTitle())
                    .snippet(data.get(i).getSnippet())
                    .icon(bitmapMarker));

        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15f);
        mMap.animateCamera(cu);
    }

    private void showSnackbar(String msg, final int action, int length) {
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
