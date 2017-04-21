package com.daily.base.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.daily.base.util.VersionUtils;

public class DailyImageView extends AppCompatImageView
{
    public DailyImageView(Context context)
    {
        super(context);
    }

    public DailyImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setVectorImageResource(@DrawableRes int resId)
    {
        super.setImageResource(resId);

        if (VersionUtils.isOverAPI21() == true)
        {
            super.setImageResource(resId);
        } else
        {
            if (resId > 0)
            {
                super.setImageDrawable(AppCompatDrawableManager.get().getDrawable(getContext(), resId));
            }
        }
    }
}