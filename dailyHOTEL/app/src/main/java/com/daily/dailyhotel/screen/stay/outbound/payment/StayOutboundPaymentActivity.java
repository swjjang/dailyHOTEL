package com.daily.dailyhotel.screen.stay.outbound.payment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPaymentActivity extends BaseActivity<StayOutboundPaymentPresenter>
{
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_URL = "url";
    static final String INTENT_EXTRA_DATA_CHECKIN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECKOUT = "checkOut";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE = "roomType";
    static final String INTENT_EXTRA_DATA_RATE_CODE = "rateCode";
    static final String INTENT_EXTRA_DATA_RATE_KEY = "rateKey";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE_CODE = "roomTypeCode";


    public static Intent newInstance(Context context, int stayIndex, String stayName//
        , String checkInDateTime, String checkOutDateTime, int numberOfAdults, ArrayList<Integer> childAgeList, String roomType, String rateCode, String rateKey, String roomTypeCode)
    {
        Intent intent = new Intent(context, StayOutboundPaymentActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childAgeList);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE, roomType);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_CODE, rateCode);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_KEY, rateKey);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE_CODE, roomTypeCode);

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
