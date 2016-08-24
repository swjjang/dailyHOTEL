package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListLayout;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListMapFragment;
import com.twoheart.dailyhotel.util.Constants;

public class GourmetSearchResultListLayout extends GourmetListLayout
{
    private TextView mResultTextView;

    public GourmetSearchResultListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mResultTextView = (TextView) view.findViewById(R.id.resultCountView);

        setBannerVisibility(false);
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
                mResultTextView.setVisibility(View.VISIBLE);

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
                mResultTextView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);
                mFilterEmptyView.setVisibility(View.GONE);

                if (isCurrentPage == true && mGourmetListMapFragment == null)
                {
                    mGourmetListMapFragment = new GourmetListMapFragment();
                    mGourmetListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mGourmetListMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);
                break;

            case GONE:
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
                break;
        }
    }

    public void setMapMyLocation(Location location, boolean isVisible)
    {
        if (mGourmetListMapFragment == null || location == null)
        {
            return;
        }

        mGourmetListMapFragment.setMyLocation(location, isVisible);
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
}
