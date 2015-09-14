package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.twoheart.dailyhotel.R;

public abstract class PlaceReceiptActivity extends BaseActivity
{
	private int mBookingIndex;
	protected boolean mIsFullscreen;

	protected abstract void requestReceiptDetail(int index);

	protected abstract View getLayout();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setActionBar(R.string.frag_issuing_receipt);

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
			mIsFullscreen = !mIsFullscreen;
			updateFullscreenStatus(mIsFullscreen);
		} else
		{
			super.onBackPressed();
		}
	}

	protected void updateFullscreenStatus(boolean bUseFullscreen)
	{
		View actionBar = findViewById(R.id.toolbar_actionbar);
		View underLine = findViewById(R.id.toolbar_actionbarUnderLine);

		if (bUseFullscreen)
		{
			actionBar.setVisibility(View.INVISIBLE);
			underLine.setVisibility(View.INVISIBLE);

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else
		{
			actionBar.setVisibility(View.VISIBLE);
			underLine.setVisibility(View.VISIBLE);

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}
