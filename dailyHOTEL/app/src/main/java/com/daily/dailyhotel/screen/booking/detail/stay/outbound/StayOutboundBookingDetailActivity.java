package com.daily.dailyhotel.screen.booking.detail.stay.outbound;


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
    static final int REQUEST_CODE_ISSUING_RECEIPT = 10000;
    static final int REQUEST_CODE_ZOOMMAP = 10001;
    static final int REQUEST_CODE_DETAIL = 10002;
    static final int REQUEST_CODE_CALL = 10003;
    static final int REQUEST_CODE_HAPPYTALK = 10004;
    static final int REQUEST_CODE_PERMISSION_MANAGER = 10005;
    static final int REQUEST_CODE_SETTING_LOCATION = 10006;
    static final int REQUEST_CODE_REFUND = 10007;

    static final String INTENT_EXTRA_DATA_BOOKING_INDEX = "index";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";

    public static Intent newInstance(Context context, int bookingIndex, String imageUrl)
    {
        Intent intent = new Intent(context, StayOutboundBookingDetailActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_INDEX, bookingIndex);
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
