package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;

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

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
	
	public static View getActionBarView(Activity activity) {
	    Window window = activity.getWindow();
	    View v = window.getDecorView();
	    int resId = activity.getResources().getIdentifier("action_bar_container", "id", "android");
	    return v.findViewById(resId);
	}

}
