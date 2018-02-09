package com.daily.base.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

public class DailyImageSpan extends ImageSpan
{
    public static final int ALIGN_VERTICAL_CENTER = 2;

    public DailyImageSpan(Context context, int resourceId, int verticalAlignment)
    {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
    {
        switch (mVerticalAlignment)
        {
            case ALIGN_VERTICAL_CENTER:
                Drawable b = getCachedDrawable();
                canvas.save();

                int transY = (bottom - top - b.getBounds().height()) / 2;

                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
                break;

            default:
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    private Drawable getCachedDrawable()
    {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
        {
            d = wr.get();
        }

        if (d == null)
        {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}