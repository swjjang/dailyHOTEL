package com.twoheart.dailyhotel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.fragment.ImageViewFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.viewpagerindicator.Loopable;

public class HotelImageFragmentPagerAdapter extends FragmentPagerAdapter implements Loopable
{
	private HotelDetail mHotelDetail;
	private int curPosReal;

	private HotelTabActivity.OnUserActionListener mOnUserActionListener;

	public HotelImageFragmentPagerAdapter(FragmentManager fm, HotelDetail mHotelDetail)
	{
		super(fm);
		this.mHotelDetail = mHotelDetail;
		this.curPosReal = 0;
	}

	public void setOnUserActionListener(HotelTabActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	@Override
	public Fragment getItem(int position)
	{
		position = getRealPos(position);
		curPosReal = position;
		ImageViewFragment item = ImageViewFragment.newInstance(mHotelDetail.getImageUrl().get(position), mHotelDetail);
		item.setOnUserActionListener(mOnUserActionListener);

		return item;
	}

	@Override
	public int getCount()
	{
		if (mHotelDetail.getImageUrl().size() == 0)
			return 0;
		return Integer.MAX_VALUE; // 루프를 위하여 뷰페이지를 여러개 만듬.
	}

	@Override
	public int getRealCount()
	{
		return mHotelDetail.getImageUrl().size();
	}

	@Override
	public int getRealPos(int fakePos)
	{
		return fakePos % getRealCount();
	}

	@Override
	public int getRealCurPos()
	{
		return curPosReal;
	}

}
