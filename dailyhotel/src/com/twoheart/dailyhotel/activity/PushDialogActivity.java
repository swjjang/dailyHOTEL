package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.WakeLock;

public class PushDialogActivity extends Activity implements OnClickListener{
	
	private Button btnOkButton;
	private Button btnCancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_dialog);
		
		btnOkButton = (Button) findViewById(R.id.btn_push_ok);
		btnCancelButton = (Button) findViewById(R.id.btn_push_cancle);
		
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
			
			startActivity(intent);
			finish();
			
		} else if(v.getId() == btnCancelButton.getId()) {
			android.util.Log.e("FINISH!", "true");
			finish();
		}
	}
}
