package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.twoheart.dailyhotel.R;

public class DailyCountryCodeTextView extends AppCompatTextView
{
    private int mCurMaxLine = 0;

    public DailyCountryCodeTextView(Context context)
    {
        super(context);

        setFontStyle(context, null);
    }

    public DailyCountryCodeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setFontStyle(context, attrs);
    }

    public DailyCountryCodeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setFontStyle(context, attrs);
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
        //	        <enum name="Black" value="0" />
        //	        <enum name="Bold" value="1" />
        //	        <enum name="DemiLight" value="2" />
        //	        <enum name="Light" value="3" />
        //	        <enum name="Medium" value="4" />
        //	        <enum name="Regular" value="5" />
        //	        <enum name="Thin" value="6" />
        //	    </attr>

        switch (fontStyle)
        {
            // Bold
            case 1:
                setTypeface(FontManager.getInstance(context).getBoldTypeface());
                break;

            // DemiLight
            case 2:
                setTypeface(FontManager.getInstance(context).getDemiLightTypeface());
                break;

            // Medium
            case 4:
                setTypeface(FontManager.getInstance(context).getMediumTypeface());
                break;

            // Regular
            case 5:
                setTypeface(FontManager.getInstance(context).getRegularTypeface());
                break;
        }
    }

    public int getCurrentMaxLines()
    {
        return mCurMaxLine;
    }

    @Override
    public void setMaxLines(int maxLines)
    {
        mCurMaxLine = maxLines;
        super.setMaxLines(maxLines);
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
    public void setTextColor(ColorStateList colors)
    {
        super.setTextColor(colors);

        setTypeface(getTypeface(), Typeface.NORMAL);
    }

    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();

        ColorStateList colorStateList = getTextColors();

        int color = colorStateList.getColorForState(getDrawableState(), 0);

        if (color == getResources().getColor(R.color.dh_theme_color))
        {
            setTypeface(getTypeface(), Typeface.BOLD);
        } else
        {
            setTypeface(getTypeface(), Typeface.NORMAL);
        }
    }
}
