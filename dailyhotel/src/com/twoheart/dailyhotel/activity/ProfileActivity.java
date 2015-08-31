/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * ProfileActivity (프로필 화면)
 * 
 * 로그인되어 있는 상태에서 프로필 정보를 보여주는 화면
 * 이름이나 연락처를 수정할 수 있고, 로그아웃할 수 있는 화면이다.  
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.androidquery.AQuery;
import com.facebook.Session;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ProfileActivity extends BaseActivity implements OnClickListener
{
	private final String INVALID_NULL = "null";

	private AQuery mAq;
	private InputMethodManager mInputMethodManager;
	private String prevName;
	private String prevPh;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile);
		setActionBar(R.string.actionbar_title_profile_activity);

		mAq = new AQuery(this);
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// 수정시에 인터페이스 편의를 위해 [사용자 정보] 바를 터치하면 완료되도록 수정.
		findViewById(R.id.profileSectionBarLayout).setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (mAq.id(R.id.tv_profile_edit).getText().equals(getString(R.string.dialog_btn_text_confirm)))
				{
					mAq.id(R.id.ll_profile_edit).click();
					return true;
				}

				return false;
			}
		});

		mAq.id(R.id.ll_profile_edit).clicked(this);
		mAq.id(R.id.btn_profile_logout).clicked(this);

		mAq.id(R.id.et_profile_phone).getEditText().setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				switch (actionId)
				{
					case EditorInfo.IME_ACTION_DONE:
						mAq.id(R.id.ll_profile_edit).click();
						break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.PROFILE);
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		updateTextField();
	}

	@Override
	protected void onPause()
	{
		toggleKeyboard(false);

		super.onPause();
	}

	/**
	 * 수정중인 상태에서 백버튼을 누른경우에 수정 취소 => 바꾸기 전 상태로 돌아감
	 */
	@Override
	public void onBackPressed()
	{
		if (mAq.id(R.id.tv_profile_edit).getText().equals(getString(R.string.dialog_btn_text_confirm)))
		{

			mAq.id(R.id.ll_profile_info_editable).visibility(View.GONE);
			mAq.id(R.id.ll_profile_info_label).visibility(View.VISIBLE);
			mAq.id(R.id.ll_profile_info_label).getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
			mAq.id(R.id.tv_profile_edit).text(getString(R.string.act_profile_modify));

			mAq.id(R.id.et_profile_name).text(prevName);
			mAq.id(R.id.et_profile_phone).text(prevPh);

			toggleKeyboard(false);
		} else
		{
			super.onBackPressed();
		}

	}

	public void setupUI(View view)
	{

		if (view.getId() == R.id.ll_profile_edit)
		{
			return;
		}

		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText))
		{
			view.setOnTouchListener(new OnTouchListener()
			{
				public boolean onTouch(View v, MotionEvent event)
				{
					if (mAq.id(R.id.tv_profile_edit).getText().equals(getString(R.string.dialog_btn_text_confirm)))
					{
						mAq.id(R.id.ll_profile_edit).click();
						return true;
					}
					return false;
				}

			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup)
		{
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
			{
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}

	private void toggleKeyboard(boolean show)
	{
		if (getWindow() == null || getWindow().getDecorView() == null || getWindow().getDecorView().getWindowToken() == null)
		{
			return;
		}

		if (show)
		{
			mAq.id(R.id.et_profile_name).getEditText().requestFocus();

			StringFilter stringFilter = new StringFilter(ProfileActivity.this);
			InputFilter[] allowAlphanumericHangul = new InputFilter[1];
			allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

			((EditText) findViewById(R.id.et_profile_name)).setFilters(allowAlphanumericHangul);

			mInputMethodManager.showSoftInput(mAq.id(R.id.et_profile_name).getEditText(), InputMethodManager.SHOW_FORCED);

		} else
		{
			mInputMethodManager.hideSoftInputFromWindow(mAq.id(R.id.et_profile_name).getEditText().getWindowToken(), 0);

		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.ll_profile_edit)
		{
			if (mAq.id(R.id.tv_profile_edit).getText().equals(getString(R.string.act_profile_modify)))
			{
				mAq.id(R.id.ll_profile_info_label).visibility(View.GONE);
				mAq.id(R.id.ll_profile_info_editable).visibility(View.VISIBLE);
				mAq.id(R.id.ll_profile_info_editable).getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
				mAq.id(R.id.tv_profile_edit).text(getString(R.string.dialog_btn_text_confirm));

				toggleKeyboard(true);

			} else if (mAq.id(R.id.tv_profile_edit).getText().equals(getString(R.string.dialog_btn_text_confirm)))
			{
				if (isLockUiComponent() == true)
				{
					return;
				}

				lockUiComponent();

				String name = mAq.id(R.id.et_profile_name).getText().toString().trim();
				String phone = mAq.id(R.id.et_profile_phone).getText().toString().trim();

				// 전화번호는 필수 사항이 아니라서 필드만 초기화된다.
				if (TextUtils.isEmpty(phone) == true)
				{
					mAq.id(R.id.et_profile_phone).text("");
				}

				// 이름은 필수 사항으로 입력되어야 한다.
				if (TextUtils.isEmpty(name) == true)
				{
					releaseUiComponent();

					mAq.id(R.id.et_profile_name).text("");
					DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_please_input_name, Toast.LENGTH_SHORT);
				} else if (name.equals(prevName) && phone.equals(prevPh))
				{
					toggleKeyboard(false);

					releaseUiComponent();

					// 기존과 동일하여 서버에 요청할 필요가 없음.
					mAq.id(R.id.ll_profile_info_editable).visibility(View.GONE);
					mAq.id(R.id.ll_profile_info_label).visibility(View.VISIBLE);
					mAq.id(R.id.ll_profile_info_label).getView().startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_in));
					mAq.id(R.id.tv_profile_edit).text(getString(R.string.act_profile_modify));

					DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_profile_not_changed, Toast.LENGTH_LONG);
				} else
				{
					toggleKeyboard(false);

					Map<String, String> updateParams = new HashMap<String, String>();
					updateParams.put("name", name);
					updateParams.put("phone", phone);

					lockUI();
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_UPDATE).toString(), updateParams, mUserUpdateJsonResponseListener, this));
				}
			}
		} else if (v.getId() == R.id.btn_profile_logout)
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			/**
			 * 로그 아웃시 내부 저장한 유저정보 초기화
			 */
			View.OnClickListener posListener = new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGOUT).toString(), null, mUserLogoutStringResponseListener, ProfileActivity.this));
					AnalyticsManager.getInstance(ProfileActivity.this).recordEvent(Screen.PROFILE, Action.CLICK, Label.LOGOUT, 0L);
				}
			};

			showSimpleDialog(null, getString(R.string.dialog_msg_chk_wanna_login), getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel), posListener, null, false);

			releaseUiComponent();
		}
	}

	private void updateTextField()
	{
		lockUI();

		// 사용자 정보 요청.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserLogInfoJsonResponseListener, this));
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				String result = response.getString("success");
				String msg = null;

				if (response.length() > 1)
				{
					msg = response.getString("msg");
				}

				if (result.equals("true") == true)
				{
					unLockUI();
					DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
					updateTextField();
				} else
				{
					unLockUI();
					DailyToast.showToast(ProfileActivity.this, msg, Toast.LENGTH_LONG);
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserLogInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				String userEmail = response.getString("email");
				String userName = response.getString("name");
				String userPhone = response.getString("phone");

				if (TextUtils.isEmpty(userEmail) == true || INVALID_NULL.equalsIgnoreCase(userEmail) == true)
				{
					userEmail = getString(R.string.act_profile_input_email);
				}

				if (TextUtils.isEmpty(userName) == true)
				{
					userName = getString(R.string.act_profile_input_name);
					prevName = "";
				} else
				{
					prevName = userName;
				}

				if (TextUtils.isEmpty(userPhone) == true || INVALID_NULL.equalsIgnoreCase(userPhone) == true)
				{
					userPhone = getString(R.string.act_profile_input_contact);
					prevPh = "";
				} else
				{
					prevPh = userPhone;
				}

				mAq.id(R.id.tv_profile_email).text(userEmail);
				mAq.id(R.id.tv_profile_name).text(userName);
				mAq.id(R.id.tv_profile_phone).text(userPhone);

				mAq.id(R.id.et_profile_name).text(prevName);
				mAq.id(R.id.et_profile_phone).text(prevPh);

				mAq.id(R.id.ll_profile_info_editable).visibility(View.GONE);
				mAq.id(R.id.ll_profile_info_label).visibility(View.VISIBLE);
				mAq.id(R.id.ll_profile_info_label).getView().startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_in));
				mAq.id(R.id.tv_profile_edit).text(getString(R.string.act_profile_modify));
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelStringResponseListener mUserLogoutStringResponseListener = new DailyHotelStringResponseListener()
	{
		@Override
		public void onResponse(String url, String response)
		{
			VolleyHttpClient.destroyCookie();

			SharedPreferences.Editor ed = sharedPreference.edit();
			//			ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
			//			ed.putString(KEY_PREFERENCE_USER_ID, null);
			//			ed.putString(KEY_PREFERENCE_USER_PWD, null);
			//			ed.putString(KEY_PREFERENCE_GCM_ID, null);

			ed.clear();
			ed.commit();

			if (Session.getActiveSession() != null)
			{
				if (Session.getActiveSession().isOpened())
				{
					Session.getActiveSession().closeAndClearTokenInformation();
					Session.setActiveSession(null);
				}
			}

			DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_logouted, Toast.LENGTH_SHORT);
			finish();
		}
	};

	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_USER_INFO)) {
	//			try {
	//				JSONObject obj = response;
	//
	//				String userEmail = obj.getString("email");
	//				String userName = obj.getString("name");
	//				String userPhone = obj.getString("phone");
	//
	//				prevName = userName;
	//				prevPh = userPhone;
	//
	//				mAq.id(R.id.tv_profile_email).text(userEmail);
	//				mAq.id(R.id.tv_profile_name).text(userName);
	//				mAq.id(R.id.tv_profile_phone).text(userPhone);
	//
	//				mAq.id(R.id.et_profile_name).text(userName);
	//				mAq.id(R.id.et_profile_phone).text(userPhone);
	//
	//				unLockUI();
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_WEBAPI_USER_UPDATE)) {
	//			try {
	//				JSONObject obj = response;
	//
	//				String result = obj.getString("success");
	//				String msg = null;
	//
	//				if (obj.length() > 1)
	//					msg = obj.getString("msg");
	//
	//				if (result.equals("true")) {
	//					unLockUI();
	//					showToast(getString(R.string.toast_msg_profile_success_to_change), Toast.LENGTH_SHORT, true);
	//					updateTextField();
	//				} else {
	//					unLockUI();
	//					showToast(msg, Toast.LENGTH_LONG, true);
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//	}
}
