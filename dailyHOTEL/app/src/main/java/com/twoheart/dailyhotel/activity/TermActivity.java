package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;

public class TermActivity extends WebViewActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_term);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.actionbar_title_term_activity));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_TERMS));
    }
}
