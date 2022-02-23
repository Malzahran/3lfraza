package com.alfraza.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.adapter.AutoSuggestAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.transformations.Animate;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.PlacesSearch;
import com.alfraza.app.models.Tracker;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class ActivityLocation extends FragmentActivity implements View.OnClickListener {

    private Session session;
    private Call<ServerResponse> response;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    private List<PlacesSearch> places;
    private CardView start_layout, permission_layout, gps_layout, cover_layout, connection_layout;
    private TextView your_city;
    private CircularFillableLoaders loader;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    private final int REQUEST_LOCATION_PERMISSION = 214;
    private boolean SETTINGS_CANCELED = false;
    private boolean place_selected = false;
    private Marker mMarker;
    /**
     * FusedLocationProviderApi Save request parameters
     */
    private LocationRequest mLocationRequest;


    /**
     * Provide callbacks for location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * An object representing the current location
     */
    private Location mCurrentLocation;

    //A client that handles connection / connection failures for Google locations
    // (changed from play-services 11.0.0)
    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_location);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initComponents();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            session.hideKeyboard(getCurrentFocus());
                            if (mCurrentLocation == null)
                                mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
                            mCurrentLocation.setLatitude(latLng.latitude);
                            mCurrentLocation.setLongitude(latLng.longitude);
                            if (mMarker == null)
                                mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                            else mMarker.setPosition(latLng);
                            if (response != null && response.isExecuted()) response.cancel();
                            requestCity();
                        }
                    });
                }

            });

    }

    private void initComponents() {
        loader = findViewById(R.id.loader);
        start_layout = findViewById(R.id.start_layout);
        permission_layout = findViewById(R.id.permission_layout);
        gps_layout = findViewById(R.id.gps_layout);
        connection_layout = findViewById(R.id.connection_layout);
        cover_layout = findViewById(R.id.cover_layout);
        your_city = findViewById(R.id.current_city);
        AppCompatButton btn_lang = findViewById(R.id.btn_lang);
        AppCompatButton btn_notify_location = findViewById(R.id.btn_notify_location);
        AppCompatButton btn_start = findViewById(R.id.btn_start);
        AppCompatButton btn_gps = findViewById(R.id.btn_gps);
        AppCompatButton btn_permission = findViewById(R.id.btn_permission);
        AppCompatButton btn_connection = findViewById(R.id.btn_connection);
        btn_lang.setOnClickListener(this);
        btn_notify_location.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_gps.setOnClickListener(this);
        btn_permission.setOnClickListener(this);
        btn_connection.setOnClickListener(this);
        places = new ArrayList<>();
        final AutoCompleteTextView autoCompleteTextView =
                findViewById(R.id.autoCompleteTextView1);
        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                R.layout.auto_complete_dropdown);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = autoCompleteTextView.getText().toString();
                    if (!search.isEmpty()) {
                        session.hideKeyboard(getCurrentFocus());
                        makeApiCall(search);
                    } else session.showToast(getString(R.string.hint_input_search), 1);
                    return true;
                }
                return false;
            }
        });
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        PlacesSearch place = autoSuggestAdapter.getObject(position);
                        autoCompleteTextView.setText(place.name);
                        session.hideKeyboard(getCurrentFocus());
                        if (place.lat != 0 && place.lon != 0) {
                            if (mCurrentLocation == null)
                                mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
                            mCurrentLocation.setLatitude(place.lat);
                            mCurrentLocation.setLongitude(place.lon);
                            LatLng latLng = new LatLng(place.lat, place.lon);
                            if (mMarker == null) {
                                mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                            } else {
                                mMarker.setPosition(latLng);
                            }
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(mMarker.getPosition())
                                    .zoom(15)
                                    .bearing(0)
                                    .tilt(45)
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            if (response != null && response.isExecuted()) response.cancel();
                            requestCity();
                        }
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        ImageButton action_search = findViewById(R.id.action_search);
        action_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.requestFocus();
                session.showKeyboard(autoCompleteTextView);
            }
        });

        ImageButton action_location = findViewById(R.id.action_location);
        action_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void makeApiCall(final String text) {
        ServerRequest request = new ServerRequest();
        MiscData misc = new MiscData();
        misc.setSearchq(text);
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.STORE_CITY_SEARCH);
        request.setMisc(misc);
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    switch (resp.getResult()) {
                        case Constants.SUCCESS:
                            places.clear();
                            if (resp.places != null && resp.places.size() > 0)
                                places.addAll(resp.places);
                            //IMPORTANT: set data here and notify
                            autoSuggestAdapter.setData(places);
                            break;
                        default:
                            makeApiCall(text);
                            break;
                    }
                } else makeApiCall(text);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled())
                    makeApiCall(text);
            }
        });
    }

    private void checkPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    initGoogleMapLocation();
                } else
                    ActivityCompat.requestPermissions(ActivityLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else initGoogleMapLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGoogleMapLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /*
          Location Setting API to
         */
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
        /*
         * Callback returning location result
         */
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                mCurrentLocation = result.getLocations().get(0);
                if (mCurrentLocation != null) {
                    mMap.clear();
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                    //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_client);
                    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    options.icon(icon);
                    mMarker = mMap.addMarker(options);


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(mMarker.getPosition())
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    /*
                      To get location information consistently
                      mLocationRequest.setNumUpdates(1) Commented out
                      Uncomment the code below
                     */
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    requestCity();
                }
            }

            //Location nMeaning that all relevant information is available
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        //To get location information only once here
        mLocationRequest.setNumUpdates(3);
        //Acquired location information based on balance of battery and accuracy (somewhat higher accuracy)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        /*
          Stores the type of location service the client wants to use. Also used for positioning.
         */
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ActivityLocation.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    e.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
            }
        });
        Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
        locationResponse.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ActivityCompat.checkSelfPermission(ActivityLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });
        //When the location information is not set and acquired, callback
        locationResponse.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        break;
                }
            }
        });
    }

    private void requestCity() {
        if (connection_layout.getVisibility() == View.VISIBLE)
            Animate.slideToBottom(connection_layout, null);
        if (cover_layout.getVisibility() == View.VISIBLE)
            Animate.slideToBottom(cover_layout, null);
        if (gps_layout.getVisibility() == View.VISIBLE)
            Animate.slideToBottom(gps_layout, null);
        if (permission_layout.getVisibility() == View.VISIBLE)
            Animate.slideToBottom(permission_layout, null);
        if (start_layout.getVisibility() == View.VISIBLE)
            Animate.slideToBottom(start_layout, null);
        if (loader.getVisibility() == View.GONE)
            Animate.slideToTop(loader, null);
        Tracker gps = new Tracker();
        gps.setLongitude(mCurrentLocation.getLongitude());
        gps.setLatitude(mCurrentLocation.getLatitude());
        Double accuracyInFeet = mCurrentLocation.getAccuracy() * 3.28;
        gps.setAccuracy(accuracyInFeet);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.STORE_CITY_TYPE);
        request.setTracker(gps);
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (loader.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(loader, null);
                if (resp != null) {
                    switch (resp.getResult()) {
                        case Constants.SUCCESS:
                            session.pref().setCity(resp.city.city_id);
                            session.pref().setCityGroup(resp.city.city_group);
                            session.pref().setCityName(resp.city.city_name);
                            session.pref().setLatitude(mCurrentLocation.getLatitude());
                            session.pref().setLongitude(mCurrentLocation.getLongitude());
                            your_city.setText(String.format("%s%s", resp.city.city_desc, resp.city.city_name));
                            if (start_layout.getVisibility() == View.GONE)
                                Animate.slideToTop(start_layout, null);
                            break;
                        case Constants.NO_COVER:
                            String msg = getString(R.string.sorry_no_cover);
                            if (resp.getMessage() != null && !resp.getMessage().isEmpty())
                                msg = resp.getMessage();
                            TextView covering_msg = findViewById(R.id.covering_msg);
                            covering_msg.setText(ITUtilities.fromHtml(msg));
                            if (cover_layout.getVisibility() == View.GONE)
                                Animate.slideToTop(cover_layout, null);

                            break;
                        default:
                            requestCity();
                            break;
                    }

                } else requestCity();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    if (loader.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(loader, null);
                    if (connection_layout.getVisibility() == View.GONE)
                        Animate.slideToTop(connection_layout, null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    initGoogleMapLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    SETTINGS_CANCELED = true;
                    if (connection_layout.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(connection_layout, null);
                    if (cover_layout.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(cover_layout, null);
                    if (start_layout.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(start_layout, null);
                    if (loader.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(loader, null);
                    if (permission_layout.getVisibility() == View.VISIBLE)
                        Animate.slideToBottom(permission_layout, null);
                    if (gps_layout.getVisibility() == View.GONE)
                        Animate.slideToTop(gps_layout, null);
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                if (connection_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(connection_layout, null);
                if (cover_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(cover_layout, null);
                if (start_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(start_layout, null);
                if (loader.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(loader, null);
                if (permission_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(permission_layout, null);
                if (gps_layout.getVisibility() == View.GONE) Animate.slideToTop(gps_layout, null);
            } else {
                if (connection_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(connection_layout, null);
                if (cover_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(cover_layout, null);
                if (start_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(start_layout, null);
                if (loader.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(loader, null);
                if (permission_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(permission_layout, null);
                if (gps_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(gps_layout, null);
                initGoogleMapLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (connection_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(connection_layout, null);
                if (cover_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(cover_layout, null);
                if (gps_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(gps_layout, null);
                if (start_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(start_layout, null);
                if (loader.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(loader, null);
                if (permission_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(permission_layout, null);
                initGoogleMapLocation();
            } else {
                if (connection_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(connection_layout, null);
                if (cover_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(cover_layout, null);
                if (gps_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(gps_layout, null);
                if (start_layout.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(start_layout, null);
                if (loader.getVisibility() == View.VISIBLE)
                    Animate.slideToBottom(loader, null);
                if (permission_layout.getVisibility() == View.GONE)
                    Animate.slideToTop(permission_layout, null);
            }
        }

    }

    private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }

    @Override
    protected void onDestroy() {
        if (response != null && response.isExecuted()) response.cancel();
        super.onDestroy();
        session.destroySession();
    }


    @Override
    protected void onResume() {
        super.onResume();
        session.clearArea();
        if (!SETTINGS_CANCELED && !place_selected) checkPermission();
        if (SETTINGS_CANCELED) SETTINGS_CANCELED = false;
        if (place_selected) place_selected = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lang:
                session.clearArea();
                session.changeLang();
                break;
            case R.id.btn_permission:
                checkPermission();
                break;
            case R.id.btn_gps:
                openGpsEnableSetting();
                break;
            case R.id.btn_notify_location:
                session.showNotifyDialog();
                break;
            case R.id.btn_connection:
                requestCity();
                break;
            case R.id.btn_start:
                session.goHome();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else finishAffinity();
    }
}