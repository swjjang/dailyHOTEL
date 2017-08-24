package com.twoheart.dailyhotel.screen.information.terms;

import android.os.Bundle;
import android.view.View;

import com.daily.base.widget.DailyWebView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;

public class LicenseActivity extends WebViewActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_license);
        setWebView(DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStaticUrlLicense());

        initToolbar();
        initLayout((DailyWebView) mWebView);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.frag_opensource_license);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
}
