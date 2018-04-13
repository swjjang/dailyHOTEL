package com.daily.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.common.web.DailyWebPresenter;

public class CouponTermActivity extends DailyWebActivity
{
    static final String INTENT_EXTRA_DATA_COUPON_IDX = "coupon_idx";

    /**
     * 공통 쿠폰 유의 사항
     *
     * @param context
     * @return
     */
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, com.twoheart.dailyhotel.screen.mydaily.coupon.CouponTermActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, "");
        return intent;
    }

    /**
     * 개별 쿠폰 유의 사항
     *
     * @param context
     * @param couponIdx 쿠폰 번호 ,  null 일때 공통 쿠폰 유의사항으로 이동
     * @return
     */
    public static Intent newInstance(Context context, String couponIdx)
    {
        Intent intent = new Intent(context, com.twoheart.dailyhotel.screen.mydaily.coupon.CouponTermActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_COUPON_IDX, DailyTextUtils.isTextEmpty(couponIdx) ? "" : couponIdx);
        return intent;
    }

    @NonNull
    @Override
    protected DailyWebPresenter createInstancePresenter()
    {
        return new CouponTermPresenter(this);
    }
}
