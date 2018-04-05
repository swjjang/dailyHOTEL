package com.daily.base.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;

public class DailyWebView extends WebView
{
    public interface OnScrollListener
    {
        void onScroll(int l, int t, int oldl, int oldt);
    }

    public OnScrollListener mOnScrollListener;

    public DailyWebView(Context context)
    {
        super(context);
    }

    public DailyWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DailyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollListener != null)
        {
            mOnScrollListener.onScroll(l, t, oldl, oldt);
        }
    }

    public void setOnScrollListener(OnScrollListener listener)
    {
        mOnScrollListener = listener;
    }
}
