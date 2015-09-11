package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class DailyCustomFontButton extends AppCompatButton
{
	public DailyCustomFontButton(Context context)
	{
		super(context);
	}

	public DailyCustomFontButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DailyCustomFontButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
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
