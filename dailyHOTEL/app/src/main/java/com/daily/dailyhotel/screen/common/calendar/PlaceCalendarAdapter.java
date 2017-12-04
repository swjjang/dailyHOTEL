package com.daily.dailyhotel.screen.common.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewCalendarDataBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class PlaceCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mList;

    View.OnClickListener mOnClickListener;

    public PlaceCalendarAdapter(Context context, ArrayList<ObjectItem> arrayList)
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
            case ObjectItem.TYPE_ENTRY:
            {
                ViewCalendarDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.view_calendar_data, null, false);

                return new MonthViewHolder(dataBinding);
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
            case ObjectItem.TYPE_ENTRY:
                onBindViewHolder((MonthViewHolder) holder, item);
                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(MonthViewHolder holder, ObjectItem objectItem)
    {
        if (holder == null || objectItem == null)
        {
            return;
        }
    }

    private void getMonthCalendarView(ViewCalendarDataBinding dataBinding, Pair<String, PlaceCalendarPresenter.Day[]> pair)
    {
        if (dataBinding == null || pair == null)
        {
            return;
        }

        dataBinding.monthTextView.setText(pair.first);

        View dayView;

        for (PlaceCalendarPresenter.Day dayClass : pair.second)
        {
            dayView = getDayView(mContext, dayClass);

            dataBinding.calendarGridLayout.addView(dayView);
        }
    }

    private View getDayView(Context context, PlaceCalendarPresenter.Day day)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        DailyTextView visitTextView = new DailyTextView(context);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        visitTextView.setTextColor(mContext.getResources().getColor(R.color.white));
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
        relativeLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selector_calendar_day_background));

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
                    if (day.isHoliday == true)
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_saturday_textcolor));
                    }
                    break;

                default:
                    if (day.isHoliday == true)
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

            if (day.isDefaultDimmed == true)
            {
                relativeLayout.setEnabled(false);
            } else
            {
                relativeLayout.setEnabled(true);
            }
        }

        relativeLayout.setOnClickListener(mOnClickListener);

        return relativeLayout;
    }

    protected class MonthViewHolder extends RecyclerView.ViewHolder
    {
        ViewCalendarDataBinding dataBinding;

        public MonthViewHolder(ViewCalendarDataBinding dataBinding)
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
