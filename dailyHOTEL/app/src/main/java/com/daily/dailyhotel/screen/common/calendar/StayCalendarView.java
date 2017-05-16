package com.daily.dailyhotel.screen.common.calendar;

import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCalendarDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;

import io.reactivex.Observable;

public class StayCalendarView extends PlaceCalendarView<StayCalendarView.OnEventListener, ActivityCalendarDataBinding> implements StayCalendarViewInterface
{
    @Override
    public void makeCalendarView(ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList)
    {
        super.makeCalendarView(arrayList);
    }

    public interface OnEventListener extends PlaceCalendarView.OnEventListener
    {
        void onDayClick(View view);

        void onConfirmClick();
    }

    public StayCalendarView(BaseActivity baseActivity, StayCalendarView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityCalendarDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        super.initLayout(viewDataBinding);


    }

    @Override
    public void setToolbarTitle(String title)
    {
        super.setToolbarTitle(title);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
            case R.id.exitView:
                getEventListener().onBackClick();
                break;

            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;

            default:
                getEventListener().onDayClick(v);
                break;
        }
    }

    @Override
    public Observable<Boolean> showAnimation()
    {
        return super.showAnimation();
    }

    @Override
    public Observable<Boolean> hideAnimation()
    {
        return super.hideAnimation();
    }

    @Override
    public void setVisibility(boolean visibility)
    {
        super.setVisibility(visibility);
    }

    @Override
    public void setCheckInDay(String checkInDateTime)
    {
        if (mDaysViewList == null || mDaysViewList.size() == 0 || DailyTextUtils.isTextEmpty(checkInDateTime) == true)
        {
            return;
        }

        View view = searchDayView(checkInDateTime);

        if (view != null)
        {
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(R.string.act_booking_chkin);
            textView.setVisibility(View.VISIBLE);

            view.setBackgroundResource(R.drawable.select_date_check_in);
        }
    }

    @Override
    public void setCheckOutDay(String checkOutDateTime)
    {
        if (mDaysViewList == null || mDaysViewList.size() == 0 || DailyTextUtils.isTextEmpty(checkOutDateTime) == true)
        {
            return;
        }

        View view = searchDayView(checkOutDateTime);

        if (view != null)
        {
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(R.string.act_booking_chkout);
            textView.setVisibility(View.VISIBLE);

            view.setBackgroundResource(R.drawable.select_date_check_out);
        }

        setRangeDays();
    }

    @Override
    public void clickDay(String checkDateTime)
    {
        if (mDaysViewList == null || mDaysViewList.size() == 0 || DailyTextUtils.isTextEmpty(checkDateTime) == true)
        {
            return;
        }

        View view = searchDayView(checkDateTime);

        if (view != null)
        {
            view.performClick();
        }
    }

    @Override
    public void setLastDayEnabled(boolean enabled)
    {
        if (mDaysViewList == null || mDaysViewList.size() == 0)
        {
            return;
        }

        mDaysViewList.get(mDaysViewList.size() - 1).setEnabled(enabled);
    }

    @Override
    public void setConfirmEnabled(boolean enabled)
    {
        super.setConfirmEnabled(enabled);
    }

    @Override
    public void setConfirmText(String text)
    {
        super.setConfirmText(text);
    }

    @Override
    public void reset()
    {
        super.reset();


    }

    private View searchDayView(String dateTime)
    {
        if (mDaysViewList == null || mDaysViewList.size() == 0 || DailyTextUtils.isTextEmpty(dateTime) == true)
        {
            return null;
        }

        try
        {
            String checkDay = DailyCalendar.convertDateFormatString(dateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd");

            for (View dayView : mDaysViewList)
            {
                if (dayView == null)
                {
                    continue;
                }

                PlaceCalendarPresenter.Day day = (PlaceCalendarPresenter.Day) dayView.getTag();

                if (checkDay.equalsIgnoreCase(DailyCalendar.convertDateFormatString(day.dateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd")) == true)
                {
                    return dayView;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    private void setRangeDays()
    {
        boolean searchStartDay = false;

        for (View dayView : mDaysViewList)
        {
            if (searchStartDay == false)
            {
                if (dayView.isSelected() == true)
                {
                    searchStartDay = true;
                }
            } else
            {
                if (dayView.isSelected() == true)
                {
                    break;
                }

                dayView.setSelected(true);
                dayView.setActivated(true);
            }
        }
    }
}
