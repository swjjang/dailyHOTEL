package com.daily.dailyhotel.screen.mydaily.coupon.select.stay.outbound;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectStayOutboundCouponDialogActivity extends BaseActivity<SelectStayOutboundCouponDialogPresenter>
{
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_RATE_CODE = "rateCode";
    static final String INTENT_EXTRA_DATA_RATE_KEY = "rateKey";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE_CODE = "roomTypeCode";

    public static final String INTENT_EXTRA_MAX_COUPON_AMOUNT = "maxCouponAmount";
    public static final String INTENT_EXTRA_SELECT_COUPON = "selectedCoupon";

    public static Intent newInstance(Context context, String checkInDateTime, String checkOutDateTime//
        , int stayIndex, String stayName, String rateCode, String rateKey, String roomTypeCode)
    {
        Intent intent = new Intent(context, SelectStayOutboundCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_CODE, rateCode);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_KEY, rateKey);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE_CODE, roomTypeCode);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @NonNull
    @Override
    protected SelectStayOutboundCouponDialogPresenter createInstancePresenter()
    {
        return new SelectStayOutboundCouponDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }
}
