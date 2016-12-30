package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import java.util.ArrayList;

public class GourmetListLayout extends PlaceListLayout
{
    protected GourmetCuration mGourmetCuration;

    public GourmetListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new GourmetListAdapter(context, arrayList, mOnItemClickListener, mOnEventBannerItemClickListener);
    }

    @Override
    protected EventBanner getEventBanner(int index)
    {
        return GourmetEventBannerManager.getInstance().getEventBanner(index);
    }

    @Override
    protected PlaceViewItem getEventBannerViewItem()
    {
        if (GourmetEventBannerManager.getInstance().getCount() == 0)
        {
            return null;
        }

        PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
            GourmetEventBannerManager.getInstance().getList());
        return placeViewItem;
    }

    @Override
    protected void onInformationClick(View view, PlaceViewItem placeViewItem)
    {
        ((OnEventListener) mOnEventListener).onPlaceClick(view, placeViewItem);

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_MAP_DETAIL_VIEW_CLICKED, placeViewItem.<Place>getItem().name, null);
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
                    mPlaceListMapFragment.resetMenuBarLayoutTranslation();
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
                    try
                    {
                        mPlaceListMapFragment = new GourmetListMapFragment();
                        mPlaceListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                        fragmentManager.beginTransaction().add(mMapLayout.getId(), mPlaceListMapFragment).commitAllowingStateLoss();
                    } catch (IllegalStateException e)
                    {
                        Crashlytics.log("GourmetListLayout");
                        Crashlytics.logException(e);
                    }
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                GourmetCurationOption gourmetCurationOption = mGourmetCuration == null //
                    ? new GourmetCurationOption() //
                    : (GourmetCurationOption) mGourmetCuration.getCurationOption();

                if (gourmetCurationOption.isDefaultFilter() == true)
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

                AnalyticsManager.getInstance(mContext).recordScreen(Screen.DAILYGOURMET_LIST_EMPTY);
                break;
        }
    }

    public void setGourmetCuration(GourmetCuration curation)
    {
        mGourmetCuration = curation;
    }
}
