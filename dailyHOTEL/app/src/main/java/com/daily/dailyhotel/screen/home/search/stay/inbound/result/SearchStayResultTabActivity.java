package com.daily.dailyhotel.screen.home.search.stay.inbound.result;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayResultTabActivity extends BaseActivity<SearchStayResultTabPresenter>
{
    public static final int REQUEST_CODE_CALENDAR = 10000;
    public static final int REQUEST_CODE_FILTER = 10001;
    public static final int REQUEST_CODE_RESEARCH = 10002;
    public static final int REQUEST_CODE_DETAIL = 10005;
    public static final int REQUEST_CODE_PREVIEW = 10006;
    public static final int REQUEST_CODE_PERMISSION_MANAGER = 10007;
    public static final int REQUEST_CODE_SETTING_LOCATION = 10008;
    public static final int REQUEST_CODE_WISH_DIALOG = 10009;
    public static final int REQUEST_CODE_CALL = 10010;

    public static final String INTENT_EXTRA_DATA_CATEGORY_TYPE = "categoryType";
    public static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    public static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    public static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_INPUT_KEYWORD = "inputKeyword";

    public static Intent newInstance(Context context, DailyCategoryType categoryType, String checkInDateTime, String checkOutDateTime//
        , StaySuggest suggest, String inputKeyWord, SearchStayResultAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, SearchStayResultTabActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY_TYPE, categoryType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_INPUT_KEYWORD, inputKeyWord);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, SearchStayResultTabActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

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
    protected SearchStayResultTabPresenter createInstancePresenter()
    {
        return new SearchStayResultTabPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
