package com.daily.dailyhotel.screen.common.calendar.stay;

import android.app.Activity;
import android.util.SparseIntArray;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.screen.common.calendar.BaseCalendarPresenter;
import com.daily.dailyhotel.screen.common.calendar.BaseCalendarView;

import java.util.List;

import io.reactivex.Observable;

public interface StayCalendarInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setCalendarList(List<ObjectItem> calendarList);

        Observable<Boolean> showAnimation();

        Observable<Boolean> hideAnimation();

        void setVisible(boolean visible);

        void setCheckInDay(int checkInDay);

        void setCheckOutDay(int checkOutDay);

        void setAvailableCheckOutDays(SparseIntArray availableCheckOutDays);

        void setLastDayEnabled(boolean enabled);

        void setConfirmEnabled(boolean enabled);

        void setConfirmText(String text);

        void setMarginTop(int marginTop);

        void scrollMonthPosition(int year, int month);

        void notifyCalendarDataSetChanged();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onDayClick(BaseCalendarPresenter.Day day);

        void onConfirmClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void setCheckInOutDateTime(String checkInDateTime, String checkOutDateTime);

        void onScreen(Activity activity);

        void onEventCloseClick(Activity activity, String callByScreen);

        void onEventConfirmClick(Activity activity, String callByScreen, String checkInDateTime, String checkOutDateTime);
    }











}
