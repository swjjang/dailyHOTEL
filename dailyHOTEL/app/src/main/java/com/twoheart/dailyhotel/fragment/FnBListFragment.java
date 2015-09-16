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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.FnBListAdapter;
import com.twoheart.dailyhotel.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.FnB;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.FnBViewItem;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

public class FnBListFragment extends PlaceListFragment
{
	private PlaceListAdapter mPlaceListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = inflater.inflate(R.layout.fragment_fnb_list, container, false);

		mListView = (PinnedSectionListView) view.findViewById(R.id.listview_hotel_list);

		if (Util.isOverAPI12() == true)
		{
			mListView.addHeaderView(inflater.inflate(R.layout.list_header_empty_164, null, true));
			mListView.setOnScrollListener(mOnScrollListener);
		} else
		{
			mListView.setPadding(0, Util.dpToPx(baseActivity, 164), 0, 0);
		}

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		mEmptyView = view.findViewById(R.id.emptyView);

		mMapLayout = (FrameLayout) view.findViewById(R.id.hotelMapLayout);
		mMapLayout.setPadding(0, Util.dpToPx(baseActivity, 56), 0, 0);

		mViewType = VIEW_TYPE.LIST;

		setVisibility(mViewType);

		// 화면에 헤더바가 잠깐 나왔다가 사라지는 경우가 있어서
		if(mOnUserActionListener != null)
		{
			mOnUserActionListener.setHeaderSectionVisible(false);
		}

		ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this).useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

		mListView.setShadowVisible(false);

		ActionbarViewHolder actionbarViewHolder = new ActionbarViewHolder();
		actionbarViewHolder.mAnchorView = baseActivity.findViewById(R.id.anchorAnimation);
		actionbarViewHolder.mActionbarLayout = baseActivity.findViewById(R.id.actionBarLayout);
		actionbarViewHolder.mTabindicatorView = baseActivity.findViewById(R.id.headerSectionBar);

		setActionbarViewHolder(actionbarViewHolder);

		return view;
	}

	@Override
	protected void setVisibility(VIEW_TYPE type, boolean isCurrentPage)
	{
		switch (type)
		{
			case LIST:
				if (mActionbarViewHolder != null && mActionbarViewHolder.mTabindicatorView != null)
				{
					if (mActionbarViewHolder.mTabindicatorView.getVisibility() == View.INVISIBLE)
					{
						mActionbarViewHolder.mTabindicatorView.setVisibility(View.VISIBLE);
					}
				}
				break;

			case MAP:
				if (mActionbarViewHolder != null && mActionbarViewHolder.mTabindicatorView != null)
				{
					if (mActionbarViewHolder.mTabindicatorView.getVisibility() == View.VISIBLE)
					{
						mActionbarViewHolder.mTabindicatorView.setVisibility(View.INVISIBLE);
					}
				}
				break;

			case GONE:
				if (mActionbarViewHolder != null && mActionbarViewHolder.mTabindicatorView != null)
				{
					if (mActionbarViewHolder.mTabindicatorView.getVisibility() == View.INVISIBLE)
					{
						mActionbarViewHolder.mTabindicatorView.setVisibility(View.VISIBLE);
					}
				}
				break;
		}

		super.setVisibility(type, isCurrentPage);
	}

	@Override
	public void onStart()
	{
		AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.FNB_LIST);
		super.onStart();
	}

	@Override
	protected PlaceViewItem getPlaceViewItem(int position)
	{
		if (mPlaceListAdapter == null)
		{
			return null;
		}

		return mPlaceListAdapter.getItem(position);
	}

	@Override
	protected void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
	{
		if (checkInSaleTime == null)
		{
			return;
		}

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		lockUI();

		int stayDays = 0;

		if (checkOutSaleTime == null)
		{
			// 오늘, 내일인 경우
			stayDays = 1;
		} else
		{
			// 연박인 경우
			stayDays = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
		}

		if (stayDays <= 0)
		{
			unLockUI();
			return;
		}

		String params = null;

		if (province instanceof Area)
		{
		} else
		{
			params = String.format("?province_idx=%d&sday=%s", province.getProvinceIndex(), checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
		}

		if (DEBUG == true)
		{
			baseActivity.showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
		}

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_LB_SERVER).append(URL_WEBAPI_FNB_SALE_LIST).append(params).toString(), null, mFnBListJsonResponseListener, baseActivity));
	}

	@Override
	protected ArrayList<PlaceViewItem> getPlaceViewItemList()
	{
		if (mPlaceListAdapter == null)
		{
			return null;
		}

		return mPlaceListAdapter.getData();
	}

	@Override
	protected void setVisibility(VIEW_TYPE type)
	{
		super.setVisibility(type);

		if(mOnUserActionListener != null)
		{
			switch(type)
			{
				case LIST:
					mOnUserActionListener.setHeaderSectionVisible(true);
					break;

				default:
					mOnUserActionListener.setHeaderSectionVisible(false);
					break;
			}
		}
	}

	@Override
	protected PlaceMapFragment createPlaceMapFragment()
	{
		return new FnBMapFragment();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mFnBListJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		private ArrayList<PlaceViewItem> makeSectionHotelList(ArrayList<FnB> fnbList)
		{
			ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<PlaceViewItem>();

			if (fnbList == null || fnbList.size() == 0)
			{
				return placeViewItemList;
			}

			String area = null;
			boolean hasDailyChoice = false;

			for (FnB fnb : fnbList)
			{
				String region = fnb.districtName;

				if (TextUtils.isEmpty(region) == true)
				{
					continue;
				}

				if (fnb.isDailyChoice == true)
				{
					if (hasDailyChoice == false)
					{
						hasDailyChoice = true;

						FnBViewItem section = new FnBViewItem(getString(R.string.label_dailychoice));
						placeViewItemList.add(section);
					}
				} else
				{
					if (TextUtils.isEmpty(area) == true || region.equalsIgnoreCase(area) == false)
					{
						area = region;

						FnBViewItem section = new FnBViewItem(region);
						placeViewItemList.add(section);
					}
				}

				placeViewItemList.add(new FnBViewItem(fnb));
			}

			return placeViewItemList;
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
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					if (response.has("msg") == true)
					{
						String msg = response.getString("msg");
						DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
					}

					throw new NullPointerException("response == null");
				}

				JSONArray jsonArray = response.getJSONArray("data");

				int length = jsonArray.length();

				if (length == 0)
				{
					if (mPlaceListAdapter != null)
					{
						mPlaceListAdapter.clear();
					}

					setVisibility(VIEW_TYPE.GONE);
				} else
				{
					JSONObject jsonObject;

					ArrayList<FnB> fnbList = new ArrayList<FnB>(length);

					for (int i = 0; i < length; i++)
					{
						jsonObject = jsonArray.getJSONObject(i);

						FnB newFnB = new FnB();

						if (newFnB.setData(jsonObject) == true)
						{
							fnbList.add(newFnB); // 추가.
						}
					}

					ArrayList<PlaceViewItem> placeViewItemList = makeSectionHotelList(fnbList);

					if (mPlaceListAdapter == null)
					{
						mPlaceListAdapter = new FnBListAdapter(baseActivity, R.layout.list_row_hotel, new ArrayList<PlaceViewItem>());
						mListView.setAdapter(mPlaceListAdapter);
						mListView.setOnItemClickListener(FnBListFragment.this);
					}

					setVisibility(mViewType);

					if (mViewType == VIEW_TYPE.MAP)
					{
						setPlaceMapData(placeViewItemList);
					}

					mPlaceListAdapter.clear();
					mPlaceListAdapter.addAll(placeViewItemList);
					mPlaceListAdapter.notifyDataSetChanged();

					if (mIsSelectionTop == true)
					{
						mListView.setSelection(0);
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
}
