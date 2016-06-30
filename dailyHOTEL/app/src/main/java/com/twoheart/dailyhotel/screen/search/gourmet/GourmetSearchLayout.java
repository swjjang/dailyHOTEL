package com.twoheart.dailyhotel.screen.search.gourmet;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;

public class GourmetSearchLayout extends PlaceSearchLayout
{
    public GourmetSearchLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mContext.getString(R.string.label_view_myaround_gourmet);
    }

    @Override
    protected String getSearchHintText()
    {
        return mContext.getString(R.string.label_search_gourmet_hint);
    }

    @Override
    protected int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case GOURMET_ICON:
                return R.drawable.search_ic_02_gourmet;

            default:
                return R.drawable.search_ic_03_recent;
        }
    }
}