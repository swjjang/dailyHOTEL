package com.daily.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseView<T extends OnBaseEventListener> implements BaseViewInterface
{
    protected BaseActivity mActivity;
    protected View mRootView;
    protected T mOnEventListener;

    protected abstract void initLayout(View view);

    public BaseView(BaseActivity activity, T listener)
    {
        if (activity == null || listener == null)
        {
            throw new NullPointerException();
        }

        mActivity = activity;
        mOnEventListener = listener;
    }

    public final View onCreateView(int layoutResID)
    {
        mRootView = LayoutInflater.from(mActivity).inflate(layoutResID, null, false);

        initLayout(mRootView);

        return mRootView;
    }

    public final View onCreateView(int layoutResID, ViewGroup viewGroup)
    {
        mRootView = LayoutInflater.from(mActivity).inflate(layoutResID, viewGroup, false);

        initLayout(mRootView);

        return mRootView;
    }

    protected void setVisibility(int visibility)
    {
        if (mRootView == null)
        {
            return;
        }

        mRootView.setVisibility(visibility);
    }
}
