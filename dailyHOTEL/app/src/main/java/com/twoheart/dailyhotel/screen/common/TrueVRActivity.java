package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daily.base.widget.DailyWebView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrueVRActivity extends WebViewActivity implements View.OnClickListener
{
    private List<TrueVRParams> mTrueVRParamsList;
    private TextView mProductNameTextView;
    private TextView mPageTextView;

    private int mCurrentPage;

    public static Intent newInstance(Context context, ArrayList<TrueVRParams> list)
    {
        Intent intent = new Intent(context, TrueVRActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVIEW_LIST, list);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trueview);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        mTrueVRParamsList = intent.getParcelableArrayListExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVIEW_LIST);

        if (mTrueVRParamsList == null || mTrueVRParamsList.size() == 0)
        {
            finish();
            return;
        }

        initWebView();
        initToolbar();
        initLayout((DailyWebView) mWebView);

        setTrueViewPage(mCurrentPage);
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
        mProductNameTextView = (TextView) findViewById(R.id.productNameTextView);
        mPageTextView = (TextView) findViewById(R.id.pageTextView);
        View nextView = findViewById(R.id.nextView);

        if (mTrueVRParamsList != null && mTrueVRParamsList.size() == 1)
        {
            nextView.setVisibility(View.GONE);
        } else
        {
            nextView.setVisibility(View.VISIBLE);
            nextView.setOnClickListener(this);
        }
    }

    private void setTrueViewPage(int page)
    {
        if (mWebView == null || page >= mTrueVRParamsList.size() || page < 0)
        {
            return;
        }

        TrueVRParams trueVRParams = mTrueVRParamsList.get(page);

        if (trueVRParams == null)
        {
            return;
        }

        mCurrentPage = page;

        mWebView.loadUrl(trueVRParams.url);
        mProductNameTextView.setText(trueVRParams.name);
        mPageTextView.setText(String.format(Locale.KOREA, "%d / %d", page + 1, mTrueVRParamsList.size()));
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(this).recordScreen(this, Screen.ABOUT, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.nextView:
                setTrueViewPage(mCurrentPage + 1);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
