package com.twoheart.dailyhotel.util.ui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.widget.FontManager;

public class LocationFactory
{
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private static LocationFactory mInstance;

	private LocationManager sLocationManager = null;
	private Location sLocation = null;
	private boolean sIsMeasuringLocation = false;
	private LocationListener mLocationListener;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			stopLocationMeasure();
		};
	};

	public static LocationFactory getInstance()
	{
		if (mInstance == null)
		{
			synchronized (FontManager.class)
			{
				if (mInstance == null)
				{
					mInstance = new LocationFactory();
				}
			}
		}
		return mInstance;
	}

	public void startLocationMeasure(final Fragment fragment, LocationListener listener)
	{
		if (sIsMeasuringLocation)
		{
			return;
		}

		if (sLocationManager == null)
		{
			sLocationManager = (LocationManager) fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
		}

		mLocationListener = listener;
		sLocation = null;
		sIsMeasuringLocation = true;

		boolean isGpsOn = sLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (isGpsOn == true)
		{
			sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mOnLocationListener);
		} else
		{
			Criteria criteria = new Criteria();
			String provider = sLocationManager.getBestProvider(criteria, true);

			ExLog.d("provider : " + provider);

			sLocationManager.requestLocationUpdates(provider, 0, 0, mOnLocationListener);
		}

		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 30 * 1000);
	}

	public void stopLocationMeasure()
	{
		mHandler.removeMessages(0);

		if (sLocationManager != null && mOnLocationListener != null)
		{
			sLocationManager.removeUpdates(mOnLocationListener);
		}
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

	private LocationListener mOnLocationListener = new LocationListener()
	{
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			if (mLocationListener != null)
			{
				mLocationListener.onStatusChanged(provider, status, extras);
			}
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			if (mLocationListener != null)
			{
				mLocationListener.onProviderEnabled(provider);
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			sIsMeasuringLocation = false;

			if (mLocationListener != null)
			{
				mLocationListener.onProviderDisabled(provider);
			}
		}

		@Override
		public void onLocationChanged(Location location)
		{
			if (isBetterLocation(location, sLocation))
			{
				sLocation = location;
			}

			sIsMeasuringLocation = false;

			if (mLocationListener != null)
			{
				mLocationListener.onLocationChanged(sLocation);
			}
		}
	};
}
