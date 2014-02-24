package com.twoheart.dailyhotel.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

public class DhManager {
	
	SharedPreferences prefs;
	
	static public int getPixels(int dipValue, Context context) {
		Resources r = context.getResources();
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, 
        r.getDisplayMetrics());
        return px;
	}

}
