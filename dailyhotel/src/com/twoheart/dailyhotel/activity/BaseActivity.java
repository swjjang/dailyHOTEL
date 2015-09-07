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
package com.twoheart.dailyhotel.activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelMainFragment;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoadingDialog;
import com.twoheart.dailyhotel.view.OnLoadListener;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends
		ActionBarActivity implements Constants, OnLoadListener, ErrorListener
{
	protected static int mStatusBarHeight;
	private Toolbar mToolbar;
	public SharedPreferences sharedPreference;
	private Dialog mDialog;

	protected RequestQueue mQueue;

	private LoadingDialog mLockUI;

	private RequestFilter cancelAllRequestFilter;

	private Handler handler;

	protected Runnable networkCheckRunner;
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
	protected void onSaveInstanceState(Bundle outState)
	{
		try
		{
			super.onSaveInstanceState(outState);
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	@Override
	public void onBackPressed()
	{
		try
		{
			super.onBackPressed();
		} catch (Exception e)
		{
			ExLog.d(e.toString());

			finish();
		} finally
		{
			// RequestQueue에 등록된 모든 Request들을 취소한다.
			if (mQueue != null)
			{
				mQueue.cancelAll(cancelAllRequestFilter);
			}
		}
	}

	@Override
	public void setContentView(int layoutResID)
	{
		try
		{
			super.setContentView(layoutResID);
		} catch (OutOfMemoryError errror)
		{
			Util.finishOutOfMemory(BaseActivity.this);
		}

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

		FontManager.apply(mToolbar, FontManager.getInstance(getApplicationContext()).getRegularTypeface());

		setActionBarAreaEnabled(false);
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

	public void setActionBarBackgroundVisible(boolean visible)
	{
		if (mToolbar == null)
		{
			return;
		}

		View view = findViewById(R.id.toolbar_actionbarUnderLine);

		if (visible == true)
		{
			if (view.getVisibility() != View.VISIBLE)
			{
				mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_title));
				mToolbar.setBackgroundColor(getResources().getColor(R.color.white));

				view.setVisibility(View.VISIBLE);
			}
		} else
		{
			if (view.getVisibility() != View.INVISIBLE)
			{
				mToolbar.setTitleTextColor(android.R.color.transparent);
				mToolbar.setBackgroundColor(android.R.color.transparent);

				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void setActionBar(int strId)
	{
		setActionBar(getString(strId), true);
	}

	public Toolbar setActionBar(String text)
	{
		return setActionBar(text, true);
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

	public void setActionBarAreaEnabled(boolean isEnable)
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

	public void setActionBarArea(String title, final HotelMainFragment.OnUserActionListener listener)
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

					// 지역표시를 선택할 경우.
					if (listener != null)
					{
						listener.onClickActionBarArea();
					}
				}
			});
		}
	}

	public void setActionBarArea(String title, final PlaceMainFragment.OnUserActionListener listener)
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

					// 지역표시를 선택할 경우.
					if (listener != null)
					{
						listener.onClickActionBarArea();
					}
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

	//	private void setLocale(Locale locale)
	//	{
	//		Locale.setDefault(locale);
	//		Configuration config = new Configuration();
	//		config.locale = locale;
	//		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
	//
	//	}

	public void restartApp()
	{
		if (isFinishing() == true)
		{
			return;
		}

		// 세션이 만료되어 재시작 요청.
		showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_session_expired), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Util.restartApp(BaseActivity.this);
			}
		}, null, false);
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
			VolleyHttpClient.cookieManagerStopSync();

			unLockUI();

			super.onPause();
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	@Override
	protected void onResume()
	{
		if (mLockUI == null)
		{
			mLockUI = new LoadingDialog(this);
		}

		super.onResume();

		VolleyHttpClient.cookieManagerStartSync();

		com.facebook.AppEventsLogger.activateApp(this, getString(R.string.app_id));

	}

	@Override
	protected void onStop()
	{
		// 현재 Activity에 등록된 Request를 취소한다. 
		if (mQueue != null)
		{
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
		}

		if (mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
			mDialog = null;
		}

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
		releaseUiComponent();

		if (mLockUI != null)
		{
			mLockUI.close();
		}

		recursiveRecycle(getWindow().getDecorView());

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
	 * 기본적으로 내부오류가 발생하였을 경우 사용
	 */
	protected void onInternalError()
	{
		showSimpleDialog(null, getString(R.string.dialog_msg_internal_error), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		}, null, false);
	}

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

	private void recursiveRecycle(View root)
	{
		if (root == null)
		{
			return;
		}

		if (Util.isOverAPI16())
		{
			root.setBackground(null);
		} else
		{
			root.setBackgroundDrawable(null);
		}

		if (root instanceof ViewGroup)
		{
			ViewGroup group = (ViewGroup) root;
			int count = group.getChildCount();

			for (int i = 0; i < count; i++)
			{
				recursiveRecycle(group.getChildAt(i));
			}

			if (!(root instanceof AdapterView))
			{
				group.removeAllViews();
			}
		}

		if (root instanceof ImageView)
		{
			((ImageView) root).setImageDrawable(null);
		}

		root = null;
		return;
	}

	public Dialog createSimpleDialog(String titleText, String message, String positive, String negative, final View.OnClickListener positiveListener, final View.OnClickListener negativeListener)
	{
		if (mDialog != null)
		{
			if (mDialog.isShowing())
			{
				mDialog.dismiss();
			}

			mDialog = null;
		}

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

		mDialog = new Dialog(this);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setCanceledOnTouchOutside(false);

		// 상단
		TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
		titleTextView.setVisibility(View.VISIBLE);

		if (Util.isTextEmpty(titleText) == true)
		{
			titleTextView.setText(getString(R.string.dialog_notice2));
		} else
		{
			titleTextView.setText(titleText);
		}

		// 메시지
		TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
		messageTextView.setText(message);
		messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

		// 버튼
		View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
		View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
		View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

		if (Util.isTextEmpty(positive) == false && Util.isTextEmpty(negative) == false)
		{
			twoButtonLayout.setVisibility(View.VISIBLE);
			oneButtonLayout.setVisibility(View.GONE);

			TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
			TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

			negativeTextView.setText(negative);
			negativeTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (negativeListener != null)
					{
						negativeListener.onClick(v);
					}
				}
			});

			positiveTextView.setText(positive);
			positiveTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (positiveListener != null)
					{
						positiveListener.onClick(v);
					}
				}
			});
		} else
		{
			twoButtonLayout.setVisibility(View.GONE);
			oneButtonLayout.setVisibility(View.VISIBLE);

			TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

			confirmTextView.setText(positive);
			confirmTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (positiveListener != null)
					{
						positiveListener.onClick(v);
					}
				}
			});
		}

		mDialog.setContentView(dialogView);

		return mDialog;
	}

	public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener)
	{
		showSimpleDialog(title, msg, positive, null, positiveListener, null);
	}

	public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener, DialogInterface.OnCancelListener cancelListener)
	{
		showSimpleDialog(title, msg, positive, null, positiveListener, null, cancelListener, null, true);
	}

	public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener, DialogInterface.OnDismissListener dismissListener)
	{
		showSimpleDialog(title, msg, positive, null, positiveListener, null, null, dismissListener, true);
	}

	public void showSimpleDialog(String title, String msg, String positive, String negative, View.OnClickListener positiveListener, View.OnClickListener negativeListener)
	{
		showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, true);
	}

	public void showSimpleDialog(String title, String msg, String positive, String negative, View.OnClickListener positiveListener, View.OnClickListener negativeListener, boolean isCancelable)
	{
		showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, isCancelable);
	}

	public void showSimpleDialog(String titleText, String msg, String positive, String negative, final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
	DialogInterface.OnDismissListener dismissListener, //
	boolean isCancelable)
	{
		if (isFinishing())
		{
			return;
		}

		if (mDialog != null)
		{
			if (mDialog.isShowing())
			{
				mDialog.dismiss();
			}

			mDialog = null;
		}

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

		mDialog = new Dialog(this);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setCanceledOnTouchOutside(false);

		// 상단
		TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
		titleTextView.setVisibility(View.VISIBLE);

		if (Util.isTextEmpty(titleText) == true)
		{
			titleTextView.setText(getString(R.string.dialog_notice2));
		} else
		{
			titleTextView.setText(titleText);
		}

		// 메시지
		TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
		messageTextView.setText(msg);
		messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

		// 버튼
		View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
		View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
		View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

		if (Util.isTextEmpty(positive) == false && Util.isTextEmpty(negative) == false)
		{
			twoButtonLayout.setVisibility(View.VISIBLE);
			oneButtonLayout.setVisibility(View.GONE);

			TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
			TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

			negativeTextView.setText(negative);
			negativeTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (negativeListener != null)
					{
						negativeListener.onClick(v);
					}
				}
			});

			positiveTextView.setText(positive);
			positiveTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (positiveListener != null)
					{
						positiveListener.onClick(v);
					}
				}
			});
		} else
		{
			twoButtonLayout.setVisibility(View.GONE);
			oneButtonLayout.setVisibility(View.VISIBLE);

			TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

			confirmTextView.setText(positive);
			confirmTextView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mDialog != null && mDialog.isShowing())
					{
						mDialog.dismiss();
					}

					if (positiveListener != null)
					{
						positiveListener.onClick(v);
					}
				}
			});
		}

		if (cancelListener != null)
		{
			mDialog.setOnCancelListener(cancelListener);
		}

		if (dismissListener != null)
		{
			mDialog.setOnDismissListener(dismissListener);
		}

		mDialog.setCancelable(isCancelable);

		try
		{
			mDialog.setContentView(dialogView);
			mDialog.show();
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}
}
