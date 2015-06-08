package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelImageDetailFragmentPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.ui.LoopViewPager;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;

public class ImageDetailActivity extends BaseActivity implements Constants
{
	private HotelDetail mHotelDetail;
	private HotelImageDetailFragmentPagerAdapter mAdapter;
	private LoopViewPager mPager;
	private CirclePageIndicator mIndicator;
	private String mSelectedImageUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_detail);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			mHotelDetail = (HotelDetail) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
			mSelectedImageUrl = bundle.getString(NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL);
		}

		mPager = (LoopViewPager) findViewById(R.id.imagedetail_pager);
		mPager.setBoundaryCaching(true);
		mIndicator = (CirclePageIndicator) findViewById(R.id.imagedetail_indicator);

		mAdapter = new HotelImageDetailFragmentPagerAdapter(getSupportFragmentManager(), mHotelDetail);
		mPager.setAdapter(mAdapter);

		mIndicator.setViewPager(mPager);
		mIndicator.setSnap(true);
		mIndicator.setCurrentItem(findSelectedImage(mSelectedImageUrl));
	}

	private int findSelectedImage(String imageUrl)
	{
		for (int i = 0; i < mHotelDetail.getImageUrl().size(); i++)
		{
			if (mHotelDetail.getImageUrl().get(i).equals(imageUrl))
				return i;
		}
		return 0;
	}
}
