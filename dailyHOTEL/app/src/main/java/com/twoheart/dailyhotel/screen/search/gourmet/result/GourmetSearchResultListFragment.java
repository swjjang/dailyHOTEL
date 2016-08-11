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
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GourmetSearchResultListFragment extends PlaceListFragment
{
    private int mLoadMorePageIndex;
    private GourmetCuration mGourmetCuration;
    private int mResultTotalCount;
    private int mResultMaxCount;

    protected BaseActivity mBaseActivity;

    protected GourmetSearchResultListLayout mGourmetSearchResultListLayout;
    protected GourmetSearchResultListNetworkController mNetworkController;

    protected int mGourmetCount;

    public interface OnGourmetSearchResultListFragmentListener extends OnPlaceListFragmentListener
    {
        void onGourmetClick(PlaceViewItem placeViewItem);

        void onGourmetCategoryFilter(int page, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mGourmetSearchResultListLayout = new GourmetSearchResultListLayout(mBaseActivity, mEventListener);
        mGourmetSearchResultListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = new GourmetSearchResultListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);

        mViewType = ViewType.LIST;

        mLoadMorePageIndex = 1;

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
        refreshList(isShowProgress, mLoadMorePageIndex + 1);
    }

    private void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 uilock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);

            if (isShowProgress == true)
            {
                // 새로 검색이 될경우에는 결과개수를 보여주는 부분은 안보이게 한다.
                mGourmetSearchResultListLayout.updateResultCount(mViewType, -1, -1);
            }
        }

        SaleTime saleTime = mGourmetCuration.getSaleTime();

        if (mGourmetCuration == null || mGourmetCuration.getCurationOption() == null//
            || mGourmetCuration.getCurationOption().getSortType() == null//
            || (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetCuration.getLocation() == null) //
            || (((GourmetSearchCuration) mGourmetCuration).getRadius() != 0d && mGourmetCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        GourmetSearchParams params = (GourmetSearchParams) mGourmetCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
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
        ArrayList<PlaceViewItem> gourmetViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return gourmetViewItemList;
        }

        for (Gourmet gourmet : gourmetList)
        {
            gourmetViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
        }

        return gourmetViewItemList;
    }

    @Override
    public int getPlaceCount()
    {
        return mGourmetCount;
    }

    public int getResultTotalCount()
    {
        return mResultTotalCount;
    }

    public int getResultMaxCount()
    {
        return mResultMaxCount;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchResultListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetSearchResultListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount)
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

            int listSize = list == null ? 0 : list.size();
            if (listSize > 0)
            {
                mLoadMorePageIndex = page;
            }

            mGourmetCount += listSize;

            SortType sortType = mGourmetCuration.getCurationOption().getSortType();

            setFilterInformation(page, list);

            ArrayList<PlaceViewItem> placeViewItems = makeSectionGourmetList(list, sortType);

            switch (mViewType)
            {
                case LIST:
                {
                    mGourmetSearchResultListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                    int size = mGourmetSearchResultListLayout.getItemCount();

                    if (size == 0)
                    {
                        setVisibility(ViewType.GONE, true);
                    }
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

            if (page <= 1)
            {
                mResultTotalCount = totalCount;
                mResultMaxCount = maxCount;
                mGourmetSearchResultListLayout.updateResultCount(mViewType, totalCount, maxCount);
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

        private void setFilterInformation(int page, ArrayList<Gourmet> gourmetList)
        {
            HashMap<String, Integer> categoryCodeMap = new HashMap<>(12);
            HashMap<String, Integer> categorySequenceMap = new HashMap<>(12);

            // 필터 정보 넣기
            for (Gourmet gourmet : gourmetList)
            {
                categoryCodeMap.put(gourmet.category, gourmet.categoryCode);
                categorySequenceMap.put(gourmet.category, gourmet.categorySequence);
            }

            ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onGourmetCategoryFilter(page, categoryCodeMap, categorySequenceMap);
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
}
