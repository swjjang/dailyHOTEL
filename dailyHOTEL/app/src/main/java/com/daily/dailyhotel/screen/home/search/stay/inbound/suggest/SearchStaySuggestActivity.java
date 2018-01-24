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

    public static Intent newInstance(Context context, String checkInDate, String checkOutDate)
    {
        Intent intent = new Intent(context, SearchStaySuggestActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE, checkOutDate);

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
    protected SearchStaySuggestPresenter createInstancePresenter()
    {
        return new SearchStaySuggestPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
