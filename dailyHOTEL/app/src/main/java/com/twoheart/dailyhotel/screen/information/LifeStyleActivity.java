package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.widget.DailyWebView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;

public class LifeStyleActivity extends WebViewActivity
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, LifeStyleActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlLifeStyleProject());

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.label_dailyh_lifestyle);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int i = 0;

                while (mWebView.canGoBackOrForward(--i))
                {
                }

                if (i == -1)
                {
                    finish();
                } else
                {
                    mWebView.goBackOrForward(i + 1);
                }
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
                smoothScrollTop(dailyWebView);
            }
        });

        topButtonView.setVisibility(View.GONE);

        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    topButtonView.setVisibility(View.GONE);
                } else
                {
                    topButtonView.setVisibility(View.VISIBLE);
                }
            }
        });

        View homeButtonView = findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(this).recordScreen(this, Screen.ABOUT, null);

        super.onStart();
    }
}
