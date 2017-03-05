package com.chenmo.callroll;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class LocationTools {
    private LocationManager mLocationManager;
    private LocationProvider mLocatonProvider;
    private Context parent;
    public double longitude;
    public double latitude;
    public boolean hasGetLocation=false;

    public LocationTools(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocatonProvider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
        parent=context;
    }

    public void getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (parent.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && parent.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        Location mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(mLocation!=null){
            longitude =mLocation.getLongitude();
            latitude = mLocation.getLatitude();
        }
        Log.e(Double.toString(latitude) + " 第一次 ", Double.toString(longitude));
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=null){
                    longitude =location.getLongitude();
                    latitude = location.getLatitude();
                }
                hasGetLocation=true;
                Log.e(Double.toString(latitude) + " A当变化是 ", Double.toString(longitude));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (parent.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && parent.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                Location mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(mLocation!=null){
                    longitude =mLocation.getLongitude();
                    latitude = mLocation.getLatitude();
                }
                Log.e(Double.toString(latitude) + " 可用时 ", Double.toString(longitude));
            }

            @Override
            public void onProviderDisabled(String provider) {
                longitude=-1;
                latitude=-1;
                Log.e(Double.toString(latitude) + " 不可用时 ", Double.toString(longitude));
            }
        });
    }
}
