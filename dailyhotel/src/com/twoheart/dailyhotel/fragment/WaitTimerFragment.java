/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * WaitTimerFragment (오픈 대기 타이머 화면)
 * 
 * 영업 시작 시간 전에 보이는 화면이다. 타이머와 함께 안내 멘트가 있는 화면
 * 으로서 영업 시간을 카운트하며 영업 시간을 알린다. 타이머의 경우 Handler
 * 를 사용했으며, 서버로부터 영업 시작 시간과 현재 시간을 얻어온다. 그런 후
 * 현재 시간으로부터 1초씩 세어 영업 시간까지인지를 판단토록 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.AlarmBroadcastReceiver;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.TicketMainFragment.TICKET_TYPE;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.WakeLock;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.widget.FontManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WaitTimerFragment
		extends BaseFragment implements OnClickListener, Constants
{
	private final static String KEY_BUNDLE_ARGUMENTS_SALETIME = "saletime";
	private final static String KEY_BUNDLE_ARGUMENTS_TYPE = "type";

	public static boolean isEnabledNotify;

	private static Handler sHandler;
	private TextView tvTimer;
	private TextView mAlarmTextView;
	private ImageView mAlarmImageView;
	private View alarmTimerLayout;

	private AlarmManager alarmManager;
	private PendingIntent pender;
	private Intent intent;
	private SaleTime mSaleTime;
	private long remainingTime;

	public static WaitTimerFragment newInstance(SaleTime saleTime, TICKET_TYPE type)
	{
		WaitTimerFragment newFragment = new WaitTimerFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_SALETIME, saleTime);
		arguments.putString(KEY_BUNDLE_ARGUMENTS_TYPE, type.name());

		newFragment.setArguments(arguments);

		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = null;

		try
		{
			view = inflater.inflate(R.layout.fragment_wait_timer, container, false);
		} catch (OutOfMemoryError errror)
		{
			Util.finishOutOfMemory(baseActivity);
			return null;
		}

		mSaleTime = (SaleTime) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_SALETIME);
		TICKET_TYPE type = TICKET_TYPE.valueOf(getArguments().getString(KEY_BUNDLE_ARGUMENTS_TYPE));

		alarmManager = (AlarmManager) baseActivity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(baseActivity.getApplicationContext(), AlarmBroadcastReceiver.class);
		pender = PendingIntent.getBroadcast(baseActivity.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		ImageView imageView = (ImageView) view.findViewById(R.id.backgroundImageView);
		tvTimer = (TextView) view.findViewById(R.id.tv_timer);
		TextView titleMainTextView = (TextView) view.findViewById(R.id.tv_wait_timer_main);
		TextView titleSubTextView = (TextView) view.findViewById(R.id.tv_wait_timer_sub);

		alarmTimerLayout = view.findViewById(R.id.alarmTimerLayout);

		switch (type)
		{
			case HOTEL:
				imageView.setImageResource(R.drawable.open_stanby_bg);

				titleMainTextView.setText(R.string.prefix_wait_timer_frag_todays_hotel_open);
				titleSubTextView.setText(R.string.frag_wait_timer_hotel_msg);
				break;

			case FNB:
				imageView.setImageResource(R.drawable.open_stanby_bg_fnb);

				titleMainTextView.setTextColor(getResources().getColor(R.color.white));
				titleMainTextView.setText(R.string.prefix_wait_timer_frag_todays_fnb_open);

				titleSubTextView.setTextColor(getResources().getColor(R.color.white));
				titleSubTextView.setText(R.string.frag_wait_timer_fnb_msg);

				tvTimer.setTextColor(getResources().getColor(R.color.white));
				break;
		}

		mAlarmTextView = (TextView) view.findViewById(R.id.alarmTextView);
		mAlarmImageView = (ImageView) view.findViewById(R.id.alarmImageView);

		mAlarmTextView.setTypeface(FontManager.getInstance(baseActivity).getMediumTypeface());
		tvTimer.setTypeface(FontManager.getInstance(baseActivity).getThinTypeface());

		alarmTimerLayout.setOnClickListener(this);

		baseActivity.setActionBar(getString(R.string.actionbar_title_wait_timer_frag), false);

		SimpleDateFormat sFormat = new SimpleDateFormat("aa H", Locale.KOREA);
		sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		titleMainTextView.setText(sFormat.format(mSaleTime.getOpenTime()) + getString(R.string.prefix_wait_timer_frag_todays_hotel_open));

		isEnabledNotify = false;
		setTimer();

		return view;
	}

	@Override
	public void onStart()
	{
		AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.WAIT_TIMER);
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setNotify(isEnabledNotify);

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		// 새로운 이벤트 확인을 위해 버전 API 호출
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(), null, mAppVersionResponseListener, baseActivity));
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == alarmTimerLayout.getId())
		{
			setNotify(!isEnabledNotify);
		}
	}

	private void setNotify(boolean enable)
	{
		if (enable)
		{
			mAlarmTextView.setText(getString(R.string.frag_wait_timer_off));
			mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert_off);

			if (enable != isEnabledNotify)
			{
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + remainingTime, pender);

				showToast(getString(R.string.frag_wait_timer_set), Toast.LENGTH_SHORT, true);
			}

		} else
		{
			mAlarmTextView.setText(getString(R.string.frag_wait_timer_on));
			mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert);

			if (enable != isEnabledNotify)
			{
				alarmManager.cancel(pender);

				showToast(getString(R.string.frag_wait_timer_cancel), Toast.LENGTH_SHORT, true);
			}

		}

		isEnabledNotify = enable;

	}

	private void setTimer()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		Date currentDate = new Date(mSaleTime.getCurrentTime());
		Date dailyOpenDate = new Date(mSaleTime.getOpenTime());

		remainingTime = dailyOpenDate.getTime() - currentDate.getTime();
		printCurrentRemaingTime(remainingTime);

		WakeLock.acquireWakeLock(baseActivity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

		sHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				BaseActivity baseActivity = (BaseActivity) getActivity();

				if (baseActivity == null || baseActivity.isFinishing() == true)
				{
					return;
				}

				remainingTime -= 1000;

				if (remainingTime > 0)
				{
					printCurrentRemaingTime(remainingTime);
					this.sendEmptyMessageDelayed(0, 1000);

				} else
				{
					this.removeMessages(0);
					WakeLock.releaseWakeLock();

					if (sHandler != null)
					{
						((MainActivity) baseActivity).replaceFragment(((MainActivity) baseActivity).getFragment(MainActivity.INDEX_HOTEL_LIST_FRAGMENT));
						sHandler = null;
					}
				}
			}
		};

		if (sHandler != null)
		{
			sHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}

	private void printCurrentRemaingTime(long remainingTime)
	{
		SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		displayTimeFormat.setTimeZone(TimeZone.getTimeZone("KST"));

		tvTimer.setText(displayTimeFormat.format(remainingTime));

	}

	@Override
	public void onDestroy()
	{
		if (sHandler != null)
		{
			sHandler.removeMessages(0);
			WakeLock.releaseWakeLock();
		}

		super.onDestroy();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private DailyHotelJsonResponseListener mAppVersionResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (getActivity() == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				if (response.getString("new_event").equals("1") == true)
				{
					//					if (ivNewEvent != null) ivNewEvent.setVisibility(View.VISIBLE);
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};
}
