package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetListLayout extends PlaceListLayout
{
    private GourmetListMapFragment mGourmetListMapFragment;

    public GourmetListLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected PlaceListAdapter getPlacetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new GourmetListAdapter(mContext, new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
    }

    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mGourmetListMapFragment != null)
                {
                    mGourmetListMapFragment.resetMenuBarLayoutranslation();
                    fragmentManager.beginTransaction().remove(mGourmetListMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mGourmetListMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mGourmetListMapFragment == null)
                {
                    mGourmetListMapFragment = new GourmetListMapFragment();
                    mGourmetListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mGourmetListMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                AnalyticsManager.getInstance(mContext).recordScreen(Screen.DAILYGOURMET_LIST_EMPTY);

                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public boolean isShowInformationAtMapView(Constants.ViewType viewType)
    {
        if (viewType == Constants.ViewType.MAP && mGourmetListMapFragment != null)
        {
            return mGourmetListMapFragment.isShowInformation();
        }

        return false;
    }

    @Override
    protected PlaceListMapFragment getListMapFragment()
    {
        return mGourmetListMapFragment;
    }

    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, ArrayList<PlaceViewItem> list, Constants.SortType sortType)
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
                mGourmetListMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
                    }
                });

                mGourmetListMapFragment.setPlaceViewItemList(list, mScrollListTop);

                AnalyticsManager.getInstance(mContext).recordScreen(Screen.DAILYGOURMET_LIST_MAP);
            } else
            {
                AnalyticsManager.getInstance(mContext).recordScreen(Screen.DAILYGOURMET_LIST);

                Map<String, String> parmas = new HashMap<>();
                GourmetCurationOption gourmetCurationOption = GourmetCurationManager.getInstance().getGourmetCurationOption();
                Province province = GourmetCurationManager.getInstance().getProvince();

                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    parmas.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    parmas.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                } else
                {
                    parmas.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    parmas.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                AnalyticsManager.getInstance(mContext).recordScreen(Screen.DAILYGOURMET_LIST, parmas);
            }

            if (sortType == Constants.SortType.DEFAULT)
            {
                if (GourmetEventBannerManager.getInstance().getCount() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER//
                        , GourmetEventBannerManager.getInstance().getList());
                    list.add(0, placeViewItem);
                }
            }

            ((GourmetListAdapter) mPlaceListAdapter).addAll(list, sortType);
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

    private boolean hasSalesPlace(List<PlaceViewItem> list)
    {
        if (list == null || list.size() == 0)
        {
            return false;
        }

        boolean hasPlace = false;

        for (PlaceViewItem placeViewItem : list)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                && placeViewItem.<Gourmet>getItem().isSoldOut == false)
            {
                hasPlace = true;
                break;
            }
        }

        return hasPlace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

            PlaceViewItem gourmetViewItem = mPlaceListAdapter.getItem(position);

            if (gourmetViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(gourmetViewItem);
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
                EventBanner eventBanner = GourmetEventBannerManager.getInstance().getEventBanner(index);
                ((OnEventListener) mOnEventListener).onEventBannerClick(eventBanner);
            }
        }
    };
}
