package com.daily.dailyhotel.screen.home.stay.inbound.payment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayPaymentActivity extends BaseActivity<StayPaymentPresenter>
{
    static final int REQUEST_CODE_CARD_MANAGER = 10000;
    static final int REQUEST_CODE_REGISTER_CARD = 10001;
    static final int REQUEST_CODE_REGISTER_CARD_PAYMENT = 10002;
    static final int REQUEST_CODE_REGISTER_PHONE_NUMBER = 10003;
    static final int REQUEST_CODE_CALL = 10004;
    static final int REQUEST_CODE_THANK_YOU = 10005;
    static final int REQUEST_CODE_PAYMENT_WEB_CARD = 10006;
    static final int REQUEST_CODE_PAYMENT_WEB_PHONE = 10007;
    static final int REQUEST_CODE_PAYMENT_WEB_VBANK = 10008;
    static final int REQUEST_CODE_COUPON_LIST = 10009;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_ROOM_PRICE = "roomPrice";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_ROOM_INDEX = "roomIndex";
    static final String INTENT_EXTRA_DATA_NRD = "nrd";
    static final String INTENT_EXTRA_DATA_CATEGORY = "category";
    static final String INTENT_EXTRA_DATA_ROOM_NAME = "roomName";
    static final String INTENT_EXTRA_DATA_LATITUDE = "latitude";
    static final String INTENT_EXTRA_DATA_LONGITUDE = "longitude";


    public static Intent newInstance(Context context, int stayIndex, String stayName, String imageUrl//
        , int roomIndex, int roomPrice, String roomName, String checkInDateTime, String checkOutDateTime, boolean nrd//
        , String category, double latitude, double longitude, StayPaymentAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayPaymentActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_INDEX, roomIndex);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_PRICE, roomPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NRD, nrd);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_NAME, roomName);
        intent.putExtra(INTENT_EXTRA_DATA_LATITUDE, latitude);
        intent.putExtra(INTENT_EXTRA_DATA_LONGITUDE, longitude);

        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayPaymentPresenter createInstancePresenter()
    {
        return new StayPaymentPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
