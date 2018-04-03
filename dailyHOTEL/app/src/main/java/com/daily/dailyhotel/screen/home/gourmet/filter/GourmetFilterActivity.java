package com.daily.dailyhotel.screen.home.gourmet.filter;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetFilterParcel;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.twoheart.dailyhotel.R;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetFilterActivity extends BaseActivity<GourmetFilterPresenter>
{
    static final int REQUEST_CODE_PERMISSION_MANAGER = 10000;
    static final int REQUEST_CODE_SETTING_LOCATION = 10001;

    static final String INTENT_EXTRA_DATA_LIST_TYPE = "listType";
    static final String INTENT_EXTRA_DATA_VIEW_TYPE = "viewType";
    public static final String INTENT_EXTRA_DATA_FILTER = "filter";
    static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_LOCATION = "location";
    static final String INTENT_EXTRA_DATA_RADIUS = "radius";
    static final String INTENT_EXTRA_DATA_SEARCH_WORD = "searchWord";

    public enum ListType
    {
        DEFAULT,
        SEARCH,
    }

    public static Intent newInstance(Context context, ListType listType, String visitDateTime//
        , String viewType, GourmetFilter filter//
        , GourmetSuggest suggest, Location location, float radius, String searchWord)
    {
        Intent intent = new Intent(context, GourmetFilterActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_LIST_TYPE, listType.name());
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_VIEW_TYPE, viewType);
        intent.putExtra(INTENT_EXTRA_DATA_FILTER, new GourmetFilterParcel(filter));
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_RADIUS, radius);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCH_WORD, searchWord);

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
    protected GourmetFilterPresenter createInstancePresenter()
    {
        return new GourmetFilterPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
