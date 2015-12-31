package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

public class TermActivity extends WebViewActivity
{
    private DailyToolbarLayout mDailyToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_term);

        initToolbar();
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_term_activity));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_TERMS));
    }
}
