package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseBlurFragmentView;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.screen.home.stay.inbound.list.map.StayMapFragment;
import com.daily.dailyhotel.screen.home.stay.inbound.list.map.StayMapViewPagerAdapter;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentStayListDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseBlurFragmentView<StayListFragmentView.OnEventListener, FragmentStayListDataBinding>//
    implements StayListFragmentInterface
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 125;
    private static final int VIEWPAGER_TOP_PADDING_DP = 10;
    private static final int VIEWPAGER_OTHER_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    private StayListFragmentAdapter mStayListFragmentAdapter;

    private StayMapFragment mStayMapFragment;

    private StayMapViewPagerAdapter mViewPagerAdapter;

    DailyOverScrollViewPager mViewPager;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSwipeRefreshing();

        void onMoreRefreshing();

        void onStayClick(int position, android.support.v4.util.Pair[] pairs, Stay stay, int listCount);

        void onStayLongClick(int position, android.support.v4.util.Pair[] pairs, Stay stay, int listCount);

        void onViewPagerClose();

        // Map Event
        void onMapReady();

        void onMarkerClick(Stay stay);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void onRetryClick();

        void onResearchClick();

        void onCallClick();

        void onFilterClick();

        void onRegionClick();

        void onCalendarClick();

        void onWishClick(int position, Stay stay);
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {
        viewDataBinding.swipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getEventListener().onSwipeRefreshing();
            }
        });

        viewDataBinding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (getViewDataBinding().swipeRefreshLayout.isRefreshing() == true)
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

                    if (lastVisibleItemPosition > itemCount * 2 / 3)
                    {
                        getEventListener().onMoreRefreshing();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));
    }

    @Override
    public void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mStayListFragmentAdapter == null)
        {
            mStayListFragmentAdapter = new StayListFragmentAdapter(getContext(), null);
            mStayListFragmentAdapter.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return;
                    }

                    ObjectItem objectItem = mStayListFragmentAdapter.getItem(position);

                    if (objectItem.mType == objectItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayCardView == true)
                        {
                            getEventListener().onStayClick(((DailyStayCardView) view).getOptionsCompat(), objectItem.getItem(), mStayListFragmentAdapter.getItemCount());
                        } else
                        {

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

                    ObjectItem objectItem = mStayListFragmentAdapter.getItem(position);

                    if (objectItem.mType == objectItem.TYPE_ENTRY)
                    {
                        getEventListener().onStayLongClick(position, ((DailyStayCardView) view).getOptionsCompat(), objectItem.getItem(), mStayListFragmentAdapter.getItemCount());
                    }

                    return true;
                }
            });

            mStayListFragmentAdapter.setOnWishClickListener(new View.OnClickListener()
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

                    ObjectItem objectItem = mStayListFragmentAdapter.getItem(position);

                    if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        getEventListener().onWishClick(position, objectItem.getItem());
                    }
                }
            });

            getViewDataBinding().recyclerView.setAdapter(mStayListFragmentAdapter);
        }

        mStayListFragmentAdapter.setDistanceEnabled(isSortByDistance);
        mStayListFragmentAdapter.setNightsEnabled(isNights);
        mStayListFragmentAdapter.setRewardEnabled(rewardEnabled);
        mStayListFragmentAdapter.setTrueVREnabled(supportTrueVR);
        mStayListFragmentAdapter.setAll(objectItemList);
        mStayListFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mStayListFragmentAdapter == null)
        {
            mStayListFragmentAdapter = new StayListFragmentAdapter(getContext(), null);

            getViewDataBinding().recyclerView.setAdapter(mStayListFragmentAdapter);
        }

        // 항상 마지막 아이템은 Loading, Footer View 이다.
        int itemCount = mStayListFragmentAdapter.getItemCount();
        if (itemCount > 0)
        {
            mStayListFragmentAdapter.remove(itemCount - 1);
        }

        mStayListFragmentAdapter.setDistanceEnabled(isSortByDistance);
        mStayListFragmentAdapter.setNightsEnabled(isNights);
        mStayListFragmentAdapter.setRewardEnabled(rewardEnabled);
        mStayListFragmentAdapter.setTrueVREnabled(supportTrueVR);
        mStayListFragmentAdapter.addAll(objectItemList);
        mStayListFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSwipeRefreshing(boolean refreshing)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setEmptyViewVisible(boolean visible, boolean applyFilter)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == false)
        {
            getViewDataBinding().emptyView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().swipeRefreshLayout.setVisibility(View.INVISIBLE);
            getViewDataBinding().mapLayout.setVisibility(View.GONE);
            getViewDataBinding().emptyView.setVisibility(View.VISIBLE);

            if (applyFilter == true)
            {
                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_not_exist_filters), getString(R.string.message_changing_filter_option));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_hotel_list_changing_filter), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onFilterClick();
                    }
                });

                getViewDataBinding().emptyView.setButton02(false, null, null);
                getViewDataBinding().emptyView.setBottomMessage(false);
            } else
            {
                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_stay_empty_message01), getString(R.string.message_stay_empty_message02));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_stay_category_change_region), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onRegionClick();
                    }
                });

                getViewDataBinding().emptyView.setButton02(true, getString(R.string.label_stay_category_change_date), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onCalendarClick();
                    }
                });
                getViewDataBinding().emptyView.setBottomMessage(false);
            }
        }
    }

    @Override
    public void setListLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showMapLayout(FragmentManager fragmentManager, boolean hide)
    {
        if (getViewDataBinding() == null || fragmentManager == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setVisibility(View.INVISIBLE);
        getViewDataBinding().mapLayout.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        if (mStayMapFragment == null)
        {
            mStayMapFragment = new StayMapFragment();
            mStayMapFragment.setOnEventListener(new StayMapFragment.OnEventListener()
            {
                @Override
                public void onMapReady()
                {
                    getEventListener().onMapReady();
                }

                @Override
                public void onMarkerClick(Stay stay, List<Stay> stayList)
                {

                }

                @Override
                public void onMarkersCompleted()
                {

                }

                @Override
                public void onMapClick()
                {

                }

                @Override
                public void onMyLocationClick()
                {

                }

                @Override
                public void onChangedLocation(LatLng latLng, float radius, float zoom)
                {

                }
            });
        }

        fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mStayMapFragment).commit();

        //        getViewDataBinding().mapLayout.setOnTouchListener(new View.OnTouchListener()
        //        {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event)
        //            {
        //                switch (event.getAction())
        //                {
        //                    case MotionEvent.ACTION_DOWN:
        //                        mPossibleLoadingListByMap = false;
        //
        //                        getEventListener().onClearChangedLocation();
        //                        break;
        //
        //                    case MotionEvent.ACTION_UP:
        //                        mPossibleLoadingListByMap = true;
        //                        break;
        //                }
        //
        //                return false;
        //            }
        //        });

        mViewPager = addMapViewPager(getContext(), getViewDataBinding().mapLayout);
    }

    @Override
    public void hideMapLayout(FragmentManager fragmentManager)
    {
        if (getViewDataBinding() == null || fragmentManager == null || mStayMapFragment == null)
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

        fragmentManager.beginTransaction().remove(mStayMapFragment).commitAllowingStateLoss();

        getViewDataBinding().mapLayout.removeAllViews();
        getViewDataBinding().mapLayout.setVisibility(View.GONE);

        //        setMapProgressBarVisible(false);

        mStayMapFragment = null;

        //        resetMenuBarLayoutTranslation();
    }

    @Override
    public void setMapList(List<Stay> stayList, boolean moveCameraBounds, boolean clear, boolean hide)
    {
        if (getViewDataBinding() == null || mStayMapFragment == null)
        {
            return;
        }

        getViewDataBinding().mapLayout.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        mStayMapFragment.setStayList(stayList, moveCameraBounds, clear);

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
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        layoutParams.gravity = Gravity.BOTTOM;

        viewPager.setLayoutParams(layoutParams);
        viewPager.setVisibility(View.INVISIBLE);

        viewGroup.addView(viewPager);

        return viewPager;
    }
}
