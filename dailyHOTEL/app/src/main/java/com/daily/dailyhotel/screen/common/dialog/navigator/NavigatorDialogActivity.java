package com.daily.dailyhotel.screen.common.dialog.navigator;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class NavigatorDialogActivity extends BaseActivity<NavigatorDialogPresenter>
{
    static final String INTENT_EXTRA_DATA_TITLE = "title";
    static final String INTENT_EXTRA_DATA_LATITUDE = "latitude";
    static final String INTENT_EXTRA_DATA_LONGITUDE = "longitude";
    static final String INTENT_EXTRA_DATA_OVERSEAS = "overseas";

    public static Intent newInstance(Context context, String title, double latitude, double longitude//
        , boolean overseas, NavigatorAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, NavigatorDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_LATITUDE, latitude);
        intent.putExtra(INTENT_EXTRA_DATA_LONGITUDE, longitude);
        intent.putExtra(INTENT_EXTRA_DATA_OVERSEAS, overseas);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected NavigatorDialogPresenter createInstancePresenter()
    {
        return new NavigatorDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }
}
