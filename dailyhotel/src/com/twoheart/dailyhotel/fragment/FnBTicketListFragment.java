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

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.FnBTicketListAdapter;
import com.twoheart.dailyhotel.adapter.TicketListAdapter;
import com.twoheart.dailyhotel.fragment.TicketMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.FnBTicketDto;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.FnBTicketViewItem;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;
import com.twoheart.dailyhotel.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

public class FnBTicketListFragment extends TicketListFragment
{
	private TicketListAdapter mTicketListAdapter;

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
			mListView.addHeaderView(inflater.inflate(R.layout.list_header_empty_109, null, true));
			mListView.setOnScrollListener(mOnScrollListener);
		} else
		{
			mListView.setPadding(0, Util.dpToPx(baseActivity, 109), 0, 0);
		}

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		mEmptyView = view.findViewById(R.id.emptyView);

		ImageView imageView = (ImageView) mEmptyView.findViewById(R.id.backgroundImageView);
		TextView messageTextView01 = (TextView) mEmptyView.findViewById(R.id.messageTextView01);
		TextView messageTextView02 = (TextView) mEmptyView.findViewById(R.id.messageTextView02);

		imageView.setImageResource(R.drawable.open_stanby_bg_fnb);
		messageTextView01.setTextColor(getResources().getColor(R.color.white));
		messageTextView01.setText(R.string.label_fnblistfragment_empty_text01);
		messageTextView02.setTextColor(getResources().getColor(R.color.white));
		messageTextView02.setText(R.string.label_fnblistfragment_empty_text02);

		mMapLayout = (FrameLayout) view.findViewById(R.id.hotelMapLayout);
		mMapLayout.setPadding(0, Util.dpToPx(baseActivity, 109) + 2, 0, 0);

		mViewType = VIEW_TYPE.LIST;

		setVisibility(mViewType);

		ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this).useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

		mListView.setShadowVisible(false);

		mActionbarViewHolder = new ActionbarViewHolder();
		mActionbarViewHolder.mAnchorView = baseActivity.findViewById(R.id.anchorAnimation);
		mActionbarViewHolder.mActionbarLayout = baseActivity.findViewById(R.id.actionBarLayout);
		mActionbarViewHolder.mTabindicatorView = baseActivity.findViewById(R.id.headerSectionBar);

		return view;
	}

	@Override
	public void onStart()
	{
		AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.FNB_LIST);
		super.onStart();
	}

	@Override
	protected TicketViewItem getTicketViewItem(int position)
	{
		if (mTicketListAdapter == null)
		{
			return null;
		}

		return mTicketListAdapter.getItem(position);
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
	protected ArrayList<TicketViewItem> getTicketListData()
	{
		if (mTicketListAdapter == null)
		{
			return null;
		}

		return mTicketListAdapter.getData();
	}

	@Override
	protected TicketListMapFragment getTicketListMapFragment()
	{
		if (mTicketListMapFragment == null)
		{
			mTicketListMapFragment = new FnBTicketListMapFragment();
		}

		return mTicketListMapFragment;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mFnBListJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		private ArrayList<TicketViewItem> makeSectionHotelList(ArrayList<FnBTicketDto> fnbList)
		{
			ArrayList<TicketViewItem> ticketViewItemList = new ArrayList<TicketViewItem>();

			if (fnbList == null || fnbList.size() == 0)
			{
				return ticketViewItemList;
			}

			String area = null;
			boolean hasDailyChoice = false;

			for (FnBTicketDto fnbTicketDto : fnbList)
			{
				String region = fnbTicketDto.districtName;

				if (TextUtils.isEmpty(region) == true)
				{
					continue;
				}

				if (fnbTicketDto.isDailyChoice == true)
				{
					if (hasDailyChoice == false)
					{
						hasDailyChoice = true;

						FnBTicketViewItem section = new FnBTicketViewItem(getString(R.string.label_dailychoice));
						ticketViewItemList.add(section);
					}
				} else
				{
					if (TextUtils.isEmpty(area) == true || region.equalsIgnoreCase(area) == false)
					{
						area = region;

						FnBTicketViewItem section = new FnBTicketViewItem(region);
						ticketViewItemList.add(section);
					}
				}

				ticketViewItemList.add(new FnBTicketViewItem(fnbTicketDto));
			}

			return ticketViewItemList;
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
					if (response.has("msg") == true)
					{
						String msg = response.getString("msg");
						DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
					}

					throw new NullPointerException("response == null");
				}

				JSONArray ticketJSONArray = response.getJSONArray("data");

				int length = ticketJSONArray.length();

				if (length == 0)
				{
					if (mTicketListAdapter != null)
					{
						mTicketListAdapter.clear();
					}

					setVisibility(VIEW_TYPE.GONE);
				} else
				{
					JSONObject jsonObject;

					ArrayList<FnBTicketDto> fnbTicketList = new ArrayList<FnBTicketDto>(length);

					for (int i = 0; i < length; i++)
					{
						jsonObject = ticketJSONArray.getJSONObject(i);

						FnBTicketDto newFnBTicketDto = new FnBTicketDto();

						if (newFnBTicketDto.setTicket(jsonObject) == true)
						{
							fnbTicketList.add(newFnBTicketDto); // 추가.
						}
					}

					ArrayList<TicketViewItem> ticketViewItemList = makeSectionHotelList(fnbTicketList);

					if (mTicketListAdapter == null)
					{
						mTicketListAdapter = new FnBTicketListAdapter(baseActivity, R.layout.list_row_hotel, new ArrayList<TicketViewItem>());
						mListView.setAdapter(mTicketListAdapter);
						mListView.setOnItemClickListener(FnBTicketListFragment.this);
					}

					setVisibility(mViewType);

					// 지역이 변경되면 다시 리스트를 받아오는데 어떻게 해야할지 의문.
					if (mViewType == VIEW_TYPE.MAP)
					{
						mTicketListMapFragment.setUserActionListener(mUserActionListener);
						mTicketListMapFragment.setTicketList(ticketViewItemList, mSaleTime, mIsSelectionTop);
					}

					mTicketListAdapter.clear();
					mTicketListAdapter.addAll(ticketViewItemList);
					mTicketListAdapter.notifyDataSetChanged();

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
