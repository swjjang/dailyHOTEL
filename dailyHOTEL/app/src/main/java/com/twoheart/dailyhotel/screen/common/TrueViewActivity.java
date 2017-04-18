package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.widget.DailyWebView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

public class TrueViewActivity extends WebViewActivity
{
    public static Intent newInstance(Context context, String url)
    {
        Intent intent = new Intent(context, TrueViewActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trueview);

        Intent intent = getIntent();

        setWebView(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_URL));

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        View backView = findViewById(R.id.backView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(final DailyWebView dailyWebView)
    {
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(this).recordScreen(this, Screen.ABOUT, null);

        super.onStart();
    }
}
