package com.twoheart.dailyhotel.screen.gourmet.list;

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
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GourmetListFragment extends PlaceListFragment
{
    protected int mGourmetCount;

    protected GourmetCuration mGourmetCuration;

    protected GourmetListLayout mGourmetListLayout;

    public interface OnGourmetListFragmentListener extends OnPlaceListFragmentListener
    {
        void onGourmetClick(PlaceViewItem placeViewItem, int listCount);

        void onGourmetCategoryFilter(int page, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mViewType = ViewType.LIST;
        mLoadMorePageIndex = 1;

        mGourmetListLayout = getGourmetListLayout();
        mGourmetListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = getNetworkController();

        return mGourmetListLayout.onCreateView(getLayoutResourceId(), container);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new GourmetListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_gourmet_list;
    }

    protected GourmetListLayout getGourmetListLayout()
    {
        return new GourmetListLayout(mBaseActivity, mEventListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            PlaceListMapFragment placeListMapFragment = mGourmetListLayout.getListMapFragment();

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
        mGourmetListLayout.setGourmetCuration(mGourmetCuration);
    }

    @Override
    public void clearList()
    {
        mGourmetCount = 0;
        mGourmetListLayout.clearList();
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
                int size = mGourmetListLayout.getItemCount();
                if (size == 0)
                {
                    refreshList(isShowProgress, 1);
                }
                break;

            case MAP:
                refreshList(isShowProgress, 0);
                break;

            default:
                break;
        }
    }

    public void addList(boolean isShowProgress)
    {
        refreshList(isShowProgress, mLoadMorePageIndex + 1);
    }

    protected void refreshList(boolean isShowProgress, int page)
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

        if (mGourmetCuration == null || mGourmetCuration.getCurationOption() == null//
            || mGourmetCuration.getCurationOption().getSortType() == null//
            || (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        GourmetParams params = (GourmetParams) mGourmetCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        ((GourmetListNetworkController) mNetworkController).requestGourmetList(params);
    }

    public boolean hasSalesPlace()
    {
        return mGourmetListLayout.hasSalesPlace();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mGourmetListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    @Override
    public void setScrollListTop()
    {
        if (mGourmetListLayout == null)
        {
            return;
        }

        mGourmetListLayout.setScrollListTop();
    }

    protected ArrayList<PlaceViewItem> makeSectionGourmetList(List<Gourmet> gourmetList, SortType sortType)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return placeViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        int entryPosition = 1;

        if (mGourmetListLayout != null)
        {
            ArrayList<PlaceViewItem> oldList = new ArrayList<>(mGourmetListLayout.getList());

            int oldListSize = oldList == null ? 0 : oldList.size();
            if (oldListSize > 0)
            {
                int start = oldList == null ? 0 : oldList.size() - 1;
                int end = oldList == null ? 0 : oldListSize - 5;
                end = end < 0 ? 0 : end;

                // 5번안에 검사 안끝나면 그냥 종료, 원래는 1번에 검사되어야 함
                for (int i = start; i >= end; i--)
                {
                    PlaceViewItem item = oldList.get(i);
                    if (item.mType == PlaceViewItem.TYPE_ENTRY)
                    {
                        Place place = item.getItem();
                        entryPosition = place.entryPosition + 1;
                        break;
                    }
                }
            }
        }

        for (Gourmet gourmet : gourmetList)
        {
            // 지역순에만 section 존재함
            if (SortType.DEFAULT == sortType)
            {
                String region = gourmet.districtName;

                if (Util.isTextEmpty(region) == true)
                {
                    continue;
                }

                if (gourmet.isDailyChoice == true)
                {
                    if (hasDailyChoice == false)
                    {
                        hasDailyChoice = true;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, mBaseActivity.getResources().getString(R.string.label_dailychoice));
                        placeViewItemList.add(section);
                    }
                } else
                {
                    if (Util.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                    {
                        previousRegion = region;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                        placeViewItemList.add(section);
                    }
                }
            }

            gourmet.entryPosition = entryPosition;
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
            entryPosition++;
        }

        return placeViewItemList;
    }

    protected void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount, //
                                 HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
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
            mGourmetListLayout.clearList();

            if (mGourmetCuration.getCurationOption().isDefaultFilter() == true)
            {
                ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetCategoryFilter(page, categoryCodeMap, categorySequenceMap);
            }
        }

        int listSize = list == null ? 0 : list.size();
        if (listSize > 0)
        {
            mLoadMorePageIndex = page;
        }

        mGourmetCount += listSize;

        SortType sortType = mGourmetCuration.getCurationOption().getSortType();

        ArrayList<PlaceViewItem> placeViewItems = makeSectionGourmetList(list, sortType);

        switch (mViewType)
        {
            case LIST:
            {
                mGourmetListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                int size = mGourmetListLayout.getItemCount();

                if (size == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                mEventListener.onShowActivityEmptyView(size == 0);
                break;
            }

            case MAP:
            {
                mGourmetListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType);

                int mapSize = mGourmetListLayout.getMapItemSize();
                if (mapSize == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                mEventListener.onShowActivityEmptyView(mapSize == 0);
                break;
            }

            default:
                break;
        }

        unLockUI();
        mGourmetListLayout.setSwipeRefreshing(false);
    }

    @Override
    public int getPlaceCount()
    {
        return mGourmetCount;
    }

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    protected GourmetListLayout.OnEventListener mEventListener = new GourmetListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetClick(placeViewItem, getPlaceCount());
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

    private GourmetListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
        {
            String value = mGourmetCuration.getSaleTime().getDayOfDaysDateFormat("yyyyMMdd");
            DailyPreference.getInstance(mBaseActivity).setGourmetLastViewDate(value);

            GourmetListFragment.this.onGourmetList(list, page, totalCount, maxCount, categoryCodeMap, categorySequenceMap);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetListFragment.this.onErrorResponse(volleyError);
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
}
