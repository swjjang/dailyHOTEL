package com.daily.dailyhotel.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.daily.base.util.VersionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.List;

public class DailyLocationExFactory
{
    private static final long UPDATE_INTERVAL = 5000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 1000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3; // Every 3 minutes.

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private boolean mIsMeasuringLocation = false;

    OnLocationListener mLocationListener;
    Context mContext;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    stopLocationMeasure();

                    if (mLocationListener != null)
                    {
                        mLocationListener.onFailed();
                    }
                    break;
            }
        }
    };

    public interface OnLocationListener
    {
        void onFailed();

        void onAlreadyRun();

        void onLocationChanged(Location location);
    }

    public interface OnCheckLocationListener
    {
        void onRequirePermission();

        void onFailed();

        void onProviderDisabled();

        void onProviderEnabled();
    }

    public DailyLocationExFactory(@NonNull Context context)
    {
        mContext = context;
    }

    public boolean measuringLocation()
    {
        return mIsMeasuringLocation;
    }

    public void checkLocationMeasure(OnCheckLocationListener listener)
    {
        if (mContext == null)
        {
            return;
        }

        if (DailyPreference.getInstance(mContext).isAgreeTermsOfLocation() == false)
        {
            if (listener != null)
            {
                listener.onRequirePermission();
            }

            return;
        }

        if (VersionUtils.isOverAPI23() == true && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (listener != null)
            {
                listener.onRequirePermission();
            }

            return;
        }

        if (isLocationProviderEnabled() == true)
        {
            listener.onProviderEnabled();
        } else
        {
            listener.onProviderDisabled();
        }
    }

    /**
     * checkLocationMeasure이 onProviderEnabled일때 호출
     *
     * @param listener
     */
    public void startLocationMeasure(OnLocationListener listener)
    {
        if (mContext == null)
        {
            return;
        }

        if (mIsMeasuringLocation)
        {
            mLocationListener.onAlreadyRun();
            return;
        }

        mIsMeasuringLocation = true;

        if (mFusedLocationClient == null)
        {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        }

        mLocationListener = listener;

        try
        {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
            {
                @Override
                public void onComplete(@NonNull Task<Location> task)
                {
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        Location location = task.getResult();

                        if (mLocationListener != null)
                        {
                            mLocationListener.onLocationChanged(location);
                        }

                        stopLocationMeasure();
                        return;
                    } else
                    {
                        try
                        {
                            if (mLocationRequest == null)
                            {
                                mLocationRequest = createLocationRequest();
                            }

                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent(mContext));

                            mHandler.removeMessages(0);
                            mHandler.sendEmptyMessageDelayed(0, MAX_WAIT_TIME);
                        } catch (SecurityException e)
                        {
                            stopLocationMeasure();

                            if (mLocationListener != null)
                            {
                                mLocationListener.onFailed();
                            }
                        }
                    }
                }
            });
        } catch (SecurityException e)
        {
            stopLocationMeasure();

            if (mLocationListener != null)
            {
                mLocationListener.onFailed();
            }
        }
    }

    public void stopLocationMeasure()
    {
        mHandler.removeMessages(0);

        if (mFusedLocationClient != null)
        {
            mFusedLocationClient.removeLocationUpdates(getPendingIntent(mContext));
        }

        mIsMeasuringLocation = false;
    }

    private PendingIntent getPendingIntent(Context context)
    {
        Intent intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);

        return locationRequest;
    }

    private boolean isLocationProviderEnabled()
    {
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        try
        {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex)
        {
            //do nothing...
        }

        try
        {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex)
        {
            //do nothing...
        }

        return gpsEnabled || networkEnabled;
    }

    private class LocationUpdatesBroadcastReceiver extends BroadcastReceiver
    {
        static final String ACTION_PROCESS_UPDATES = "com.daily.dailyhotel.PROCESS_UPDATES";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null)
            {
                return;
            }

            final String action = intent.getAction();

            if (ACTION_PROCESS_UPDATES.equals(action))
            {
                stopLocationMeasure();

                LocationResult result = LocationResult.extractResult(intent);

                if (result != null)
                {
                    List<Location> locations = result.getLocations();

                    if (locations.isEmpty() == true)
                    {
                        if (mLocationListener != null)
                        {
                            mLocationListener.onLocationChanged(locations.get(locations.size() - 1));
                        }
                    } else
                    {
                        if (mLocationListener != null)
                        {
                            mLocationListener.onFailed();
                        }
                    }
                } else
                {
                    if (mLocationListener != null)
                    {
                        mLocationListener.onFailed();
                    }
                }
            }
        }
    }
}
