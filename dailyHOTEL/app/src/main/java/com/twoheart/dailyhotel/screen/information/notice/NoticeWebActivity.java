package com.twoheart.dailyhotel.screen.information.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyWebView;

public class NoticeWebActivity extends WebViewActivity
{
    private static final String INTENT_EXTRA_DATA_TITLE = "title";
    private static final String INTENT_EXTRA_DATA_URL = "url";

    public static Intent newInstance(Context context, String title, String url)
    {
        Intent intent = new Intent(context, NoticeWebActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String url = intent.getStringExtra(INTENT_EXTRA_DATA_URL);

        if (Util.isTextEmpty(url) == true)
        {
            finish();
            return;
        } else
        {
            setWebView(url);
        }

        initToolbar(title);
        initLayout((DailyWebView) webView);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.MENU_NOTICELIST);
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(title, new View.OnClickListener()
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
        topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dailyWebView.setScrollY(0);
            }
        });

        topButtonView.setVisibility(View.INVISIBLE);

        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    topButtonView.setVisibility(View.INVISIBLE);
                } else
                {
                    topButtonView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
