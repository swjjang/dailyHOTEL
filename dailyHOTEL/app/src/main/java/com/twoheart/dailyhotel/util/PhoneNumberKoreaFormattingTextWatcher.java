package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.view.widget.DailyToast;

public class PhoneNumberKoreaFormattingTextWatcher implements TextWatcher
{
    private Context mContext;
    private int mChangedLength;
    private boolean mChanged;
    private boolean mIsDeleteNumber;
    private boolean mIsNotDigit;

    public PhoneNumberKoreaFormattingTextWatcher(Context context)
    {
        mContext = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        if (mChanged == true)
        {
            return;
        }

        mChangedLength = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (mChanged == true)
        {
            return;
        }

        if (TextUtils.isDigitsOnly(s.subSequence(start, start + count).toString()) == false)
        {
            mIsNotDigit = true;
        }

        if (mChangedLength > s.length())
        {
            mIsDeleteNumber = true;
        } else
        {
            mIsDeleteNumber = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        if (mChanged == true || s.length() == 0)
        {
            return;
        }

        mChanged = true;

        if (mIsNotDigit == true)
        {
            mIsNotDigit = false;

            s.delete(s.length() - 1, s.length());

            DailyToast.showToast(mContext, R.string.toast_msg_input_error_numeric, Toast.LENGTH_SHORT);
            mChanged = false;
            return;
        }

        StringBuilder number = new StringBuilder(s.toString().replace("-", ""));

        if (number.length() == 1 || number.charAt(0) == '0' || number.charAt(0) == '1')
        {
            if (compareString(number.toString(), new String[]{"0"}) == true)
            {
                number.replace(0, 1, "(0)");
            } else if (compareString(number.toString(), new String[]{"1"}) == true)
            {
                number.replace(0, 1, "(0)1");
            } else
            {
                DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
                s.clear();

                mChanged = false;
                return;
            }
        }

        if (number.length() == 2 && mIsDeleteNumber == true)
        {
            if ("(0".equalsIgnoreCase(number.toString()) == true)
            {
                s.clear();

                mChanged = false;
                return;
            }
        }

        if (number.length() == 3)
        {
            if (compareString(number.toString(), new String[]{"(0)"}) == false)
            {
                if (mIsDeleteNumber == false)
                {
                    DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
                }

                s.clear();

                mChanged = false;
                return;
            }
        }

        if (number.length() == 4)
        {
            if (compareString(number.toString(), new String[]{"(0)1"}) == false)
            {
                if (mIsDeleteNumber == false)
                {
                    DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
                }

                s.clear();

                mChanged = false;
                return;
            }
        }

        // 앞의 3자리 검증
        if (number.length() > 4)
        {
            if (compareString(number.toString(), new String[]{"(0)10", "(0)11", "(0)16", "(0)17", "(0)18", "(0)19"}) == false)
            {
                s.clear();

                DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
                mChanged = false;
                return;
            }

            // 백키로 '-'를 지워야 하는 부분
            if (mIsDeleteNumber == true && number.length() == 5)
            {

            } else
            {
                number.insert(5, '-');
            }
        }

        if (number.length() == 7 && number.charAt(6) == '0')
        {
            number.deleteCharAt(number.length() - 1);
            s.replace(0, s.length(), number.toString());

            DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
            mChanged = false;
            return;
        }

        if (number.length() == 11)
        {
            // ex_ (0)10-1-2345
            number.insert(7, '-');
        } else if (number.length() == 12)
        {
            // ex_ (0)10-12-3456
            number.insert(8, "-");
        } else if (number.length() == 13)
        {
            // ex_ (0)10-123-4567
            number.insert(9, "-");
        } else if (number.length() > 13)
        {
            // ex_ (0)10-1234-5678
            number.insert(10, "-");
        }

        // 더이상 허용되지 않는 자리
        if (number.length() > 15)
        {
            number.deleteCharAt(number.length() - 1);
            s.replace(0, s.length(), number.toString());

            DailyToast.showToast(mContext, R.string.toast_msg_input_error_phonenumber, Toast.LENGTH_SHORT);
            mChanged = false;
            return;
        }

        s.replace(0, s.length(), number.toString());

        mChanged = false;
    }

    private boolean compareString(String text, String[] values)
    {
        if (text == null || values == null)
        {
            return false;
        }

        for (String value : values)
        {
            if (text.startsWith(value) == true)
            {
                return true;
            }
        }

        return false;
    }
}
