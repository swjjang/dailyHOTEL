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
public class StayOutboundSearchSuggestActivity extends BaseActivity<StayOutboundSearchSuggestPresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StayOutboundSearchSuggestActivity.class);
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
    protected StayOutboundSearchSuggestPresenter createInstancePresenter()
    {
        return new StayOutboundSearchSuggestPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
