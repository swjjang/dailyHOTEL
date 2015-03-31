package com.twoheart.dailyhotel.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ForgotPwdActivity extends BaseActivity implements Constants, OnClickListener
{

	private Button btnForgot;
	private EditText etForgot;

	private String mEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_forgot_pwd_activity);
		setContentView(R.layout.activity_forgot_pwd);

		etForgot = (EditText) findViewById(R.id.et_forgot_pwd);
		btnForgot = (Button) findViewById(R.id.btn_forgot_pwd);
		btnForgot.setOnClickListener(this);
		etForgot.setId(EditorInfo.IME_ACTION_DONE);
		etForgot.setOnEditorActionListener(new OnEditorActionListener()
		{

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				switch (actionId)
				{
					case EditorInfo.IME_ACTION_DONE:
						btnForgot.performClick();
						break;
				}
				return false;
			}
		});

	}

	// Jason | Fix send email api
	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnForgot.getId())
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			mEmail = etForgot.getText().toString().trim();

			if (mEmail.equals(""))
			{
				showToast(getString(R.string.toast_msg_please_input_email_address), Toast.LENGTH_SHORT, true);
				return;
			}

			else if (!isValidEmail(mEmail))
			{
				showToast(getString(R.string.toast_msg_wrong_email_address), Toast.LENGTH_SHORT, true);
				return;
			}

			lockUI();

			Map<String, String> params = new HashMap<String, String>();

			params.put("userEmail", mEmail);

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_CHECK_EMAIL).toString(), params, mUserCheckEmailJsonResponseListener, this));
		}
	}

	public boolean isValidEmail(String inputStr)
	{
		Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mUserCheckEmailJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				String result = null;

				if (response != null)
				{
					result = response.getString("isSuccess");
				}

				if ("true".equalsIgnoreCase(result) == true)
				{
					if (TextUtils.isEmpty(mEmail) == true)
					{
						showToast(getString(R.string.toast_msg_please_input_email_address), Toast.LENGTH_SHORT, true);
					} else
					{
						Map<String, String> params = new HashMap<String, String>();
						params.put("userEmail", mEmail);

						mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_CHANGE_PW).toString(), params, mUserChangePwJsonResponseListener, ForgotPwdActivity.this));
					}
				} else
				{
					unLockUI();
					releaseUiComponent();

					String message = response.getString("msg");
					SimpleAlertDialog.build(ForgotPwdActivity.this, message, getString(R.string.dialog_btn_text_confirm), null).show();
				}
			} catch (JSONException e)
			{
				onError(e);
				unLockUI();
				releaseUiComponent();
			}
		}
	};

	private DailyHotelJsonResponseListener mUserChangePwJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				String result = null;

				if (response != null)
				{
					result = response.getString("isSuccess");
				}

				if ("true".equalsIgnoreCase(result) == true)
				{
					SimpleAlertDialog.build(ForgotPwdActivity.this, getString(R.string.dialog_msg_sent_email), getString(R.string.dialog_btn_text_confirm), null).show();
					etForgot.setText("");
				} else
				{
					String message = response.getString("msg");
					SimpleAlertDialog.build(ForgotPwdActivity.this, message, getString(R.string.dialog_btn_text_confirm), null).show();
				}
			} catch (JSONException e)
			{
				onError(e);
			} finally
			{
				unLockUI();
				releaseUiComponent();
			}

		}
	};

	//	@Override
	//	public void onResponse(String url, String response) {
	//		if (url.contains(URL_WEBAPI_USER_FORGOTPWD)) {
	//
	//			if (response.trim().equals("done")) {
	//				unLockUI();
	//				SimpleAlertDialog.build(this, getString(R.string.dialog_msg_sent_email), getString(R.string.dialog_btn_text_confirm), null).show();
	//				etForgot.setText("");
	//			}
	//		}
	//	}

}
