package com.twoheart.dailyhotel.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class ForgotPwdActivity extends BaseActivity implements Constants,
		DailyHotelResponseListener, ErrorListener, OnClickListener {

	private static final String TAG = "ForgotPwdActivity";

	private RequestQueue mQueue;

	private Button btnForgot;
	private EditText etForgot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("비밀번호 찾기");
		setContentView(R.layout.activity_forgot_pwd);

		mQueue = VolleyHttpClient.getRequestQueue();

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
				Toast.makeText(this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (!isValidEmail(strEmail)) {
				Toast.makeText(this, "올바른 이메일 형식을 입력해주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			LoadingDialog.showLoading(this);

			mQueue.add(new DailyHotelRequest(Method.GET, new StringBuilder(
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
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
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
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_FORGOTPWD)) {

			if (response.equals("done")) {
				LoadingDialog.hideLoading();
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ForgotPwdActivity.this);
				alert.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss(); // 닫기
							}
						});

				// Jason | Clear input form
				etForgot.setText("");

				alert.setMessage("이메일이 발송되었습니다.");
				alert.show();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		LoadingDialog.hideLoading();
		Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
				Toast.LENGTH_SHORT).show();

	}
}
