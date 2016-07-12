package com.twoheart.dailyhotel.screen.search.stay.result;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StaySearchResultListFragment extends StayListFragment
{
    @Override
    protected ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList, SortType sortType)
    {
        ArrayList<PlaceViewItem> stayViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return stayViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Stay stay : stayList)
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        if (Constants.PAGENATION_LIST_SIZE > stayList.size())
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
        }

        return stayViewItemList;
    }
}
