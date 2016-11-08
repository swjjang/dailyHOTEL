package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StayAutoRefundActivity extends BaseActivity implements Constants, View.OnClickListener
{
    private static final String INTENT_EXTRA_DATA_BOOKING_DETAIL = "bookingDetail";

    private HotelBookingDetail mHotelBookingDetail;

    public static Intent newInstance(Context context, HotelBookingDetail hotelBookingDetail)
    {
        Intent intent = new Intent(context, StayAutoRefundActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL, hotelBookingDetail);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stay_autorefund);

        Intent intent = getIntent();

        if(intent == null)
        {
            finish();
            return;
        }

        mHotelBookingDetail = intent.getParcelableExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_request_free_refund), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {

    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }


    @Override
    public void onClick(View v)
    {

    }
}
