package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StayListFragment extends PlaceListFragment
{
    private int mPageIndex;
    private int mStayCount;
    private StayCuration mStayCuration;

    protected StayListLayout mStayListLayout;
    protected StayListNetworkController mNetworkController;

    protected BaseActivity mBaseActivity;

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
        mStayCount = 0;

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

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        mStayCuration = (StayCuration) curation;
        mStayListLayout.setStayCuration(mStayCuration);
    }

    @Override
    public void clearList()
    {
        mStayCount = 0;
        mStayListLayout.clearList();
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
        switch (mViewType)
        {
            case LIST:
                int size = mStayListLayout.getItemCount();
                if (size == 0)
                {
                    refreshList(isShowProgress, 1);
                }
                break;

            case MAP:
                refreshList(isShowProgress, 0);
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

        SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
        Province province = mStayCuration.getProvince();

        if (province == null || checkInSaleTime == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

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
        return mStayListLayout.hasSalesPlace();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mStayListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    @Override
    public void setScrollListTop()
    {
        if (mStayListLayout == null)
        {
            return;
        }

        mStayListLayout.setScrollListTop();
    }

    @Override
    public int getEntryCount()
    {
        return mStayCount;
    }

    protected ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList, SortType sortType)
    {
        ArrayList<PlaceViewItem> stayViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return stayViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Stay stay : stayList)
        {
            // 지역순에만 section 존재함
            if (SortType.DEFAULT == sortType)
            {
                String region = stay.districtName;

                if (Util.isTextEmpty(region) == true)
                {
                    continue;
                }

                if (stay.isDailyChoice == true)
                {
                    if (hasDailyChoice == false)
                    {
                        hasDailyChoice = true;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, mBaseActivity.getResources().getString(R.string.label_dailychoice));
                        stayViewItemList.add(section);
                    }
                } else
                {
                    if (Util.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                    {
                        previousRegion = region;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                        stayViewItemList.add(section);
                    }
                }
            }

            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        if (Constants.PAGENATION_LIST_SIZE > stayList.size())
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
        }

        return stayViewItemList;
    }

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
                mStayListLayout.clearList();
            }

            mStayCount += list == null ? 0 : list.size();

            SortType sortType = mStayCuration.getCurationOption().getSortType();

            ArrayList<PlaceViewItem> placeViewItems = makeSectionStayList(list, sortType);

            switch (mViewType)
            {
                case LIST:
                {
                    mStayListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int size = mStayListLayout.getItemCount();
                    if (size == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }
                    break;
                }

                case MAP:
                {
                    mStayListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int mapSize = mStayListLayout.getMapItemSize();
                    if (mapSize == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }
                    break;
                }
            }

            unLockUI();
            mStayListLayout.setSwipeRefreshing(false);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayListFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            if (DEBUG == false && e != null)
            {
                Crashlytics.logException(e);
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.onRuntimeError("msgCode : " + msgCode + " , message : " + message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.onRuntimeError("message : " + message);
        }
    };

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    protected StayListLayout.OnEventListener mEventListener = new StayListLayout.OnEventListener()
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
