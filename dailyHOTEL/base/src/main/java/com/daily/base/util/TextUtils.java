package com.daily.base.util;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.daily.base.R;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class TextUtils
{
    public static final int PASSWORD_MIN_COUNT = 8;

    public static boolean isNameCharacter(String text)
    {
        boolean result = false;

        if (com.daily.base.util.TextUtils.isTextEmpty(text) == false)
        {
            result = Pattern.matches("^[a-zA-Z\\s.'-]+$", text);
        }

        return result;
    }

    public static boolean isTextEmpty(String... texts)
    {
        if (texts == null)
        {
            return true;
        }

        for (String text : texts)
        {
            if ((android.text.TextUtils.isEmpty(text) == true || "null".equalsIgnoreCase(text) == true || text.trim().length() == 0) == true)
            {
                return true;
            }
        }

        return false;
    }

    public static void clipText(Context context, String text)
    {
        if (VersionUtils.isOverAPI11() == true)
        {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        } else
        {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        }
    }

    public static String getPriceFormat(Context context, int price, boolean isPrefixType)
    {
        if (isPrefixType == true)
        {
            DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.label_currency_format_prefix));
            return decimalFormat.format(price);
        } else
        {
            DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.label_currency_format_postfix));
            return decimalFormat.format(price);
        }
    }

    /**
     * String value 값 중 "true", "1", "Y", "y" 값을 true로 바꿔 주는 메소드
     *
     * @param value
     * @return boolean value
     */
    public static boolean parseBoolean(String value)
    {
        if (isTextEmpty(value) == true)
        {
            return false;
        }

        value = value.toLowerCase();

        if ("true".equalsIgnoreCase(value))
        {
            return true;
        } else if ("1".equalsIgnoreCase(value))
        {
            return true;
        } else if ("Y".equalsIgnoreCase(value))
        {
            return true;
        }

        return false;
    }

    public static float getTextWidth(Context context, String text, double dp, Typeface typeface)
    {
        return getScaleTextWidth(context, text, dp, 1.0f, typeface);
    }

    public static float getScaleTextWidth(Context context, String text, double dp, float scaleX, Typeface typeface)
    {
        if (context == null || isTextEmpty(text))
        {
            return 0;
        }

        Paint p = new Paint();

        float size = ScreenUtils.dpToPx(context, dp);
        p.setTextSize(size);
        p.setTypeface(typeface);
        p.setTextScaleX(scaleX);

        float width = p.measureText(text);

        p.reset();
        return width;
    }

    /**
     * textView에 텍스트가 들어가 있어야 한다.
     *
     * @param textView
     * @param textViewWidth
     * @return
     */
    public static float getTextViewHeight(TextView textView, int textViewWidth)
    {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public static String trim(String text)
    {
        if (com.daily.base.util.TextUtils.isTextEmpty(text) == true)
        {
            return text;
        }

        int length = text.length();
        int index = 0;

        while ((index < length) && (text.charAt(index) <= ' '))
        {
            index++;
        }
        while ((index < length) && (text.charAt(length - 1) <= ' '))
        {
            length--;
        }

        return ((index > 0) || (length < text.length())) ? text.substring(index, length) : text;
    }

    public static boolean verifyPassword(String email, @NonNull final String password)
    {
        // 둘중에 한개라도 없으면 안됨.
        if (isTextEmpty(password) == true)
        {
            return false;
        }

        int length = password.length();

        if (length < PASSWORD_MIN_COUNT)
        {
            return false;
        }

        if (length == PASSWORD_MIN_COUNT)
        {
            boolean oneCharacterVerified = false;

            // 8자이면서 한개의 영문(대소문자 구분)이나 숫자, 특수문자로만 입력된 경우
            for (int i = 1; i < length; i++)
            {
                if (password.charAt(0) != password.charAt(i))
                {
                    oneCharacterVerified = true;
                    break;
                }
            }

            if (oneCharacterVerified == false)
            {
                return false;
            }

            boolean doubleCharacterVerified = false;

            // 8자이면서 두개의 숫자가 반복적으로 입력된 경우 (12121212, 82828282…)
            for (int i = 2; i < length; i += 2)
            {
                if (password.charAt(0) != password.charAt(i) && password.charAt(1) != password.charAt(i + 1))
                {
                    doubleCharacterVerified = true;
                    break;
                }
            }

            if (doubleCharacterVerified == false)
            {
                return false;
            }
        }

        // 이메일주소와 동일한 경우
        if (password.equalsIgnoreCase(email) == true)
        {
            return false;
        }

        //  특정 문자열의 경우
        final String[] patterns = {"12345678"};

        for (String pattern : patterns)
        {
            if (pattern.equalsIgnoreCase(password) == true)
            {
                return false;
            }
        }

        return true;
    }
}