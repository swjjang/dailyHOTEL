package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ScrollView;

import com.daily.dailyhotel.entity.CampaignTag;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;

import java.util.ArrayList;
import java.util.List;

public class GourmetSearchResultLayout extends PlaceSearchResultLayout
{
    public GourmetSearchResultLayout(Context context, String callByScreen, OnBaseEventListener listener)
    {
        super(context, callByScreen, listener);
    }

    public void setCalendarText(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return;
        }

        setCalendarText(gourmetBookingDay.getVisitDay("MM.dd(EEE)"));
    }

    @Override
    protected void initEmptyLayout(ScrollView scrollView)
    {

    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<GourmetSearchResultListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            GourmetSearchResultListFragment gourmetSearchResultListFragment = new GourmetSearchResultListFragment();
            gourmetSearchResultListFragment.setPlaceOnListFragmentListener(listener);
            gourmetSearchResultListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetSearchResultListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {

    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {

    }

    @Override
    public void setCampaignTagVisible(boolean visible)
    {

    }

    @Override
    public void setCampaignTagList(List<CampaignTag> campaignTagList)
    {

    }

    @Override
    public boolean hasCampaignTag()
    {
        return false;
    }
}