package com.twoheart.dailyhotel;

import java.util.List;

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
import android.view.WindowManager;
import android.widget.TextView;

public class PushDialogActivity extends Activity implements OnClickListener{
	
	private TextView tv_ok;
	private TextView tv_cancle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_push_dialog);
		
		loadResource();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				finish();
			}
		};
		
		handler.sendEmptyMessageDelayed(0, 5000);
	}
	
	public void loadResource() {
		tv_ok = (TextView) findViewById(R.id.tv_push_ok);
		tv_cancle = (TextView) findViewById(R.id.tv_push_cancle);
		tv_ok.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == tv_ok.getId()) {
			
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
			
		} else if(v.getId() == tv_cancle.getId()) {
			finish();
		}
	}
}
