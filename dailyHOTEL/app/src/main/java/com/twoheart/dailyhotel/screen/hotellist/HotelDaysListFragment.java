package com.twoheart.dailyhotel.screen.hotellist;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;

public class HotelDaysListFragment extends HotelListFragment
{
    private SaleTime mSelectedCheckInSaleTime;
    private SaleTime mSelectedCheckOutSaleTime;

    public void initSelectedCheckInOutDate(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        mSelectedCheckInSaleTime = checkInSaleTime;
        mSelectedCheckOutSaleTime = checkOutSaleTime;
    }

    public SaleTime getSelectedCheckInSaleTime()
    {
        return mSelectedCheckInSaleTime;
    }

    @Override
    public void onPageSelected()
    {
        super.onPageSelected();

        SaleTime saleTime = mSelectedCheckInSaleTime.getClone(0);

        Intent intent = com.twoheart.dailyhotel.activity.CalendarActivity.newInstance(getContext(), PlaceType.HOTEL, saleTime);
        getParentFragment().startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    protected void fetchList()
    {
        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
        fetchList(hotelCurationOption.getProvince(), mSelectedCheckInSaleTime, mSelectedCheckOutSaleTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    mSelectedCheckInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                    mSelectedCheckOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                    mOnCommunicateListener.selectDay(mSelectedCheckInSaleTime, mSelectedCheckOutSaleTime, true);
                } else
                {
                    if (mHotelRecycleView.getVisibility() == View.VISIBLE && mHotelRecycleView.getAdapter() != null)
                    {
                        if (mHotelRecycleView.getAdapter().getItemCount() == 0)
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

    @Override
    public View.OnClickListener getOnItemClickListener()
    {
        return mOnItemClickListener;
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            int position = mHotelRecycleView.getChildAdapterPosition(view);

            if (position < 0)
            {
                refreshList();
                return;
            }

            PlaceViewItem placeViewItem = mHotelAdapter.getItem(position);

            if (placeViewItem.getType() != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mOnCommunicateListener.selectHotel(placeViewItem, mSelectedCheckInSaleTime);
        }
    };
}
