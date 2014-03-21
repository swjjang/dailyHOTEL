package com.twoheart.dailyhotel.util;

public class Log implements Constants {
	
	public static void d(String tag, String message) {
		if (DEBUG)
			android.util.Log.d(tag, message);
		
	}
	
	public static void e(String message) {
		android.util.Log.e("dailyHOTEL", message);
		
	}

}
