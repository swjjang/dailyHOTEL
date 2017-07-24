package com.daily.dailyhotel.screen.home.gourmet.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetThankYouActivity extends BaseActivity<GourmetThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName";
    static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    static final String INTENT_EXTRA_DATA_VISIT_TIME = "visitTime";
    static final String INTENT_EXTRA_DATA_PRODUCT_TYPE = "productType";
    static final String INTENT_EXTRA_DATA_PRODUCT_COUNT = "productCount";

    public static Intent newInstance(Context context, String gourmetName, String imageUrl//
        , String visitDate, String visitTime, String productType, int productCount, GourmetThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_TIME, visitTime);
        intent.putExtra(INTENT_EXTRA_DATA_PRODUCT_TYPE, productType);
        intent.putExtra(INTENT_EXTRA_DATA_PRODUCT_COUNT, productCount);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected GourmetThankYouPresenter createInstancePresenter()
    {
        return new GourmetThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
