package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.regex.Pattern;

/**
 * @author hyogij@gmail.com
 * @description : Inputfilter class to constrain the EditText changes
 */
public class StringFilter
{
    private static final int ALLOW_ALPHANUMERIC = 0;
    private static final int ALLOW_ALPHANUMERIC_HANGUL = 1;
    private static final int ALLOW_ALPHANUMERIC_NAME = 2;
    private static final int ALLOW_NUMERIC = 3;
    private static final int ALLOW_SEARCH_FILTER = 4;
    private static final int ALLOW_REGISTER_COUPON_FILTER = 5;

    private Context mContext;
    // Allows only alphanumeric characters. Filters special and hangul
    // characters.
    public InputFilter allowAlphanumeric = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_ALPHANUMERIC);
        }
    };
    public InputFilter allowAlphanumericName = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_ALPHANUMERIC_NAME);
        }
    };
    // Allows only alphanumeric and hangul characters. Filters special
    // characters.
    public InputFilter allowAlphanumericHangul = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_ALPHANUMERIC_HANGUL);
        }
    };
    public InputFilter allowNumeric = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_NUMERIC);
        }
    };

    public InputFilter allowSearchFilter = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_SEARCH_FILTER);
        }
    };

    public InputFilter allowRegisterCouponFilter = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            return filteredString(source, start, end, ALLOW_REGISTER_COUPON_FILTER);
        }
    };

    public StringFilter(Context context)
    {
        mContext = context;
    }

    // Returns the string result which is filtered by the given mode
    private CharSequence filteredString(CharSequence source, int start, int end, int mode)
    {
        Pattern pattern;
        switch (mode)
        {
            case ALLOW_REGISTER_COUPON_FILTER:
            case ALLOW_ALPHANUMERIC_HANGUL:
                pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]+$");
                break;

            case ALLOW_ALPHANUMERIC_NAME:
                pattern = Pattern.compile("^[a-zA-Z\\s.'-]+$");
                break;

            case ALLOW_NUMERIC:
                pattern = Pattern.compile("^[0-9]+$");
                break;

            case ALLOW_SEARCH_FILTER:
                pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55\u002b\u002c\u002d\u002e\u002f\u0040\u0026]+$");
                break;

            case ALLOW_ALPHANUMERIC:
            default:
                pattern = Pattern.compile("^[a-zA-Z0-9\\s]+$");
                break;
        }

        boolean keepOriginal = true;
        StringBuilder stringBuilder = new StringBuilder(end - start);
        for (int i = start; i < end; i++)
        {
            char c = source.charAt(i);
            if (pattern.matcher(Character.toString(c)).matches())
            {
                stringBuilder.append(c);
            } else
            {
                switch (mode)
                {
                    case ALLOW_ALPHANUMERIC_HANGUL:
                    case ALLOW_SEARCH_FILTER:
                        DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_input_error_alphanumeric_hangul), Toast.LENGTH_SHORT);
                        break;

                    case ALLOW_ALPHANUMERIC_NAME:
                        DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_input_error_alphanum_name), Toast.LENGTH_SHORT);
                        break;

                    case ALLOW_NUMERIC:
                        DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_input_error_numeric), Toast.LENGTH_SHORT);
                        break;

                    case ALLOW_REGISTER_COUPON_FILTER:
                        DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_input_error_register_coupon), Toast.LENGTH_SHORT);
                        break;

                    case ALLOW_ALPHANUMERIC:
                    default:
                        DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_input_error_alphanum), Toast.LENGTH_SHORT);
                        break;
                }

                keepOriginal = false;
            }
        }

        if (keepOriginal)
        {
            return null;
        } else
        {
            if (source instanceof Spanned)
            {
                SpannableString spannableString = new SpannableString(stringBuilder);
                TextUtils.copySpansFrom((Spanned) source, start, stringBuilder.length(), null, spannableString, 0);
                return spannableString;
            } else
            {
                return stringBuilder;
            }
        }
    }
}