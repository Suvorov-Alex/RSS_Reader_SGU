package com.rssreader.app.alex.rss_reader_sgu.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Alex on 02.08.2017.
 */

public class Location {
    private static final String LOCATION_TAG = "LocationLog";

    private LocationManager locationManager;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(LOCATION_TAG, "onStatusChanged: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            try {
                showLocation(locationManager.getLastKnownLocation(provider));
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }
    };

    public Location(Context context) {
        locationManager = (LocationManager)
                context.getSystemService(LOCATION_SERVICE);
    }


    public void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            checkEnabled();
            Log.d(LOCATION_TAG, "Please wait while the geolocation data is collected...");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean isProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void removeUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    private void checkEnabled() {
        Log.d(LOCATION_TAG,
                "Enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    private void showLocation(android.location.Location location) {
        if (location == null) {
            Log.d(LOCATION_TAG, "showLocation: " + "LOCATION NULL");
            return;
        }
        Log.d(LOCATION_TAG, "showLocation: " + formatLocation(location));
    }

    private String formatLocation(android.location.Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }
}
