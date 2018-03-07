package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StayAreaFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setAreaGroup(List<StayAreaGroup> areaList);

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

        void onAreaClick(int groupPosition, StayArea area);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventAreaGroupClick(Activity activity, String name);

        void onEventAreaClick(Activity activity, String areaGroupNanem, String areaName);
    }
}
