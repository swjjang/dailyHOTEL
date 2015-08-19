package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * asset의 폰트를 싱글톤으로 관리하기 위해서 사용. (여기저기서 막 사용하면 메모리 낭비가 심해지므로)
 * 
 * @author Administrator
 */
public class FontManager
{

	private volatile static FontManager instance;

	private Typeface mBlackTypeface;
	private Typeface mBoldTypeface;
	private Typeface mDemiLightTypeface;
	private Typeface mLightTypeface;
	private Typeface mMediumTypeface;
	private Typeface mRegularTypeface;
	private Typeface mThinTypeface;

	public static FontManager getInstance(Context context)
	{
		if (instance == null)
		{
			synchronized (FontManager.class)
			{
				if (instance == null)
				{
					instance = new FontManager(context);
				}
			}
		}

		return instance;
	}

	public FontManager(Context context)
	{
		try
		{
			mBlackTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Black.otf");
		} catch (Exception e)
		{
			mBlackTypeface = Typeface.DEFAULT_BOLD;
		}

		try
		{
			mBoldTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Bold.otf");
		} catch (Exception e)
		{
			mBoldTypeface = Typeface.DEFAULT_BOLD;
		}

		try
		{
			mDemiLightTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-DemiLight.otf");
		} catch (Exception e)
		{
			mDemiLightTypeface = Typeface.DEFAULT;
		}

		try
		{
			mLightTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Light.otf");
		} catch (Exception e)
		{
			mLightTypeface = Typeface.DEFAULT;
		}

		try
		{
			mMediumTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Medium.otf");
		} catch (Exception e)
		{
			mMediumTypeface = Typeface.DEFAULT;
		}

		try
		{
			mRegularTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Regular.otf");
		} catch (Exception e)
		{
			mRegularTypeface = Typeface.DEFAULT;
		}

		try
		{
			mThinTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Thin.otf");
		} catch (Exception e)
		{
			mThinTypeface = Typeface.DEFAULT;
		}
	}

	public Typeface getBlackTypeface()
	{
		return mBlackTypeface;
	}

	public Typeface getBoldTypeface()
	{
		return mBoldTypeface;
	}

	public Typeface getDemiLightTypeface()
	{
		return mDemiLightTypeface;
	}

	public Typeface getLightTypeface()
	{
		return mLightTypeface;
	}

	public Typeface getMediumTypeface()
	{
		return mMediumTypeface;
	}

	public Typeface getRegularTypeface()
	{
		return mRegularTypeface;
	}

	public Typeface getThinTypeface()
	{
		return mThinTypeface;
	}

	public static void apply(ViewGroup root, Typeface typeface)
	{
		for (int i = 0; i < root.getChildCount(); i++)
		{
			View child = root.getChildAt(i);

			if (child instanceof TextView)
			{
				TextView fontTextView = ((TextView) child);

				fontTextView.setPaintFlags(((TextView) child).getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
				fontTextView.setTypeface(typeface);
				fontTextView.invalidate();
			} else if (child instanceof ViewGroup)
			{
				apply((ViewGroup) child, typeface);
			}
		}
	}

	public static void apply(View view, Typeface typeface)
	{
		if (view instanceof TextView)
		{
			TextView fontTextView = ((TextView) view);

			fontTextView.setPaintFlags(((TextView) view).getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
			fontTextView.setTypeface(typeface);
			fontTextView.invalidate();
		}
	}
}
