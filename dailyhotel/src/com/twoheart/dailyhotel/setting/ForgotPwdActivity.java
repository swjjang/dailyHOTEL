package com.twoheart.dailyhotel.setting;

import static com.twoheart.dailyhotel.AppConstants.REST_URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.asynctask.GeneralHttpTask;
import com.twoheart.dailyhotel.asynctask.onCompleteListener;
import com.twoheart.dailyhotel.utils.LoadingDialog;

public class ForgotPwdActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = "ForgotPwdActivity";
	
	private Button btn_forgot;
	private EditText et_forgot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_forgot_pwd);
		
		loadResource();
		
		// setTitle
		setTitle(Html.fromHtml("<font color='#050505'>비밀번호 찾기</font>"));
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e(TAG, "Exception loading drawable"); 
		}
	}
	
	public void loadResource() {
		et_forgot = (EditText) findViewById(R.id.et_forgot_pwd);
		btn_forgot = (Button) findViewById(R.id.btn_forgot_pwd);
		btn_forgot.setOnClickListener(this);
		et_forgot.setId(EditorInfo.IME_ACTION_DONE);
		et_forgot.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId) {
                case EditorInfo.IME_ACTION_DONE:
                	btn_forgot.performClick();
                    break;
                }
				return false;
			}
		});
	}
	
	// Jason | Fix send email api
	@Override
	public void onClick(View v) {

		if(v.getId() == btn_forgot.getId()) {
			
			String strEmail = et_forgot.getText().toString();
			
			if(strEmail.equals("")) {
				Toast.makeText(this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!isValidEmail(strEmail)) {
				Toast.makeText(this, "올바른 이메일 형식을 입력해주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			LoadingDialog.showLoading(this);
			
			new GeneralHttpTask(forgotListener, getApplicationContext()).execute(REST_URL + "user/sendpw/" + strEmail + "/trim");
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
	
	protected onCompleteListener forgotListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			LoadingDialog.hideLoading();
			AlertDialog.Builder alert = new AlertDialog.Builder(ForgotPwdActivity.this);
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();     //닫기
			    }
			});
			
			// Jason | Clear input form
			et_forgot.setText("");
			
			alert.setMessage("이메일이 발송되었습니다.");
			alert.show();
		}
	};
}
