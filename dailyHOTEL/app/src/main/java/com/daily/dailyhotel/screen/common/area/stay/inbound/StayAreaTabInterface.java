package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import io.reactivex.Observable;

public interface StayAreaTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        Observable<Boolean> getCompleteCreatedFragment();

        void setTabVisible(boolean visible);

        void setSubwayAreaTabSelection();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onAreaTabClick();

        void onSubwayTabClick();

        void onAroundSearchClick();

        void onAreaClick(StayAreaGroup areaGroup, StayArea area);

        void onSubwayAreaClick(StaySubwayAreaGroup areaGroup, Area area);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, String categoryCode);

        void onEventSearchClick(Activity activity, DailyCategoryType dailyCategoryType);

        void onEventChangedDistrictClick(Activity activity, String previousDistrictName, String previousTownName//
            , String changedDistrictName, String changedTownName, StayBookDateTime stayBookDateTime);

        void onEventChangedDateClick(Activity activity);

        void onEventTownClick(Activity activity, String districtName, String townName);

        void onEventClosedClick(Activity activity, String stayCategory);

        void onEventAroundSearchClick(Activity activity, DailyCategoryType dailyCategoryType);
    }
}
