package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewStayListNavigationBarDataBinding;

public class DailyStayListNavigationBarView extends android.support.constraint.ConstraintLayout
{
    private DailyViewStayListNavigationBarDataBinding mViewDataBinding;

    public DailyStayListNavigationBarView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyStayListNavigationBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyStayListNavigationBarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_list_navigation_bar_data, this, true);

        final int DP_8 = ScreenUtils.dpToPx(context, 8);
        setPadding(DP_8, DP_8, DP_8, DP_8);
    }

    public void setRegionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.regionTextView.setText(text);
    }

    public void setDateText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            mViewDataBinding.dateTextView.setText(text);
            return;
        }

        int viewWidth = mViewDataBinding.dateTextView.getWidth() - mViewDataBinding.dateTextView.getCompoundDrawablePadding() * 2//
            - mViewDataBinding.dateTextView.getCompoundDrawables()[0].getIntrinsicWidth() - mViewDataBinding.dateTextView.getCompoundDrawables()[2].getIntrinsicWidth();

        final Typeface typeface = FontManager.getInstance(getContext()).getRegularTypeface();
        final float width = DailyTextUtils.getTextWidth(getContext(), text, 12d, typeface);

        if (viewWidth > width)
        {
            mViewDataBinding.dateTextView.setText(text);
        } else
        {
            float scaleX = 1f;
            float scaleWidth;

            for (int i = 99; i >= 60; i--)
            {
                scaleX = (float) i / 100;
                scaleWidth = DailyTextUtils.getScaleTextWidth(getContext(), text, 12d, scaleX, typeface);

                if (viewWidth > scaleWidth)
                {
                    break;
                }
            }

            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ScaleXSpan(scaleX), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mViewDataBinding.dateTextView.setText(spannableString);
        }
    }

    public void setOnRegionClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.regionLayout.setOnClickListener(listener);
    }

    public void setOnDateClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateLayout.setOnClickListener(listener);
    }
}
