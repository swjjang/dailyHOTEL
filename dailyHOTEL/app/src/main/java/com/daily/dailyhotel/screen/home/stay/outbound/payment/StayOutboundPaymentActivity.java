package com.daily.dailyhotel.screen.home.stay.outbound.payment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPaymentActivity extends BaseActivity<StayOutboundPaymentPresenter>
{
    static final int REQUEST_CODE_CARD_MANAGER = 10000;
    static final int REQUEST_CODE_REGISTER_CARD = 10001;
    static final int REQUEST_CODE_REGISTER_CARD_PAYMENT = 10002;
    static final int REQUEST_CODE_REGISTER_PHONE_NUMBER = 10003;
    static final int REQUEST_CODE_CALL = 10004;
    static final int REQUEST_CODE_THANK_YOU = 10005;
    static final int REQUEST_CODE_PAYMENT_WEB_CARD = 10006;
    static final int REQUEST_CODE_PAYMENT_WEB_PHONE = 10007;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_ROOM_PRICE = "roomPrice";
    static final String INTENT_EXTRA_DATA_CHECK_IN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECK_OUT = "checkOut";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE = "roomType";
    static final String INTENT_EXTRA_DATA_VENDOR_TYPE = "vendorType";
    static final String INTENT_EXTRA_DATA_RATE_CODE = "rateCode";
    static final String INTENT_EXTRA_DATA_RATE_KEY = "rateKey";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE_CODE = "roomTypeCode";
    static final String INTENT_EXTRA_DATA_ROOM_BED_TYPE_ID = "roomBedTypeId";


    public static Intent newInstance(Context context, int stayIndex, String stayName, String imageUrl, int roomPrice//
        , String checkInDateTime, String checkOutDateTime, int numberOfAdults//
        , ArrayList<Integer> childAgeList, String roomType//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, String vendorType//
        , StayOutboundPaymentAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayOutboundPaymentActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_PRICE, roomPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childAgeList);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE, roomType);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_CODE, rateCode);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_KEY, rateKey);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE_CODE, roomTypeCode);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_BED_TYPE_ID, roomBedTypeId);
        intent.putExtra(INTENT_EXTRA_DATA_VENDOR_TYPE, vendorType);
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
    protected StayOutboundPaymentPresenter createInstancePresenter()
    {
        return new StayOutboundPaymentPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
