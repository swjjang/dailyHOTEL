package com.daily.dailyhotel.screen.booking.detail.stay.refund;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.StayBookingDetailParcel;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAutoRefundActivity extends BaseActivity<StayAutoRefundPresenter>
{
    public static final String INTENT_EXTRA_DATA_BOOKING_DETAIL = "bookingDetail";
    public static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";

    public static final String PAYMENT_TYPE_VBANK = "VBANK_INICIS";

    public static final int REQUEST_CODE_SELECT_CANCEL_TYPE = 10000;
    public static final int REQUEST_CODE_SELECT_BANK_LIST = 10001;

    public static Intent newInstance(Context context, StayBookingDetailParcel stayBookingDetailParcel , String aggregationId)
    {
        Intent intent = new Intent(context, StayAutoRefundActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL, stayBookingDetailParcel);
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);

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
    protected StayAutoRefundPresenter createInstancePresenter()
    {
        return new StayAutoRefundPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
