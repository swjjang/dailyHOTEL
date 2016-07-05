package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StayListLayout extends PlaceListLayout
{
    private StayMapFragment mStayMapFragment;

    public StayListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlacetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new StayListAdapter(mContext, new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
    }

    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mStayMapFragment != null)
                {
                    mStayMapFragment.resetMenuBarLayoutranslation();
                    fragmentManager.beginTransaction().remove(mStayMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mStayMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mStayMapFragment == null)
                {
                    mStayMapFragment = new StayMapFragment();
                    mStayMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mStayMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY);
                break;
        }
    }

    public boolean isShowInformationAtMapView(Constants.ViewType viewType)
    {
        if (viewType == Constants.ViewType.MAP && mStayMapFragment != null)
        {
            return mStayMapFragment.isShowInformation();
        }

        return false;
    }

    @Override
    protected PlaceListMapFragment getListMapFragment()
    {
        return mStayMapFragment;
    }

    public List<PlaceViewItem> getList()
    {
        if (mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getAll();
    }

    public void addResultList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                              ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        mIsLoading = false;

        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        // 지도의 경우 무조건 전체 데이터를 가져옴으로 clear 후 진행되야 함
        if (viewType == Constants.ViewType.MAP)
        {
            clearList();

            if (list == null || list.size() == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);

            } else
            {
                setVisibility(fragmentManager, viewType, true);

                mStayMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
                    }
                });

                mStayMapFragment.setPlaceViewItemList(list, true);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP);
            }

        } else
        {
            setVisibility(fragmentManager, viewType, true);

            // 리스트의 경우 Pagenation 상황 고려
            ArrayList<PlaceViewItem> oldList = new ArrayList<>(getList());

            if (oldList != null && oldList.size() > 0)
            {
                PlaceViewItem placeViewItem = oldList.get(oldList.size() - 1);

                if (placeViewItem.mType == PlaceViewItem.TYPE_FOOTER_VIEW)
                {
                    getList().remove(placeViewItem);
                }
            }

            if (list != null && list.size() > 0)
            {
                mPlaceListAdapter.addAll(list);
            }

            List<PlaceViewItem> allList = getList();
            if (allList == null || allList.size() == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);

            } else
            {

                // 배너의 경우 리스트 타입이면서, 기존 데이터가 0일때 즉 첫 페이지일때, sortType은 default type 이면서 배너가 있을때만 최상단에 위치한다.
                if (oldList == null || oldList.size() == 0)
                {
                    if (sortType == Constants.SortType.DEFAULT)
                    {
                        if (StayEventBannerManager.getInstance().getCount() > 0)
                        {
                            PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
                                StayEventBannerManager.getInstance().getList());
                            mPlaceListAdapter.add(0, placeViewItem);
                        }
                    }
                }

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST);

                Map<String, String> params = new HashMap<>();
                Province province = StayCurationManager.getInstance().getProvince();

                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST, params);

                mPlaceListAdapter.setSortType(sortType);
                mPlaceListAdapter.notifyDataSetChanged();
            }
        }
    }

    // stay 에서 사용안함 기존 소스 유지
    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                        ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        ExLog.d("call method");
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mPlaceListAdapter.getAll());
    }

    protected boolean hasSalesPlace(List<PlaceViewItem> list)
    {
        if (list == null || list.size() == 0)
        {
            return false;
        }

        boolean hasPlace = false;

        for (PlaceViewItem placeViewItem : list)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                && placeViewItem.<Stay>getItem().isSoldOut == false)
            {
                hasPlace = true;
                break;
            }
        }

        return hasPlace;
    }

    public int getMapItemSize()
    {
        return mStayMapFragment != null ? mStayMapFragment.getPlaceViewItemListSize() : 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Listener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mPlaceRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(null);
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
            }
        }
    };

    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Integer index = (Integer) view.getTag(view.getId());
            if (index != null)
            {
                EventBanner eventBanner = StayEventBannerManager.getInstance().getEventBanner(index);

                ((OnEventListener) mOnEventListener).onEventBannerClick(eventBanner);
            }
        }
    };
}
