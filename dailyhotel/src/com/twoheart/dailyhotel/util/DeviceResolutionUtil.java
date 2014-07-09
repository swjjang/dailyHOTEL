package com.twoheart.dailyhotel.util;

import java.util.HashMap;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DeviceResolutionUtil {
	
	public static final int KEY_WIDTH = 0;
	public static final int KEY_HEIGHT = 1;
//	public static final int KEY_DENSITY = 2;
	
	public static final int RESOLUTION_XXHDPI = 4; //s4,s5,g프로,vega6 등 
	public static final int RESOLUTION_XHDPI = 3; //s3 등 720x1280
	public static final int RESOLUTION_HDPI = 2; //s2 등 480x800
	public static final int RESOLUTION_MDPI = 1; // 320x480
	public static final int RESOLUTION_LDPI = 0; //  320x200
	
	public static HashMap<Integer,Integer> getDeviceResolution(Activity act) {
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		HashMap<Integer,Integer> hm = new HashMap<Integer, Integer>();
		hm.put(KEY_WIDTH, displayMetrics.widthPixels);
		hm.put(KEY_HEIGHT, displayMetrics.heightPixels);
//		hm.put(KEY_DENSITY, displayMetrics.density);
		
		return hm;
	}
	
	public static int getResolutionType(Activity act) {
		  HashMap<Integer, Integer> hm = getDeviceResolution(act);
		  int width = hm.get(KEY_WIDTH);
		  int height = hm.get(KEY_HEIGHT);
		  
		  int resoultion = -1;
		  
		  if (height >= 1920 && width >= 1080) {
			  android.util.Log.e("RESOLUTION","XXHDPI");
			  resoultion = RESOLUTION_XXHDPI;
		  } else if (height >= 1280 && width >= 720) {
			  android.util.Log.e("RESOLUTION","XHDPI");
			  resoultion = RESOLUTION_XHDPI;
		  } else if (height >= 800 && width >= 480) {
			  android.util.Log.e("RESOLUTION","HDPI");
			  resoultion = RESOLUTION_HDPI;
		  } else if (height >= 480 && width >= 320) {
			  android.util.Log.e("RESOLUTION","MDPI");
			  resoultion = RESOLUTION_MDPI;
		  } else {
			  android.util.Log.e("RESOLUTION","LDPI");
			  resoultion = RESOLUTION_LDPI;
		  }
		  return resoultion;
	}
}
