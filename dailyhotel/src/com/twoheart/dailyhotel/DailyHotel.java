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

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.androidquery.callback.BitmapAjaxCallback;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class DailyHotel extends Application implements Constants{
	
	private static Typeface mTypeface;
	private static Typeface mBoldTypeface;
	private static GoogleAnalytics mGa;
	private static Tracker mTracker;

	@Override
	public void onCreate() {
		super.onCreate();
		
		initializeVolley();
		initializeGa();
		initializeFont();

	}

	private void initializeGa() {
		mGa = GoogleAnalytics.getInstance(this);
		mTracker = mGa.getTracker(GA_PROPERTY_ID);

		// Set dispatch period.
		GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

		// Set dryRun flag.
		mGa.setDryRun(GA_IS_DRY_RUN);

		// Set Logger verbosity.
		mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);

		// Set the opt out flag when user updates a tracking preference.
		SharedPreferences userPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		userPrefs
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						if (key.equals(TRACKING_PREF_KEY)) {
							GoogleAnalytics
									.getInstance(getApplicationContext())
									.setAppOptOut(
											sharedPreferences.getBoolean(key,
													false));
						}
					}
				});

		UncaughtExceptionHandler myHandler = new ExceptionReporter(DailyHotel
				.getGaInstance().getDefaultTracker(), // Tracker, may return
														// null if not yet
														// initialized.
				GAServiceManager.getInstance(), // GAServiceManager singleton.
				Thread.getDefaultUncaughtExceptionHandler(), this); // Current
																	// default
																	// uncaught
																	// exception
																	// handler.

		// Make myHandler the new default uncaught exception handler.
		Thread.setDefaultUncaughtExceptionHandler(myHandler);
	}

	/*
	 * Returns the Google Analytics tracker.
	 */
	public static Tracker getGaTracker() {
		return mTracker;
	}

	/*
	 * Returns the Google Analytics instance.
	 */
	public static GoogleAnalytics getGaInstance() {
		return mGa;
	}

	private void initializeVolley() {
		VolleyHttpClient.init(this);

	}
	
	private void initializeVolleyImageLoader() {
		VolleyImageLoader.init();
		
	}
	
	private void initializeFont() {
		mTypeface = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.ttf.mp3");
		mBoldTypeface = Typeface.createFromAsset(getAssets(), "NanumBarunGothicBold.ttf.mp3");
	}

	public static Typeface getTypeface() {
		return mTypeface;
	}

	public static Typeface getBoldTypeface() {
		return mBoldTypeface;
	}
	
	@Override
    public void onLowMemory() {
		super.onLowMemory();
        BitmapAjaxCallback.clearCache();
    }
	
}
