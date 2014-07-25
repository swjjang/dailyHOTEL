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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
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

	private RequestFilter cancelAllRequestFilter;

	private Handler handler;

	private Runnable networkCheckRunner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		mQueue = VolleyHttpClient.getRequestQueue();
		mLockUI = new LoadingDialog(this);
		
		cancelAllRequestFilter = new RequestQueue.RequestFilter() {
		    @Override
	        public boolean apply(Request<?> request) {
	            return true;
	        }
	    };
	    
	    handler = new Handler();
		networkCheckRunner = new Runnable() {
			@Override
			public void run() {
				if(mLockUI.isVisible()) {
					android.util.Log.e("EXPIRED_UNLOCK","true");
					mQueue.cancelAll(cancelAllRequestFilter);
					unLockUI();
					onError();
				}
			}
		};
	    
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// RequestQueue에 등록된 모든 Request들을 취소한다.
		if (mQueue != null)
			mQueue.cancelAll(cancelAllRequestFilter);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
	
	/**
	 * 액션바를 설정하는 메서드로서, 어플리케이션 액션바 테마를 설정하고 제목을 지정한다.
	 * 
	 * @param title 액션바에 표시할 화면의 제목을 받는다.
	 */
	public void setActionBar(String title) {
		actionBar = getSupportActionBar();
		
//		int resType = DeviceResolutionUtil.getResolutionType(this);
		
		// bottom에 1px 구분선 추가된 흰 배경.
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		
		actionBar.setIcon(R.drawable.img_ic_menu);
		actionBar.setTitle(title);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	public void setActionBar(int strId) {
		setActionBar(getString(strId));
	}
	
	
	/**
	 * 액션바에 ProgressBar를 표시할 수 있도록 셋팅한다.
	 */
	public void setActionBarProgressBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			setSupportProgressBarIndeterminate(true);
		}
	}
	
	/**
	 * 액션바를 숨기도록 셋팅한다.
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
		
		// 현재 Activity에 의존적인 Toast를 제거한다.
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
		
		com.facebook.AppEventsLogger.activateApp(this, getString(R.string.app_id));
		
	}
	
	@Override
	protected void onStop() {
		
		// 현재 Activity에 등록된 Request를 취소한다. 
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

	/**
	 * LoadingDialog를 띄워 로딩 중임을 나타내어 사용자가 UI를 사용할 수 없도록 한다.
	 */
	@Override
	public void lockUI() {
		android.util.Log.e("LOCKED","a");
		mLockUI.show();
		// 만약 제한시간이 지났는데도 리퀘스트가 끝나지 않았다면 Error 발생.
		handler.postDelayed(networkCheckRunner, REQUEST_EXPIRE_JUDGE);
		
	}

	/**
	 * 로딩이 완료되어 LoadingDialog를 제거하고 전역 폰트를 설정한다.
	 */
	@Override
	public void unLockUI() {
		android.util.Log.e("UNLOCKED","a");
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
		mLockUI.hide();
		handler.removeCallbacks(networkCheckRunner);
		
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
	
	/**
	 * Error 발생 시 분기되는 메서드
	 */
	public void onError() {
		// 잘못된 멘트, 모든 에러가 이쪽으로 빠지게됨. 변경 필요.
		showToast("인터넷 연결 상태가 불안정합니다.\n인터넷 연결을 확인하신 뒤 다시 시도해주세요.", Toast.LENGTH_LONG, false);
	}
	
	/**
	 * Toast를 쉽게 표시해주는 메서드로서, 참조 Context로는 ApplicationContext를 사용한다. 
	 * 삼성 단말기에서 삼성 테마를 사용하기 위함이다.
	 * 
	 * @param message Toast에 표시할 내용
	 * @param length Toast가 표시되는 시간. Toast.LENGTH_SHORT, Toast.LENGTH_LONG
	 * @param isAttachToActivity	현재 Activity가 종료되면 Toast도 제거할지를 결정한다
	 */
	public void showToast(String message, int length, boolean isAttachToActivity) {
		if (mToast != null)
			mToast.cancel();
		
		if (isAttachToActivity) {
			mToast = Toast.makeText(getApplicationContext(), message, length);
			mToast.show();
			
		} else {
			Toast.makeText(getApplicationContext(), message, length).show();
			
		}
	}
	
	/**
	 * 버튼 난타를 방지하기 위한 메서드, 버튼의 클릭 가능 여부를 반대로 변경.
	 * @param v 타겟 뷰
	 */
	protected void chgClickable(View v) {
		v.setClickable(!v.isClickable());
	}
	

	protected void chgClickable(View v, boolean isClickable) {
		v.setClickable(isClickable);
	}
	
}
