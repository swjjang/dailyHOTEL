package com.twoheart.dailyhotel.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ForgotPwdActivity extends BaseActivity implements Constants,
		DailyHotelStringResponseListener, OnClickListener {

	private static final String TAG = "ForgotPwdActivity";

	private Button btnForgot;
	private EditText etForgot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_forgot_pwd_activity);
		setContentView(R.layout.activity_forgot_pwd);

		etForgot = (EditText) findViewById(R.id.et_forgot_pwd);
		btnForgot = (Button) findViewById(R.id.btn_forgot_pwd);
		btnForgot.setOnClickListener(this);
		etForgot.setId(EditorInfo.IME_ACTION_DONE);
		etForgot.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
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
	public void onClick(View v) {

		if (v.getId() == btnForgot.getId()) {

			String strEmail = etForgot.getText().toString();

			if (strEmail.equals("")) {
				showToast(getString(R.string.toast_msg_input_email_address), Toast.LENGTH_SHORT, true);
				return;
			} 
			
			else if (!isValidEmail(strEmail)) {
				showToast(getString(R.string.toast_msg_wrong_email_address), Toast.LENGTH_SHORT, true);
				return;
			}

			lockUI();
			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_FORGOTPWD)
					.append(strEmail).append("/trim").toString(), null, this,
					this));

		}
	}

	public boolean isValidEmail(String inputStr) {
		Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_FORGOTPWD)) {

			if (response.trim().equals("done")) {
				unLockUI();
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ForgotPwdActivity.this);
				
				alert.setPositiveButton("»Æ¿Œ",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss(); // ¥›±‚
							}
						});

				// Jason | Clear input form
				etForgot.setText("");

				alert.setMessage(getString(R.string.dialog_msg_sent_email));
				alert.show();
			}
		}
	}
}
