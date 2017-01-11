package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class StayMainLayout extends PlaceMainLayout
{
    public StayMainLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StayListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            StayListFragment stayListFragment = new StayListFragment();
            stayListFragment.setPlaceOnListFragmentListener(listener);
            stayListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(stayListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, null);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, null);
    }

    @Override
    protected String getAppBarTitle()
    {
        return mContext.getString(R.string.label_daily_hotel);
    }

    protected void setToolbarDateText(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        if (checkInSaleTime == null || checkOutSaleTime == null)
        {
            return;
        }

        String format = Util.getLCDWidth(mContext) > 480 ? "M.d(EEE)" : "M.d";

        String checkInDay = checkInSaleTime.getDayOfDaysDateFormat(format);
        String checkOutDay = checkOutSaleTime.getDayOfDaysDateFormat(format);

        setToolbarDateText(String.format("%s-%s", checkInDay, checkOutDay));
    }
}
