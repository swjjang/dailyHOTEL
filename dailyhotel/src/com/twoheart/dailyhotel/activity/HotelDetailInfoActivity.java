package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelDetailInfoActivity extends BaseActivity
{
	private HotelDetail mHotelDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHotelDetail = new HotelDetail();
		Intent intent = getIntent();

		if (intent != null)
		{
			mHotelDetail = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
		}

		if (mHotelDetail == null)
		{
			Util.restartApp(this);
			return;
		}

		initLayout(mHotelDetail);
	}

	private void initLayout(HotelDetail hotelDetail)
	{
		setContentView(R.layout.activity_hoteldetail_info);
		setActionBar(R.string.actionbar_title_hoteldetailinfo_activity);
	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}
}
