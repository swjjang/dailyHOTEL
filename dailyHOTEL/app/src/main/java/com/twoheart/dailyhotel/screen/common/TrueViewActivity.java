package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.widget.DailyWebView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

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

        setContentView(R.layout.activity_about);

        Intent intent = getIntent();

        setWebView(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_URL));

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_trueview), new View.OnClickListener()
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
        final View topButtonView = findViewById(R.id.topButtonView);
        //        topButtonView.setOnClickListener(new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                smoothScrollTop(dailyWebView);
        //            }
        //        });
        //
        topButtonView.setVisibility(View.GONE);
        //
        //        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        //        {
        //            @Override
        //            public void onScroll(int l, int t, int oldl, int oldt)
        //            {
        //                if (t == 0)
        //                {
        //                    topButtonView.setVisibility(View.GONE);
        //                } else
        //                {
        //                    topButtonView.setVisibility(View.VISIBLE);
        //                }
        //            }
        //        });
        //
        View homeButtonView = findViewById(R.id.homeButtonView);
        //        homeButtonView.setOnClickListener(new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
        //                finish();
        //            }
        //        });

        homeButtonView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(this).recordScreen(this, Screen.ABOUT, null);

        super.onStart();
    }
}
