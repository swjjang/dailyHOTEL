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
package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.TicketMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class TicketListFragment extends
		BaseFragment implements Constants, OnItemClickListener, OnRefreshListener
{
	protected PinnedSectionListView mListView;
	protected PullToRefreshLayout mPullToRefreshLayout;

	protected SaleTime mSaleTime;
	protected boolean mIsSelectionTop;
	protected View mEmptyView;

	protected FrameLayout mMapLayout;
	protected VIEW_TYPE mViewType;
	private Province mSelectedProvince;

	protected TicketViewItem mSelectedTicketViewItem;
	protected TicketListMapFragment mTicketListMapFragment;
	protected TicketMainFragment.OnUserActionListener mUserActionListener;

	private float mOldY;
	private int mOldfirstVisibleItem;
	private int mDirection;
	private static boolean mIsClosedActionBar = false;
	private static ValueAnimator mValueAnimator = null;
	private static boolean mLockActionBar = false;
	private static int mAnchorY = Integer.MAX_VALUE;
	protected ActionbarViewHolder mActionbarViewHolder;

	protected class ActionbarViewHolder
	{
		public View mAnchorView;
		public View mActionbarLayout;
		public View mTabindicatorView;
		public View mUnderlineView02;
	}

	protected abstract void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime);

	protected abstract TicketViewItem getTicketViewItem(int position);

	protected abstract ArrayList<TicketViewItem> getTicketListData();

	protected abstract TicketListMapFragment getTicketListMapFragment();

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

		position -= mListView.getHeaderViewsCount();

		if (position < 0)
		{
			refreshList(mSelectedProvince, true);
			return;
		}

		mSelectedTicketViewItem = getTicketViewItem(position);

		Map<String, String> params = new HashMap<String, String>();
		params.put("timeZone", "Asia/Seoul");

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mTicketListMapFragment != null)
		{
			mTicketListMapFragment.onActivityResult(requestCode, resultCode, data);
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
		if (detailRegion != null && mViewType == VIEW_TYPE.MAP)
		{
			refreshList(mSelectedProvince, true);
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
	}

	/**
	 * 새로 고침을 하지 않고 기존의 있는 데이터를 보여준다.
	 * 
	 * @param type
	 * @param isCurrentPage
	 */
	public void setViewType(VIEW_TYPE type, boolean isCurrentPage)
	{
		mViewType = type;

		if (mEmptyView.getVisibility() == View.VISIBLE)
		{
			setVisibility(VIEW_TYPE.GONE);
		} else
		{
			switch (type)
			{
				case LIST:
					setVisibility(VIEW_TYPE.LIST, isCurrentPage);
					break;

				case MAP:
					setVisibility(VIEW_TYPE.MAP, isCurrentPage);

					if (mTicketListMapFragment != null)
					{
						mTicketListMapFragment.setUserActionListener(mUserActionListener);

						if (isCurrentPage == true)
						{
							ArrayList<TicketViewItem> arrayList = getTicketListData();

							if (arrayList != null)
							{
								mTicketListMapFragment.setTicketList(arrayList, mSaleTime, false);
							}
						}
					}
					break;

				case GONE:
					break;
			}
		}
	}

	private void setVisibility(VIEW_TYPE type, boolean isCurrentPage)
	{
		switch (type)
		{
			case LIST:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.GONE);

				if (mTicketListMapFragment != null)
				{
					getChildFragmentManager().beginTransaction().remove(mTicketListMapFragment).commitAllowingStateLoss();
					mMapLayout.removeAllViews();
					mTicketListMapFragment = null;
				}

				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				break;

			case MAP:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.VISIBLE);

				if (isCurrentPage == true)
				{
					if (mTicketListMapFragment == null)
					{
						mTicketListMapFragment = getTicketListMapFragment();
						getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mTicketListMapFragment).commitAllowingStateLoss();
					}
				}

				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;

			case GONE:
				mEmptyView.setVisibility(View.VISIBLE);
				mMapLayout.setVisibility(View.GONE);

				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;
		}
	}

	protected void setVisibility(VIEW_TYPE type)
	{
		setVisibility(type, true);
	}

	public void setSaleTime(SaleTime saleTime)
	{
		mSaleTime = saleTime;
	}

	public SaleTime getSaleTime()
	{
		return mSaleTime;
	}

	public void setUserActionListener(TicketMainFragment.OnUserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void refreshList(Province province, boolean isSelectionTop)
	{
		mSelectedProvince = province;
		mIsSelectionTop = isSelectionTop;

		fetchHotelList(province, mSaleTime, null);
	}

	public Province getProvince()
	{
		return mSelectedProvince;
	}

	@Override
	public void onRefreshStarted(View view)
	{
		refreshList(mSelectedProvince, true);
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
		if (Util.isOverAPI12() == false)
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
		mActionbarViewHolder.mActionbarLayout.setTranslationY(0);
		mActionbarViewHolder.mTabindicatorView.setTranslationY(0);

		if (mActionbarViewHolder.mUnderlineView02 != null)
		{
			mActionbarViewHolder.mUnderlineView02.setTranslationY(0);
		}

		mActionbarViewHolder.mAnchorView.setVisibility(View.INVISIBLE);
	}

	public void showActionBarAnimatoin(BaseActivity baseActivity)
	{
		if (Util.isOverAPI12() == false || mIsClosedActionBar == false || mLockActionBar == true)
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
				mActionbarViewHolder.mActionbarLayout.setTranslationY(value);
				mActionbarViewHolder.mTabindicatorView.setTranslationY(value);

				if (mActionbarViewHolder.mUnderlineView02 != null)
				{
					mActionbarViewHolder.mUnderlineView02.setTranslationY(value);
				}
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
		if (Util.isOverAPI12() == false || mIsClosedActionBar == true || mLockActionBar == true)
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
				mActionbarViewHolder.mActionbarLayout.setTranslationY(value);
				mActionbarViewHolder.mTabindicatorView.setTranslationY(value);

				if (mActionbarViewHolder.mUnderlineView02 != null)
				{
					mActionbarViewHolder.mUnderlineView02.setTranslationY(value);
				}
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

	protected AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener()
	{
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

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
				}

				mOldY = y;
				mOldfirstVisibleItem = firstVisibleItem;
			}

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

				if (mSaleTime.isSaleTime() == true)
				{
					if (mUserActionListener != null)
					{
						mUserActionListener.selectedTicket(mSelectedTicketViewItem, mSaleTime);
					}
				} else
				{
					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mSaleTime, TicketMainFragment.TICKET_TYPE.FNB));
					unLockUI();
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};
}
