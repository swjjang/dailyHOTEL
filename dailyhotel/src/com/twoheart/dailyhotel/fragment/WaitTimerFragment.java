package com.twoheart.dailyhotel.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.AlarmBroadcastReceiver;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.SaleTime;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class WaitTimerFragment extends Fragment implements OnClickListener {

	private final static String TAG = "WaitTimerFragment";

	private MainActivity mHostActivity;

	private Handler mHandler;
	private TextView tvTimer;
	private Button btnNotify;

	private SaleTime mSaleTime;
	private boolean isEnableNotify;

	public WaitTimerFragment() {
		super();
	}

	public WaitTimerFragment(SaleTime saleTime) {
		super();
		mSaleTime = saleTime;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wait_timer, null);
		mHostActivity = (MainActivity) getActivity();
		
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

			AlarmManager alarmManager = (AlarmManager) mHostActivity
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(mHostActivity,
					AlarmBroadcastReceiver.class);
			PendingIntent pender = PendingIntent.getBroadcast(mHostActivity, 0,
					intent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					mSaleTime.getOpenTime(), pender);

			Toast.makeText(mHostActivity, "알람이 등록 되었습니다", Toast.LENGTH_SHORT).show();

		} else {
			btnNotify.setText("알람 켜기");

			AlarmManager alarmManager = (AlarmManager) mHostActivity
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(mHostActivity,
					AlarmBroadcastReceiver.class);
			PendingIntent pender = PendingIntent.getBroadcast(mHostActivity, 0,
					intent, 0);
			alarmManager.cancel(pender);
			
			Toast.makeText(mHostActivity, "알람이 취소 되었습니다", Toast.LENGTH_SHORT).show();

		}

	}

	private void setTimer() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				long currentTime = mSaleTime.getCurrentTime();
				mSaleTime.setCurrentTime(Long.toString(++currentTime));

				if (mSaleTime.getCurrentTime() == mSaleTime.getOpenTime()) {
					mHandler.removeMessages(0);
					mHostActivity.replaceFragment(mHostActivity.getFragment(mHostActivity.INDEX_HOTEL_LIST_FRAGMENT));
				} else {
					
					long remainingTime = mSaleTime.getOpenTime() - currentTime;
					
					tvTimer.setText(new SimpleDateFormat("HH:mm:ss", SaleTime.locale).format(remainingTime));
					tvTimer.invalidate();
					
					this.sendEmptyMessageDelayed(0, 1);
				}
			}
		};

		mHandler.sendEmptyMessageDelayed(0, 1);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHandler != null)
			mHandler.removeMessages(0);
	}

}
