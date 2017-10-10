package com.daily.dailyhotel.screen.home.campaigntag.stay;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class StayCampaignTagListActivity extends BaseActivity<StayCampaignTagListPresenter>
{
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";

    protected static final int REQUEST_CODE_CALL = 10000;

    public static Intent newInstance(Context context, int index, String hashTag, StayBookingDay stayBookingDay)
    {
        Intent intent = new Intent(context, StayCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);

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
    protected StayCampaignTagListPresenter createInstancePresenter()
    {
        return new StayCampaignTagListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
