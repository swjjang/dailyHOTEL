/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelTabBookingFragment (호텔 예약 탭)
 * 
 * 호텔 탭 중 예약 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class HotelMainFragment extends BaseFragment
{
	private static final String TAG_TAB_1 = "today";
	private static final String TAG_TAB_2 = "tomorrow";
	private static final String TAG_TAB_3 = "select";

	private static final String ARGUMENT_NAME = "name";

	private FragmentTabHost mTabHost;
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;
	
	TabIndicator mTabIndicator;

	protected List<BaseFragment> mFragments = new LinkedList<BaseFragment>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);
		
		mTabIndicator = (TabIndicator)findViewById(R.id.tabindicator);
		mTabIndicator.setData(tabList, true);
		mTabIndicator.setSelectedItemUnderLineColor(getResources().getColor(R.color.my_giftbox_tab_selected_line));
		mTabIndicator.setTextColor(getResources().getColorStateList(R.color.selector_my_gift_tab_indicator_text));
		mTabIndicator.setTextTypeface(Typeface.BOLD);
		mTabIndicator.setSubTextColor(getResources().getColorStateList(R.color.selector_my_gift_tab_indicator_sub_text));
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
		
		mMainViewPager = (FragmentViewPager)findViewById(R.id.my_gift_list_viewpager);
		mMainViewPager.setOnPageSelectedListener(mOnPageSelectedListener);
		
		
		
		
		

//		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
//
//		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_container);
//
//		View view1 = getTabView("오늘", "1일(수)");
//		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_1).setIndicator(view1), TabRoot.class, null);
//
//		View view2 = getTabView("내일", "2일(목)");
//		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_2).setIndicator(view2), TabRoot.class, null);
//		
//		View view3 = getTabView("선택", "");
//		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_3).setIndicator(view3), TabRoot.class, null);

		mHostActivity.setActionBar(R.string.actionbar_title_hotel_list_frag);

		return view;
	}
	
	private initLayout()
	{
		ArrayList<String> tabList = new ArrayList<String>();
		tabList.add(getString(R.string.action_giftbox_receivedgiftbox));
		tabList.add(getString(R.string.action_giftbox_sentgiftbox));
		
		mTabIndicator = (TabIndicator)findViewById(R.id.tabindicator);
		mTabIndicator.setData(tabList, true);
		mTabIndicator.setSelectedItemUnderLineColor(getResources().getColor(R.color.my_giftbox_tab_selected_line));
		mTabIndicator.setTextColor(getResources().getColorStateList(R.color.selector_my_gift_tab_indicator_text));
		mTabIndicator.setTextTypeface(Typeface.BOLD);
		mTabIndicator.setSubTextColor(getResources().getColorStateList(R.color.selector_my_gift_tab_indicator_sub_text));
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
		
		mMainViewPager = (FragmentViewPager)findViewById(R.id.my_gift_list_viewpager);
		mMainViewPager.setOnPageSelectedListener(mOnPageSelectedListener);
	}
	
	private View getTabView(String title, String day)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View view = layoutInflater.inflate(R.layout.hotel_main_tab_view, null, true);
		
		TextView titleTextView = (TextView)view.findViewById(R.id.titleTextView);
		TextView dayTextView = (TextView)view.findViewById(R.id.dayTextView);
		
		titleTextView.setText(title);
		dayTextView.setText(day);
		
		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		mTabHost = null;
	}

	public static class TabRoot extends Fragment implements OnClickListener
	{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			if (container == null)
			{
				return null;
			}
			
			return inflater.inflate(R.layout.tab_root, container, false);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState == null)
			{
				getChildFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container, createNewChild()).commit();
			}
		}

		@Override
		public void onClick(View v)
		{
			getChildFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, createNewChild()).commit();
		}
		
		private void clearFragmentBackStack()
		{
			FragmentManager fragmentManager = getChildFragmentManager();
			
			for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i)
			{
				fragmentManager.popBackStackImmediate();

			}

		}
//
//		public boolean onBackPressed()
//		{
//			FragmentManager fm = getChildFragmentManager();
//			if (fm.getBackStackEntryCount() == 1)
//			{
//				return false;
//			} else
//			{
//				fm.popBackStack();
//				return true;
//			}
//		}

		private Fragment createNewChild()
		{
			clearFragmentBackStack();
			
			FragmentManager fragmentManager = getChildFragmentManager();
			
			Bundle args = getArguments();
			if (args == null)
			{
				args = new Bundle();
				args.putString(ARGUMENT_NAME, "Name unknown");
			} else
			{
				args = new Bundle(args);
			}
			
			Fragment f = new TabChild();
			f.setArguments(args);
			return f;
		}
	}

	public static class TabChild extends Fragment
	{
		private static final String ARGUMENT_CHILD_COUNT = "child_count";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			if (container == null)
			{
				return null;
			}

			View v = inflater.inflate(R.layout.tab_child, container, false);
			Bundle args = getArguments();
			if (args != null && args.containsKey(ARGUMENT_NAME) && args.containsKey(ARGUMENT_CHILD_COUNT))
			{
				String text = args.getString(ARGUMENT_NAME) + "__" + args.getInt(ARGUMENT_CHILD_COUNT);
				Button button = (Button) v.findViewById(R.id.button);
				button.setText(text);

				Fragment f = getParentFragment();
				if (f instanceof OnClickListener)
				{
					button.setOnClickListener((OnClickListener) f);
				}
			}

			return v;
		}

	}

	protected FragmentPagerAdapter mAdapter;

}
