package com.twoheart.dailyhotel.screen.search.gourmet.result;

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
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GourmetSearchResultListFragment extends PlaceListFragment
{
    private int mPageIndex;
    private GourmetCuration mGourmetCuration;

    protected BaseActivity mBaseActivity;

    protected GourmetSearchResultListLayout mGourmetSearchResultListLayout;
    protected GourmetSearchResultListNetworkController mNetworkController;

    protected int mGourmetCount;

    public interface OnGourmetSearchResultListFragmentListener extends OnPlaceListFragmentListener
    {
        void onGourmetClick(PlaceViewItem placeViewItem);

        void onResultListCount(int count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mGourmetSearchResultListLayout = new GourmetSearchResultListLayout(mBaseActivity, mEventListener);
        mGourmetSearchResultListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = new GourmetSearchResultListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);

        mViewType = ViewType.LIST;

        mPageIndex = 1;

        return mGourmetSearchResultListLayout.onCreateView(R.layout.fragment_gourmet_search_result_list, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            PlaceListMapFragment placeListMapFragment = mGourmetSearchResultListLayout.getListMapFragment();

            if (placeListMapFragment != null)
            {
                placeListMapFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        mGourmetCuration = (GourmetCuration) curation;
        mGourmetSearchResultListLayout.setGourmetCuration(mGourmetCuration);
    }

    @Override
    public void clearList()
    {
        mGourmetSearchResultListLayout.clearList();
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
        switch (mViewType)
        {
            case LIST:
                int size = mGourmetSearchResultListLayout.getItemCount();
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

        SaleTime saleTime = mGourmetCuration.getSaleTime();
        Province province = mGourmetCuration.getProvince();

        if (province == null || saleTime == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        mPageIndex = page;

        if (mGourmetCuration == null || mGourmetCuration.getCurationOption() == null//
            || mGourmetCuration.getCurationOption().getSortType() == null//
            || (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        GourmetParams params = (GourmetParams) mGourmetCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        mNetworkController.requestGourmetList(params);
    }

    public boolean hasSalesPlace()
    {
        return mGourmetSearchResultListLayout.hasSalesPlace();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mGourmetSearchResultListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    @Override
    public void setScrollListTop()
    {
        if (mGourmetSearchResultListLayout == null)
        {
            return;
        }

        mGourmetSearchResultListLayout.setScrollListTop();
    }

    protected ArrayList<PlaceViewItem> makeSectionGourmetList(List<Gourmet> gourmetList, SortType sortType)
    {
        ArrayList<PlaceViewItem> stayViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return stayViewItemList;
        }

        for (Gourmet gourmet : gourmetList)
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
        }

        if (Constants.PAGENATION_LIST_SIZE > gourmetList.size())
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
        }

        return stayViewItemList;
    }

    @Override
    public int getPlaceCount()
    {
        return mGourmetCount;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchResultListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetSearchResultListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetList(ArrayList<Gourmet> list, int page)
        {
            if (isFinishing() == true)
            {
                unLockUI();
                return;
            }

            // 페이지가 전체데이터 이거나 첫페이지 이면 스크롤 탑
            if (page <= 1)
            {
                mGourmetCount = 0;
                mGourmetSearchResultListLayout.clearList();
            }

            mGourmetCount += list == null ? 0 : list.size();
            SortType sortType = mGourmetCuration.getCurationOption().getSortType();

            ArrayList<PlaceViewItem> placeViewItems = makeSectionGourmetList(list, sortType);

            switch (mViewType)
            {
                case LIST:
                {
                    mGourmetSearchResultListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int size = mGourmetSearchResultListLayout.getItemCount();

                    if (size == 0 && mGourmetCuration.getCurationOption().isDefaultFilter() == false)
                    {
                        setVisibility(ViewType.GONE, true);
                    }

                    ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(0);
                    break;
                }

                case MAP:
                {
                    mGourmetSearchResultListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int mapSize = mGourmetSearchResultListLayout.getMapItemSize();
                    if (mapSize == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }
                    break;
                }
            }

            unLockUI();
            mGourmetSearchResultListLayout.setSwipeRefreshing(false);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetSearchResultListFragment.this.onErrorResponse(volleyError);
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

    protected GourmetSearchResultListLayout.OnEventListener mEventListener = new GourmetSearchResultListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onGourmetClick(placeViewItem);
        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
        {
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
            ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onShowActivityEmptyView(isShow);
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
