package com.daily.dailyhotel.screen.common.calendar;

import android.support.v4.util.Pair;

import com.daily.base.BaseViewInterface;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface StayCalendarViewInterface extends BaseViewInterface
{
    void makeCalendarView(ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList);

    Observable<Boolean> showAnimation();

    Observable<Boolean> hideAnimation();

    void setVisibility(boolean visibility);

    void setCheckInDay(String checkInDateTime);

    void setCheckOutDay(String checkOutDateTime);

    void clickDay(String checkDateTime);

    void setLastDayEnabled(boolean enabled);

    void setConfirmEnabled(boolean enabled);

    void setConfirmText(String text);

    void reset();
}
