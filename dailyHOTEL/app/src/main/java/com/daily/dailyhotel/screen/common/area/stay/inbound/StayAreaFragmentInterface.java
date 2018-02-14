package com.daily.dailyhotel.screen.common.area.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StayAreaFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setAreaList(List<StayAreaGroup> areaList);

        void setLocationText(String locationText);

        void setLocationTermVisible(boolean visible);

        Completable collapseGroupWithAnimation(int groupPosition, boolean animation);

        Completable expandGroupWithAnimation(int groupPosition, boolean animation);

        void setSelectedAreaGroup(int groupPosition);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onAroundSearchClick();

        void onAreaGroupClick(int groupPosition);

        void onAreaClick(int groupPosition, StayArea stayTown);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
