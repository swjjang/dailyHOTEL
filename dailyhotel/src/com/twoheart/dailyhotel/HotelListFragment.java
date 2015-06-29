/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelListFragment (호텔 목록 화면)
 * 
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.fragment.HotelListMapFragment;
import com.twoheart.dailyhotel.fragment.HotelMainFragment;
import com.twoheart.dailyhotel.fragment.HotelMainFragment.HOTEL_VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.DailyFloatingActionButton;
import com.twoheart.dailyhotel.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

public class HotelListFragment extends BaseFragment implements Constants, OnItemClickListener, OnRefreshListener
{
	private PinnedSectionListView mHotelListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private HotelListAdapter mHotelListAdapter;

	protected SaleTime mSaleTime;

	//	private boolean event;
	protected boolean mIsSelectionTop;
	private View mEmptyView;
	//	private View mFooterView; // FooterView

	private FrameLayout mMapLayout;
	private HotelListMapFragment mHotelListMapFragment;
	private HOTEL_VIEW_TYPE mHotelViewType;
	protected Province mSelectedProvince;

	//	private DailyFloatingActionButton mDailyFloatingActionButton;

	private HotelListViewItem mSelectedHotelListViewItem;
	private int mSelectedHotelIndex;

	protected HotelMainFragment.OnUserActionListener mUserActionListener;

	private float mOldY;
	private int mOldfirstVisibleItem;
	private int mDirection;
	private static boolean mIsClosedActionBar = false;
	private static ValueAnimator mValueAnimator = null;
	private static boolean mLockActionBar = false;
	private static int mAnchorY = Integer.MAX_VALUE;
	private int mScrollState;
	private ActionbarViewHolder mActionbarViewHolder;

	private class ActionbarViewHolder
	{
		public View mAnchorView;
		public View mActionbarView;
		public View mTabindicatorView;
		public View mUnderlineView01;
		public View mUnderlineView02;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);

		mHotelListView = (PinnedSectionListView) view.findViewById(R.id.listview_hotel_list);

		if (isCanActionBarAnimation() == true)
		{
			mHotelListView.addHeaderView(inflater.inflate(R.layout.list_header_empty, null, true));
			mHotelListView.setOnScrollListener(mOnScrollListener);
		} else
		{
			mHotelListView.setPadding(0, Util.dpToPx(baseActivity, 110), 0, 0);
		}

		// 이벤트를 마지막에 넣는다.
		// FooterView
		//		mFooterView = inflater.inflate(R.layout.list_row_hotel_event, null, true);
		//		mHotelListView.addFooterView(mFooterView);

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		mEmptyView = view.findViewById(R.id.emptyView);

		mMapLayout = (FrameLayout) view.findViewById(R.id.hotelMapLayout);
		mMapLayout.setPadding(0, Util.dpToPx(baseActivity, 109) + 2, 0, 0);

		//		mHotelListMapFragment = (HotelListMapFragment) getChildFragmentManager().findFragmentById(R.id.hotelMapFragment);

		//		mDailyFloatingActionButton = (DailyFloatingActionButton) view.findViewById(R.id.floatingActionButton);
		//		mDailyFloatingActionButton.setOnClickListener(new View.OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View v)
		//			{
		//			}
		//		});

		//		mDailyFloatingActionButton.setVisibility(View.GONE);

		mHotelViewType = HOTEL_VIEW_TYPE.LIST;

		setVisibility(HOTEL_VIEW_TYPE.LIST);

		// Now find the PullToRefreshLayout and set it up
		ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this)
		// Here we'll set a custom ViewDelegate
		.useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

		mHotelListView.setShadowVisible(false);

		mActionbarViewHolder = new ActionbarViewHolder();

		mActionbarViewHolder.mAnchorView = baseActivity.findViewById(R.id.anchorAnimation);
		mActionbarViewHolder.mActionbarView = baseActivity.findViewById(R.id.toolbar_actionbar);
		mActionbarViewHolder.mTabindicatorView = baseActivity.findViewById(R.id.tabindicator);
		mActionbarViewHolder.mUnderlineView01 = baseActivity.findViewById(R.id.toolbar_actionbar_underline);
		mActionbarViewHolder.mUnderlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

		return view;
	}

	@Override
	public void onResume()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		showActionBar(baseActivity);
		setActionBarAnimationLock(false);

		super.onResume();
	}
	
	@Override
	public void onDestroyView()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		showActionBar(baseActivity);
		setActionBarAnimationLock(true);
		
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		position -= mHotelListView.getHeaderViewsCount();

		mSelectedHotelListViewItem = mHotelListAdapter.getItem(position);

		int count = 0;
		for (int i = 0; i < position; i++)
		{
			if (mHotelListAdapter.getItem(i).getType() == HotelListViewItem.TYPE_SECTION)
			{
				count++;
			}
		}

		mSelectedHotelIndex = position - count;

		Map<String, String> params = new HashMap<String, String>();
		params.put("timeZone", "Asia/Seoul");

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mHotelListMapFragment != null)
		{
			mHotelListMapFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 토글이 아닌 경우에만 진행하는 프로세스.
	 * 
	 * @param detailRegion
	 */
	public void processSelectedDetailRegion(String detailRegion)
	{
		// 현재 맵화면을 보고 있으면 맵화면을 유지 시켜중어야 한다.
		if (detailRegion != null && mHotelViewType == HOTEL_VIEW_TYPE.MAP)
		{
			refreshHotelList(mSelectedProvince, true);
		}
	}

	public void onPageSelected(boolean isRequestHotelList)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		showActionBarAnimatoin(baseActivity);
		setActionBarAnimationLock(true);

		mDirection = MotionEvent.ACTION_CANCEL;
	}

	public void onPageUnSelected()
	{
		mDirection = MotionEvent.ACTION_CANCEL;
	}

	public void onRefreshComplete()
	{
		//		mDailyFloatingActionButton.attachToListView(mHotelListView);
	}

	/**
	 * 새로 고침을 하지 않고 기존의 있는 데이터를 보여준다.
	 * 
	 * @param type
	 * @param isCurrentPage
	 */
	public void setHotelViewType(HOTEL_VIEW_TYPE type, boolean isCurrentPage)
	{
		mHotelViewType = type;

		if (mEmptyView.getVisibility() == View.VISIBLE)
		{
			setVisibility(HOTEL_VIEW_TYPE.GONE);
		} else
		{
			switch (mHotelViewType)
			{
				case LIST:
					setVisibility(HOTEL_VIEW_TYPE.LIST, isCurrentPage);
					break;

				case MAP:
					setVisibility(HOTEL_VIEW_TYPE.MAP, isCurrentPage);

					if (mHotelListMapFragment != null)
					{
						mHotelListMapFragment.setUserActionListener(mUserActionListener);

						if (isCurrentPage == true && mHotelListAdapter != null)
						{
							mHotelListMapFragment.setHotelList(mHotelListAdapter.getData(), mSaleTime, false);
						}
					}
					break;

				case GONE:
					break;
			}
		}
	}

	private void setVisibility(HOTEL_VIEW_TYPE type, boolean isCurrentPage)
	{
		switch (type)
		{
			case LIST:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.GONE);

				if (mHotelListMapFragment != null)
				{
					getChildFragmentManager().beginTransaction().remove(mHotelListMapFragment).commitAllowingStateLoss();
					mMapLayout.removeAllViews();
					mHotelListMapFragment = null;
				}

				//				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
				//				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_map_mini);

				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				break;

			case MAP:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.VISIBLE);

				if (isCurrentPage == true)
				{
					if (mHotelListMapFragment == null)
					{
						mHotelListMapFragment = new HotelListMapFragment();
						getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mHotelListMapFragment).commitAllowingStateLoss();
					}
				}

				//				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
				//				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_list_mini);
				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;

			case GONE:
				mEmptyView.setVisibility(View.VISIBLE);
				mMapLayout.setVisibility(View.GONE);

				//				mDailyFloatingActionButton.setVisibility(View.GONE);
				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;
		}
	}

	private void setVisibility(HOTEL_VIEW_TYPE type)
	{
		setVisibility(type, true);
	}

	protected boolean isCanActionBarAnimation()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	protected boolean isUsedAnimatorApi()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public void setSaleTime(SaleTime saleTime)
	{
		mSaleTime = saleTime;
	}

	public SaleTime getSaleTime()
	{
		return mSaleTime;
	}

	public void setUserActionListener(HotelMainFragment.OnUserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void setFloatingActionButtonVisible(boolean visible)
	{
		//		if (mDailyFloatingActionButton == null)
		//		{
		//			return;
		//		}
		//
		//		// 일단 눈에 안보이도록 함.
		//		mDailyFloatingActionButton.hide(false, true);
		//
		//		if (visible == true)
		//		{
		//			if (mHotelListAdapter != null && mHotelListAdapter.getCount() != 0)
		//			{
		//				mDailyFloatingActionButton.show(false, true);
		//			}
		//		} else
		//		{
		//			mDailyFloatingActionButton.hide(false, true);
		//		}
	}

	public void refreshHotelList(Province province, boolean isSelectionTop)
	{
		mSelectedProvince = province;
		mIsSelectionTop = isSelectionTop;

		fetchHotelList(province, mSaleTime);
	}

	/**
	 * 호텔리스트를 보여준다.
	 * 
	 * @param position
	 */
	private void fetchHotelList(Province province, SaleTime saleTime)
	{
		if (saleTime == null)
		{
			ExLog.e("saleTime == null");
			return;
		}

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		lockUI();

		String params = null;

		if (province instanceof Area)
		{
			Area area = (Area) province;

			params = String.format("?province_idx=%d&area_idx=%d&date=%s", area.getProvinceIndex(), area.index, saleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
		} else
		{
			params = String.format("?province_idx=%d&date=%s", province.getProvinceIndex(), saleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
		}

		// 호텔 리스트를 가져온다. 
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SALE_HOTEL_LIST).append(params).toString(), null, mHotelJsonResponseListener, baseActivity));

		RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordScreen("hotelList", "/todays-hotels/" + province.name);
	}

	public Province getProvince()
	{
		return mSelectedProvince;
	}

	@Override
	public void onRefreshStarted(View view)
	{
		refreshHotelList(mSelectedProvince, true);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ScrollListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setActionBarAnimationLock(boolean lock)
	{
		mLockActionBar = lock;

		mDirection = MotionEvent.ACTION_CANCEL;
	}

	private void showActionBar(BaseActivity baseActivity)
	{
		if (isCanActionBarAnimation() == false)
		{
			return;
		}

		mIsClosedActionBar = false;

		if (mValueAnimator != null)
		{
			mValueAnimator.cancel();
			mValueAnimator.removeAllListeners();
			mValueAnimator = null;
		}

		mActionbarViewHolder.mAnchorView.setVisibility(View.VISIBLE);

		mAnchorY = 0;

		mActionbarViewHolder.mAnchorView.setTranslationY(0);
		mActionbarViewHolder.mActionbarView.setTranslationY(0);
		mActionbarViewHolder.mUnderlineView01.setTranslationY(0);
		mActionbarViewHolder.mTabindicatorView.setTranslationY(0);
		mActionbarViewHolder.mUnderlineView02.setTranslationY(0);

		mActionbarViewHolder.mAnchorView.setVisibility(View.INVISIBLE);
	}

	public void showActionBarAnimatoin(BaseActivity baseActivity)
	{
		if (isCanActionBarAnimation() == false || mIsClosedActionBar == false || mLockActionBar == true)
		{
			return;
		}

		mIsClosedActionBar = false;

		mActionbarViewHolder.mAnchorView.setVisibility(View.VISIBLE);

		if (mValueAnimator != null)
		{
			mValueAnimator.cancel();
			mValueAnimator.removeAllListeners();
			mValueAnimator = null;
		}

		if (mAnchorY == Integer.MAX_VALUE)
		{
			mAnchorY = -mActionbarViewHolder.mAnchorView.getHeight();
		}

		mValueAnimator = ValueAnimator.ofInt(mAnchorY, 0);
		mValueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int value = (Integer) animation.getAnimatedValue();

				mAnchorY = value;

				mActionbarViewHolder.mAnchorView.setTranslationY(value);
				mActionbarViewHolder.mActionbarView.setTranslationY(value);
				mActionbarViewHolder.mUnderlineView01.setTranslationY(value);
				mActionbarViewHolder.mTabindicatorView.setTranslationY(value);
				mActionbarViewHolder.mUnderlineView02.setTranslationY(value);
			}
		});

		mValueAnimator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				mActionbarViewHolder.mAnchorView.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{
			}
		});

		mValueAnimator.start();

	}

	private void hideActionbarAnimation(BaseActivity baseActivity)
	{
		if (isCanActionBarAnimation() == false || mIsClosedActionBar == true || mLockActionBar == true)
		{
			return;
		}

		mIsClosedActionBar = true;

		mActionbarViewHolder.mAnchorView.setVisibility(View.VISIBLE);

		if (mValueAnimator != null)
		{
			mValueAnimator.cancel();
			mValueAnimator.removeAllListeners();
			mValueAnimator = null;
		}

		if (mAnchorY == Integer.MAX_VALUE)
		{
			mAnchorY = 0;
		}

		mValueAnimator = ValueAnimator.ofInt(mAnchorY, -mActionbarViewHolder.mAnchorView.getHeight());
		mValueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int value = (Integer) animation.getAnimatedValue();

				mAnchorY = value;

				mActionbarViewHolder.mAnchorView.setTranslationY(value);
				mActionbarViewHolder.mActionbarView.setTranslationY(value);
				mActionbarViewHolder.mUnderlineView01.setTranslationY(value);
				mActionbarViewHolder.mTabindicatorView.setTranslationY(value);
				mActionbarViewHolder.mUnderlineView02.setTranslationY(value);
			}
		});

		mValueAnimator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{
			}
		});

		mValueAnimator.start();
	}

	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener()
	{
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			mScrollState = scrollState;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			ExLog.d("isLockUiComponent() : " + isLockUiComponent() + ", baseActivity.isLockUiComponent() : " + baseActivity.isLockUiComponent());

			if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
			{
				return;
			}

			View firstView = view.getChildAt(0);

			if (null == firstView)
			{
				return;
			}

			int[] lastViewRect = new int[2];
			float y = Float.MAX_VALUE;

			View lastView = view.getChildAt(view.getChildCount() - 1);

			if (null != lastView)
			{
				lastView.getLocationOnScreen(lastViewRect);
				y = lastViewRect[1];
			}

			if (Float.compare(mOldY, Float.MAX_VALUE) == 0)
			{
				mOldY = y;
				mOldfirstVisibleItem = firstVisibleItem;
			} else
			{
				// MotionEvent.ACTION_CANCEL을 사용하는 이유는 가끔씩 내리거나 올리면 갑자기 좌표가 튀는 경우가
				// 있는데 해당 튀는 경우를 무시하기 위해서
				if (mOldfirstVisibleItem > firstVisibleItem)
				{
					mDirection = MotionEvent.ACTION_DOWN;
				} else if (mOldfirstVisibleItem < firstVisibleItem)
				{
					mDirection = MotionEvent.ACTION_UP;
				} else
				{
					//					if (mScrollState != OnScrollListener.SCROLL_STATE_FLING)
					//					{
					//						if (mOldY > y)
					//						{
					//							if (mDirection == MotionEvent.ACTION_DOWN)
					//							{
					//								mDirection = MotionEvent.ACTION_CANCEL;
					//							} else
					//							{
					//								mDirection = MotionEvent.ACTION_UP;
					//							}
					//						} else if (mOldY < y)
					//						{
					//							if (mDirection == MotionEvent.ACTION_UP)
					//							{
					//								mDirection = MotionEvent.ACTION_CANCEL;
					//							} else
					//							{
					//								mDirection = MotionEvent.ACTION_DOWN;
					//							}
					//						}
					//					}
				}

				mOldY = y;
				mOldfirstVisibleItem = firstVisibleItem;
			}

			ExLog.d("mDirection : " + mDirection);

			switch (mDirection)
			{
				case MotionEvent.ACTION_DOWN:
				{
					showActionBarAnimatoin(baseActivity);
					break;
				}

				case MotionEvent.ACTION_UP:
				{
					// 전체 내용을 위로 올린다.
					if (firstVisibleItem >= 1)
					{
						hideActionbarAnimation(baseActivity);
					}
					break;
				}
			}
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Hotel List Listener
	 */
	private DailyHotelJsonResponseListener mHotelJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		/**
		 * 
		 * @param region
		 *            : in
		 * @param hotelList
		 *            : in & out
		 * @param hotelListViewList
		 *            : out
		 */
		private ArrayList<HotelListViewItem> makeSectionHotelList(ArrayList<Hotel> hotelList)
		{
			ArrayList<HotelListViewItem> hotelListViewItemList = new ArrayList<HotelListViewItem>();

			if (hotelList == null || hotelList.size() == 0)
			{
				return hotelListViewItemList;
			}

			String area = null;
			boolean hasDailyChoice = false;

			for (Hotel hotel : hotelList)
			{
				String region = hotel.getDetailRegion();

				if (TextUtils.isEmpty(region) == true)
				{
					continue;
				}

				if (hotel.isDailyChoice == true)
				{
					if (hasDailyChoice == false)
					{
						hasDailyChoice = true;

						HotelListViewItem section = new HotelListViewItem(getString(R.string.label_dailychoice));
						hotelListViewItemList.add(section);
					}
				} else
				{
					if (TextUtils.isEmpty(area) == true || region.equalsIgnoreCase(area) == false)
					{
						area = region;

						HotelListViewItem section = new HotelListViewItem(region);
						hotelListViewItemList.add(section);
					}
				}

				hotelListViewItemList.add(new HotelListViewItem(hotel));
			}

			return hotelListViewItemList;
		}

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (getActivity() == null)
				{
					return;
				}

				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					throw new NullPointerException("response == null");
				}

				JSONArray hotelJSONArray = response.getJSONArray("data");

				int length = hotelJSONArray.length();

				if (length == 0)
				{
					if (mHotelListAdapter != null)
					{
						mHotelListAdapter.clear();
					}

					setVisibility(HOTEL_VIEW_TYPE.GONE);
				} else
				{
					JSONObject jsonObject;

					ArrayList<Hotel> hotelList = new ArrayList<Hotel>(length);

					for (int i = 0; i < length; i++)
					{
						jsonObject = hotelJSONArray.getJSONObject(i);

						Hotel newHotel = new Hotel();

						if (newHotel.setHotel(jsonObject) == true)
						{
							hotelList.add(newHotel); // 추가.
						}
					}

					// section 및 HotelListViewItem 으로 바꾸어 주기.
					ArrayList<HotelListViewItem> hotelListViewItemList = makeSectionHotelList(hotelList);

					if (mHotelListAdapter == null)
					{
						mHotelListAdapter = new HotelListAdapter(baseActivity, R.layout.list_row_hotel, new ArrayList<HotelListViewItem>());
						mHotelListView.setAdapter(mHotelListAdapter);
						mHotelListView.setOnItemClickListener(HotelListFragment.this);
					}

					setVisibility(mHotelViewType);

					// 지역이 변경되면 다시 리스트를 받아오는데 어떻게 해야할지 의문.
					if (mHotelViewType == HOTEL_VIEW_TYPE.MAP)
					{
						mHotelListMapFragment.setUserActionListener(mUserActionListener);
						mHotelListMapFragment.setHotelList(hotelListViewItemList, mSaleTime, mIsSelectionTop);
					}

					mHotelListAdapter.clear();
					mHotelListAdapter.addAll(hotelListViewItemList);
					mHotelListAdapter.notifyDataSetChanged();

					if (mIsSelectionTop == true)
					{
						mHotelListView.setSelection(0);
						// mDailyFloatingActionButton
						//						mDailyFloatingActionButton.detachToListView(mHotelListView);
					}
				}

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();

				// 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
				onRefreshComplete();

				setActionBarAnimationLock(false);
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				mSaleTime.setCurrentTime(response.getLong("currentDateTime"));
				mSaleTime.setOpenTime(response.getLong("openDateTime"));
				mSaleTime.setCloseTime(response.getLong("closeDateTime"));
				mSaleTime.setDailyTime(response.getLong("dailyDateTime"));

				if (mSaleTime.isSaleTime() == false)
				{
					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mSaleTime));
					unLockUI();
				} else
				{
					if (mUserActionListener != null)
					{
						mUserActionListener.selectHotel(mSelectedHotelListViewItem, mSelectedHotelIndex, mSaleTime);
					}
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};
}
