package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public abstract class PlaceReceiptActivity extends BaseActivity
{
    protected boolean mIsFullscreen;
    private int mBookingIndex;
    private DailyToolbarLayout mDailyToolbarLayout;

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
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.frag_issuing_receipt), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
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
            updateFullscreenStatus(false);
        } else
        {
            super.onBackPressed();
        }
    }

    protected void updateFullscreenStatus(boolean bUseFullscreen)
    {
        if (bUseFullscreen)
        {
            mDailyToolbarLayout.setToolbarVisibility(false, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else
        {
            mDailyToolbarLayout.setToolbarVisibility(true, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
