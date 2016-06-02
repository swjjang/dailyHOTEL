package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class HotelDaysListFragment extends HotelListFragment
{
    @Override
    public void onPageSelected(String tabText)
    {
        super.onPageSelected(tabText);

        boolean isSelected = true;

        if (getString(R.string.label_selecteday).equalsIgnoreCase(tabText) == true)
        {
            isSelected = false;
        }

        int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

        Intent intent = HotelSearchCalendarActivity.newInstance(getContext(), mCheckInSaleTime, nights, AnalyticsManager.ValueType.LIST, isSelected, true);
        getParentFragment().startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mHotelRecyclerView == null)
        {
            Util.restartApp(getContext());
            return;
        }

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    mCheckInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                    mCheckOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                    mOnCommunicateListener.selectDay(mCheckInSaleTime, mCheckOutSaleTime, true);
                } else
                {
                    if (mHotelRecyclerView.getVisibility() == View.VISIBLE && mHotelRecyclerView.getAdapter() != null)
                    {
                        if (mHotelRecyclerView.getAdapter().getItemCount() == 0)
                        {
                            fetchList();
                        }
                    }
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
