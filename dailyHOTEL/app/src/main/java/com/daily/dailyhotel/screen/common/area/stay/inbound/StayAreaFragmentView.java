package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.view.View;

import com.daily.base.BaseFragmentDialogView;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.twoheart.dailyhotel.databinding.FragmentStayAreaListDataBinding;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaFragmentView extends BaseFragmentDialogView<StayAreaFragmentInterface.OnEventListener, FragmentStayAreaListDataBinding>//
    implements StayAreaFragmentInterface.ViewInterface
{
    public StayAreaFragmentView(StayAreaFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.expandableListView.setTabletDevice(isTabletDevice());
        viewDataBinding.expandableListView.removeTabLayout();
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
                getEventListener().onAreaClick(groupPosition, (StayArea)area);
            }

            @Override
            public void onTabChanged(View view, int position)
            {

            }
        });
    }

    @Override
    public void setAreaGroup(List<StayAreaGroup> areaList)
    {
        if (getViewDataBinding() == null || areaList == null || areaList.size() == 0)
        {
            return;
        }

        getViewDataBinding().expandableListView.setAreaList(areaList);
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
    public void setSelectedAreaGroup(int groupPosition)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return;
        }

        getViewDataBinding().expandableListView.setAreaGroupSelected(groupPosition);
    }
}
