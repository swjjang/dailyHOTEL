package com.daily.dailyhotel.screen.home.gourmet.payment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;

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

    static final String INTENT_EXTRA_DATA_GOURMET_INDEX = "gourmetIndex";
    static final String INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_MENU_PRICE = "menuPrice";
    static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    static final String INTENT_EXTRA_DATA_MENU_INDEX = "menuIndex";
    static final String INTENT_EXTRA_DATA_OVERSEAS = "overseas";
    static final String INTENT_EXTRA_DATA_CATEGORY = "category";
    static final String INTENT_EXTRA_DATA_MENU_NAME = "menuName";


    public static Intent newInstance(Context context, int gourmetIndex, String gourmetName, String imageUrl//
        , int menuIndex, int menuPrice, String menuName, String visitDate, boolean overseas//
        , String category, GourmetPaymentAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetPaymentActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_INDEX, gourmetIndex);
        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_INDEX, menuIndex);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_PRICE, menuPrice);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_NAME, menuName);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
        intent.putExtra(INTENT_EXTRA_DATA_OVERSEAS, overseas);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, category);
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
