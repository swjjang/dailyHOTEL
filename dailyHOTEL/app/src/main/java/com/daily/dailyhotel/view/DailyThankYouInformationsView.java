package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewThankYouInformationDataBinding;

public class DailyThankYouInformationsView extends ConstraintLayout
{
    private DailyViewThankYouInformationDataBinding mViewDataBinding;

    public DailyThankYouInformationsView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyThankYouInformationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyThankYouInformationsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setBackgroundResource(R.drawable.thankyou_receipt);

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_thank_you_information_data, this, true);

        setNoticeVisible(false);
    }

    public void setMessageText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.messageTextView.setText(text);
    }

    public void setMessageText(@StringRes int resid)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.messageTextView.setText(resid);
    }

    public void setNoticeVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.noticeLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setNoticeText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.noticeTextView.setText(text);
    }

    public void setDate1Text(CharSequence title, CharSequence dateString)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateInformationView.setDate1Text(title, dateString);
    }

    public void setDate2Text(CharSequence title, CharSequence dateString)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateInformationView.setDate2Text(title, dateString);
    }

    public void setCenterNightsVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateInformationView.setCenterNightsVisible(visible);
    }

    public void setCenterNightsText(CharSequence nights)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateInformationView.setCenterNightsText(nights);
    }

    public void setReservationTitle(CharSequence title)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.reservationInformationView.setTitle(title);
    }

    public void setReservationTitle(@StringRes int resid)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.reservationInformationView.setTitle(resid);
    }

    public void removeAllReservationInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.reservationInformationView.removeAllInformation();
    }

    public void addReservationInformation(String title, String description)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.reservationInformationView.addInformation(title, description);
    }
}
