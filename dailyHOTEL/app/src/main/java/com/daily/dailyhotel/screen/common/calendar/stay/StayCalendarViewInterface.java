package com.daily.dailyhotel.screen.common.calendar.stay;

import android.util.SparseIntArray;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;

import java.util.List;

import io.reactivex.Observable;

public interface StayCalendarViewInterface extends BaseDialogViewInterface
{
    void setCalendarList(List<ObjectItem> calendarList);

    Observable<Boolean> showAnimation();

    Observable<Boolean> hideAnimation();

    void setVisibility(boolean visibility);

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
