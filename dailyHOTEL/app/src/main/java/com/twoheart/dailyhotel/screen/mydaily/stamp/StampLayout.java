package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyButton;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StampLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mStampEventMessageTextView;
    private TextView mNightsTextView;
    private View mPushLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onLoginClick();

        void onStampEventDetailClick();

        void onSettingPushClick();

        void onStampHistoryClick();

        void onStampTermsClick();
    }

    public StampLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mStampEventMessageTextView = (TextView) view.findViewById(R.id.stampEventMessageTextView);
        mStampEventMessageTextView.setOnClickListener(this);

        mPushLayout = view.findViewById(R.id.pushLayout);

        View stampMessageLayout = view.findViewById(R.id.stampMessageLayout);
        View pushMessageLayout = mPushLayout.findViewById(R.id.pushMessageLayout);

        stampMessageLayout.setOnClickListener(this);
        pushMessageLayout.setOnClickListener(this);

        View stampHistoryTextView = view.findViewById(R.id.stampHistoryTextView);
        View stampTermsTextView = view.findViewById(R.id.stampTermsTextView);

        stampHistoryTextView.setOnClickListener(this);
        stampTermsTextView.setOnClickListener(this);
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_stamp), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    public void setLogin(boolean login)
    {
        View loginView = mRootView.findViewById(R.id.loginView);
        mNightsTextView = (TextView) mRootView.findViewById(R.id.nightsTextView);

        View stampBottomLayout = mRootView.findViewById(R.id.stampBottomLayout);

        if (login == true)
        {
            loginView.setVisibility(View.GONE);
            mNightsTextView.setVisibility(View.VISIBLE);
            stampBottomLayout.setVisibility(View.VISIBLE);
        } else
        {
            loginView.setVisibility(View.VISIBLE);
            loginView.setOnClickListener(this);
            mNightsTextView.setVisibility(View.GONE);
            stampBottomLayout.setVisibility(View.GONE);
        }
    }

    public void setPushLayout(boolean enabled)
    {
        if(enabled == true)
        {
            mPushLayout.setVisibility(View.VISIBLE);
        } else
        {
            mPushLayout.setVisibility(View.GONE);
        }
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

    public void setNights(int nights)
    {
        DailyButton nights01TextView = (DailyButton) mRootView.findViewById(R.id.nights01TextView);
        FrameLayout nights02TextView = (FrameLayout) mRootView.findViewById(R.id.nights02TextView);
        DailyButton nights03TextView = (DailyButton) mRootView.findViewById(R.id.nights03TextView);
        DailyButton nights04TextView = (DailyButton) mRootView.findViewById(R.id.nights04TextView);
        FrameLayout nights05TextView = (FrameLayout) mRootView.findViewById(R.id.nights05TextView);

        if (nights >= 1)
        {
            nights01TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_01);
            nights01TextView.setText(null);
        }

        if (nights >= 2)
        {
            nights02TextView.removeAllViews();
            nights02TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_02);
        }

        if (nights >= 3)
        {
            nights03TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_03);
            nights03TextView.setText(null);
        }

        if (nights >= 4)
        {
            nights04TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_04);
            nights04TextView.setText(null);
        }

        if (nights >= 5)
        {
            nights05TextView.removeAllViews();
            nights05TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_05);
        }

        mNightsTextView.setText(mContext.getString(R.string.label_stamp_nights, nights));

        switch (nights)
        {
            case 0:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message0);
                break;

            case 1:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message1);
                break;

            case 2:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message2);
                break;

            case 3:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message3);
                break;

            case 4:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message4);
                break;

            case 5:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message5);
                break;
        }
    }

    private int test;

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.stampEventMessageTextView:
                setNights(test++ % 6);
                break;

            case R.id.loginView:
                ((OnEventListener) mOnEventListener).onLoginClick();
                break;

            case R.id.stampMessageLayout:
                ((OnEventListener) mOnEventListener).onStampEventDetailClick();
                break;

            case R.id.pushMessageLayout:
                ((OnEventListener) mOnEventListener).onSettingPushClick();
                break;

            case R.id.stampHistoryTextView:
                ((OnEventListener) mOnEventListener).onStampHistoryClick();
                break;

            case R.id.stampTermsTextView:
                ((OnEventListener) mOnEventListener).onStampTermsClick();
                break;
        }
    }
}