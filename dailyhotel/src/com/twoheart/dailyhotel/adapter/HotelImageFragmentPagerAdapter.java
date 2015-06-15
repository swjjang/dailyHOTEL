package com.twoheart.dailyhotel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.fragment.ImageViewFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.ui.LoopViewPager;

public class HotelImageFragmentPagerAdapter extends FragmentPagerAdapter
{
	private HotelDetail mHotelDetail;

	private HotelTabActivity.OnUserActionListener mOnUserActionListener;

	public HotelImageFragmentPagerAdapter(FragmentManager fm, HotelDetail mHotelDetail)
	{
		super(fm);
		this.mHotelDetail = mHotelDetail;
	}

	public void setOnUserActionListener(HotelTabActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	@Override
	public Fragment getItem(int position)
	{
		position = LoopViewPager.toRealPosition(position, getCount());

		ImageViewFragment item = ImageViewFragment.newInstance(mHotelDetail.getImageUrl().get(position % getCount()), mHotelDetail);
		item.setOnUserActionListener(mOnUserActionListener);

		return item;
	}

	@Override
	public int getCount()
	{
		return mHotelDetail.getImageUrl().size();
	}
}
