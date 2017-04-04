package com.daily.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;

public abstract class BaseView<T extends OnBaseEventListener> implements BaseViewInterface
{
    protected BaseActivity mActivity;
    protected View mRootView;
    protected ViewDataBinding mViewDataBinding;
    protected T mOnEventListener;

    protected abstract void initLayout(View view, ViewDataBinding viewDataBinding);

    public BaseView(BaseActivity activity, T listener)
    {
        if (activity == null || listener == null)
        {
            throw new NullPointerException();
        }

        mActivity = activity;
        mOnEventListener = listener;
    }

    public final void setContentView(int layoutResID)
    {
        mViewDataBinding = DataBindingUtil.setContentView(mActivity, layoutResID);
        mRootView = mViewDataBinding.getRoot();

        initLayout(mRootView, mViewDataBinding);
    }

    public final void setContentView(int layoutResID, ViewGroup viewGroup)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), layoutResID, viewGroup, false);
        mRootView = mViewDataBinding.getRoot();

        initLayout(mRootView, mViewDataBinding);
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
