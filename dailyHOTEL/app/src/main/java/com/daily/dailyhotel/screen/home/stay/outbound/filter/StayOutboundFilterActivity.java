package com.daily.dailyhotel.screen.home.stay.outbound.filter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundFilterActivity extends BaseActivity<StayOutboundFilterPresenter>
{
    public static final String INTENT_EXTRA_DATA_SORT = "sort";
    public static final String INTENT_EXTRA_DATA_DEFAULT_SORT = "defaultSort";
    public static final String INTENT_EXTRA_DATA_RATING = "rating";
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";
    public static final String INTENT_EXTRA_DATA_ENABLEDLINES = "enabledLines";

    static final int REQUEST_CODE_STAYOUTBOUND_PERMISSION_MANAGER = 10000;
    static final int REQUEST_CODE_STAYOUTBOUND_SETTING_LOCATION = 10001;

    public static Intent newInstance(Context context, StayOutboundFilters stayOutboundFilters, String viewType)
    {
        Intent intent = new Intent(context, StayOutboundFilterActivity.class);

        if (stayOutboundFilters != null)
        {
            intent.putExtra(INTENT_EXTRA_DATA_SORT, stayOutboundFilters.sortType.name());
            intent.putExtra(INTENT_EXTRA_DATA_DEFAULT_SORT, stayOutboundFilters.defaultSortType.name());
            intent.putExtra(INTENT_EXTRA_DATA_RATING, stayOutboundFilters.rating);
            intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayOutboundFilterPresenter createInstancePresenter()
    {
        return new StayOutboundFilterPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
