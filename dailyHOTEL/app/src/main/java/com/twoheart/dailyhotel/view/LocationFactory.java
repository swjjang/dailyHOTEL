package com.twoheart.dailyhotel.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import java.util.List;

public class LocationFactory
{
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int TEN_MINUTES = 1000 * 60;
    protected static final String SINGLE_LOCATION_UPDATE_ACTION = "com.twoheart.dailyhotel.places.SINGLE_LOCATION_UPDATE_ACTION";
    private static LocationFactory mInstance;
    protected PendingIntent mUpdatePendingIntent;
    private LocationManager mLocationManager = null;
    private boolean mIsMeasuringLocation = false;
    private LocationListenerEx mLocationListener;
    private View mMyLocationView;
    private Drawable mMyLocationDrawable;
    private BaseActivity mBaseActivity;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (mBaseActivity == null || mBaseActivity.isFinishing() == true)
            {
                return;
            }

            switch (msg.what)
            {
                case 0:
                    stopLocationMeasure();

                    if (mLocationListener != null)
                    {
                        mLocationListener.onFailed();
                    }

                    if (mBaseActivity != null)
                    {
                        DailyToast.showToast(mBaseActivity, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    }
                    break;

                case 1:
                {
                    if (mMyLocationView != null)
                    {
                        mMyLocationView.setBackgroundColor(mBaseActivity.getResources().getColor(R.color.dh_theme_color));
                    }

                    sendEmptyMessageDelayed(2, 1000);
                    break;
                }

                case 2:
                {
                    if (mMyLocationView != null)
                    {
                        mMyLocationView.setBackgroundDrawable(mMyLocationDrawable);
                    }

                    sendEmptyMessageDelayed(1, 1000);
                    break;
                }

                case 3:
                {
                    if (mMyLocationView != null)
                    {
                        mMyLocationView.setBackgroundDrawable(mMyLocationDrawable);
                    }
                    break;
                }

                case 4:
                {
                    stopLocationMeasure();
                    break;
                }
            }
        }
    };

    protected BroadcastReceiver mSingleUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String key = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(key);

            if (mLocationListener != null && location != null)
            {
                mLocationListener.onLocationChanged(location);
            }

            stopLocationMeasure();
        }
    };

    public interface LocationListenerEx extends LocationListener
    {
        void onRequirePermission();

        void onFailed();
    }

    private LocationFactory()
    {
    }

    public synchronized static LocationFactory getInstance(BaseActivity activity)
    {
        if (mInstance == null)
        {
            mInstance = new LocationFactory();
        }

        mInstance.mBaseActivity = activity;

        return mInstance;
    }

    public void clear()
    {
        mInstance = null;
    }

    public void startLocationMeasure(Activity activity, View myLocation, LocationListenerEx listener)
    {
        if (activity == null)
        {
            return;
        }

        if (mIsMeasuringLocation)
        {
            return;
        }

        if (mLocationManager == null)
        {
            mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }

        if (mUpdatePendingIntent == null)
        {
            Intent updateIntent = new Intent(SINGLE_LOCATION_UPDATE_ACTION);
            mUpdatePendingIntent = PendingIntent.getBroadcast(mBaseActivity, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mLocationListener = listener;
        mMyLocationView = myLocation;

        if (mMyLocationView != null)
        {
            mMyLocationDrawable = mMyLocationView.getBackground();
        }

        boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsProviderEnabled == false && isNetworkProviderEnabled == false)
        {
            if (mLocationListener != null)
            {
                mLocationListener.onProviderDisabled(null);
            }
            return;
        }

        //        if (hasPermission() == false)
        //        {
        //            if (mLocationListener != null)
        //            {
        //                mLocationListener.onRequirePermission();
        //            }
        //            return;
        //        }

        mIsMeasuringLocation = true;

        Location location = getLastBestLocation(mBaseActivity, 10, TEN_MINUTES);

        boolean hasLastLocation = false;

        if (location != null && mLocationListener != null)
        {
            mLocationListener.onLocationChanged(location);
            stopLocationMeasure();
            return;
        }

        mHandler.sendEmptyMessageDelayed(1, 1000);

        try
        {
            IntentFilter locIntentFilter = new IntentFilter(SINGLE_LOCATION_UPDATE_ACTION);
            mBaseActivity.registerReceiver(mSingleUpdateReceiver, locIntentFilter);
            mLocationManager.requestSingleUpdate(new Criteria(), mUpdatePendingIntent);

            mHandler.removeMessages(0);

            if (hasLastLocation == true)
            {
                mHandler.sendEmptyMessageDelayed(4, 10 * 1000);
            } else
            {
                mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            mHandler.sendEmptyMessage(4);
        }
    }

    public void startLocationMeasure(Fragment fragment, View myLocation, LocationListenerEx listener)
    {
        startLocationMeasure(fragment.getActivity(), myLocation, listener);
    }

    //    public boolean hasPermission()
    //    {
    //        if (Util.isOverAPI23() == true)
    //        {
    //            if (mLocationManager == null)
    //            {
    //                mLocationManager = (LocationManager) mBaseActivity.getSystemService(Context.LOCATION_SERVICE);
    //            }
    //
    //            List<String> matchingProviders = mLocationManager.getAllProviders();
    //
    //            for (String provider : matchingProviders)
    //            {
    //                Location location = mLocationManager.getLastKnownLocation(provider);
    //                boolean isEnabled = mLocationManager.isProviderEnabled(provider);
    //
    //                if (location != null)
    //                {
    //                    return true;
    //                }
    //            }
    //        } else
    //        {
    //            return true;
    //        }
    //
    //        return false;
    //    }

    public Location getLastBestLocation(Context context, int minDistance, long minTime)
    {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Iterate through all the providers on the system, keeping
        // note of the most accurate result within the acceptable time limit.
        // If no result is found within maxTime, return the newest Location.
        List<String> matchingProviders = mLocationManager.getAllProviders();
        for (String provider : matchingProviders)
        {
            Location location = mLocationManager.getLastKnownLocation(provider);

            if (location != null)
            {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy))
                {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                } else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime)
                {
                    bestResult = location;
                    bestTime = time;
                }
            }
        }

        return bestResult;
    }

    public void stopLocationMeasure()
    {
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);

        mHandler.sendEmptyMessage(3);

        if (mLocationManager != null) // && mOnLocationListener != null)
        {
            //			mLocationManager.removeUpdates(mOnLocationListener);
            mLocationManager.removeUpdates(mUpdatePendingIntent);
        }

        if (mBaseActivity != null)
        {
            try
            {
                mBaseActivity.unregisterReceiver(mSingleUpdateReceiver);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        mIsMeasuringLocation = false;
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer)
        {
            return true;
        } else if (isSignificantlyOlder)
        {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate)
        {
            return true;
        } else if (isNewer && !isLessAccurate)
        {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }

        return false;
    }

    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //	private LocationListener mOnLocationListener = new LocationListener()
    //	{
    //		@Override
    //		public void onStatusChanged(String provider, int status, Bundle extras)
    //		{
    //			if (mLocationListener != null)
    //			{
    //				mLocationListener.onStatusChanged(provider, status, extras);
    //			}
    //		}
    //
    //		@Override
    //		public void onProviderEnabled(String provider)
    //		{
    //			if (mLocationListener != null)
    //			{
    //				mLocationListener.onProviderEnabled(provider);
    //			}
    //		}
    //
    //		@Override
    //		public void onProviderDisabled(String provider)
    //		{
    //			mIsMeasuringLocation = false;
    //
    //			if (mLocationListener != null)
    //			{
    //				mLocationListener.onProviderDisabled(provider);
    //			}
    //		}
    //
    //		@Override
    //		public void onLocationChanged(Location location)
    //		{
    //			if (isBetterLocation(location, mLocation))
    //			{
    //				mLocation = location;
    //			}
    //
    //			mIsMeasuringLocation = false;
    //
    //			if (mLocationListener != null)
    //			{
    //				mLocationListener.onLocationChanged(mLocation);
    //			}
    //		}
    //	};
}
