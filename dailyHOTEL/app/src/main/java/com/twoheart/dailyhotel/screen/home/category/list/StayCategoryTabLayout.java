package com.twoheart.dailyhotel.screen.home.category.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 4. 19..
 */

public class StayCategoryTabLayout extends PlaceMainLayout
{
    private String mTitleText;
    private DailyCategoryType mDailyCategoryType;

    public StayCategoryTabLayout(Context context, String titleText, DailyCategoryType dailyCategoryType, PlaceMainLayout.OnEventListener onEventListener)
    {
        super(context, onEventListener);

        mTitleText = titleText;
        mDailyCategoryType = dailyCategoryType;
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter( //
                                                                              FragmentManager fragmentManager, int count, View bottomOptionLayout //
        , PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StayCategoryListFragment> list = new ArrayList<>(count);

        boolean isBoutiqueBMEnabled = DailyPreference.getInstance(mContext).isRemoteConfigBoutiqueBMEnabled();

        for (int i = 0; i < count; i++)
        {
            StayCategoryListFragment stayCategoryListFragment = new StayCategoryListFragment();
            stayCategoryListFragment.setPlaceOnListFragmentListener(listener);
            stayCategoryListFragment.setBottomOptionLayout(bottomOptionLayout);
            stayCategoryListFragment.setIsShowLocalPlus( //
                isBoutiqueBMEnabled == true && DailyCategoryType.STAY_BOUTIQUE.equals(mDailyCategoryType) == true);
            list.add(stayCategoryListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        //        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, null);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        //        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, null);
    }

    @Override
    protected String getAppBarTitle()
    {
        return DailyTextUtils.isTextEmpty(mTitleText) == false //
            ? mTitleText //
            : mContext.getString(R.string.label_daily_hotel);
    }

    protected void setToolbarDateText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        String checkInDay = stayBookingDay.getCheckInDay("M.d(EEE)");
        String checkOutDay = stayBookingDay.getCheckOutDay("M.d(EEE)");

        setToolbarDateText(String.format(Locale.KOREA, "%s - %s", checkInDay, checkOutDay));
    }
}
