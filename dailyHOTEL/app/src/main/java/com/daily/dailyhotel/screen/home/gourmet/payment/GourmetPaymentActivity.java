package com.daily.dailyhotel.screen.home.gourmet.payment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.parcel.GourmetCartParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetPaymentActivity extends BaseActivity<GourmetPaymentPresenter>
{
    static final int REQUEST_CODE_CARD_MANAGER = 10000;
    static final int REQUEST_CODE_REGISTER_CARD = 10001;
    static final int REQUEST_CODE_REGISTER_CARD_PAYMENT = 10002;
    static final int REQUEST_CODE_REGISTER_PHONE_NUMBER = 10003;
    static final int REQUEST_CODE_CALL = 10004;
    static final int REQUEST_CODE_THANK_YOU = 10005;
    static final int REQUEST_CODE_PAYMENT_WEB_CARD = 10006;
    static final int REQUEST_CODE_PAYMENT_WEB_PHONE = 10007;
    static final int REQUEST_CODE_PAYMENT_WEB_VBANK = 10008;
    static final int REQUEST_CODE_COUPON_LIST = 10009;
    static final int REQUEST_CODE_LOGIN_IN = 10010;
    static final int REQUEST_CODE_PROFILE_UPDATE = 10011;

    static final String INTENT_EXTRA_DATA_GOURMET_CART = "gourmetCart";

    public static Intent newInstance(Context context, GourmetCart gourmetCart, GourmetPaymentAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetPaymentActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_CART, new GourmetCartParcel(gourmetCart));
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

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
    protected GourmetPaymentPresenter createInstancePresenter()
    {
        return new GourmetPaymentPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
