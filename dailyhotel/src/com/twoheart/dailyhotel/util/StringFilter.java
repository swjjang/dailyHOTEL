package com.twoheart.dailyhotel.util;

import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

/**
 * @description : Inputfilter class to constrain the EditText changes
 * @author hyogij@gmail.com
 * 
 */
public class StringFilter
{
	private static final int ALLOW_ALPHANUMERIC = 0;
	private static final int ALLOW_ALPHANUMERIC_HANGUL = 1;
	private static final int TOAST_LELNGTH = 400;

	private BaseActivity mActivity;

	public StringFilter(BaseActivity activity)
	{
		mActivity = activity;
	}

	// Allows only alphanumeric characters. Filters special and hangul
	// characters.
	public InputFilter allowAlphanumeric = new InputFilter()
	{
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
		{
			return filteredString(source, start, end, ALLOW_ALPHANUMERIC);
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

	// Returns the string result which is filtered by the given mode
	private CharSequence filteredString(CharSequence source, int start, int end, int mode)
	{
		Pattern pattern = null;
		if (mode == ALLOW_ALPHANUMERIC)
		{
			pattern = Pattern.compile("^[a-zA-Z0-9|\\s]+$");
		} else
		{
			pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s|\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]+$");
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
				if (mode == ALLOW_ALPHANUMERIC)
				{
					mActivity.showToast(mActivity.getString(R.string.toast_msg_input_error_alphanum), Toast.LENGTH_SHORT, false);

				} else
				{
					mActivity.showToast(mActivity.getString(R.string.toast_msg_input_error_alphanumeric_hangul), Toast.LENGTH_SHORT, false);
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

	// Shows toast with specify delay that is shorter than Toast.LENGTH_SHORT
	//	private void showToast(String msg)
	//	{
	//		final Toast toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
	//		toast.show();
	//
	//		Handler handler = new Handler();
	//		handler.postDelayed(new Runnable()
	//		{
	//			@Override
	//			public void run()
	//			{
	//				toast.cancel();
	//			}
	//		}, TOAST_LELNGTH);
	//	}
}