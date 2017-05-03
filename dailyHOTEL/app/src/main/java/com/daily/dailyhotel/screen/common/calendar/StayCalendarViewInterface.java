package com.daily.dailyhotel.screen.common.calendar;

import android.support.v4.util.Pair;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.ListItem;

import java.util.ArrayList;
import java.util.List;

public interface StayCalendarViewInterface extends BaseViewInterface
{
    void makeCalendarView(ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList);

    void showAnimation();

    void hideAnimation();

    void setVisibility(boolean visibility);
}
