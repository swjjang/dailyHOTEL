package com.daily.dailyhotel.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.storage.preference.DailyPreference;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class DailyLocationExFactory
{
    private static final long UPDATE_INTERVAL = 1000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    private static final long MAX_WAIT_TIME = 15000;

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    SettingsClient mSettingsClient;
    LocationCallback mLocationCallback;

    private boolean mIsMeasuringLocation = false;

    OnLocationListener mLocationListener;
    Context mContext;

    Handler mHandler = new Handler()
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

        void onCheckSetting(ResolvableApiException exception);
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
                                mLocationSettingsRequest = buildLocationSettingsRequest(mLocationRequest);
                                mSettingsClient = LocationServices.getSettingsClient(mContext);
                                mLocationCallback = createLocationCallback();
                            }

                            mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>()
                            {
                                @Override
                                public void onSuccess(LocationSettingsResponse locationSettingsResponse)
                                {
                                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    stopLocationMeasure();

                                    int statusCode = ((ApiException) e).getStatusCode();

                                    switch (statusCode)
                                    {
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                            if (mLocationListener != null)
                                            {
                                                mLocationListener.onCheckSetting((ResolvableApiException) e);
                                            }
                                            break;

                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        default:
                                            if (mLocationListener != null)
                                            {
                                                mLocationListener.onFailed();
                                            }
                                            break;
                                    }
                                }
                            });

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

        if (mFusedLocationClient != null && mLocationCallback != null)
        {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

        mIsMeasuringLocation = false;
    }

    LocationRequest createLocationRequest()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    LocationCallback createLocationCallback()
    {
        return new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult);

                if (mLocationListener != null)
                {
                    mLocationListener.onLocationChanged(locationResult.getLastLocation());
                }

                stopLocationMeasure();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability)
            {
                super.onLocationAvailability(locationAvailability);

                if (locationAvailability.isLocationAvailable() == false)
                {
                    stopLocationMeasure();

                    if (mLocationListener != null)
                    {
                        mLocationListener.onFailed();
                    }
                }
            }
        };
    }

    LocationSettingsRequest buildLocationSettingsRequest(LocationRequest locationRequest)
    {
        if (locationRequest == null)
        {
            return null;
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        return builder.build();
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
}
