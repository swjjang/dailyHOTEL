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

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.StampHistory;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StampHistoryLayout extends BaseLayout
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

    public void setStampDate(String date1, String date2)
    {
        TextView stampDate01TextView = (TextView) mRootView.findViewById(R.id.stampDate01TextView);
        TextView stampDate02TextView = (TextView) mRootView.findViewById(R.id.stampDate02TextView);

        stampDate01TextView.setText(date1);
        stampDate02TextView.setText(date2);
    }

    public void setHistoryList(List<StampHistory> stampHistoryList)
    {
        if (stampHistoryList == null || stampHistoryList.size() == 0)
        {
            return;
        }

        mStampHistoryLayout.removeAllViews();

        int size = stampHistoryList.size();

        for (int i = 0; i < size; i++)
        {
            mStampHistoryLayout.addView(addHistory(size - i, stampHistoryList.get(i), mStampHistoryLayout));
        }
    }

    private View addHistory(int order, final StampHistory stampHistory, ViewGroup viewGroup)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_stamp_history, viewGroup, false);

        TextView stayNameTextView = (TextView) view.findViewById(R.id.stayNameTextView);
        TextView stampDateTextView = (TextView) view.findViewById(R.id.stampDateTextView);
        TextView bookingGoTextView = (TextView) view.findViewById(R.id.bookingGoTextView);

        stayNameTextView.setText(stampHistory.reservationName);

        try
        {
            stampDateTextView.setText(mContext.getString(R.string.label_stamphistory_date, DailyCalendar.convertDateFormatString(stampHistory.publishedAt, "yyyy-MM-dd", "yyyy.MM.dd(EEE)")));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        SpannableString spannableString = new SpannableString(mContext.getString(R.string.label_stamp_view_booking_detail));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookingGoTextView.setText(spannableString);

        ImageView stampImageView = (ImageView) view.findViewById(R.id.stampImageView);

        switch (order)
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

            case 6:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_06);
                break;

            case 7:
                stampImageView.setImageResource(R.drawable.ic_stamp_achieved_07);
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
}