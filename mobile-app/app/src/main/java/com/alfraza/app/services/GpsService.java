package com.alfraza.app.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class GpsService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Session session;
    private int trackinginterval = 10;
    private boolean currentlyProcessingLocation = false;
    private GoogleApiClient googleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NotificationCreator.getNotificationId(),
                    NotificationCreator.getNotification(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().contains("start")) {
            session = new Session(getApplicationContext());
            trackinginterval = session.pref().Trackinginterval();
            if (session.pref().IsUserlogged()) {
                if (session.pref().iscurrentlyTracking()) {
                    // if we are currently trying to get a location and the alarm manager has called this again,
                    // no need to start processing a new location.
                    if (!currentlyProcessingLocation) {
                        currentlyProcessingLocation = true;
                        startTracking();
                    }
                } else {
                    stopLocationUpdates();
                    stopSelf();
                }
            } else {
                stopLocationUpdates();
                stopSelf();
            }
        } else {
            stopLocationUpdates();
            stopSelf();
        }

        return START_STICKY;
    }


    private void startTracking() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting())
                googleApiClient.connect();

        }
    }

    protected void sendLocationDataToWebsite(Location location) {
        float totalDistanceInMeters = session.pref().getTotalDistance();

        boolean firstTimeGettingPosition = session.pref().isfirstTimeGettingPosition();

        if (firstTimeGettingPosition)
            session.pref().firstTimeGettingPosition(false);
        else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(session.pref().getpreviousLatitude());
            previousLocation.setLongitude(session.pref().getpreviousLongitude());

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            session.pref().setTotalDistance(distance);
        }

        session.pref().setpreviousLatitude((float) location.getLatitude());
        session.pref().setpreviousLongitude((float) location.getLongitude());

        session.pref().setLatitude(location.getLatitude());
        session.pref().setLongitude(location.getLongitude());

        session.pref().setpreviousLongitude((float) location.getLongitude());

        Double speedInMilesPerHour = location.getSpeed() * 2.2369;
        Double accuracyInFeet = location.getAccuracy() * 3.28;
        Double altitudeInFeet = location.getAltitude() * 3.28;
        Float direction = location.getBearing();

        Tracker tracker = new Tracker();
        tracker.setLatitude(location.getLatitude());
        tracker.setLongitude(location.getLongitude());
        if (totalDistanceInMeters > 0)
            tracker.setDistance(String.format(Locale.ENGLISH, "%.1f", totalDistanceInMeters / 1609)); // in miles,
        else tracker.setDistance("0.0"); // in miles

        tracker.setSpeed(speedInMilesPerHour);
        tracker.setMethod(location.getProvider());
        tracker.setAccuracy(accuracyInFeet);
        tracker.setAltitude(altitudeInFeet);
        tracker.setDirection(direction);
        tracker.setEventtype("android");
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.TRACKER_OPERATION);
        request.setUser(session.getUserInfo());
        request.setTracker(tracker);
        Call<ServerResponse> response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.LOGOUT:
                                session.pref().clearNotfPref();
                                session.pref().clearUserPref();
                                session.pref().clearGpsPref();
                                session.pref().clearSettingsPref();
                                stopSelf();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (session.pref().IsUserlogged()) {
                if (session.pref().iscurrentlyTracking()) sendLocationDataToWebsite(location);
                else stopSelf();

            } else stopSelf();
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) googleApiClient.disconnect();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(trackinginterval * 1000); // milliseconds
        locationRequest.setFastestInterval(trackinginterval * 1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}