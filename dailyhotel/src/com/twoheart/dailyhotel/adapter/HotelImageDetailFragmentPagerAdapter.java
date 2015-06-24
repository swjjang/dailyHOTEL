package com.twoheart.dailyhotel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.fragment.ImageDetailFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.ui.LoopViewPager;

public class HotelImageDetailFragmentPagerAdapter extends FragmentPagerAdapter
{
	private HotelDetail mHotelDetail;

	public HotelImageDetailFragmentPagerAdapter(FragmentManager fm, HotelDetail mHotelDetail)
	{
		super(fm);
		this.mHotelDetail = mHotelDetail;
	}

	@Override
	public Fragment getItem(int position)
	{
		position = LoopViewPager.toRealPosition(position, getCount());

		ImageDetailFragment item = ImageDetailFragment.newInstance(mHotelDetail.getImageUrl().get(position % getCount()));

		return item;
	}

	@Override
	public int getCount()
	{
		return mHotelDetail.getImageUrl().size();
	}
}
