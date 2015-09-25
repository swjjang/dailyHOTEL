package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class DailyEditText extends AppCompatEditText
{
    private boolean mUsedImeActionSend;

    public DailyEditText(Context context)
    {
        super(context);

        setUsedImeActionSend(false);
    }

    public DailyEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setUsedImeActionSend(false);
    }

    public DailyEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setUsedImeActionSend(false);
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
}
