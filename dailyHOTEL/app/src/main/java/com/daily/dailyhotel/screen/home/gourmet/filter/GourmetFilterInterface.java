package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggestV2;

public interface GourmetFilterInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setSortLayout(StayFilter.SortType sortType);

        void setSortLayoutEnabled(boolean enabled);

        void setPerson(int person, int personCountOfMax, int personCountOfMin);

        void setBedTypeCheck(int flagBedTypeFilters);

        void setAmenitiesCheck(int flagAmenitiesFilters);

        void setRoomAmenitiesCheck(int flagRoomAmenitiesFilters);

        void setConfirmText(String text);

        void setConfirmEnabled(boolean enabled);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onMinusPersonClick();

        void onPlusPersonClick();

        void onResetClick();

        void onConfirmClick();

        void onCheckedChangedSort(StayFilter.SortType sortType);

        void onCheckedChangedBedType(int flag);

        void onCheckedChangedAmenities(int flag);

        void onCheckedChangedRoomAmenities(int flag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onConfirmClick(Activity activity, StaySuggestV2 suggest, StayFilter stayFilter, int listCountByFilter);

        void onBackClick(Activity activity);

        void onResetClick(Activity activity);

        void onEmptyResult(Activity activity, StayFilter stayFilter);
    }
}