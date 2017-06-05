package com.daily.dailyhotel.screen.booking.detail.stayoutbound;


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
public class StayOutboundBookingDetailActivity extends BaseActivity<StayOutboundBookingDetailPresenter>
{
    static final String INTENT_EXTRA_DATA_RESERVATION_INDEX = "reservationIndex";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";

    public static Intent newInstance(Context context, int reservationIndex, String imageUrl)
    {
        Intent intent = new Intent(context, StayOutboundBookingDetailActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_RESERVATION_INDEX, reservationIndex);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);

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
    protected StayOutboundBookingDetailPresenter createInstancePresenter()
    {
        return new StayOutboundBookingDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
