package com.daily.dailyhotel.screen.booking.detail.map;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.databinding.ActivityPlaceBookingDetailMapDataBinding;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public abstract class PlaceBookingDetailMapView extends BaseDialogView<PlaceBookingDetailMapView.OnEventListener, ActivityPlaceBookingDetailMapDataBinding> //
    implements PlaceBookingDetailMapInterface, ViewPager.OnPageChangeListener, PlaceBookingDetailMapFragment.OnEventListener
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 120;
    private static final int VIEWPAGER_TOP_N_BOTTOM_PADDING_DP = 10;
    private static final int VIEWPAGER_LEFT_N_RIGHT_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    private DailyToolbarLayout mDailyToolbarLayout;
    private PlaceBookingDetailMapFragment mPlaceBookingDetailMapFragment;
    private DailyOverScrollViewPager mViewPager;
    private PlaceBookingDetailMapViewPagerAdapter mViewPagerAdapter;

    private ValueAnimator mValueAnimator;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onPlaceClick(View view, Place place);

        // Map Event
        void onMapReady();

        void onMarkerClick(Place place);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();
    }

    public abstract PlaceBookingDetailMapViewPagerAdapter getViewPagerAdapter(Context context);

    public PlaceBookingDetailMapView(BaseActivity baseActivity, PlaceBookingDetailMapView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(ActivityPlaceBookingDetailMapDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
    }

    private void initToolbar(ActivityPlaceBookingDetailMapDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        mDailyToolbarLayout.initToolbar(null//
            , v -> getEventListener().onBackClick());
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
    public void initMapLayout(FragmentManager fragmentManager)
    {
        if (getViewDataBinding() == null || fragmentManager == null)
        {
            return;
        }

        getViewDataBinding().mapLayout.setVisibility(View.VISIBLE);

        if (mPlaceBookingDetailMapFragment == null)
        {
            mPlaceBookingDetailMapFragment = new PlaceBookingDetailMapFragment();
            mPlaceBookingDetailMapFragment.setOnEventListener(this);
        }

        fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mPlaceBookingDetailMapFragment, "MAP").commitAllowingStateLoss();

        mViewPager = addMapViewPager(getContext(), getViewDataBinding().mapLayout);

        initViewPagerAdapter(getContext());
    }

    private void initViewPagerAdapter(Context context)
    {
        if (context == null || mViewPagerAdapter != null)
        {
            return;
        }

        mViewPagerAdapter = getViewPagerAdapter(context);
        mViewPagerAdapter.setOnPlaceMapViewPagerAdapterListener(new PlaceBookingDetailMapViewPagerAdapter.OnPlaceMapViewPagerAdapterListener()
        {
            @Override
            public void onPlaceClick(View view, Place place)
            {
                getEventListener().onPlaceClick(view, place);
            }

            @Override
            public void onCloseClick()
            {
                onMapClick();
            }
        });
    }

    @Override
    public void setPlaceList(ArrayList<Place> placeList, PlaceBookingDay placeBookingDay, Location placeLocation, String placeName)
    {
        if (mPlaceBookingDetailMapFragment == null || mViewPager == null || mViewPagerAdapter == null)
        {
            return;
        }

        mPlaceBookingDetailMapFragment.setPlaceList(placeList, placeLocation, placeName);
    }

    @Override
    public void setPlaceMarker(double lat, double lng, String placeName)
    {
        if (mPlaceBookingDetailMapFragment == null)
        {
            return;
        }

        mPlaceBookingDetailMapFragment.setPlaceMarker(lat, lng, placeName);
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// PlaceDetailMapFragment.OnEventListener - Start //////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady()
    {
        getEventListener().onMapReady();
    }

    @Override
    public void onMarkerClick(Place place)
    {
        getEventListener().onMarkerClick(place);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////// PlaceDetailMapFragment.OnEventListener - End ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// ViewPager.OnPageChangeListener - Start ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

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

        Place place = mViewPagerAdapter.getItem(position);

        if (place != null)
        {
            mPlaceBookingDetailMapFragment.setSelectedMarker(place);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// ViewPager.OnPageChangeListener - End ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlaceMapViewPagerList(Context context, ArrayList<Place> placeList)
    {
        if (context == null)
        {
            return;
        }

        initViewPagerAdapter(context);

        mViewPagerAdapter.clear();
        mViewPagerAdapter.setData(placeList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMapViewPagerVisibility(boolean isVisible)
    {
        if (mViewPager == null)
        {
            return;
        }

        mViewPager.bringToFront();

        if (isVisible == true)
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

                if (mPlaceBookingDetailMapFragment != null)
                {
                    mPlaceBookingDetailMapFragment.hideSelectedMarker();
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
    public Observable<Long> getLocationAnimation()
    {
        if (mPlaceBookingDetailMapFragment == null)
        {
            return null;
        }

        return mPlaceBookingDetailMapFragment.getLocationAnimation();
    }

    @Override
    public void setMyLocation(Location location)
    {
        if (mPlaceBookingDetailMapFragment == null || location == null)
        {
            return;
        }

        mPlaceBookingDetailMapFragment.setMyLocation(new LatLng(location.getLatitude(), location.getLongitude()), true);
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

                if (mViewPager != null)
                {
                    mViewPager.setTranslationY(translationY);
                }
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

                if (mViewPager != null)
                {
                    mViewPager.setTranslationY(translationY);
                }
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                if (mViewPager != null)
                {
                    mViewPager.setTranslationY(0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllListeners();
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator = null;

                mViewPager.setVisibility(View.INVISIBLE);

                if (mViewPager != null)
                {
                    mViewPager.setTranslationY(0);
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
}
