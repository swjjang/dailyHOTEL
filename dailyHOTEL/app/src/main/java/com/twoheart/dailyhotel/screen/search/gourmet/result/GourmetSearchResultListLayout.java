package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListLayout;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListMapFragment;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class GourmetSearchResultListLayout extends GourmetListLayout
{
    private TextView mResultTextView;
    private Constants.SearchType mSearchType;

    public GourmetSearchResultListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mResultTextView = (TextView) view.findViewById(R.id.resultCountTextView);

        //        setBannerVisibility(false);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new GourmetSearchResultListAdapter(context, arrayList, mOnItemClickListener, null);
    }

    @Override
    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, Constants.EmptyStatus emptyStatus, boolean isCurrentPage)
    {
        if (emptyStatus == Constants.EmptyStatus.EMPTY)
        {
            GourmetCurationOption GourmetCurationOption = mGourmetCuration == null //
                ? new GourmetCurationOption() //
                : (GourmetCurationOption) mGourmetCuration.getCurationOption();

            if (GourmetCurationOption.isDefaultFilter() == true)
            {
                mEmptyView.setVisibility(View.VISIBLE);
                mFilterEmptyView.setVisibility(View.GONE);
            } else
            {
                mEmptyView.setVisibility(View.GONE);
                mFilterEmptyView.setVisibility(View.VISIBLE);
            }

            mMapLayout.setVisibility(View.GONE);
            mResultTextView.setVisibility(View.GONE);

            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
        } else
        {
            switch (viewType)
            {
                case LIST:
                    mEmptyView.setVisibility(View.GONE);
                    mMapLayout.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.GONE);
                    mResultTextView.setVisibility(View.VISIBLE);

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
                    mResultTextView.setVisibility(View.GONE);
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
                            Crashlytics.log("GourmetSearchResultListLayout");
                            Crashlytics.logException(e);
                        }
                    }

                    mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    public void setMapMyLocation(Location location, boolean isVisible)
    {
        if (mPlaceListMapFragment == null || location == null)
        {
            return;
        }

        mPlaceListMapFragment.setMyLocation(location, isVisible);
    }

    public void setSearchType(Constants.SearchType searchType)
    {
        mSearchType = searchType;
    }

    public void updateResultCount(Constants.ViewType viewType, int count, int maxCount)
    {
        if (mResultTextView == null)
        {
            return;
        }

        if (count <= 0)
        {
            mResultTextView.setVisibility(View.GONE);
        } else
        {
            if (viewType == Constants.ViewType.LIST)
            {
                mResultTextView.setVisibility(View.VISIBLE);
            } else
            {
                mResultTextView.setVisibility(View.GONE);
            }

            if (count >= maxCount)
            {
                mResultTextView.setText(mContext.getString(R.string.label_searchresult_over_resultcount, maxCount));
            } else
            {
                mResultTextView.setText(mContext.getString(R.string.label_searchresult_resultcount, count));
            }
        }
    }

    @Override
    public void addResultList(FragmentManager fragmentManager, Constants.ViewType viewType//
        , ArrayList<PlaceViewItem> list, Constants.SortType sortType, PlaceBookingDay placeBookingDay, boolean rewardEnabled)
    {
        if (mSearchType == Constants.SearchType.LOCATION)
        {
            mPlaceListAdapter.setShowDistanceIgnoreSort(true);
        }

        super.addResultList(fragmentManager, viewType, list, sortType, placeBookingDay, rewardEnabled);
    }
}
