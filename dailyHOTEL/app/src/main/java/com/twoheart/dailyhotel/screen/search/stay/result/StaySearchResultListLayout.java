package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.graphics.Paint;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListMapFragment;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class StaySearchResultListLayout extends StayListLayout
{
    private TextView mResultTextView;
    private boolean mLocationSearchType;

    public interface OnEventListener extends StayListLayout.OnEventListener
    {
        void onResearchClick();

        void onRadiusClick();
    }

    public StaySearchResultListLayout(Context context, PlaceListLayout.OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mResultTextView = view.findViewById(R.id.resultCountTextView);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new StaySearchResultListAdapter(context, arrayList, mOnItemClickListener, null);
    }

    @Override
    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, Constants.EmptyStatus emptyStatus, boolean isCurrentPage)
    {
        if (emptyStatus == Constants.EmptyStatus.EMPTY)
        {
            StayCurationOption stayCurationOption = mStayCuration == null //
                ? new StayCurationOption() //
                : (StayCurationOption) mStayCuration.getCurationOption();

            StaySuggestV2 suggest = ((StaySearchCuration) mStayCuration).getSuggest();

            if(suggest.isLocationSuggestType() == true)
            {
                if ((stayCurationOption.isDefaultFilter() == true && ((StaySearchCuration) mStayCuration).getRadius() == PlaceSearchResultActivity.DEFAULT_SEARCH_RADIUS))
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mFilterEmptyView.setVisibility(View.GONE);
                    ((OnEventListener) mOnEventListener).onBottomOptionVisible(false);
                } else
                {
                    mEmptyView.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.VISIBLE);
                    ((OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
                }
            } else
            {
                if (stayCurationOption.isDefaultFilter() == true)
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mFilterEmptyView.setVisibility(View.GONE);
                    ((OnEventListener) mOnEventListener).onBottomOptionVisible(false);
                } else
                {
                    mEmptyView.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.VISIBLE);
                    ((OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
                }
            }

            mMapLayout.setVisibility(View.GONE);
            mResultTextView.setVisibility(View.GONE);

            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

            if (viewType == Constants.ViewType.LIST)
            {
                ((OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(false);
            } else
            {
                ((OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
            }
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

                    ((OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
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
                            mPlaceListMapFragment = new StayListMapFragment();
                            mPlaceListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                            fragmentManager.beginTransaction().add(mMapLayout.getId(), mPlaceListMapFragment).commitAllowingStateLoss();
                        } catch (IllegalStateException e)
                        {
                            Crashlytics.log("StaySearchResultListLayout");
                            Crashlytics.logException(e);
                        }
                    }

                    mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                    ((OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
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

    public void setLocationSearchType(boolean searchType)
    {
        mLocationSearchType = searchType;
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
        mPlaceListAdapter.setShowDistanceIgnoreSort(mLocationSearchType);

        super.addResultList(fragmentManager, viewType, list, sortType, placeBookingDay, rewardEnabled);
    }

    /**
     * 검색 방식에 따라서 빈화면의 내용이 다르다.
     *
     */
    public void setEmptyType(StaySuggestV2 suggest)
    {
        if (suggest == null || mEmptyView == null || mFilterEmptyView == null)
        {
            return;
        }

        if(suggest.isLocationSuggestType() == true)
        {
            if (mStayCuration.getCurationOption().isDefaultFilter() == true//
                && ((StaySearchCuration) mStayCuration).getRadius() == PlaceSearchResultActivity.DEFAULT_SEARCH_RADIUS)
            {
                setLocationTypeEmptyView(mEmptyView);
            } else
            {
                setLocationTypeFilterEmptyView(mFilterEmptyView);
            }
        } else
        {
            if (mStayCuration.getCurationOption().isDefaultFilter() == true//
                && ((StaySearchCuration) mStayCuration).getRadius() == PlaceSearchResultActivity.DEFAULT_SEARCH_RADIUS)
            {
                setDefaultTypeEmptyView(mEmptyView);
            } else
            {
                setDefaultTypeFilterEmptyView(mFilterEmptyView);
            }
        }
    }

    private void setLocationTypeEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView messageTextView01 = view.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = view.findViewById(R.id.messageTextView02);

        messageTextView01.setText(R.string.message_searchresult_stay_empty_message01);
        messageTextView02.setText(R.string.message_searchresult_stay_empty_message02);

        TextView researchView = view.findViewById(R.id.changeRegionView);
        View changeDateView = view.findViewById(R.id.changeDateView);

        researchView.setText(R.string.label_searchresult_research);
        changeDateView.setVisibility(View.GONE);

        researchView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onResearchClick());

        TextView callTextView = view.findViewById(R.id.callTextView);
        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    private void setDefaultTypeEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView messageTextView01 = view.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = view.findViewById(R.id.messageTextView02);

        messageTextView01.setText(R.string.message_searchresult_stay_filter_empty_message01);
        messageTextView02.setText(R.string.message_changing_option);

        View changeRegionView = view.findViewById(R.id.changeRegionView);
        View changeDateView = view.findViewById(R.id.changeDateView);

        changeRegionView.setVisibility(View.GONE);
        changeDateView.setVisibility(View.GONE);

        TextView callTextView = view.findViewById(R.id.callTextView);
        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    private void setLocationTypeFilterEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView filterMessageTextView01 = view.findViewById(R.id.filterMessageTextView01);
        TextView filterMessageTextView02 = view.findViewById(R.id.filterMessageTextView02);

        filterMessageTextView01.setText(R.string.message_searchresult_stay_filter_empty_message01);
        filterMessageTextView02.setText(R.string.message_searchresult_stay_filter_empty_message02);

        TextView buttonView = view.findViewById(R.id.buttonView);
        buttonView.setText(R.string.label_searchresult_change_radius);

        buttonView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onRadiusClick());
    }

    private void setDefaultTypeFilterEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView filterMessageTextView01 = view.findViewById(R.id.filterMessageTextView01);
        TextView filterMessageTextView02 = view.findViewById(R.id.filterMessageTextView02);

        filterMessageTextView01.setText(R.string.message_searchresult_stay_filter_empty_message01);
        filterMessageTextView02.setText(R.string.message_changing_filter_option);

        TextView buttonView = view.findViewById(R.id.buttonView);
        buttonView.setText(R.string.label_hotel_list_changing_filter);

        buttonView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onFilterClick());
    }
}
