package com.daily.dailyhotel.screen.home.search;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchActivity extends BaseActivity<SearchPresenter>
{
    public static final int REQUEST_CODE_STAY_SUGGEST = 10000;
    public static final int REQUEST_CODE_STAY_CALENDAR = 10003;
    public static final int REQUEST_CODE_STAY_SEARCH_RESULT = 10004;
    public static final int REQUEST_CODE_STAY_DETAIL = 10005;

    public static final int REQUEST_CODE_STAY_OUTBOUND_SUGGEST = 10010;
    public static final int REQUEST_CODE_STAY_OUTBOUND_CALENDAR = 10011;
    public static final int REQUEST_CODE_STAY_OUTBOUND_PEOPLE = 10012;
    public static final int REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT = 10013;
    public static final int REQUEST_CODE_STAY_OUTBOUND_DETAIL = 10014;

    public static final int REQUEST_CODE_GOURMET_SUGGEST = 10020;
    public static final int REQUEST_CODE_GOURMET_CALENDAR = 10021;
    public static final int REQUEST_CODE_GOURMET_SEARCH_RESULT = 10022;
    public static final int REQUEST_CODE_GOURMET_DETAIL = 10023;

    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    static final String INTENT_EXTRA_DATA_SERVICE_TYPE = "serviceType";

    public static Intent newInstance(Context context, Constants.ServiceType serviceType)
    {
        Intent intent = new Intent(context, SearchActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SERVICE_TYPE, serviceType.name());

        return intent;
    }

    public static Intent newInstance(Context context, Constants.ServiceType serviceType, String checkInDateTime, String checkOutDateTime)
    {
        Intent intent = new Intent(context, SearchActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SERVICE_TYPE, serviceType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);

        return intent;
    }

    public static Intent newInstance(Context context, Constants.ServiceType serviceType, String visitDateTime)
    {
        Intent intent = new Intent(context, SearchActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SERVICE_TYPE, serviceType.name());
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, SearchActivity.class);

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
    protected SearchPresenter createInstancePresenter()
    {
        return new SearchPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
