package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.screen.home.stay.outbound.list.map.StayOutboundMapFragment;
import com.daily.dailyhotel.screen.home.stay.outbound.list.map.StayOutboundMapViewPagerAdapter;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchResultDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;

import java.util.List;

import io.reactivex.Observable;

public class StayOutboundListView extends BaseBlurView<StayOutboundListView.OnEventListener, ActivityStayOutboundSearchResultDataBinding>//
    implements StayOutboundListViewInterface, ViewPager.OnPageChangeListener, View.OnClickListener, StayOutboundMapFragment.OnEventListener
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 120;
    private static final int VIEWPAGER_TOP_N_BOTTOM_PADDING_DP = 10;
    private static final int VIEWPAGER_LEFT_N_RIGHT_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

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

        void onStayClick(android.support.v4.util.Pair[] pairs, StayOutbound stayOutbound);

        void onStayLongClick(android.support.v4.util.Pair[] pairs, StayOutbound stayOutbound);

        void onScrollList(int listSize, int lastVisibleItemPosition);

        void onViewPagerClose();

        // Map Event
        void onMapReady();

        void onMarkerClick(StayOutbound stayOutbound);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void retryClick();

        void researchClick();

        void onCallClick();
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

        viewDataBinding.calendarTextView.setDrawableVectorTint(R.color.default_background_c454545);
        viewDataBinding.calendarTextView.setOnClickListener(this);

        viewDataBinding.peopleTextView.setDrawableVectorTint(R.color.default_background_c454545);
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
        viewDataBinding.researchView.setOnClickListener(this);
        viewDataBinding.filterView.setOnClickListener(this);
        viewDataBinding.retryTextView.setOnClickListener(this);

        viewDataBinding.callTextView.setPaintFlags(viewDataBinding.callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewDataBinding.callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCallClick();
            }
        });

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.location_progressbar_cc8c8c8), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setCalendarText(String calendarText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().calendarTextView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int viewWidth = getViewDataBinding().calendarTextView.getWidth()//
                    - (getViewDataBinding().calendarTextView.getCompoundDrawablePadding() * 2)//
                    - getViewDataBinding().calendarTextView.getCompoundDrawables()[0].getIntrinsicWidth()//
                    - getViewDataBinding().calendarTextView.getCompoundDrawables()[2].getIntrinsicWidth()//
                    - getViewDataBinding().calendarTextView.getPaddingLeft()//
                    - getViewDataBinding().calendarTextView.getPaddingRight();

                final float width = DailyTextUtils.getTextWidth(getContext(), calendarText, 13d, getViewDataBinding().calendarTextView.getTypeface());

                if (viewWidth > width)
                {
                    getViewDataBinding().calendarTextView.setText(calendarText);
                } else
                {
                    float scaleX = 1f;
                    float scaleWidth;

                    for (int i = 99; i >= 60; i--)
                    {
                        scaleX = (float) i / 100;
                        scaleWidth = DailyTextUtils.getScaleTextWidth(getContext(), calendarText, 13d, scaleX, getViewDataBinding().calendarTextView.getTypeface());

                        if (viewWidth > scaleWidth)
                        {
                            break;
                        }
                    }

                    SpannableString spannableString = new SpannableString(calendarText);
                    spannableString.setSpan(new ScaleXSpan(scaleX), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    getViewDataBinding().calendarTextView.setText(spannableString);
                }
            }
        });
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
    public void setStayOutboundList(List<ListItem> listItemList, boolean isSortByDistance, boolean isNights)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRefreshing(false);

        if (listItemList.size() == 0)
        {
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
                        if (view instanceof DailyStayOutboundCardView == true)
                        {
                            getEventListener().onStayClick(((DailyStayOutboundCardView) view).getOptionsCompat(), listItem.getItem());
                        } else
                        {
                            View simpleDraweeView = view.findViewById(R.id.imageView);
                            View nameTextView = view.findViewById(R.id.nameTextView);
                            View gradientTopView = view.findViewById(R.id.gradientTopView);
                            View gradientBottomView = view.findViewById(R.id.gradientView);

                            android.support.v4.util.Pair[] pairs = new Pair[4];
                            pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                            pairs[1] = android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name));
                            pairs[2] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                            pairs[3] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                            getEventListener().onStayClick(pairs, listItem.getItem());
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

                    ListItem listItem = mStayOutboundListAdapter.getItem(position);

                    if (listItem.mType == listItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayOutboundCardView == true)
                        {
                            getEventListener().onStayClick(((DailyStayOutboundCardView) view).getOptionsCompat(), listItem.getItem());
                        } else
                        {
                            View simpleDraweeView = view.findViewById(R.id.imageView);
                            View nameTextView = view.findViewById(R.id.nameTextView);
                            View gradientTopView = view.findViewById(R.id.gradientTopView);
                            View gradientBottomView = view.findViewById(R.id.gradientView);

                            android.support.v4.util.Pair[] pairs = new Pair[4];
                            pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                            pairs[1] = android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name));
                            pairs[2] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                            pairs[3] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                            getEventListener().onStayLongClick(pairs, listItem.getItem());
                        }
                    }

                    return true;
                }
            });
        }

        getViewDataBinding().recyclerView.setAdapter(mStayOutboundListAdapter);

        mStayOutboundListAdapter.setAll(listItemList);
        mStayOutboundListAdapter.setDistanceEnabled(isSortByDistance);
        mStayOutboundListAdapter.setNightsEnabled(isNights);
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

        setEmptyScreenVisible(false);
        setErrorScreenVisible(false);
        setSearchLocationScreenVisible(false);
        setListScreenVisible(true);

        mStayOutboundListAdapter.remove(mStayOutboundListAdapter.getItemCount() - 1);
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
    public void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList, boolean isNights)
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

                    android.support.v4.util.Pair[] pairs = new Pair[4];
                    pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
                    pairs[1] = android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name));
                    pairs[2] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
                    pairs[3] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

                    getEventListener().onStayClick(pairs, stayOutbound);
                }

                @Override
                public void onCloseClick()
                {
                    onMapClick();
                }
            });
        }

        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(stayOutboundList);
        mViewPagerAdapter.setNightsEnabled(isNights);
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

        getViewDataBinding().swipeRefreshLayout.setVisibility(View.INVISIBLE);
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
        getViewDataBinding().swipeRefreshLayout.setVisibility(View.VISIBLE);

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
    public void setEmptyScreenVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
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
    public void setEmptyScreenType(EmptyScreenType emptyScreenType)
    {
        if (getViewDataBinding() == null || emptyScreenType == null)
        {
            return;
        }

        switch (emptyScreenType)
        {
            case FILTER_ON:
            {
                getViewDataBinding().messageTextView02.setText(R.string.label_stay_outbound_research_filter_on);
                getViewDataBinding().filterView.setVisibility(View.VISIBLE);
                getViewDataBinding().callLayout.setVisibility(View.INVISIBLE);
                break;
            }

            case DEFAULT:
            {
                getViewDataBinding().messageTextView02.setText(R.string.label_searchresult_text01);
                getViewDataBinding().filterView.setVisibility(View.GONE);
                getViewDataBinding().callLayout.setVisibility(View.VISIBLE);
                break;
            }
        }
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
            case FILTER_ON:
            {
                getViewDataBinding().viewTypeOptionImageView.setVisibility(View.GONE);
                getViewDataBinding().filterOptionImageView.setVisibility(View.VISIBLE);
                break;
            }

            case DEFAULT:
            {
                getViewDataBinding().viewTypeOptionImageView.setVisibility(View.VISIBLE);
                getViewDataBinding().filterOptionImageView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void setBottomLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().bottomOptionLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
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

            case R.id.viewTypeOptionImageView:
                getEventListener().onViewTypeClick();
                break;


            case R.id.filterOptionImageView:
            case R.id.filterView:
                getEventListener().onFilterClick();
                break;

            case R.id.researchView:
                getEventListener().researchClick();
                break;

            case R.id.retryTextView:
                getEventListener().retryClick();
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

        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    public void setMenuBarLayoutTranslationY(float dy)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().bottomOptionLayout.setTranslationY(dy - ScreenUtils.dpToPx(getContext(), (VIEWPAGER_HEIGHT_DP - VIEWPAGER_TOP_N_BOTTOM_PADDING_DP)));

        if (mViewPager != null)
        {
            mViewPager.setTranslationY(dy);
        }
    }

    public void resetMenuBarLayoutTranslation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().bottomOptionLayout.setTranslationY(0);

        if (mViewPager != null)
        {
            mViewPager.setVisibility(View.INVISIBLE);
            mViewPager.setTranslationY(0);
        }
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
