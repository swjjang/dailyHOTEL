package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStaySuggestActivity extends BaseActivity<SearchStaySuggestPresenter>
{
    public static final String INTENT_EXTRA_DATA_CHECK_IN_DATE = "checkInDate";
    public static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE = "checkOutDate";
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    public static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_SUGGEST_2 = "suggest2";

    public static final int REQUEST_CODE_SPEECH_INPUT = 10000;
    public static final int REQUEST_CODE_SETTING_LOCATION = 10001;
    public static final int REQUEST_CODE_PERMISSION_MANAGER = 10002;

    public static final int RECENTLY_PLACE_MAX_REQUEST_COUNT = 10;

    public static Intent newInstance(Context context, String keyword, String checkInDate, String checkOutDate)
    {
        Intent intent = new Intent(context, SearchStaySuggestActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE, checkOutDate);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);

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
    protected SearchStaySuggestPresenter createInstancePresenter()
    {
        return new SearchStaySuggestPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
