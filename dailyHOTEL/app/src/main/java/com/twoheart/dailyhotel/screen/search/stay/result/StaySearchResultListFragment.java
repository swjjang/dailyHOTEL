package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayListNetworkController;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StaySearchResultListFragment extends PlaceListFragment
{
    private int mPageIndex;
    private int mStayCount;

    protected StaySearchResultListLayout mStaySearchResultListLayout;
    protected StayListNetworkController mNetworkController;

    protected BaseActivity mBaseActivity;
    private StayCuration mStayCuration;

    public interface OnStayListFragmentListener extends OnPlaceListFragmentListener
    {
        void onStayClick(PlaceViewItem placeViewItem, SaleTime checkInSaleTime);

        void onResultListCount(int count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mStaySearchResultListLayout = new StaySearchResultListLayout(mBaseActivity, mEventListener);
        mStaySearchResultListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = new StayListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);

        mViewType = ViewType.LIST;

        mPageIndex = 1;

        return mStaySearchResultListLayout.onCreateView(R.layout.fragment_stay_search_result_list, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            PlaceListMapFragment placeListMapFragment = mStaySearchResultListLayout.getListMapFragment();

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
    public void setPlaceCuration(PlaceCuration curation)
    {
        mStayCuration = (StayCuration) curation;
        mStaySearchResultListLayout.setStayCuration(mStayCuration);
    }

    @Override
    public void clearList()
    {
        mStayCount = 0;
        mStaySearchResultListLayout.clearList();
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
        if (mViewType == null)
        {
            return;
        }

        switch (mViewType)
        {
            case LIST:
                int size = mStaySearchResultListLayout.getItemCount();
                if (size == 0)
                {
                    refreshList(isShowProgress, 1);
                } else
                {
                    ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(mStayCount);
                }

                break;

            case MAP:
                refreshList(isShowProgress, 0);
                ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(0);
                break;

            default:
                ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(0);
                break;
        }
    }

    public void addList(boolean isShowProgress)
    {
        refreshList(isShowProgress, mPageIndex + 1);
    }

    private void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 uilock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);
        }

        //        SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
        //
        //        Province province = mStayCuration.getProvince();
        //
        //        if (province == null || checkInSaleTime == null)
        //        {
        //            unLockUI();
        //            Util.restartApp(mBaseActivity);
        //            return;
        //        }

        int nights = mStayCuration.getNights();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        mPageIndex = page;

        if (mStayCuration == null || mStayCuration.getCurationOption() == null//
            || mStayCuration.getCurationOption().getSortType() == null//
            || (mStayCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStayCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        StayParams params = mStayCuration.toStayParams(page, PAGENATION_LIST_SIZE, true);
        mNetworkController.requestStayList(params);
    }

    public boolean hasSalesPlace()
    {
        return mStaySearchResultListLayout.hasSalesPlace();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mStaySearchResultListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    @Override
    public void setScrollListTop()
    {
        mStaySearchResultListLayout.setScrollListTop();
    }

    protected ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList, SortType sortType)
    {
        ArrayList<PlaceViewItem> stayViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return stayViewItemList;
        }

        for (Stay stay : stayList)
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        return stayViewItemList;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////           Ovrride method     end    /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private StayListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<Stay> list, int page)
        {
            if (isFinishing() == true)
            {
                unLockUI();
                return;
            }

            // 페이지가 전체데이터 이거나 첫페이지 이면 스크롤 탑
            if (page <= 1)
            {
                mStayCount = 0;
                mStaySearchResultListLayout.clearList();
            }

            mStayCount += list == null ? 0 : list.size();

            SortType sortType = mStayCuration.getCurationOption().getSortType();

            ArrayList<PlaceViewItem> placeViewItems = makeSectionStayList(list, sortType);

            switch (mViewType)
            {
                case LIST:
                {
                    mStaySearchResultListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int size = mStaySearchResultListLayout.getItemCount();
                    if (size == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }

                    ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(mStayCount);
                    break;
                }

                case MAP:
                {
                    mStaySearchResultListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int mapSize = mStaySearchResultListLayout.getMapItemSize();
                    if (mapSize == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }
                    ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(0);
                    break;
                }

                default:
                    ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(0);
                    break;
            }

            unLockUI();
            mStaySearchResultListLayout.setSwipeRefreshing(false);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StaySearchResultListFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            StaySearchResultListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StaySearchResultListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StaySearchResultListFragment.this.onErrorToastMessage(message);
        }
    };

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    protected StaySearchResultListLayout.OnEventListener mEventListener = new StaySearchResultListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
            ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onStayClick(placeViewItem, checkInSaleTime);
        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
        {
            // do nothing.
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

            mOnPlaceListFragmentListener.onShowMenuBar();
        }

        @Override
        public void onLoadMoreList()
        {
            addList(false);
        }

        @Override
        public void onFilterClick()
        {
            mOnPlaceListFragmentListener.onFilterClick();
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
