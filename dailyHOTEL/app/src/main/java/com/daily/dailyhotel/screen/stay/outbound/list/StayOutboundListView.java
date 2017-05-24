package com.daily.dailyhotel.screen.stay.outbound.list;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.screen.stay.outbound.list.map.StayOutboundMapFragment;
import com.daily.dailyhotel.screen.stay.outbound.list.map.StayOutboundMapViewPagerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchResultDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundListView extends BaseView<StayOutboundListView.OnEventListener, ActivityStayOutboundSearchResultDataBinding>//
    implements StayOutboundListViewInterface, ViewPager.OnPageChangeListener, View.OnClickListener, StayOutboundMapFragment.OnEventListener
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 120;
    private static final int VIEWPAGER_TOP_N_BOTTOM_PADDING_DP = 10;
    private static final int VIEWPAGER_LEFT_N_RIGHT_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    private DailyToolbarLayout mDailyToolbarLayout;
    private StayOutboundListAdapter mStayOutboundListAdapter;

    private StayOutboundMapFragment mStayOutboundMapFragment;
    private DailyOverScrollViewPager mViewPager;
    private StayOutboundMapViewPagerAdapter mViewPagerAdapter;

    private ValueAnimator mValueAnimator;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefreshAll(boolean showProgress);

        void onCalendarClick();

        void onPeopleClick();

        void onFilterClick();

        void onViewTypeClick();

        void onStayClick(View view, StayOutbound stayOutbound);

        void onStayLongClick();

        void onScrollList(int listSize, int lastVisibleItemPosition);

        void onViewPagerClose();

        // Map Event
        void onMapReady();

        void onMarkerClick(StayOutbound stayOutbound);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();
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

        viewDataBinding.calendarTextView.setOnClickListener(this);
        viewDataBinding.peopleTextView.setOnClickListener(this);
        viewDataBinding.swipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getEventListener().onRefreshAll(false);
            }
        });

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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

        viewDataBinding.viewTypeOptionImageView.setOnClickListener(this);
        viewDataBinding.filterOptionImageView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }

    @Override
    public void setCalendarText(String calendarText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().calendarTextView.setText(calendarText);
    }

    @Override
    public void setPeopleText(String peopleText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().peopleTextView.setText(peopleText);
    }

    @Override
    public void setStayOutboundList(List<ListItem> listItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRefreshing(false);

        if (listItemList.size() == 0)
        {
            getViewDataBinding().emptyLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().resultLayout.setVisibility(View.GONE);
            return;
        }

        if (mStayOutboundListAdapter == null)
        {
            mStayOutboundListAdapter = new StayOutboundListAdapter(getContext(), null);
            mStayOutboundListAdapter.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return;
                    }

                    ListItem listItem = mStayOutboundListAdapter.getItem(position);

                    if (listItem.mType == listItem.TYPE_ENTRY)
                    {
                        getEventListener().onStayClick(view, listItem.getItem());
                    }
                }
            }, new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    return false;
                }
            });
        }

        getViewDataBinding().recyclerView.setAdapter(mStayOutboundListAdapter);

        getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        getViewDataBinding().resultLayout.setVisibility(View.VISIBLE);

        mStayOutboundListAdapter.setAll(listItemList);
        mStayOutboundListAdapter.notifyDataSetChanged();
    }

    @Override
    public void addStayOutboundList(List<ListItem> listItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRefreshing(false);

        if (mStayOutboundListAdapter == null || listItemList == null || listItemList.size() == 0)
        {
            return;
        }

        getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        getViewDataBinding().resultLayout.setVisibility(View.VISIBLE);

        mStayOutboundListAdapter.addAll(listItemList);
        mStayOutboundListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setStayOutboundMakeMarker(List<StayOutbound> stayOutboundList)
    {
        if (mStayOutboundMapFragment == null || stayOutboundList == null)
        {
            return;
        }

        mStayOutboundMapFragment.setStayOutboundList(stayOutboundList);
    }

    @Override
    public void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList)
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

                }

                @Override
                public void onCloseClick()
                {

                }
            });
        }

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(stayOutboundList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getMapLayoutResourceId()
    {
        return 0;
    }

    @Override
    public void setViewTypeOptionLayout(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (enabled == true)
        {
            getViewDataBinding().viewTypeOptionImageView.getBackground().setAlpha(255);
        } else
        {
            getViewDataBinding().viewTypeOptionImageView.getBackground().setAlpha(102);
        }

        getViewDataBinding().viewTypeOptionImageView.setEnabled(enabled);
    }

    @Override
    public void setFilterOptionLayout(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (enabled == true)
        {
            getViewDataBinding().filterOptionImageView.getBackground().setAlpha(255);
        } else
        {
            getViewDataBinding().filterOptionImageView.getBackground().setAlpha(102);
        }

        getViewDataBinding().filterOptionImageView.setEnabled(enabled);
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
                getViewDataBinding().viewTypeOptionImageView.setBackgroundResource(R.drawable.fab_01_map);
                break;

            case MAP:
                getViewDataBinding().viewTypeOptionImageView.setBackgroundResource(R.drawable.fab_02_list);
                break;
        }
    }

    @Override
    public void setFilterOptionImage(boolean onOff)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().filterOptionImageView.setSelected(onOff);
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

        getViewDataBinding().mapLayout.setVisibility(View.VISIBLE);

        if (mStayOutboundMapFragment == null)
        {
            mStayOutboundMapFragment = new StayOutboundMapFragment();
            mStayOutboundMapFragment.setOnEventListener(this);
        }

        fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mStayOutboundMapFragment, "MAP").commitAllowingStateLoss();

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

            case R.id.viewTypeOptionImageView:
                getEventListener().onViewTypeClick();
                break;


            case R.id.filterOptionImageView:
                getEventListener().onFilterClick();
                break;
        }
    }

    @Override
    public void onMapReady()
    {
        getEventListener().onMapReady();
    }

    @Override
    public void onMarkerClick(StayOutbound stayOutbound)
    {
        getEventListener().onMarkerClick(stayOutbound);
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
                int height = ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_TOP_N_BOTTOM_PADDING_DP));
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
                mViewPager.setTranslationY(ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_TOP_N_BOTTOM_PADDING_DP)));
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllListeners();
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator = null;
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
                int height = ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_TOP_N_BOTTOM_PADDING_DP));
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
                mValueAnimator.removeAllListeners();
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator = null;

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

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(null//
            , v -> getEventListener().onBackClick());
    }

    public void setMenuBarLayoutTranslationY(float dy)
    {
        if (getViewDataBinding() == null || mViewPager == null)
        {
            return;
        }

        getViewDataBinding().bottomOptionLayout.setTranslationY(dy - ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_TOP_N_BOTTOM_PADDING_DP)));
        mViewPager.setTranslationY(dy);
    }

    public void resetMenuBarLayoutTranslation()
    {
        if (getViewDataBinding() == null || mViewPager == null)
        {
            return;
        }

        getViewDataBinding().bottomOptionLayout.setTranslationY(0);

        mViewPager.setVisibility(View.INVISIBLE);
        mViewPager.setTranslationY(0);
    }

    private DailyOverScrollViewPager addMapViewPager(Context context, ViewGroup viewGroup)
    {
        if (context == null || viewGroup == null)
        {
            return null;
        }

        int paddingLeftRight = ScreenUtils.dpToPx(context, VIEWPAGER_LEFT_N_RIGHT_PADDING_DP);
        int paddingTopBottom = ScreenUtils.dpToPx(context, VIEWPAGER_TOP_N_BOTTOM_PADDING_DP);

        DailyOverScrollViewPager viewPager = new DailyOverScrollViewPager(context);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(ScreenUtils.dpToPx(context, VIEWPAGER_PAGE_MARGIN_DP));
        viewPager.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, VIEWPAGER_HEIGHT_DP));
        viewPager.setOnPageChangeListener(this);

        layoutParams.gravity = Gravity.BOTTOM;

        viewPager.setLayoutParams(layoutParams);
        viewPager.setVisibility(View.INVISIBLE);

        viewGroup.addView(viewPager);

        return viewPager;
    }
}
