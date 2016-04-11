package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;

public class DailyToast extends Toast
{
    private final static Object mLock = new Object();
    private static DailyToast mToast;
    private Context mContext;

    public DailyToast(Context context)
    {
        super(context);

        mContext = context;
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
            mToast.showToast(context.getString(resId), duration);
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
            mToast.showToast(text, duration);
        }
    }

    public void showToast(String text, int duration)
    {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(text);

        mToast.setDuration(duration);
        mToast.setView(view);
        mToast.show();
    }
}
