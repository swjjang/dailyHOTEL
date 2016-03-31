package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Util;

public class HotelDaysListFragment extends HotelListFragment
{
    @Override
    public void onPageSelected()
    {
        super.onPageSelected();

        SaleTime saleTime = mCheckInSaleTime.getClone(0);

        Intent intent = HotelCalendarActivity.newInstance(getContext(), saleTime);
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
