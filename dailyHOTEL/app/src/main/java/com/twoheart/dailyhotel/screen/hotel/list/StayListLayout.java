package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class StayListLayout extends PlaceListLayout
{
    protected StayCuration mStayCuration;

    public StayListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new StayListAdapter(context, arrayList, mOnItemClickListener, mOnEventBannerItemClickListener);
    }

    @Override
    protected EventBanner getEventBanner(int index)
    {
        return StayEventBannerManager.getInstance().getEventBanner(index);
    }

    @Override
    protected PlaceViewItem getEventBannerViewItem()
    {
        if (StayEventBannerManager.getInstance().getCount() == 0)
        {
            return null;
        }

        PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
            StayEventBannerManager.getInstance().getList());
        return placeViewItem;
    }

    @Override
    protected void onInformationClick(PlaceViewItem placeViewItem)
    {
        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_MAP_DETAIL_VIEW_CLICKED, placeViewItem.<Place>getItem().name, null);
    }

    @Override
    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);
                mFilterEmptyView.setVisibility(View.GONE);

                if (mPlaceListMapFragment != null)
                {
                    mPlaceListMapFragment.resetMenuBarLayoutranslation();
                    fragmentManager.beginTransaction().remove(mPlaceListMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mPlaceListMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);
                mFilterEmptyView.setVisibility(View.GONE);

                if (isCurrentPage == true && mPlaceListMapFragment == null)
                {
                    mPlaceListMapFragment = new StayListMapFragment();
                    mPlaceListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mPlaceListMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                StayCurationOption stayCurationOption = mStayCuration == null //
                    ? new StayCurationOption() //
                    : (StayCurationOption) mStayCuration.getCurationOption();

                if (stayCurationOption.isDefaultFilter() == true)
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mFilterEmptyView.setVisibility(View.GONE);
                } else
                {
                    mEmptyView.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.VISIBLE);
                }

                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY);
                break;
        }
    }

    public void setStayCuration(StayCuration curation)
    {
        mStayCuration = curation;
    }
}
