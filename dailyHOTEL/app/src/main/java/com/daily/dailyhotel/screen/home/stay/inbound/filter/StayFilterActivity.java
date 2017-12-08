package com.daily.dailyhotel.screen.home.stay.inbound.filter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.parcel.StayFitlerParcel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayFilterActivity extends BaseActivity<StayFilterPresenter>
{
    static final String INTENT_EXTRA_DATA_VIEW_TYPE = "viewType";
    static final String INTENT_EXTRA_DATA_STAY_FILTER = "stayFilter";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StayFilterActivity.class);

        return intent;
    }

    public static Intent newInstance(Context context, String checkInDateTime, String checkOutDateTime, Constants.ViewType viewType, StayFilter stayFilter)
    {
        Intent intent = new Intent(context, StayFilterActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_VIEW_TYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_STAY_FILTER, new StayFitlerParcel(stayFilter));

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
    protected StayFilterPresenter createInstancePresenter()
    {
        return new StayFilterPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
