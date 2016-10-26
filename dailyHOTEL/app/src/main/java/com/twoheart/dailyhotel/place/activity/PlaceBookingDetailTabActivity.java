package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Map;

public abstract class PlaceBookingDetailTabActivity extends BaseActivity
{
    protected static final int TAB_COUNT = 1;

    private ViewPager mViewPager;
    private boolean mDontReload;
    protected int mReservationIndex;

    private DailyToolbarLayout mDailyToolbarLayout;

    protected abstract void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail);

    protected abstract void requestPlaceBookingDetail(int reservationIndex);

    protected abstract void setCurrentDateTime(long currentDateTime, long dailyDateTime);

    protected abstract void showCallDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mReservationIndex = bundle.getInt(NAME_INTENT_EXTRA_DATA_BOOKINGIDX);
        }

        if (mReservationIndex <= 0)
        {
            Util.restartApp(this);
            return;
        }

        initLayout();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL);
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_booking_tab);

        initToolbar(getString(R.string.actionbar_title_booking_list_frag));

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.clearOnPageChangeListeners();
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showCallDialog();
            }
        });
    }

    public ViewPager getViewPager()
    {
        return mViewPager;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mReservationIndex <= 0)
        {
            Util.restartApp(this);
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            startLogin();
            return;
        }

        if (mDontReload == false)
        {
            lockUI();

            requestCommonDatetime();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mDontReload == false)
        {
            mDontReload = true;
        } else
        {
            unLockUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == RESULT_OK)
                {
                    lockUI();

                    requestCommonDatetime();
                } else
                {
                    finish();
                }
                break;
            }
        }
    }

    protected void requestCommonDatetime()
    {
        DailyNetworkAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        setCurrentDateTime(currentDateTime, dailyDateTime);

                        // 호텔 정보를 가져온다.
                        requestPlaceBookingDetail(mReservationIndex);
                    } else
                    {
                        String message = response.getString("msg");
                        PlaceBookingDetailTabActivity.this.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                PlaceBookingDetailTabActivity.this.onErrorResponse(volleyError);
            }
        });
    }

    private void startLogin()
    {
        showSimpleDialog(null, getString(R.string.message_booking_detail_do_login), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LoginActivity.newInstance(PlaceBookingDetailTabActivity.this);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
