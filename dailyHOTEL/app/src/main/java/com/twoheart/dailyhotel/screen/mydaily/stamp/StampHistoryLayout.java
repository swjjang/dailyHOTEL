package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.StampHistory;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StampHistoryLayout extends BaseLayout implements View.OnClickListener
{
    private LinearLayout mStampHistoryLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHomeClick();

        void onStampHistoryClick(StampHistory stampHistory);
    }

    public StampHistoryLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mStampHistoryLayout = (LinearLayout) view.findViewById(R.id.stampHistoryLayout);


        View homeButtonView = view.findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onHomeClick();
            }
        });
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_stamp_history), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    public void setStampDate(String date1, String date2, String date3)
    {
        TextView stampDate01TextView = (TextView) mRootView.findViewById(R.id.stampDate01TextView);
        TextView stampDate02TextView = (TextView) mRootView.findViewById(R.id.stampDate02TextView);
        TextView stampDate03TextView = (TextView) mRootView.findViewById(R.id.stampDate03TextView);

        stampDate01TextView.setText(mContext.getString(R.string.label_stamp_event_date1, date1));
        stampDate02TextView.setText(mContext.getString(R.string.label_stamp_event_date2, date2));
        stampDate03TextView.setText(mContext.getString(R.string.label_stamp_event_date3, date3));
    }

    public void setHistoryList(List<StampHistory> stampHistoryList)
    {
        if (stampHistoryList == null || stampHistoryList.size() == 0)
        {
            return;
        }

        mStampHistoryLayout.removeAllViews();

        for (StampHistory stampHistory : stampHistoryList)
        {
            mStampHistoryLayout.addView(addHistory(stampHistory, mStampHistoryLayout));
        }
    }

    private View addHistory(final StampHistory stampHistory, ViewGroup viewGroup)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_stamp_history, viewGroup, false);

        TextView stayNameTextView = (TextView) view.findViewById(R.id.stayNameTextView);
        TextView stampDateTextView = (TextView) view.findViewById(R.id.stampDateTextView);
        TextView bookingGoTextView = (TextView) view.findViewById(R.id.bookingGoTextView);

        stayNameTextView.setText(stampHistory.placeName);

        try
        {
            stampDateTextView.setText(mContext.getString(R.string.label_stamphistory_date, DailyCalendar.convertDateFormatString(stampHistory.date, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        SpannableString spannableString = new SpannableString(mContext.getString(R.string.label_stamp_view_booking_detail));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookingGoTextView.setText(spannableString);

        ImageView stampImageView = (ImageView) view.findViewById(R.id.stampImageView);

        switch (stampHistory.nights)
        {
            case 1:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_01);
                break;

            case 2:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_02);
                break;

            case 3:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_03);
                break;

            case 4:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_04);
                break;

            case 5:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_05);
                break;
        }

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onStampHistoryClick(stampHistory);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        }
    }
}