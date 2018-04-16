package com.daily.dailyhotel.screen.common.web;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class DailyWebActivity extends BaseActivity<DailyWebPresenter>
{
    public static final String INTENT_EXTRA_DATA_TITLE_TEXT = "titleText";
    public static final String INTENT_EXTRA_DATA_URL = "url";

    public static Intent newInstance(Context context, String titleText, String url)
    {
        Intent intent = new Intent(context, DailyWebActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE_TEXT, titleText);
        intent.putExtra(INTENT_EXTRA_DATA_URL, url);

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
    protected DailyWebPresenter createInstancePresenter()
    {
        return new DailyWebPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
