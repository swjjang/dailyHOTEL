package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.WakeLock;

public class GcmDialogActivity extends Activity implements OnClickListener,Constants{
	
	private Button btnOkButton;
	private Button btnCancelButton;
	private TextView tvMsg;
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_dialog_gcm);
		
		String msg = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG);
		String title = msg.substring(0, msg.indexOf("]")+1);
		String content = msg.substring(msg.indexOf("]")+1);
		
		tvTitle = (TextView) findViewById(R.id.tv_gcm_push_title);
		tvTitle.setText(title);
		
		tvMsg = (TextView) findViewById(R.id.tv_gcm_push_msg);
		tvMsg.setText(content);
		
		btnOkButton = (Button) findViewById(R.id.btn_gcm_push_ok);
		btnCancelButton = (Button) findViewById(R.id.btn_gcm_push_cancle);
		
		btnOkButton.setOnClickListener(this);
		btnCancelButton.setOnClickListener(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		WakeLock.releaseWakeLock();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btnOkButton.getId()) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(this, MainActivity.class);
	        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_INTENT_FROM_PUSH, true); // 메인엑티비티 -> 예약확인리스트 -> 최신 예약 클릭, 
			
			startActivity(intent);
			finish();
			
		} else if(v.getId() == btnCancelButton.getId()) {
			finish();
		}
	}
}
