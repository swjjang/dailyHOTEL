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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;

public class BaseActivity extends ActionBarActivity implements Constants, OnLoadListener, ErrorListener {

	private final static String TAG = "BaseActivity";

	public ActionBar actionBar;
	public SharedPreferences sharedPreference;
	
	protected RequestQueue mQueue;
	protected Toast mToast;
	
	private LoadingDialog mLockUI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		mQueue = VolleyHttpClient.getRequestQueue();
		mLockUI = new LoadingDialog(this);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (mQueue != null)
			mQueue.cancelAll(new RequestQueue.RequestFilter() {
			    @Override
		        public boolean apply(Request<?> request) {
		            return true;
		        }
		    });
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
	
	/**
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
	
	public void setActionBarProgressBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			setSupportProgressBarIndeterminate(true);
		}
	}
	
	/**
	 * 액션바를 숨겨주는 메소드
	 * 
	 */
	public void setActionBarHide() {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		if (mToast != null)
			mToast.cancel();
		
		try {
			CookieSyncManager.getInstance().stopSync();
			
		} catch (Exception e) {
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().stopSync();
			
		}
		
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			CookieSyncManager.getInstance().startSync();
			
		} catch (Exception e) {
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().startSync();
			
		}
		
	}
	
	@Override
	protected void onStop() {
		if (mQueue != null)
			mQueue.cancelAll(new RequestQueue.RequestFilter() {
			    @Override
		        public boolean apply(Request<?> request) {
			    		DailyHotelRequest<?> dailyHotelRequest = (DailyHotelRequest<?>) request;
			    		
			    		if (dailyHotelRequest != null && dailyHotelRequest.getTag() != null)
			    			if (dailyHotelRequest.getTag().equals(this)) {
			    				return true;
			    			}
			    				
		            return false;
		        }
		    });
		
		super.onStop();
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

	@Override
	public void lockUI() {
		mLockUI.show();
	}

	@Override
	public void unLockUI() {
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
		mLockUI.hide();
		
	}

	@Override
	protected void onDestroy() {
		mLockUI.hide();
		super.onDestroy();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG) {
			error.printStackTrace();
		}
		
		onError();
	}
	
	public void onError(Exception error) {
		if (DEBUG) {
			error.printStackTrace();
		}
		
		onError();
	}
	
	public void onError() {
		showToast("인터넷 연결 상태가 불안정합니다.\n인터넷 연결을 확인하신 뒤 다시 시도해주세요.", Toast.LENGTH_LONG, false);
	}
	
	public void showToast(String message, int length, boolean isAttachToActivity) {
		if (isAttachToActivity) {
			mToast = Toast.makeText(getApplicationContext(), message, length);
			mToast.show();
			
		} else {
			Toast.makeText(getApplicationContext(), message, length).show();
			
		}
	}
}
