package com.daily.dailyhotel.screen.stay.outbound.search;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundSearchActivity extends BaseActivity<StayOutboundSearchPresenter>
{
    static final int REQUEST_CODE_CALENDAR = 10000;
    static final int REQUEST_CODE_SUGGEST = 10001;
    static final int REQUEST_CODE_SELECT_PERSONS = 10002;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StayOutboundSearchActivity.class);
        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayOutboundSearchActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

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
    protected StayOutboundSearchPresenter createInstancePresenter()
    {
        return new StayOutboundSearchPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
