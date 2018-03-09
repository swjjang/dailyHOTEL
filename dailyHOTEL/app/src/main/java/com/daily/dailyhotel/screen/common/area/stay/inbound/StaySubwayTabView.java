package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StaySubwayTabView extends LinearLayout implements View.OnClickListener
{
    private OnTabChangeListener mOnTabChangeListener;

    public interface OnTabChangeListener
    {
        void onTabChanged(View view, int position);
    }

    public StaySubwayTabView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public StaySubwayTabView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public StaySubwayTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public StaySubwayTabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setOrientation(HORIZONTAL);
    }

    public void addTab(String text, Object tag)
    {
        if (getChildCount() > 0)
        {
            addView(newDividerView(getContext()));
        }

        View tabView = newTabView(getContext(), text);
        tabView.setTag(tag);
        tabView.setOnClickListener(this);

        addView(tabView);
    }

    public void clearTab()
    {
        if (getChildCount() == 0)
        {
            return;
        }

        removeAllViews();
    }

    public void setSelection(int position)
    {
        if (position < 0)
        {
            return;
        }

        int dividerCount = position;
        int childIndex = position + dividerCount;
        int childCount = getChildCount();

        if (childIndex >= childCount)
        {
            return;
        }

        View childView;

        for (int i = 0; i < childCount; i++)
        {
            childView = getChildAt(i);

            if (childView instanceof DailyTextView)
            {
                childView.setSelected(i == childIndex);
            }
        }
    }

    public void setOnTabChangeListener(OnTabChangeListener listener)
    {
        mOnTabChangeListener = listener;
    }

    private View newTabView(Context context, String text)
    {
        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setText(text);
        dailyTextView.setTextColor(getResources().getColorStateList(R.drawable.selector_text_color_c929292_cb70038));
        dailyTextView.setGravity(Gravity.CENTER);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, 44));
        layoutParams.weight = 1.0f;
        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    private View newDividerView(Context context)
    {
        View view = new View(context);
        view.setBackgroundColor(getResources().getColor(R.color.default_line_cdcdcdd));

        LinearLayout.LayoutParams layoutParams = new LayoutParams(ScreenUtils.dpToPx(context, 1), ScreenUtils.dpToPx(context, 15));
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(layoutParams);

        return view;
    }

    @Override
    public synchronized void onClick(View v)
    {
        int childCount = getChildCount();
        View childView;

        for (int i = 0; i < childCount; i++)
        {
            childView = getChildAt(i);

            if (childView == v)
            {
                childView.setSelected(true);

                if (mOnTabChangeListener != null)
                {
                    mOnTabChangeListener.onTabChanged(v, i / 2);
                }
            } else
            {
                childView.setSelected(false);
            }
        }
    }
}
