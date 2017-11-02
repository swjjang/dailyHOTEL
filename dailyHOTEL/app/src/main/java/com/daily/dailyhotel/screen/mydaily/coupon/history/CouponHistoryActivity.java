package com.daily.dailyhotel.screen.mydaily.coupon.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponHistoryActivity extends BaseActivity<CouponHistoryPresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, CouponHistoryActivity.class);

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
    protected CouponHistoryPresenter createInstancePresenter()
    {
        return new CouponHistoryPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
