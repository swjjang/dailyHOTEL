package com.daily.dailyhotel.screen.booking.detail.map;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityPlaceBookingDetailMapDataBinding;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListMapFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayListMapFragment;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class PlaceBookingDetailMapView extends BaseDialogView<PlaceBookingDetailMapView.OnEventListener, ActivityPlaceBookingDetailMapDataBinding> //
    implements PlaceBookingDetailMapInterface
{
    private static final int VIEWPAGER_TOP_N_BOTTOM_PADDING_DP = 10;
    private static final int VIEWPAGER_LEFT_N_RIGHT_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    private DailyToolbarLayout mDailyToolbarLayout;
    private PlaceListMapFragment mPlaceMapFragment;
//    private DailyOverScrollViewPager mViewPager;
//    private PlaceMapViewPagerAdapter mViewPagerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onStayClick(View view, PlaceViewItem placeViewItem);

        // Map Event
        void onMapReady();

        void onMarkerClick(Place place);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();
    }

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
    public void setPlaceList(FragmentManager fragmentManager, List<PlaceViewItem> placeViewItemList, PlaceBookingDay placeBookingDay)
    {
        if (getViewDataBinding() == null || fragmentManager == null)
        {
            return;
        }

        if (placeViewItemList == null || placeViewItemList.size() == 0)
        {
            //            getViewDataBinding().emptyLayout.setVisibility(View.VISIBLE);

            if (mPlaceMapFragment != null)
            {
                fragmentManager.beginTransaction().remove(mPlaceMapFragment).commitAllowingStateLoss();
            }

            getViewDataBinding().mapLayout.removeAllViews();
            getViewDataBinding().mapLayout.setVisibility(View.GONE);
            mPlaceMapFragment = null;
            return;
        }

        if (mPlaceMapFragment == null)
        {
            Place place = null;
            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                if (PlaceViewItem.TYPE_ENTRY == placeViewItem.mType)
                {
                    place = placeViewItem.getItem();
                    break;
                }
            }

            if (place == null && place instanceof Stay)
            {
                mPlaceMapFragment = new StayListMapFragment();
            } else
            {
                mPlaceMapFragment = new GourmetListMapFragment();
            }

            mPlaceMapFragment.setBottomOptionLayout(new View(getContext()));
            fragmentManager.beginTransaction().add(getViewDataBinding().mapLayout.getId(), mPlaceMapFragment, "MAP").commitAllowingStateLoss();

            mPlaceMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
            {
                @Override
                public void onInformationClick(View view, PlaceViewItem placeViewItem)
                {
                    getEventListener().onStayClick(view, placeViewItem);
                }
            });
        }

//        mViewPager = addMapViewPager(getContext(), getViewDataBinding().mapLayout);

        mPlaceMapFragment.setPlaceViewItemList(placeBookingDay, placeViewItemList, true);

        //        getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        getViewDataBinding().mapLayout.setVisibility(View.VISIBLE);
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

//    public void setViewPagerTranslationY(float dy)
//    {
//        if (mViewPager != null)
//        {
//            mViewPager.setTranslationY(dy);
//        }
//    }
//
//    public void resetViewPagerTranslation()
//    {
//        if (mViewPager != null)
//        {
//            mViewPager.setVisibility(View.INVISIBLE);
//            mViewPager.setTranslationY(0);
//        }
//    }

//    private DailyOverScrollViewPager addMapViewPager(Context context, ViewGroup viewGroup)
//    {
//        if (context == null || viewGroup == null)
//        {
//            return null;
//        }
//
//        int paddingLeftRight = ScreenUtils.dpToPx(context, VIEWPAGER_LEFT_N_RIGHT_PADDING_DP);
//        int paddingTopBottom = ScreenUtils.dpToPx(context, VIEWPAGER_TOP_N_BOTTOM_PADDING_DP);
//
//        DailyOverScrollViewPager viewPager = new DailyOverScrollViewPager(context);
//        viewPager.setOffscreenPageLimit(2);
//        viewPager.setClipToPadding(false);
//        viewPager.setPageMargin(ScreenUtils.dpToPx(context, VIEWPAGER_PAGE_MARGIN_DP));
//        viewPager.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
//        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
//
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, VIEWPAGER_HEIGHT_DP));
//        viewPager.setOnPageChangeListener(mOnPageChangeListener);
//
//        layoutParams.gravity = Gravity.BOTTOM;
//
//        viewPager.setLayoutParams(layoutParams);
//        viewPager.setVisibility(View.INVISIBLE);
//
//        viewGroup.addView(viewPager);
//
//        return viewPager;
//    }
//
//    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
//    {
//        @Override
//        public void onPageSelected(int page)
//        {
//            if (mPlaceViewItemViewPagerList == null || mPlaceViewItemViewPagerList.size() <= page)
//            {
//                return;
//            }
//
//            PlaceViewItem placeViewItem = mPlaceViewItemViewPagerList.get(page);
//
//            Place place = placeViewItem.getItem();
//
//            if (place != null)
//            {
//                PlaceClusterItem hotelClusterItem = new PlaceClusterItem(place);
//                mPlaceClusterRenderer.setSelectedClusterItem(hotelClusterItem);
//
//                onMarkerTempClick(hotelClusterItem.getPosition());
//            }
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2)
//        {
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0)
//        {
//        }
//    };
}
