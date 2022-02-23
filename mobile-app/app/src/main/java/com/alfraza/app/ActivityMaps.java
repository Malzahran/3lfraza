package com.alfraza.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.LocationData;
import com.alfraza.app.models.MiscData;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Session session;
    private LayoutData layout;
    private LinearLayout loading_overlay, warning_overlay;
    private ActionBar aBar;
    private MenuItem homeItem;
    private MenuItem acProgress;
    private String reqtype, reqstype, reqatype;
    private int reqid, reqmid, reqsid, reqaid;
    private Call<ServerResponse> response;
    private ArrayList<LocationData> data;
    private CheckBox autoUpdate, autoZoom;
    private int interval = 5;
    private boolean firstStart = true, autoUP = false, autoZOOM = true;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (autoUP) {
                getMapMarks();
                interval = session.pref().Mapsinterval();
                handler.postDelayed(this, interval * 1000); // for interval...
            }
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, interval * 1000);
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
        setContentView(R.layout.activity_maps);
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
            initViews();
        }
    }

    private void initViews() {
        warning_overlay = findViewById(R.id.warning_overlay);
        autoUpdate = findViewById(R.id.auto_update);
        autoZoom = findViewById(R.id.auto_zoom);
        autoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    autoUP = true;
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, interval * 1000);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    autoUP = false;
                    handler.removeCallbacks(runnable);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
        autoZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                autoZOOM = ((CheckBox) v).isChecked();
            }
        });
    }

    private void prepareMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inside, menu);
        homeItem = menu.findItem(R.id.menu_home);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        acProgress = menu.findItem(R.id.action_progress);
        homeItem.setVisible(false);
        searchItem.setVisible(false);
        acProgress.setVisible(false);
        prepareMap();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
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
        data = new ArrayList<>();
        getMapMarks();
    }

    private void getMapMarks() {
        try {
            acProgress.setVisible(true);
            if (firstStart && loading_overlay.getVisibility() == View.GONE) {
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
                                    acProgress.setVisible(false);
                                    if (firstStart) {
                                        loading_overlay.setVisibility(View.GONE);
                                    }
                                    data.clear();
                                    data.addAll((Arrays.asList(resp.getLocations())));
                                    layout = resp.getLayoutInfo();
                                    if (firstStart && layout != null) PrepareLayout();
                                    addMarkersToMap(data);
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
                                    String error = getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    acProgress.setVisible(false);
                                    if (firstStart) {
                                        warning_overlay.setVisibility(View.VISIBLE);
                                        loading_overlay.setVisibility(View.GONE);
                                        homeItem.setVisible(true);
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
                        acProgress.setVisible(false);
                        homeItem.setVisible(true);
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        String error = getString(R.string.error_failed_later);
                        showSnackBar(error, 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    String error = getString(R.string.internetcheck);
                    acProgress.setVisible(false);
                    if (firstStart) {
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        homeItem.setVisible(true);
                    }
                    showSnackBar(error, 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void PrepareLayout() {
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
        if (layout.allowBarBack() != 0) aBar.setDisplayHomeAsUpEnabled(true);
        else aBar.setDisplayHomeAsUpEnabled(false);
        if (layout.showHome() != 0) homeItem.setVisible(true);
        else homeItem.setVisible(false);
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
        if (layout.getBarTitle() != null) aBar.setTitle(layout.getBarTitle());
        if (layout.isZoomAllowed() == 0) autoZoom.setVisibility(View.GONE);
        if (layout.isRefreshAllowed() == 0) autoUpdate.setVisibility(View.GONE);
    }

    private void addMarkersToMap(ArrayList<LocationData> data) {
        if (data.size() > 0) {
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

            if (autoZOOM) {
                LatLngBounds bounds = b.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15f);
                mMap.animateCamera(cu);
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
        RelativeLayout view = findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(view, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMapMarks();
                }
            });
            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}