package com.twoheart.dailyhotel.widget;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomFontTypefaceSpan extends MetricAffectingSpan
{
    private Typeface mTypeface;

    public CustomFontTypefaceSpan(Typeface type)
    {
        mTypeface = type;
    }

    @Override
    public void updateDrawState(TextPaint textPaint)
    {
        textPaint.setTypeface(mTypeface);
        textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateMeasureState(TextPaint textPaint)
    {
        textPaint.setTypeface(mTypeface);
        textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}