package com.twoheart.dailyhotel.util;

import android.content.Context;

public class Util implements Constants {
	
	public static int dpToPx(Context context, double dp) {
		float scale = context.getResources().getDisplayMetrics().density; 
		return (int) (dp * scale + 0.5f);
	}
	
	public static String storeReleaseAddress() {
		if (IS_GOOGLE_RELEASE) {
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else {
			return URL_STORE_T_DAILYHOTEL;
		}
	}
	
	public static String storeReleaseAddress(String newUrl) {
		if (IS_GOOGLE_RELEASE) {
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else {
			return newUrl;
		}
	}
}
