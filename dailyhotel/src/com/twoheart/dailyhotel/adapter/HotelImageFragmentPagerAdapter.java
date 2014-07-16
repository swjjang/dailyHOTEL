package com.twoheart.dailyhotel.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.twoheart.dailyhotel.fragment.ImageViewFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.viewpagerindicator.Loopable;

public class HotelImageFragmentPagerAdapter extends FragmentPagerAdapter implements Loopable {

	private HotelDetail mHotelDetail;
	private int curPosReal;
	private HashMap<String,ImageViewFragment> mPrevIvFrags;
	
	public HotelImageFragmentPagerAdapter(FragmentManager fm, 
			HotelDetail mHotelDetail) {
		super(fm);
		this.mHotelDetail = mHotelDetail;
		this.curPosReal = 0;
		mPrevIvFrags = new HashMap<String, ImageViewFragment>();
	}

	@Override
	public Fragment getItem(int position) {
		position = getRealPos(position);
		curPosReal = position;
		
		ImageViewFragment item = mPrevIvFrags.get(position);
		
		if (item == null) {
			item = ImageViewFragment.newInstance(mHotelDetail.getImageUrl()
					.get(position), mHotelDetail);
			mPrevIvFrags.put(mHotelDetail.getImageUrl()
					.get(position), item);
		}
		
		return item;
	}

	@Override
	public int getCount() {
		if(mHotelDetail.getImageUrl().size() == 0)
			return 0;
		return Integer.MAX_VALUE; // 루프를 위하여 뷰페이지를 여러개 만듬.
	}

	@Override
	public int getRealCount() {
		return mHotelDetail.getImageUrl().size();
	}

	@Override
	public int getRealPos(int fakePos) {
		return fakePos % getRealCount();
	}
	
	@Override
	public int getRealCurPos() {
		return curPosReal;
	}

}
