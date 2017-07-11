package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundThankYouActivity extends BaseActivity<StayOutboundThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_ROOM_PRICE = "roomPrice";
    static final String INTENT_EXTRA_DATA_CHECK_IN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECK_OUT = "checkOut";
    static final String INTENT_EXTRA_DATA_CHECK_IN_TIME = "checkInTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_TIME = "checkOutTime";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE = "roomType";
    static final String INTENT_EXTRA_DATA_RESERVATION_ID = "reservationId";

    public static Intent newInstance(Context context, int stayIndex, String stayName, String imageUrl, int roomPrice//
        , String checkInDateTime, String checkOutDateTime, String checkInTime, String checkOutTime//
        , String roomType, int reservationId, StayOutboundThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayOutboundThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_PRICE, roomPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_TIME, checkInTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_TIME, checkOutTime);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE, roomType);
        intent.putExtra(INTENT_EXTRA_DATA_RESERVATION_ID, reservationId);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayOutboundThankYouPresenter createInstancePresenter()
    {
        return new StayOutboundThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
