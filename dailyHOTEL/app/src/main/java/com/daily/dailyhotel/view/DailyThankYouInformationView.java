package com.daily.dailyhotel.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;

import java.util.List;

public class DailyThankYouInformationView extends ConstraintLayout
{
    private DailyTextView mMessageTextView;
    private View mNoticeLayout;
    private DailyTextView mNoticeTextView;
    private DailyDateInformationView mDailyDateInformationView;
    private DailyBookingInformationView mReservationInformationView;

    public DailyThankYouInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyThankYouInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyThankYouInformationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setBackgroundResource(R.drawable.thankyou_receipt);

        View view = LayoutInflater.from(context).inflate(R.layout.daily_view_thank_you_information, this, true);

        mMessageTextView = (DailyTextView) view.findViewById(R.id.messageTextView);
        mNoticeLayout = view.findViewById(R.id.noticeLayout);
        mNoticeTextView = (DailyTextView) mNoticeLayout.findViewById(R.id.noticeTextView);
        mDailyDateInformationView = (DailyDateInformationView) view.findViewById(R.id.dateInformationView);
        mReservationInformationView = (DailyBookingInformationView) view.findViewById(R.id.reservationInformationView);

        setNoticeVisible(false);
    }

    public void setMessageText(CharSequence text)
    {
        if (mMessageTextView == null)
        {
            return;
        }

        mMessageTextView.setText(text);
    }

    public void setMessageText(@StringRes int resid)
    {
        if (mMessageTextView == null)
        {
            return;
        }

        mMessageTextView.setText(resid);
    }

    public void setNoticeVisible(boolean visible)
    {
        if (mNoticeLayout == null)
        {
            return;
        }

        mNoticeLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setNoticeText(CharSequence text)
    {
        if (mNoticeTextView == null)
        {
            return;
        }

        mNoticeTextView.setText(text);
    }

    public void setDate1Text(CharSequence title, CharSequence dateString)
    {
        if (mDailyDateInformationView == null)
        {
            return;
        }

        mDailyDateInformationView.setDate1Text(title, dateString);
    }

    public void setDate2Text(CharSequence title, CharSequence dateString)
    {
        if (mDailyDateInformationView == null)
        {
            return;
        }

        mDailyDateInformationView.setDate2Text(title, dateString);
    }

    public void setCenterNightsVisible(boolean visible)
    {
        if (mDailyDateInformationView == null)
        {
            return;
        }

        mDailyDateInformationView.setCenterNightsVisible(visible);
    }

    public void setCenterNightsText(CharSequence nights)
    {
        if (mDailyDateInformationView == null)
        {
            return;
        }

        mDailyDateInformationView.setCenterNightsText(nights);
    }

    public void setReservationTitle(CharSequence title)
    {
        if (mReservationInformationView == null)
        {
            return;
        }

        mReservationInformationView.setTitle(title);
    }

    public void setReservationTitle(@StringRes int resid)
    {
        if (mReservationInformationView == null)
        {
            return;
        }

        mReservationInformationView.setTitle(resid);
    }

    public void setReservationInformation(List<Pair<CharSequence, CharSequence>> informationList)
    {
        if (mReservationInformationView == null)
        {
            return;
        }

        if (informationList == null || informationList.size() == 0)
        {
            return;
        }

        mReservationInformationView.setInformation(informationList);
    }
}
