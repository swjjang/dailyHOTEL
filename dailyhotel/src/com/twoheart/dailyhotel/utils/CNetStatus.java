package com.twoheart.dailyhotel.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CNetStatus {
	public static final int NET_TYPE_NONE = 0x00;
	public static final int NET_TYPE_WIFI = 0x01;
	public static final int NET_TYPE_3G = 0x02;
	private static CNetStatus current = null;
	
	public static CNetStatus getInstance() {
		if (current == null) {
			current = new CNetStatus();
		}
		return current;
	}
	
	private boolean getWifiState(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context  
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	    boolean isConn = ni.isConnected();  
	    return isConn;  
	}
	
	private boolean get3GState(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context  
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	    boolean isConn = ni.isConnected();  
	    return isConn;  
	}
	
	public int getNetType(Context context) {
		int nNetType = CNetStatus.NET_TYPE_NONE;  
		  
	    if (getWifiState(context)) {  
	        nNetType = CNetStatus.NET_TYPE_WIFI;  
	    } else if (get3GState(context)) {  
	        nNetType = CNetStatus.NET_TYPE_3G;  
	    }  
	  
	    return nNetType; 
	}
}
