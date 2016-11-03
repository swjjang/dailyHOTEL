package com.twoheart.dailyhotel.screen.information;

import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyWebView;

public class FAQActivity extends WebViewActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faq);
        setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_FAQ));

        initToolbar();
        initLayout((DailyWebView) webView);

//        setEnabledProgress(false);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.frag_faqs), new View.OnClickListener()
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

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
