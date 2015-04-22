package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Typeface;

import com.twoheart.dailyhotel.DailyHotel;

/**
 * asset의 폰트를 싱글톤으로 관리하기 위해서 사용. (여기저기서 막 사용하면 메모리 낭비가 심해지므로)
 * 
 * @author Administrator
 */
public class FontManager
{

	private volatile static FontManager instance;

	private Typeface mTypeface = null;
	private Typeface mBoldTypeface = null;

	//	private Typeface mTypefaceDroidFallbackIM = null;
	//	private Typeface mTypefaceDroidFallbackBIM = null;

	public FontManager(Context context)
	{
		if (mTypeface == null)
		{
			//			mTypeface = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic.ttf");
			mTypeface = DailyHotel.getTypeface();
		}

		if (mBoldTypeface == null)
		{
			//			mBoldTypeface = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.ttf");
			mBoldTypeface = DailyHotel.getBoldTypeface();
		}

		//		if (mTypefaceDroidFallbackBIM == null)
		//		{
		//			mTypefaceDroidFallbackBIM = Typeface.createFromAsset(context.getAssets(), "NotoSans-BoldItalic.ttf");
		//		}
		//
		//		if (mTypefaceDroidFallbackIM == null)
		//		{
		//			mTypefaceDroidFallbackIM = Typeface.createFromAsset(context.getAssets(), "NotoSans-Italic.ttf");
		//		}
	}

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

	//	public Typeface getIM()
	//	{
	//		return mTypefaceDroidFallbackIM;
	//	}
	//
	//	public Typeface getBIM()
	//	{
	//		return mTypefaceDroidFallbackBIM;
	//	}

	public Typeface getNormalTypeface()
	{
		return mTypeface;
	}

	public Typeface getBoldTypeface()
	{
		return mBoldTypeface;
	}
}
