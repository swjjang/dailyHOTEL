package com.daily.dailyhotel.screen.booking.receipt.stay;


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
public class StayReceiptActivity extends BaseActivity<StayReceiptPresenter>
{
    public static final String INTENT_EXTRA_RESERVATION_INDEX = "reservationIndex";
    public static final String INTENT_EXTRA_AGGREGATION_ID = "aggregationId";
    public static final String INTENT_EXTRA_BOOKING_STATE = "bookingState";

    public static final int REQUEST_CODE_EMAIL = 10000;

    public static Intent newInstance(Context context, int reservationIndex, String aggregationId, int bookingState)
    {
        Intent intent = new Intent(context, StayReceiptActivity.class);
        intent.putExtra(INTENT_EXTRA_RESERVATION_INDEX, reservationIndex);
        intent.putExtra(INTENT_EXTRA_AGGREGATION_ID, aggregationId);
        intent.putExtra(INTENT_EXTRA_BOOKING_STATE, bookingState);

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
    protected StayReceiptPresenter createInstancePresenter()
    {
        return new StayReceiptPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
