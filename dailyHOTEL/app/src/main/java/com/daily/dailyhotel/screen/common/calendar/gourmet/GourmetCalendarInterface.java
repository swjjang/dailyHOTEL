package com.daily.dailyhotel.screen.common.calendar.gourmet;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.screen.common.calendar.BaseCalendarPresenter;

import java.util.List;

import io.reactivex.Observable;

public interface GourmetCalendarInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setCalendarList(List<ObjectItem> calendarList);

        Observable<Boolean> showAnimation();

        Observable<Boolean> hideAnimation();

        void setVisible(boolean visible);

        void setVisitDay(int visitDay);

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
        void setVisitDateTime(String visitDateTime);

        void onScreen(Activity activity);

        void onEventCloseClick(Activity activity, String callByScreen);

        void onEventConfirmClick(Activity activity, String callByScreen, String visitDateTime);
    }
}
