package com.twoheart.dailyhotel.util;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

import android.graphics.Bitmap;

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
		synchronized (VolleyImageLoader.class)
		{
			if (sImageLoader == null)
			{
				init();
			}
		}

		return sImageLoader;
	}

	public static void putCache(String url, Bitmap bitmap)
	{
		if (mBitmapLruCache != null && bitmap != null)
		{
			mBitmapLruCache.putBitmap(url, bitmap);
		}
	}

	public static Bitmap getCache(String url)
	{
		if (mBitmapLruCache != null && url != null)
		{
			return mBitmapLruCache.getBitmap(url);
		}

		return null;
	}

	public static void removeCache(String url)
	{
		if (mBitmapLruCache != null)
		{
			mBitmapLruCache.remove(url);
		}
	}

}