package com.twoheart.dailyhotel.util;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class VolleyImageLoader {

	private static RequestQueue sRequestQueue;
	private static ImageLoader sImageLoader;
	
	public static void init() {
		sRequestQueue = VolleyHttpClient.getRequestQueue();

		int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);
		sImageLoader = new ImageLoader(sRequestQueue, new BitmapLruCache(
				cacheSize));
		sImageLoader.setBatchedResponseDelay(0);
		
	}

	public static ImageLoader getImageLoader() {
		if (sImageLoader == null) init();
		return sImageLoader;
	}

}