package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class DailyCustomFontTextView extends TextView
{
	private int mCurMaxLine = 0;

	public DailyCustomFontTextView(Context context)
	{
		super(context);
	}

	public DailyCustomFontTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DailyCustomFontTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public DailyCustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public int getCurrentMaxLines()
	{
		return mCurMaxLine;
	}

	@Override
	public void setMaxLines(int maxlines)
	{
		mCurMaxLine = maxlines;
		super.setMaxLines(maxlines);
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
}
