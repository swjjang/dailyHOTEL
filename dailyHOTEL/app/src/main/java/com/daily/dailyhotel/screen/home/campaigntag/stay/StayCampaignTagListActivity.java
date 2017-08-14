package com.daily.dailyhotel.screen.home.campaigntag.stay;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class StayCampaignTagListActivity extends BaseActivity<StayCampaignTagListPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TYPE = "type";
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";
    protected static final String INTENT_EXTRA_DATA_CHECK_IN_DATE = "checkInDate";
    protected static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE = "checkOutDate";
    protected static final String INTENT_EXTRA_DATA_AFTER_DAY = "afterDay";
    protected static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    protected static final int TYPE_DEFAULT = 0;
    protected static final int TYPE_DATE = 1;
    protected static final int TYPE_AFTER_DAY = 2;

    public static Intent newInstance(Context context, int index, String hashTag, String checkInDateTime, String checkOutDateTime)
    {
        Intent intent = new Intent(context, StayCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DATE);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE, checkOutDateTime);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String hashTag, int afterDay, int nights)
    {
        Intent intent = new Intent(context, StayCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_AFTER_DAY);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_AFTER_DAY, afterDay);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String hashTag, StayBookingDay stayBookingDay)
    {
        Intent intent = new Intent(context, StayCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DEFAULT);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
//        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    @NonNull
    @Override
    protected StayCampaignTagListPresenter createInstancePresenter()
    {
        return new StayCampaignTagListPresenter(this);
    }
}
