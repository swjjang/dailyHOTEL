package com.daily.dailyhotel.screen.home.campaigntag.gourmet;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class GourmetCampaignTagListActivity extends BaseActivity<GourmetCampaignTagListPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TYPE = "type";
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";
    protected static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    protected static final String INTENT_EXTRA_DATA_AFTER_DAY = "afterDay";

    protected static final int TYPE_DEFAULT = 0;
    protected static final int TYPE_DATE = 1;
    protected static final int TYPE_AFTER_DAY = 2;

    public static Intent newInstance(Context context, int index, String hashTag, String visitDateTime)
    {
        Intent intent = new Intent(context, GourmetCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DATE);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDateTime);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String hashTag, int afterDay)
    {
        Intent intent = new Intent(context, GourmetCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_AFTER_DAY);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_AFTER_DAY, afterDay);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String hashTag, GourmetBookingDay gourmetBookingDay)
    {
        Intent intent = new Intent(context, GourmetCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DEFAULT);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);

        return intent;
    }

    @NonNull
    @Override
    protected GourmetCampaignTagListPresenter createInstancePresenter()
    {
        return new GourmetCampaignTagListPresenter(this);
    }
}
