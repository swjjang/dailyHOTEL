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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class PlaceListFragment extends BaseFragment implements Constants, OnItemClickListener, OnRefreshListener
{
    private static boolean mIsClosedActionBar = false;
    private static ValueAnimator mValueAnimator = null;
    private static boolean mLockActionBar = false;
    private static int mAnchorY = Integer.MAX_VALUE;
    protected PinnedSectionListView mListView;
    protected PullToRefreshLayout mPullToRefreshLayout;
    protected FrameLayout mMapLayout;
    protected View mEmptyView;
    protected boolean mIsSelectionTop;
    protected VIEW_TYPE mViewType;
    protected PlaceMainFragment.OnUserActionListener mOnUserActionListener;
    protected ActionbarViewHolder mActionbarViewHolder;
    private SaleTime mSaleTime;
    private Province mSelectedProvince;
    private PlaceMapFragment mPlaceMapFragment;
    private float mOldY;
    private int mOldfirstVisibleItem;
    private int mDirection;

    protected class ActionbarViewHolder
    {
        public View mAnchorView;
        public View mActionbarLayout;
        public View mTabindicatorView;
        public View mUnderlineView02;
    }

    protected abstract void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime);

    protected abstract PlaceViewItem getPlaceViewItem(int position);

    protected abstract ArrayList<PlaceViewItem> getPlaceViewItemList();

    protected abstract PlaceMapFragment createPlaceMapFragment();

    @Override
    public void onResume()
    {
        showActionBar();
        setActionBarAnimationLock(false);

        super.onResume();
    }

    @Override
    public void onDestroyView()
    {
        showActionBar();
        setActionBarAnimationLock(true);

        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mPlaceMapFragment != null)
        {
            mPlaceMapFragment.onActivityResult(requestCode, resultCode, data);
        }
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
            return;
        }

        if (mOnUserActionListener != null)
        {
            PlaceViewItem placeViewItem = getPlaceViewItem(position);
            mOnUserActionListener.selectPlace(placeViewItem, mSaleTime);
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

        showActionBarAnimatoin();
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

                    if (mPlaceMapFragment != null)
                    {
                        mPlaceMapFragment.setUserActionListener(mOnUserActionListener);

                        if (isCurrentPage == true)
                        {
                            ArrayList<PlaceViewItem> arrayList = getPlaceViewItemList();

                            if (arrayList != null)
                            {
                                mPlaceMapFragment.setPlaceViewItemList(arrayList, mSaleTime, false);
                            }
                        }
                    }
                    break;

                case GONE:
                    break;
            }
        }
    }

    protected void setVisibility(VIEW_TYPE type, boolean isCurrentPage)
    {
        switch (type)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                // 맵과 리스트에서 당일상품 탭 안보이도록 수정

                if (mPlaceMapFragment != null)
                {
                    getChildFragmentManager().beginTransaction().remove(mPlaceMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mPlaceMapFragment = null;
                }

                mPullToRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                // 맵과 리스트에서 당일상품 탭 안보이도록 수정

                if (isCurrentPage == true)
                {
                    if (mPlaceMapFragment == null)
                    {
                        mPlaceMapFragment = createPlaceMapFragment();
                        getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mPlaceMapFragment).commitAllowingStateLoss();
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

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    protected void setPlaceMapData(ArrayList<PlaceViewItem> placeViewItemList)
    {
        if (mViewType == VIEW_TYPE.MAP && mPlaceMapFragment != null)
        {
            mPlaceMapFragment.setUserActionListener(mOnUserActionListener);
            mPlaceMapFragment.setPlaceViewItemList(placeViewItemList, mSaleTime, mIsSelectionTop);
        }
    }

    public void setUserActionListener(PlaceMainFragment.OnUserActionListener userActionLister)
    {
        mOnUserActionListener = userActionLister;
    }

    public void setActionbarViewHolder(ActionbarViewHolder actionbarViewHolder)
    {
        mActionbarViewHolder = actionbarViewHolder;
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

    public void setActionBarAnimationLock(boolean isLock)
    {
        mLockActionBar = isLock;

        mDirection = MotionEvent.ACTION_CANCEL;
    }

    private void showActionBar()
    {
        if (Util.isOverAPI12() == false || mActionbarViewHolder == null)
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

        mAnchorY = 0;

        mActionbarViewHolder.mAnchorView.setVisibility(View.VISIBLE);
        mActionbarViewHolder.mAnchorView.setTranslationY(0);
        mActionbarViewHolder.mActionbarLayout.setTranslationY(0);
        mActionbarViewHolder.mTabindicatorView.setTranslationY(0);

        if (mActionbarViewHolder.mUnderlineView02 != null)
        {
            mActionbarViewHolder.mUnderlineView02.setTranslationY(0);
        }

        mActionbarViewHolder.mAnchorView.setVisibility(View.INVISIBLE);
    }

    protected void showActionBarAnimatoin()
    {
        if (Util.isOverAPI12() == false || mIsClosedActionBar == false || mLockActionBar == true || mActionbarViewHolder == null)
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
            int moveHeight = mActionbarViewHolder.mActionbarLayout.getHeight() + mActionbarViewHolder.mTabindicatorView.getHeight() + 4;
            mAnchorY = -moveHeight;
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

    private void hideActionbarAnimation()
    {
        if (Util.isOverAPI12() == false || mIsClosedActionBar == true || mLockActionBar == true || mActionbarViewHolder == null)
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

        int moveHeight = mActionbarViewHolder.mActionbarLayout.getHeight() + mActionbarViewHolder.mTabindicatorView.getHeight() + 4;

        mValueAnimator = ValueAnimator.ofInt(mAnchorY, -moveHeight);
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ScrollListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                    showActionBarAnimatoin();
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    // 전체 내용을 위로 올린다.
                    if (firstVisibleItem >= 1)
                    {
                        hideActionbarAnimation();
                    }
                    break;
                }
            }
        }
    };
}
