package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StayListFragment extends PlaceListFragment
{
    private static final int PAGE_SIZE = 20;

    private int mPageIndex;

    protected SaleTime mCheckInSaleTime;

    private ViewType mViewType;

    private StayListLayout mStayListLayout;
    private BaseActivity mBaseActivity;
    private StayListNetworkController mNetworkController;

    public interface OnStayListFragmentListener extends OnPlaceListFragmentListener
    {
        void onStayClick(PlaceViewItem placeViewItem, SaleTime checkInSaleTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mStayListLayout = new StayListLayout(mBaseActivity, mEventListener);
        mStayListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = new StayListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);

        mViewType = ViewType.LIST;

        mPageIndex = 1;

        return mStayListLayout.onCreateView(R.layout.fragment_hotel_list, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            PlaceListMapFragment placeListMapFragment = mStayListLayout.getListMapFragment();

            if (placeListMapFragment != null)
            {
                placeListMapFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////           Ovrride method    start   /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void refreshList(boolean isShowProgress)
    {
        ArrayList<PlaceViewItem> list = new ArrayList<>(mStayListLayout.getList());
        if (list == null || list.size() == 0)
        {
            refreshList(isShowProgress, 1);
            ExLog.d("request");
        } else
        {
            ExLog.d("refresh");
        }
    }

    public void addList(boolean isShowProgress)
    {
        refreshList(isShowProgress, mPageIndex + 1);
    }

    private void refreshList(boolean isShowProgress, int page)
    {
        lockUI(isShowProgress);

        SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

        Province province = StayCurationManager.getInstance().getProvince();

        if (province == null || checkInSaleTime == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        int nights = StayCurationManager.getInstance().getNight();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        mPageIndex = page;

        mNetworkController.requestStayList(StayCurationManager.getInstance().getStayParams(page, PAGE_SIZE, true));
    }

    public boolean hasSalesPlace()
    {
        return mStayListLayout.hasSalesPlace();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mStayListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////           Ovrride method     end    /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setScrollListTop(boolean scrollListTop)
    {
        mStayListLayout.setScrollListTop(scrollListTop);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private StayListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<PlaceViewItem> list, int page)
        {
            if (isFinishing() == true)
            {
                unLockUI();
                return;
            }

            // 페이지가 전체데이터 이거나 첫페이지 이면 스크롤 탑
            if (page <= 1)
            {
                setScrollListTop(true);
                mStayListLayout.clearList();
            }

            StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

            mStayListLayout.addResultList(getChildFragmentManager(), mViewType, list, stayCurationOption.getSortType());

            List<PlaceViewItem> allList = mStayListLayout.getList();
            if (allList == null || allList.size() == 0)
            {
                setVisibility(ViewType.GONE, true);
            }

            unLockUI();
            mStayListLayout.setSwipeRefreshing(false);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onError(Exception e)
        {

        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {

        }

        @Override
        public void onErrorToastMessage(String message)
        {

        }
    };

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    private StayListLayout.OnEventListener mEventListener = new StayListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();
            ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onStayClick(placeViewItem, checkInSaleTime);
        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
        {
            mOnPlaceListFragmentListener.onEventBannerClick(eventBanner);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            mOnPlaceListFragmentListener.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            mOnPlaceListFragmentListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            refreshList(isShowProgress, 1);
        }

        @Override
        public void onLoadMoreList()
        {
            addList(false);
        }

        @Override
        public void finish()
        {
            if (mBaseActivity != null)
            {
                mBaseActivity.finish();
            }
        }
    };
}
