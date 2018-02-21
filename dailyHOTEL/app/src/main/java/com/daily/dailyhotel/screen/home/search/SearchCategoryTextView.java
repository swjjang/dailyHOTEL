package com.daily.dailyhotel.screen.home.search;

import android.content.Context;
import android.util.AttributeSet;

import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyTextView;

public class SearchCategoryTextView extends DailyTextView
{
    public SearchCategoryTextView(Context context)
    {
        super(context);
    }

    public SearchCategoryTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SearchCategoryTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);

        if (selected == true)
        {
            setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        } else
        {
            setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        }
    }
}
