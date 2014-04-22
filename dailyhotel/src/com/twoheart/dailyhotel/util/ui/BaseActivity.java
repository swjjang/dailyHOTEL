/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BaseActivity
 * 
 * ActionBarCompat 라이브러리의 ActionBarActivity를 상속받는 A
 * ctivity로서 어플리케이션에서 사용되는 Activity들의 UI를 기본적으로 구
 * 성하는데 필요한 API 메서드들을 제공한다. 뿐만 아니라, CookieSyncMana
 * ger의 인스턴스를 관리하기도 하며, 어플리케이션의 SharedPreference를
 * 관리하기도 한다.
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
import android.view.KeyEvent;
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
	
	/**
	 * setActionBar(String title)
	 * 액션바 설정 메소드
	 * 
	 * @param title
	 */
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
	
	/**
	 * setActionBarHide()
	 * 액션바를 숨겨주는 메소드
	 * 
	 */
	public void setActionBarHide() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			getSupportActionBar().hide();
		
	}
	  
	// 메뉴 버튼을 막아버림.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
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
		
		try {
			if (cookieSyncManager != null)
				cookieSyncManager.startSync();
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			
			
		}
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

}
