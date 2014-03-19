package com.twoheart.dailyhotel.util;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class VolleyImageLoader {
	
	private static RequestQueue sRequestQueue;
	private static ImageLoader sImageLoader;
	
	public static void init(Context context) {

		VolleyHttpClient.init(context);
		sRequestQueue = VolleyHttpClient.getRequestQueue();
		
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        sImageLoader = new ImageLoader(sRequestQueue, new BitmapLruCache(cacheSize));
    }
	
	public static ImageLoader getImageLoader() {
        if (sImageLoader != null) {
            return sImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader is not initialized");
        }
    }

}
