package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;

/**
 * asset의 폰트를 싱글톤으로 관리하기 위해서 사용. (여기저기서 막 사용하면 메모리 낭비가 심해지므로)
 *
 * @author Administrator
 */
public class FontManager
{

    private volatile static FontManager instance;

    private Typeface mBoldTypeface;
    private Typeface mDemiLightTypeface;
    private Typeface mMediumTypeface;
    private Typeface mRegularTypeface;

    public FontManager(Context context)
    {
        try
        {
            mBoldTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Bold.otf");
        } catch (Exception e)
        {
            mBoldTypeface = Typeface.DEFAULT_BOLD;
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
            mMediumTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-Medium.otf");
        } catch (Exception e)
        {
            mMediumTypeface = Typeface.DEFAULT;
        }

        try
        {
            mDemiLightTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSans-DemiLight.otf");
        } catch (Exception e)
        {
            mDemiLightTypeface = Typeface.DEFAULT;
        }
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

    public Typeface getBoldTypeface()
    {
        return mBoldTypeface;
    }

    public Typeface getDemiLightTypeface()
    {
        return mDemiLightTypeface;
    }

    public Typeface getMediumTypeface()
    {
        return mMediumTypeface;
    }

    public Typeface getRegularTypeface()
    {
        return mRegularTypeface;
    }
}
