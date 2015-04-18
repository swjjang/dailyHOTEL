package com.twoheart.dailyhotel.util;

import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class VolleyImageLoader
{

	private static RequestQueue sRequestQueue;
	private static ImageLoader sImageLoader;
	private static BitmapLruCache mBitmapLruCache;

	public static void init()
	{
		sRequestQueue = VolleyHttpClient.getRequestQueue();
		mBitmapLruCache = new BitmapLruCache();

		sImageLoader = new ImageLoader(sRequestQueue, mBitmapLruCache);
		sImageLoader.setBatchedResponseDelay(0);

	}

	public static ImageLoader getImageLoader()
	{
		if (sImageLoader == null)
			init();
		return sImageLoader;
	}

	public static void putCache(String url, Bitmap bitmap)
	{
		if (mBitmapLruCache != null)
		{
			mBitmapLruCache.putBitmap(url, bitmap);
		}
	}

	public static Bitmap getCache(String url)
	{
		if (mBitmapLruCache != null)
		{
			return mBitmapLruCache.getBitmap(url);
		}

		return null;
	}

}