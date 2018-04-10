package com.daily.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

    @SuppressLint("RestrictedApi")
    public void setVectorImageResource(@DrawableRes int resId)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            super.setImageResource(resId);
        } else
        {
            Drawable drawable = resId == 0 ? null : AppCompatDrawableManager.get().getDrawable(getContext(), resId);

            super.setImageDrawable(drawable);
        }
    }
}
