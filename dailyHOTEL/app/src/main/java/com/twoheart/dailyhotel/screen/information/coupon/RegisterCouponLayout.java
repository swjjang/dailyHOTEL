package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

/**
 * Created by android_sam on 2016. 9. 19..
 */
public class RegisterCouponLayout extends BaseLayout implements View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onRegisterCoupon(String couponCode);
    }

    public RegisterCouponLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {

    }

    @Override
    public void onClick(View v)
    {

    }
}
