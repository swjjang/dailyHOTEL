package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.screen.home.stay.outbound.list.map.StayOutboundMapFragment;
import com.daily.dailyhotel.screen.home.stay.outbound.list.map.StayOutboundMapViewPagerAdapter;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailyRecyclerStickyItemDecoration;
import com.daily.dailyhotel.view.DailySearchStayOutboundAreaCardView;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchResultDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;

import java.util.List;

import io.reactivex.Observable;

public class StayOutboundListView extends BaseBlurView<StayOutboundListView.OnEventListener, ActivityStayOutboundSearchResultDataBinding>//
    implements StayOutboundListViewInterface, ViewPager.OnPageChangeListener, View.OnClickListener, StayOutboundMapFragment.OnEventListener
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 125;
    private static final int VIEWPAGER_TOP_PADDING_DP = 10;
    private static final int VIEWPAGER_OTHER_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    StayOutboundListAdapter mListAdapter;
    ShimmerViewAdapter mShimmerViewAdapter;

    StayOutboundMapFragment mStayOutboundMapFragment;
    DailyOverScrollViewPager mViewPager;
    StayOutboundMapViewPagerAdapter mViewPagerAdapter;

    ValueAnimator mValueAnimator;

    boolean mPossibleLoadingListByMap;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefreshAll(boolean showProgress);

        void onCalendarClick();

        void onPeopleClick();

        void onFilterClick();

        void onViewTypeClick();

        void onStayClick(android.support.v4.util.Pair[] pairs, StayOutbound stayOutbound);

        void onStayLongClick(int position, android.support.v4.util.Pair[] pairs, StayOutbound stayOutbound);

        void onScrollList(int listSize, int lastVisibleItemPosition);

        // Map Event
        void onMapReady();

        void onMarkerClick(StayOutbound stayOutbound, List<StayOutbound> stayOutboundList);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void onRetryClick();

        void onResearchClick();

        void onCallClick();

        void onWishClick(int position, StayOutbound stayOutbound);

        void onChangedLocation(LatLng latLng, float radius, float zoom);

        void onClearChangedLocation();

        void onRadiusClick();

        void onChangedRadius(float radius);

        void onSearchStayClick();

        void onSearchGourmetClick();

        void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest);
    }

    public StayOutboundListView(BaseActivity baseActivity, StayOutboundListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        // 빈화면 설정
        viewDataBinding.retryTextView.setOnClickListener(v -> getEventListener().onRetryClick());
        viewDataBinding.searchLeftLayout.setOnClickListener(v -> getEventListener().onSearchStayClick());
        viewDataBinding.searchRightLayout.setOnClickListener(v -> getEventListener().onSearchGourmetClick());

        //
        viewDataBinding.swipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener(() -> getEventListener().onRefreshAll(false));

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (getViewDataBinding() == null || recyclerView == null)
                {
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        getViewDataBinding().swipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        getViewDataBinding().swipeRefreshLayout.setEnabled(false);
                    }
                } else
                {
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    int itemCount = linearLayoutManager.getItemCount();

                    getEventListener().onScrollList(itemCount, lastVisibleItemPosition);
                }
            }
        });

        viewDataBinding.floatingActionView.setOnViewOptionClickListener(v -> getEventListener().onViewTypeClick());
        viewDataBinding.floatingActionView.setOnFilterOptionClickListener(v -> getEventListener().onFilterClick());

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.location_progressbar_cc8c8c8), PorterDuff.Mode.SRC_IN);
        viewDataBinding.mapProgressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.dh_theme_color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

    }

    @Override
    public void setToolbarTitle(String titleText, CharSequence subTitleText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(titleText);
        getViewDataBinding().toolbarView.setSubTitleText(subTitleText);
    }

    @Override
    public void setRadius(float radius)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position;

        if (radius == 10.0f)
        {
            position = 3;
        } else if (radius == 7.0f)
        {
            position = 2;
        } else if (radius == 5.0f)
        {
            position = 1;
        } else
        {
            position = 0; // 1km
        }

        getViewDataBinding().toolbarView.setRadiusSpinnerSelection(position);
    }

    @Override
    public void setRadiusVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setRadiusSpinnerVisible(visible);
    }

    @Override
    public void setCalendarText(String calendarText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setSubTitleText(calendarText);
    }

    @Override
    public void setStayOutboundList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRefreshing(false);

        if (objectItemList.size() == 0)
        {
            return;
        }

        if (mListAdapter == null)
        {
            mListAdapter = new StayOutboundListAdapter(getContext(), null);
            mListAdapter.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return;
                    }

                    ObjectItem objectItem = mListAdapter.getItem(position);

                    if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayOutboundCardView == true)
                        {
                            getEventListener().onStayClick(((DailyStayOutboundCardView) view).getOptionsCompat(), objectItem.getItem());
                        } else
                        {
                            View simpleDraweeView = view.findViewById(R.id.imageView);
                            View gradientTopView = view.findViewById(R.id.gradientTopView);
                            View gradientBottomView = view.findViewById(R.id.gradientView);

                            android.support.v4.util.Pair[] pairs = new Pair[3];
                            pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                            pairs[1] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                            pairs[2] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                            getEventListener().onStayClick(pairs, objectItem.getItem());
                        }
                    }
                }
            }, new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return false;
                    }

                    ObjectItem objectItem = mListAdapter.getItem(position);

                    if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayOutboundCardView == true)
                        {
                            getEventListener().onStayLongClick(position, ((DailyStayOutboundCardView) view).getOptionsCompat(), objectItem.getItem());
                        } else
                        {
                            View simpleDraweeView = view.findViewById(R.id.imageView);
                            View nameTextView = view.findViewById(R.id.nameTextView);
                            View gradientTopView = view.findViewById(R.id.gradientTopView);
                            View gradientBottomView = view.findViewById(R.id.gradientView);

                            android.support.v4.util.Pair[] pairs = new Pair[3];
                            pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                            pairs[1] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                            pairs[2] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                            getEventListener().onStayLongClick(position, pairs, objectItem.getItem());
                        }
                    }

                    return true;
                }
            });

            mListAdapter.setOnWishClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return;
                    }

                    ObjectItem objectItem = mListAdapter.getItem(position);

                    if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        getEventListener().onWishClick(position, objectItem.getItem());
                    }
                }
            });

            DailyRecyclerStickyItemDecoration itemDecoration = new DailyRecyclerStickyItemDecoration(getViewDataBinding().recyclerView, mListAdapter);
            getViewDataBinding().recyclerView.addItemDecoration(itemDecoration);
        }

        DailyRecyclerStickyItemDecoration itemDecoration = getItemDecoration(getViewDataBinding().recyclerView);

        if (itemDecoration != null)
        {
            itemDecoration.setStickyEnabled(hasSectionList(objectItemList));
        }

        getViewDataBinding().recyclerView.setAdapter(mListAdapter);

        mListAdapter.setAll(objectItemList);
        mListAdapter.setDistanceEnabled(isSortByDistance);
        mListAdapter.setNightsEnabled(isNights);
        mListAdapter.setRewardEnabled(rewardEnabled);
        mListAdapter.notifyDataSetChanged();
    }

    private DailyRecyclerStickyItemDecoration getItemDecoration(RecyclerView recyclerView)
    {
        if (recyclerView == null)
        {
            return null;
        }

        int itemDecorationCount = recyclerView.getItemDecorationCount();

        if (itemDecorationCount > 0)
        {
            for (int i = 0; i < itemDecorationCount; i++)
            {
                RecyclerView.ItemDecoration itemDecoration = recyclerView.getItemDecorationAt(i);

                if (itemDecoration instanceof DailyRecyclerStickyItemDecoration)
                {
                    return (DailyRecyclerStickyItemDecoration) itemDecoration;
                }
            }
        }

        return null;
    }

    private boolean hasSectionList(List<ObjectItem> objectItemList)
    {
        return objectItemList != null && objectItemList.size() > 0 && objectItemList.get(0).mType == ObjectItem.TYPE_SECTION;
    }

    @Override
    public void addStayOutboundList(List<ObjectItem> objectItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRefreshing(false);

        if (mListAdapter == null)
        {
            return;
        }

        hideEmptyScreen();
        setErrorScreenVisible(false);
        setSearchLocationScreenVisible(false);
        setListScreenVisible(true);

        mListAdapter.remove(mListAdapter.getItemCount() - 1);
        mListAdapter.addAll(objectItemList);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setStayOutboundMakeMarker(List<StayOutbound> stayOutboundList, boolean moveCameraBounds, boolean clear)
    {
        if (mStayOutboundMapFragment == null || stayOutboundList == null)
        {
            return;
        }

        mStayOutboundMapFragment.setStayOutboundList(stayOutboundList, moveCameraBounds, clear);
    }

    @Override
    public void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList, boolean isNights, boolean rewardEnabled)
    {
        if (context == null)
        {
            return;
        }

        if (mViewPagerAdapter == null)
        {
            mViewPagerAdapter = new StayOutboundMapViewPagerAdapter(context);
            mViewPagerAdapter.setOnPlaceMapViewPagerAdapterListener(new StayOutboundMapViewPagerAdapter.OnPlaceMapViewPagerAdapterListener()
            {
                @Override
                public void onStayClick(View view, StayOutbound stayOutbound)
                {
                    View simpleDraweeView = view.findViewById(R.id.simpleDraweeView);
                    View nameTextView = view.findViewById(R.id.nameTextView);
                    View gradientTopView = view.findViewById(R.id.gradientTopView);
                    View gradientBottomView = view.findViewById(R.id.gradientView);

                    android.support.v4.util.Pair[] pairs = new Pair[3];
                    pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                    pairs[1] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                    pairs[2] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                    getEventListener().onStayClick(pairs, stayOutbound);
                }

                @Override
                public void onWishClick(int position, StayOutbound stayOutbound)
                {
                    getEventListener().onWishClick(position, stayOutbound);
                }
            });
        }

        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(stayOutboundList);
        mViewPagerAdapter.setNightsEnabled(isNights);
        mViewPagerAdapter.setRewardEnabled(rewardEnabled);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getMapLayoutResourceId()
    {
        return 0;
    }

    @Override
    public void setViewTypeOptionImage(StayOutboundListPresenter.ViewState viewState)
    {
        if (viewState == null || getViewDataBinding() == null)
        {
            return;
        }

        switch (viewState)
        {
            case LIST:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.LIST);
                break;

            case MAP:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.MAP);
                break;
        }
    }

    @Override
    public void setFilterOptionImage(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setFilterOptionSelected(selected);
    }

    /**
     * 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
     *
     * @param fragmentManager
     */
    @Override
    public void showMapLayout(FragmentManager fragmentManager)
    {
        if (getViewDataBinding() == null || fragmentManager == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setVisibility(View.INVISIBLE);
        getViewDataBinding().mapLayout.setVisibility(View.VISIBLE);

        if (mStayOutboundMapFragment == null)
        {
            mStayOutboundMapFragment = new StayOutboundMapFragment();
            mStayOutboundMapFragment.setOnEventListener(this);
        }

        fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mStayOutboundMapFragment, "MAP").commitAllowingStateLoss();

        getViewDataBinding().mapLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mPossibleLoadingListByMap = false;

                        getEventListener().onClearChangedLocation();
                        break;

                    case MotionEvent.ACTION_UP:
                        mPossibleLoadingListByMap = true;
                        break;
                }

                return false;
            }
        });

        mViewPager = addMapViewPager(getContext(), getViewDataBinding().mapLayout);
    }

    /**
     * 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
     *
     * @param fragmentManager
     */
    @Override
    public void hideMapLayout(FragmentManager fragmentManager)
    {
        if (getViewDataBinding() == null || fragmentManager == null || mStayOutboundMapFragment == null)
        {
            return;
        }

        if (mViewPagerAdapter != null)
        {
            mViewPagerAdapter.clear();
            mViewPagerAdapter = null;
        }

        if (mViewPager != null)
        {
            mViewPager.removeAllViews();
            mViewPager = null;
        }

        fragmentManager.beginTransaction().remove(mStayOutboundMapFragment).commitAllowingStateLoss();

        getViewDataBinding().mapLayout.removeAllViews();
        getViewDataBinding().mapLayout.setVisibility(View.GONE);
        getViewDataBinding().swipeRefreshLayout.setVisibility(View.VISIBLE);

        setMapProgressBarVisible(false);

        mStayOutboundMapFragment = null;

        resetMenuBarLayoutTranslation();
    }

    @Override
    public void setMapViewPagerVisibility(boolean visibility)
    {
        if (mViewPager == null)
        {
            return;
        }

        mViewPager.bringToFront();

        if (visibility == true)
        {
            if (mViewPager.getVisibility() != View.VISIBLE)
            {
                showViewPagerAnimation();
            }
        } else
        {
            if (mViewPager.getVisibility() == View.VISIBLE)
            {
                hideViewPagerAnimation();

                if (mStayOutboundMapFragment != null)
                {
                    mStayOutboundMapFragment.hideSelectedMarker();
                }
            }
        }
    }

    @Override
    public boolean isMapViewPagerVisibility()
    {
        if (mViewPager == null)
        {
            return false;
        }

        return mViewPager.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setMyLocation(Location location)
    {
        if (mStayOutboundMapFragment == null || location == null)
        {
            return;
        }

        mStayOutboundMapFragment.setMyLocation(new LatLng(location.getLatitude(), location.getLongitude()), true);
    }

    @Override
    public void setRefreshing(boolean refreshing)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setErrorScreenVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().errorLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSearchLocationScreenVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchLocationLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setListScreenVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().resultLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setShimmerScreenVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().shimmerLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);

        if (mShimmerViewAdapter == null)
        {
            mShimmerViewAdapter = new ShimmerViewAdapter(getContext(), 5);
        }

        getViewDataBinding().shimmerRecyclerView.setAdapter(mShimmerViewAdapter);

        if (visible)
        {
            mShimmerViewAdapter.setAnimationStart(true);
        } else
        {
            mShimmerViewAdapter.setAnimationStart(false);
        }

        mShimmerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyScreen(EmptyScreenType emptyScreenType)
    {
        if (getViewDataBinding() == null || emptyScreenType == null)
        {
            return;
        }

        switch (emptyScreenType)
        {
            case SEARCH_SUGGEST_DEFAULT:
                getViewDataBinding().errorLayout.setVisibility(View.GONE);
                getViewDataBinding().emptyScrollView.setVisibility(View.VISIBLE);
                getViewDataBinding().emptyView.setVisibility(View.GONE);
                break;

            case SEARCH_SUGGEST_FILTER_ON:
                getViewDataBinding().errorLayout.setVisibility(View.GONE);
                getViewDataBinding().emptyScrollView.setVisibility(View.GONE);
                getViewDataBinding().emptyView.setVisibility(View.VISIBLE);

                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_searchresult_stay_filter_empty_message01), getString(R.string.message_changing_filter_option));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_hotel_list_changing_filter), v -> getEventListener().onFilterClick());
                getViewDataBinding().emptyView.setButton02(false, null, null);
                getViewDataBinding().emptyView.setBottomMessageVisible(false);
                break;

            case LOCATION_DEFAULT:
                getViewDataBinding().errorLayout.setVisibility(View.GONE);
                getViewDataBinding().emptyScrollView.setVisibility(View.GONE);
                getViewDataBinding().emptyView.setVisibility(View.VISIBLE);

                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_searchresult_stay_empty_message01), getString(R.string.message_changing_option));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_searchresult_research), v -> getEventListener().onResearchClick());
                getViewDataBinding().emptyView.setButton02(false, null, null);
                getViewDataBinding().emptyView.setBottomMessageVisible(true);
                getViewDataBinding().emptyView.setOnCallClickListener(v -> getEventListener().onCallClick());
                break;

            case LOCATOIN_FILTER_ON:
                getViewDataBinding().errorLayout.setVisibility(View.GONE);
                getViewDataBinding().emptyScrollView.setVisibility(View.GONE);
                getViewDataBinding().emptyView.setVisibility(View.VISIBLE);

                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_searchresult_stay_filter_empty_message01), getString(R.string.message_searchresult_stay_filter_empty_message02));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_searchresult_change_radius), v -> getEventListener().onRadiusClick());
                getViewDataBinding().emptyView.setButton02(false, null, null);
                getViewDataBinding().emptyView.setBottomMessageVisible(false);
                break;
        }
    }

    @Override
    public void hideEmptyScreen()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().errorLayout.setVisibility(View.GONE);
        getViewDataBinding().emptyScrollView.setVisibility(View.GONE);
        getViewDataBinding().emptyView.setVisibility(View.GONE);
    }

    @Override
    public void setBottomLayoutType(EmptyScreenType emptyScreenType)
    {
        if (getViewDataBinding() == null || emptyScreenType == null)
        {
            return;
        }

        switch (emptyScreenType)
        {
            case NONE:
                setBottomLayoutVisible(true);
                getViewDataBinding().floatingActionView.setViewOptionEnabled(true);
                getViewDataBinding().floatingActionView.setFilterOptionEnable(true);
                break;

            case SEARCH_SUGGEST_DEFAULT:
                break;

            case SEARCH_SUGGEST_FILTER_ON:
                getViewDataBinding().floatingActionView.setViewOptionEnabled(false);
                getViewDataBinding().floatingActionView.setFilterOptionEnable(true);
                break;

            case LOCATION_DEFAULT:
                setBottomLayoutVisible(false);
                break;

            case LOCATOIN_FILTER_ON:
                getViewDataBinding().floatingActionView.setViewOptionEnabled(false);
                getViewDataBinding().floatingActionView.setFilterOptionEnable(true);
                break;
        }
    }

    @Override
    public void setBottomLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<Long> getLocationAnimation()
    {
        if (mStayOutboundMapFragment == null)
        {
            return null;
        }

        return mStayOutboundMapFragment.getLocationAnimation();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
        if (mViewPagerAdapter == null || mViewPagerAdapter.getCount() <= position)
        {
            return;
        }

        StayOutbound stayOutbound = mViewPagerAdapter.getItem(position);

        if (stayOutbound != null)
        {
            mStayOutboundMapFragment.setSelectedMarker(stayOutbound);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.calendarTextView:
                getEventListener().onCalendarClick();
                break;

            case R.id.peopleTextView:
                getEventListener().onPeopleClick();
                break;
        }
    }

    @Override
    public void onMapReady()
    {
        getEventListener().onMapReady();
    }

    @Override
    public void onMarkerClick(StayOutbound stayOutbound, List<StayOutbound> stayOutboundList)
    {
        getEventListener().onMarkerClick(stayOutbound, stayOutboundList);
    }

    @Override
    public void onMarkersCompleted()
    {
        getEventListener().onMarkersCompleted();
    }

    @Override
    public void onMapClick()
    {
        getEventListener().onMapClick();
    }

    @Override
    public void onMyLocationClick()
    {
        getEventListener().onMyLocationClick();
    }

    @Override
    public void onChangedLocation(LatLng latLng, float radius, float zoom)
    {
        if (mPossibleLoadingListByMap == true)
        {
            mPossibleLoadingListByMap = false;

            getEventListener().onChangedLocation(latLng, radius, zoom);

            hideViewPagerAnimation();
        }
    }

    @Override
    public void showPreviewGuide()
    {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_preview_layout, null, false);

        View confirmTextView = dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dialogView, null, null, false);
    }

    @Override
    public void setWish(int position, boolean wish)
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                StayOutboundListAdapter.StayViewHolder stayViewHolder = (StayOutboundListAdapter.StayViewHolder) getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position);

                if (stayViewHolder != null)
                {
                    stayViewHolder.stayOutboundCardView.setWish(wish);
                }
            }
        });
    }

    @Override
    public void setMapWish(int position, boolean wish)
    {
        if (getViewDataBinding() == null || mViewPagerAdapter == null)
        {
            return;
        }

        mViewPagerAdapter.getItem(position).myWish = wish;
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public ObjectItem getObjectItem(int position)
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getItem(position);
    }

    @Override
    public List<ObjectItem> getObjectItemList()
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getItemList();
    }

    @Override
    public void setMapProgressBarVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().mapProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPopularAreaList(List<StayOutboundSuggest> popularAreaList)
    {
        if (getViewDataBinding() == null || popularAreaList == null || popularAreaList.size() == 0)
        {
            return;
        }

        getViewDataBinding().popularAreaLayout.removeAllViews();

        final int DP_58 = ScreenUtils.dpToPx(getContext(), 58);

        int size = popularAreaList.size();

        for (int i = 0; i < size; i++)
        {
            View view = getAreaView(i + 1, popularAreaList.get(i));

            if (view != null)
            {
                getViewDataBinding().popularAreaLayout.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DP_58));
            }
        }
    }

    @Override
    public void setPopularAreaVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().popularAreasLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showRadiusPopup()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.showRadiusSpinnerPopup();
    }

    private void showViewPagerAnimation()
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (mViewPager.getVisibility() == View.VISIBLE)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DELAY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP));
                float translationY = height - height * value / 100;

                setMenuBarLayoutTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mViewPager.setVisibility(View.VISIBLE);
                mViewPager.setTranslationY(ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP)));
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    private void hideViewPagerAnimation()
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (mViewPager.getVisibility() != View.VISIBLE)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DELAY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP));
                float translationY = height * value / 100;

                setMenuBarLayoutTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setMenuBarLayoutTranslationY(0);

                //                setMenuBarLayoutEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;
                }

                mViewPager.setVisibility(View.INVISIBLE);
                resetMenuBarLayoutTranslation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    private void initToolbar(ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        CharSequence[] strings = getContext().getResources().getTextArray(R.array.search_result_stayoutbound_distance_array);
        RadiusArrayAdapter radiusArrayAdapter = new RadiusArrayAdapter(getContext(), R.layout.list_row_search_result_spinner, strings);
        radiusArrayAdapter.setDropDownViewResource(R.layout.list_row_search_result_sort_dropdown_item);

        viewDataBinding.toolbarView.setTitleImageResource(R.drawable.search_ic_01_search);
        viewDataBinding.toolbarView.setRadiusSpinnerAdapter(radiusArrayAdapter);
        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                getEventListener().onResearchClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                RadiusArrayAdapter radiusArrayAdapter = (RadiusArrayAdapter) getViewDataBinding().toolbarView.getRadiusSpinnerAdapter();
                radiusArrayAdapter.setSelection(position);

                getEventListener().onChangedRadius(getSpinnerRadiusValue(position));
            }
        });
    }

    void setMenuBarLayoutTranslationY(float dy)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setTranslationY(dy - ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP)));

        if (mViewPager != null)
        {
            mViewPager.setTranslationY(dy);
        }
    }

    void resetMenuBarLayoutTranslation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setTranslationY(0);

        if (mViewPager != null)
        {
            mViewPager.setVisibility(View.INVISIBLE);
            mViewPager.setTranslationY(0);
        }
    }

    float getSpinnerRadiusValue(int spinnerPosition)
    {
        if (getViewDataBinding() == null)
        {
            return 0.0f;
        }

        float radius;

        switch (spinnerPosition)
        {
            case 3:
                radius = 10.0f;
                break;

            case 2:
                radius = 7.0f;
                break;

            case 1:
                radius = 5.0f;
                break;

            case 0:
                radius = 2.0f;
                break;

            default:
                radius = StayOutboundListPresenter.DEFAULT_RADIUS;
                break;
        }

        return radius;
    }

    private View getAreaView(int index, StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            return null;
        }

        DailySearchStayOutboundAreaCardView areaCardView = new DailySearchStayOutboundAreaCardView(getContext());

        areaCardView.setTitleText(stayOutboundSuggest.display);
        areaCardView.setSubTitleText(stayOutboundSuggest.country);
        areaCardView.setTag(stayOutboundSuggest);
        areaCardView.setOnClickListener(v -> getEventListener().onPopularAreaClick((StayOutboundSuggest) v.getTag()));

        return areaCardView;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private DailyOverScrollViewPager addMapViewPager(Context context, ViewGroup viewGroup)
    {
        if (context == null || viewGroup == null)
        {
            return null;
        }

        int paddingOther = ScreenUtils.dpToPx(context, VIEWPAGER_OTHER_PADDING_DP);
        int paddingTop = ScreenUtils.dpToPx(context, VIEWPAGER_TOP_PADDING_DP);

        DailyOverScrollViewPager viewPager = new DailyOverScrollViewPager(context);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(ScreenUtils.dpToPx(context, VIEWPAGER_PAGE_MARGIN_DP));
        viewPager.setPadding(paddingOther, paddingTop, paddingOther, paddingOther);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, VIEWPAGER_HEIGHT_DP));
        viewPager.setOnPageChangeListener(this);

        layoutParams.gravity = Gravity.BOTTOM;

        viewPager.setLayoutParams(layoutParams);
        viewPager.setVisibility(View.INVISIBLE);

        if (Util.isUsedMultiTransition() == true)
        {
            viewPager.setTransitionGroup(true);
        }

        viewGroup.addView(viewPager);

        return viewPager;
    }

    private class RadiusArrayAdapter extends ArrayAdapter<CharSequence>
    {
        private int mSelectedPosition;

        public RadiusArrayAdapter(Context context, int resourceId, CharSequence[] list)
        {
            super(context, resourceId, list);
        }

        public void setSelection(int position)
        {
            mSelectedPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            if (view != null)
            {
                TextView textView = (TextView) view;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                textView.setSelected(mSelectedPosition == position);

                if (mSelectedPosition == position)
                {
                    textView.setTextColor(getColor(R.color.default_text_cb70038));
                } else
                {
                    textView.setTextColor(getColor(R.color.default_text_c323232));
                }
            }

            return view;
        }
    }
}
