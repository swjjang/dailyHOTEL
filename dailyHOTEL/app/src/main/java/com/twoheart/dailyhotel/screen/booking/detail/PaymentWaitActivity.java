/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * PaymentWaitActivity (입금대기 화면)
 * <p>
 * 계좌이체 결제 선택 후 입금대기 상태 화면
 * 가상계좌 정보를 보여주는 화면이다.
 */
package com.twoheart.dailyhotel.screen.booking.detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class PaymentWaitActivity extends BaseActivity
{
    private TextView mAccountTextView;
    private TextView mDailyTextView;
    private TextView mPriceTextView, mBonusTextView, mCouponTextView, mTotlalPriceTextView;
    private TextView mDeadlineTextView;
    private ViewGroup mGuide1Layout;
    private View mBonusLayout, mCouponLayout;

    public static Intent newInstance(Context context, Booking booking)
    {
        Intent intent = new Intent(context, PaymentWaitActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, booking);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Booking booking = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_BOOKING);

        if (booking == null)
        {
            Util.restartApp(this);
            return;
        }

        setContentView(R.layout.activity_payment_wait);

        initToolbar();
        initLayout(booking);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_payment_wait_activity), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        dailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        dailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showCallDialog();
            }
        });
    }

    private void initLayout(Booking booking)
    {
        if (booking == null)
        {
            return;
        }

        ScrollView scrollLayout = (ScrollView) findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollLayout, getResources().getColor(R.color.default_over_scroll_edge));

        TextView placeNameTextView = (TextView) findViewById(R.id.tv_payment_wait_hotel_name);
        mAccountTextView = (TextView) findViewById(R.id.tv_payment_wait_account);
        mDailyTextView = (TextView) findViewById(R.id.tv_payment_wait_name);
        mPriceTextView = (TextView) findViewById(R.id.priceTextView);
        mBonusTextView = (TextView) findViewById(R.id.bonusTextView);
        mCouponTextView = (TextView) findViewById(R.id.couponTextView);
        mTotlalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);
        mDeadlineTextView = (TextView) findViewById(R.id.tv_payment_wait_deadline);
        mGuide1Layout = (ViewGroup) findViewById(R.id.guide1Layout);

        mBonusLayout = findViewById(R.id.bonusLayout);
        mCouponLayout = findViewById(R.id.couponLayout);

        View view = findViewById(R.id.editLinearLayout);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAccountTextView == null)
                {
                    return;
                }

                Util.clipText(PaymentWaitActivity.this, (String) mAccountTextView.getTag());

                DailyToast.showToast(PaymentWaitActivity.this, R.string.message_detail_copy_account_number, Toast.LENGTH_SHORT);
            }
        });

        placeNameTextView.setText(booking.placeName);

        lockUI();

        TextView placeInformationView = (TextView) findViewById(R.id.placeInformationView);
        TextView placeNameView = (TextView) findViewById(R.id.placeNameView);

        switch (booking.placeType)
        {
            case HOTEL:
            {
                placeInformationView.setText(R.string.actionbar_title_hoteldetailinfo_activity);
                placeNameView.setText(R.string.label_receipt_hotelname);

                DailyNetworkAPI.getInstance(this).requestDepositWaitDetailInformation(mNetworkTag, booking.tid, mHotelReservationJsonResponseListener, this);
                break;
            }

            case FNB:
            {
                placeInformationView.setText(R.string.label_restaurant_information);
                placeNameView.setText(R.string.label_receipt_restaurantname);

                DailyNetworkAPI.getInstance(this).requestGourmetAccountInformation(mNetworkTag, booking.tid, mFnBReservationJsonResponseListener, this);
                break;
            }
        }
    }

    private void showCallDialog()
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
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
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

        String operatingTimeMessage = DailyPreference.getInstance(this).getOperationTimeMessage(this);

        showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    private void setGuideText(ViewGroup viewGroups, String[] guides, boolean isImportant)
    {
        if (guides == null)
        {
            return;
        }

        for (String guide : guides)
        {
            View textLayout = LayoutInflater.from(this).inflate(R.layout.list_row_detail_text, viewGroups, false);
            TextView textView = (TextView) textLayout.findViewById(R.id.textView);
            textView.setText(guide.replace("\n", " ").trim() + ".");

            if (isImportant == true)
            {
                textView.setTextColor(getResources().getColor(R.color.dh_theme_color));
            }

            viewGroups.addView(textLayout);
        }
    }

    private void setHotelReservationData(JSONObject jsonObject) throws JSONException, ParseException
    {
        JSONObject reservationJSONObject = jsonObject.getJSONObject("reservation");
        String accountNumber = reservationJSONObject.getString("vactNum");
        mAccountTextView.setText(reservationJSONObject.getString("bankName") + ", " + accountNumber);
        mAccountTextView.setTag(accountNumber);

        mDailyTextView.setText(reservationJSONObject.getString("vactName"));

        // 입금기한
        //        String validToDate = Util.simpleDateFormatISO8601toFormat(reservationJSONObject.getString("validTo"), "yyyy년 MM월 dd일 HH시 mm분 까지");
        String validToDate = DailyCalendar.convertDateFormatString(reservationJSONObject.getString("validTo"), DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
        mDeadlineTextView.setText(validToDate);

        // 결재 금액 정보
        mPriceTextView.setText(Util.getPriceFormat(this, reservationJSONObject.getInt("price"), false));

        int bonus = reservationJSONObject.getInt("bonus");

        if (bonus > 0)
        {
            mBonusLayout.setVisibility(View.VISIBLE);
            mBonusTextView.setText("- " + Util.getPriceFormat(this, bonus, false));
        } else
        {
            mBonusLayout.setVisibility(View.GONE);
        }

        int coupon = reservationJSONObject.getInt("couponAmount");

        if (coupon > 0)
        {
            mCouponLayout.setVisibility(View.VISIBLE);
            mCouponTextView.setText("- " + Util.getPriceFormat(this, coupon, false));
        } else
        {
            mCouponLayout.setVisibility(View.GONE);
        }

        int paymetPrice = reservationJSONObject.getInt("amt");

        mTotlalPriceTextView.setText(Util.getPriceFormat(this, paymetPrice, false));

        // 확인 사항
        String msg1 = jsonObject.getString("msg1");
        setGuideText(mGuide1Layout, msg1.split("\\."), false);

        String msg2 = getString(R.string.message__wait_payment03);
        setGuideText(mGuide1Layout, msg2.split("\\."), true);
    }

    private void setGourmetReservationData(JSONObject jsonObject) throws JSONException
    {
        String accountNumber = jsonObject.getString("account_num");
        mAccountTextView.setText(jsonObject.getString("bank_name") + ", " + accountNumber);
        mAccountTextView.setTag(accountNumber);

        mDailyTextView.setText(jsonObject.getString("name"));

        int paymetPrice = jsonObject.getInt("amt");
        mPriceTextView.setText(Util.getPriceFormat(this, paymetPrice, false));

        String[] dateSlice = jsonObject.getString("date").split("/");
        String[] timeSlice = jsonObject.getString("time").split(":");

        String date = String.format("%s년 %s월 %s일", dateSlice[0], dateSlice[1], dateSlice[2]);

        mDeadlineTextView.setText(String.format("%s %s시 %s분 까지", date, timeSlice[0], timeSlice[1]));

        String msg1 = jsonObject.getString("msg1");
        setGuideText(mGuide1Layout, msg1.split("\\."), false);

        String msg2 = getString(R.string.message__wait_payment03);
        setGuideText(mGuide1Layout, msg2.split("\\."), true);

        mTotlalPriceTextView.setText(Util.getPriceFormat(this, paymetPrice, false));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mHotelReservationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    setHotelReservationData(dataJSONObject);
                } else
                {
                    Intent intent = new Intent();
                    intent.putExtra("msg", response.getString("msg"));
                    setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                    finish();
                    return;
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mFnBReservationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    setGourmetReservationData(jsonObject);
                } else
                {
                    Intent intent = new Intent();
                    intent.putExtra("msg", response.getString("msg"));
                    setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                    finish();
                    return;
                }
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
