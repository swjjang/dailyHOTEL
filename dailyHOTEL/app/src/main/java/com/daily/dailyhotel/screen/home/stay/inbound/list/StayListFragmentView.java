package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseBlurFragmentView;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.list.map.StayMapFragment;
import com.daily.dailyhotel.screen.home.stay.inbound.list.map.StayMapViewPagerAdapter;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayMapCardView;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentStayListDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseBlurFragmentView<StayListFragmentInterface.OnEventListener, FragmentStayListDataBinding>//
    implements StayListFragmentInterface.ViewInterface
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 115;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    StayListAdapter mListAdapter;

    StayMapFragment mMapFragment;

    StayMapViewPagerAdapter mViewPagerAdapter;

    ValueAnimator mValueAnimator;

    DailyFloatingActionView mFloatingActionView;

    public StayListFragmentView(StayListFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {
        mFloatingActionView = getWindow().findViewById(R.id.floatingActionView);

        initListLayout(viewDataBinding);
        initMapLayout(viewDataBinding);
    }

    @Override
    public void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mListAdapter == null)
        {
            mListAdapter = new StayListAdapter(getContext(), null);
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

                    if (objectItem.mType == objectItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayCardView == true)
                        {
                            getEventListener().onStayClick(position, objectItem.getItem(), mListAdapter.getItemCount(), ((DailyStayCardView) view).getOptionsCompat(), StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
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

                    ObjectItem objectItem = mListAdapter.getItem(position);

                    if (objectItem.mType == objectItem.TYPE_ENTRY)
                    {
                        getEventListener().onStayLongClick(position, objectItem.getItem(), mListAdapter.getItemCount(), ((DailyStayCardView) view).getOptionsCompat());
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
        }

        mListAdapter.setDistanceEnabled(isSortByDistance);
        mListAdapter.setNightsEnabled(nightsEnabled);
        mListAdapter.setRewardEnabled(rewardEnabled);
        mListAdapter.setTrueVREnabled(supportTrueVR);
        mListAdapter.setAll(objectItemList);

        getViewDataBinding().recyclerView.setAdapter(mListAdapter);
    }

    @Override
    public void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mListAdapter == null)
        {
            mListAdapter = new StayListAdapter(getContext(), null);

            getViewDataBinding().recyclerView.setAdapter(mListAdapter);
        }

        // 항상 마지막 아이템은 Loading, Footer View 이다.
        int itemCount = mListAdapter.getItemCount();
        if (itemCount > 0)
        {
            mListAdapter.remove(itemCount - 1);
        }

        mListAdapter.setDistanceEnabled(isSortByDistance);
        mListAdapter.setNightsEnabled(nightsEnabled);
        mListAdapter.setRewardEnabled(rewardEnabled);
        mListAdapter.setTrueVREnabled(supportTrueVR);
        mListAdapter.addAll(objectItemList);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setStayMapViewPagerList(Context context, List<Stay> stayList, boolean nightsEnabled, boolean rewardEnabled)
    {
        if (context == null)
        {
            return;
        }

        if (mViewPagerAdapter == null)
        {
            mViewPagerAdapter = new StayMapViewPagerAdapter(context);
            mViewPagerAdapter.setOnPlaceMapViewPagerAdapterListener(new StayMapViewPagerAdapter.OnPlaceMapViewPagerAdapterListener()
            {
                @Override
                public void onStayClick(View view, Stay stay)
                {
                    if (view instanceof DailyStayMapCardView)
                    {
                        getEventListener().onStayClick(-1, stay, getViewDataBinding().mapViewPager.getChildCount(), ((DailyStayMapCardView) view).getOptionsCompat(), StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP);
                    }
                }

                @Override
                public void onCloseClick()
                {
                    getEventListener().onMapClick();
                }
            });
        }

        getViewDataBinding().mapViewPager.setAdapter(mViewPagerAdapter);

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(stayList);
        mViewPagerAdapter.setNightsEnabled(nightsEnabled);
        mViewPagerAdapter.setRewardEnabled(rewardEnabled);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMapViewPagerVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true && getViewDataBinding().mapViewPager.getVisibility() != View.VISIBLE)
        {
            showViewPagerAnimation();
        } else if (visible == false && getViewDataBinding().mapViewPager.getVisibility() == View.VISIBLE)
        {
            hideViewPagerAnimation();

            if (mMapFragment != null)
            {
                mMapFragment.hideSelectedMarker();
            }
        }
    }

    @Override
    public boolean isMapViewPagerVisible()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return getViewDataBinding().mapViewPager.getVisibility() == View.VISIBLE;
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
                getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_searchresult_stay_filter_empty_message01), getString(R.string.message_changing_filter_option));
                getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_hotel_list_changing_filter), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onFilterClick();
                    }
                });

                getViewDataBinding().emptyView.setButton02(false, null, null);
                getViewDataBinding().emptyView.setBottomMessageVisible(false);
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

                getViewDataBinding().emptyView.setBottomMessageVisible(true);
                getViewDataBinding().emptyView.setOnCallClickListener(v -> getEventListener().onCallClick());
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
    public void setMapLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().mapLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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

        if (mMapFragment == null)
        {
            mMapFragment = new StayMapFragment();
            mMapFragment.setOnEventListener(new StayMapFragment.OnEventListener()
            {
                @Override
                public void onMapReady()
                {
                    getEventListener().onMapReady();
                }

                @Override
                public void onMarkerClick(Stay stay, List<Stay> stayList)
                {
                    getEventListener().onMarkerClick(stay, stayList);
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

                }
            });
        }

        fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mMapFragment).commitAllowingStateLoss();

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
    }

    @Override
    public void hideMapLayout(FragmentManager fragmentManager)
    {
        if (getViewDataBinding() == null || fragmentManager == null || mMapFragment == null)
        {
            return;
        }

        if (mViewPagerAdapter != null)
        {
            mViewPagerAdapter.clear();
            mViewPagerAdapter = null;
        }

        getViewDataBinding().mapViewPager.removeAllViews();
        getViewDataBinding().mapViewPager.setAdapter(null);
        getViewDataBinding().mapViewPager.setVisibility(View.GONE);

        fragmentManager.beginTransaction().remove(mMapFragment).commitAllowingStateLoss();

        getViewDataBinding().mapLayout.removeAllViews();
        getViewDataBinding().mapLayout.setVisibility(View.GONE);

        mMapFragment = null;

        mFloatingActionView.setTranslationY(0);
        getViewDataBinding().mapViewPager.setTranslationY(0);
    }

    @Override
    public void setMapList(List<Stay> stayList, boolean moveCameraBounds, boolean clear, boolean hide)
    {
        if (getViewDataBinding() == null || mMapFragment == null)
        {
            return;
        }

        getViewDataBinding().mapLayout.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        mMapFragment.setList(stayList, moveCameraBounds, clear);

    }

    @Override
    public void setWish(int position, boolean wish)
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return;
        }

        if (mListAdapter.getItem(position).mType == ObjectItem.TYPE_ENTRY)
        {
            ((Stay) mListAdapter.getItem(position).getItem()).myWish = wish;
        }

        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                StayListAdapter.StayViewHolder stayViewHolder = (StayListAdapter.StayViewHolder) getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position);

                if (stayViewHolder != null)
                {
                    stayViewHolder.stayCardView.setWish(wish);
                }
            }
        });
    }

    @Override
    public void scrollTop()
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.scrollToPosition(0);
    }

    @Override
    public void scrollStop()
    {
        if (getViewDataBinding() == null || mListAdapter == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.smoothScrollBy(0, 1);
    }

    @Override
    public Observable<Long> getLocationAnimation()
    {
        if (getViewDataBinding() == null || mMapFragment == null)
        {
            return null;
        }

        return mMapFragment.getLocationAnimation();
    }

    @Override
    public void setMyLocation(Location location)
    {
        if (getViewDataBinding() == null || mMapFragment == null)
        {
            return;
        }

        mMapFragment.setMyLocation(new LatLng(location.getLatitude(), location.getLongitude()), true);
    }

    @Override
    public void setFloatingActionViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null || mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setFloatingActionViewTypeMapEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null || mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setViewOptionMapEnabled(enabled);
    }

    private void initListLayout(FragmentStayListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

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
                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_IDLE:
                    {
                        if (recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() >= recyclerView.computeVerticalScrollRange())
                        {
                            StayListAdapter listAdapter = (StayListAdapter) recyclerView.getAdapter();

                            if (listAdapter != null)
                            {
                                int count = listAdapter.getItemCount();

                                if (count > 0)
                                {
                                    ObjectItem objectItem = listAdapter.getItem(listAdapter.getItemCount() - 1);

                                    if (objectItem != null && objectItem.mType == objectItem.TYPE_FOOTER_VIEW)
                                    {
                                        getEventListener().showActionBar();
                                    }
                                }
                            }
                        }
                        break;
                    }

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));
    }

    private void initMapLayout(FragmentStayListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.mapViewPager.setOffscreenPageLimit(2);
        viewDataBinding.mapViewPager.setClipToPadding(false);
        viewDataBinding.mapViewPager.setPageMargin(ScreenUtils.dpToPx(getContext(), VIEWPAGER_PAGE_MARGIN_DP));
        viewDataBinding.mapViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewDataBinding.mapViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                if (mViewPagerAdapter == null || mViewPagerAdapter.getCount() <= position)
                {
                    return;
                }

                Stay stay = mViewPagerAdapter.getItem(position);

                if (stay != null)
                {
                    mMapFragment.setSelectedMarker(stay);
                }
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

        viewDataBinding.mapViewPager.setVisibility(View.GONE);
    }

    private void showViewPagerAnimation()
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (getViewDataBinding().mapViewPager.getVisibility() == View.VISIBLE)
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
                int height = ScreenUtils.dpToPx(getContext(), VIEWPAGER_HEIGHT_DP);
                float translationY = height - height * value / 100;

                mFloatingActionView.setTranslationY(translationY - ScreenUtils.dpToPx(getContext(), VIEWPAGER_HEIGHT_DP));
                getViewDataBinding().mapViewPager.setTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mFloatingActionView.setTranslationY(0);
                getViewDataBinding().mapViewPager.setTranslationY(ScreenUtils.dpToPx(getContext(), VIEWPAGER_HEIGHT_DP));

                getViewDataBinding().mapViewPager.setVisibility(View.VISIBLE);
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

        if (getViewDataBinding().mapViewPager.getVisibility() != View.VISIBLE)
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
                int height = ScreenUtils.dpToPx(getContext(), VIEWPAGER_HEIGHT_DP);
                float translationY = height * value / 100;

                mFloatingActionView.setTranslationY(translationY - ScreenUtils.dpToPx(getContext(), VIEWPAGER_HEIGHT_DP));
                getViewDataBinding().mapViewPager.setTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                //                mFloatingActionView.setTranslationY(0);
                //                getViewDataBinding().mapViewPager.setTranslationY(0);

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

                getViewDataBinding().mapViewPager.setVisibility(View.GONE);

                mFloatingActionView.setTranslationY(0);
                getViewDataBinding().mapViewPager.setTranslationY(0);
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
}
