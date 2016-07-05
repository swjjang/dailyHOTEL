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
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StayListFragment extends PlaceListFragment
{
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
    public void clearList()
    {
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
        lockUI(isShowProgress);

        SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

        Province province = StayCurationManager.getInstance().getProvince();

        if (province == null || checkInSaleTime == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        int nights = StayCurationManager.getInstance().getNights();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        mPageIndex = page;

        mNetworkController.requestStayList(StayCurationManager.getInstance().getStayParams(page, PAGENATION_LIST_SIZE, true));
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

    @Override
    public void setScrollListTop()
    {
        mStayListLayout.setScrollListTop();
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
                mStayListLayout.clearList();
            }

            StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

            ArrayList<PlaceViewItem> placeViewItems = makeSectionStayList(list);


            if (ViewType.MAP == mViewType)
            {
                mStayListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, stayCurationOption.getSortType(), true);

                int mapSize = mStayListLayout.getMapItemSize();
                if (mapSize == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }
            } else
            {
                mStayListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, stayCurationOption.getSortType());

                int size = mStayListLayout.getItemCount();
                if (size == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }
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

        private ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList)
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

                stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
            }

            boolean isLast;
            if (Constants.PAGENATION_LIST_SIZE > stayList.size())
            {
                isLast = true;
            } else
            {
                isLast = false;
            }

            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, isLast));

            return stayViewItemList;
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

            mOnPlaceListFragmentListener.onShowMenuBar();
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
