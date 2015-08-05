/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * DailyHotel
 * 
 * Android의 Application을 상속받은 서브 클래스로서 어플리케이션의 가장
 * 기본이 되는 클래스이다. 이 클래스에서는 어플리케이션에서 전역적으로 사용되
 * 는 GoogleAnalytics와 폰트, Volley, Universal Image Loder를
 * 초기화하는 작업을 생성될 시(onCreate)에 수행한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import io.fabric.sdk.android.Fabric;
import android.app.Application;
import android.graphics.Typeface;

import com.androidquery.callback.BitmapAjaxCallback;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class DailyHotel extends Application implements Constants
{
	public static String VERSION;

	private static Typeface mTypeface;
	private static Typeface mBoldTypeface;

	@Override
	public void onCreate()
	{
		super.onCreate();

		Fabric.with(this, new Crashlytics());

		// 버전 정보 얻기
		DailyHotel.VERSION = Util.getAppVersion(getApplicationContext());

		initializeVolley();
		initializeAnalytics();
		initializeFont();
	}

	private void initializeAnalytics()
	{
		AnalyticsManager.getInstance(getApplicationContext());
	}

	private void initializeVolley()
	{
		VolleyHttpClient.init(this);
		VolleyImageLoader.init();
	}

	private void initializeFont()
	{
		try
		{
			mTypeface = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.ttf");
			mBoldTypeface = Typeface.createFromAsset(getAssets(), "NanumBarunGothicBold.ttf");
		} catch (Exception e)
		{
			ExLog.d(e.toString());

			mTypeface = Typeface.DEFAULT;
			mBoldTypeface = Typeface.DEFAULT_BOLD;
		}
	}

	public static Typeface getTypeface()
	{
		return mTypeface;
	}

	public static Typeface getBoldTypeface()
	{
		return mBoldTypeface;
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		BitmapAjaxCallback.clearCache();
	}

}
