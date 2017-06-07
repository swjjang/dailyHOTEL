package com.daily.dailyhotel.screen.booking.detail.stayoutbound.refund;


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
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StayOutboundRefundActivity.class);

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
