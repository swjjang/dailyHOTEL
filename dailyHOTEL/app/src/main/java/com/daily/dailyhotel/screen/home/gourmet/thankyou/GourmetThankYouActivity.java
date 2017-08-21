package com.daily.dailyhotel.screen.home.gourmet.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetThankYouActivity extends BaseActivity<GourmetThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName";
    static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    static final String INTENT_EXTRA_DATA_MENU_NAME = "menuName";
    static final String INTENT_EXTRA_DATA_MENU_COUNT = "menuCount";
    static final String INTENT_EXTRA_DATA_RESERVATION_ID = "reservationId";

    public static Intent newInstance(Context context, String gourmetName, String imageUrl//
        , String visitDateTime, String menuName, int menuCount
        , int reservationId, GourmetThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_NAME, menuName);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_COUNT, menuCount);
        intent.putExtra(INTENT_EXTRA_DATA_RESERVATION_ID, reservationId);
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
