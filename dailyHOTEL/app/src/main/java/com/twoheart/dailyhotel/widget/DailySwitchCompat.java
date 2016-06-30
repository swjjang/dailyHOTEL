package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

import java.lang.reflect.Method;

public class DailySwitchCompat extends SwitchCompat
{
    private Method getThumbOffset;
    private Method getThumbScrollRange;
    private OnScrollListener mOnScrollListener;

    public interface OnScrollListener
    {
        void onScrolled(int offset, int range);
    }

    public DailySwitchCompat(Context context)
    {
        super(context);

        initReflectionClass();
        setFontStyle(context, null);
    }

    public DailySwitchCompat(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initReflectionClass();
        setFontStyle(context, attrs);
    }

    public DailySwitchCompat(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initReflectionClass();
        setFontStyle(context, attrs);
    }

    private void initReflectionClass()
    {
        Class reflectionClass = SwitchCompat.class;

        try
        {
            getThumbOffset = reflectionClass.getDeclaredMethod("getThumbOffset");
            getThumbOffset.setAccessible(true);

            getThumbScrollRange = reflectionClass.getDeclaredMethod("getThumbScrollRange");
            getThumbScrollRange.setAccessible(true);
        } catch (NoSuchMethodException e)
        {
            ExLog.d(e.toString());
        }
    }

    private void setFontStyle(Context context, AttributeSet attrs)
    {
        int fontStyle = 3;

        Typeface typeface = getTypeface();

        if (typeface != null)
        {
            boolean isBold = getTypeface().isBold();

            if (isBold == true)
            {
                fontStyle = 0;
            } else
            {
                if (attrs != null)
                {
                    fontStyle = context.obtainStyledAttributes(attrs, R.styleable.dailyFont).getInt(R.styleable.dailyFont_style, 3);
                }
            }
        }

        //		 <attr name="fontStyle" >
        //	        <enum name="Bold" value="0" />
        //	        <enum name="DemiLight" value="1" />
        //	        <enum name="Medium" value="2 />
        //	        <enum name="Regular" value="3" />
        //	    </attr>

        switch (fontStyle)
        {
            // Bold
            case 0:
                setTypeface(FontManager.getInstance(context).getBoldTypeface());
                break;

            // DemiLight
            case 1:
                setTypeface(FontManager.getInstance(context).getDemiLightTypeface());
                break;

            // Medium
            case 2:
                setTypeface(FontManager.getInstance(context).getMediumTypeface());
                break;

            // Regular
            case 3:
                setTypeface(FontManager.getInstance(context).getRegularTypeface());
                break;
        }
    }

    @Override
    public void setTypeface(Typeface typeface, int style)
    {
        switch (style)
        {
            case Typeface.NORMAL:
                setTypeface(FontManager.getInstance(getContext()).getRegularTypeface());
                break;
            case Typeface.BOLD:
                setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
                break;
            case Typeface.ITALIC:
                setTypeface(FontManager.getInstance(getContext()).getRegularTypeface());
                break;
            case Typeface.BOLD_ITALIC:
                setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
                break;
        }
    }

    @Override
    public void setTypeface(Typeface typeface)
    {
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        super.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        try
        {
            if (mOnScrollListener != null)
            {
                mOnScrollListener.onScrolled((int) getThumbOffset.invoke(this), (int) getThumbScrollRange.invoke(this));
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void setOnScrollListener(OnScrollListener listener)
    {
        mOnScrollListener = listener;
    }
}
