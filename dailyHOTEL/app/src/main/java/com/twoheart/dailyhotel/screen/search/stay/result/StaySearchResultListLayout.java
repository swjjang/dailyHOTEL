package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class StaySearchResultListLayout extends StayListLayout
{
    private TextView mResultTextView;
    private boolean mLocationSearchType;

    public StaySearchResultListLayout(Context context, PlaceListLayout.OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        mPlaceRecyclerView = view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mPlaceRecyclerView.setLayoutManager(mLayoutManager);
        EdgeEffectColor.setEdgeGlowColor(mPlaceRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mPlaceListAdapter = getPlaceListAdapter(mContext, new ArrayList<>());

        if (DailyPreference.getInstance(mContext).getTrueVRSupport() > 0)
        {
            mPlaceListAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(mContext) == true)
        {
            mPlaceListAdapter.setOnLongClickListener(mOnItemLongClickListener);
        }

        mPlaceListAdapter.setOnWishClickListener(mOnWishClickListener);

        mPlaceRecyclerView.setAdapter(mPlaceListAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });

        mPlaceRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mPlaceRecyclerView.getLayoutManager();

                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        mSwipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                } else
                {
                    if (mIsLoading == false)
                    {
                        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                        int itemCount = mLayoutManager.getItemCount();

                        int loadMorePosition = itemCount > LOAD_MORE_POSITION_GAP //
                            ? lastVisibleItemPosition + LOAD_MORE_POSITION_GAP //
                            : lastVisibleItemPosition + (itemCount / 3);

                        if (itemCount > 0)
                        {
                            if ((itemCount - 1) <= (loadMorePosition))
                            {
                                mIsLoading = true;
                                ((OnEventListener) mOnEventListener).onLoadMoreList();
                            }
                        }
                    }
                }

                ((OnEventListener) mOnEventListener).onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                ((OnEventListener) mOnEventListener).onScrollStateChanged(recyclerView, newState);
            }
        });

        mEmptyView = view.findViewById(R.id.emptyView);

        initEmptyView(mEmptyView);

        mMapLayout = view.findViewById(R.id.mapLayout);
        mPlaceRecyclerView.setShadowVisible(false);
        //        setBannerVisibility(false);

        mResultTextView = view.findViewById(R.id.resultCountTextView);

        //        setBannerVisibility(false);
    }

    @Override
    protected void initEmptyView(View view)
    {
        if (view == null)
        {
            return;
        }

        //        View searchStayOutboundLayout = mEmptyView.findViewById(R.id.searchStayOutboundLayout);
        //        View searchGourmetLayout = mEmptyView.findViewById(R.id.searchGourmetLayout);
        //
        //        ((DailyStayListEmptyView) mFilterEmptyView).setMessageTextView(mContext.getString(R.string.message_not_exist_filters), mContext.getString(R.string.message_changing_filter_option));
        //        ((DailyStayListEmptyView) mFilterEmptyView).setButton01(true, mContext.getString(R.string.label_hotel_list_changing_filter), new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                ((StaySearchResultLayout.OnEventListener) mOnEventListener).onFilterClick();
        //            }
        //        });
        //
        //        ((DailyStayListEmptyView) mFilterEmptyView).setButton02(false, null, null);
        //        ((DailyStayListEmptyView) mFilterEmptyView).setBottomMessageVisible(false);
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

            if (stayCurationOption.isDefaultFilter() == true)
            {
                // 필터없이 리스트가 없는 경우
                showEmptyView(true);

                ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(false);
            } else
            {
                // 필터링으로 리스트가 없는 경우
                showEmptyView(false);

                ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);
            }

            mMapLayout.setVisibility(View.GONE);
            mResultTextView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

            if (viewType == Constants.ViewType.LIST)
            {
                ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(false);
            } else
            {
                ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
            }
        } else
        {
            switch (viewType)
            {
                case LIST:
                    mEmptyView.setVisibility(View.GONE);
                    mMapLayout.setVisibility(View.GONE);
                    mResultTextView.setVisibility(View.VISIBLE);

                    if (mPlaceListMapFragment != null)
                    {
                        mPlaceListMapFragment.resetMenuBarLayoutTranslation();
                        fragmentManager.beginTransaction().remove(mPlaceListMapFragment).commitAllowingStateLoss();
                        mMapLayout.removeAllViews();
                        mPlaceListMapFragment = null;
                    }

                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                    ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
                    break;

                case MAP:
                    mResultTextView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mMapLayout.setVisibility(View.VISIBLE);

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

                    ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateFilterEnabled(true);

                    if (emptyStatus != Constants.EmptyStatus.NONE)
                    {
                        ((StaySearchResultListLayout.OnEventListener) mOnEventListener).onUpdateViewTypeEnabled(true);
                    }
                    break;
            }
        }
    }

    @Override
    public void setFilterEmptyScreenVisible(boolean visible)
    {
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

    private void showEmptyView(boolean hasDefaultFilter)
    {

    }
}
