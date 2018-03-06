package com.daily.dailyhotel.screen.common.area.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StaySubwayFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setTab(List<Area> tabList);

        void setAreaGroup(List<StaySubwayAreaGroup> areaList);

        void setLocationText(String locationText);

        void setLocationTermVisible(boolean visible);

        Observable<Boolean> collapseGroupWithAnimation(int position, boolean animation);

        Observable<Boolean> expandGroupWithAnimation(int position, boolean animation);

        void setTabSelected(int position);

        void setAreaGroupSelected(int position);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onAroundSearchClick();

        void onAreaGroupClick(int position);

        void onAreaClick(int groupPosition, Area area);

        void onTabChanged(int position, Object tag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
