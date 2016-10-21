package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
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
    protected RecentPlacesListAdapter getRecentPlacesListAdapter(Context context, ArrayList<? extends Place> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener)
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
        return 0;
    }
}
