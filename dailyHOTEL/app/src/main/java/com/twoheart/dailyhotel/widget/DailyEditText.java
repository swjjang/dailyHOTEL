package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyEditText extends AppCompatEditText
{
    private boolean mUsedImeActionSend;
    private boolean mHasDeleteButton;

    public DailyEditText(Context context)
    {
        super(context);

        setDrawableCompat(context, null);
        setFontStyle(context, null);

        setUsedImeActionSend(false);
    }

    public DailyEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setDrawableCompat(context, attrs);
        setFontStyle(context, attrs);

        setUsedImeActionSend(false);
    }

    public DailyEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setDrawableCompat(context, attrs);
        setFontStyle(context, attrs);

        setUsedImeActionSend(false);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (mHasDeleteButton == true)
        {
            if (focused == true)
            {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_ic_01_delete, 0);
            } else
            {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mHasDeleteButton == true)
        {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Drawable[] drawables = getCompoundDrawables();

                if (drawables == null || drawables[DRAWABLE_RIGHT] == null)
                {
                    return super.onTouchEvent(event);
                }

                int withDrawable = drawables[DRAWABLE_RIGHT].getBounds().width() + getCompoundDrawablePadding();

                if (event.getRawX() >= (getRight() - withDrawable))
                {
                    setText(null);
                    return false;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void setDeleteButtonVisible(boolean visible)
    {
        mHasDeleteButton = visible;
    }

    private void setDrawableCompat(Context context, AttributeSet attrs)
    {
        if (context == null || attrs == null)
        {
            return;
        }

        int drawableCompatLeftResId = context.obtainStyledAttributes(attrs, R.styleable.app).getResourceId(R.styleable.app_drawableCompatLeft, 0);
        int drawableCompatTopResId = context.obtainStyledAttributes(attrs, R.styleable.app).getResourceId(R.styleable.app_drawableCompatTop, 0);
        int drawableCompatRightResId = context.obtainStyledAttributes(attrs, R.styleable.app).getResourceId(R.styleable.app_drawableCompatRight, 0);
        int drawableCompatBottomResId = context.obtainStyledAttributes(attrs, R.styleable.app).getResourceId(R.styleable.app_drawableCompatBottom, 0);

        if (drawableCompatLeftResId == 0 && drawableCompatTopResId == 0 && drawableCompatRightResId == 0 && drawableCompatBottomResId == 0)
        {
            return;
        }

        setCompoundDrawablesWithIntrinsicBounds(drawableCompatLeftResId, drawableCompatTopResId, drawableCompatRightResId, drawableCompatBottomResId);
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

    public void setUsedImeActionSend(boolean used)
    {
        mUsedImeActionSend = used;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        if (mUsedImeActionSend == false)
        {
            return super.onCreateInputConnection(outAttrs);
        }

        InputConnection connection = super.onCreateInputConnection(outAttrs);
        int imeActions = outAttrs.imeOptions & EditorInfo.IME_MASK_ACTION;
        if ((imeActions & EditorInfo.IME_ACTION_SEND) != 0)
        {
            // clear the existing action
            outAttrs.imeOptions ^= imeActions;
            // set the DONE action
            outAttrs.imeOptions |= EditorInfo.IME_ACTION_SEND;
        }
        if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0)
        {
            outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return connection;
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
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom)
    {
        if (Util.isOverAPI21() == true)
        {
            super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } else
        {
            Context context = getContext();
            Drawable leftDrawable = null;
            Drawable topDrawable = null;
            Drawable rightDrawable = null;
            Drawable bottomDrawable = null;

            if (left > 0)
            {
                leftDrawable = AppCompatDrawableManager.get().getDrawable(context, left);
            }

            if (top > 0)
            {
                topDrawable = AppCompatDrawableManager.get().getDrawable(context, top);
            }

            if (right > 0)
            {
                rightDrawable = AppCompatDrawableManager.get().getDrawable(context, right);
            }

            if (bottom > 0)
            {
                bottomDrawable = AppCompatDrawableManager.get().getDrawable(context, bottom);
            }

            super.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        }
    }

    public void setDrawableVectorTintList(int id)
    {
        Drawable[] drawables = getCompoundDrawables();

        if (drawables == null)
        {
            return;
        }

        for (Drawable drawable : drawables)
        {
            if (drawable == null)
            {
                continue;
            }

            if (drawable instanceof VectorDrawableCompat)
            {
                ((VectorDrawableCompat) drawable).setTintList(getResources().getColorStateList(id));
            } else
            {
                if (Util.isOverAPI21() == true)
                {
                    drawable.setTintList(getResources().getColorStateList(id));
                }
            }
        }
    }

    public void setDrawableVectorTint(int id)
    {
        Drawable[] drawables = getCompoundDrawables();

        if (drawables == null)
        {
            return;
        }

        for (Drawable drawable : drawables)
        {
            if (drawable == null)
            {
                continue;
            }

            if (drawable instanceof VectorDrawableCompat)
            {
                ((VectorDrawableCompat) drawable).setTint(getResources().getColor(id));
            } else
            {
                if (Util.isOverAPI21() == true)
                {
                    drawable.setTint(getResources().getColor(id));
                }
            }
        }
    }
}
