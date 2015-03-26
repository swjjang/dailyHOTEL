package com.twoheart.dailyhotel.util;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public abstract class TabActivity extends BaseActivity
{

	public HotelDetail hotelDetail;

	protected List<BaseFragment> mFragments;

	protected FragmentPagerAdapter mAdapter;
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mFragments = new LinkedList<BaseFragment>();
	}

	protected abstract void onPostSetCookie();

	@Override
	protected void onResume()
	{
		super.onResume();
		onPostSetCookie();

		if (mAdapter == null)
		{
			mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
			{

				@Override
				public Fragment getItem(int position)
				{
					return mFragments.get(position);
				}

				@Override
				public CharSequence getPageTitle(int position)
				{
					return mFragments.get(position).getTitle();
				}

				@Override
				public int getCount()
				{
					return mFragments.size();
				}
			};

			mViewPager.setOffscreenPageLimit(mAdapter.getCount() + 2);
			mViewPager.setAdapter(mAdapter);
		} else
		{
			mAdapter.notifyDataSetChanged();
		}
		mIndicator.setViewPager(mViewPager);
	}

	protected void loadFragments()
	{
		String[] titles = { getString(R.string.frag_tab_info_title), getString(R.string.frag_tab_map_title) };
		mFragments.add(TabInfoFragment.newInstance(hotelDetail, titles[0]));
		mFragments.add(TabMapFragment.newInstance(hotelDetail, titles[1]));

		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();

		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
}
