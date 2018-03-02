package com.daily.dailyhotel.screen.home.stay.inbound.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCalendarDayDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutCalendarFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutCalendarMonthDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutCalendarWeekDataBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class StayCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mList;
    private int mCheckInDay;
    private int mCheckOutDay;
    private boolean mLastDayEnabled;

    View.OnClickListener mOnClickListener;

    public StayCalendarAdapter(Context context, List<ObjectItem> arrayList)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(arrayList);
    }

    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        mOnClickListener = onClickListener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void add(ObjectItem objectItem)
    {
        mList.add(objectItem);
    }

    public void add(int position, ObjectItem placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends ObjectItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends ObjectItem> collection)
    {
        clear();
        addAll(collection);
    }

    public void remove(int position)
    {
        if (mList == null || mList.size() <= position)
        {
            return;
        }

        mList.remove(position);
    }

    public ObjectItem getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }

    public int getMonthPosition(int year, int month)
    {
        int size = mList.size();
        ObjectItem objectItem;

        for (int i = 0; i < size; i++)
        {
            objectItem = mList.get(i);

            if (objectItem.mType == ObjectItem.TYPE_MONTH_VIEW)
            {
                BaseCalendarPresenter.Month monthItem = objectItem.getItem();

                if (monthItem.year == year && monthItem.month == month)
                {
                    return i;
                }
            }
        }

        return 0;
    }

    public void setCheckInDay(int checkInDay)
    {
        mCheckInDay = checkInDay;
    }

    public void setCheckOutDay(int checkOutDay)
    {
        mCheckOutDay = checkOutDay;
    }

    public void setLastDayEnabled(boolean enabled)
    {
        mLastDayEnabled = enabled;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mList.get(position).mType;
    }

    @Override
    public int getItemCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_MONTH_VIEW:
            {
                LayoutCalendarMonthDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_calendar_month_data, parent, false);

                return new MonthViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_WEEK_VIEW:
            {
                LayoutCalendarWeekDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_calendar_week_data, parent, false);

                return new WeekViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutCalendarFooterDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_calendar_footer_data, parent, false);

                return new BaseDataBindingViewHolder(dataBinding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ObjectItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ObjectItem.TYPE_MONTH_VIEW:
                onBindViewHolder((MonthViewHolder) holder, item.getItem(), position);
                break;

            case ObjectItem.TYPE_WEEK_VIEW:
                onBindViewHolder((WeekViewHolder) holder, item.getItem(), position);
                break;

        }

    }

    private void onBindViewHolder(MonthViewHolder holder, BaseCalendarPresenter.Month month, int position)
    {
        if (holder == null || month == null)
        {
            return;
        }

        holder.dataBinding.monthTextView.setText(String.format(Locale.KOREA, "%4d.%02d", month.year, month.month));
    }

    private void onBindViewHolder(WeekViewHolder holder, BaseCalendarPresenter.Day[] week, int position)
    {
        if (holder == null || week == null)
        {
            return;
        }

        // 일요일
        setDayView(holder.dataBinding.sundayLayout, week[0], Calendar.SUNDAY);

        // 월요일
        setDayView(holder.dataBinding.mondayLayout, week[1], Calendar.MONDAY);

        // 화요일
        setDayView(holder.dataBinding.tuesdayLayout, week[2], Calendar.TUESDAY);

        // 수요일
        setDayView(holder.dataBinding.wednesdayLayout, week[3], Calendar.WEDNESDAY);

        // 목요일
        setDayView(holder.dataBinding.thursdayLayout, week[4], Calendar.THURSDAY);

        // 금요일
        setDayView(holder.dataBinding.fridayLayout, week[5], Calendar.FRIDAY);

        // 토요일
        setDayView(holder.dataBinding.saturdayLayout, week[6], Calendar.SATURDAY);
    }

    private void setDayView(LayoutCalendarDayDataBinding dayDataBinding, BaseCalendarPresenter.Day day, int dayOfWeek)
    {
        if (dayDataBinding == null)
        {
            return;
        }

        if (day == null)
        {
            dayDataBinding.checkTextView.setVisibility(View.INVISIBLE);
            dayDataBinding.dayTextView.setText(null);
            dayDataBinding.dayLayout.setTag(null);
            dayDataBinding.dayLayout.setEnabled(false);
            dayDataBinding.dayTextView.setStrikeFlag(false);
            dayDataBinding.dayLayout.setBackgroundResource(R.color.white);
        } else
        {
            if (day.sideDay == true)
            {
                dayDataBinding.checkTextView.setVisibility(View.INVISIBLE);
                dayDataBinding.dayLayout.setTag(null);
                dayDataBinding.dayLayout.setEnabled(false);
                dayDataBinding.dayTextView.setStrikeFlag(false);
                dayDataBinding.dayLayout.setBackgroundResource(R.color.white);
                dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
            } else if (day.soldOut == true)
            {
                dayDataBinding.checkTextView.setVisibility(View.INVISIBLE);
                dayDataBinding.dayLayout.setTag(null);
                dayDataBinding.dayLayout.setEnabled(false);
                dayDataBinding.dayTextView.setStrikeFlag(true);
                dayDataBinding.dayLayout.setBackgroundResource(R.color.white);
                dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
            } else
            {
                // yyyyMMdd
                int yyyyMMdd = day.year * 10000 + day.month * 100 + day.dayOfMonth;

                if (mCheckInDay == yyyyMMdd)
                {
                    dayDataBinding.checkTextView.setVisibility(View.VISIBLE);
                    dayDataBinding.checkTextView.setText(R.string.act_booking_chkin);
                    dayDataBinding.dayLayout.setBackgroundResource(R.drawable.select_date_check_in);
                    dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                } else if (mCheckOutDay == yyyyMMdd)
                {
                    dayDataBinding.checkTextView.setVisibility(View.VISIBLE);
                    dayDataBinding.checkTextView.setText(R.string.act_booking_chkout);
                    dayDataBinding.dayLayout.setBackgroundResource(R.drawable.select_date_check_out);
                    dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                } else
                {
                    dayDataBinding.checkTextView.setVisibility(View.INVISIBLE);
                    dayDataBinding.dayLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selector_calendar_day_background));

                    if (day.holiday == true)
                    {
                        dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        switch (dayOfWeek)
                        {
                            // 일요일
                            case Calendar.SUNDAY:
                                dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                                break;

                            case Calendar.SATURDAY:
                                dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColorStateList(R.color.selector_calendar_saturday_textcolor));
                                break;

                            default:
                                dayDataBinding.dayTextView.setTextColor(mContext.getResources().getColorStateList(R.color.selector_calendar_default_text_color));
                                break;
                        }
                    }

                    dayDataBinding.dayLayout.setEnabled(day.lastDay ? mLastDayEnabled : true);
                    dayDataBinding.dayTextView.setStrikeFlag(false);

                    if (yyyyMMdd > mCheckInDay && yyyyMMdd < mCheckOutDay)
                    {
                        dayDataBinding.dayLayout.setActivated(true);
                        dayDataBinding.dayLayout.setSelected(true); // 배경색 변경
                    } else
                    {
                        dayDataBinding.dayLayout.setActivated(false);
                        dayDataBinding.dayLayout.setSelected(false); // 배경색 변경
                    }
                }
            }

            dayDataBinding.dayTextView.setText(Integer.toString(day.dayOfMonth));
            dayDataBinding.dayLayout.setTag(day);

            dayDataBinding.dayLayout.setOnClickListener(mOnClickListener);
        }
    }

    protected class MonthViewHolder extends RecyclerView.ViewHolder
    {
        LayoutCalendarMonthDataBinding dataBinding;

        public MonthViewHolder(LayoutCalendarMonthDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    protected class WeekViewHolder extends RecyclerView.ViewHolder
    {
        LayoutCalendarWeekDataBinding dataBinding;

        public WeekViewHolder(LayoutCalendarWeekDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    private class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
