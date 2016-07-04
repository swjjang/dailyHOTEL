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

    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                        ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        mPlaceListAdapter.clear();

        if (list == null || list.size() == 0)
        {
            mPlaceListAdapter.notifyDataSetChanged();

            setVisibility(fragmentManager, Constants.ViewType.GONE, true);

        } else
        {
            setVisibility(fragmentManager, viewType, true);

            if (viewType == Constants.ViewType.MAP)
            {
                mStayMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
                    }
                });

                mStayMapFragment.setPlaceViewItemList(list, mScrollListTop);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP);
            } else
            {
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
            }

            if (sortType == Constants.SortType.DEFAULT)
            {
                if (StayEventBannerManager.getInstance().getCount() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
                        StayEventBannerManager.getInstance().getList());
                    list.add(0, placeViewItem);
                }
            }

            mPlaceListAdapter.clear();
            mPlaceListAdapter.addAll(list);
            mPlaceListAdapter.notifyDataSetChanged();

            if (mScrollListTop == true)
            {
                mScrollListTop = false;
                mPlaceRecyclerView.scrollToPosition(0);
            }
        }
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
