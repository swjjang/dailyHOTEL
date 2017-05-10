package com.daily.dailyhotel.screen.stay.outbound.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
    implements StayOutboundListViewInterface, ViewPager.OnPageChangeListener
{
    private static final int VIEWPAGER_HEIGHT_DP = 120;
    private static final int VIEWPAGER_TOP_N_BOTTOM_PADDING_DP = 10;
    private static final int VIEWPAGER_LEFT_N_RIGHT_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    private DailyToolbarLayout mDailyToolbarLayout;
    private StayOutboundListAdapter mStayOutboundListAdapter;

    private StayOutboundMapFragment mStayOutboundMapFragment;
    private DailyOverScrollViewPager mViewPager;
    private StayOutboundMapViewPagerAdapter mViewPagerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onFilterClick();

        void onMapClick();

        void onListClick();

        void onStayClick();

        void onStayLongClick();

        void onScrollList(int listSize, int lastVisibleItemPosition);

        void onMapReady();
    }

    public StayOutboundListView(BaseActivity baseActivity, StayOutboundListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

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
    public void setStayOutboundList(List<ListItem> listItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (listItemList.size() == 0)
        {
            getViewDataBinding().emptyLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().resultLayout.setVisibility(View.GONE);
            return;
        }

        if (mStayOutboundListAdapter == null)
        {
            mStayOutboundListAdapter = new StayOutboundListAdapter(getContext(), null);
            getViewDataBinding().recyclerView.setAdapter(mStayOutboundListAdapter);
        }

        getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        getViewDataBinding().resultLayout.setVisibility(View.VISIBLE);

        mStayOutboundListAdapter.setAll(listItemList);
        mStayOutboundListAdapter.notifyDataSetChanged();
    }

    @Override
    public void addStayOutboundList(List<ListItem> listItemList)
    {

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

            mViewPager.setAdapter(mViewPagerAdapter);
        }

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(stayOutboundList);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getMapLayoutResourceId()
    {
        return 0;
    }

    @Override
    public void removeAllMapLayout()
    {

    }

    @Override
    public void setMapOptionLayout(boolean enabled)
    {

    }

    @Override
    public void setFilterOptonLayout(boolean enabled)
    {

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

        if (mStayOutboundMapFragment == null)
        {
            mStayOutboundMapFragment = new StayOutboundMapFragment();
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
            mStayOutboundMapFragment.setSelectedMarker(stayOutbound.latitude, stayOutbound.longitude);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    private void initToolbar(ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        mDailyToolbarLayout.initToolbar(null//
            , v -> getEventListener().onBackClick());
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
