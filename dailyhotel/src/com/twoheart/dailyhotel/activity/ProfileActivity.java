package com.twoheart.dailyhotel.activity;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.facebook.Session;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class ProfileActivity extends BaseActivity implements
		DailyHotelJsonResponseListener, OnClickListener {

	// private TextView tvEmail, tvName, tvPhone, tvProfileEdit;
	// private EditText etEmail, etName, etPhone;
	// private LinearLayout llProfileEdit, llProfileInfoLabel,
	// llProfileInfoEditable;

	private AQuery mAq;
	private InputMethodManager mInputMethodManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("프로필");
		setContentView(R.layout.activity_profile);

		mAq = new AQuery(this);
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mAq.id(R.id.ll_profile_edit).clicked(this);
		mAq.id(R.id.btn_profile_logout).clicked(this);

		mAq.id(R.id.et_profile_phone).getEditText()
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						switch (actionId) {
						case EditorInfo.IME_ACTION_DONE:
							mAq.id(R.id.ll_profile_edit).click();
							break;
						}
						return true;
					}
				});
		
//		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
//		
//		mAq.id(R.id.ll_profile_info_label).getView().startAnimation(fadeInAnimation);
//		mAq.id(R.id.ll_profile_info_editable).getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
		
		
	}

	@Override
	public void onResume() {
		super.onResume();

		lockUI();
		// 사용자 정보 요청.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(),
				null, this, this));

	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ll_profile_edit) {
			if (mAq.id(R.id.tv_profile_edit).getText().equals("수정")) {
				mAq.id(R.id.ll_profile_info_label).visibility(View.GONE);
				mAq.id(R.id.ll_profile_info_editable).visibility(View.VISIBLE);
				mAq.id(R.id.ll_profile_info_editable).getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
				mAq.id(R.id.tv_profile_edit).text("완료");

				mAq.id(R.id.et_profile_name).getEditText().requestFocus();

			} else if (mAq.id(R.id.tv_profile_edit).getText().equals("완료")) {
				mAq.id(R.id.ll_profile_info_editable).visibility(View.GONE);
				mAq.id(R.id.ll_profile_info_label).visibility(View.VISIBLE);
				mAq.id(R.id.ll_profile_info_label).getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
				mAq.id(R.id.tv_profile_edit).text("수정");

				mInputMethodManager.hideSoftInputFromWindow(
						mAq.id(R.id.et_profile_name).getEditText()
								.getWindowToken(), 0);

			}

		} else if (v.getId() == R.id.btn_profile_logout) {
			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
			alert_confirm
					.setMessage("로그아웃하시겠습니까?")
					.setCancelable(false)
					.setPositiveButton("로그아웃",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									mQueue.add(new DailyHotelJsonRequest(
											Method.GET,
											new StringBuilder(
													URL_DAILYHOTEL_SERVER)
													.append(URL_WEBAPI_USER_LOGOUT)
													.toString(), null,
											ProfileActivity.this,
											ProfileActivity.this));
									VolleyHttpClient.destroyCookie();

									SharedPreferences.Editor ed = sharedPreference
											.edit();
									ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN,
											false);
									ed.putString(KEY_PREFERENCE_USER_ID, null);
									ed.putString(KEY_PREFERENCE_USER_PWD, null);
									ed.commit();

									if (Session.getActiveSession() != null)
										if (Session.getActiveSession()
												.isOpened()) {
											Session.getActiveSession()
													.closeAndClearTokenInformation();
											Session.setActiveSession(null);
										}
									
									showToast("로그아웃되었습니다", Toast.LENGTH_SHORT, true);
									finish();

								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							});
			AlertDialog alert = alert_confirm.create();
			alert.show();

		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				JSONObject obj = response;

				String userEmail = obj.getString("email");
				String userName = obj.getString("name");
				String userPhone = obj.getString("phone");

				mAq.id(R.id.tv_profile_email).text(userEmail);
				mAq.id(R.id.tv_profile_name).text(userName);
				mAq.id(R.id.tv_profile_phone).text(userPhone);

				mAq.id(R.id.et_profile_name).text(userName);
				mAq.id(R.id.et_profile_phone).text(userPhone);

				unLockUI();
			} catch (Exception e) {
				onError(e);
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);

	}

}
