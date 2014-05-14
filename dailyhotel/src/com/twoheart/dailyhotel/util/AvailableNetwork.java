package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AvailableNetwork {
	public static final int NET_TYPE_NONE = 0x00;
	public static final int NET_TYPE_WIFI = 0x01;
	public static final int NET_TYPE_3G = 0x02;
	private static AvailableNetwork current = null;

	public static AvailableNetwork getInstance() {
		if (current == null) {
			current = new AvailableNetwork();
		}
		return current;
	}

	private boolean getWifiState(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (ni != null)
			if (ni.isConnected())
				return true;
		return false;
	}

	private boolean get3GState(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (ni != null)
			if (ni.isConnected())
				return true;
		return false;
	}

	public int getNetType(Context context) {
		int nNetType = AvailableNetwork.NET_TYPE_NONE;

		if (getWifiState(context)) {
			nNetType = AvailableNetwork.NET_TYPE_WIFI;
		} else if (get3GState(context)) {
			nNetType = AvailableNetwork.NET_TYPE_3G;
		}

		return nNetType;
	}
	
}
