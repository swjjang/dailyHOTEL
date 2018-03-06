package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.view.View;

import com.daily.base.BaseFragmentDialogView;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.twoheart.dailyhotel.databinding.FragmentStaySubwayListDataBinding;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StaySubwayFragmentView extends BaseFragmentDialogView<StaySubwayFragmentInterface.OnEventListener, FragmentStaySubwayListDataBinding>//
    implements StaySubwayFragmentInterface.ViewInterface
{
    public StaySubwayFragmentView(StaySubwayFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStaySubwayListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.expandableListView.setTabletDevice(isTabletDevice());
        viewDataBinding.expandableListView.setOnAreaExpandableListener(new StayAreaExpandableListView.OnStayAreaExpandableListener()
        {
            @Override
            public void onAroundSearchClick()
            {
                getEventListener().onAroundSearchClick();
            }

            @Override
            public void onAreaGroupClick(int groupPosition)
            {
                getEventListener().onAreaGroupClick(groupPosition);
            }

            @Override
            public void onAreaClick(int groupPosition, Area area)
            {
                getEventListener().onAreaClick(groupPosition, area);
            }

            @Override
            public void onTabChanged(View view, int position)
            {
                getEventListener().onTabChanged(position, view.getTag());
            }
        });
    }

    @Override
    public void setTab(List<Area> tabList)
    {
        if (getViewDataBinding() == null || tabList == null)
        {
            return;
        }

        getViewDataBinding().expandableListView.setTab(tabList);
    }

    @Override
    public void setAreaGroup(List<StaySubwayAreaGroup> areaGroup)
    {
        if (getViewDataBinding() == null || areaGroup == null || areaGroup.size() == 0)
        {
            return;
        }

        getViewDataBinding().expandableListView.setAreaList(areaGroup);
    }

    @Override
    public void setLocationText(String locationText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().expandableListView.setHeaderLocationText(locationText);
    }

    @Override
    public void setLocationTermVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().expandableListView.setHeaderLocationTermVisible(visible);
    }

    @Override
    public Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        return getViewDataBinding().expandableListView.collapseGroupWithAnimation(groupPosition, animation);
    }

    @Override
    public Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        return getViewDataBinding().expandableListView.expandGroupWithAnimation(groupPosition, animation);
    }

    @Override
    public void setTabSelected(int position)
    {
        if (getViewDataBinding() == null || position < 0)
        {
            return;
        }

        getViewDataBinding().expandableListView.setTabSelected(position);
    }

    @Override
    public void setAreaGroupSelected(int position)
    {
        if (getViewDataBinding() == null || position < 0)
        {
            return;
        }

        getViewDataBinding().expandableListView.setAreaGroupSelected(position);
    }
}
