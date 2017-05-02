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

public class StayCalendarView extends PlaceCalendarView<StayCalendarView.OnEventListener, ActivityCalendarDataBinding> implements StayCalendarViewInterface
{
    public interface OnEventListener extends PlaceCalendarView.OnEventListener
    {
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

    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    @Override
    public void onClick(View v)
    {

    }
}
