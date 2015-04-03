package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NanumBarunTextView extends TextView
{
	private int mCurMaxLine = 0;

	public NanumBarunTextView(Context context)
	{
		super(context);
	}

	public NanumBarunTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public NanumBarunTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
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
	public void setTypeface(Typeface tf, int style)
	{
		switch (style)
		{
			case Typeface.NORMAL:
				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getNormalTypeface());
				//				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
				//				{
				//					int flags = getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG;
				//
				//					setPaintFlags(flags);
				//				}
				break;

			case Typeface.BOLD:
				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getBoldTypeface());
				//
				//				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
				//				{
				//					int flags = getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG;
				//
				//					setPaintFlags(flags | Paint.FAKE_BOLD_TEXT_FLAG);
				//				}
				break;
			case Typeface.ITALIC:
				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getNormalTypeface());
				//				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getIM());
				//
				//				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
				//				{
				//					int flags = getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG;
				//
				//					setPaintFlags(flags);
				//				}
				break;
			case Typeface.BOLD_ITALIC:
				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getBoldTypeface());
				//				setTypeface(FontManager.getInstance(getContext().getApplicationContext()).getBIM());
				//
				//				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
				//				{
				//					int flags = getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG;
				//
				//					setPaintFlags(flags | Paint.FAKE_BOLD_TEXT_FLAG);
				//				}
				break;
		}
	}
}
