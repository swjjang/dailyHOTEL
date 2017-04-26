package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyWebView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TrueVRActivity extends WebViewActivity implements View.OnClickListener
{
    private List<TrueVRParams> mTrueVRParamsList;
    private TextView mProductNameTextView;
    private TextView mCurrentPageTextView, mTotalPageTextView;
    private View mPageLayout;
    private View mPrevView, mNextView;

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
        mPageLayout = findViewById(R.id.pageLayout);

        if (mTrueVRParamsList == null && mTrueVRParamsList.size() == 1)
        {
            mPageLayout.setVisibility(View.GONE);
        } else
        {
            mPageLayout.setVisibility(View.VISIBLE);
        }

        mProductNameTextView = (TextView) findViewById(R.id.productNameTextView);
        mCurrentPageTextView = (TextView) findViewById(R.id.currentPageTextView);
        mTotalPageTextView = (TextView) findViewById(R.id.totalPageTextView);

        mNextView = findViewById(R.id.nextView);
        mPrevView = findViewById(R.id.prevView);

        mNextView.setOnClickListener(this);
        mPrevView.setOnClickListener(this);
    }

    private void setTrueViewPage(int page)
    {
        if (mWebView == null || page >= mTrueVRParamsList.size() || page < 0)
        {
            return;
        }

        int totalPage = mTrueVRParamsList.size();

        TrueVRParams trueVRParams = mTrueVRParamsList.get(page);

        if (trueVRParams == null)
        {
            return;
        }

        mCurrentPage = page;

        if (mPageLayout.getVisibility() == View.VISIBLE)
        {
            mCurrentPageTextView.setText(Integer.toString(mCurrentPage + 1));
            mTotalPageTextView.setText("/" + totalPage);

            if (page == 0)
            {
                mPrevView.setEnabled(false);
                mNextView.setEnabled(true);
            } else if (page == totalPage - 1)
            {
                mPrevView.setEnabled(true);
                mNextView.setEnabled(false);
            } else
            {
                mPrevView.setEnabled(true);
                mNextView.setEnabled(true);
            }
        }

        mWebView.loadUrl(trueVRParams.url);

        if (DailyTextUtils.isTextEmpty(trueVRParams.name) == true)
        {
            mProductNameTextView.setVisibility(View.INVISIBLE);
        } else
        {
            mProductNameTextView.setVisibility(View.VISIBLE);
            mProductNameTextView.setText(trueVRParams.name);
        }
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
            case R.id.prevView:
                setTrueViewPage(mCurrentPage - 1);
                break;

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
