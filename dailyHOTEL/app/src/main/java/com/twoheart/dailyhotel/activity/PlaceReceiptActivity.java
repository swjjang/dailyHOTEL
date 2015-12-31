package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

public abstract class PlaceReceiptActivity extends BaseActivity
{
    protected boolean mIsFullscreen;
    private int mBookingIndex;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mToolbarUnderline;

    protected abstract void requestReceiptDetail(int index);

    protected abstract View getLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        initToolbar();

        Intent intent = getIntent();

        mBookingIndex = -1;

        if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX) == true)
        {
            mBookingIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, -1);
        }

        if (mBookingIndex < 0)
        {
            finish();
            return;
        }

        mIsFullscreen = false;
    }

    private void initToolbar()
    {
        mToolbarUnderline = findViewById(R.id.toolbarUnderline);
        mToolbarUnderline.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.frag_issuing_receipt));
    }

    @Override
    protected void onResume()
    {
        lockUI();

        requestReceiptDetail(mBookingIndex);

        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (mIsFullscreen == true)
        {
            mIsFullscreen = false;
            updateFullscreenStatus(mIsFullscreen);
        } else
        {
            super.onBackPressed();
        }
    }

    protected void updateFullscreenStatus(boolean bUseFullscreen)
    {
        if (bUseFullscreen)
        {
            mDailyToolbarLayout.setToolbarVisibility(false);
            mToolbarUnderline.setVisibility(View.INVISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else
        {
            mDailyToolbarLayout.setToolbarVisibility(true);
            mToolbarUnderline.setVisibility(View.VISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
