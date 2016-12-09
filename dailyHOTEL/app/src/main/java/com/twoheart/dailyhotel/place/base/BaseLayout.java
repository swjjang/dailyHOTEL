package com.twoheart.dailyhotel.place.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseLayout
{
    protected Context mContext;
    protected View mRootView;
    protected OnBaseEventListener mOnEventListener;

    protected abstract void initLayout(View view);

    public BaseLayout(Context context, OnBaseEventListener listener)
    {
        if (context == null || listener == null)
        {
            throw new NullPointerException();
        }

        mContext = context;
        mOnEventListener = listener;
    }

    public final View onCreateView(int layoutResID)
    {
        mRootView = LayoutInflater.from(mContext).inflate(layoutResID, null, false);

        initLayout(mRootView);

        return mRootView;
    }

    public final View onCreateView(int layoutResID, ViewGroup viewGroup)
    {
        mRootView = LayoutInflater.from(mContext).inflate(layoutResID, viewGroup, false);

        initLayout(mRootView);

        return mRootView;
    }

    protected void setVisibility(int visibility)
    {
        if(mRootView == null)
        {
            return;
        }

        mRootView.setVisibility(visibility);
    }
}
