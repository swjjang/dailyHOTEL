package com.daily.dailyhotel.screen.home.stay.outbound.calendar;

import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;

import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundCalendarViewInterface extends BaseDialogViewInterface
{
    void setCalendarList(List<ObjectItem> calendarList);

    Observable<Boolean> showAnimation();

    Observable<Boolean> hideAnimation();

    void setVisibility(boolean visibility);

    void setCheckInDay(int checkInDay);

    void setCheckOutDay(int checkOutDay);

    void setLastDayEnabled(boolean enabled);

    void setConfirmEnabled(boolean enabled);

    void setConfirmText(String text);

    void setMarginTop(int marginTop);

    void reset();

    void smoothScrollMonthPosition(int year, int month);

    void notifyCalendarDataSetChanged();
}
