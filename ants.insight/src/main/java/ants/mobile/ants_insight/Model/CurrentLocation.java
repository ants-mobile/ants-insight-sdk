package ants.mobile.ants_insight.Model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import ants.mobile.ants_insight.Constants.Constants;
import ants.mobile.ants_insight.InsightSharedPref;

public class CurrentLocation {
    private Activity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        int PERMISSION_ID = 44;
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    public void getAndSaveLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                InsightSharedPref.savePreference(Constants.PREF_CURRENT_LONGITUDE, String.valueOf(location.getLongitude()));
                                InsightSharedPref.savePreference(Constants.PREF_CURRENT_LATITUDE, String.valueOf(location.getLatitude()));
                            }
                        }
                );
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            InsightSharedPref.savePreference(Constants.PREF_CURRENT_LONGITUDE, String.valueOf(mLastLocation.getLongitude()));
            InsightSharedPref.savePreference(Constants.PREF_CURRENT_LATITUDE, String.valueOf(mLastLocation.getLatitude()));
        }
    };

    public static class Builder {
        private Activity mActivity;

        public Builder activity(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        public CurrentLocation build() {
            return new CurrentLocation(this);
        }

    }

    private CurrentLocation(Builder builder) {
        activity = builder.mActivity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mContext = activity.getApplicationContext();
    }
}
