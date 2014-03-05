package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SALE_TIME;
import static com.twoheart.dailyhotel.util.AppConstants.TIME;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.AlarmBroadcastReceiver;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.util.ui.NoActionBarException;

public class WaitTimerFragment extends Fragment implements OnClickListener{
	
	private final static String TAG = "WaitTimerFragment";
	
	private View view;
	
	private Handler handler;
	private TextView timer, main, sub;
	private ImageView hotel;
	private Button btn_alram, btn_applyEvent;
	
	private long waitTime;
	private long curTime;
	private long openTime;
	private long closeTime;
	
	private boolean isAlarm;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Hotel View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_wait_timer, null);
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(view.getContext());
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		// ActionBar Setting
		MainActivity activity = (MainActivity)view.getContext();
		try {
			activity.changeTitle(" ");
		} catch (NoActionBarException e) {
			e.printStackTrace();
		}
		activity.hideMenuItem();
		
		// sliding setting
//		activity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		
		loadResource();
		
		LoadingDialog.showLoading(view.getContext());
//		new GeneralHttpTask(timeListener, view.getContext()).execute(REST_URL + TIME);
		new GeneralHttpTask(saleTimeListener, view.getContext()).execute(REST_URL + SALE_TIME);
		
		btn_applyEvent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(view.getContext(), EventWebActivity.class);
				MainActivity activity = (MainActivity) view.getContext();
				activity.startActivity(i);
				activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
			}
		});
		
		return view;
	}
	
	public void loadResource() {
		timer = (TextView) view.findViewById(R.id.tv_timer);
		main = (TextView) view.findViewById(R.id.tv_wait_timer_main);
		sub = (TextView) view.findViewById(R.id.tv_wait_timer_sub);
		hotel = (ImageView) view.findViewById(R.id.iv_wait_timer_hotel);
		btn_alram = (Button) view.findViewById(R.id.btn_wait_timer_alram);
		btn_applyEvent = (Button) view.findViewById(R.id.btn_apply_event);
		btn_alram.setOnClickListener(this);
		btn_applyEvent.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(isAlarm) {	 // 알람해제
			
			Log.d(TAG, "Cancle Alram");
			isAlarm = false;
			
			btn_alram.setText("알람 등록");
			
			AlarmManager alarmManager = (AlarmManager)view.getContext().getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(view.getContext(), AlarmBroadcastReceiver.class);
			PendingIntent pender =PendingIntent.getBroadcast(view.getContext(), 0, intent, 0);
			alarmManager.cancel(pender);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();     //닫기
			    }
			});
			alert.setMessage("알람이 취소 되었습니다");
			alert.show();
			
			
		} else {		// 알람등록
			Log.d(TAG, "Set Alram");
			isAlarm = true;
			
			btn_alram.setText("알람 끄기");
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, (int) waitTime);
			
			AlarmManager alarmManager = (AlarmManager)view.getContext().getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(view.getContext(), AlarmBroadcastReceiver.class);
			PendingIntent pender =PendingIntent.getBroadcast(view.getContext(), 0, intent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis(), pender);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();     //닫기
			    }
			});
			alert.setMessage("알람이 등록 되었습니다");
			alert.show();
		}
			
	}
	
	public boolean checkTimer(String str) {
		boolean result = false;
		
		Date now = new Date(Long.parseLong(str.trim()));
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		String time = format.format(now);
		long hour = Integer.parseInt(time.substring(0, 2));
		long min = Integer.parseInt(time.substring(2,4));
		long sec = Integer.parseInt(time.substring(4,6));
		
		long curTime = (hour * 60 * 60 ) + (min * 60) + sec;
		
		// true 면 호텔판매시간 아님
		if( (openTime < curTime) && (curTime < closeTime))
			result = false;
		else if( (curTime < closeTime)  && (closeTime < openTime))
			result = false;
		else {
			waitTime = openTime - curTime;
			result = true;
		}
		
		return result;
		// Open anytime...
//		return false;
	}
	
	public void setTimer() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				waitTime--;
				
				if(waitTime < 0) {
					handler.removeMessages(0);
					MainActivity activity = (MainActivity) view.getContext();
					activity.switchContent(new HotelListFragment());
				} else {
					
					String waitHour = Long.toString(waitTime / (60 * 60));
					String waitMin = Long.toString((waitTime % (60 * 60)) / 60);
					String waitSecond = Long.toString((waitTime % (60 * 60)) % 60);
					
					if(Integer.parseInt(waitHour) < 10 )
						waitHour = "0" + waitHour;
					if(Integer.parseInt(waitMin) < 10 )
						waitMin = "0" + waitMin;
					if(Integer.parseInt(waitSecond) < 10 )
						waitSecond = "0" + waitSecond;
					
					this.sendEmptyMessageDelayed(0, 1000);
					timer.setText(waitHour + " : " +waitMin + " : " + waitSecond);
					timer.invalidate();
				}
			}
		};
		
		handler.sendEmptyMessage(1);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(handler != null)
			handler.removeMessages(0);
	}
	
//	@Override
//	public void onStop() {
//		super.onStop();
//		if(handler!= null)
//			handler.removeMessages(0);
//	}
	
	protected OnCompleteListener saleTimeListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "saleTimeListener onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			try {
				JSONObject obj = new JSONObject(result);
				String open = obj.getString("open");
				String close = obj.getString("close");
				
				openTime = (Long.parseLong(open.substring(0, 2)) * 60 * 60) + (Long.parseLong(open.substring(3, 5)) * 60);
				closeTime = (Long.parseLong(close.substring(0, 2)) * 60 * 60) + (Long.parseLong(close.substring(3, 5)) * 60);
				
				new GeneralHttpTask(timeListener, view.getContext()).execute(REST_URL + TIME);
			} catch (Exception e) {
				e.printStackTrace();
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	
	protected OnCompleteListener timeListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			LoadingDialog.hideLoading();
			
			if(checkTimer(result)) {
				setTimer();
			} else {
				MainActivity activity = (MainActivity) view.getContext();
				Fragment fragment = new HotelListFragment();
				activity.switchContent(fragment);
			}
		}
	};
}
