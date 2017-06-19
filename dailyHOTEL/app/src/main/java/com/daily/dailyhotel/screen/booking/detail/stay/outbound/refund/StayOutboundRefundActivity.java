package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;


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
public class StayOutboundRefundActivity extends BaseActivity<StayOutboundRefundPresenter>
{
    static final String INTENT_EXTRA_DATA_BOOKING_INDEX = "bookingIndex";
    static final String INTENT_EXTRA_DATA_TITLE = "title";

    public static Intent newInstance(Context context, int bookingIndex, String title)
    {
        Intent intent = new Intent(context, StayOutboundRefundActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_INDEX, bookingIndex);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);

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
    protected StayOutboundRefundPresenter createInstancePresenter()
    {
        return new StayOutboundRefundPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
