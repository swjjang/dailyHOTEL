package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;

public class HotelSearchLayout extends PlaceSearchLayout
{
    public HotelSearchLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected String getAroundPlaceString()
    {
        return mContext.getString(R.string.label_view_myaround_hotel);
    }

    @Override
    protected String getSearchHintText()
    {
        return mContext.getString(R.string.label_search_hotel_hint);
    }
}