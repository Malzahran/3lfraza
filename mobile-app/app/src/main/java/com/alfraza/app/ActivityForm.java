package com.alfraza.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alfraza.app.helpers.utilities.CallbackDialog;
import com.alfraza.app.helpers.utilities.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.alfraza.app.adapter.ExpandItemsAdapter;
import com.alfraza.app.adapter.UploadedFilesAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.customs.DateDisplayPicker;
import com.alfraza.app.helpers.customs.KeyboardStatusDetector;
import com.alfraza.app.helpers.customs.TimeDisplayPicker;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.ExpandItemsData;
import com.alfraza.app.models.FormInputData;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.NewFormData;
import com.alfraza.app.models.SelectedSpinnerData;
import com.alfraza.app.models.SpinnerData;
import com.alfraza.app.models.UpFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityForm extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private static final int REQUEST_GPS = 127;
    private static final int REQUEST_READ_STORAGE_FP = 124;
    private static final int REQUEST_READ_STORAGE_PP = 125;
    private static final int REQUEST_CAMERA = 126;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Session session;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation, lastLocation;
    private GoogleMap mMap;
    private Call<ServerResponse> response;
    private NewFormData formData;
    private FormInputData[] inputData;
    private ArrayList<FormInputData> moreData = new ArrayList<>();
    private LayoutData layout;
    private LinearLayout loading_overlay, warning_overlay, maps_layout, llTop, llinputs, llbottom, llend, upfile_llayout;
    private RelativeLayout loc_layout;
    private TextView formhead, formdesc, forminst, formprice, files_add_head, loc_title, tv_pre_bottom_notes, tv_bottom_notes;
    private FrameLayout img_frame;
    private CheckBox chk_location;
    private ImageView imv_img;
    private ProgressBar progressBar;
    private CoordinatorLayout main_layout;
    private AlertDialog progressDialog;
    private String reqtype, reqstype, reqatype, reqptype;
    private int reqid, reqmid, reqsid, reqaid;
    private RecyclerView files_recycler;
    private ExpandItemsAdapter ExpandAdapter;
    private AppCompatButton btn_sel_img, btn_sel_file, btn_chg_img, btn_chg_file, post_form_btn, btn_clr_files;
    private ActionBar aBar;
    private AlertDialog dialogegps;
    private MenuItem homeitem;
    private MenuItem acprogress;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<SelectedSpinnerData> spinnersel = new ArrayList<>();
    private boolean postform = false;
    private boolean gps = false;

    private boolean isStorageReadPermissionGranted(Integer code) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
        if (response != null && response.isExecuted()) response.cancel();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        if (dialogegps != null && dialogegps.isShowing()) dialogegps.dismiss();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gps) gpsState(true);
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
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_form);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setTitle(getString(R.string.app_name));
        }
        processIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
        main_layout = findViewById(R.id.main_layout);
        warning_overlay = findViewById(R.id.warning_overlay);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_progress, nullParent);
        builder.setView(view);
        builder.setCancelable(false);
        progressDialog = builder.create();
        maps_layout = findViewById(R.id.maps_layout);
        llTop = findViewById(R.id.lltop);
        llinputs = findViewById(R.id.llinputs);
        llbottom = findViewById(R.id.llbottom);
        llend = findViewById(R.id.llend);
        upfile_llayout = findViewById(R.id.upfile_llayout);
        formhead = findViewById(R.id.form_head);
        files_add_head = findViewById(R.id.files_add_head);
        formprice = findViewById(R.id.tv_price);
        formdesc = findViewById(R.id.form_desc);
        forminst = findViewById(R.id.form_inst);
        img_frame = findViewById(R.id.img_frame);
        imv_img = findViewById(R.id.imv_img);
        progressBar = findViewById(R.id.img_progress);
        loc_layout = findViewById(R.id.loc_layout);
        loc_title = findViewById(R.id.loc_title);
        tv_pre_bottom_notes = findViewById(R.id.tv_pre_bottom_notes);
        tv_bottom_notes = findViewById(R.id.tv_bottom_notes);
        chk_location = findViewById(R.id.chk_location);
        files_recycler = findViewById(R.id.files_recycler);
        btn_sel_file = findViewById(R.id.files_sel_btn);
        btn_sel_img = findViewById(R.id.img_sel_btn);
        btn_chg_img = findViewById(R.id.btn_chng_img);
        btn_chg_file = findViewById(R.id.btn_chng_files);
        btn_clr_files = findViewById(R.id.btn_clr_files);
        post_form_btn = findViewById(R.id.post_form_btn);
        KeyboardStatusDetector keyBoard = new KeyboardStatusDetector();
        keyBoard.registerActivity(this);         //or register to a view
        keyBoard.setVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean keyboardVisible) {
                if (keyboardVisible) hideMap(true);
                else hideMap(false);
            }
        });
        btn_sel_file.setOnClickListener(this);
        btn_sel_img.setOnClickListener(this);
        post_form_btn.setOnClickListener(this);
        btn_chg_img.setOnClickListener(this);
        btn_chg_file.setOnClickListener(this);
        btn_clr_files.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!postform) finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inside, menu);
        homeitem = menu.findItem(R.id.menu_home);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        acprogress = menu.findItem(R.id.action_progress);
        homeitem.setVisible(false);
        searchItem.setVisible(false);
        acprogress.setVisible(false);
        GetFormLayout();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    addThemToView();

                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    addThemToView();
                }
                break;
        }

    }

    public void gpsState(boolean state) {
        gps = state;
        if (state) {
            if (!mGoogleApiClient.isConnected() && gps) {
                mGoogleApiClient.connect();
            }
            chk_location.setChecked(true);
            PrepareMap();
            checkLocation();
        } else {
            chk_location.setChecked(false);
            if (mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
            }
            if (mMap != null) mMap.clear();
            mLocation = null;
            maps_layout.setVisibility(View.GONE);
        }
    }

    private void PrepareMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) mapFragment.getMapAsync(this);
        } else {
            if (maps_layout.getVisibility() == View.GONE) maps_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        maps_layout.setVisibility(View.VISIBLE);
    }

    private void addMarkersToMap(Location location) {
        if (mMap != null) {
            mMap.clear();
            if (location != null) {
                boolean mapzoom;
                if (lastLocation != null) {
                    double lat1 = lastLocation.getLatitude();
                    double lng1 = lastLocation.getLongitude();
                    double lat2 = location.getLatitude();
                    double lng2 = location.getLongitude();

                    // lat1 and lng1 are the values of a previously stored location
                    // if distance > 0.1 KM
                    // we zoom to the location on the map
                    mapzoom = distance(lat1, lng1, lat2, lng2) > 0.1;
                } else {
                    mapzoom = true;
                }
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                mMap.addMarker(new MarkerOptions()
                        .position(ll)
                        .title(getString(R.string.my_location))
                        .icon(bitmapMarker));
                lastLocation = location;
                if (mapzoom) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(ll)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    /**
     * calculates the distance between two locations in KM
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in KM

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // output distance, in KM
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_GPS);
                return;
            }
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        addMarkersToMap(mLocation);

    }

    protected void startLocationUpdates() {
        // Create the location request
        long UPDATE_INTERVAL = 15 * 1000;
        long FASTEST_INTERVAL = 10000;
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_GPS);
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        if (mLocation == null)
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        } else {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
        isLocationEnabled();
    }

    private void showAlert() {
        AlertDialog.Builder dialogebuilder = new AlertDialog.Builder(this);
        dialogebuilder.setTitle(getString(R.string.enable_locatin_head))
                .setMessage(getString(R.string.enable_locatin_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.location_settings_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        dialogegps.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        chk_location.setChecked(false);
                    }
                });
        dialogegps = dialogebuilder.create();
        dialogegps.show();
        float paddingDp = 5f;
        // Convert to pixels
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());

        Button pbutton = dialogegps.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.White));
        pbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        Button nbutton = dialogegps.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        nbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void addThemToView() {
        ArrayList<UpFile> filesel = new ArrayList<>();
        if (photoPaths != null)
            for (int pc = 0; pc < photoPaths.size(); pc++) {
                String file = photoPaths.get(pc);
                filesel.add(new UpFile(file, 1));
            }

        if (docPaths != null)
            for (int pc = 0; pc < docPaths.size(); pc++) {
                String file = docPaths.get(pc);
                filesel.add(new UpFile(file, 2));
            }

        if (files_recycler != null && filesel.size() > 0) {
            files_recycler.setVisibility(View.VISIBLE);
            if (formData.UploadImg() != 0 && photoPaths.size() > 0) {
                if (formData.getChange_img_btn() != null) {
                    btn_chg_img.setText(formData.getChange_img_btn());
                }
                btn_chg_img.setVisibility(View.VISIBLE);
                if (btn_clr_files.getVisibility() == View.GONE) {
                    btn_clr_files.setVisibility(View.VISIBLE);
                }
                btn_sel_img.setVisibility(View.GONE);
            }
            if (formData.UploadFile() != 0 && docPaths.size() > 0) {
                if (formData.getChange_file_btn() != null) {
                    btn_chg_file.setText(formData.getChange_file_btn());
                }
                btn_chg_file.setVisibility(View.VISIBLE);
                if (btn_clr_files.getVisibility() == View.GONE) {
                    btn_clr_files.setVisibility(View.VISIBLE);
                }
                btn_sel_file.setVisibility(View.GONE);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            files_recycler.setLayoutManager(layoutManager);
            UploadedFilesAdapter fileAdapter = new UploadedFilesAdapter(this, filesel);
            files_recycler.setAdapter(fileAdapter);
            files_recycler.setItemAnimator(new DefaultItemAnimator());
        }
    }

    public void PickMedia() {
        if (formData != null) {
            int maxCount = formData.UploadImgCount();
            FilePickerBuilder fpick = FilePickerBuilder.Companion.getInstance();
            fpick.setMaxCount(maxCount);
            fpick.setSelectedFiles(photoPaths);
            fpick.setActivityTheme(R.style.LibAppTheme);
            fpick.sortDocumentsBy(SortingTypes.name);
            fpick.enableVideoPicker(false);
            fpick.showGifs(false);
            fpick.showFolderView(true);
            fpick.enableImagePicker(true);
            if (formData.CaptureImg() != 0) {
                if (isCameraPermissionGranted()) {
                    fpick.enableCameraSupport(true);
                    fpick.setCameraPlaceholder(R.drawable.ic_camera);
                    fpick.pickPhoto(this);
                }
            } else {
                if (isStorageReadPermissionGranted(REQUEST_READ_STORAGE_PP)) {
                    fpick.enableCameraSupport(false);
                    fpick.pickPhoto(this);
                }
            }
        }
    }

    public void PickDoc() {
        if (isStorageReadPermissionGranted(REQUEST_READ_STORAGE_FP) && formData != null) {
            String[] zips = {".zip", ".rar"};
            String[] pdfs = {".pdf"};
            int maxCount = formData.UploadFileCount();
            FilePickerBuilder fpick = FilePickerBuilder.Companion.getInstance();
            fpick.setMaxCount(maxCount);
            fpick.setSelectedFiles(docPaths);
            fpick.setActivityTheme(R.style.LibAppTheme);
            fpick.sortDocumentsBy(SortingTypes.name);
            fpick.addFileSupport("ZIP", zips);
            if (formData.UploadFileCount() != maxCount) {
                fpick.addFileSupport("PDF", pdfs);
                fpick.enableDocSupport(false);
            } else fpick.enableDocSupport(true);
            fpick.pickFile(this);
        }
    }

    private void ClearSelection() {
        photoPaths.clear();
        docPaths.clear();
        btn_chg_img.setVisibility(View.GONE);
        btn_chg_file.setVisibility(View.GONE);
        btn_clr_files.setVisibility(View.GONE);
        files_recycler.setVisibility(View.GONE);
        if (formData.UploadImg() != 0) btn_sel_img.setVisibility(View.VISIBLE);
        if (formData.UploadFile() != 0) btn_sel_file.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GPS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        if (!mGoogleApiClient.isConnected() && gps) {
                            mGoogleApiClient.connect();
                        }
                    } else {
                        session.showToast(getString(R.string.permission_not_granted), 1);
                    }
                } else {
                    session.showToast(getString(R.string.permission_not_granted), 1);
                }
                break;
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        PickMedia();
                    } else {
                        session.showToast(getString(R.string.permission_not_granted), 1);
                    }
                } else {
                    session.showToast(getString(R.string.permission_not_granted), 1);
                }
                break;
            case REQUEST_READ_STORAGE_PP:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // read storage related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        PickMedia();
                    } else {
                        session.showToast(getString(R.string.permission_not_granted), 1);
                    }
                } else {
                    session.showToast(getString(R.string.permission_not_granted), 1);
                }
                break;
            case REQUEST_READ_STORAGE_FP:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // read storage related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        PickDoc();
                    } else {
                        session.showToast(getString(R.string.permission_not_granted), 1);
                    }
                } else {
                    session.showToast(getString(R.string.permission_not_granted), 1);
                }
                break;
        }
    }

    private void GetFormLayout() {
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
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    acprogress.setVisible(false);
                                    loading_overlay.setVisibility(View.GONE);
                                    layout = resp.getLayoutInfo();
                                    formData = resp.getFormInfo();
                                    inputData = formData.getFormInputs();
                                    if (layout != null) PrepareLayout();
                                    if (formData != null) prepareForm();
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
                                    warning_overlay.setVisibility(View.VISIBLE);
                                    loading_overlay.setVisibility(View.GONE);
                                    acprogress.setVisible(false);
                                    String error = getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) error = resp.getMessage();
                                    showSnackbar(error, 2, 2);
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
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        String error = getString(R.string.error_failed_later);
                        showSnackbar(error, 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    showSnackbar(error, 2, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GetSpinnerMore(String fetch, final LinearLayout LL, int selected) {
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
        misc.setAdid(selected);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(reqtype);
        request.setSubType(fetch);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    acprogress.setVisible(false);
                                    if (resp.getFormInfo().getFormInputs().length > 0) {
                                        clearMore(LL);
                                        moreData.addAll(Arrays.asList(resp.getFormInfo().getFormInputs()));
                                    } else clearMore(LL);
                                    if (moreData != null) prepareMore(LL);
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
                                    String error = getString(R.string.error_failed_try_again);
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
                    String error = getString(R.string.internetcheck);
                    acprogress.setVisible(false);
                    session.showToast(error, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void PrepareLayout() {
        if (layout.disableActionBar() != 0) aBar.hide();
        else aBar.show();
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
            aBar.setDisplayHomeAsUpEnabled(true);
        } else {
            aBar.setDisplayHomeAsUpEnabled(false);
        }
        if (layout.showHome() != 0) {
            homeitem.setVisible(true);
        } else {
            homeitem.setVisible(false);
        }

        if (layout.showBaricon() != 0) {
            aBar.setHomeButtonEnabled(true);
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setIcon(R.mipmap.ic_launcher);
        } else {
            aBar.setHomeButtonEnabled(false);
            aBar.setDisplayShowHomeEnabled(false);
        }
        if (layout.allowBarTitle() != 0) {
            aBar.setDisplayShowTitleEnabled(true);
        } else {
            aBar.setDisplayShowTitleEnabled(false);
        }

        if (layout.getBarTitle() != null) {
            aBar.setTitle(layout.getBarTitle());
        }
    }

    @SuppressLint("SetTextI18n")
    private void prepareForm() {
        if (formData.getLocation_title() != null) {
            loc_title.setText(formData.getLocation_title());
        }
        if (formData.getFiles_head() != null) {
            files_add_head.setText(formData.getFiles_head());
        }

        if (formData.UploadImg() != 0) {
            if (formData.getUp_img_btn() != null) {
                btn_sel_img.setText(formData.getUp_img_btn());
            }
            btn_sel_img.setVisibility(View.VISIBLE);
            if (upfile_llayout.getVisibility() == View.GONE) {
                upfile_llayout.setVisibility(View.VISIBLE);
            }
        }

        if (formData.UploadFile() != 0) {
            if (formData.getUp_file_btn() != null) {
                btn_sel_file.setText(formData.getUp_file_btn());
            }
            btn_sel_file.setVisibility(View.VISIBLE);
            if (upfile_llayout.getVisibility() == View.GONE) {
                upfile_llayout.setVisibility(View.VISIBLE);
            }
        }

        if (formData.getFormhead() != null) {
            formhead.setText(formData.getFormhead());
        }

        if (formData.getFormprice() != null) {
            formprice.setVisibility(View.VISIBLE);
            formprice.setText(formData.getFormprice());
        }

        if (formData.getFormdesc() != null) {
            formdesc.setText(ITUtilities.fromHtml(formData.getFormdesc()));
            formdesc.setVisibility(View.VISIBLE);
        }

        if (formData.getForminst() != null) {
            forminst.setText(ITUtilities.fromHtml(formData.getForminst()));
            forminst.setVisibility(View.VISIBLE);
        }

        if (formData.getPreBottomNotes() != null) {
            tv_pre_bottom_notes.setText(ITUtilities.fromHtml(formData.getPreBottomNotes()));
            tv_pre_bottom_notes.setVisibility(View.VISIBLE);
        }

        if (formData.getBottomNotes() != null) {
            tv_bottom_notes.setText(ITUtilities.fromHtml(formData.getBottomNotes()));
            tv_bottom_notes.setVisibility(View.VISIBLE);
        }

        if (formData.getFormimage() != null && !formData.getFormimage().isEmpty()) {
            img_frame.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            int resizeDPW = ITUtilities.DpToPx(this.getApplicationContext(), 500f);
            int resizeDPH = ITUtilities.DpToPx(this.getApplicationContext(), 150f);
            session.imgLoader()
                    .load(formData.getFormimage())
                    .error(R.drawable.ic_error)
                    .resize(resizeDPW, resizeDPH)
                    .centerCrop()
                    .into(imv_img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        if (formData.getFormbutton() != null) {
            post_form_btn.setText(formData.getFormbutton());
        }

        if (formData.getFormInputs().length != 0) {
            LinearLayout linearLayout = null;
            FormInputData[] fdata = formData.getFormInputs();
            for (final FormInputData sf_data : fdata) {
                LinearLayout pos = null;
                switch (sf_data.getPlace()) {
                    case 1:
                        pos = llTop;
                        break;
                    case 2:
                        pos = llinputs;
                        break;
                    case 3:
                        pos = llbottom;
                        break;
                    case 4:
                        pos = llend;
                        break;
                }
                if (sf_data.getTitle() != null) {
                    TextView tv = new TextView(this);
                    if (sf_data.getIcon() != 0) {
                        Drawable img;
                        switch (sf_data.getIcon()) {
                            case 1:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_person);
                                break;
                            case 2:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_desc);
                                break;
                            case 3:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_money);
                                break;
                            case 4:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_land_phone);
                                break;
                            case 5:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_phone);
                                break;
                            case 6:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_link);
                                break;
                            case 7:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_address);
                                break;
                            case 8:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_email_colored);
                                break;
                            case 9:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_username);
                                break;
                            case 10:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_password);
                                break;
                            case 11:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_date_colored);
                                break;
                            case 12:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_time);
                                break;
                            case 13:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_facebook_colored);
                                break;
                            case 14:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_instagram);
                                break;
                            case 15:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_delivery_colored);
                                break;
                            case 16:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_fav_colored);
                                break;
                            case 17:
                                img = null;
                                break;
                            default:
                                img = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                        }
                        if (img != null) {
                            img.setBounds(0, 0, 45, 45);
                            if (session.pref().GetLang().equals("ar")) {
                                tv.setCompoundDrawables(null, null, img, null);
                            } else {
                                tv.setCompoundDrawables(img, null, null, null);
                            }
                        }
                    }
                    String required = "";
                    if (sf_data.isRequired() != 0) {
                        required = "* ";
                    }
                    tv.setText(required + sf_data.getTitle());
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (sf_data.getType() == 7) {
                        params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                    }
                    if (sf_data.getIcon() != 0) {
                        params.gravity = Gravity.CENTER_VERTICAL;
                    } else {
                        float marginsDp = 5f;
                        // Convert to pixels
                        int marginsPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginsDp, getResources().getDisplayMetrics());

                        params.setMargins(0, 0, 0, marginsPx);
                    }
                    tv.setLayoutParams(params);
                    if (sf_data.getType() == 7) {
                        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        linearLayout = new LinearLayout(this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setLayoutParams(llParams);
                        linearLayout.addView(tv);
                    } else {
                        if (pos != null) {
                            pos.addView(tv);
                        }
                    }
                }
                switch (sf_data.getType()) {
                    case 1:
                        final TextInputEditText ettext = new TextInputEditText(this);
                        ettext.setId(sf_data.getId());
                        if (sf_data.getHeight() != 0) {
                            ettext.setHeight(sf_data.getHeight());
                            ettext.setGravity(Gravity.TOP);
                        }
                        if (sf_data.getHint() != null) {
                            ettext.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            ettext.setText(sf_data.getText());
                        }
                        switch (sf_data.getEtType()) {
                            case 1:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 2:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                if (sf_data.getHeight() == 0) {
                                    ettext.setHeight(150);
                                    ettext.setGravity(Gravity.TOP);
                                }
                                break;
                            case 3:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case 4:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                break;
                            case 5:
                                ettext.setInputType(InputType.TYPE_CLASS_PHONE);
                                break;
                            case 6:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                                break;
                            case 7:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                                break;
                            case 8:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                break;
                            case 9:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                break;
                            case 10:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                break;
                            default:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        if (sf_data.allowMin() != 0) {
                            if (sf_data.MinimumPay() != 0f && sf_data.MaximumPay() != 0f) {
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        String payamount = "";
                                        if (ettext.getText() != null)
                                            payamount = ettext.getText().toString();
                                        if (!payamount.isEmpty()) {
                                            Float paid = Float.valueOf(payamount);
                                            Float Minpay = sf_data.MinimumPay();
                                            Float Maxpay = sf_data.MaximumPay();
                                            if (paid < Minpay || paid > Maxpay) {
                                                if (paid > Maxpay) {
                                                    ettext.setText(String.valueOf(Maxpay));
                                                } else {
                                                    ettext.setText(String.valueOf(Minpay));
                                                }
                                            }
                                        }
                                    }
                                };
                                ettext.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before,
                                                              int count) {

                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                  int after) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        handler.removeCallbacks(runnable);
                                        handler.postDelayed(runnable, 2000);

                                    }
                                });
                            }
                        }

                        if (sf_data.Disabled() != 0) {
                            ettext.setEnabled(false);
                        }
                        if (pos != null) {
                            pos.addView(ettext);
                        }
                        break;
                    case 2:
                        final Spinner spin = new Spinner(this);
                        final int sid = sf_data.getId();
                        spin.setId(sid);
                        ArrayList<SpinnerData> spinnerList = new ArrayList<>();
                        SpinnerData sdata[] = sf_data.getSpinnerData();
                        for (int s = 0; s < sf_data.getSpinnerData().length; s++) {
                            SpinnerData sd = sdata[s];
                            spinnerList.add(new SpinnerData(sd.getId(), sd.getName()));
                        }
                        //fill data in spinner
                        ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
                        spin.setAdapter(adapter);
                        final LinearLayout finalPos = pos;
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                SpinnerData spinner = (SpinnerData) parent.getSelectedItem();
                                String search = sf_data.getName();
                                int searchListLength = spinnersel.size();
                                int rc = 0;
                                for (int i = 0; i < searchListLength; i++) {
                                    if (spinnersel.get(i).getStag().contains(search)) {
                                        rc++;
                                        spinnersel.remove(i);
                                        spinnersel.add(new SelectedSpinnerData(sid, spinner.getId(), sf_data.getName()));
                                    }
                                }
                                if (rc == 0) {
                                    spinnersel.add(new SelectedSpinnerData(sid, spinner.getId(), sf_data.getName()));
                                }
                                if (sf_data.getMore() != null) {
                                    if (finalPos != null) {
                                        Spinner s = finalPos.findViewById(sid);
                                        if (s != null) {
                                            GetSpinnerMore(sf_data.getMore(), finalPos, spinner.getId());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        if (sf_data.Disabled() != 0) {
                            spin.setEnabled(false);
                        }
                        if (pos != null) {
                            pos.addView(spin);
                        }
                        break;
                    case 3:
                        RecyclerView expandrecycler = new RecyclerView(this);
                        final int rcid = sf_data.getId();
                        expandrecycler.setId(rcid);
                        ArrayList<ExpandItemsData> expanditems = new ArrayList<>();
                        if (sf_data.getExpanditems().length > 0) {
                            expanditems.addAll(Arrays.asList(sf_data.getExpanditems()));
                        }
                        LinearLayoutManager linerlayout = new LinearLayoutManager(this);
                        ExpandAdapter = new ExpandItemsAdapter(this, expanditems);
                        expandrecycler.setLayoutManager(linerlayout);
                        expandrecycler.setAdapter(ExpandAdapter);
                        if (pos != null) {
                            pos.addView(expandrecycler);
                        }
                        break;
                    case 4:
                        DateDisplayPicker date_input = new DateDisplayPicker(this);
                        date_input.setId(sf_data.getId());
                        if (sf_data.getHint() != null) {
                            date_input.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            date_input.setText(sf_data.getText());
                        }
                        if (sf_data.Disabled() != 0) {
                            date_input.setEnabled(false);
                        }
                        if (pos != null) {
                            pos.addView(date_input);
                        }
                        break;
                    case 5:
                        TimeDisplayPicker time_input = new TimeDisplayPicker(this);
                        time_input.setId(sf_data.getId());
                        if (sf_data.getHint() != null) {
                            time_input.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            time_input.setText(sf_data.getText());
                        }
                        if (sf_data.Disabled() != 0) {
                            time_input.setEnabled(false);
                        }
                        if (pos != null) {
                            pos.addView(time_input);
                        }
                        break;
                    case 6:
                        RatingBar rating = new RatingBar(new ContextThemeWrapper(this, R.style.RateStyle));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;
                        rating.setLayoutParams(params);
                        rating.setId(sf_data.getId());
                        if (sf_data.getHeight() != 0) {
                            rating.setMax(sf_data.getHeight());
                            rating.setNumStars(sf_data.getHeight());
                        }
                        if (sf_data.getText() != null) {
                            rating.setRating(Float.parseFloat(sf_data.getText()));
                        }
                        if (sf_data.Disabled() != 0) {
                            rating.setIsIndicator(true);
                        }
                        if (pos != null) {
                            pos.addView(rating);
                        }
                        break;
                    case 7:
                        CheckBox chk = new CheckBox(new ContextThemeWrapper(this, R.style.CheckBoxStyle));
                        chk.setId(sf_data.getId());
                        LinearLayout.LayoutParams chkParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        chk.setLayoutParams(chkParams);
                        if (sf_data.Disabled() == 1) {
                            chk.setEnabled(false);
                        }
                        if (sf_data.getEtType() == 1) {
                            chk.setChecked(true);
                        }
                        if (linearLayout != null) {
                            linearLayout.addView(chk);
                            if (pos != null) {
                                pos.addView(linearLayout);
                            }
                        }
                }
                View v = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3
                );
                float marginsDp = 5f;
                // Convert to pixels
                int marginsPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginsDp, getResources().getDisplayMetrics());
                params.setMargins(0, marginsPx, 0, marginsPx);
                v.setLayoutParams(params);
                v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                if (pos != null) pos.addView(v);
            }
            int paddingDp = 10;
            float density = getResources().getDisplayMetrics().density;
            int paddingPixel = (int) (paddingDp * density);
            if (llTop.getChildCount() > 0) {
                llTop.setBackground(ContextCompat.getDrawable(ActivityForm.this, R.drawable.border));
                llTop.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            } else llTop.setVisibility(View.GONE);
            if (llinputs.getChildCount() > 0) {
                llinputs.setBackground(ContextCompat.getDrawable(ActivityForm.this, R.drawable.border));
                llinputs.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            } else llinputs.setVisibility(View.GONE);
            if (llbottom.getChildCount() > 0) {
                llbottom.setBackground(ContextCompat.getDrawable(ActivityForm.this, R.drawable.border));
                llbottom.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            } else llbottom.setVisibility(View.GONE);
            if (llend.getChildCount() > 0) {
                llend.setBackground(ContextCompat.getDrawable(ActivityForm.this, R.drawable.border));
                llend.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            } else llend.setVisibility(View.GONE);
        }

        if (formData.AllowLocation() != 0) {
            if (formData.AllowLocation() == 1) {
                gpsState(true);
            } else if (formData.AllowLocation() == 3) {
                gpsState(true);
                loc_layout.setVisibility(View.VISIBLE);
                chk_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //is check box checked?
                        if (((CheckBox) v).isChecked()) gpsState(true);
                        else gpsState(false);
                    }
                });
            } else {
                loc_layout.setVisibility(View.VISIBLE);
                chk_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //is check box checked?
                        if (((CheckBox) v).isChecked()) {
                            gpsState(true);
                        } else {
                            gpsState(false);
                        }
                    }
                });
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void prepareMore(LinearLayout LL) {
        if (moreData != null && moreData.size() != 0) {
            LinearLayout linearLayout = null;
            ArrayList<FormInputData> fdata = moreData;
            for (final FormInputData sf_data : fdata) {
                if (sf_data.getTitle() != null) {
                    TextView tv = new TextView(this);
                    String tvSId = sf_data.getId() + "25";
                    int tvID = Integer.parseInt(tvSId);
                    tv.setId(tvID);
                    if (sf_data.getIcon() != 0) {
                        Drawable img;
                        switch (sf_data.getIcon()) {
                            case 1:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_person);
                                break;
                            case 2:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_desc);
                                break;
                            case 3:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_money);
                                break;
                            case 4:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_land_phone);
                                break;
                            case 5:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_phone);
                                break;
                            case 6:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_link);
                                break;
                            case 7:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_address);
                                break;
                            case 8:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_email_colored);
                                break;
                            case 9:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_username);
                                break;
                            case 10:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_password);
                                break;
                            case 11:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_date_colored);
                                break;
                            case 12:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_time);
                                break;
                            case 13:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_facebook_colored);
                                break;
                            case 14:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_instagram);
                                break;
                            case 15:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_delivery_colored);
                                break;
                            case 16:
                                img = ContextCompat.getDrawable(this, R.drawable.ic_fav_colored);
                                break;
                            case 17:
                                img = null;
                                break;
                            default:
                                img = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                        }
                        if (img != null) {
                            img.setBounds(0, 0, 45, 45);
                            if (session.pref().GetLang().equals("ar"))
                                tv.setCompoundDrawables(null, null, img, null);
                            else tv.setCompoundDrawables(img, null, null, null);
                        }
                    }
                    String required = "";
                    if (sf_data.isRequired() != 0) required = "* ";
                    tv.setText(required + sf_data.getTitle());
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (sf_data.getType() == 7) {
                        params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                    }
                    if (sf_data.getIcon() != 0) {
                        params.gravity = Gravity.CENTER_VERTICAL;
                    } else {
                        float marginsDp = 5f;
                        // Convert to pixels
                        int marginsPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginsDp, getResources().getDisplayMetrics());

                        params.setMargins(0, 0, 0, marginsPx);
                    }
                    tv.setLayoutParams(params);
                    if (sf_data.getType() == 7) {
                        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        linearLayout = new LinearLayout(this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setLayoutParams(llParams);
                        linearLayout.addView(tv);
                    } else {
                        if (LL != null) {
                            LL.addView(tv);
                        }
                    }
                }
                switch (sf_data.getType()) {
                    case 1:
                        final TextInputEditText ettext = new TextInputEditText(this);
                        ettext.setId(sf_data.getId());
                        if (sf_data.getHeight() != 0) {
                            ettext.setHeight(sf_data.getHeight());
                            ettext.setGravity(Gravity.TOP);
                        }
                        if (sf_data.getHint() != null) {
                            ettext.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            ettext.setText(sf_data.getText());
                        }
                        switch (sf_data.getEtType()) {
                            case 1:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            case 2:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                if (sf_data.getHeight() == 0) {
                                    ettext.setHeight(150);
                                    ettext.setGravity(Gravity.TOP);
                                }
                                break;
                            case 3:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case 4:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                break;
                            case 5:
                                ettext.setInputType(InputType.TYPE_CLASS_PHONE);
                                break;
                            case 6:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                                break;
                            case 7:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                                break;
                            case 8:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                break;
                            case 9:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                break;
                            case 10:
                                ettext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                break;
                            default:
                                ettext.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        if (sf_data.allowMin() != 0) {
                            if (sf_data.MinimumPay() != 0f && sf_data.MaximumPay() != 0f) {
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        String payamount = "";
                                        if (ettext.getText() != null)
                                            payamount = ettext.getText().toString();
                                        if (!payamount.isEmpty()) {
                                            Float paid = Float.valueOf(payamount);
                                            Float Minpay = sf_data.MinimumPay();
                                            Float Maxpay = sf_data.MaximumPay();
                                            if (paid < Minpay || paid > Maxpay) {
                                                if (paid > Maxpay) {
                                                    ettext.setText(String.valueOf(Maxpay));
                                                } else {
                                                    ettext.setText(String.valueOf(Minpay));
                                                }
                                            }
                                        }
                                    }
                                };
                                ettext.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before,
                                                              int count) {

                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                  int after) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        handler.removeCallbacks(runnable);
                                        handler.postDelayed(runnable, 2000);

                                    }
                                });
                            }
                        }
                        if (sf_data.Disabled() != 0) ettext.setEnabled(false);
                        if (LL != null) LL.addView(ettext);
                        break;
                    case 2:
                        final Spinner spin = new Spinner(this);
                        final int sid = sf_data.getId();
                        spin.setId(sid);
                        ArrayList<SpinnerData> spinnerList = new ArrayList<>();
                        SpinnerData sdata[] = sf_data.getSpinnerData();
                        for (int s = 0; s < sf_data.getSpinnerData().length; s++) {
                            SpinnerData sd = sdata[s];
                            spinnerList.add(new SpinnerData(sd.getId(), sd.getName()));
                        }
                        //fill data in spinner
                        ArrayAdapter<SpinnerData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
                        spin.setAdapter(adapter);
                        final LinearLayout finalPos = LL;
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                SpinnerData spinner = (SpinnerData) parent.getSelectedItem();
                                String search = sf_data.getName();
                                int searchListLength = spinnersel.size();
                                int rc = 0;
                                for (int i = 0; i < searchListLength; i++) {
                                    if (spinnersel.get(i).getStag().contains(search)) {
                                        rc++;
                                        spinnersel.remove(i);
                                        spinnersel.add(new SelectedSpinnerData(sid, spinner.getId(), sf_data.getName()));
                                    }
                                }
                                if (rc == 0) {
                                    spinnersel.add(new SelectedSpinnerData(sid, spinner.getId(), sf_data.getName()));
                                }
                                if (sf_data.getMore() != null) {
                                    if (finalPos != null) {
                                        Spinner s = finalPos.findViewById(sid);
                                        if (s != null)
                                            GetSpinnerMore(sf_data.getMore(), finalPos, spinner.getId());
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        if (sf_data.Disabled() != 0) {
                            spin.setEnabled(false);
                        }
                        if (LL != null) {
                            LL.addView(spin);
                        }
                        break;
                    case 3:
                        RecyclerView expandrecycler = new RecyclerView(this);
                        final int rcid = sf_data.getId();
                        expandrecycler.setId(rcid);
                        ArrayList<ExpandItemsData> expanditems = new ArrayList<>();
                        if (sf_data.getExpanditems().length > 0) {
                            expanditems.addAll(Arrays.asList(sf_data.getExpanditems()));
                        }
                        LinearLayoutManager linerlayout = new LinearLayoutManager(this);
                        ExpandAdapter = new ExpandItemsAdapter(this, expanditems);
                        expandrecycler.setLayoutManager(linerlayout);
                        expandrecycler.setAdapter(ExpandAdapter);
                        if (LL != null) {
                            LL.addView(expandrecycler);
                        }
                        break;
                    case 4:
                        DateDisplayPicker date_input = new DateDisplayPicker(this);
                        date_input.setId(sf_data.getId());
                        if (sf_data.getHint() != null) {
                            date_input.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            date_input.setText(sf_data.getText());
                        }
                        if (sf_data.Disabled() != 0) {
                            date_input.setEnabled(false);
                        }
                        if (LL != null) {
                            LL.addView(date_input);
                        }
                        break;
                    case 5:
                        TimeDisplayPicker time_input = new TimeDisplayPicker(this);
                        time_input.setId(sf_data.getId());
                        if (sf_data.getHint() != null) {
                            time_input.setHint(sf_data.getHint());
                        }
                        if (sf_data.getText() != null) {
                            time_input.setText(sf_data.getText());
                        }
                        if (sf_data.Disabled() != 0) {
                            time_input.setEnabled(false);
                        }
                        if (LL != null) {
                            LL.addView(time_input);
                        }
                        break;
                    case 6:
                        RatingBar rating = new RatingBar(new ContextThemeWrapper(this, R.style.RateStyle));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;
                        rating.setLayoutParams(params);
                        rating.setId(sf_data.getId());
                        if (sf_data.getHeight() != 0) {
                            rating.setMax(sf_data.getHeight());
                            rating.setNumStars(sf_data.getHeight());
                        }
                        if (sf_data.getText() != null) {
                            rating.setRating(Float.parseFloat(sf_data.getText()));
                        }
                        if (sf_data.Disabled() != 0) {
                            rating.setIsIndicator(true);
                        }
                        if (LL != null) {
                            LL.addView(rating);
                        }
                        break;
                    case 7:
                        CheckBox chk = new CheckBox(new ContextThemeWrapper(this, R.style.CheckBoxStyle));
                        chk.setId(sf_data.getId());
                        LinearLayout.LayoutParams chkParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        chk.setLayoutParams(chkParams);
                        if (sf_data.Disabled() == 1) {
                            chk.setEnabled(false);
                        }
                        if (sf_data.getEtType() == 1) {
                            chk.setChecked(true);
                        }
                        if (linearLayout != null) {
                            linearLayout.addView(chk);
                            if (LL != null) {
                                LL.addView(linearLayout);
                            }
                        }
                }
                View v = new View(this);
                String vSId = sf_data.getId() + "28";
                int vID = Integer.parseInt(vSId);
                v.setId(vID);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3
                );
                float marginsDp = 5f;
                // Convert to pixels
                int marginsPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginsDp, getResources().getDisplayMetrics());
                params.setMargins(0, marginsPx, 0, marginsPx);
                v.setLayoutParams(params);
                v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                if (LL != null) {
                    LL.addView(v);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void clearMore(LinearLayout LL) {
        if (moreData != null && moreData.size() != 0 && LL != null) {
            ArrayList<FormInputData> fdata = moreData;
            for (final FormInputData sf_data : fdata) {
                if (sf_data.getTitle() != null) {
                    String tvSId = sf_data.getId() + "25";
                    int tvID = Integer.parseInt(tvSId);
                    TextView tv = LL.findViewById(tvID);
                    if (tv != null) {
                        LL.removeView(tv);
                    }
                }
                switch (sf_data.getType()) {
                    case 1:
                        TextInputEditText ettext = LL.findViewById(sf_data.getId());
                        if (ettext != null) {
                            LL.removeView(ettext);
                        }
                        break;
                    case 2:
                        Spinner spin = LL.findViewById(sf_data.getId());
                        String search = sf_data.getName();
                        int searchListLength = spinnersel.size() - 1;
                        for (int i = 0; i < searchListLength; i++) {
                            if (spinnersel.get(i).getStag().contains(search)) {
                                spinnersel.remove(i);
                            }
                        }

                        if (spin != null) {
                            LL.removeView(spin);
                        }
                        break;
                    case 3:
                        RecyclerView expandrecycler = LL.findViewById(sf_data.getId());
                        if (expandrecycler != null) {
                            LL.removeView(expandrecycler);
                        }
                        break;
                    case 4:
                        DateDisplayPicker date_input = LL.findViewById(sf_data.getId());
                        if (date_input != null) {
                            LL.removeView(date_input);
                        }
                        break;
                    case 5:
                        TimeDisplayPicker time_input = LL.findViewById(sf_data.getId());
                        if (time_input != null) {
                            LL.removeView(time_input);
                        }
                        break;
                    case 6:
                        RatingBar rating = LL.findViewById(sf_data.getId());
                        if (rating != null) {
                            LL.removeView(rating);
                        }
                        break;
                    case 7:
                        CheckBox chk = LL.findViewById(sf_data.getId());
                        if (chk != null) {
                            LL.removeView(chk);
                        }
                        break;
                }
                String vSId = sf_data.getId() + "28";
                int vID = Integer.parseInt(vSId);
                View v = LL.findViewById(vID);
                if (v != null) {
                    LL.removeView(v);
                }
            }
            moreData.clear();
        }
    }

    private void PostItem() {
        try {
            postform = true;
            session.hideKeyboard(this.getCurrentFocus());
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, RequestBody> map = new HashMap<>();
        map.put("item_post", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(1111)));
        if (session.pref().IsUserlogged()) {
            map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().GetUserid())));
            map.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), session.pref().GetUseruname()));
            map.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), session.pref().GetUserpass()));
        }
        map.put("langcode", RequestBody.create(MediaType.parse("multipart/form-data"), session.pref().GetLang()));
        if (session.pref().getRole() != null && !session.pref().getRole().isEmpty()) {
            map.put("role", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().getRole())));
        }
        if (session.pref().getLatitude() != 0) {
            map.put("user_lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().getLatitude())));
        }
        if (session.pref().getLongitude() != 0) {
            map.put("user_lon", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().getLongitude())));
        }
        if (session.pref().getCity() != 0) {
            map.put("user_city", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().getCity())));
        }
        if (session.pref().getCityGroup() != 0) {
            map.put("user_city_group", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(session.pref().getCityGroup())));
        }
        if (reqtype != null) {
            map.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), reqtype));
        }
        if (reqstype != null) {
            map.put("subtype", RequestBody.create(MediaType.parse("multipart/form-data"), reqstype));
        }
        if (reqptype != null) {
            map.put("posttype", RequestBody.create(MediaType.parse("multipart/form-data"), reqptype));
        }
        if (reqatype != null) {
            map.put("atype", RequestBody.create(MediaType.parse("multipart/form-data"), reqatype));
        }
        if (reqid != 0) {
            map.put("reqid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(reqid)));
        }
        if (reqmid != 0) {
            map.put("reqmid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(reqmid)));
        }
        if (reqsid != 0) {
            map.put("reqsid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(reqsid)));
        }
        if (reqaid != 0) {
            map.put("reqaid", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(reqaid)));
        }

        if (formData.AllowLocation() != 0) {
            if (mLocation != null) {
                map.put("latitude", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mLocation.getLatitude())));
                map.put("longitude", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mLocation.getLongitude())));
            }
        }

        int req = 0;
        for (FormInputData fdata : inputData) {
            switch (fdata.getType()) {
                case 1:
                    int etid = fdata.getId();
                    TextInputEditText et = findViewById(etid);
                    String etv = "";
                    if (et.getText() != null) etv = et.getText().toString();
                    if (fdata.isRequired() != 0 && etv.isEmpty()) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), etv));
                    break;
                case 2:
                    int sid = fdata.getId();
                    for (int sl = 0; sl < spinnersel.size(); sl++) {
                        SelectedSpinnerData slsid = spinnersel.get(sl);
                        int slid = slsid.getSpid();
                        if (slid == sid) {
                            map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), String.format(Locale.ENGLISH, "%d", slsid.getSelid())));
                        }
                    }
                    break;
                case 3:
                    int radioid = ExpandAdapter.getSelectedRadio();
                    if (fdata.isRequired() != 0 && radioid == 0) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), String.format(Locale.ENGLISH, "%d", radioid)));
                    break;
                case 4:
                    int dateId = fdata.getId();
                    DateDisplayPicker dt = findViewById(dateId);
                    String sdt = dt.getText().toString();
                    if (fdata.isRequired() != 0 && sdt.isEmpty()) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), sdt));
                    break;
                case 5:
                    int timeId = fdata.getId();
                    TimeDisplayPicker tm = findViewById(timeId);
                    String stm = tm.getText().toString();
                    if (fdata.isRequired() != 0 && stm.isEmpty()) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), stm));
                    break;
                case 6:
                    int rateId = fdata.getId();
                    RatingBar rt = findViewById(rateId);
                    String rdt = String.format(Locale.ENGLISH, "%f", rt.getRating());
                    if (fdata.isRequired() != 0 && rdt.isEmpty()) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), rdt));
                    break;
                case 7:
                    int chkId = fdata.getId();
                    CheckBox chk = findViewById(chkId);
                    int val = chk.isChecked() ? 1 : 0;
                    String chkVal = String.format(Locale.ENGLISH, "%d", val);
                    if (fdata.isRequired() != 0 && val == 0) {
                        req++;
                    }
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), chkVal));
                    break;
                case 8:
                    map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fdata.getText()));
                    break;
            }

        }

        if (moreData != null && moreData.size() > 0) {
            for (FormInputData fdata : moreData) {
                switch (fdata.getType()) {
                    case 1:
                        int etid = fdata.getId();
                        TextInputEditText et = findViewById(etid);
                        String etv = "";
                        if (et.getText() != null) etv = et.getText().toString();
                        if (fdata.isRequired() != 0 && etv.isEmpty()) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), etv));
                        break;
                    case 2:
                        int sid = fdata.getId();
                        for (int sl = 0; sl < spinnersel.size(); sl++) {
                            SelectedSpinnerData slsid = spinnersel.get(sl);
                            int slid = slsid.getSpid();
                            if (slid == sid) {
                                map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), String.format(Locale.ENGLISH, "%d", slsid.getSelid())));
                            }
                        }
                        break;
                    case 3:
                        int radioid = ExpandAdapter.getSelectedRadio();
                        if (fdata.isRequired() != 0 && radioid == 0) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), String.format(Locale.ENGLISH, "%d", radioid)));
                        break;
                    case 4:
                        int dateId = fdata.getId();
                        DateDisplayPicker dt = findViewById(dateId);
                        String sdt = dt.getText().toString();
                        if (fdata.isRequired() != 0 && sdt.isEmpty()) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), sdt));
                        break;
                    case 5:
                        int timeId = fdata.getId();
                        TimeDisplayPicker tm = findViewById(timeId);
                        String stm = tm.getText().toString();
                        if (fdata.isRequired() != 0 && stm.isEmpty()) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), stm));
                        break;
                    case 6:
                        int rateId = fdata.getId();
                        RatingBar rt = findViewById(rateId);
                        String rdt = String.format(Locale.ENGLISH, "%f", rt.getRating());
                        if (fdata.isRequired() != 0 && rdt.isEmpty()) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), rdt));
                        break;
                    case 7:
                        int chkId = fdata.getId();
                        CheckBox chk = findViewById(chkId);
                        int val = chk.isChecked() ? 1 : 0;
                        String chkVal = String.format(Locale.ENGLISH, "%d", val);
                        if (fdata.isRequired() != 0 && val == 0) {
                            req++;
                        }
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), chkVal));
                        break;
                    case 8:
                        map.put(fdata.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fdata.getText()));
                        break;
                }
            }
        }

        if (docPaths != null) {
            for (String result : docPaths) {
                File file = new File(result);
                RequestBody fb = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                map.put("files[]\"; filename=\"" + String.format(Locale.ENGLISH, "%s", file.getName()), fb);
            }
        }

        if (photoPaths != null) {
            for (String result : photoPaths) {
                File file = new File(result);
                RequestBody fb = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                map.put("photos[]\"; filename=\"" + String.format(Locale.ENGLISH, "%s", file.getName()), fb);
            }
        }

        if (formData.UploadFile() != 0 && formData.UploadFile() == 2 && (docPaths == null || docPaths.size() == 0)) {
            try {
                String msg = getString(R.string.req_files);
                if (formData.getFileMsg() != null && !formData.getFileMsg().isEmpty())
                    msg = formData.getFileMsg();
                showSnackbar(msg, 0, 1);
                postform = false;
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (formData.UploadImg() != 0 && formData.UploadImg() == 2 && (photoPaths == null || photoPaths.size() == 0)) {
            try {
                String msg = getString(R.string.req_photos);
                if (formData.getPhotoMsg() != null && !formData.getPhotoMsg().isEmpty())
                    msg = formData.getPhotoMsg();
                showSnackbar(msg, 0, 1);
                postform = false;
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (req != 0) {
            try {
                String msg = getString(R.string.req_fields);
                if (formData.getReqMsg() != null && !formData.getReqMsg().isEmpty())
                    msg = formData.getReqMsg();
                showSnackbar(msg, 0, 1);
                postform = false;
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (formData.AllowLocation() != 0 && (formData.AllowLocation() == 3 || formData.AllowLocation() == 1)) {
            if (mLocation == null) {
                try {
                    String msg = getString(R.string.req_location);
                    if (formData.getLocMsg() != null && !formData.getLocMsg().isEmpty())
                        msg = formData.getLocMsg();
                    showSnackbar(msg, 0, 1);
                    postform = false;
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        Call<ServerResponse> resultCall = session.api().uploadImage(map, null);

        resultCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final ServerResponse resp = response.body();
                // Response Success or Fail
                if (response.isSuccessful()) {
                    if (resp != null) {
                        if (resp.getError() != null && resp.getError()) {
                            try {
                                postform = false;
                                String error = getResources().getString(R.string.error_form_not_submited);
                                if (resp.getMessage() != null) error = resp.getMessage();
                                showSnackbar(error, 1, 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                final Activity activity = ActivityForm.this;
                                String msg = getResources().getString(R.string.success_done);
                                if (resp.getMessage() != null) {
                                    if (resp.getMessage().equals(Constants.UPDATE_USER)) {
                                        session.UpdateUserInfo();
                                    } else msg = resp.getMessage();
                                }
                                if (resp.getSuccessAction() == 0) {
                                    session.showToast(msg, 1);
                                    finish();
                                } else if (resp.getSuccessAction() == 1) session.goHome();
                                else if (resp.getSuccessAction() == 2 && resp.getIntent() != null) {
                                    session.NewActivity(resp.getIntent(), 0);
                                    finish();
                                } else if (resp.getSuccessAction() == 3) {
                                    CallbackDialog callbackDialog = new CallbackDialog() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                            if (resp.getIntent() != null) {
                                                session.NewActivity(resp.getIntent(), 0);
                                                activity.finish();
                                            } else activity.finish();
                                        }

                                        @Override
                                        public void onNegativeClick(Dialog dialog) {

                                        }
                                    };
                                    Tools.showDialogCustom(activity, msg, callbackDialog);
                                } else finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        postform = false;
                        session.showToast(getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progressDialog.dismiss();
                    postform = false;
                    session.showToast(getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hideMap(boolean state) {
        if (gps && mMap != null) {
            if (state) {
                if (maps_layout.getVisibility() == View.VISIBLE) {
                    maps_layout.setVisibility(View.GONE);
                }
            } else {
                if (maps_layout.getVisibility() == View.GONE) {
                    maps_layout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clr_files:
                ClearSelection();
                break;
            case R.id.img_sel_btn:
            case R.id.btn_chng_img:
                PickMedia();
                break;
            case R.id.files_sel_btn:
            case R.id.btn_chng_files:
                PickDoc();
                break;
            case R.id.post_form_btn:
                PostItem();
        }
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
        Snackbar snackbar = Snackbar.make(main_layout, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (action == 1) {
                        PostItem();
                    } else {
                        GetFormLayout();
                    }
                }
            });
            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}