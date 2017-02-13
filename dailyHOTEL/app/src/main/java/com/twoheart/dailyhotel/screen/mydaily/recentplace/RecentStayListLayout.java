package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentStayListLayout extends RecentPlacesListLayout
{
    public RecentStayListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected RecentPlacesListAdapter getRecentPlacesListAdapter(Context context, ArrayList<PlaceViewItem> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener)
    {
        return new RecentStayListAdapter(context, list, listener);
    }

    @Override
    protected int getEmptyTextResId()
    {
        return R.string.recent_stay_list_empty_message;
    }

    @Override
    protected int getEmptyImageResId()
    {
        return R.drawable.no_hotel_ic;
    }

    @Override
    protected int getEmptyButtonTextResId()
    {
        return R.string.recent_stay_list_empty_button_message;
    }

    @Override
    protected ArrayList<PlaceViewItem> makePlaceViewItemList(ArrayList<? extends Place> list)
    {
        if (list == null || list.size() == 0)
        {
            return new ArrayList<>();
        }

        ArrayList<Stay> stayList = (ArrayList<Stay>) list;
        ArrayList<PlaceViewItem> placeViewItems = new ArrayList<>();
        for (Stay stay : stayList)
        {
            placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

        return placeViewItems;
    }
}
