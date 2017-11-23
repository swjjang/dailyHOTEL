package com.daily.dailyhotel.screen.booking.detail.gourmet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetBookingDetailActivity extends BaseActivity<GourmetBookingDetailPresenter>
{
    public static final String NAME_INTENT_EXTRA_DATA_BOOKINGIDX = "bookingIndex";
    public static final String NAME_INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";
    public static final String NAME_INTENT_EXTRA_DATA_URL = "url";
    public static final String NAME_INTENT_EXTRA_DATA_DEEPLINK = "deepLink";
    public static final String NAME_INTENT_EXTRA_DATA_BOOKING_STATE = "bookingState";

    public static Intent newInstance(Context context, int reservationIndex, String aggregationId, String imageUrl, boolean isDeepLink, int bookingState)
    {
        Intent intent = new Intent(context, GourmetBookingDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING_STATE, bookingState);

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
    protected GourmetBookingDetailPresenter createInstancePresenter()
    {
        return new GourmetBookingDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
