package com.daily.dailyhotel.screen.common.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCalendarDataBinding;
import com.twoheart.dailyhotel.databinding.ViewCalendarDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.Calendar;
import java.util.Date;

public abstract class PlaceCalendarView<T1 extends PlaceCalendarView.OnEventListener, T2 extends ActivityCalendarDataBinding> extends BaseView<T1, T2> implements View.OnClickListener
{
    private View[] mDaysView;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public PlaceCalendarView(BaseActivity baseActivity, T1 listener)
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

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.calendarScrollView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.exitView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    /**
     * 시작 날짜와 끝날짜를 입력한다.
     *
     * @param startDateTime
     * @param endDateTime
     */
    protected void setCalendar(String startDateTime, String endDateTime, int[] holidays)
    {
        // setDaysCount

        // makeCalendar
    }

    /**
     * 체크 가능한 날짜 개수
     * 0인 경우 당일로 처리한다.
     *
     * @param days 0 ~ (endDateTime - startDateTime)
     */
    protected void setCheckDaysCount(int days)
    {

    }

    private void makeCalendar(String startDateTime, String endDateTime, int[] holidays)
    {
        try
        {
            Date startDate = DailyCalendar.convertStringToDate(startDateTime);
            Date endDate = DailyCalendar.convertStringToDate(endDateTime);

            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(startDate);

            final int DAYS_OF_MAX = DailyCalendar.compareDateDay(startDateTime, endDateTime) + 1;
            int remainDay = DAYS_OF_MAX;
            int maxMonth = getMonthInterval(calendar, DAYS_OF_MAX);

            int dayOffset = 0;

            for (int i = 0; i <= maxMonth; i++)
            {
                int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                ViewCalendarDataBinding calendarDataBinding = getMonthCalendarView(getContext(), dayOffset//
                    , calendar, day + remainDay - 1 > maxDayOfMonth ? maxDayOfMonth : day + remainDay - 1, holidays);

                View monthCalendarLayout = calendarDataBinding.getRoot();

                if (i >= 0 && i < maxMonth)
                {
                    monthCalendarLayout.setPadding(monthCalendarLayout.getPaddingLeft(), monthCalendarLayout.getPaddingTop()//
                        , monthCalendarLayout.getPaddingRight(), monthCalendarLayout.getPaddingBottom() + ScreenUtils.dpToPx(getContext(), 30));
                }

                getViewDataBinding().calendarLayout.addView(monthCalendarLayout);

                dayOffset += maxDayOfMonth - day + 1;
                remainDay = DAYS_OF_MAX - dayOffset;

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());


        }
    }

    private int getMonthInterval(final Calendar calendar, int interval)
    {
        Calendar lastMonthCalendar = (Calendar) calendar.clone();
        lastMonthCalendar.add(Calendar.DAY_OF_MONTH, interval - 1);

        int lastMonth = lastMonthCalendar.get(Calendar.MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (currentMonth > lastMonth)
        {
            return 12 - currentMonth + lastMonth;
        } else
        {
            return lastMonth - currentMonth;
        }
    }

    private ViewCalendarDataBinding getMonthCalendarView(Context context, final int dayOffset, final Calendar calendar, final int maxDayOfMonth, int[] holidays)
    {
        ViewCalendarDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_calendar_data, null, false);

        dataBinding.monthTextView.setText(DailyCalendar.format(calendar.getTime(), "yyyy.MM"));

        // dayOfMonth
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int length = maxDayOfMonth - day + 1 + dayOfWeek;
        final int LENGTH_OF_WEEK = 7;

        if (length % LENGTH_OF_WEEK != 0)
        {
            length += (LENGTH_OF_WEEK - (length % LENGTH_OF_WEEK));
        }

        PlaceCalendarPresenter.Day[] days = new PlaceCalendarPresenter.Day[length];

        Calendar cloneCalendar = (Calendar) calendar.clone();

        for (int i = 0, j = dayOfWeek, k = day; k <= maxDayOfMonth; i++, j++, k++)
        {
            days[j] = new PlaceCalendarPresenter.Day();
            days[j].dayOffset = dayOffset + i;
            days[j].dateFormat = DailyCalendar.format(cloneCalendar.getTime(), PlaceCalendarPresenter.Day.DATE_FORMAT);
            days[j].dayOfMonth = Integer.toString(cloneCalendar.get(Calendar.DAY_OF_MONTH));
            days[j].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        View dayView;

        for (PlaceCalendarPresenter.Day dayClass : days)
        {
            dayView = getDayView(context, dayClass, holidays);

            if (dayClass != null)
            {
                mDaysView[dayClass.dayOffset] = dayView;
            }

            dataBinding.calendarGridLayout.addView(dayView);
        }

        return dataBinding;
    }

    public View getDayView(Context context, PlaceCalendarPresenter.Day day, int[] holidays)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        DailyTextView visitTextView = new DailyTextView(context);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        visitTextView.setTextColor(getColor(R.color.white));
        visitTextView.setDuplicateParentStateEnabled(true);
        visitTextView.setId(R.id.textView);
        visitTextView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(context, 5);

        relativeLayout.addView(visitTextView, visitLayoutParams);

        DailyTextView dayTextView = new DailyTextView(context);
        dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dayTextView.setDuplicateParentStateEnabled(true);

        RelativeLayout.LayoutParams dayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayLayoutParams.bottomMargin = ScreenUtils.dpToPx(context, 6);
        dayLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        relativeLayout.addView(dayTextView, dayLayoutParams);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ScreenUtils.dpToPx(context, 45);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_calendar_day_background));

        if (day == null)
        {
            dayTextView.setText(null);
            relativeLayout.setTag(null);
            relativeLayout.setEnabled(false);
        } else
        {
            switch (day.dayOfWeek)
            {
                // 일요일
                case Calendar.SUNDAY:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    break;

                case Calendar.SATURDAY:
                    if (isHoliday(day, holidays) == true)
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_saturday_textcolor));
                    }
                    break;

                default:
                    if (isHoliday(day, holidays) == true)
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_default_text_color));
                    }
                    break;
            }

            dayTextView.setText(day.dayOfMonth);
            relativeLayout.setTag(day);
        }

        relativeLayout.setOnClickListener(this);

        return relativeLayout;
    }

    private void setDaysCount(int daysCount)
    {
        if (mDaysView != null)
        {
            int length = mDaysView.length;

            for (int i = 0; i < length; i++)
            {
                mDaysView[i] = null;
            }

            mDaysView = null;
        }

        mDaysView = new View[daysCount];
    }

    private boolean isHoliday(PlaceCalendarPresenter.Day day, int[] holidays)
    {
        if (day == null || holidays == null || holidays.length == 0)
        {
            return false;
        }

        Calendar calendar = DailyCalendar.getInstance();

        try
        {
            int calendarDay = Integer.parseInt(day.dateFormat);

            for (int holiday : holidays)
            {
                if (holiday == calendarDay)
                {
                    return true;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }
}
