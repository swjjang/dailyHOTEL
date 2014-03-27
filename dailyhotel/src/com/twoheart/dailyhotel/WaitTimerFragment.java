package com.twoheart.dailyhotel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.obj.SaleTime;
import com.twoheart.dailyhotel.util.Constants;

@SuppressLint("ValidFragment")
public class WaitTimerFragment extends Fragment implements OnClickListener, Constants {

	private final static String TAG = "WaitTimerFragment";

	private MainActivity mHostActivity;

	private Handler mHandler;
	private TextView tvTimer;
	private Button btnNotify;

	private AlarmManager alarmManager;
	private PendingIntent pender;
	private Intent intent;
	private SaleTime mSaleTime;
	private boolean isEnableNotify;

	private long remainingTime;

	public WaitTimerFragment() {
		super();
	}

	public WaitTimerFragment(SaleTime saleTime) {
		mSaleTime = saleTime;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wait_timer, null);
		mHostActivity = (MainActivity) getActivity();
		alarmManager = (AlarmManager) mHostActivity.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(mHostActivity.getApplicationContext(),
				AlarmBroadcastReceiver.class);
		pender = PendingIntent.getBroadcast(mHostActivity.getApplicationContext(), 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		tvTimer = (TextView) view.findViewById(R.id.tv_timer);
		btnNotify = (Button) view.findViewById(R.id.btn_wait_timer_alram);
		btnNotify.setOnClickListener(this);

		mHostActivity.setActionBar("dailyHOTEL");
		setTimer();

		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnNotify.getId()) {
			isEnableNotify = !isEnableNotify;
			setNotify(isEnableNotify);
		}
	}

	private void setNotify(boolean enable) {
		if (enable) {
			btnNotify.setText("알람 끄기");
			alarmManager.set(AlarmManager.RTC_WAKEUP, mSaleTime.getOpenTime(),
					pender);

			Toast.makeText(mHostActivity, "알람이 등록되었습니다", Toast.LENGTH_SHORT)
					.show();

		} else {
			btnNotify.setText("알람 켜기");
			alarmManager.cancel(pender);

			Toast.makeText(mHostActivity, "알람이 취소되었습니다", Toast.LENGTH_SHORT)
					.show();

		}

	}

	private void setTimer() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				
				Date currentSystemDate = new Date(System
						.currentTimeMillis());
				
				Date dailyOpenDate = new Date(mSaleTime.getOpenTime());
				
				remainingTime = dailyOpenDate.getTime() - currentSystemDate.getTime();
				
				if (remainingTime > 0) {
					SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm:ss");
					displayTimeFormat.setTimeZone(TimeZone.getTimeZone("KST"));

					tvTimer.setText(displayTimeFormat.format(remainingTime));
					
					this.sendEmptyMessageDelayed(0, 1);
					
				} else {
					mHandler.removeMessages(0);
					mHostActivity
							.replaceFragment(mHostActivity
									.getFragment(mHostActivity.INDEX_HOTEL_LIST_FRAGMENT));
					
				}

				
			}
		};

		mHandler.sendEmptyMessageDelayed(0, 1);

	}

	@Override
	public void onDestroy() {
		if (mHandler != null)
			mHandler.removeMessages(0);
		
		super.onDestroy();
	}

}
