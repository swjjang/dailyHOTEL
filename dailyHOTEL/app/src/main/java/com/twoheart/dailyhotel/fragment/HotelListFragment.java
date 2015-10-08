/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * HotelListFragment (호텔 목록 화면)
 * <p/>
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.fragment.HotelMainFragment.HOTEL_VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DrawableLruCache;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

public class HotelListFragment extends BaseFragment implements Constants, OnItemClickListener, OnRefreshListener
{
    private static boolean mIsClosedActionBar = false;
    private static ValueAnimator mValueAnimator = null;
    private static boolean mLockActionBar = false;
    private static int mAnchorY = Integer.MAX_VALUE;
    protected PinnedSectionListView mHotelListView;
    protected HotelListAdapter mHotelListAdapter;
    protected SaleTime mSaleTime;
    protected boolean mIsSelectionTop;
    protected Province mSelectedProvince;
    protected HotelMainFragment.OnUserActionListener mUserActionListener;
    private PullToRefreshLayout mPullToRefreshLayout;
    private View mEmptyView;
    private FrameLayout mMapLayout;
    private HotelListMapFragment mHotelListMapFragment;
    private HOTEL_VIEW_TYPE mHotelViewType;
    private float mOldY;
    private int mOldfirstVisibleItem;
    private int mDirection;
    private ActionbarViewHolder mActionbarViewHolder;

    private static class ActionbarViewHolder
    {
        public View mAnchorView;
        public View mActionbarLayout;
        public View mTabindicatorView;
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

        if (Util.isOverAPI12() == true)
        {
            mHotelListView.addHeaderView(inflater.inflate(R.layout.list_header_empty, null, true));
            mHotelListView.setOnScrollListener(mOnScrollListener);
        } else
        {
            mHotelListView.setPadding(0, Util.dpToPx(baseActivity, 119), 0, 0);
        }

        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        mEmptyView = view.findViewById(R.id.emptyView);

        mMapLayout = (FrameLayout) view.findViewById(R.id.hotelMapLayout);
        mMapLayout.setPadding(0, Util.dpToPx(baseActivity, 119) + 2, 0, 0);

        mHotelViewType = HOTEL_VIEW_TYPE.LIST;

        setVisibility(HOTEL_VIEW_TYPE.LIST);

        // Now find the PullToRefreshLayout and set it up
        ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this)
                // Here we'll set a custom ViewDelegate
                .useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

        mHotelListView.setShadowVisible(false);

        mActionbarViewHolder = new ActionbarViewHolder();

        mActionbarViewHolder.mAnchorView = baseActivity.findViewById(R.id.anchorAnimation);
        mActionbarViewHolder.mActionbarLayout = baseActivity.findViewById(R.id.actionBarLayout);
        mActionbarViewHolder.mTabindicatorView = baseActivity.findViewById(R.id.tabindicator);

        return view;
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.HOTEL_LIST);
        super.onStart();
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

        if (position < 0)
        {
            refreshHotelList(mSelectedProvince, true);
            return;
        }

        if (mUserActionListener != null)
        {
            HotelListViewItem hotelListViewItem = mHotelListAdapter.getItem(position);

            if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
            {
                return;
            }

            mUserActionListener.selectHotel(hotelListViewItem, mSaleTime);

            try
            {
                ImageView imageView = (ImageView) childView.findViewById(R.id.iv_hotel_row_img);
                DrawableLruCache.getInstance().put(hotelListViewItem.getItem().getImage(), imageView.getDrawable());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
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
                            if (HotelListFragment.this instanceof HotelDaysListFragment)
                            {
                                mHotelListMapFragment.setHotelList(mHotelListAdapter.getData(), ((HotelDaysListFragment) HotelListFragment.this).getSelectedCheckInSaleTime(), false);
                            } else
                            {
                                mHotelListMapFragment.setHotelList(mHotelListAdapter.getData(), mSaleTime, false);
                            }
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

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
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

        fetchHotelList(province, mSaleTime, null);
    }

    /**
     * 호텔리스트를 보여준다.
     */
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
            Area area = (Area) province;

            params = String.format("?province_idx=%d&area_idx=%d&checkin_date=%s&length_stay=%d", area.getProvinceIndex(), area.index, checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), stayDays);
        } else
        {
            params = String.format("?province_idx=%d&checkin_date=%s&length_stay=%d", province.getProvinceIndex(), checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), stayDays);
        }

        //		if (DEBUG == true)
        //		{
        //			baseActivity.showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
        //		}

        // 호텔 리스트를 가져온다.
        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_LB_SERVER).append(URL_WEBAPI_SALE_HOTEL_LIST).append(params).toString(), null, mHotelJsonResponseListener, baseActivity));
    }

    public Province getProvince()
    {
        return mSelectedProvince;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ScrollListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRefreshStarted(View view)
    {
        refreshHotelList(mSelectedProvince, true);
    }

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

    /**
     * Hotel List Listener
     */
    private DailyHotelJsonResponseListener mHotelJsonResponseListener = new DailyHotelJsonResponseListener()
    {
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

                if (Util.isTextEmpty(region) == true)
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
                    if (Util.isTextEmpty(area) == true || region.equalsIgnoreCase(area) == false)
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

                JSONArray hotelJSONArray = response.getJSONArray("data");

                int length = hotelJSONArray.length();

                if (length == 0)
                {
                    if (mHotelListAdapter != null)
                    {
                        mHotelListAdapter.clear();
                    }

                    setVisibility(HOTEL_VIEW_TYPE.GONE);

                    if (mUserActionListener != null)
                    {
                        mUserActionListener.setMapViewVisible(false);
                    }
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

                        if (HotelListFragment.this instanceof HotelDaysListFragment)
                        {
                            mHotelListMapFragment.setHotelList(hotelListViewItemList, ((HotelDaysListFragment) HotelListFragment.this).getSelectedCheckInSaleTime(), mIsSelectionTop);
                        } else
                        {
                            mHotelListMapFragment.setHotelList(hotelListViewItemList, mSaleTime, mIsSelectionTop);
                        }
                    }

                    mHotelListAdapter.clear();
                    mHotelListAdapter.addAll(hotelListViewItemList);
                    mHotelListAdapter.notifyDataSetChanged();

                    if (mIsSelectionTop == true)
                    {
                        mHotelListView.setSelection(0);
                    }

                    if (mUserActionListener != null)
                    {
                        mUserActionListener.setMapViewVisible(true);
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
