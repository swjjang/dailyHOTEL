package com.twoheart.dailyhotel.activity;

import java.util.List;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.id;
import com.twoheart.dailyhotel.R.layout;

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
			
			boolean isRunning = false;
			
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> proceses = am.getRunningAppProcesses();
			
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
				                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
				                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			for(RunningAppProcessInfo process : proceses) {
				if(process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					if(process.processName.equals("com.twoheart.dailyhotel")) {
						
						intent.setClass(this, MainActivity.class);
						
						isRunning = true;
						
						break;
					} 
					
				} else if(process.processName.equals("com.twoheart.dailyhotel")) {
					intent.setClass(this, MainActivity.class);					
					isRunning = true;
					
					break;
				}
			}
			
			if(!isRunning) {
				intent.setClass(this, MainActivity.class);
			}
			
			startActivity(intent);
			finish();
			
		} else if(v.getId() == tvCancelButton.getId()) {
			finish();
		}
	}
}
