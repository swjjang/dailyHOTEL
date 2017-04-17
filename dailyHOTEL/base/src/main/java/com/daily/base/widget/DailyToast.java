package com.daily.base.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.daily.base.R;
import com.daily.base.databinding.ToastLayoutDataBinding;

public class DailyToast extends Toast
{
    private final static Object mLock = new Object();
    private static DailyToast mToast;

    public DailyToast(Context context)
    {
        super(context);
    }

    /**
     * Toast 팝업 노출
     *
     * @param context  Android application context or activity
     * @param resId    The Resource ID of string resource
     * @param duration Duration (ex : Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
     */
    public static void showToast(Context context, int resId, int duration)
    {
        if (context == null)
        {
            return;
        }

        synchronized (mLock)
        {
            if (mToast != null)
            {
                mToast.cancel();
            }

            mToast = new DailyToast(context);
            mToast.showDailyToast(context, context.getString(resId), duration);
        }
    }

    /**
     * Toast 팝업 노출
     *
     * @param context  Android application context or activity
     * @param text     The text to show. Can be formatted text.
     * @param duration Duration (ex : Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
     */
    public static void showToast(Context context, String text, int duration)
    {
        if (context == null)
        {
            return;
        }

        synchronized (mLock)
        {
            if (mToast != null)
            {
                mToast.cancel();
            }

            mToast = new DailyToast(context);
            mToast.showDailyToast(context, text, duration);
        }
    }

    private void showDailyToast(Context context, String text, int duration)
    {
        if (context == null)
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ToastLayoutDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.toast_layout_data, null, false);

        dataBinding.textView.setText(text);

        mToast.setDuration(duration);
        mToast.setView(dataBinding.getRoot());
        mToast.show();
    }
}
