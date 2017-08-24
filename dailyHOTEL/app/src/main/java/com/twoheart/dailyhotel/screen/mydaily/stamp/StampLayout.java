package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daily.base.widget.DailyButton;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

public class StampLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mStampEventMessageTextView;
    private TextView mNightsTextView;
    private View mPushLayout, mStampHistoryTextView;
    private View mFreePassLayout, mFreePassCouponLayout;
    private TextView mFreePassCoupon01TextView, mFreePassCoupon02TextView;
    private TextView mFreePassTextView;

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

        mPushLayout = view.findViewById(R.id.pushLayout);

        mFreePassLayout = view.findViewById(R.id.freePassLayout);
        mFreePassTextView = (TextView) view.findViewById(R.id.freePassTextView);
        mFreePassCouponLayout = mFreePassLayout.findViewById(R.id.freePassCouponLayout);
        mFreePassCoupon01TextView = (TextView) mFreePassCouponLayout.findViewById(R.id.freePassCoupon01TextView);
        mFreePassCoupon02TextView = (TextView) mFreePassCouponLayout.findViewById(R.id.freePassCoupon02TextView);

        View stampMessageLayout = view.findViewById(R.id.stampMessageLayout);
        View pushMessageLayout = mPushLayout.findViewById(R.id.pushMessageLayout);

        stampMessageLayout.setOnClickListener(this);
        pushMessageLayout.setOnClickListener(this);

        TextView stampMessage03TextView = (TextView) stampMessageLayout.findViewById(R.id.stampMessage03TextView);
        SpannableString stampMessage03SpannableString = new SpannableString(mContext.getString(R.string.message_stamp_explanation3));
        stampMessage03SpannableString.setSpan(new UnderlineSpan(), 0, stampMessage03SpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stampMessage03TextView.setText(stampMessage03SpannableString);

        TextView pushMessage02TextView = (TextView) pushMessageLayout.findViewById(R.id.pushMessage02TextView);
        SpannableString pushMessage02SpannableString = new SpannableString(mContext.getString(R.string.label_stamp_notification_on_setting));
        pushMessage02SpannableString.setSpan(new UnderlineSpan(), 0, pushMessage02SpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pushMessage02TextView.setText(pushMessage02SpannableString);

        mStampHistoryTextView = view.findViewById(R.id.stampHistoryTextView);
        View stampTermsTextView = view.findViewById(R.id.stampTermsTextView);

        mStampHistoryTextView.setOnClickListener(this);
        stampTermsTextView.setOnClickListener(this);
    }

    private void initToolbar(View view)
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.label_stamp_mydaily_title);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
            mFreePassLayout.setVisibility(View.VISIBLE);
        } else
        {
            loginView.setVisibility(View.VISIBLE);
            loginView.setOnClickListener(this);
            mNightsTextView.setVisibility(View.GONE);
            stampBottomLayout.setVisibility(View.GONE);
            mFreePassLayout.setVisibility(View.GONE);
        }
    }

    public void setPushLayout(boolean enabled)
    {
        if (enabled == true)
        {
            mPushLayout.setVisibility(View.VISIBLE);
        } else
        {
            mPushLayout.setVisibility(View.GONE);
        }
    }

    public void setStampDate(String date1)
    {
        TextView stampDate01TextView = (TextView) mRootView.findViewById(R.id.stampDate01TextView);

        stampDate01TextView.setText(date1);
    }

    public void setNights(int nights)
    {
        DailyButton nights01TextView = (DailyButton) mRootView.findViewById(R.id.nights01TextView);
        DailyButton nights02TextView = (DailyButton) mRootView.findViewById(R.id.nights02TextView);
        DailyButton nights03TextView = (DailyButton) mRootView.findViewById(R.id.nights03TextView);
        DailyButton nights04TextView = (DailyButton) mRootView.findViewById(R.id.nights04TextView);
        DailyButton nights05TextView = (DailyButton) mRootView.findViewById(R.id.nights05TextView);
        DailyButton nights06TextView = (DailyButton) mRootView.findViewById(R.id.nights06TextView);
        FrameLayout nights07TextView = (FrameLayout) mRootView.findViewById(R.id.nights07TextView);

        if (nights >= 1)
        {
            nights01TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_01);
            nights01TextView.setText(null);
        }

        if (nights >= 2)
        {
            nights02TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_02);
            nights02TextView.setText(null);
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
            nights05TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_05);
            nights05TextView.setText(null);
        }

        if (nights >= 6)
        {
            nights06TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_06);
            nights06TextView.setText(null);
        }

        if (nights >= 7)
        {
            nights07TextView.removeAllViews();
            nights07TextView.setBackgroundResource(R.drawable.ic_stamp_achieved_07);
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

            case 6:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message6);
                break;

            case 7:
                mStampEventMessageTextView.setText(R.string.message_stamp_title_message7);
                break;

            default:
                mStampEventMessageTextView.setText(R.string.message_stamp_login);
                break;
        }
    }

    public void setStampHistoryEnabled(boolean enabled)
    {
        if (mStampHistoryTextView == null)
        {
            return;
        }

        mStampHistoryTextView.setEnabled(enabled);
    }

    public void setStampFreePassEnabled(boolean enabled)
    {
        if (mFreePassCouponLayout == null)
        {
            return;
        }

        if (enabled == true)
        {
            mFreePassTextView.setText(R.string.message_stamp_completed_issuance);
            mFreePassCouponLayout.setBackgroundResource(R.drawable.ic_stamp_coupon_active);

            mFreePassCoupon01TextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            mFreePassCoupon02TextView.setText(R.string.label_stamp_completed_issuance);
            mFreePassCoupon02TextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c666666));
        } else
        {
            mFreePassTextView.setText(R.string.message_stamp_before_issuance);
            mFreePassCouponLayout.setBackgroundResource(R.drawable.ic_stamp_coupon_inactive);

            mFreePassCoupon01TextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
            mFreePassCoupon02TextView.setText(R.string.label_stamp_before_issuance);
            mFreePassCoupon02TextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c929292));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
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