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

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieSyncManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.RegionPopupListView;
import com.twoheart.dailyhotel.widget.RegionPopupListView.UserActionListener;

public class BaseActivity extends ActionBarActivity implements Constants, OnLoadListener, ErrorListener
{
	private Toolbar mToolbar;
	public SharedPreferences sharedPreference;

	protected RequestQueue mQueue;

	private LoadingDialog mLockUI;

	private RequestFilter cancelAllRequestFilter;

	private Handler handler;

	protected Runnable networkCheckRunner;
	private PopupWindow mPopupWindow;
	private int mSpinnderIndex = -1;
	private boolean mActionBarRegionEnabled;

	/**
	 * UI Component의 잠금 상태인지 확인하는 변수..
	 */
	private boolean mIsLockUiComponent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		sharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		mQueue = VolleyHttpClient.getRequestQueue();

		mLockUI = new LoadingDialog(this);

		cancelAllRequestFilter = new RequestQueue.RequestFilter()
		{
			@Override
			public boolean apply(Request<?> request)
			{
				return true;
			}
		};

		handler = new Handler();
		networkCheckRunner = new Runnable()
		{
			@Override
			public void run()
			{
				if (mLockUI.isVisible())
				{
					mQueue.cancelAll(cancelAllRequestFilter);
					unLockUI();
					onError();
				}
			}
		};

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		// RequestQueue에 등록된 모든 Request들을 취소한다.
		if (mQueue != null)
		{
			mQueue.cancelAll(cancelAllRequestFilter);
		}
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}

	/**
	 * 액션바를 설정하는 메서드로서, 어플리케이션 액션바 테마를 설정하고 제목을 지정한다.
	 * 
	 * @param title
	 *            액션바에 표시할 화면의 제목을 받는다.
	 */
	public Toolbar setActionBar(String title, boolean isFinish)
	{
		if (mToolbar == null)
		{
			mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			setSupportActionBar(mToolbar);

			mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_title));
			mToolbar.setBackgroundColor(getResources().getColor(R.color.white));
		}

		GlobalFont.apply(mToolbar);

		setActionBarListEnabled(false);
		//		mToolbar.setTitle(title);
		getSupportActionBar().setTitle(title);

		if (isFinish == true)
		{
			mToolbar.setNavigationIcon(R.drawable.back);

			mToolbar.setNavigationOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});
		}

		// pinkred_font
		//		GlobalFont.apply(mToolbar);

		return mToolbar;
	}

	public void setActionBar(int strId)
	{
		setActionBar(getString(strId), true);
	}

	public void setActionBar(String text)
	{
		setActionBar(text, true);
	}

	public void setActionBarRegionEnable(boolean isEnable)
	{
		if (mActionBarRegionEnabled == isEnable)
		{
			return;
		}

		mActionBarRegionEnabled = isEnable;

		if (mSpinnderIndex != -1 && mToolbar.getChildAt(mSpinnderIndex) != null)
		{
			View view = mToolbar.getChildAt(mSpinnderIndex);
			View imageView = view.findViewById(R.id.spinnerImageView);

			if (isEnable == true)
			{
				view.setEnabled(true);
				imageView.setVisibility(View.VISIBLE);
			} else
			{
				view.setEnabled(false);
				imageView.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void setActionBarListEnabled(boolean isEnable)
	{
		if (isEnable == true)
		{
			if (mSpinnderIndex == -1)
			{
				mToolbar.setTitle("");

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.view_actionbar_spinner, null, true);

				mSpinnderIndex = mToolbar.getChildCount();
				mToolbar.addView(view, mSpinnderIndex);

				mActionBarRegionEnabled = true;
			}
		} else
		{
			if (mSpinnderIndex != -1 && mToolbar.getChildAt(mSpinnderIndex) != null)
			{
				mToolbar.removeViewAt(mSpinnderIndex);
				mSpinnderIndex = -1;

				mActionBarRegionEnabled = false;
			}
		}
	}

	public void setActionBarListData(final String title, final ArrayList<String> arrayList, final UserActionListener userActionListener)
	{
		if (mSpinnderIndex == -1)
		{
			return;
		}

		View view = mToolbar.getChildAt(mSpinnderIndex);

		if (view != null)
		{
			TextView textView = (TextView) view.findViewById(R.id.titleTextView);
			textView.setText(title);

			view.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mActionBarRegionEnabled == false || isLockUiComponent() == true)
					{
						return;
					}

					lockUiComponent();

					showPopupWindow(v, arrayList, userActionListener);
				}
			});
		}
	}

	/**
	 * 액션바에 ProgressBar를 표시할 수 있도록 셋팅한다.
	 */
	//	public void setActionBarProgressBar()
	//	{
	//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	//		{
	//			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	//			setSupportProgressBarIndeterminate(true);
	//		}
	//	}

	private void setLocale(Locale locale)
	{
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

	}

	public void restartApp()
	{
		// 세션이 만료되어 재시작 요청.
		SimpleAlertDialog.build(BaseActivity.this, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_session_expired), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Util.restartApp(BaseActivity.this);
			}
		}, null).setCancelable(false).show();
	}

	private void showPopupWindow(View oTargetView, ArrayList<String> stringlist, final UserActionListener userActionListener)
	{
		if (oTargetView == null || stringlist == null)
		{
			return;
		}

		if (mPopupWindow != null && mPopupWindow.isShowing() == true)
		{
			return;
		}

		RegionPopupListView regionPopupListView = new RegionPopupListView(BaseActivity.this);
		regionPopupListView.setData(stringlist);
		regionPopupListView.setUserActionListener(new RegionPopupListView.UserActionListener()
		{

			@Override
			public void onItemClick(int position)
			{
				if (mPopupWindow != null && mPopupWindow.isShowing() == true)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}

				if (userActionListener != null)
				{
					userActionListener.onItemClick(position);
				}
			}
		});

		mPopupWindow = new PopupWindow(regionPopupListView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				releaseUiComponent();
			}
		});

		//영역이외의 터치시 팝업 윈도우를 닫히게 하기 위해서
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		//팝업 윈도우에 터치가 가능 하게 하려면 포커스를 줘야 함 
		mPopupWindow.setFocusable(true);

		mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)
		//      mPopupWindow.showAsDropDown(btn_Popup, 50, 50);

		Rect oRect = new Rect();
		oTargetView.getGlobalVisibleRect(oRect);

		/**
		 * showAtLocation(parent, gravity, x, y)
		 * 
		 * @praent : PopupWindow가 생성될 parent View 지정 View v = (View)
		 *         findViewById(R.id.btn_click)의 형태로 parent 생성
		 * @gravity : parent View의 Gravity 속성 지정 Popupwindow 위치에 영향을 줌.
		 * @x : PopupWindow를 (-x, +x) 만큼 좌,우 이동된 위치에 생성
		 * @y : PopupWindow를 (-y, +y) 만큼 상,하 이동된 위치에 생성
		 */
		mPopupWindow.showAtLocation(regionPopupListView, Gravity.NO_GRAVITY, oRect.left, oRect.top + oRect.height() / 5);
	}

	// 메뉴 버튼을 막아버림.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause()
	{
		try
		{
			CookieSyncManager.getInstance().stopSync();

		} catch (Exception e)
		{
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().stopSync();

		}

		unLockUI();
		mLockUI.close();
		mLockUI = null;

		super.onPause();
	}

	@Override
	protected void onResume()
	{
		if (mLockUI == null)
		{
			mLockUI = new LoadingDialog(this);
		}

		super.onResume();

		try
		{
			CookieSyncManager.getInstance().startSync();
		} catch (Exception e)
		{
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().startSync();
		}

		com.facebook.AppEventsLogger.activateApp(this, getString(R.string.app_id));

	}

	@Override
	protected void onStop()
	{
		// 현재 Activity에 등록된 Request를 취소한다. 
		if (mQueue != null)

			mQueue.cancelAll(new RequestQueue.RequestFilter()
			{
				@Override
				public boolean apply(Request<?> request)
				{
					Request<?> cancelRequest = (Request<?>) request;

					if (cancelRequest != null && cancelRequest.getTag() != null)
					{
						if (cancelRequest.getTag().equals(this))
						{
							return true;
						}
					}

					return false;
				}
			});

		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
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
	public void lockUI()
	{
		lockUiComponent();

		if (isFinishing() == false)
		{
			if (mLockUI == null)
			{
				mLockUI = new LoadingDialog(this);
			}

			mLockUI.show();
		}

		// 만약 제한시간이 지났는데도 리퀘스트가 끝나지 않았다면 Error 발생.
		//		handler.postDelayed(networkCheckRunner, REQUEST_EXPIRE_JUDGE);
	}

	/**
	 * 로딩이 완료되어 LoadingDialog를 제거하고 전역 폰트를 설정한다.
	 */
	@Override
	public void unLockUI()
	{
		releaseUiComponent();

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());

		if (isFinishing() == false && mLockUI != null)
		{
			mLockUI.hide();
		}

		//		handler.removeCallbacks(networkCheckRunner);
	}

	/**
	 * UI Component의 잠금 상태를 확인하는 변수..
	 * 
	 * @return
	 */
	public boolean isLockUiComponent()
	{
		synchronized (this)
		{
			return mIsLockUiComponent;
		}
	}

	public boolean isLockUiComponent(boolean lock)
	{
		synchronized (this)
		{
			if (mIsLockUiComponent == true)
			{
				return true;
			} else
			{
				mIsLockUiComponent = lock;
				return false;
			}
		}
	}

	/**
	 * UI Component를 잠금상태로 변경..
	 */
	public void lockUiComponent()
	{
		synchronized (this)
		{
			mIsLockUiComponent = true;
		}
	}

	/**
	 * UI Component를 잠금해제로 변경..
	 */
	public void releaseUiComponent()
	{
		synchronized (this)
		{
			mIsLockUiComponent = false;
		}
	}

	@Override
	protected void onDestroy()
	{
		//mLockUI.hide();
		unLockUI();
		super.onDestroy();
	}

	@Override
	public void onErrorResponse(VolleyError error)
	{
		unLockUI();

		ExLog.e(error.toString());

		onError();
	}

	public void onError(Exception error)
	{
		releaseUiComponent();

		ExLog.e(error.toString());

		if (Constants.DEBUG == true)
		{
			Log.e("DailyHotel", error.toString(), error.fillInStackTrace());
			//			DailyToast.showToast(this, error.toString(), Toast.LENGTH_LONG);
		}

		onError();
	}

	/**
	 * Error 발생 시 분기되는 메서드
	 */
	public void onError()
	{
		releaseUiComponent();

		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				// 잘못된 멘트, 모든 에러가 이쪽으로 빠지게됨. 변경 필요.
				DailyToast.showToast(BaseActivity.this, getResources().getString(R.string.act_base_network_connect), Toast.LENGTH_LONG);
			}
		});
	}

	/**
	 * Toast를 쉽게 표시해주는 메서드로서, 참조 Context로는 ApplicationContext를 사용한다. 삼성 단말기에서 삼성
	 * 테마를 사용하기 위함이다.
	 * 
	 * @param message
	 *            Toast에 표시할 내용
	 * @param length
	 *            Toast가 표시되는 시간. Toast.LENGTH_SHORT, Toast.LENGTH_LONG
	 * @param isAttachToActivity
	 *            현재 Activity가 종료되면 Toast도 제거할지를 결정한다
	 */
	//	public void showToast(String message, int length, boolean isAttachToActivity)
	//	{
	//		try
	//		{
	//			if (mToast != null)
	//				mToast.cancel();
	//
	//			if (isAttachToActivity)
	//			{
	//				mToast = Toast.makeText(getApplicationContext(), message, length);
	//				mToast.show();
	//
	//			} else
	//			{
	//				Toast.makeText(getApplicationContext(), message, length).show();
	//			}
	//		} catch (Exception e)
	//		{ // show Toast 도중 Stackoverflow가 자주 발생함. 이유를 알 수 없음. 이에따른 임시 방편 
	//			ExLog.e(e.toString());
	//		}
	//	}

	/**
	 * 버튼 난타를 방지하기 위한 메서드, 버튼의 클릭 가능 여부를 반대로 변경.
	 * 
	 * @param v
	 *            타겟 뷰
	 */
	protected void chgClickable(View v)
	{
		v.setClickable(!v.isClickable());
	}

	protected void chgClickable(View v, boolean isClickable)
	{
		v.setClickable(isClickable);
	}

}
