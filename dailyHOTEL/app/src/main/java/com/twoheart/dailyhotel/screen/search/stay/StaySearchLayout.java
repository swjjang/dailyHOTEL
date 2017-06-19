package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;

public class StaySearchLayout extends PlaceSearchLayout
{
    public interface OnEventListener extends PlaceSearchLayout.OnEventListener
    {
        void onStayOutboundClick();
    }

    public StaySearchLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        View stayOutboundLayout = view.findViewById(R.id.stayOutboundLayout);
        stayOutboundLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    ((StaySearchLayout.OnEventListener) mOnEventListener).onStayOutboundClick();
                }
            }
        });
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mContext.getString(R.string.label_view_myaround_hotel);
    }

    @Override
    protected String getSearchHintText()
    {
        return mContext.getString(R.string.label_search_hotel_hint);
    }

    @Override
    protected int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case HOTEL_ICON:
                return R.drawable.search_ic_02_hotel;

            default:
                return R.drawable.search_ic_03_recent;
        }
    }
}