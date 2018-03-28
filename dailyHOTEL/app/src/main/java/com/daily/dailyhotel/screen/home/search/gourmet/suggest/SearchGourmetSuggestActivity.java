package com.daily.dailyhotel.screen.home.search.gourmet.suggest;


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
public class SearchGourmetSuggestActivity extends BaseActivity<SearchGourmetSuggestPresenter>
{
    public static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    public static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_ORIGIN_SERVICE_TYPE = "originServiceType";

    public static final int REQUEST_CODE_SPEECH_INPUT = 10000;
    public static final int REQUEST_CODE_SETTING_LOCATION = 10001;
    public static final int REQUEST_CODE_PERMISSION_MANAGER = 10002;

    public static final int RECENTLY_PLACE_MAX_REQUEST_COUNT = 10;

    public static Intent newInstance(Context context, String keyword, String visitDate)
    {
        Intent intent = new Intent(context, SearchGourmetSuggestActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
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
    protected SearchGourmetSuggestPresenter createInstancePresenter()
    {
        return new SearchGourmetSuggestPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
