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

public class DailyTextUtils
{
    public static final int PASSWORD_MIN_COUNT = 8;

    public static boolean isNameCharacter(String text)
    {
        boolean result = false;

        if (DailyTextUtils.isTextEmpty(text) == false)
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
        if (DailyTextUtils.isTextEmpty(text) == true)
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

    private static boolean repeatedCharacters(String text, int length)
    {
        String snippet = text.substring(0, length);
        // 특정 문자열이 반복되는 경우에 대한 정규 표현식 생성 (1111, 1212), arg 길이가 index 의 배수일 때만 유효하게 동작함.
        String regex = String.format("(%s){%d}", snippet, (text.length() / length));
        return Pattern.matches(regex, text);
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
            if (repeatedCharacters(password, 1) == true || repeatedCharacters(password, 2) == true)
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