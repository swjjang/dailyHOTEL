package com.daily.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.daily.base.R;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;


public class DailyAutoCompleteEditText extends AppCompatAutoCompleteTextView
{
    private final int DRAWABLE_LEFT = 0;
    private final int DRAWABLE_TOP = 1;
    private final int DRAWABLE_RIGHT = 2;
    private final int DRAWABLE_BOTTOM = 3;

    private boolean mUsedImeActionSend;
    private boolean mHasDeleteButton;

    private OnDeleteTextClickListener mOnDeleteTextClickListener;

    public interface OnDeleteTextClickListener
    {
        void onDelete(DailyAutoCompleteEditText dailyEditText);
    }

    public DailyAutoCompleteEditText(Context context)
    {
        super(context);

        setDrawableCompat(context, null);
        setFontStyle(context, null);

        setUsedImeActionSend(false);
    }

    public DailyAutoCompleteEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setDrawableCompat(context, attrs);
        setFontStyle(context, attrs);

        setUsedImeActionSend(false);
    }

    public DailyAutoCompleteEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setDrawableCompat(context, attrs);
        setFontStyle(context, attrs);

        setUsedImeActionSend(false);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (mHasDeleteButton == true)
        {
            if (isFocused() == true && lengthAfter > 0)
            {
                setDeleteDrawable();
            } else
            {
                Drawable[] drawables = getCompoundDrawables();

                if (drawables == null)
                {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else
                {
                    setCompoundDrawablesWithIntrinsicBounds(drawables[DRAWABLE_LEFT], drawables[DRAWABLE_TOP], null, drawables[DRAWABLE_BOTTOM]);
                }
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (mHasDeleteButton == true)
        {
            if (focused == false)
            {
                Drawable[] drawables = getCompoundDrawables();

                if (drawables == null)
                {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else
                {
                    setCompoundDrawablesWithIntrinsicBounds(drawables[DRAWABLE_LEFT], drawables[DRAWABLE_TOP], drawables[DRAWABLE_RIGHT], drawables[DRAWABLE_BOTTOM]);
                }
            } else
            {
                if (getText().length() > 0)
                {
                    setDeleteDrawable();
                }
            }
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        super.setText(text, type);

        if (mHasDeleteButton == true && text != null && DailyTextUtils.isTextEmpty(text.toString()) == false)
        {
            setDeleteDrawable();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mHasDeleteButton == true)
        {
            int action = event.getAction() & MotionEvent.ACTION_MASK;

            if (action == MotionEvent.ACTION_DOWN)
            {
                Drawable[] drawables = getCompoundDrawables();

                if (drawables == null || drawables[DRAWABLE_RIGHT] == null)
                {
                    return super.onTouchEvent(event);
                }

                int withDrawable = drawables[DRAWABLE_RIGHT].getBounds().width() + getCompoundDrawablePadding();

                if (event.getX() >= (getWidth() - withDrawable))
                {
                    setText(null);

                    if (mOnDeleteTextClickListener != null)
                    {
                        mOnDeleteTextClickListener.onDelete(this);
                    }
                    return false;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private void setDeleteDrawable()
    {
        Drawable[] drawables = getCompoundDrawables();

        if (drawables == null)
        {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vector_ic_delete_circle, 0);
        } else
        {
            Context context = getContext();

            @SuppressLint("RestrictedApi") Drawable rightDrawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.vector_ic_delete_circle);
            setCompoundDrawablesWithIntrinsicBounds(drawables[DRAWABLE_LEFT], drawables[DRAWABLE_TOP], rightDrawable, drawables[DRAWABLE_BOTTOM]);
        }
    }

    public void setDeleteButtonVisible(OnDeleteTextClickListener listener)
    {
        mHasDeleteButton = true;
        mOnDeleteTextClickListener = listener;

        setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 8));
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

    @SuppressLint({"RestrictedApi", "ResourceType"})
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } else
        {
            Context context = getContext();
            Drawable leftDrawable = left == 0 ? null : AppCompatDrawableManager.get().getDrawable(context, left);
            Drawable topDrawable = top == 0 ? null : AppCompatDrawableManager.get().getDrawable(context, top);
            Drawable rightDrawable = right == 0 ? null : AppCompatDrawableManager.get().getDrawable(context, right);
            Drawable bottomDrawable = bottom == 0 ? null : AppCompatDrawableManager.get().getDrawable(context, bottom);

            super.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        }
    }
}
