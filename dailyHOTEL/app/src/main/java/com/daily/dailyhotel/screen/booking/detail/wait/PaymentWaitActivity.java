package com.daily.dailyhotel.screen.booking.detail.wait;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.parcel.BookingParcel;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class PaymentWaitActivity extends BaseActivity<PaymentWaitPresenter>
{
    public static String INTENT_EXTRA_DATA_BOOKING = "booking";

    public static final int REQUEST_CODE_FAQ = 10000;

    public static Intent newInstance(Context context, Booking booking)
    {
        Intent intent = new Intent(context, PaymentWaitActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BOOKING, new BookingParcel(booking));

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
    protected PaymentWaitPresenter createInstancePresenter()
    {
        return new PaymentWaitPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
