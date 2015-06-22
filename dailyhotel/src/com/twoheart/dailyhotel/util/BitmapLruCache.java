/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twoheart.dailyhotel.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache
{
	public static int getDefaultLruCacheSize()
	{
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		int divideValue = 8;
		// [2015.02.26 - bugman] 저사양 단말의 경우 Bitmap Cache Size가 너무 작아서 이미지 로딩에 이슈가 발생하기 때문에 Size를 조정함..
		// 주 메모리 용량이 65MB 보다 작을 경우는 CacheSize를 1/4로 하고 그 이상은 1/8로 조정함. (테스트하면서 값 조정 필요)
		if (maxMemory < 65 * 1024)
		{
			divideValue = 4;
		}

		final int cacheSize = maxMemory / divideValue;

		ExLog.i("[BitmapLruCache - getDefaultLruCacheSize] maxMemory : " + maxMemory + ", cacheSize : " + cacheSize);

		return cacheSize;
	}

	public BitmapLruCache()
	{
		this(getDefaultLruCacheSize());
	}

	public BitmapLruCache(int sizeInKiloBytes)
	{
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value)
	{
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url)
	{
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap)
	{
		if (url == null || bitmap == null)
		{
			return;
		}

		put(url, bitmap);
	}
}