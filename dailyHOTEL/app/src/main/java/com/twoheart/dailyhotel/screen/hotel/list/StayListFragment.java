package com.twoheart.dailyhotel.screen.hotel.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class StayListFragment extends PlaceListFragment
{
    protected StayCuration mStayCuration;
    private StayListLayout mStayListLayout;

    public interface OnStayListFragmentListener extends OnPlaceListFragmentListener
    {
        void onStayClick(View view, PlaceViewItem placeViewItem, int listCount);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new StayListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected PlaceListLayout getPlaceListLayout()
    {
        if (mStayListLayout == null)
        {
            mStayListLayout = new StayListLayout(mBaseActivity, mEventListener);
        }
        return mStayListLayout;
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_hotel_list;
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        mStayCuration = (StayCuration) curation;
        ((StayListLayout) mPlaceListLayout).setStayCuration(mStayCuration);
    }

    @Override
    protected void refreshList(boolean isShowProgress, int page)
    {
        if (mStayCuration == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        // 더보기 시 unlock 걸지않음
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

        if (mStayCuration == null || mStayCuration.getCurationOption() == null//
            || mStayCuration.getCurationOption().getSortType() == null//
            || (mStayCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStayCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        StayParams params = (StayParams) mStayCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        ((StayListNetworkController) mNetworkController).requestStayList(params);
    }

    protected void onStayList(ArrayList<Stay> list, int page, boolean hasSection)
    {
        if (isFinishing() == true)
        {
            unLockUI();
            return;
        }

        // 페이지가 전체데이터 이거나 첫페이지 이면 스크롤 탑
        if (page <= 1)
        {
            mPlaceCount = 0;
            mPlaceListLayout.clearList();
        }

        int listSize = list == null ? 0 : list.size();
        if (listSize > 0)
        {
            mLoadMorePageIndex = page;
            mIsLoadMoreFlag = true;
        } else
        {
            mIsLoadMoreFlag = false;
        }

        mPlaceCount += listSize;

        SortType sortType = mStayCuration.getCurationOption().getSortType();

        ArrayList<PlaceViewItem> placeViewItems = makePlaceList(list, sortType, hasSection);

        switch (mViewType)
        {
            case LIST:
            {
                mPlaceListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                int size = mPlaceListLayout.getItemCount();
                if (size == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                Category category = mStayCuration.getCategory();
                if (Category.ALL.code.equalsIgnoreCase(category.code))
                {
                    mEventListener.onShowActivityEmptyView(size == 0);
                }
                break;
            }

            case MAP:
            {
                mPlaceListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                int mapSize = mPlaceListLayout.getMapItemSize();
                if (mapSize == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                Category category = mStayCuration.getCategory();
                if (Category.ALL.code.equalsIgnoreCase(category.code))
                {
                    mEventListener.onShowActivityEmptyView(mapSize == 0);
                }
                break;
            }

            default:
                break;
        }

        unLockUI();
        mPlaceListLayout.setSwipeRefreshing(false);
    }

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    protected StayListLayout.OnEventListener mEventListener = new StayListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(View view, PlaceViewItem placeViewItem)
        {
            ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onStayClick(view, placeViewItem, getPlaceCount());
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
        public void onShowActivityEmptyView(boolean isShow)
        {
            mOnPlaceListFragmentListener.onShowActivityEmptyView(isShow);
        }

        @Override
        public void onRecordAnalytics(ViewType viewType)
        {
            mOnPlaceListFragmentListener.onRecordAnalytics(viewType);
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

    private StayListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<Stay> list, int page)
        {
            String value = mStayCuration.getCheckInSaleTime().getDayOfDaysDateFormat("yyyyMMdd") + "," + mStayCuration.getNights();
            DailyPreference.getInstance(mBaseActivity).setStayLastViewDate(value);

            StayListFragment.this.onStayList(list, page, true);
        }

        @Override
        public void onError(Throwable e)
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

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayListFragment.this.onErrorResponse(call, response);
        }
    };
}
