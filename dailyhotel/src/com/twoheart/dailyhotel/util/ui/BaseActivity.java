/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BaseActivity
 * 
 * ActionBarCompat 라이브러리의 ActionBarActivity를 상속받는 A
 * ctivity로서 어플리케이션에서 사용되는 Activity들의 UI를 기본적으로 구
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieSyncManager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;

public class BaseActivity extends ActionBarActivity implements Constants {

	private final static String TAG = "BaseActivity";

	public ActionBar actionBar;
	public SharedPreferences sharedPreference;
	public CookieSyncManager cookieSyncManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
	
	public void setActionBar(String title) {
		actionBar = getSupportActionBar();
		
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().
				getColor(android.R.color.white)));
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		
		actionBar.setIcon(R.drawable.img_ic_menu);
		actionBar.setTitle(title);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	public void setActionBarHide() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			getSupportActionBar();
		
	}
	
	@Override
	protected void onPause() {
		if (cookieSyncManager != null)
			cookieSyncManager.stopSync();
		
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (cookieSyncManager != null)
			cookieSyncManager.startSync();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
//	@Override
//	public void setContentView(int layoutResID) {
//		super.setContentView(layoutResID);
//		setGlobalFont((ViewGroup) this.getWindow().getDecorView().findViewById(
//				android.R.id.content));
//	}

//	public void setGlobalFont(ViewGroup root) {
//		if (BaseActivity.mTypefaceCommon == null) {
//			BaseActivity.mTypefaceCommon = Typeface.createFromAsset(getAssets(),
//					"NanumBarunGothic.ttf.mp3");
//			
//			BaseActivity.mTypefaceBold = Typeface.createFromAsset(getAssets(),
//					"NanumBarunGothicBold.ttf.mp3");
//		}
//
//		int childCnt = root.getChildCount();
//		for (int i = 0; i < childCnt; i++) {
//			View v = root.getChildAt(i);
//			Log.d(TAG, v.toString());
//			if (v instanceof TextView) {
//				((TextView) v).setTypeface(mTypefaceCommon);
//			}
//		}
//	}

}
