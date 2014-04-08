package com.twoheart.dailyhotel.activity;

import java.util.List;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.id;
import com.twoheart.dailyhotel.R.layout;
import com.twoheart.dailyhotel.util.WakeLock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class PushDialogActivity extends Activity implements OnClickListener{
	
	private TextView tvOkButton;
	private TextView tvCancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_dialog);
		
		tvOkButton = (TextView) findViewById(R.id.tv_push_ok);
		tvCancelButton = (TextView) findViewById(R.id.tv_push_cancle);
		tvOkButton.setOnClickListener(this);
		tvCancelButton.setOnClickListener(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		WakeLock.releaseWakeLock();
		
//		Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				finish();
//			}
//		};
//		
//		handler.sendEmptyMessageDelayed(0, 5000);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == tvOkButton.getId()) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(this, MainActivity.class);
			
			startActivity(intent);
			finish();
			
		} else if(v.getId() == tvCancelButton.getId()) {
			finish();
		}
	}
}
