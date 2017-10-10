package com.daily.dailyhotel.screen.home.campaigntag.gourmet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class GourmetCampaignTagListActivity extends BaseActivity<GourmetCampaignTagListPresenter>
{
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";

    protected static final int REQUEST_CODE_CALL = 10000;

    public static Intent newInstance(Context context, int index, String hashTag, GourmetBookingDay gourmetBookingDay)
    {
        Intent intent = new Intent(context, GourmetCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);

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
    protected GourmetCampaignTagListPresenter createInstancePresenter()
    {
        return new GourmetCampaignTagListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
