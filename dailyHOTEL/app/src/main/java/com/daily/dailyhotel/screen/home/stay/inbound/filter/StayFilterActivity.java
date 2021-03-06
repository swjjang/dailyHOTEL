package com.daily.dailyhotel.screen.home.stay.inbound.filter;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayFilterActivity extends BaseActivity<StayFilterPresenter>
{
    static final int REQUEST_CODE_PERMISSION_MANAGER = 10000;
    static final int REQUEST_CODE_SETTING_LOCATION = 10001;

    static final String INTENT_EXTRA_DATA_LIST_TYPE = "listType";
    static final String INTENT_EXTRA_DATA_VIEW_TYPE = "viewType";
    public static final String INTENT_EXTRA_DATA_FILTER = "filter";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    static final String INTENT_EXTRA_DATA_CATEGORIES = "categories";
    public static final String INTENT_EXTRA_DATA_LOCATION = "location";
    static final String INTENT_EXTRA_DATA_RADIUS = "radius";
    static final String INTENT_EXTRA_DATA_CATEGORY_TYPE = "categoryType";

    public static Intent newInstance(Context context, SearchStayResultTabPresenter.ListType listType//
        , String checkInDateTime, String checkOutDateTime//
        , DailyCategoryType categoryType, String viewType, StayFilter filter, StaySuggest suggest//
        , ArrayList<String> categories, Location location, float radius)
    {
        Intent intent = new Intent(context, StayFilterActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_LIST_TYPE, listType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY_TYPE, categoryType.name());
        intent.putExtra(INTENT_EXTRA_DATA_VIEW_TYPE, viewType);
        intent.putExtra(INTENT_EXTRA_DATA_FILTER, new StayFilterParcel(filter));
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORIES, categories);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_RADIUS, radius);

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
    protected StayFilterPresenter createInstancePresenter()
    {
        return new StayFilterPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
