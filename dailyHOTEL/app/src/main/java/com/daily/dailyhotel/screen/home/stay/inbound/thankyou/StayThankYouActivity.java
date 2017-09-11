package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayThankYouActivity extends BaseActivity<StayThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_CHECK_IN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECK_OUT = "checkOut";
    static final String INTENT_EXTRA_DATA_ROOM_NAME = "roomName";
    static final String INTENT_EXTRA_DATA_OVERSEAS = "overseas";
    static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";
    static final String INTENT_EXTRA_DATA_WAITING_FOR_BOOKING = "waitingForBooking";

    public static Intent newInstance(Context context, boolean overseas, String stayName, String imageUrl//
        , String checkInDateTime, String checkOutDateTime, String roomName, String aggregationId//
        , boolean waitingForBooking, StayThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_NAME, roomName);
        intent.putExtra(INTENT_EXTRA_DATA_OVERSEAS, overseas);
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
        intent.putExtra(INTENT_EXTRA_DATA_WAITING_FOR_BOOKING, waitingForBooking);
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
    protected StayThankYouPresenter createInstancePresenter()
    {
        return new StayThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
