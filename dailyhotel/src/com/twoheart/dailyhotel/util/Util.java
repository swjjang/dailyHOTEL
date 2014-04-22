package com.twoheart.dailyhotel.util;

import android.content.Context;

public class Util {
	
	public static int dpToPx(Context context, double dp) {
		float scale = context.getResources().getDisplayMetrics().density; 
		return (int) (dp * scale + 0.5f);
		
	}

}
