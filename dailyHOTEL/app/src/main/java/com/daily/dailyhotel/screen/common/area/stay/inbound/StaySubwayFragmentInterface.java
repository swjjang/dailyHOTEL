package com.daily.dailyhotel.screen.common.area.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

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
        void setAreaList(List<StayAreaGroup> areaList);

        void setLocationText(String locationText);

        void setLocationTermVisible(boolean visible);

        Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation);

        Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation);

        void setSelectedAreaGroup(int groupPosition);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onAroundSearchClick();

        void onAreaGroupClick(int groupPosition);

        void onAreaClick(int groupPosition, StayArea are);

        void onSubwayAreaClick(int position, Area area);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
