/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * WaitTimerFragment (오픈 대기 타이머 화면)
 * <p/>
 * 영업 시작 시간 전에 보이는 화면이다. 타이머와 함께 안내 멘트가 있는 화면
 * 으로서 영업 시간을 카운트하며 영업 시간을 알린다. 타이머의 경우 Handler
 * 를 사용했으며, 서버로부터 영업 시작 시간과 현재 시간을 얻어온다. 그런 후
 * 현재 시간으로부터 1초씩 세어 영업 시간까지인지를 판단토록 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

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

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.AlarmBroadcastReceiver;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.TYPE;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.WakeLock;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WaitTimerFragment extends BaseFragment implements OnClickListener, Constants
{
    private final static String KEY_BUNDLE_ARGUMENTS_SALETIME = "saletime";
    private final static String KEY_BUNDLE_ARGUMENTS_TYPE = "type";

    private static Handler sHandler;
    private TextView tvTimer;
    private TextView mAlarmTextView;
    private ImageView mAlarmImageView;
    private View alarmTimerLayout;

    private AlarmManager alarmManager;
    private PendingIntent mPendingIntent;
    private SaleTime mSaleTime;
    private long remainingTime;

    private TYPE mType;
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

    public static WaitTimerFragment newInstance(SaleTime saleTime, TYPE type)
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
        mType = TYPE.valueOf(getArguments().getString(KEY_BUNDLE_ARGUMENTS_TYPE));

        alarmManager = (AlarmManager) baseActivity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(baseActivity.getApplicationContext(), AlarmBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(baseActivity.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        ImageView imageView = (ImageView) view.findViewById(R.id.open_stanby_bg_fnb);

        tvTimer = (TextView) view.findViewById(R.id.tv_timer);
        tvTimer.setTypeface(FontManager.getInstance(baseActivity).getThinTypeface());

        TextView titleMainTextView = (TextView) view.findViewById(R.id.tv_wait_timer_main);
        TextView titleSubTextView = (TextView) view.findViewById(R.id.tv_wait_timer_sub);

        alarmTimerLayout = view.findViewById(R.id.alarmTimerLayout);
        alarmTimerLayout.setOnClickListener(this);

        SimpleDateFormat sFormat = new SimpleDateFormat("aa H", Locale.KOREA);
        sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        // 알람 설정
        boolean enabledAlarm = DailyPreference.getInstance(baseActivity).getEnabledOpeningAlarm();

        switch (mType)
        {
            case HOTEL:
            {
                imageView.setImageResource(R.drawable.open_stanby_bg);

                titleMainTextView.setText(sFormat.format(mSaleTime.getOpenTime()) + getString(R.string.prefix_wait_timer_frag_todays_hotel_open));
                titleSubTextView.setText(R.string.frag_wait_timer_hotel_msg);

                initAlarmLayout(baseActivity, alarmTimerLayout, enabledAlarm);
                break;
            }

            case FNB:
            {
                imageView.setImageResource(R.drawable.open_stanby_bg_fnb);

                titleMainTextView.setTextColor(getResources().getColor(R.color.white));
                titleMainTextView.setText(sFormat.format(mSaleTime.getOpenTime()) + getString(R.string.prefix_wait_timer_frag_todays_fnb_open));

                titleSubTextView.setTextColor(getResources().getColor(R.color.white));
                titleSubTextView.setText(R.string.frag_wait_timer_fnb_msg);

                tvTimer.setTextColor(getResources().getColor(R.color.white));

                initAlarmLayout(baseActivity, alarmTimerLayout, enabledAlarm);
                break;
            }
        }

        baseActivity.setActionBar(getString(R.string.actionbar_title_wait_timer_frag), false);

        setTimer();

        return view;
    }

    private void initAlarmLayout(BaseActivity activity, View view, boolean enable)
    {
        if (mAlarmTextView == null)
        {
            mAlarmTextView = (TextView) view.findViewById(R.id.alarmTextView);
            mAlarmTextView.setTypeface(FontManager.getInstance(activity).getMediumTypeface());
        }

        if (mAlarmImageView == null)
        {
            mAlarmImageView = (ImageView) view.findViewById(R.id.alarmImageView);
        }

        if (enable)
        {
            mAlarmTextView.setText(getString(R.string.frag_wait_timer_off));
            mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert_off);
        } else
        {
            mAlarmTextView.setText(getString(R.string.frag_wait_timer_on));
            mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert);
        }
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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            boolean enabledAlarm = DailyPreference.getInstance(baseActivity).getEnabledOpeningAlarm();
            setNotifyEnable(!enabledAlarm);
        }
    }

    private void setNotifyEnable(boolean enable)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        DailyPreference.getInstance(baseActivity).setEnabledOpeningAlarm(enable);

        if (enable)
        {
            mAlarmTextView.setText(getString(R.string.frag_wait_timer_off));
            mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert_off);

            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + remainingTime, mPendingIntent);
            showToast(getString(R.string.frag_wait_timer_set), Toast.LENGTH_SHORT, true);
        } else
        {
            mAlarmTextView.setText(getString(R.string.frag_wait_timer_on));
            mAlarmImageView.setImageResource(R.drawable.open_stanby_ic_alert);

            alarmManager.cancel(mPendingIntent);
            showToast(getString(R.string.frag_wait_timer_cancel), Toast.LENGTH_SHORT, true);
        }
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

        sHandler.sendEmptyMessageDelayed(0, 1000);
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
}
