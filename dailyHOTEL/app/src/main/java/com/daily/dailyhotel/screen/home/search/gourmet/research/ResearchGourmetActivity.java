package com.daily.dailyhotel.screen.home.search.gourmet.research;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchGourmetActivity extends BaseActivity<ResearchGourmetPresenter>
{
    static final int REQUEST_CODE_SUGGEST = 10000;
    static final int REQUEST_CODE_CALENDAR = 10001;
    static final int REQUEST_CODE_DETAIL = 10002;
    static final int REQUEST_CODE_SEARCH_RESULT = 10003;


    static final String INTENT_EXTRA_DATA_OPEN_DATE_TIME = "openDateTime";
    static final String INTENT_EXTRA_DATA_CLOSE_DATE_TIME = "closeDateTime";
    static final String INTENT_EXTRA_DATA_CURRENT_DATE_TIME = "currentDateTime";
    static final String INTENT_EXTRA_DATA_DAILY_DATE_TIME = "dailyDateTime";

    public static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    public static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";

    public static Intent newInstance(Context context, String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime//
        , String visitDateTime, GourmetSuggest suggest)
    {
        Intent intent = new Intent(context, ResearchGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_OPEN_DATE_TIME, openDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CLOSE_DATE_TIME, closeDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CURRENT_DATE_TIME, currentDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_DAILY_DATE_TIME, dailyDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(suggest));

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
    protected ResearchGourmetPresenter createInstancePresenter()
    {
        return new ResearchGourmetPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
