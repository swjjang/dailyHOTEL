package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentGourmetListLayout extends RecentPlacesListLayout
{
    public RecentGourmetListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected RecentPlacesListAdapter getRecentPlacesListAdapter(Context context, ArrayList<PlaceViewItem> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener)
    {
        return new RecentGourmetListAdapter(context, list, listener);
    }

    @Override
    protected int getEmptyTextResId()
    {
        return R.string.recent_gourmet_list_empty_message;
    }

    @Override
    protected int getEmptyImageResId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected int getEmptyButtonTextResId()
    {
        return R.string.recent_gourmet_list_empty_button_message;
    }

    protected ArrayList<PlaceViewItem> makePlaceViewItemList(ArrayList<? extends Place> list)
    {
        if (list == null || list.size() == 0)
        {
            return new ArrayList<>();
        }

        ArrayList<Gourmet> gourmetList = (ArrayList<Gourmet>) list;
        ArrayList<PlaceViewItem> placeViewItems = new ArrayList<>();
        for (Gourmet gourmet : gourmetList)
        {
            placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
        }

        placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

        return placeViewItems;
    }
}
