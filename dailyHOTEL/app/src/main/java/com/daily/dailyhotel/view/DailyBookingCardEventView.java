package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingCardEventDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewBookingCardEventMessageDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewBookingCardEventTitleDataBinding;

import java.util.List;

public class DailyBookingCardEventView extends ConstraintLayout
{
    private DailyViewBookingCardEventDataBinding mViewDataBinding;

    public DailyBookingCardEventView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingCardEventView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingCardEventView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_card_event_data, this, true);
    }

    public void addCardEventView(String title, List<String> messageList)
    {
        if (mViewDataBinding == null || DailyTextUtils.isTextEmpty(title) == true || messageList == null || messageList.size() == 0)
        {
            return;
        }

        DailyViewBookingCardEventTitleDataBinding titleDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.daily_view_booking_card_event_title_data, mViewDataBinding.cardEventLayout, true);
        titleDataBinding.titleTextView.setText(title);

        for (String message : messageList)
        {
            DailyViewBookingCardEventMessageDataBinding messageDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.daily_view_booking_card_event_message_data, mViewDataBinding.cardEventLayout, true);
            messageDataBinding.messageTextView.setText(message);
        }
    }

    public void clearView()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mViewDataBinding.cardEventLayout.getChildCount() > 0)
        {
            mViewDataBinding.cardEventLayout.removeAllViews();
        }
    }
}