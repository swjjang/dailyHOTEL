package com.twoheart.dailyhotel.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
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
    protected PinnedSectionListView mListView;
    protected PullToRefreshLayout mPullToRefreshLayout;
    protected FrameLayout mMapLayout;
    protected View mEmptyView;
    protected boolean mIsSelectionTop;
    protected VIEW_TYPE mViewType;
    protected PlaceMainFragment.OnUserActionListener mOnUserActionListener;
    private Province mSelectedProvince;
    protected PlaceMapFragment mPlaceMapFragment;
    private float mOldY;
    private int mOldfirstVisibleItem;
    private int mDirection;

    public enum SortType
    {
        DEFAULT,
        DISTANCE,
        LOW_PRICE,
        HIGH_PRICE;
    }

    public abstract void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime);

    public abstract PlaceViewItem getPlaceViewItem(int position);

    public abstract ArrayList<PlaceViewItem> getPlaceViewItemList();

    public abstract PlaceMapFragment createPlaceMapFragment();

    public abstract boolean hasSalesPlace();

    public abstract void setViewType(VIEW_TYPE type, boolean isCurrentPage);

    /**
     * 토글이 아닌 경우에만 진행하는 프로세스.
     *
     * @param detailRegion
     */
    //    public void processSelectedDetailRegion(String detailRegion)
    //    {
    //        // 현재 맵화면을 보고 있으면 맵화면을 유지 시켜중어야 한다.
    //        if (detailRegion != null && mViewType == VIEW_TYPE.MAP)
    //        {
    //            refreshList(mSelectedProvince, true);
    //        }
    //    }

    public void onPageSelected(boolean isRequestHotelList)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mDirection = MotionEvent.ACTION_CANCEL;
    }

    public void onPageUnSelected()
    {
        mDirection = MotionEvent.ACTION_CANCEL;
    }

    public void onRefreshComplete()
    {
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
                if (isCurrentPage == true && mPlaceMapFragment == null)
                {
                    mPlaceMapFragment = createPlaceMapFragment();
                    getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mPlaceMapFragment).commitAllowingStateLoss();
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

    public void setUserActionListener(PlaceMainFragment.OnUserActionListener userActionLister)
    {
        mOnUserActionListener = userActionLister;
    }

    public Province getProvince()
    {
        return mSelectedProvince;
    }

    protected void setProvince(Province province)
    {
        mSelectedProvince = province;
    }

    @Override
    public void onRefreshStarted(View view)
    {
        if (mOnUserActionListener != null)
        {
            mOnUserActionListener.refreshAll();
        }
    }
}
