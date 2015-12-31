/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * PaymentWaitActivity (입금대기 화면)
 * <p>
 * 계좌이체 결제 선택 후 입금대기 상태 화면
 * 가상계좌 정보를 보여주는 화면이다.
 */
package com.twoheart.dailyhotel.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PaymentWaitActivity extends BaseActivity
{
    private TextView tvAccount;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvDeadline;
    private TextView tvGuide1;
    private TextView tvGuide2;
    private String mCSoperatingTimeMessage;

    private DailyToolbarLayout mDailyToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Booking booking = new Booking();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
        }

        setContentView(R.layout.activity_payment_wait);

        initToolbar();

        TextView tvHotelName = (TextView) findViewById(R.id.tv_payment_wait_hotel_name);
        tvAccount = (TextView) findViewById(R.id.tv_payment_wait_account);
        tvName = (TextView) findViewById(R.id.tv_payment_wait_name);
        tvPrice = (TextView) findViewById(R.id.tv_payment_wait_price);
        tvDeadline = (TextView) findViewById(R.id.tv_payment_wait_deadline);
        tvGuide1 = (TextView) findViewById(R.id.tv_payment_wait_guide1);
        tvGuide2 = (TextView) findViewById(R.id.tv_payment_wait_guide2);

        tvHotelName.setText(booking.placeName);

        lockUI();

        switch (booking.placeType)
        {
            case HOTEL:
            {
                String params = String.format("/%d/%s", booking.payType, booking.tid);
                DailyNetworkAPI.getInstance().requestDepositWaitDetailInformation(mNetworkTag, params, mHotelReservationJsonResponseListener, this);
                break;
            }

            case FNB:
            {
                String params = String.format("?tid=%s", booking.tid);
                DailyNetworkAPI.getInstance().requestGourmetAccountInformation(mNetworkTag, params, mFnBReservationJsonResponseListener, this);
                break;
            }
        }
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_payment_wait_activity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.payment_wait_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_call:
                if (isFinishing() == true)
                {
                    return super.onOptionsItemSelected(item);
                }

                showCallDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showCallDialog()
    {
        if (isFinishing() == true)
        {
            return;
        }

        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(PaymentWaitActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(PaymentWaitActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(PaymentWaitActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        if (Util.isTextEmpty(mCSoperatingTimeMessage) == true)
        {
            mCSoperatingTimeMessage = getString(R.string.dialog_msg_call);
        }

        showSimpleDialog(getString(R.string.dialog_notice2), mCSoperatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mHotelReservationJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response.getBoolean("result") == false)
                {
                    unLockUI();

                    Intent intent = new Intent();
                    intent.putExtra("msg", response.getString("msg"));
                    setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                    finish();
                    return;
                } else
                {
                    tvAccount.setText(response.getString("bank_name") + ", " + response.getString("account_num"));
                    tvName.setText(response.getString("name"));

                    DecimalFormat comma = new DecimalFormat("###,##0");
                    tvPrice.setText(comma.format(response.getInt("amt")) + Html.fromHtml(getString(R.string.currency)));

                    String[] dateSlice = response.getString("date").split("/");
                    String[] timeSlice = response.getString("time").split(":");

                    tvDeadline.setText(Integer.parseInt(dateSlice[1]) + "월 " + Integer.parseInt(dateSlice[2]) + "일 " + timeSlice[0] + ":" + timeSlice[1] + "까지");

                    tvGuide1.setText(response.getString("msg1"));
                    tvGuide2.setText(response.getString("msg2"));
                }

                DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, PaymentWaitActivity.this);
            } catch (JSONException e)
            {
                ExLog.e(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mFnBReservationJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    tvAccount.setText(jsonObject.getString("bank_name") + ", " + jsonObject.getString("account_num"));
                    tvName.setText(jsonObject.getString("name"));

                    DecimalFormat comma = new DecimalFormat("###,##0");
                    tvPrice.setText(comma.format(jsonObject.getInt("amt")) + Html.fromHtml(getString(R.string.currency)));

                    String[] dateSlice = jsonObject.getString("date").split("/");
                    String[] timeSlice = jsonObject.getString("time").split(":");

                    tvDeadline.setText(Integer.parseInt(dateSlice[1]) + "월 " + Integer.parseInt(dateSlice[2]) + "일 " + timeSlice[0] + ":" + timeSlice[1] + "까지");

                    tvGuide1.setText(jsonObject.getString("msg1"));
                    tvGuide2.setText(jsonObject.getString("msg2"));
                } else
                {
                    unLockUI();

                    Intent intent = new Intent();
                    intent.putExtra("msg", response.getString("msg"));
                    setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                    finish();
                    return;
                }

                DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, PaymentWaitActivity.this);
            } catch (Exception e)
            {
                onError(e);
                finish();
            }
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                mCSoperatingTimeMessage = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));
            } catch (Exception e)
            {
                onError(e);
                finish();
            } finally
            {
                unLockUI();
            }

        }
    };
}
